package auto.qinglong.activity.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import auto.qinglong.R;
import auto.qinglong.activity.app.login.LoginActivity;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.module.config.ConfigFragment;
import auto.qinglong.activity.module.dependence.DepFragment;
import auto.qinglong.activity.module.environment.EnvFragment;
import auto.qinglong.activity.module.log.LogFragment;
import auto.qinglong.activity.module.script.ScriptFragment;
import auto.qinglong.activity.module.setting.SettingFragment;
import auto.qinglong.activity.module.task.TaskFragment;
import auto.qinglong.tools.NetUnit;
import auto.qinglong.tools.ToastUnit;

public class HomeActivity extends BaseActivity {
    private long lastBackPressed = 0;//上次返回按下时间

    private TaskFragment taskFragment;
    private LogFragment logFragment;
    private ConfigFragment configFragment;
    private ScriptFragment scriptFragment;
    private EnvFragment envFragment;
    private DepFragment depFragment;
    private SettingFragment settingFragment;

    private BaseFragment currentFragment;
    private String currentMenu = "";
    private BaseFragment.MenuClickListener menuClickListener;

    //布局变量
    private DrawerLayout layout_drawer;
    private LinearLayout layout_drawer_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layout_drawer = findViewById(R.id.drawer_layout);
        layout_drawer_left = findViewById(R.id.drawer_left);
        init();
    }

    /**
     * 初始化函数，进行变量和控件的初始化
     */
    @Override
    protected void init() {
        //变量初始化
        menuClickListener = () -> layout_drawer.openDrawer(layout_drawer_left);

        //导航栏初始化
        initDrawerBar();

        //初始化第一帧页面
        showFragment(TaskFragment.TAG);
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
            if (taskFragment == null) {
                taskFragment = new TaskFragment();
                taskFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, taskFragment, TaskFragment.TAG).commit();
            }
            currentFragment = taskFragment;
        } else if (menu.equals(LogFragment.TAG)) {
            if (logFragment == null) {
                logFragment = new LogFragment();
                logFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, logFragment, LogFragment.TAG).commit();
            }
            currentFragment = logFragment;
        } else if (menu.equals(ConfigFragment.TAG)) {
            if (configFragment == null) {
                configFragment = new ConfigFragment();
                configFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, configFragment, ConfigFragment.TAG).commit();
            }
            currentFragment = configFragment;
        } else if (menu.equals(ScriptFragment.TAG)) {
            if (scriptFragment == null) {
                scriptFragment = new ScriptFragment();
                scriptFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, scriptFragment, ScriptFragment.TAG).commit();
            }
            currentFragment = scriptFragment;
        } else if (menu.equals(EnvFragment.TAG)) {
            if (envFragment == null) {
                envFragment = new EnvFragment();
                envFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, envFragment, EnvFragment.TAG).commit();
            }
            currentFragment = envFragment;
        } else if (menu.equals(DepFragment.TAG)) {
            if (depFragment == null) {
                depFragment = new DepFragment();
                depFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, depFragment, EnvFragment.TAG).commit();
            }
            currentFragment = depFragment;
        } else if (menu.equals(SettingFragment.TAG)) {
            if (settingFragment == null) {
                settingFragment = new SettingFragment();
                settingFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, settingFragment, EnvFragment.TAG).commit();
            }
            currentFragment = settingFragment;
        }

        //隐藏旧页面
        if (old != null) {
            getSupportFragmentManager().beginTransaction().hide(old).commit();
        }
        //显示新页面
        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        //关闭导航栏
        if (layout_drawer.isDrawerOpen(layout_drawer_left)) {
            layout_drawer.closeDrawer(layout_drawer_left);
        }
    }

    /**
     * 初始化导航栏
     */
    @SuppressLint("SetTextI18n")
    private void initDrawerBar() {
        layout_drawer_left.setVisibility(View.INVISIBLE);
        //左侧导航栏
        //用户名和地址
        TextView layout_username = layout_drawer_left.findViewById(R.id.menu_info_username);
        TextView layout_address = layout_drawer_left.findViewById(R.id.menu_info_address);
        layout_username.setText(AccountSP.getCurrentAccount().getUsername());
        layout_address.setText(AccountSP.getCurrentAccount().getAddress());
        String ip = NetUnit.getIP();
        if (ip != null) {
            TextView layout_ip = layout_drawer_left.findViewById(R.id.menu_info_inner_ip);
            layout_ip.setText("本地：" + ip);
            layout_ip.setVisibility(View.VISIBLE);
        }

        //导航监听
        LinearLayout menu_task = layout_drawer_left.findViewById(R.id.menu_task);
        LinearLayout menu_log = layout_drawer_left.findViewById(R.id.menu_log);
        LinearLayout menu_config = layout_drawer_left.findViewById(R.id.menu_config);
        LinearLayout menu_script = layout_drawer_left.findViewById(R.id.menu_script);
        LinearLayout menu_env = layout_drawer_left.findViewById(R.id.menu_env);
        LinearLayout menu_setting = layout_drawer_left.findViewById(R.id.menu_setting);
        LinearLayout menu_dep = layout_drawer_left.findViewById(R.id.menu_dep);
        LinearLayout menu_app_exit = layout_drawer_left.findViewById(R.id.menu_app_exit);
        LinearLayout menu_app_setting = layout_drawer_left.findViewById(R.id.menu_app_setting);

        menu_task.setOnClickListener(v -> showFragment(TaskFragment.TAG));

        menu_log.setOnClickListener(v -> showFragment(LogFragment.TAG));

        menu_config.setOnClickListener(v -> showFragment(ConfigFragment.TAG));

        menu_script.setOnClickListener(v -> showFragment(ScriptFragment.TAG));

        menu_env.setOnClickListener(v -> showFragment(EnvFragment.TAG));

        menu_dep.setOnClickListener(v -> showFragment(DepFragment.TAG));

        menu_setting.setOnClickListener(v -> {
            ToastUnit.showShort("敬请期待");
        });

        menu_app_exit.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        });

        menu_app_setting.setOnClickListener(v -> {
            ToastUnit.showShort("敬请期待");
        });


    }


}