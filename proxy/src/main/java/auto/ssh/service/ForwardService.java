package auto.ssh.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    public static final String EXTRA_HOSTNAME = "hostname";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_REMOTE_ADDRESS = "remoteAddress";
    public static final String EXTRA_REMOTE_PORT = "remotePort";
    public static final String EXTRA_LOCAL_ADDRESS = "localAddress";
    public static final String EXTRA_LOCAL_PORT = "localPort";
    public static final String EXTRA_WAKEUP = "wakeup";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_MSG = "msg";
    public static final String EXTRA_ACCIDENT = "accident";

    public static final int ACTION_SERVICE_START = 0;
    public static final int ACTION_SERVICE_STOP = 1;
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    private static final int DEFAULT_PORT = 9100;
    private static final int DEFAULT_KEEP_ALIVE_INTERVAL = 10;

    private volatile Thread forwardThread;
    private volatile boolean interrupted;
    private SSHClient sshClient;
    private PowerManager.WakeLock wakeLock;
    private PendingIntent returnIntent;
    private Notification notification;
    private TimeBroadcastReceiver timeReceiver;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        super.onCreate();

        //广播接收器
        timeReceiver = new TimeBroadcastReceiver();

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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(EXTRA_ACTION, ACTION_SERVICE_START);

        if (action == ACTION_SERVICE_START) {
            startForwardThread(intent);
        } else if (action == ACTION_SERVICE_STOP) {
            stopThread();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }

    private boolean isAlive() {
        return forwardThread != null && forwardThread.isAlive() && sshClient != null && sshClient.isConnected();
    }

    private void startForwardThread(Intent intent) {
        if (isAlive()) {
            sendOpenBroadcast();
            return;
        }

        // 读取参数
        String hostname = intent.getStringExtra(EXTRA_HOSTNAME);
        String username = intent.getStringExtra(EXTRA_USERNAME);
        String password = intent.getStringExtra(EXTRA_PASSWORD);
        String remoteAddress = intent.getStringExtra(EXTRA_REMOTE_ADDRESS);
        String localAddress = intent.getStringExtra(EXTRA_LOCAL_ADDRESS);
        int remotePort = intent.getIntExtra(EXTRA_REMOTE_PORT, DEFAULT_PORT);
        int localPort = intent.getIntExtra(EXTRA_LOCAL_PORT, DEFAULT_PORT);
        boolean wakeup = intent.getBooleanExtra(EXTRA_WAKEUP, true);

        // 创建通知
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("内网穿透")
                .setContentText(String.format("%1$s@%2$s\t%3$s", username, hostname, wakeup ? "lock" : "unlock"))
                .setContentIntent(returnIntent)
                .setSmallIcon(R.drawable.ic_logo_small)
                .build();

        // 创建线程
        forwardThread = new Thread(() -> {
            // 设置异常中断标志
            interrupted = true;

            // 连接远程服务
            try {
                sshClient = new SSHClient();
                sshClient.addHostKeyVerifier(new HostKeyVerifier());
                sshClient.getConnection().getKeepAlive().setKeepAliveInterval(DEFAULT_KEEP_ALIVE_INTERVAL);
                sshClient.connect(hostname, 22);
            } catch (Exception e) {
                Logger.error("SSH远程连接失败", e);
                return;
            }

            // 用户鉴权
            try {
                sshClient.authPassword(username, password);
            } catch (UserAuthException | TransportException e) {
                Logger.error("SSH远程登录失败", e);
                return;
            }

            //检查端口
            try {
                Session session = sshClient.startSession();
                Session.Command command = session.exec(Commands.checkPortCommand(remotePort));
                command.join(5, TimeUnit.SECONDS);
                String result = IOUtils.readFully(command.getInputStream()).toString();
                command.close();
                // 端口已被占用
                if (!result.isEmpty()) {
                    Logger.warn(String.format(Locale.CHINA, "远程端口%1$d已被占用", remotePort), null);
                    Logger.info("尝试释放端口", null);
                    NetStat netStat = new NetStat(result);
                    session = sshClient.startSession();
                    command = session.exec(Commands.killPid(netStat.getPid()));
                    command.join(3, TimeUnit.SECONDS);
                    command.close();
                }
            } catch (IOException e) {
                Logger.error("端口检查失败", e);
                return;
            }

            // 新建端口转发
            try {
                RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(remoteAddress, remotePort);
                SocketForwardingConnectListener connectListener = new SocketForwardingConnectListener(new InetSocketAddress(localAddress, localPort));
                sshClient.getRemotePortForwarder().bind(forward, connectListener);
                Logger.info("远程转发已启动", null);
            } catch (Exception e) {
                Logger.error("SSH远程转发启动失败", e);
                return;
            }

            // 开启定时广播接收
            registerReceiver(timeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

            // 开启前台通知
            startForeground(NOTIFICATION_ID, notification);

            // 发送开启广播
            sendOpenBroadcast();

            // 保存CPU唤醒
            if (wakeup) {
                wakeLock.acquire();
            }

            // 线程阻塞
            try {
                sshClient.getTransport().join();
            } catch (Exception e) {
                Logger.warn("远程转发断开", e);
            }

            // 断开远程连接
            if (sshClient.isConnected()) {
                try {
                    sshClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //移除定时广播
            unregisterReceiver(timeReceiver);

            // 发送关闭广播
            sendCloseBroadcast();

            // 关闭所有线程
            stopThread();
        });

        forwardThread.start();
    }

    private void stopThread() {
        // 更新异常中断标志
        interrupted = false;

        // 移除通知
        stopForeground(true);

        // 关闭唤醒
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        // 关闭线程
        if (forwardThread != null && forwardThread.isAlive()) {
            forwardThread.interrupt();
        }
    }

    private void sendOpenBroadcast() {
        Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
        openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);
    }

    private void sendCloseBroadcast() {
        Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
        closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);
        closeIntent.putExtra(EXTRA_ACCIDENT, interrupted);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private class TimeBroadcastReceiver extends BroadcastReceiver {
        private int count = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAlive()) {
                if (count % 6 == 0) {
                    startForeground(NOTIFICATION_ID, notification);
                    Logger.debug("TIME_TICK", null);
                    count = 1;
                } else {
                    count++;
                }
            }
        }
    }
}