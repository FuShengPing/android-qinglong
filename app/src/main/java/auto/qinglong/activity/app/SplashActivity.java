package auto.qinglong.activity.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.Account;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.ToastUnit;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    private void start() {
        //网络状态
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            ToastUnit.showShort("请检查设备网络状态");
            enterActivity(false);
            return;
        }
        //当前账号
        Account account = AccountSP.getCurrentAccount();
        if (account != null) {
            checkAccountValid(account);
        } else {
            enterActivity(false);
        }
    }

    private void checkAccountValid(Account account) {
        QLApiController.checkToken(getNetRequestID(), account, new QLApiController.NetLoginCallback() {
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



}