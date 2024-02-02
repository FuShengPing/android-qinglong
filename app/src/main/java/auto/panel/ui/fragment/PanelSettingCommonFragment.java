package auto.panel.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemConfig;
import auto.panel.database.sp.PanelPreference;
import auto.panel.ui.activity.LoginActivity;
import auto.panel.utils.ActivityUtils;
import auto.panel.utils.TextUnit;
import auto.panel.utils.ToastUnit;

public class PanelSettingCommonFragment extends BaseFragment {
    public static String TAG = "PanelSettingCommonFragment";
    public static String NAME = "系统设置-常规设置";

    private EditText uiSecurityUsername;
    private EditText uiSecurityPassword;
    private Button uiSecuritySave;
    private EditText uiLogRemoveFrequency;
    private EditText uiCronConcurrency;
    private Button uiConfigSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_setting_common, container, false);
        uiSecurityUsername = view.findViewById(R.id.setting_security_username);
        uiSecurityPassword = view.findViewById(R.id.setting_security_password);
        uiSecuritySave = view.findViewById(R.id.setting_security_save);
        uiLogRemoveFrequency = view.findViewById(R.id.setting_other_log);
        uiCronConcurrency = view.findViewById(R.id.setting_other_cron);
        uiConfigSave = view.findViewById(R.id.setting_other_save);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void init() {
        uiConfigSave.setOnClickListener(v -> {
            String logValue = uiLogRemoveFrequency.getText().toString();
            String cronValue = uiCronConcurrency.getText().toString();
            if (TextUnit.isEmpty(logValue) || TextUnit.isEmpty(cronValue)) {
                ToastUnit.showShort("请输入正确数值");
                return;
            }
            WindowUnit.hideKeyboard(uiLogRemoveFrequency);
            PanelSystemConfig config = new PanelSystemConfig();
            config.setLogRemoveFrequency(Integer.parseInt(logValue));
            config.setCronConcurrency(Integer.parseInt(cronValue));
            updateSystemConfig(config);
        });

        uiSecuritySave.setOnClickListener(v -> {
            String username = uiSecurityUsername.getText().toString();
            String password = uiSecurityPassword.getText().toString();

            if (username.isEmpty()) {
                ToastUnit.showShort("请输入用户名");
                return;
            }
            if (password.isEmpty()) {
                ToastUnit.showShort("请输入密码");
                return;
            }

            WindowUnit.hideKeyboard(uiSecurityUsername);
            PanelAccount account = new PanelAccount(username, password, PanelPreference.getAddress(), null);
            updateAccount(account);
        });

        getSystemConfig();
    }

    private void getSystemConfig() {
        auto.panel.net.panel.ApiController.getSystemConfig(new auto.panel.net.panel.ApiController.SystemConfigCallBack() {
            @Override
            public void onSuccess(PanelSystemConfig config) {
                uiLogRemoveFrequency.setText(String.valueOf(config.getLogRemoveFrequency()));
                uiCronConcurrency.setText(String.valueOf(config.getCronConcurrency()));
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void updateSystemConfig(PanelSystemConfig config) {
        auto.panel.net.panel.ApiController.updateSystemConfig( config, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiLogRemoveFrequency.clearFocus();
                uiCronConcurrency.clearFocus();
                ToastUnit.showShort("更新成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void updateAccount(PanelAccount account) {
        auto.panel.net.panel.ApiController.updateAccount(account, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                PanelPreference.updateCurrentAccount(account);
                login(account);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void login(PanelAccount account) {
        auto.panel.net.panel.ApiController.login(account.getBaseUrl(), account, new auto.panel.net.panel.ApiController.LoginCallBack() {
            @Override
            public void onSuccess(String token) {
                uiSecurityUsername.setText(null);
                uiSecurityPassword.setText(null);
                account.setToken(token);
                PanelPreference.updateCurrentAccount(account);
                ToastUnit.showShort("更新成功");
            }

            @Override
            public void onFailure(String msg) {
                ActivityUtils.clearAndStartActivity(requireActivity(), LoginActivity.class);
                ToastUnit.showShort(msg);
            }
        });
    }
}