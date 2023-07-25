package auto.ssh.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import auto.base.util.WindowUnit;
import auto.ssh.R;

public class ConfigActivity extends BaseActivity {
    private View uiExit;
    private View uiRemotePort;
    private TextView uiReometePortValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.proxy_activity_config);

        uiExit = findViewById(R.id.exit);
        uiRemotePort = findViewById(R.id.config_remote_port);
        uiReometePortValue = findViewById(R.id.config_remote_port_value);

        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());

        // 远程端口
        uiRemotePort.setOnClickListener(v -> showPopInput());
    }

    private void showPopInput() {
        PopupWindow popupWindow = new PopupWindow();
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.proxy_pop_input, null, false);
        popupWindow.setContentView(view);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(auto.base.R.style.anim_pop_common);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);

//        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        WindowUnit.setBackgroundAlpha(this, 0.5f);
        popupWindow.showAtLocation(this.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }
}