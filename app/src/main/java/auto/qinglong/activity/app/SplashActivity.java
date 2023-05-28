package auto.qinglong.activity.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.Account;
import auto.qinglong.bean.ql.QLSystem;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.NetUnit;
import auto.qinglong.utils.ToastUnit;


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
        start();
    }

    private void enterActivity(boolean isHome) {
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
        }, 750);
    }

    private void start() {
        //网络状态
        if (!NetUnit.isConnected(this)) {
            ToastUnit.showShort("请检查设备网络状态");
            enterActivity(false);
            return;
        }
        //当前账号
        Account account = AccountSP.getCurrentAccount();
        if (account != null) {
            netQuerySystemInfo(account);
        } else {
            enterActivity(false);
        }
    }

    private void netCheckAccountValid(Account account) {
        QLApiController.checkToken(getNetRequestID(), account, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                enterActivity(true);
            }

            @Override
            public void onFailure(String msg) {
                enterActivity(false);
            }
        });
    }

    protected void netQuerySystemInfo(Account account) {
        QLApiController.getSystemInfo(this.getNetRequestID(), account, new QLApiController.NetSystemCallback() {
            @Override
            public void onSuccess(QLSystem system) {
                QLSystem.setStaticVersion(system.getVersion());
                netCheckAccountValid(account);
            }

            @Override
            public void onFailure(String msg) {
                enterActivity(false);
            }
        });
    }

}