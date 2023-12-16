package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.baidu.mobstat.StatService;

import auto.panel.utils.LogUnit;
import auto.panel.utils.NetUnit;
import auto.panel.utils.ToastUnit;
import auto.panel.R;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.panel.ApiController;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_splash);

        // setSendLogStrategy已经@deprecated，建议使用新的start接口
        // 如果没有页面和自定义事件统计埋点，此代码一定要设置，否则无法完成统计
        // 进程第一次执行此代码，会导致发送上次缓存的统计数据；若无上次缓存数据，则发送空启动日志
        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        StatService.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    private void onEnter(boolean isHome) {
        Intent intent;
        if (isHome) {
            intent = new Intent(getBaseContext(), HomeActivity.class);
        } else {
            intent = new Intent(getBaseContext(), LoginActivity.class);
        }
        new Handler().postDelayed(() -> {
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        }, 500);
    }

    private void check() {
        //网络状态
        if (!NetUnit.isConnected(this)) {
            ToastUnit.showShort("请检查设备网络状态");
            onEnter(false);
            return;
        }
        //当前账号
        PanelAccount account = PanelPreference.getCurrentAccount();
        if (account != null) {
            netQuerySystemInfo(account);
        } else {
            onEnter(false);
        }
    }

    protected void netQuerySystemInfo(PanelAccount account) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(PanelSystemInfo system) {
                PanelPreference.setVersion(system.getVersion());
                netCheckAccountToken(account);
            }

            @Override
            public void onFailure(String msg) {
                onEnter(false);
            }
        });
    }

    private void netCheckAccountToken(PanelAccount account) {
        auto.panel.net.panel.ApiController.checkAccountToken(account.getBaseUrl(), account.getAuthorization(), new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                onEnter(true);
            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(msg);
                onEnter(false);
            }
        });
    }

}