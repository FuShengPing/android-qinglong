package auto.ssh.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import auto.ssh.R;
import auto.ssh.bean.Config;
import auto.ssh.data.ConfigPreference;
import auto.ssh.ui.popup.Builder;
import auto.ssh.ui.popup.InputPopup;

public class ConfigActivity extends BaseActivity {
    private View uiExit;
    private View uiLocalAddress;
    private TextView uiLocalAddressValue;
    private View uiLocalPort;
    private TextView uiLocalPortValue;
    private View uiRemoteAddress;
    private TextView uiRemoteAddressValue;
    private View uiRemotePort;
    private TextView uiRemotePortValue;
    private View uiRemoteUsername;
    private TextView uiRemoteUsernameValue;
    private View uiRemotePassword;
    private TextView uiRemotePasswordValue;
    private View uiRemoteForwardAddress;
    private TextView uiRemoteForwardAddressValue;
    private View uiRemoteForwardPort;
    private TextView uiRemoteForwardPortValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.proxy_activity_config);

        uiExit = findViewById(R.id.exit);
        uiLocalAddress = findViewById(R.id.config_local_address);
        uiLocalAddressValue = findViewById(R.id.config_local_address_value);
        uiLocalPort = findViewById(R.id.config_local_port);
        uiLocalPortValue = findViewById(R.id.config_local_port_value);
        uiRemoteAddress = findViewById(R.id.config_remote_address);
        uiRemoteAddressValue = findViewById(R.id.config_remote_address_value);
        uiRemotePort = findViewById(R.id.config_remote_port);
        uiRemotePortValue = findViewById(R.id.config_remote_port_value);
        uiRemoteUsername = findViewById(R.id.config_remote_username);
        uiRemoteUsernameValue = findViewById(R.id.config_remote_username_value);
        uiRemotePassword = findViewById(R.id.config_remote_password);
        uiRemotePasswordValue = findViewById(R.id.config_remote_password_value);
        uiRemoteForwardAddress = findViewById(R.id.config_remote_forward_address);
        uiRemoteForwardAddressValue = findViewById(R.id.config_remote_forward_address_value);
        uiRemoteForwardPort = findViewById(R.id.config_remote_forward_port);
        uiRemoteForwardPortValue = findViewById(R.id.config_remote_forward_port_value);

        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());

        // 配置信息
        Config config = ConfigPreference.getConfig();

        uiLocalAddressValue.setText(config.getLocalAddress());
        uiLocalPortValue.setText(String.valueOf(config.getLocalPort()));
        uiRemoteAddressValue.setText(config.getRemoteAddress());
        uiRemotePortValue.setText(String.valueOf(config.getRemotePort()));
        uiRemoteUsernameValue.setText(config.getRemoteUsername());
        if (config.getRemotePassword().isEmpty()) {
            uiRemotePasswordValue.setText("未设置");
        } else {
            uiRemotePasswordValue.setText(config.getRemotePassword());
        }
        uiRemoteForwardAddressValue.setText(config.getRemoteForwardAddress());
        uiRemoteForwardPortValue.setText(String.valueOf(config.getRemoteForwardPort()));

        // 远程地址
        uiRemoteAddress.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("远程地址", null, config.getRemoteAddress());
            inputPopup.setLength(15);
            inputPopup.setType(InputType.TYPE_CLASS_PHONE);

            inputPopup.setActionListener(value -> {
                uiRemoteAddressValue.setText(value);
                config.setRemoteAddress(value);
                ConfigPreference.setRemoteAddress(value);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });

        // 远程端口
        uiRemotePort.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("远程端口", null, String.valueOf(config.getRemotePort()));
            inputPopup.setLength(5);
            inputPopup.setType(InputType.TYPE_CLASS_NUMBER);

            inputPopup.setActionListener(value -> {
                int port = Integer.parseInt(value);
                uiRemotePortValue.setText(value);
                config.setRemotePort(port);
                ConfigPreference.setRemotePort(port);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });

        // 远程用户名
        uiRemoteUsername.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("用户名", null, config.getRemoteUsername());
            inputPopup.setLength(20);
            inputPopup.setType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            inputPopup.setActionListener(value -> {
                uiRemoteUsernameValue.setText(value);
                config.setRemoteUsername(value);
                ConfigPreference.setRemoteUsername(value);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });

        // 远程密码
        uiRemotePassword.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("密码", null, config.getRemotePassword());
            inputPopup.setLength(30);
            inputPopup.setType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            inputPopup.setActionListener(value -> {
                uiRemotePasswordValue.setText(value);
                config.setRemotePassword(value);
                ConfigPreference.setRemotePassword(value);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });

        // 远程转发端口
        uiRemoteForwardPort.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("转发端口", null, String.valueOf(config.getRemoteForwardPort()));
            inputPopup.setLength(5);
            inputPopup.setType(InputType.TYPE_CLASS_NUMBER);

            inputPopup.setActionListener(value -> {
                int port = Integer.parseInt(value);
                uiRemoteForwardPortValue.setText(value);
                config.setRemoteForwardPort(port);
                ConfigPreference.setRemoteForwardPort(port);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });

    }

}