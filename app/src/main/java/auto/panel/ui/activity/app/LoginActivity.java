package auto.panel.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.view.popup.PopProgressWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.panel.R;
import auto.panel.bean.panel.Account;
import auto.panel.bean.panel.SystemInfo;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.BaseActivity;

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";
    private static final int ACTION_LOGIN = 0;
    private static final int ACTION_REGISTER = 1;

    private EditText uiAddress;
    private EditText uiUsername;
    private EditText uiPassword;
    private Button uiLogin;
    private Button uiRegister;

    private PopProgressWindow uiPopProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        uiAddress = findViewById(R.id.et_address);
        uiUsername = findViewById(R.id.et_username);
        uiPassword = findViewById(R.id.et_password);
        uiLogin = findViewById(R.id.bt_login);
        uiRegister = findViewById(R.id.bt_register);

        init();
    }

    @Override
    protected void onDestroy() {
        //关闭pop 防止内存泄漏
        if (uiPopProgress != null) {
            uiPopProgress.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            uiPopProgress.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void init() {
        uiPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                uiPassword.clearFocus();
                uiLogin.performClick();
                return true;
            }
            return false;
        });

        uiLogin.setOnClickListener(v -> {
            Account account = checkInput();

            if (account == null) {
                return;
            }

            uiLogin.setEnabled(false);
            uiLogin.postDelayed(() -> uiLogin.setEnabled(true), 300);

            buildPopWindowProgress();
            uiPopProgress.setTextAndShow("登录中...");

            //账号存在本地则尝试旧token 避免重复登录
            account.setToken(PanelPreference.getAuthorization(account.getAddress(), account.getUsername(), account.getPassword()));
            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> querySystemInfo(account, ACTION_LOGIN), 500);
        });

        uiRegister.setOnClickListener(v -> {
            Account account = checkInput();

            if (account == null) {
                return;
            }

            uiRegister.setEnabled(false);
            uiRegister.postDelayed(() -> uiRegister.setEnabled(true), 300);

            buildPopWindowProgress();
            uiPopProgress.setTextAndShow("初始化中...");

            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> querySystemInfo(account, ACTION_REGISTER), 500);
        });

        //显示之前账号
        Account account = PanelPreference.getCurrentAccount();
        if (account != null) {
            uiAddress.setText(account.getAddress());
            uiUsername.setText(account.getUsername());
            uiPassword.setText(account.getPassword());
        }
    }

    private void buildPopWindowProgress() {
        if (uiPopProgress == null) {
            uiPopProgress = PopupWindowBuilder.buildProgressWindow(this, () -> NetManager.cancelAllCall(getNetRequestID()));
        }
    }

    private void dismissProgress() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            uiPopProgress.dismiss();
        }
    }

    private Account checkInput() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            return null;
        }

        String address = uiAddress.getText().toString();
        String username = uiUsername.getText().toString().trim();
        String password = uiPassword.getText().toString().trim();

        if (!address.matches("[0-9a-zA-Z.:/_-]+")) {
            ToastUnit.showShort("地址格式错误");
            return null;
        } else if (username.isEmpty()) {
            ToastUnit.showShort("账号不能为空");
            return null;
        } else if (password.isEmpty()) {
            ToastUnit.showShort("密码不能为空");
            return null;
        }

        WindowUnit.hideKeyboard(uiPassword);

        return new Account(username, password, address, null);
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void querySystemInfo(Account account, int action) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(SystemInfo system) {
                PanelPreference.setVersion(system.getVersion());

                if (action == ACTION_LOGIN) {
                    if (!system.isInitialized()) {
                        dismissProgress();
                        ToastUnit.showShort("系统未初始化，无法登录");
                    } else if (TextUnit.isFull(account.getToken())) {
                        checkAccountToken(account);
                    } else {
                        login(account);
                    }
                } else {
                    if (system.isInitialized()) {
                        dismissProgress();
                        ToastUnit.showShort("系统已初始化，无法注册");
                    } else {
                        register(account);
                    }
                }

            }

            @Override
            public void onFailure(String msg) {
                dismissProgress();
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void checkAccountToken(Account account) {
        auto.panel.net.panel.ApiController.checkAccountToken(account.getBaseUrl(), account.getAuthorization(), new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                enterHome();
            }

            @Override
            public void onFailure(String msg) {
                login(account);
            }
        });
    }

    protected void register(Account account) {

    }

    protected void login(Account account) {
        auto.panel.net.panel.ApiController.login(account.getBaseUrl(), account, new auto.panel.net.panel.ApiController.LoginCallBack() {
            @Override
            public void onSuccess(String token) {
                account.setToken(token);
                PanelPreference.updateCurrentAccount(account);
                enterHome();
            }

            @Override
            public void onFailure(String msg) {
                dismissProgress();
                ToastUnit.showShort(msg);
            }
        });
    }
}