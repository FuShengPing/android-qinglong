package auto.qinglong.activity.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;

import java.net.URLEncoder;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.database.sp.SettingSP;
import auto.qinglong.utils.DeviceUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WebUnit;

public class SettingActivity extends BaseActivity {
    private ImageView ui_back;
    private SwitchCompat ui_notify_switch;
    private LinearLayout ui_document;
    private LinearLayout ui_issue;
    private LinearLayout ui_donate;
    private LinearLayout ui_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ui_back = findViewById(R.id.bar_back);
        ui_notify_switch = findViewById(R.id.app_setting_notify_switch);
        ui_document = findViewById(R.id.app_setting_document);
        ui_issue = findViewById(R.id.app_setting_issue);
        ui_share = findViewById(R.id.app_setting_share);
        ui_donate = findViewById(R.id.app_setting_donate);

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_notify_switch.setChecked(SettingSP.isNotify());
        ui_notify_switch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingSP.setBoolean(SettingSP.FIELD_NOTIFY, isChecked));

        ui_document.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_readme)));

        ui_issue.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_issue)));

        ui_share.setOnClickListener(v -> DeviceUnit.shareText(this, getString(R.string.app_share_description)));

        ui_donate.setOnClickListener(v -> {
            //跳转支付宝支付
            try {
                String scheme = getString(R.string.alipay_scheme) + URLEncoder.encode(getString(R.string.alipay_qrcode), "UTF-8");
                Uri uri = Uri.parse(scheme);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {
                ToastUnit.showShort(e.getLocalizedMessage());
            }

        });
    }
}