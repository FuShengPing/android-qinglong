package auto.panel.ui.activity.panel.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.bean.panel.Account;
import auto.panel.bean.panel.SystemConfig;
import auto.panel.database.sp.PanelPreference;
import auto.panel.ui.BaseFragment;
import auto.panel.ui.activity.app.LoginActivity;

public class CommonFragment extends BaseFragment {
    private EditText uiSecurityUsername;
    private EditText uiSecurityPassword;
    private Button uiSecuritySave;
    private EditText uiLogRemoveFrequency;
    private EditText uiCronConcurrency;
    private Button uiConfigSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_common, container, false);
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
    protected void init() {
        uiConfigSave.setOnClickListener(v -> {
            String logValue = uiLogRemoveFrequency.getText().toString();
            String cronValue = uiCronConcurrency.getText().toString();
            if (TextUnit.isEmpty(logValue) || TextUnit.isEmpty(cronValue)) {
                ToastUnit.showShort("请输入正确数值");
                return;
            }
            WindowUnit.hideKeyboard(uiLogRemoveFrequency);
            SystemConfig config = new SystemConfig();
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
            Account account = new Account(username, password, PanelPreference.getAddress(), null);
            updateAccount(account);
        });

        getSystemConfig();
    }

    private void getSystemConfig() {
        auto.panel.net.panel.ApiController.getSystemConfig(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.panel.net.panel.ApiController.SystemConfigCallBack() {
            @Override
            public void onSuccess(SystemConfig config) {
                uiLogRemoveFrequency.setText(String.valueOf(config.getLogRemoveFrequency()));
                uiCronConcurrency.setText(String.valueOf(config.getCronConcurrency()));
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void updateSystemConfig(SystemConfig config) {
        auto.panel.net.panel.ApiController.updateSystemConfig(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), config, new auto.panel.net.panel.ApiController.BaseCallBack() {
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

    private void updateAccount(Account account) {
        auto.panel.net.panel.ApiController.updateAccount(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), account, new auto.panel.net.panel.ApiController.BaseCallBack() {
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

    protected void login(Account account) {
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
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
                ToastUnit.showShort(msg);
            }
        });
    }
}