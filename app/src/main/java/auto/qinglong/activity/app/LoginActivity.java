package auto.qinglong.activity.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.Account;
import auto.qinglong.bean.ql.network.QLSystemRes;
import auto.qinglong.database.db.AccountDBHelper;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.PopupWindowBuilder;
import auto.qinglong.views.popup.ProgressWindow;

public class LoginActivity extends BaseActivity {

    private ImageView ui_logo;
    private Button ui_confirm;
    private EditText ui_address;
    private EditText ui_username;
    private EditText ui_password;
    private ProgressWindow ui_pop_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏黑色字体
        WindowUnit.setStatusBarTextColor(this, false);

        setContentView(R.layout.activity_login);

        ui_logo = findViewById(R.id.img_logo);
        ui_confirm = findViewById(R.id.bt_confirm);
        ui_address = findViewById(R.id.et_address);
        ui_username = findViewById(R.id.et_username);
        ui_password = findViewById(R.id.et_password);

        init();
    }

    @Override
    public void onBackPressed() {
        if (ui_pop_progress != null && ui_pop_progress.isShowing()) {
            ui_pop_progress.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void init() {
        ui_logo.setOnClickListener(v -> {
            Uri uri = Uri.parse(getString(R.string.url_gitee));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        ui_confirm.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            String address = ui_address.getText().toString();

            if (!address.matches("((\\d{1,3}\\.){3})\\d{1,3}:\\d{1,5}")) {
                ToastUnit.showShort("地址格式错误");
                return;
            }

            String username = ui_username.getText().toString().trim();
            if (username.isEmpty()) {
                ToastUnit.showShort("账号不能为空");
                return;
            }

            String password = ui_password.getText().toString().trim();
            if (password.isEmpty()) {
                ToastUnit.showShort("密码不能为空");
                return;
            }
            WindowUnit.hideKeyboard(ui_password);

            ui_confirm.setEnabled(false);
            ui_confirm.postDelayed(() -> ui_confirm.setEnabled(true), 300);

            if (ui_pop_progress == null) {
                ui_pop_progress = PopupWindowBuilder.buildProgressWindow(this, () -> RequestManager.cancelAllCall(getNetRequestID()));
            }
            ui_pop_progress.setTextAndShow("登录中...");

            Account account = new Account(username, password, address, "");
            //账号登录过则设置之前token
            if (AccountDBHelper.isAccountExist(address)) {
                account.setToken(AccountDBHelper.getAccount(address).getToken());
            }
            //检测系统是否初始化和版本信息
            netQuerySystemInfo(account);
        });

        //显示之前账号
        Account account = AccountSP.getCurrentAccount();
        if (account != null) {
            ui_address.setText(account.getAddress());
            ui_username.setText(account.getUsername());
            ui_password.setText(account.getPassword());
        }
    }

    private void enterHome(Account account) {
        //保存账号信息
        AccountSP.saveCurrentAccount(account);
        AccountDBHelper.insertAccount(account);
        //进入主界面
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void netQuerySystemInfo(Account account) {
        QLApiController.getSystemInfo(this.getNetRequestID(), account, new QLApiController.SystemCallback() {
            @Override
            public void onSuccess(QLSystemRes systemRes) {
                if (systemRes.getData().isInitialized()) {
                    netCheckToken(account);
                } else {
                    ui_pop_progress.dismiss();
                    ToastUnit.showShort("系统未初始化，无法登录");
                }
            }

            @Override
            public void onFailure(String msg) {
                ui_pop_progress.dismiss();
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void netCheckToken(Account account) {
        QLApiController.checkToken(this.getNetRequestID(), account, new QLApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                enterHome(account);
            }

            @Override
            public void onFailure(String msg) {
                netLogin(account);
            }
        });
    }

    protected void netLogin(Account account) {
        QLApiController.login(this.getNetRequestID(), account, new QLApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                enterHome(account);
            }

            @Override
            public void onFailure(String msg) {
                ui_pop_progress.dismiss();
                ToastUnit.showShort(msg);
            }
        });
    }
}