package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import auto.panel.R;
import auto.panel.bean.app.Account;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.database.db.AccountDataSource;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.panel.ApiController;
import auto.panel.utils.NetUnit;
import auto.panel.utils.ToastUnit;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    public static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccount();
    }

    private void toLoginActivity(PanelAccount account) {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ADDRESS, account.getAddress());
        intent.putExtra(LoginActivity.EXTRA_USERNAME, account.getUsername());
        intent.putExtra(LoginActivity.EXTRA_PASSWORD, account.getPassword());
        toActivity(intent);
    }

    private void toActivity(Intent intent) {
        new Handler().postDelayed(() -> {
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        }, 500);
    }

    private void checkAccount() {
        //当前账号
        String address = PanelPreference.getAddress();
        AccountDataSource source = new AccountDataSource(this);
        Account account = source.getAccount(address);
        source.close();

        if (account != null) {
            PanelAccount panelAccount = new PanelAccount(address, account.getUsername(), account.getPassword());
            //网络状态
            if (!NetUnit.isConnected(this)) {
                ToastUnit.showShort("请检查设备网络状态");
                toLoginActivity(panelAccount);
            } else {
                netQuerySystemInfo(panelAccount, account.getToken());
            }
        } else {
            toActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
    }

    protected void netQuerySystemInfo(PanelAccount account, String token) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(PanelSystemInfo system) {
                if (system.getVersion().compareTo(LoginActivity.MIN_VERSION) < 0) {
                    toLoginActivity(account);
                } else {
                    netCheckAccountToken(account, token);
                }
            }

            @Override
            public void onFailure(String msg) {
                toLoginActivity(account);
            }
        });
    }

    private void netCheckAccountToken(PanelAccount account, String token) {
        auto.panel.net.panel.ApiController.checkAccountToken(account.getBaseUrl(), token, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                toActivity(new Intent(getBaseContext(), HomeActivity.class));
            }

            @Override
            public void onFailure(String msg) {
                toLoginActivity(account);
            }
        });
    }

}