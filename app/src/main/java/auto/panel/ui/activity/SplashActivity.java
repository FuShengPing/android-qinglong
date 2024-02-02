package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import auto.panel.R;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.panel.ApiController;
import auto.panel.utils.NetUnit;
import auto.panel.utils.ToastUnit;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_splash);
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
                if (system.getVersion().compareTo(LoginActivity.MIN_VERSION) < 0) {
                    onEnter(false);
                } else {
                    PanelPreference.setVersion(system.getVersion());
                    netCheckAccountToken();
                }
            }

            @Override
            public void onFailure(String msg) {
                onEnter(false);
            }
        });
    }

    private void netCheckAccountToken() {
        auto.panel.net.panel.ApiController.checkAccountToken(new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                onEnter(true);
            }

            @Override
            public void onFailure(String msg) {
                onEnter(false);
            }
        });
    }

}