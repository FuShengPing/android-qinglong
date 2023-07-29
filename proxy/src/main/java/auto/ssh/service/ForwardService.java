package auto.ssh.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import auto.base.util.Logger;
import auto.ssh.R;
import auto.ssh.bean.NetStat;
import auto.ssh.data.ConfigPreference;
import auto.ssh.data.SettingPrefence;
import auto.ssh.ui.activity.MainActivity;

@SuppressLint("WakelockTimeout")
public class ForwardService extends Service {
    public static final String TAG = "ForwardService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.forward.ACTION_FORWARD_STATE_CHANGE";
    private static final String WAKELOCK_TAG = "ssh:forward";

    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "ForwardServiceChannel";
    private static final String CHANNEL_NAME = "ForwardServiceChannel";

    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_LOCAL_ADDRESS = "localAddress";
    public static final String EXTRA_LOCAL_PORT = "localPort";
    public static final String EXTRA_REMOTE_ADDRESS = "remoteAddress";
    public static final String EXTRA_REMOTE_PORT = "remotePort";
    public static final String EXTRA_REMOTE_USERNAME = "remoteUsername";
    public static final String EXTRA_REMOTE_PASSWORD = "remotePassword";
    public static final String EXTRA_REMOTE_FORWARD_ADDRESS = "remoteForwardAddress";
    public static final String EXTRA_REMOTE_FORWARD_PORT = "remoteForwardPort";
    public static final String EXTRA_WAKEUP = "wakeup";
    public static final String EXTRA_REFRESH_INTERVAL = "refreshInterval";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_INTERRUPTED = "accident";
    public static final String EXTRA_MSG = "msg";

    public static final int ACTION_SERVICE_START = 0;
    public static final int ACTION_SERVICE_STOP = 1;
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    private static final int DEFAULT_LOCAL_PORT = ConfigPreference.DEFAULT_LOCAL_PORT;
    private static final int DEFAULT_REMOTE_PORT = ConfigPreference.DEFAULT_REMOTE_PORT;
    private static final int DEFAULT_REMOTE_FORWARD_PORT = ConfigPreference.DEFAULT_REMOTE_FORWARD_PORT;
    private static final int DEFAULT_REFRESH_INTERVAL = SettingPrefence.DEFAULT_SERVICE_REFRESH_INTERVAL;
    private static final boolean DEFAULT_WAKEUP = SettingPrefence.DEFAULT_SERVICE_WAKEUP;
    private static final int DEFAULT_KEEP_ALIVE_INTERVAL = 10;

    private volatile Thread forwardThread;
    private volatile Thread keepAliveThread;
    private volatile boolean interrupted;
    private volatile boolean connecting;
    private SSHClient sshClient;
    private PowerManager.WakeLock wakeLock;
    private PendingIntent returnIntent;
    private Notification notification;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        super.onCreate();

        //创建通知管道
        createNotificationChannel();

