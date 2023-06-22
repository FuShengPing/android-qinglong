package auto.ssh;

import android.annotation.SuppressLint;
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

import auto.base.util.Logger;

@SuppressLint("DefaultLocale")
public class ProxyService extends Service {
    public static final String TAG = "ProxyService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.proxy.ACTION_PROXY_STATE_CHANGE";
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ProxyServiceChannel";
    private static final String CHANNEL_NAME = "ProxyServiceChannel";

    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_STATE = "state";

    public static final int DEFAULT_PORT = 9100;

    private volatile Thread proxyThread;
    private HttpProxyServer httpProxyServer;

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

        // 获取地址和端口
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_PORT);

        // 创建返回应用的Intent
        Intent returnIntent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, returnIntent, 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("本地代理")
                .setContentText(String.format("%1$s:%2$d", address, port))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo_small);

        // 创建代理线程
        proxyThread = new Thread(() -> {
            Intent openIntent = new Intent(BROADCAST_ACTION_STATE);
            openIntent.putExtra(EXTRA_STATE, STATE_OPEN);
            Intent closeIntent = new Intent(BROADCAST_ACTION_STATE);
            closeIntent.putExtra(EXTRA_STATE, STATE_CLOSE);

            // 开启前台通知
            startForeground(NOTIFICATION_ID, builder.build());
            // 发送开启广播
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(openIntent);
            Logger.info("本地代理开启", null);
            // 开始监听
            httpProxyServer.start(address, port);
            Logger.info("本地代理关闭", null);
            // 发送关闭广播
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(closeIntent);
            // 关闭服务
            stopSelf();
        });
        proxyThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除通知
        stopForeground(true);
        //关闭线程
        if (proxyThread != null && proxyThread.isAlive()) {
            httpProxyServer.close();
            proxyThread.interrupt();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}