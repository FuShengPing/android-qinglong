package auto.qinglong.ui.activity.panel.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.qinglong.R;
import auto.qinglong.bean.panel.Account;
import auto.qinglong.bean.panel.SystemConfig;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.panel.v10.ApiController;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.app.LoginActivity;

public class CommonFragment extends BaseFragment {
    private EditText uiSecurityUsername;
    private EditText uiSecurityPassword;
    private Button uiSecuritySave;
    private EditText uiLogRemoveFrequency;
    private Button uiLogSave;

    private int mOldFrequency = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_common, container, false);
        uiLogRemoveFrequency = view.findViewById(R.id.setting_log);
        uiLogSave = view.findViewById(R.id.setting_log_save);
        uiSecurityUsername = view.findViewById(R.id.setting_security_username);
        uiSecurityPassword = view.findViewById(R.id.setting_security_password);
        uiSecuritySave = view.findViewById(R.id.setting_security_save);

        init();
        return view;
    }

    @Override
    protected void init() {

        uiLogSave.setOnClickListener(v -> {
            String value = uiLogRemoveFrequency.getText().toString();
            if (TextUnit.isEmpty(value)) {
                ToastUnit.showShort("请输入正确数值");
                return;
            }
            WindowUnit.hideKeyboard(uiLogRemoveFrequency);
            netUpdateLogRemove(Integer.parseInt(value), mOldFrequency);
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
            netUpdateUser(account);
        });

        getSystemConfig();
    }

    private void getSystemConfig() {
        auto.qinglong.net.panel.ApiController.getSystemConfig(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.qinglong.net.panel.ApiController.SystemConfigCallBack() {
            @Override
            public void onSuccess(SystemConfig config) {
                uiLogRemoveFrequency.setText(String.valueOf(config.getLogRemoveFrequency()));
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netUpdateLogRemove(int newFrequency, int oldFrequency) {
        ApiController.updateLogRemove(getNetRequestID(), newFrequency, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                uiLogRemoveFrequency.clearFocus();
                mOldFrequency = newFrequency;
                ToastUnit.showShort("保存成功");
            }

            @Override
            public void onFailure(String msg) {
                uiLogRemoveFrequency.clearFocus();
                uiLogRemoveFrequency.clearComposingText();
                if (oldFrequency > -1) {
                    uiLogRemoveFrequency.setText(String.valueOf(oldFrequency));
                } else {
                    uiLogRemoveFrequency.setText("");
                }
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netUpdateUser(Account account) {
        ApiController.updateUser(getNetRequestID(), account, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                PanelPreference.updateCurrentAccount(account);
                netLogin(account);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void netLogin(Account account) {
        auto.qinglong.net.panel.ApiController.login(account.getBaseUrl(), account, new auto.qinglong.net.panel.ApiController.LoginCallBack() {
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
                LogUnit.log(msg);
            }
        });
    }
}