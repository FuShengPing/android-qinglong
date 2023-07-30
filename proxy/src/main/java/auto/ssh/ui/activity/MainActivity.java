package auto.ssh.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Arrays;
import java.util.List;

import auto.base.BaseApplication;
import auto.base.util.LogUnit;
import auto.base.util.Logger;
import auto.base.util.NetUnit;
import auto.base.util.WebUnit;
import auto.ssh.R;
import auto.ssh.bean.ConfigParams;
import auto.ssh.bean.SettingParams;
import auto.ssh.data.ConfigPreference;
import auto.ssh.data.SettingPrefence;
import auto.ssh.service.ForwardService;
import auto.ssh.service.ProxyService;
import auto.ssh.ui.popup.Builder;
import auto.ssh.ui.popup.ConfirmPopup;

public class MainActivity extends BaseActivity {
    private static final String EXTRA_FROM = "from";
    private static final String EXTRA_TOKEN = "token";


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
    private View uiAbout;

    private PopupWindow uiConfirmPopup;

    private String from;
    private String token;

    private volatile int proxyState = ProxyService.STATE_CLOSE;
    private volatile int forwardState = ForwardService.STATE_CLOSE;

    private BroadcastReceiver proxyBroadcastReceiver;
    private BroadcastReceiver forwardBroadcastReceiver;
    private BroadcastReceiver netBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.proxy_activity_main);

        from = getIntent().getStringExtra(EXTRA_FROM);
        token = getIntent().getStringExtra(EXTRA_TOKEN);

        Logger.debug("from " + from + " token：" + token, null);

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
        uiAbout = findViewById(R.id.proxy_about);

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (uiConfirmPopup == null) {
            if (from == null) {
                showConfirmPopup("风险提示", "异常启动，请从正规渠道启动！");
            } else if (token == null) {
                showConfirmPopup("鉴权失败", "身份信息无效或已过期，请重新启动！");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭弹窗
        if (uiConfirmPopup != null) {
            uiConfirmPopup.dismiss();
            uiConfirmPopup = null;
        }

        // 关闭服务
        stopService(new Intent(this, ProxyService.class));
        stopService(new Intent(this, ForwardService.class));

        // 注销广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(proxyBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(forwardBroadcastReceiver);
        unregisterReceiver(netBroadcastReceiver);

        LogUnit.log("onDestroy");
    }

    @Override
    public void onBackPressed() {
        if (uiConfirmPopup != null && uiConfirmPopup.isShowing()) {
            finish();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //pop存在阻止点击
        if (uiConfirmPopup != null && uiConfirmPopup.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void onProxyServiceOpen() {
        this.proxyState = ProxyService.STATE_OPEN;
        this.uiLocal.setCardBackgroundColor(getResources().getColor(R.color.proxy_theme, null));
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
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.proxy_theme, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_disconnect);
    }

    private void onForwardServiceClose(boolean interrupted) {
        this.forwardState = ForwardService.STATE_CLOSE;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_connect);
        LogUnit.log(interrupted);
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
        ConfigParams config = ConfigPreference.getConfig();
        SettingParams setting = SettingPrefence.getSettingParams();

        intent.putExtra(ForwardService.EXTRA_ACTION, ForwardService.ACTION_SERVICE_START);
        intent.putExtra(ForwardService.EXTRA_REMOTE_ADDRESS, config.getRemoteAddress());
        intent.putExtra(ForwardService.EXTRA_REMOTE_PORT, config.getRemotePort());
        intent.putExtra(ForwardService.EXTRA_REMOTE_USERNAME, config.getRemoteUsername());
        intent.putExtra(ForwardService.EXTRA_REMOTE_PASSWORD, config.getRemotePassword());
        intent.putExtra(ForwardService.EXTRA_REMOTE_FORWARD_ADDRESS, config.getRemoteForwardAddress());
        intent.putExtra(ForwardService.EXTRA_REMOTE_FORWARD_PORT, config.getRemoteForwardPort());
        intent.putExtra(ForwardService.EXTRA_LOCAL_ADDRESS, config.getLocalAddress());
        intent.putExtra(ForwardService.EXTRA_LOCAL_PORT, config.getLocalPort());
        intent.putExtra(ForwardService.EXTRA_REFRESH_INTERVAL, setting.getServiceRefreshInterval());
        intent.putExtra(ForwardService.EXTRA_WAKEUP, setting.isServiceWakeup());
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

    private void showConfirmPopup(String title, String content) {
        ConfirmPopup confirmPopup = new ConfirmPopup();
        confirmPopup.setTitle(title);
        confirmPopup.setContent(content);
        confirmPopup.setCancel(false);
        confirmPopup.setConfirm(true);
        confirmPopup.setActionListener(new ConfirmPopup.OnActionListener() {
            @Override
            public boolean onConfirm() {
                finish();
                return false;
            }

            @Override
            public boolean onCancel() {
                return false;
            }
        });

        uiConfirmPopup = Builder.buildConfirmWindow(self, confirmPopup);
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

        // 日志
        uiLog.setOnClickListener(v -> {
            Intent intent = new Intent(self, LogActivity.class);
            startActivity(intent);
        });

        // 设置
        uiSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });

        // 帮助
        uiHelp.setOnClickListener(v -> {
            WebUnit.open(self, "https://gitee.com/wsfsp4/android-lanproxy/blob/master/README.md");
        });

        // 关于
        uiAbout.setOnClickListener(v -> {

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