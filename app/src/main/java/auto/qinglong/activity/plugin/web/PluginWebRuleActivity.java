package auto.qinglong.activity.plugin.web;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.database.db.WebRuleDBHelper;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.PopupWindowManager;

public class PluginWebRuleActivity extends BaseActivity {
    public static final String TAG = "PluginWebRuleActivity";

    private RuleItemAdapter itemAdapter;

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

        itemAdapter = new RuleItemAdapter(getBaseContext());
        ui_rv_list.setAdapter(itemAdapter);
        ui_rv_list.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(ui_rv_list.getItemAnimator()).setChangeDuration(0);

        init();
    }

    @Override
    protected void init() {
        itemAdapter.setActionListener(new RuleItemAdapter.OnActionListener() {

            @Override
            public void onCheck(boolean isChecked, int position, int id) {
                WebRuleDBHelper.update(id, isChecked);
            }

            @Override
            public void onAction(View view, int position, WebRule rule) {
                WebRuleDBHelper.delete(rule.getId());
                itemAdapter.removeItem(position);
                //震动
                @SuppressLint("ServiceCast") Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100L);
            }
        });

        ui_bar_back.setOnClickListener(v -> finish());

        ui_bar_add.setOnClickListener(v -> {
            EditWindow editWindow = new EditWindow("新建规则", "取消", "确定");
            editWindow.setMaxHeight(WindowUnit.getWindowHeightPix() / 3);//限制最大高度
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_envName, null, "环境变量", "", true, true));
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_name, null, "规则名称", "", true, true));
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_url, null, "网址", "", true, true));
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_target, null, "目标键", "选填", true, true));
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_main, null, "主键", "", true, true));
            editWindow.addItem(new EditWindowItem(WebRuleDBHelper.key_joinChar, null, "连接符", "选填", true, true));
            editWindow.setActionListener(new EditWindow.OnActionListener() {
                @Override
                public boolean onConfirm(Map<String, String> map) {
                    String envName = map.get(WebRuleDBHelper.key_envName).replace(" ", "");
                    String name = map.get(WebRuleDBHelper.key_name).replace(" ", "");
                    String url = map.get(WebRuleDBHelper.key_url).replace(" ", "");
                    String target = map.get(WebRuleDBHelper.key_target).replace(" ", "");
                    String main = map.get(WebRuleDBHelper.key_main).replace(" ", "");
                    String joinChar = map.get(WebRuleDBHelper.key_joinChar).replace(" ", "");

                    //默认值
                    if (target.isEmpty()) {
                        target = "*";
                    }
                    if (joinChar.isEmpty()) {
                        joinChar = ";";
                    }

                    if (!envName.matches("\\w+")) {
                        ToastUnit.showShort("非法环境变量");
                        return false;
                    } else if (name.isEmpty()) {
                        ToastUnit.showShort("名称不能为空");
                        return false;
                    } else if (url.isEmpty()) {
                        ToastUnit.showShort("网址不能为空");
                        return false;
                    } else if (!WebRule.isTargetValid(target)) {
                        ToastUnit.showShort("非法目标键");
                        return false;
                    } else if (main.isEmpty()) {
                        ToastUnit.showShort("主键不能为空");
                        return false;
                    } else if (!joinChar.matches("[;&%#@]")) {
                        ToastUnit.showShort("非法连接符");
                        return false;
                    }

                    //插入数据库并重新查询显示
                    WebRuleDBHelper.insert(new WebRule(0, envName, name, url, target, main, joinChar, true));
                    itemAdapter.setData(WebRuleDBHelper.getAllWebRule());

                    return true;
                }

                @Override
                public boolean onCancel() {
                    return true;
                }
            });
            PopupWindowManager.buildEditWindow(this, editWindow);
        });

        itemAdapter.setData(WebRuleDBHelper.getAllWebRule());
    }

}