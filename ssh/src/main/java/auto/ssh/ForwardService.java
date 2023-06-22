package auto.ssh;

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

@SuppressLint("WakelockTimeout")
public class ForwardService extends Service {
    public static final String TAG = "ForwardService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.forward.ACTION_FORWARD_STATE_CHANGE";
    private static final String WAKELOCK_TAG = "ssh:forward";
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "ForwardServiceChannel";
    private static final String CHANNEL_NAME = "ForwardServiceChannel";

    public static final String EXTRA_HOSTNAME = "hostname";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_REMOTE_ADDRESS = "remoteAddress";
    public static final String EXTRA_REMOTE_PORT = "remotePort";
    public static final String EXTRA_LOCAL_ADDRESS = "localAddress";
    public static final String EXTRA_LOCAL_PORT = "localPort";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_WAKEUP = "wakeup";

    private static final int DEFAULT_PORT = 9100;
    private static final int DEFAULT_KEEP_ALIVE_INTERVAL = 5;

    private volatile Thread forwardThread;
    private SSHClient sshClient;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new HostKeyVerifier());
        // 设置保活间隔
        sshClient.getConnection().getKeepAlive().setKeepAliveInterval(DEFAULT_KEEP_ALIVE_INTERVAL);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (forwardThread != null && forwardThread.isAlive()) {
            return super.onStartCommand(intent, flags, startId);
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

        // 创建返回应用的Intent
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, returnIntent, 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("远程连接")
                .setContentText(String.format("%1$s@%2$s", username, hostname))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo_small);

        // 创建线程
        forwardThread = new Thread(() -> {
            // 广播意图
            Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
            openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
            Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
            closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);

            long startTime = System.currentTimeMillis();
            long divTime = 0L;

            Session session;
            Session.Command command;

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
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);
            // 保存CPU唤醒
            if (wakeup) {
                wakeLock.acquire();
            }

            Logger.info("远程转发已启动", null);

            try {
                sshClient.getTransport().join();
            } catch (TransportException e) {
                e.printStackTrace();
            }

            divTime = System.currentTimeMillis() - startTime;
            Logger.warn("远程转发已断开", null);
            Logger.info("", null);

            // 断开远程连接
            if (sshClient.isConnected()) {
                try {
                    sshClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 发送关闭广播
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);
            // 停止服务
            stopSelf();
        });
        forwardThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}