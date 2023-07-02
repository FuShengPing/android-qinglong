package auto.qinglong.ui.activity.panel.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import auto.base.util.WindowUnit;
import auto.qinglong.R;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.app.LoginActivity;
import auto.qinglong.bean.panel.Account;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.panel.v10.ApiController;
import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;

public class CommonFragment extends BaseFragment {
    private EditText ui_log;
    private Button ui_log_save;
    private EditText ui_security_username;
    private EditText ui_security_password;
    private Button ui_security_save;

    private int mOldFrequency = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_common, container, false);
        ui_log = view.findViewById(R.id.setting_log);
        ui_log_save = view.findViewById(R.id.setting_log_save);
        ui_security_username = view.findViewById(R.id.setting_security_username);
        ui_security_password = view.findViewById(R.id.setting_security_password);
        ui_security_save = view.findViewById(R.id.setting_security_save);

        init();
        return view;
    }

    @Override
    protected void init() {

        ui_log_save.setOnClickListener(v -> {
            String value = ui_log.getText().toString();
            if (TextUnit.isEmpty(value)) {
                ToastUnit.showShort("请输入正确数值");
                return;
            }
            WindowUnit.hideKeyboard(ui_log);
            netUpdateLogRemove(Integer.parseInt(value), mOldFrequency);
        });

        ui_security_save.setOnClickListener(v -> {
            String username = ui_security_username.getText().toString();
            String password = ui_security_password.getText().toString();

            if (username.isEmpty()) {
                ToastUnit.showShort("请输入用户名");
                return;
            }
            if (password.isEmpty()) {
                ToastUnit.showShort("请输入密码");
                return;
            }

            WindowUnit.hideKeyboard(ui_security_username);
            Account account = new Account(username, password, PanelPreference.getAddress(), null);
            netUpdateUser(account);
        });

        netGetLogRemove();
    }

    private void netGetLogRemove() {
        ApiController.getLogRemove(getNetRequestID(), new ApiController.NetGetLogRemoveCallback() {
            @Override
            public void onSuccess(int frequency) {
                ui_log.setText(String.valueOf(frequency));
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
                ui_log.clearFocus();
                mOldFrequency = newFrequency;
                ToastUnit.showShort("保存成功");
            }

            @Override
            public void onFailure(String msg) {
                ui_log.clearFocus();
                ui_log.clearComposingText();
                if (oldFrequency > -1) {
                    ui_log.setText(String.valueOf(oldFrequency));
                } else {
                    ui_log.setText("");
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
                ui_security_username.setText(null);
                ui_security_password.setText(null);
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