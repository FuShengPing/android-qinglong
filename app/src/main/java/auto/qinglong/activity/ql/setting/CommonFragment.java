package auto.qinglong.activity.ql.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.app.LoginActivity;
import auto.qinglong.bean.app.Account;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;

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
            Account account = new Account(username, password, AccountSP.getAddress(), null);
            netUpdateUser(account);
        });

        netGetLogRemove();
    }

    private void netGetLogRemove() {
        QLApiController.getLogRemove(getNetRequestID(), new QLApiController.NetGetLogRemoveCallback() {
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
        QLApiController.updateLogRemove(getNetRequestID(), newFrequency, new QLApiController.NetBaseCallback() {
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
        QLApiController.updateUser(getNetRequestID(), account, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                AccountSP.updateCurrentAccount(account);
                netLogin(account);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    protected void netLogin(Account account) {
        QLApiController.login(this.getNetRequestID(), account, new QLApiController.NetLoginCallback() {
            @Override
            public void onSuccess(Account account) {
                ui_security_username.setText(null);
                ui_security_password.setText(null);
                AccountSP.updateCurrentAccount(account);
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