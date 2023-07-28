package auto.ssh.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import auto.ssh.R;

public class SettingActivity extends BaseActivity {
    private View uiExit;
    private View uiLogLevel;
    private TextView uiLogLevelValue;
    private View uiLogDelete;
    private TextView uiLogDeleteValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_setting);

        uiExit = findViewById(R.id.exit);
        uiLogLevel = findViewById(R.id.proxy_setting_log_level);
        uiLogLevelValue = findViewById(R.id.proxy_setting_log_level_value);
        uiLogDelete = findViewById(R.id.proxy_setting_log_delete);
        uiLogDeleteValue = findViewById(R.id.proxy_setting_log_delete_value);
        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());
    }
}