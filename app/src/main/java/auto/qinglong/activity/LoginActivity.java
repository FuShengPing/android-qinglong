package auto.qinglong.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import auto.qinglong.R;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.res.SystemRes;
import auto.qinglong.database.db.AccountDBHelper;
import auto.qinglong.database.object.Account;
import auto.qinglong.database.sp.AccountSP;

import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.tools.CallManager;

public class LoginActivity extends BaseActivity {

    private ImageView layout_logo_ql;
    private ImageView layout_logo_github;
    private ImageView layout_logo_gitee;

    private Button layout_confirm;
    private EditText layout_address;
    private EditText layout_username;
    private EditText layout_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layout_logo_ql = findViewById(R.id.logo_ql);
        layout_logo_gitee = findViewById(R.id.logo_gitee);
        layout_logo_github = findViewById(R.id.logo_github);
        layout_confirm = findViewById(R.id.button_confirm);
        layout_address = findViewById(R.id.input_address);
        layout_username = findViewById(R.id.input_username);
        layout_password = findViewById(R.id.input_password);

        initWindow();
        initViewSetting();
    }

    @Override
    protected void initViewSetting() {
        layout_logo_ql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.url_qinglong));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        layout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                String address = layout_address.getText().toString();
                if (!address.matches("\\d{2,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}")) {
                    ToastUnit.showShort(myContext, "地址格式错误");
                    return;
                }

                String username = layout_username.getText().toString();
                if (username.isEmpty()) {
                    ToastUnit.showShort(myContext, "账号不能为空");
                    return;
                }

                String password = layout_password.getText().toString();
                if (password.isEmpty()) {
                    ToastUnit.showShort(myContext, "密码不能为空");
                    return;
                }
                WindowUnit.hideKeyboard(layout_password);

                //先检查是否存在数据库中，存在则先检查会话是否有效，不存在则进行登录
                Account account;
                if (AccountDBHelper.isAccountExist(address)) {
                    account = AccountDBHelper.getAccount(address);
                    querySystemInfo(account, false);
                } else {
                    account = new Account(username, password, address, "");
                    querySystemInfo(account, true);
                }
            }
        });

        //初始化账号
        Account account = AccountSP.getAccount();
        if (account != null) {
            layout_address.setText(account.getAddress());
            layout_username.setText(account.getUsername());
            layout_password.setText(account.getPassword());
        }
    }

    @Override
    protected void initWindow() {
        WindowUnit.setStatusBarTextColor(this, false);
        WindowUnit.setTranslucentStatus(this);
    }


    /**
     * 查询系统信息 主要是查看是否已经初始化
     *
     * @param account
     * @param isLogin
     */
    protected void querySystemInfo(Account account, boolean isLogin) {
        ApiController.getSystemInfo(this.getClassName(), account, new ApiController.SystemCallback() {
            @Override
            public void onSuccess(SystemRes systemRes) {
                if (systemRes.getData().isInitialized()) {
                    if (isLogin) {
                        login(account);
                    } else {
                        checkToken(account);
                    }
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
     * 检查会话是否有效，防止多次登录
     * @param account
     */
    protected void checkToken(Account account) {
        ApiController.checkToken(this.getClassName(), account, new ApiController.LoginCallback() {
            @Override
            public void onSuccess(Account account) {
                LogUnit.log(account.getAuthorization());
                enterMain(account);
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
                enterMain(account);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(myContext, msg);
            }
        });
    }

    private void enterMain(Account account) {
        //保存账号信息
        AccountSP.saveAccount(account);
        AccountDBHelper.insertAccount(account);
        //进入主界面
        Intent intent = new Intent(myContext, HomeActivity.class);
        startActivity(intent);
    }
}