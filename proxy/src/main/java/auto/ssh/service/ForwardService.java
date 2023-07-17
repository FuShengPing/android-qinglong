package auto.ssh.service;

import android.annotation.SuppressLint;
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

import auto.base.util.LogUnit;
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
    private static final int DEFAULT_KEEP_ALIVE_INTERVAL = 30;

    private volatile Thread forwardThread;
    private volatile Thread keepAliveThread;
    private volatile boolean isAccident;
    private SSHClient sshClient;
    private PowerManager.WakeLock wakeLock;

    private PendingIntent returnIntent;
    private PendingIntent stopIntent;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        // SSH
        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new HostKeyVerifier());
        sshClient.getConnection().getKeepAlive().setKeepAliveInterval(DEFAULT_KEEP_ALIVE_INTERVAL);

        // 唤醒锁
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);

        //返回主界面-out
        Intent returnIntent = new Intent(this, MainActivity.class);
        this.returnIntent = PendingIntent.getActivity(this, 0, returnIntent, 0);

        //停止服务-in
        Intent stopIntent = new Intent(this, ForwardService.class);
        stopIntent.putExtra(EXTRA_ACTION, ACTION_SERVICE_STOP);
        this.stopIntent = PendingIntent.getService(this, 0, stopIntent, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(EXTRA_ACTION, ACTION_SERVICE_START);

        LogUnit.log(action);

        if (action == ACTION_SERVICE_START) {
            startForwardThread(intent);
        } else if (action == ACTION_SERVICE_STOP) {
            stopThread();
        }

        return START_STICKY;
    }

    private void startForwardThread(Intent intent) {
        if (forwardThread != null && forwardThread.isAlive()) {
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("内网穿透")
                .setContentText(String.format("%1$s@%2$s %3$s", username, hostname, wakeup ? "lock" : "unlock"))
                .setContentIntent(returnIntent)
                .setSmallIcon(R.drawable.ic_logo_small);

        // 创建线程
        forwardThread = new Thread(() -> {
            // 设置异常中断标志
            isAccident = true;

            // 连接远程服务
            try {
                sshClient.connect(hostname, 22);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.error("SSH远程连接失败", e);
                return;
            }

            // 用户鉴权
            try {
                sshClient.authPassword(username, password);
            } catch (UserAuthException | TransportException e) {
                Logger.error("SSH远程登录失败", e);
                e.printStackTrace();
                return;
            }

            Session session;
            Session.Command command;

            try {
                session = sshClient.startSession();
                command = session.exec(Commands.checkPortCommand(remotePort));
                command.join(5, TimeUnit.SECONDS);
                String result = IOUtils.readFully(command.getInputStream()).toString();
                command.close();
                // 端口已被占用
                if (!result.isEmpty()) {
                    Logger.error(String.format(Locale.CHINA, "远程端口%1$d已被占用", remotePort), null);
                    Logger.info("尝试释放端口", null);
                    NetStat netStat = new NetStat(result);
                    session = sshClient.startSession();
                    command = session.exec(Commands.killPid(netStat.getPid()));
                    command.join(3, TimeUnit.SECONDS);
                    command.close();
                }
            } catch (IOException e) {
                Logger.error("端口检查失败", e);
                e.printStackTrace();
                return;
            }

            // 新建端口转发
            RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(remoteAddress, remotePort);
            SocketForwardingConnectListener connectListener = new SocketForwardingConnectListener(new InetSocketAddress(localAddress, localPort));
            try {
                sshClient.getRemotePortForwarder().bind(forward, connectListener);
            } catch (Exception e) {
                Logger.error("SSH远程转发启动失败", e);
                e.printStackTrace();
                return;
            }

            // 开启前台通知
            startForeground(NOTIFICATION_ID, builder.build());

            // 发送开启广播
            Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
            openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);

            // 保存CPU唤醒
            if (wakeup) {
                wakeLock.acquire();
            }

            Logger.info("远程转发已启动", null);

            //启动保活线程
            startKeepAliveThread();

            // 线程阻塞
            try {
                sshClient.getTransport().join();
            } catch (TransportException e) {
                e.printStackTrace();
            }

            Logger.warn("远程转发已断开", null);

            // 断开远程连接
            if (sshClient.isConnected()) {
                try {
                    sshClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 发送关闭广播
            Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
            closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);
            closeIntent.putExtra(EXTRA_ACCIDENT, isAccident);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);

            // 关闭服务
            stopSelf();
        });

        forwardThread.start();
    }

    private void startKeepAliveThread() {
        keepAliveThread = new Thread(() -> {
            while (true) {
                try {
                    if (sshClient.isConnected()) {
                        Session session = sshClient.startSession();
                        Session.Command command = session.exec("ps");
                        command.join(1, TimeUnit.SECONDS);
                        command.close();
                        Logger.info("保活线程心跳...", null);
                        Thread.sleep(60 * 10 * 1000);
                    } else {
                        Logger.info("连接断开，保活线程退出", null);
                        return;
                    }
                } catch (Exception e) {
                    Logger.warn("保活线程中断", e);
                    return;
                }
            }
        });
        keepAliveThread.start();
    }

    private void stopThread() {
        // 更新异常中断标志
        isAccident = false;

        // 移除通知
        stopForeground(true);

        // 关闭唤醒
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        // 关闭线程
        if (keepAliveThread != null && keepAliveThread.isAlive()) {
            keepAliveThread.interrupt();
        }
        if (forwardThread != null && forwardThread.isAlive()) {
            forwardThread.interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}