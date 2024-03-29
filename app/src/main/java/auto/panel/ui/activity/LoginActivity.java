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
import auto.panel.database.db.AccountDataSource;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.utils.NetUnit;
import auto.panel.utils.ToastUnit;

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";
    public static final String MIN_VERSION = "2.15.0";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    private static final int ACTION_LOGIN = 0;
    private static final int ACTION_REGISTER = 1;


    private String extraAddress;
    private String extraUsername;
    private String extraPassword;

    private EditText uiAddress;
    private EditText uiUsername;
    private EditText uiPassword;
    private EditText uiCode;
    private Button uiLogin;
    private Button uiRegister;
    private View uiActionAccount;
    private View uiActionSetting;
    private ProgressPopupWindow uiPopProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        extraAddress = getIntent().getStringExtra(EXTRA_ADDRESS);
        extraUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        extraPassword = getIntent().getStringExtra(EXTRA_PASSWORD);

        uiAddress = findViewById(R.id.et_address);
        uiUsername = findViewById(R.id.et_username);
        uiPassword = findViewById(R.id.et_password);
        uiCode = findViewById(R.id.et_code);
        uiLogin = findViewById(R.id.bt_login);
        uiRegister = findViewById(R.id.bt_register);
        uiActionAccount = findViewById(R.id.action_account);
        uiActionSetting = findViewById(R.id.action_setting);

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

            buildProgressWindow();
            uiPopProgress.setTextAndShow("登录中...");

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

            buildProgressWindow();
            uiPopProgress.setTextAndShow("初始化中...");

            //检测系统是否初始化和版本信息(延迟500ms)
            new Handler().postDelayed(() -> netQuerySystemInfo(account, ACTION_REGISTER), 500);
        });

        uiActionAccount.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AccountActivity.class);
            intent.putExtra(AccountActivity.EXTRA_HIDE_ACTION_ADD, true);
            startActivity(intent);
        });

        uiActionSetting.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SettingActivity.class);
            startActivity(intent);
        });

        //显示预设信息
        if (extraAddress != null) {
            uiAddress.setText(extraAddress);
        }
        if (extraUsername != null) {
            uiUsername.setText(extraUsername);
        }
        if (extraPassword != null) {
            uiPassword.setText(extraPassword);
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

        PanelAccount account = new PanelAccount(address, username, password);

        if (uiCode.getVisibility() == View.VISIBLE && !code.isEmpty()) {
            account.setCode(code);
        }

        return account;
    }

    private void buildProgressWindow() {
        if (uiPopProgress == null) {
            uiPopProgress = PopupWindowBuilder.buildProgressWindow(this, () -> NetManager.cancelAllCall(getNetRequestID()));
        }
    }

    private void dismissProgressWindow() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            uiPopProgress.dismiss();
        }
    }

    /**
     * 进入主界面
     */
    private void toHomeActivity() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void netQuerySystemInfo(PanelAccount account, int action) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(PanelSystemInfo system) {
                account.setVersion(system.getVersion());
                if (action == ACTION_LOGIN) { //登录
                    if (system.getVersion().compareTo(MIN_VERSION) < 0) {
                        dismissProgressWindow();
                        ToastUnit.showShort("仅支持" + MIN_VERSION + "及以上版本");
                    } else if (!system.isInitialized()) {
                        dismissProgressWindow();
                        ToastUnit.showShort("系统未初始化，无法登录");
                    } else {
                        netLogin(account);
                    }
                } else if (action == ACTION_REGISTER) { //注册
                    if (system.isInitialized()) {
                        dismissProgressWindow();
                        ToastUnit.showShort("系统已初始化，无法注册");
                    } else {
                        netRegister(account);
                    }
                } else { //未知操作
                    dismissProgressWindow();
                    ToastUnit.showShort("未知操作");
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissProgressWindow();
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
                dismissProgressWindow();
                ToastUnit.showShort("初始化失败：" + msg);
            }
        });
    }

    protected void netLogin(PanelAccount panelAccount) {
        auto.panel.net.panel.ApiController.login(panelAccount.getBaseUrl(), panelAccount, new auto.panel.net.panel.ApiController.LoginCallBack() {
            @Override
            public void onSuccess(String token) {
                if(!token.startsWith("Bearer ")){
                    token = "Bearer " + token;
                }
                PanelPreference.setAddress(panelAccount.getAddress());
                PanelPreference.setAuthorization(token);
                AccountDataSource source = new AccountDataSource(mContext);
                source.insertOrUpdateAccount(panelAccount.getAddress(), panelAccount.getUsername(), panelAccount.getPassword(), token, panelAccount.getVersion());
                source.close();
                toHomeActivity();
            }

            @Override
            public void onFailure(String msg) {
                dismissProgressWindow();
                if (msg.endsWith("验证token")) {
                    uiCode.setVisibility(View.VISIBLE);
                }
                ToastUnit.showShort(msg);
            }
        });
    }
}