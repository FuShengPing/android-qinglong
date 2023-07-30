package auto.panel.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;

import auto.base.util.DeviceUnit;
import auto.base.util.ToastUnit;
import auto.panel.R;
import auto.panel.bean.app.Config;
import auto.panel.database.sp.PanelPreference;
import auto.panel.database.sp.SettingPreference;
import auto.panel.net.app.ApiController;
import auto.base.util.EncryptUtil;
import auto.base.util.WebUnit;

public class SettingActivity extends BaseActivity {
    public static final String TAG = "SettingActivity";

    private ImageView uiBack;
    private SwitchCompat uiNotifySwitch;
    private SwitchCompat uiVibrateSwitch;
    private LinearLayout uiDocument;
    private LinearLayout uiIssue;
    private LinearLayout uiShare;
    private LinearLayout uiAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_setting);

        uiBack = findViewById(R.id.bar_back);
        uiNotifySwitch = findViewById(R.id.app_setting_notify_switch);
        uiVibrateSwitch = findViewById(R.id.app_setting_vibrate_switch);
        uiDocument = findViewById(R.id.app_setting_document);
        uiIssue = findViewById(R.id.app_setting_issue);
        uiShare = findViewById(R.id.app_setting_share);
        uiAbout = findViewById(R.id.app_setting_about);

        init();
    }

    private void onUpdateConfig(Config config) {
        uiDocument.setOnClickListener(v -> WebUnit.open(this, config.getDocumentUrl()));

        uiIssue.setOnClickListener(v -> joinQQGroup(config.getGroupKey()));

        uiShare.setOnClickListener(v -> DeviceUnit.shareText(this, config.getShareText()));
    }

    @Override
    protected void init() {
        uiBack.setOnClickListener(v -> finish());

        uiNotifySwitch.setChecked(SettingPreference.isNotify());
        uiNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingPreference.setBoolean(SettingPreference.FIELD_NOTIFY, isChecked));

        uiVibrateSwitch.setChecked(SettingPreference.isVibrate());
        uiVibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingPreference.setBoolean(SettingPreference.FIELD_VIBRATE, isChecked));

        uiDocument.setOnClickListener(v -> WebUnit.open(this, SettingPreference.getDocumentUrl()));

        uiIssue.setOnClickListener(v -> joinQQGroup(SettingPreference.getGroupKey()));

        uiShare.setOnClickListener(v -> DeviceUnit.shareText(this, SettingPreference.getShareText()));

        uiAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        getConfig();
    }

    private void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            ToastUnit.showShort("未安装手Q或安装的版本不支持");
        }
    }

    private void getConfig() {
        String uid = EncryptUtil.md5(PanelPreference.getAddress());

        ApiController.getConfig(uid, config -> {
            onUpdateConfig(config);
            SettingPreference.updateConfig(config);
        });
    }
}