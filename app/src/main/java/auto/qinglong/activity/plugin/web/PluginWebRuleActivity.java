package auto.qinglong.activity.plugin.web;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.PopupWindowManager;

public class PluginWebRuleActivity extends BaseActivity {
    public static final String TAG = "PluginWebRuleActivity";

    private ImageView ui_bar_back;
    private ImageView ui_bar_add;
    private RecyclerView ui_rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_web_rule);

        ui_bar_back = findViewById(R.id.common_bar_back);
        ui_bar_add = findViewById(R.id.bar_add);
        ui_rv_list = findViewById(R.id.plugin_web_rule_recycler);

        init();
    }

    @Override
    protected void init() {
        ui_bar_back.setOnClickListener(v -> finish());

        ui_bar_add.setOnClickListener(v -> {
            EditWindow editWindow = new EditWindow("新建规则", "取消", "确定");
            editWindow.addItem(new EditWindowItem("name", null, "名称", "", true, true));
            editWindow.addItem(new EditWindowItem("url", null, "网址", "", true, true));
            editWindow.addItem(new EditWindowItem("name", null, "目标键", "选填", true, true));
            editWindow.addItem(new EditWindowItem("name", null, "主键", "", true, true));
            editWindow.setActionListener(new EditWindow.OnActionListener() {
                @Override
                public boolean onConfirm(Map<String, String> map) {
                    return false;
                }

                @Override
                public boolean onCancel() {
                    return true;
                }
            });
            PopupWindowManager.buildEditWindow(this, editWindow);
        });
    }
}