package auto.ssh.ui.activity;

import android.os.Bundle;
import android.view.View;

import auto.ssh.R;

public class ConfigActivity extends BaseActivity {
    private View uiExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);

        uiExit = findViewById(R.id.exit);

        init();
    }

    private void init(){
        // 退出
        uiExit.setOnClickListener(v -> finish());
    }
}