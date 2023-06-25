package auto.ssh.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Arrays;
import java.util.List;

import auto.base.BaseApplication;
import auto.ssh.R;
import auto.ssh.service.ForwardService;
import auto.ssh.service.ProxyService;

public class MainActivity extends BaseActivity {
    private CardView uiLocal;
    private ImageView uiLocalImg;
    private TextView uiLocalTip;
    private CardView uiForward;
    private ImageView uiForwardImg;
    private TextView uiForwardTip;
    private View uiConfig;
    private View uiSetting;
    private View uiLog;
    private View uiHelp;

    private volatile int proxyState = ProxyService.STATE_CLOSE;
    private volatile int forwardState = ForwardService.STATE_CLOSE;

    private BroadcastReceiver proxyBroadcastReceiver;
    private BroadcastReceiver forwardBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        uiLocal = findViewById(R.id.proxy_local);
        uiLocalImg = findViewById(R.id.proxy_local_img);
        uiLocalTip = findViewById(R.id.proxy_local_tip);
        uiForward = findViewById(R.id.proxy_forward);
        uiForwardImg = findViewById(R.id.proxy_forward_img);
        uiForwardTip = findViewById(R.id.proxy_forward_tip);
        uiConfig = findViewById(R.id.proxy_config);
        uiSetting = findViewById(R.id.proxy_setting);
        uiLog = findViewById(R.id.proxy_log);
        uiHelp = findViewById(R.id.proxy_help);

        init();

        // 注册广播
        IntentFilter proxyFilter = new IntentFilter(ProxyService.BROADCAST_ACTION_STATE);
        IntentFilter forwardFilter = new IntentFilter(ForwardService.BROADCAST_ACTION_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(forwardBroadcastReceiver, forwardFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(proxyBroadcastReceiver, proxyFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭服务
        stopService(new Intent(this, ProxyService.class));
        stopService(new Intent(this, ForwardService.class));

        // 注销广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(proxyBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(forwardBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onProxyServiceOpen() {
        this.proxyState = ProxyService.STATE_OPEN;
        this.uiLocal.setCardBackgroundColor(getResources().getColor(R.color.blue_deep, null));
        this.uiLocalImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiLocalTip.setText(R.string.proxy_local_close);
        this.uiForward.setVisibility(View.VISIBLE);
    }

    private void onProxyServiceClose() {
        this.proxyState = ProxyService.STATE_CLOSE;
        this.uiLocal.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiLocalImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiLocalTip.setText(R.string.proxy_local_open);
        this.uiForward.setVisibility(View.GONE);
        //关闭代理服务
        closeForwardService();
    }

    private void onForwardServiceOpen() {
        this.forwardState = ForwardService.STATE_OPEN;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.blue_deep, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_disconnect);
    }

    private void onForwardServiceClose(boolean isAccident) {
        this.forwardState = ForwardService.STATE_CLOSE;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_connect);
    }

    private void startForwarService() {

    }

    private void closeForwardService() {
        Intent intent = new Intent(this, ForwardService.class);
        intent.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_STOP);
        stopService(intent);
    }

    private void startForwardRetry() {
        new Thread(() -> {
            List<Integer> times = Arrays.asList(1, 2, 3, 5, 5, 10, 10, 20, 20, 30, 30, 50, 50, 60, 60);
            Intent service = new Intent(BaseApplication.getContext(), ForwardService.class);
            service.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_START);
            service.putExtra(ForwardService.EXTRA_HOSTNAME, "60.205.228.46");
            service.putExtra(ForwardService.EXTRA_USERNAME, "root");
            service.putExtra(ForwardService.EXTRA_PASSWORD, "aly@123456Fsp");
            service.putExtra(ForwardService.EXTRA_REMOTE_ADDRESS, "0.0.0.0");
            service.putExtra(ForwardService.EXTRA_REMOTE_PORT, 9200);
            service.putExtra(ForwardService.EXTRA_LOCAL_ADDRESS, "127.0.0.1");
            service.putExtra(ForwardService.EXTRA_LOCAL_PORT, 9100);

            for (int time : times) {
                startService(service);
                if (this.forwardState == ForwardService.STATE_OPEN) {
                    break;
                }
                try {
                    Thread.sleep(time * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    private void init() {
        proxyBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(ProxyService.EXTRA_STATE, ProxyService.STATE_CLOSE);
                if (state == ProxyService.STATE_OPEN) {
                    onProxyServiceOpen();
                } else {
                    onProxyServiceClose();
                }
            }
        };

        forwardBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(ForwardService.EXTRA_STATE, ForwardService.STATE_CLOSE);
                if (state == ForwardService.STATE_OPEN) {
                    onForwardServiceOpen();
                } else {
                    boolean isAccident = intent.getBooleanExtra(ForwardService.EXTRA_ACCIDENT, false);
                    onForwardServiceClose(isAccident);
                }
            }
        };

        // 本地代理
        uiLocal.setOnClickListener(v -> {
            Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
            if (this.proxyState == ProxyService.STATE_CLOSE) {//开启服务
                intent.putExtra(ProxyService.EXTRA_ADDRESS, "127.0.0.1");
                intent.putExtra(ProxyService.EXTRA_PORT, 9100);
                startService(intent);
            } else {//结束服务
                stopService(intent);
            }
        });
        // 远程连接
        uiForward.setOnClickListener(v -> {
            Intent intent = new Intent(BaseApplication.getContext(), ForwardService.class);
            if (this.forwardState == ForwardService.STATE_CLOSE) {//开启服务
                intent.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_START);
                intent.putExtra(ForwardService.EXTRA_HOSTNAME, "60.205.228.46");
                intent.putExtra(ForwardService.EXTRA_USERNAME, "root");
                intent.putExtra(ForwardService.EXTRA_PASSWORD, "aly@123456Fsp");
                intent.putExtra(ForwardService.EXTRA_REMOTE_ADDRESS, "0.0.0.0");
                intent.putExtra(ForwardService.EXTRA_REMOTE_PORT, 9200);
                intent.putExtra(ForwardService.EXTRA_LOCAL_ADDRESS, "127.0.0.1");
                intent.putExtra(ForwardService.EXTRA_LOCAL_PORT, 9100);
                startService(intent);
            } else {//结束服务
                stopService(intent);
            }
        });
        // 代理配置
        uiConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfigActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            startActivity(intent);
        });
        // 设置
        uiSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });
        // 日志
        uiLog.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });
        // 帮助
        uiHelp.setOnClickListener(v -> {

        });
    }

}