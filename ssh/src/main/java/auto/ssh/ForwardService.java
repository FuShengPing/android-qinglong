package auto.ssh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import auto.base.util.LogUnit;

public class ForwardService extends Service {
    public static final String TAG = "ForwardService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.forward.ACTION_FORWARD_STATE_CHANGE";
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

    private static final int DEFAULT_PORT = 9100;

    private volatile Thread forwardThread;
    private SSHClient sshClient;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new HostKeyVerifier());
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
        int remotePort = intent.getIntExtra(EXTRA_REMOTE_PORT, DEFAULT_PORT);
        String localAddress = intent.getStringExtra(EXTRA_LOCAL_ADDRESS);
        int localPort = intent.getIntExtra(EXTRA_LOCAL_PORT, DEFAULT_PORT);

        // 创建返回应用的Intent
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, returnIntent, 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("远程连接")
                .setContentText(String.format("%1$s@%2$s", username, hostname))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo_small);

        forwardThread = new Thread(() -> {
            Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
            openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
            Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
            closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);

            try {
                // 连接远程服务
                sshClient.connect(hostname, 22);
                // 设置登录用户名和密码
                sshClient.authPassword(username, password);
                // 设置保活间隔
                sshClient.getConnection().getKeepAlive().setKeepAliveInterval(3);

                Session session = sshClient.startSession();

                Session.Command command = session.exec(Commands.checkPortCommand(remotePort));

                command.join(3, TimeUnit.SECONDS);

                String result = IOUtils.readFully(command.getInputStream()).toString();

                // 端口已被占用
                if (!result.isEmpty()) {
                    NetStat netStat = new NetStat(result);
                    session = sshClient.startSession();
                    session.exec(Commands.killPid(netStat.getPid())).join(3, TimeUnit.SECONDS);
                }

                // 新建端口转发
                RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(remoteAddress, remotePort);
                SocketForwardingConnectListener connectListener = new SocketForwardingConnectListener(new InetSocketAddress(localAddress, localPort));
                sshClient.getRemotePortForwarder().bind(forward, connectListener);
                // 开启前台通知
                startForeground(NOTIFICATION_ID, builder.build());
                // 发送开启广播
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);
                // 线程阻塞
                sshClient.getTransport().join();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sshClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
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