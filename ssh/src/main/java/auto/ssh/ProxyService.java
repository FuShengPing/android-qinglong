package auto.ssh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.monkeywie.proxyee.server.HttpProxyServer;

public class ProxyService extends Service {
    public static final String TAG = "ProxyService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.proxy.ACTION_PROXY_STATE_CHANGE";

    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_STATE = "state";

    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    public static final int MIN_PORT = 2000;
    public static final int MAX_PORT = 65535;
    public static final int DEFAULT_PORT = 9100;
    public static final String LOCAL_IP = "127.0.0.1";
    public static final String OPEN_IP = "0.0.0.0";

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundProxyServiceChannel";
    private static final String CHANNEL_NAME = "Foreground Service Channel";

    private static volatile Thread proxyThread;
    private static HttpProxyServer httpProxyServer;

    public ProxyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        httpProxyServer = new HttpProxyServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //服务正在运行
        if (proxyThread != null && proxyThread.isAlive()) {
            return super.onStartCommand(intent, flags, startId);
        }

        int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_PORT);
        if (port < MIN_PORT || port > MAX_PORT) {
            port = DEFAULT_PORT;
        }

        int finalPort = port;

        //创建代理线程
        proxyThread = new Thread(() -> {
            Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
            openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);

            httpProxyServer.start(LOCAL_IP, finalPort);

            Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
            closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);
        });

        proxyThread.start();

        // 创建返回应用的Intent
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, returnIntent, 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("本地代理")
                .setContentText("127.0.0.1:9999")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo_small);

        //开启前台通知
        startForeground(NOTIFICATION_ID, builder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭线程
        try {
            if (proxyThread != null && proxyThread.isAlive()) {
                httpProxyServer.close();
                proxyThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}