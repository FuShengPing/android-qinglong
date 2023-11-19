package auto.panel.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import auto.base.util.EncryptUtil;
import auto.base.util.ToastUnit;
import auto.base.util.WebUnit;
import auto.panel.R;
import auto.panel.bean.app.Config;
import auto.panel.database.sp.PanelPreference;
import auto.panel.database.sp.SettingPreference;
import auto.panel.net.app.ApiController;
import auto.panel.utils.DeviceUnit;
import auto.panel.utils.FileUtil;
import auto.panel.utils.thread.AppLogTask;
import auto.panel.utils.thread.ThreadPoolUtil;

public class SettingActivity extends BaseActivity {
    public static final String TAG = "SettingActivity";

    private View uiBack;
    private SwitchCompat uiNotifySwitch;

    private View uiStorage;
    private TextView uiStorageValue;
    private View uiPermission;
    private TextView uiPermissionValue;
    private View uiDocument;
    private View uiGroup;
    private View uiShare;
    private View uiAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_setting);

        uiBack = findViewById(R.id.bar_back);
        uiNotifySwitch = findViewById(R.id.app_setting_notify_switch);
        uiStorage = findViewById(R.id.app_setting_storage);
        uiStorageValue = findViewById(R.id.app_setting_storage_value);
        uiPermission = findViewById(R.id.app_setting_permission);
        uiPermissionValue = findViewById(R.id.app_setting_permission_value);
        uiDocument = findViewById(R.id.app_setting_document);
        uiGroup = findViewById(R.id.app_setting_group);
        uiShare = findViewById(R.id.app_setting_share);
        uiAbout = findViewById(R.id.app_setting_about);

        init();
    }

    private void onUpdateConfig(Config config) {
        uiDocument.setOnClickListener(v -> WebUnit.open(this, config.getDocumentUrl()));

        uiGroup.setOnClickListener(v -> joinQQGroup(config.getGroupKey()));

        uiShare.setOnClickListener(v -> DeviceUnit.shareText(this, config.getShareText()));
    }

    @Override
    protected void init() {
        uiBack.setOnClickListener(v -> finish());

        uiNotifySwitch.setChecked(SettingPreference.isNotify());
        uiNotifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SettingPreference.setBoolean(SettingPreference.FIELD_NOTIFY, isChecked));

        uiStorage.setOnClickListener(v -> {
            // 复制到剪切板
            DeviceUnit.copyText(SettingActivity.this, FileUtil.externalStorage);
            ToastUnit.showShort("已复制");
        });
        uiStorageValue.setText(FileUtil.externalStorage);

        if (FileUtil.checkStoragePermission()) {
            uiPermissionValue.setText("已授权");
        } else {
            uiPermissionValue.setText("未授权");
            uiPermission.setOnClickListener(v -> FileUtil.requestStoragePermission(mActivity));
        }

        uiDocument.setOnClickListener(v -> WebUnit.open(this, SettingPreference.getDocumentUrl()));

        uiGroup.setOnClickListener(v -> joinQQGroup(SettingPreference.getGroupKey()));

        uiShare.setOnClickListener(v -> DeviceUnit.shareText(this, SettingPreference.getShareText()));

        uiAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        netGetConfig();
    }

    private void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            ThreadPoolUtil.execute(new AppLogTask(e.getMessage()));
            ToastUnit.showShort("未安装手Q或安装的版本不支持");
        }
    }

    private void netGetConfig() {
        String uid = EncryptUtil.md5(PanelPreference.getAddress());

        ApiController.getConfig(uid, config -> {
            onUpdateConfig(config);
            SettingPreference.updateConfig(config);
        });
    }
}