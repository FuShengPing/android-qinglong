package auto.qinglong.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.view.popup.PopProgressWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.panel.Account;
import auto.qinglong.bean.panel.SystemInfo;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.ApiController;
import auto.qinglong.ui.BaseActivity;

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";

    private ImageView uiLogo;
    private Button uiConfirm;
    private EditText uiAddress;
    private EditText uiUsername;
    private EditText uiPassword;
    private PopProgressWindow uiPopProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        uiLogo = findViewById(R.id.img_logo);
        uiConfirm = findViewById(R.id.bt_confirm);
        uiAddress = findViewById(R.id.et_address);
        uiUsername = findViewById(R.id.et_username);
        uiPassword = findViewById(R.id.et_password);

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
                uiConfirm.performClick();
                return true;
            }
            return false;
        });

        uiConfirm.setOnClickListener(v -> {
            if (uiPopProgress != null && uiPopProgress.isShowing()) {
                return;
            }

            String address = uiAddress.getText().toString();

            if (!address.matches("[0-9a-zA-Z.:/_-]+")) {
                ToastUnit.showShort("地址格式错误");
                return;
            }

            String username = uiUsername.getText().toString().trim();
            if (username.isEmpty()) {
                ToastUnit.showShort("账号不能为空");
                return;
            }

            String password = uiPassword.getText().toString().trim();
            if (password.isEmpty()) {
                ToastUnit.showShort("密码不能为空");
                return;
            }
            WindowUnit.hideKeyboard(uiPassword);

            uiConfirm.setEnabled(false);
            uiConfirm.postDelayed(() -> uiConfirm.setEnabled(true), 300);

            if (uiPopProgress == null) {
                uiPopProgress = PopupWindowBuilder.buildProgressWindow(this, () -> NetManager.cancelAllCall(getNetRequestID()));
            }
            uiPopProgress.setTextAndShow("登录中...");

            Account account = new Account(username, password, address, "");
            //账号存在本地则尝试旧token 避免重复登录
            account.setToken(PanelPreference.getAuthorization(address, username, password));
            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> querySystemInfo(account), 500);
        });

        //显示之前账号
        Account account = PanelPreference.getCurrentAccount();
        if (account != null) {
            uiAddress.setText(account.getAddress());
            uiUsername.setText(account.getUsername());
            uiPassword.setText(account.getPassword());
        }
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void querySystemInfo(Account account) {
        auto.qinglong.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(SystemInfo system) {
                PanelPreference.setVersion(system.getVersion());
                if (!system.isInitialized()) {
                    uiPopProgress.dismiss();
                    ToastUnit.showShort("系统未初始化，无法登录");
                } else if (TextUnit.isFull(account.getToken())) {
                    checkAccountToken(account);
                } else {
                    login(account);
                }
            }

            @Override
            public void onFailure(String msg) {
                uiPopProgress.dismiss();
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void checkAccountToken(Account account) {
        auto.qinglong.net.panel.ApiController.checkAccountToken(account.getBaseUrl(), account.getAuthorization(), new auto.qinglong.net.panel.ApiController.BaseCallBack() {
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

    protected void login(Account account) {
        auto.qinglong.net.panel.ApiController.login(account.getBaseUrl(), account, new auto.qinglong.net.panel.ApiController.LoginCallBack() {
            @Override
            public void onSuccess(String token) {
                account.setToken(token);
                PanelPreference.updateCurrentAccount(account);
                enterHome();
            }

            @Override
            public void onFailure(String msg) {
                uiPopProgress.dismiss();
                ToastUnit.showShort(msg);
            }
        });
    }
}