package auto.qinglong.activity.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.app.HomeActivity;
import auto.qinglong.network.ApiController;
import auto.qinglong.network.ql.SystemRes;
import auto.qinglong.database.db.AccountDBHelper;
import auto.qinglong.bean.app.Account;
import auto.qinglong.database.sp.AccountSP;

import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.network.RequestManager;

public class LoginActivity extends BaseActivity {

    private ImageView layout_logo_ql;

    private Button layout_confirm;
    private EditText layout_address;
    private EditText layout_username;
    private EditText layout_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏黑色字体
        WindowUnit.setStatusBarTextColor(this, false);

        setContentView(R.layout.activity_login);

        layout_logo_ql = findViewById(R.id.logo_ql);
        layout_confirm = findViewById(R.id.button_confirm);
        layout_address = findViewById(R.id.input_address);
        layout_username = findViewById(R.id.input_username);
        layout_password = findViewById(R.id.input_password);

        init();
    }

    @Override
    protected void init() {
        layout_logo_ql.setOnClickListener(v -> {
            Uri uri = Uri.parse(getString(R.string.url_qinglong));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        layout_confirm.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getClassName())) {
                return;
            }
            String address = layout_address.getText().toString();
            if (!address.matches("\\d{2,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}")) {
                ToastUnit.showShort(myContext, "地址格式错误");
                return;
            }

            String username = layout_username.getText().toString().trim();
            if (username.isEmpty()) {
                ToastUnit.showShort(myContext, "账号不能为空");
                return;
            }

            String password = layout_password.getText().toString().trim();
            if (password.isEmpty()) {
                ToastUnit.showShort(myContext, "密码不能为空");
                return;
            }
            WindowUnit.hideKeyboard(layout_password);

            Account account = new Account(username, password, address, "");
            //账号登录过则设置之前token
            if (AccountDBHelper.isAccountExist(address)) {
                account.setToken(AccountDBHelper.getAccount(address).getToken());
            }
            //检测系统是否初始化和版本信息
            querySystemInfo(account);
        });

        //初始化账号
        Account account = AccountSP.getCurrentAccount();
        if (account != null) {
            layout_address.setText(account.getAddress());
            layout_username.setText(account.getUsername());
            layout_password.setText(account.getPassword());
        }
    }

    /**
     * 查询系统信息 主要是查看是否已经初始化
     */
    protected void querySystemInfo(Account account) {
        ApiController.getSystemInfo(this.getClassName(), account, new ApiController.SystemCallback() {
            @Override
            public void onSuccess(SystemRes systemRes) {
                if (systemRes.getData().isInitialized()) {
                    checkToken(account);
                } else {
                    ToastUnit.showShort(myContext, "系统未初始化，无法登录");
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(myContext, msg);
            }
        });
    }

    /**
     * 检查会话是否有效，无效则登录
     */
    protected void checkToken(Account account) {
        ApiController.checkToken(this.getClassName(), account, new ApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                LogUnit.log(account.getAuthorization());
                enterHome(account);
            }

            @Override
            public void onFailure(String msg) {
                login(account);
            }
        });
    }

    protected void login(Account account) {
        ApiController.login(this.getClassName(), account, new ApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                enterHome(account);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(myContext, msg);
            }
        });
    }

    private void enterHome(Account account) {
        //保存账号信息
        AccountSP.saveCurrentAccount(account);
        AccountDBHelper.insertAccount(account);
        //进入主界面
        Intent intent = new Intent(myContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}