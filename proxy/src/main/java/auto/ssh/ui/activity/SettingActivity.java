package auto.ssh.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import auto.base.util.Logger;
import auto.ssh.R;
import auto.ssh.bean.SettingParams;
import auto.ssh.data.SettingPrefence;
import auto.ssh.ui.popup.Builder;
import auto.ssh.ui.popup.InputPopup;
import auto.ssh.ui.popup.SelectItem;
import auto.ssh.ui.popup.SelectPopup;

public class SettingActivity extends BaseActivity {
    private View uiExit;
    private View uiAliveWakeup;
    private TextView uiAliveWakeupValue;
    private View uiAliveRefresh;
    private TextView uiAliveRefreshValue;
    private View uiAliveBattery;
    private TextView uiAliveBatteryValue;
    private View uiAliveWhitelist;
    private View uiLogLevel;
    private TextView uiLogLevelValue;
    private View uiLogDelete;
    private TextView uiLogDeleteValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_setting);

        uiExit = findViewById(R.id.exit);
        uiAliveWakeup = findViewById(R.id.proxy_setting_wakeup);
        uiAliveWakeupValue = findViewById(R.id.proxy_setting_wakeup_value);
        uiAliveRefresh = findViewById(R.id.proxy_setting_refresh);
        uiAliveRefreshValue = findViewById(R.id.proxy_setting_refresh_value);
        uiAliveBattery = findViewById(R.id.proxy_setting_battery);
        uiAliveBatteryValue = findViewById(R.id.proxy_setting_battery_value);
        uiAliveWhitelist = findViewById(R.id.proxy_setting_whitelist);
        uiLogLevel = findViewById(R.id.proxy_setting_log_level);
        uiLogLevelValue = findViewById(R.id.proxy_setting_log_level_value);
        uiLogDelete = findViewById(R.id.proxy_setting_log_delete);
        uiLogDeleteValue = findViewById(R.id.proxy_setting_log_delete_value);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());

        SettingParams params = SettingPrefence.getSettingParams();

        if (params.isServiceWakeup()) {
            uiAliveWakeupValue.setText("已开启");
        } else {
            uiAliveWakeupValue.setText("未开启");
        }
        if (isIgnoringBatteryOptimizations()) {
            uiAliveBatteryValue.setText("已开启");
        } else {
            uiAliveBatteryValue.setText("未开启");
        }
        uiAliveRefreshValue.setText(params.getServiceRefreshInterval() + "秒");
        uiLogLevelValue.setText(Logger.getLevelName(params.getLogLevel()));
        uiLogDeleteValue.setText(params.getLogDeleteFrequency() + "天");

        // 忽略电池优化
        uiAliveBattery.setOnClickListener(v -> requestIgnoreBatteryOptimizations());

        // 后台白名单
        uiAliveWhitelist.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        // 日志级别
        uiLogLevel.setOnClickListener(v -> {
            SelectPopup selectPopup = new SelectPopup();
            List<Logger.LogLevel> levels = Logger.getLevels();

            for (Logger.LogLevel level : levels) {
                SelectItem item = new SelectItem(level.getName(), level.getValue(), false);
                if (params.getLogLevel() == level.getValue()) {
                    item.setSelected(true);
                }
                selectPopup.addItem(item);
            }

            selectPopup.setSelectListener(value -> {
                uiLogLevelValue.setText(Logger.getLevelName((Integer) value));
                Logger.setLevel((Integer) value);
                params.setLogLevel((Integer) value);
                SettingPrefence.setLogLevel((Integer) value);
                return true;
            });

            Builder.buildSelectWindow(self, uiLogLevel, selectPopup);
        });

        // 日志删除频率
        uiLogDelete.setOnClickListener(v -> {
            InputPopup inputPopup = new InputPopup("删除频率", null, String.valueOf(params.getLogDeleteFrequency()));
            inputPopup.setType(InputType.TYPE_CLASS_NUMBER);
            inputPopup.setLength(2);

            inputPopup.setActionListener(value -> {
                int frequency = Integer.parseInt(value);
                uiLogDeleteValue.setText(frequency + "天");
                params.setLogDeleteFrequency(frequency);
                SettingPrefence.setLogDeleteFrequency(frequency);
                return true;
            });

            Builder.buildInputWindow(self, inputPopup);
        });
    }

    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }
}