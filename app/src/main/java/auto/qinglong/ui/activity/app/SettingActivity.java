package auto.qinglong.ui.activity.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;

import java.net.URLEncoder;

import auto.base.util.DeviceUnit;
import auto.base.util.ToastUnit;
import auto.qinglong.R;
import auto.qinglong.database.sp.SettingPreference;
import auto.qinglong.ui.BaseActivity;
import auto.qinglong.utils.WebUnit;

public class SettingActivity extends BaseActivity {
    public static final String TAG = "SettingActivity";

    private ImageView uiBack;
    private SwitchCompat uiNotifySwitch;
    private SwitchCompat uiVibrateSwitch;
    private LinearLayout uiHelp;
    private LinearLayout uiIssue;
    private LinearLayout uiDonate;
    private LinearLayout uiShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        uiBack = findViewById(R.id.bar_back);
        uiNotifySwitch = findViewById(R.id.app_setting_notify_switch);
        uiVibrateSwitch = findViewById(R.id.app_setting_vibrate_switch);
        uiHelp = findViewById(R.id.app_setting_document);
        uiIssue = findViewById(R.id.app_setting_issue);
        uiShare = findViewById(R.id.app_setting_share);
        uiDonate = findViewById(R.id.app_setting_donate);

        init();
    }

    @Override
    protected void init() {
        uiBack.setOnClickListener(v -> finish());

        uiNotifySwitch.setChecked(SettingPreference.isNotify());
        uiNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingPreference.setBoolean(SettingPreference.FIELD_NOTIFY, isChecked));
        uiVibrateSwitch.setChecked(SettingPreference.isVibrate());
        uiVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingPreference.setBoolean(SettingPreference.FIELD_VIBRATE, isChecked));

        uiHelp.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_readme)));

        uiIssue.setOnClickListener(v -> WebUnit.open(this, getString(R.string.url_issue)));

        uiShare.setOnClickListener(v -> DeviceUnit.shareText(this, getString(R.string.app_share_description)));

        uiDonate.setOnClickListener(v -> {
            try {
                String scheme = getString(R.string.url_alipay_scheme) + URLEncoder.encode(getString(R.string.url_alipay_qrcode), "UTF-8");
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