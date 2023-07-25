package auto.ssh.service;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import auto.ssh.R;
import auto.ssh.ui.activity.MainActivity;

@SuppressLint("DefaultLocale")
public class ProxyService extends Service {
    public static final String TAG = "ProxyService";
    public static final String BROADCAST_ACTION_STATE = "auto.ssh.proxy.ACTION_PROXY_STATE_CHANGE";

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ProxyServiceChannel";
    private static final String CHANNEL_NAME = "ProxyServiceChannel";

    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_STATE = "state";

    public static final int ACTION_SERVICE_START = 0;
    public static final int ACTION_SERVICE_STOP = 1;
    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;
    public static final int DEFAULT_PORT = 9100;

    private volatile Thread proxyThread;
    private HttpProxyServer httpProxyServer;
    private PendingIntent returnIntent;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        httpProxyServer = new HttpProxyServer();

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

    @Nullable
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
        return proxyThread != null && proxyThread.isAlive();
    }

    private void startThread(Intent intent) {
        if (isAlive()) {
            sendOpenBroadcast();
            return;
        }

        // 获取地址和端口
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_PORT);

        // 创建通知
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("本地代理")
                .setContentText(String.format("%1$s:%2$d", address, port))
                .setContentIntent(returnIntent)
                .setSmallIcon(R.drawable.proxy_ic_logo_small)
                .build();

        // 创建线程
        proxyThread = new Thread(() -> {
            // 开启前台通知
            startForeground(NOTIFICATION_ID, notification);

            // 发送开启广播
            sendOpenBroadcast();

            Logger.info("本地代理开启", null);
            // 开始监听
            httpProxyServer.start(address, port);
            Logger.info("本地代理关闭", null);

            // 发送关闭广播
            sendCloseBroadcast();

            // 关闭线程
            stopThread();
        });

        proxyThread.start();
    }

    private void stopThread() {
        // 移除通知
        stopForeground(true);

        //关闭线程
        if (proxyThread != null && proxyThread.isAlive()) {
            httpProxyServer.close();
            proxyThread.interrupt();
        }
    }

    private void sendOpenBroadcast() {
        Intent intent = new Intent(BROADCAST_ACTION_STATE);
        intent.putExtra(EXTRA_STATE, STATE_OPEN);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void sendCloseBroadcast() {
        Intent intent = new Intent(BROADCAST_ACTION_STATE);
        intent.putExtra(EXTRA_STATE, STATE_CLOSE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}