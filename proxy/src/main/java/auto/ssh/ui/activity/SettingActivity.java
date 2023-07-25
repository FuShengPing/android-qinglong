package auto.ssh.ui.activity;

import android.os.Bundle;
import android.view.View;

import auto.ssh.R;

public class SettingActivity extends BaseActivity {
    private View uiExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_setting);

        uiExit = findViewById(R.id.exit);

        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());
    }
}