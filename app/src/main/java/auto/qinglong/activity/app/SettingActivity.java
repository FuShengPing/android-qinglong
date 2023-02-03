package auto.qinglong.activity.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;

public class SettingActivity extends BaseActivity {
    private ImageView ui_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ui_back = findViewById(R.id.bar_back);

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());
    }
}