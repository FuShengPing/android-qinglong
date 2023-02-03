package auto.qinglong.activity.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.utils.DeviceUnit;
import auto.qinglong.utils.WebUnit;

public class SettingActivity extends BaseActivity {
    private ImageView ui_back;

    private LinearLayout ui_document;
    private LinearLayout ui_issue;
    private LinearLayout ui_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ui_back = findViewById(R.id.bar_back);
        ui_document = findViewById(R.id.app_setting_document);
        ui_issue = findViewById(R.id.app_setting_issue);
        ui_share = findViewById(R.id.app_setting_share);

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_document.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_readme)));

        ui_issue.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_issue)));

        ui_share.setOnClickListener(v -> DeviceUnit.shareText(this, getString(R.string.app_share_description)));
    }
}