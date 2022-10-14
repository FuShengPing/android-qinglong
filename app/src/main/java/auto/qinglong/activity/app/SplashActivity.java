package auto.qinglong.activity.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.app.account.Account;
import auto.qinglong.activity.app.login.LoginActivity;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.ApiController;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //避免点击icon重新打开
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    private void start() {
        Account account = AccountSP.getCurrentAccount();
        if (account == null) {
            enterActivity(false);
        } else {
            checkAccountValid(account);
        }
    }

    private void checkAccountValid(Account account) {
        ApiController.checkToken(getClassName(), account, new ApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                enterActivity(true);
            }

            @Override
            public void onFailure(String msg) {
                enterActivity(false);
            }
        });
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
        }, 1000);
    }

    @Override
    protected void init() {

    }

}