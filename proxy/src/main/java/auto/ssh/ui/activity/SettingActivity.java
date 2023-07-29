package auto.ssh.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import auto.base.util.Logger;
import auto.ssh.R;
import auto.ssh.ui.popup.Builder;
import auto.ssh.ui.popup.SelectItem;
import auto.ssh.ui.popup.SelectPopup;

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

        //
        uiLogLevel.setOnClickListener(v -> {
            SelectPopup selectPopup = new SelectPopup();
            List<Logger.LogLevel> levels = Logger.getLevels();

            for (Logger.LogLevel level : levels) {
                SelectItem item = new SelectItem(level.getName(), level.getLevel(), false);
                selectPopup.addItem(item);
            }

            Builder.buildSelectWindow(self, uiLogLevel, selectPopup);
        });
    }
}