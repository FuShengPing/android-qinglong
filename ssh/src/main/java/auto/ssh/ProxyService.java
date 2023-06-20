package auto.ssh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.github.monkeywie.proxyee.server.HttpProxyServer;

import auto.base.util.LogUnit;

public class ProxyService extends Service {
    public static final int MIN_PORT = 2000;
    public static final int MAX_PORT = 65535;
    public static final int DEFAULT_PORT = 9100;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundProxyServiceChannel";
    private static final String CHANNEL_NAME = "Foreground Service Channel";
    public static final String LOCAL_IP = "127.0.0.1";
    public static final String OPEN_IP = "0.0.0.0";
    public static final String EXTRA_PORT = "port";

    static HttpProxyServer httpProxyServer;

    public ProxyService() {
    }

    @Override
    public void onCreate() {
        createNotificationChannel();

        httpProxyServer = new HttpProxyServer();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_PORT);
        if (port < MIN_PORT || port > MAX_PORT) {
            port = DEFAULT_PORT;
        }

        int finalPort = port;

        // 创建返回应用的Intent
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, returnIntent, 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("代理服务")
                .setContentText("127.0.0.1:9999")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_vpc);

        startForeground(NOTIFICATION_ID, builder.build());

        new Thread(() -> {
            LogUnit.log("ProxyService start");
            httpProxyServer.start(LOCAL_IP, finalPort);
            LogUnit.log("ProxyService end");
            stopSelf();
        }).start();

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
        try {
            if (httpProxyServer != null) {
                httpProxyServer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}