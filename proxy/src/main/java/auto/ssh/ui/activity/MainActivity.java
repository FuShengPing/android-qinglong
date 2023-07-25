package auto.ssh.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Arrays;
import java.util.List;

import auto.base.BaseApplication;
import auto.base.util.LogFileUtil;
import auto.base.util.LogUnit;
import auto.base.util.Logger;
import auto.base.util.NetUnit;
import auto.ssh.R;
import auto.ssh.data.ConfigPreference;
import auto.ssh.service.ForwardService;
import auto.ssh.service.ProxyService;

public class MainActivity extends BaseActivity {
    private static final String PROJECT = "proxy";
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
    private BroadcastReceiver netBroadcastReceiver;

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

        proxyBroadcastReceiver = new ProxyBroadcastReceiver();
        forwardBroadcastReceiver = new ForwardBroadcastReceiver();
        netBroadcastReceiver = new NetBroadcastReceiver();

        // 广播过滤
        IntentFilter proxyFilter = new IntentFilter(ProxyService.BROADCAST_ACTION_STATE);
        IntentFilter forwardFilter = new IntentFilter(ForwardService.BROADCAST_ACTION_STATE);
        IntentFilter netStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // 注册广播
        LocalBroadcastManager.getInstance(this).registerReceiver(forwardBroadcastReceiver, forwardFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(proxyBroadcastReceiver, proxyFilter);
        registerReceiver(netBroadcastReceiver, netStateFilter);
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
        unregisterReceiver(netBroadcastReceiver);
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
        //停止代理服务
        stopForwardService();
    }

    private void onForwardServiceOpen() {
        this.forwardState = ForwardService.STATE_OPEN;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.blue_deep, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_disconnect);
    }

    private void onForwardServiceClose(boolean interrupted) {
        this.forwardState = ForwardService.STATE_CLOSE;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_connect);
        if (interrupted) {
            retryStartForward();
        }
    }

    private void startProxyService() {
        Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
        intent.putExtra(ProxyService.EXTRA_ACTION, ProxyService.ACTION_SERVICE_START);
        intent.putExtra(ProxyService.EXTRA_ADDRESS, ConfigPreference.getLocalAddress());
        intent.putExtra(ProxyService.EXTRA_PORT, ConfigPreference.getLocalPort());
        startService(intent);
    }

    private void stopProxyService() {
        Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
        intent.putExtra(ProxyService.EXTRA_ACTION, ProxyService.ACTION_SERVICE_STOP);
        startService(intent);
    }

    private void startForwardService() {
        Intent intent = new Intent(BaseApplication.getContext(), ForwardService.class);
        intent.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_START);
        intent.putExtra(ForwardService.EXTRA_HOSTNAME, "114.67.238.143");
        intent.putExtra(ForwardService.EXTRA_USERNAME, "root");
        intent.putExtra(ForwardService.EXTRA_PASSWORD, "jdy@123456fsp");
        intent.putExtra(ForwardService.EXTRA_REMOTE_ADDRESS, "0.0.0.0");
        intent.putExtra(ForwardService.EXTRA_REMOTE_PORT, 9100);
        intent.putExtra(ForwardService.EXTRA_LOCAL_ADDRESS, "127.0.0.1");
        intent.putExtra(ForwardService.EXTRA_LOCAL_PORT, 9100);
        intent.putExtra(ForwardService.EXTRA_WAKEUP, true);
        startService(intent);
    }

    private void stopForwardService() {
        Intent intent = new Intent(BaseApplication.getContext(), ForwardService.class);
        intent.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_STOP);
        startService(intent);
    }

    private void retryStartForward() {
        new Thread(() -> {
            List<Integer> times = Arrays.asList(6, 6, 6, 12, 30, 30, 60, 60, 120);
            int count = 0;

            while (proxyState == ProxyService.STATE_OPEN && forwardState == ForwardService.STATE_CLOSE) {
                startForwardService();

                if (count < times.size() - 1) {
                    count++;
                }

                try {
                    Logger.info("等待" + times.get(count) + "s再次尝试连接", null);
                    Thread.sleep(times.get(count) * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    private void init() {
        // 本地代理
        uiLocal.setOnClickListener(v -> {
            if (this.proxyState == ProxyService.STATE_CLOSE) {
                startProxyService();
            } else {
                stopProxyService();
            }
        });

        // 远程连接
        uiForward.setOnClickListener(v -> {
            if (this.forwardState == ForwardService.STATE_CLOSE) {
                startForwardService();
            } else {
                stopForwardService();
            }
        });

        // 代理配置
        uiConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfigActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // 设置
        uiSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });

        // 日志
        uiLog.setOnClickListener(v -> {
            //日志目录路径
            String path = LogFileUtil.getLogFileDir(PROJECT);

            LogUnit.log(path);

            // 创建一个Intent对象
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(path);
            intent.setDataAndType(uri, "*/*");
            startActivity(Intent.createChooser(intent, "选择文件管理器"));
        });

        // 帮助
        uiHelp.setOnClickListener(v -> {

        });
    }

    private class ProxyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(ProxyService.EXTRA_STATE, ProxyService.STATE_CLOSE);
            if (state == ProxyService.STATE_OPEN) {
                onProxyServiceOpen();
            } else {
                onProxyServiceClose();
            }
        }
    }

    private class ForwardBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(ForwardService.EXTRA_STATE, ForwardService.STATE_CLOSE);
            if (state == ForwardService.STATE_OPEN) {
                onForwardServiceOpen();
            } else {
                boolean interrupted = intent.getBooleanExtra(ForwardService.EXTRA_INTERRUPTED, false);
                onForwardServiceClose(interrupted);
            }
        }
    }

    private class NetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.debug("网络状态变化 " + NetUnit.isConnected(getApplicationContext()), null);
        }
    }

}