        // 唤醒锁
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);

        //返回主界面
        Intent returnIntent = new Intent(this, MainActivity.class);
        this.returnIntent = PendingIntent.getActivity(this, 0, returnIntent, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(EXTRA_ACTION, ACTION_SERVICE_START);

        if (action == ACTION_SERVICE_START) {
            startThread(intent);
        } else if (action == ACTION_SERVICE_STOP) {
            stopThread();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }

    private boolean isAlive() {
        return forwardThread != null && forwardThread.isAlive() && sshClient != null && sshClient.isConnected();
    }

    private void startThread(Intent intent) {
        if (isAlive()) {
            sendOpenBroadcast();
            return;
        }

        if (connecting) {
            return;
        } else {
            connecting = true;
        }

        // 读取参数
        String localAddress = intent.getStringExtra(EXTRA_LOCAL_ADDRESS);
        int localPort = intent.getIntExtra(EXTRA_LOCAL_PORT, DEFAULT_LOCAL_PORT);
        String remoteAddress = intent.getStringExtra(EXTRA_REMOTE_ADDRESS);
        int remotePort = intent.getIntExtra(EXTRA_REMOTE_PORT, DEFAULT_REMOTE_PORT);
        String remoteUsername = intent.getStringExtra(EXTRA_REMOTE_USERNAME);
        String remotePassword = intent.getStringExtra(EXTRA_REMOTE_PASSWORD);
        String remoteForwardAddress = intent.getStringExtra(EXTRA_REMOTE_FORWARD_ADDRESS);
        int remoteForwardPort = intent.getIntExtra(EXTRA_REMOTE_FORWARD_PORT, DEFAULT_REMOTE_FORWARD_PORT);
        int refreshInterval = intent.getIntExtra(EXTRA_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL);
        boolean wakeup = intent.getBooleanExtra(EXTRA_WAKEUP, DEFAULT_WAKEUP);

        // 创建通知
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("内网穿透")
                .setContentText(String.format("%1$s@%2$s", remoteUsername, remoteAddress))
                .setContentIntent(returnIntent)
                .setSmallIcon(R.drawable.proxy_ic_logo_small)
                .build();

        // 创建保活线程
        keepAliveThread = new Thread(() -> {
            while (isAlive()) {
                try {
                    Thread.sleep(refreshInterval * 1000L);
                    if (isAlive()) {
                        startForeground(NOTIFICATION_ID, notification);
                        Logger.debug("TIME_TICK", null);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        // 创建转发线程
        forwardThread = new Thread(() -> {
            // 设置异常中断标志
            interrupted = true;

            // 连接远程服务
            try {
                sshClient = new SSHClient();
                sshClient.addHostKeyVerifier(new HostKeyVerifier());
                sshClient.getConnection().getKeepAlive().setKeepAliveInterval(DEFAULT_KEEP_ALIVE_INTERVAL);
                sshClient.connect(remoteAddress, remotePort);
            } catch (Exception e) {
                Logger.error("SSH远程连接失败", e);
                connecting = false;
                return;
            }

            // 用户鉴权
            try {
                sshClient.authPassword(remoteUsername, remotePassword);
            } catch (UserAuthException | TransportException e) {
                Logger.error("SSH远程登录失败", e);
                connecting = false;
                return;
            }

            //检查端口
            try {
                Session session = sshClient.startSession();
                Session.Command command = session.exec(Commands.checkPortCommand(remoteForwardPort));
                command.join(5, TimeUnit.SECONDS);
                String result = IOUtils.readFully(command.getInputStream()).toString();
                command.close();
                // 端口已被占用
                if (!result.isEmpty()) {
                    Logger.warn(String.format(Locale.CHINA, "远程端口%1$d已被占用", remoteForwardPort), null);
                    Logger.info("尝试释放端口", null);
                    NetStat netStat = new NetStat(result);
                    session = sshClient.startSession();
                    command = session.exec(Commands.killPid(netStat.getPid()));
                    command.join(3, TimeUnit.SECONDS);
                    command.close();
                }
            } catch (IOException e) {
                Logger.error("端口检查失败", e);
                connecting = false;
                return;
            }

            // 新建端口转发
            try {
                RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(remoteForwardAddress, remoteForwardPort);
                SocketForwardingConnectListener connectListener = new SocketForwardingConnectListener(new InetSocketAddress(localAddress, localPort));
                sshClient.getRemotePortForwarder().bind(forward, connectListener);
                Logger.info("远程转发已启动", null);
            } catch (Exception e) {
                Logger.error("SSH远程转发启动失败", e);
                connecting = false;
                return;
            }

            // 开启前台通知
            startForeground(NOTIFICATION_ID, notification);

            // 发送开启广播
            sendOpenBroadcast();

            // 保存CPU唤醒
            if (wakeup) {
                wakeLock.acquire();
            }

            // 开启保活线程
            keepAliveThread.start();

            // 线程阻塞
            try {
                sshClient.getTransport().join();
            } catch (Exception e) {
                Logger.warn("远程转发断开", e);
                connecting = false;
            }

            // 断开远程连接
            if (sshClient.isConnected()) {
                try {
                    sshClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 移除通知
            stopForeground(true);

            // 发送关闭广播
            sendCloseBroadcast();

            // 销毁线程
            stopThread();
        });

        forwardThread.start();
    }

    private void stopThread() {
        // 更新异常中断标志
        interrupted = false;

        // 关闭唤醒
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        // 关闭线程
        if (forwardThread != null && forwardThread.isAlive()) {
            forwardThread.interrupt();
        }
        if (keepAliveThread != null && keepAliveThread.isAlive()) {
            keepAliveThread.interrupt();
        }

        connecting = false;
    }

    private void sendOpenBroadcast() {
        Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
        openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);
    }

    private void sendCloseBroadcast() {
        Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
        closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);
        closeIntent.putExtra(EXTRA_INTERRUPTED, interrupted);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}