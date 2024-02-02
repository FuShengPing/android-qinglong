package auto.panel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.ui.popup.ProgressPopupWindow;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.utils.NetUnit;
import auto.panel.utils.TextUnit;
import auto.panel.utils.ToastUnit;

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";
    public static final String MIN_VERSION = "2.15.0";
    private static final int ACTION_LOGIN = 0;
    private static final int ACTION_REGISTER = 1;

    private EditText uiAddress;
    private EditText uiUsername;
    private EditText uiPassword;
    private EditText uiCode;
    private Button uiLogin;
    private Button uiRegister;
    private ProgressPopupWindow uiPopProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.panel_activity_login);

        uiAddress = findViewById(R.id.et_address);
        uiUsername = findViewById(R.id.et_username);
        uiPassword = findViewById(R.id.et_password);
        uiCode = findViewById(R.id.et_code);
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
            PanelAccount account = checkInput();

            if (account == null) {
                return;
            }

            uiLogin.setEnabled(false);
            uiLogin.postDelayed(() -> uiLogin.setEnabled(true), 300);

            buildPopWindowProgress();
            uiPopProgress.setTextAndShow("登录中...");

            //为当前存在账号则尝试旧token 避免重复登录
            account.setToken(PanelPreference.getAuthorization(account.getAddress(), account.getUsername(), account.getPassword()));
            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> netQuerySystemInfo(account, ACTION_LOGIN), 500);
        });

        uiRegister.setOnClickListener(v -> {
            PanelAccount account = checkInput();

            if (account == null) {
                return;
            }

            uiRegister.setEnabled(false);
            uiRegister.postDelayed(() -> uiRegister.setEnabled(true), 300);

            buildPopWindowProgress();
            uiPopProgress.setTextAndShow("初始化中...");

            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> netQuerySystemInfo(account, ACTION_REGISTER), 500);
        });

        //显示之前账号
        PanelAccount account = PanelPreference.getCurrentAccount();
        if (account != null) {
            uiAddress.setText(account.getAddress());
            uiUsername.setText(account.getUsername());
            uiPassword.setText(account.getPassword());
        }
    }

    private PanelAccount checkInput() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            return null;
        }

        String address = uiAddress.getText().toString();
        String username = uiUsername.getText().toString().trim();
        String password = uiPassword.getText().toString().trim();
        String code = uiCode.getText().toString().trim();

        if (!NetUnit.checkUrlValid(address)) {
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

        PanelAccount account = new PanelAccount(username, password, address, null);

        if (uiCode.getVisibility() == View.VISIBLE && !code.isEmpty()) {
            account.setCode(code);
        }

        return account;
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

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void netQuerySystemInfo(PanelAccount account, int action) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(PanelSystemInfo system) {
                PanelPreference.setVersion(system.getVersion());
                if (action == ACTION_LOGIN) {
                    if (system.getVersion().compareTo(MIN_VERSION) < 0) {
                        dismissProgress();
                        ToastUnit.showShort("仅支持" + MIN_VERSION + "及以上版本");
                    } else if (!system.isInitialized()) {
                        dismissProgress();
                        ToastUnit.showShort("系统未初始化，无法登录");
                    } else if (TextUnit.isFull(account.getToken())) {
                        netCheckAccountToken(account);
                    } else {
                        netLogin(account);
                    }
                } else if (action == ACTION_REGISTER) {
                    if (system.isInitialized()) {
                        dismissProgress();
                        ToastUnit.showShort("系统已初始化，无法注册");
                    } else {
                        netRegister(account);
                    }
                } else {
                    dismissProgress();
                    ToastUnit.showShort("未知操作");
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissProgress();
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void netRegister(PanelAccount account) {
        ApiController.initAccount(account.getBaseUrl(), account, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                netLogin(account);
            }

            @Override
            public void onFailure(String msg) {
                dismissProgress();
                ToastUnit.showShort("初始化失败：" + msg);
            }
        });
    }

    protected void netLogin(PanelAccount account) {
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
                if (msg.endsWith("验证token")) {
                    uiCode.setVisibility(View.VISIBLE);
                }
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void netCheckAccountToken(PanelAccount account) {
        auto.panel.net.panel.ApiController.checkAccountToken(new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                enterHome();
            }

            @Override
            public void onFailure(String msg) {
                netLogin(account);
            }
        });
    }
}