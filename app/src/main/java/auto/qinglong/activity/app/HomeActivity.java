package auto.qinglong.activity.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import auto.qinglong.R;
import auto.qinglong.activity.plugin.web.PluginWebActivity;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.config.ConfigFragment;
import auto.qinglong.activity.ql.dependence.DepFragment;
import auto.qinglong.activity.ql.environment.EnvFragment;
import auto.qinglong.activity.ql.log.LogFragment;
import auto.qinglong.activity.ql.script.ScriptFragment;
import auto.qinglong.activity.ql.setting.SettingFragment;
import auto.qinglong.activity.ql.task.TaskFragment;
import auto.qinglong.utils.NetUnit;
import auto.qinglong.utils.ToastUnit;

public class HomeActivity extends BaseActivity {
    private long lastBackPressed = 0;//上次返回按下时间戳
    private BaseFragment currentFragment;//当前碎片
    private String currentMenu = "";//当前菜单名称
    private BaseFragment.MenuClickListener menuClickListener;
    // 碎片界面列表
    private TaskFragment fg_task;
    private LogFragment fg_log;
    private ConfigFragment fg_config;
    private ScriptFragment fg_script;
    private EnvFragment fg_environment;
    private DepFragment fg_dependence;
    private SettingFragment fg_setting;
    //布局变量
    private DrawerLayout ui_drawer;
    private LinearLayout ui_drawer_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ui_drawer = findViewById(R.id.drawer_layout);
        ui_drawer_left = findViewById(R.id.drawer_left);

        init();
    }

    @Override
    public void onBackPressed() {
        if (!currentFragment.onBackPressed()) {
            long current = System.currentTimeMillis();
            if (current - lastBackPressed < 2000) {
                finish();
            } else {
                lastBackPressed = current;
                ToastUnit.showShort("再按一次退出");
            }
        }
    }

    /**
     * 初始化函数，进行变量和控件的初始化
     */
    @Override
    protected void init() {
        //变量初始化
        menuClickListener = () -> ui_drawer.openDrawer(ui_drawer_left);

        //导航栏初始化
        initDrawerBar();

        //初始化第一帧页面
        showFragment(TaskFragment.TAG);
    }

    /**
     * 显示帧布局 设置监听
     *
     * @param menu 帧标签
     */
    private void showFragment(String menu) {
        //点击当前界面导航则直接返回
        if (menu.equals(currentMenu)) {
            return;
        } else {
            currentMenu = menu;
        }
        //记录之前帧
        BaseFragment old = currentFragment;

        if (menu.equals(TaskFragment.TAG)) {
            if (fg_task == null) {
                fg_task = new TaskFragment();
                fg_task.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_task, TaskFragment.TAG).commit();
            }
            currentFragment = fg_task;
        } else if (menu.equals(LogFragment.TAG)) {
            if (fg_log == null) {
                fg_log = new LogFragment();
                fg_log.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_log, LogFragment.TAG).commit();
            }
            currentFragment = fg_log;
        } else if (menu.equals(ConfigFragment.TAG)) {
            if (fg_config == null) {
                fg_config = new ConfigFragment();
                fg_config.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_config, ConfigFragment.TAG).commit();
            }
            currentFragment = fg_config;
        } else if (menu.equals(ScriptFragment.TAG)) {
            if (fg_script == null) {
                fg_script = new ScriptFragment();
                fg_script.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_script, ScriptFragment.TAG).commit();
            }
            currentFragment = fg_script;
        } else if (menu.equals(EnvFragment.TAG)) {
            if (fg_environment == null) {
                fg_environment = new EnvFragment();
                fg_environment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_environment, EnvFragment.TAG).commit();
            }
            currentFragment = fg_environment;
        } else if (menu.equals(DepFragment.TAG)) {
            if (fg_dependence == null) {
                fg_dependence = new DepFragment();
                fg_dependence.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_dependence, EnvFragment.TAG).commit();
            }
            currentFragment = fg_dependence;
        } else if (menu.equals(SettingFragment.TAG)) {
            if (fg_setting == null) {
                fg_setting = new SettingFragment();
                fg_setting.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_setting, EnvFragment.TAG).commit();
            }
            currentFragment = fg_setting;
        }

        //隐藏旧页面
        if (old != null) {
            getSupportFragmentManager().beginTransaction().hide(old).commit();
        }
        //显示新页面
        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        //关闭导航栏
        if (ui_drawer.isDrawerOpen(ui_drawer_left)) {
            ui_drawer.closeDrawer(ui_drawer_left);
        }
    }

    /**
     * 初始化导航栏
     */
//    @SuppressLint("SetTextI18n")
    private void initDrawerBar() {
        ui_drawer_left.setVisibility(View.INVISIBLE);
        //用户信息
        TextView layout_username = ui_drawer_left.findViewById(R.id.menu_top_info_username);
        TextView layout_address = ui_drawer_left.findViewById(R.id.menu_top_info_address);
        layout_username.setText(AccountSP.getCurrentAccount().getUsername());
        layout_address.setText(AccountSP.getCurrentAccount().getAddress());
        String ip = NetUnit.getIP();
        if (ip != null) {
            TextView layout_ip = ui_drawer_left.findViewById(R.id.menu_top_info_inner_ip);
            layout_ip.setText("本地：" + ip);
            layout_ip.setVisibility(View.VISIBLE);
        }

        //导航监听
        LinearLayout menu_task = ui_drawer_left.findViewById(R.id.menu_task);
        LinearLayout menu_log = ui_drawer_left.findViewById(R.id.menu_log);
        LinearLayout menu_config = ui_drawer_left.findViewById(R.id.menu_config);
        LinearLayout menu_script = ui_drawer_left.findViewById(R.id.menu_script);
        LinearLayout menu_env = ui_drawer_left.findViewById(R.id.menu_env);
        LinearLayout menu_setting = ui_drawer_left.findViewById(R.id.menu_setting);
        LinearLayout menu_dep = ui_drawer_left.findViewById(R.id.menu_dep);
        LinearLayout menu_exit = ui_drawer_left.findViewById(R.id.menu_exit);
        LinearLayout menu_extension_webck = ui_drawer_left.findViewById(R.id.menu_extension_webck);
        LinearLayout menu_app_setting = ui_drawer_left.findViewById(R.id.menu_app_setting);

        menu_task.setOnClickListener(v -> showFragment(TaskFragment.TAG));

        menu_log.setOnClickListener(v -> showFragment(LogFragment.TAG));

        menu_config.setOnClickListener(v -> showFragment(ConfigFragment.TAG));

        menu_script.setOnClickListener(v -> showFragment(ScriptFragment.TAG));

        menu_env.setOnClickListener(v -> showFragment(EnvFragment.TAG));

        menu_dep.setOnClickListener(v -> showFragment(DepFragment.TAG));

        menu_setting.setOnClickListener(v -> {
            ToastUnit.showShort("暂未开放");
        });

        menu_exit.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        });

        menu_extension_webck.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PluginWebActivity.class);
            startActivity(intent);
        });

        menu_app_setting.setOnClickListener(v -> {
            ToastUnit.showShort("暂未开放");
        });


    }


}