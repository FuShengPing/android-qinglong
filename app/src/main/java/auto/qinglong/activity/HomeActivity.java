package auto.qinglong.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import auto.qinglong.R;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.config.ConfigFragment;
import auto.qinglong.fragment.dependence.DepFragment;
import auto.qinglong.fragment.environment.EnvFragment;
import auto.qinglong.fragment.log.LogFragment;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.fragment.script.ScriptFragment;
import auto.qinglong.fragment.setting.SettingFragment;
import auto.qinglong.fragment.task.TaskFragment;
import auto.qinglong.tools.NetUnit;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;

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
    private MenuClickInterface menuClickInterface;

    private AnimatorSet animator_menu_enter;
    private AnimatorSet animator_menu_exit;

    //布局变量
    private RelativeLayout layout_menu_bar;
    private LinearLayout layout_menu_bar_left;
    private View layout_menu_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layout_menu_bar = findViewById(R.id.home_menu_bar);
        layout_menu_bar_left = layout_menu_bar.findViewById(R.id.home_menu_bar_left);
        layout_menu_bar_right = layout_menu_bar.findViewById(R.id.home_menu_bar_right);

        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }

    /**
     * 初始化函数，进行变量和控件的初始化
     */
    @Override
    protected void init() {
        //变量初始化
        menuClickInterface = new MenuClickInterface() {
            @Override
            public void onMenuClick() {
                showMenuBar();
            }
        };

        //导航栏初始化
        initMenuBar();

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

        //之前帧存在则隐藏
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }

        if (menu.equals(TaskFragment.TAG)) {
            if (taskFragment == null) {
                taskFragment = new TaskFragment();
                taskFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, taskFragment, TaskFragment.TAG).commit();
            }
            currentFragment = taskFragment;
        } else if (menu.equals(LogFragment.TAG)) {
            if (logFragment == null) {
                logFragment = new LogFragment();
                logFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, logFragment, LogFragment.TAG).commit();
            }
            currentFragment = logFragment;
        } else if (menu.equals(ConfigFragment.TAG)) {
            if (configFragment == null) {
                configFragment = new ConfigFragment();
                configFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, configFragment, ConfigFragment.TAG).commit();
            }
            currentFragment = configFragment;
        } else if (menu.equals(ScriptFragment.TAG)) {
            if (scriptFragment == null) {
                scriptFragment = new ScriptFragment();
                scriptFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, scriptFragment, ScriptFragment.TAG).commit();
            }
            currentFragment = scriptFragment;
        } else if (menu.equals(EnvFragment.TAG)) {
            if (envFragment == null) {
                envFragment = new EnvFragment();
                envFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, envFragment, EnvFragment.TAG).commit();
            }
            currentFragment = envFragment;
        } else if (menu.equals(DepFragment.TAG)) {
            if (depFragment == null) {
                depFragment = new DepFragment();
                depFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, depFragment, EnvFragment.TAG).commit();
            }
            currentFragment = depFragment;
        } else if (menu.equals(SettingFragment.TAG)) {
            if (settingFragment == null) {
                settingFragment = new SettingFragment();
                settingFragment.setMenuClickInterface(menuClickInterface);
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, settingFragment, EnvFragment.TAG).commit();
            }
            currentFragment = settingFragment;
        }

        //显示帧页面
        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        //关闭导航栏
        if (layout_menu_bar.getVisibility() == View.VISIBLE) {
            hideMenuBar();
        }
    }

    /**
     * 初始化导航栏
     */
    @SuppressLint("SetTextI18n")
    private void initMenuBar() {
        layout_menu_bar.setVisibility(View.INVISIBLE);
        //左侧导航栏
        //用户名和地址
        TextView layout_username = layout_menu_bar.findViewById(R.id.menu_info_username);
        TextView layout_address = layout_menu_bar.findViewById(R.id.menu_info_address);
        layout_username.setText(AccountSP.getCurrentAccount().getUsername());
        layout_address.setText(AccountSP.getCurrentAccount().getAddress());
        String ip = NetUnit.getIP();
        if (ip != null) {
            TextView layout_ip = layout_menu_bar.findViewById(R.id.menu_info_inner_ip);
            layout_ip.setText("本地：" + ip);
            layout_ip.setVisibility(View.VISIBLE);
        }

        //导航监听
        LinearLayout menu_task = layout_menu_bar.findViewById(R.id.menu_task);
        LinearLayout menu_log = layout_menu_bar.findViewById(R.id.menu_log);
        LinearLayout menu_config = layout_menu_bar.findViewById(R.id.menu_config);
        LinearLayout menu_script = layout_menu_bar.findViewById(R.id.menu_script);
        LinearLayout menu_env = layout_menu_bar.findViewById(R.id.menu_env);
        LinearLayout menu_setting = layout_menu_bar.findViewById(R.id.menu_setting);
        LinearLayout menu_dep = layout_menu_bar.findViewById(R.id.menu_dep);
        LinearLayout menu_app_account = layout_menu_bar.findViewById(R.id.menu_app_account);
        LinearLayout menu_app_setting = layout_menu_bar.findViewById(R.id.menu_app_setting);

        menu_task.setOnClickListener(v -> showFragment(TaskFragment.TAG));

        menu_log.setOnClickListener(v -> showFragment(LogFragment.TAG));

        menu_config.setOnClickListener(v -> showFragment(ConfigFragment.TAG));

        menu_script.setOnClickListener(v -> showFragment(ScriptFragment.TAG));

        menu_env.setOnClickListener(v -> showFragment(EnvFragment.TAG));

        menu_dep.setOnClickListener(v -> showFragment(DepFragment.TAG));

        menu_setting.setOnClickListener(v -> {
            ToastUnit.showShort("敬请期待");
            //showFragment(SettingFragment.TAG);
        });

        menu_app_account.setOnClickListener(v -> {
            ToastUnit.showShort("敬请期待");
//                Intent intent = new Intent(getBaseContext(), AccountActivity.class);
//                startActivity(intent);
        });

        menu_app_setting.setOnClickListener(v -> ToastUnit.showShort("敬请期待"));

        //右侧空白 实现点击隐藏导航栏
        View layout_right = layout_menu_bar.findViewById(R.id.home_menu_bar_right);
        layout_right.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideMenuBar();
            }
            return true;
        });
    }

    /**
     * 显示导航栏
     */
    private void showMenuBar() {
        if (animator_menu_enter == null) {
            initMenuBarAnimator();
        }

        if (animator_menu_enter.isRunning()) {
            return;
        }

        animator_menu_enter.start();
    }

    /**
     * 隐藏导航栏
     */
    private void hideMenuBar() {
        if (animator_menu_exit == null) {
            initMenuBarAnimator();
        }

        if (animator_menu_exit.isRunning()) {
            return;
        }

        animator_menu_exit.start();

    }

    /**
     * 初始化导航栏动画
     */
    private void initMenuBarAnimator() {
        //退场
        if (animator_menu_exit == null) {
            int width = layout_menu_bar_left.getWidth();
            animator_menu_exit = new AnimatorSet();
            ObjectAnimator animator_right = ObjectAnimator.ofFloat(layout_menu_bar_right, "alpha", 1f, 0f);
            ObjectAnimator animator_left = ObjectAnimator.ofFloat(layout_menu_bar_left, "translationX", 0, -width);
            animator_right.setDuration(200);
            animator_left.setDuration(200);
            animator_menu_exit.play(animator_right).with(animator_left);
            animator_menu_exit.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    layout_menu_bar.setVisibility(View.INVISIBLE);
                }
            });
        }

        //进场
        if (animator_menu_enter == null) {
            int width = layout_menu_bar_left.getWidth();
            animator_menu_enter = new AnimatorSet();
            ObjectAnimator animator_right = ObjectAnimator.ofFloat(layout_menu_bar_right, "alpha", 0f, 1f);
            ObjectAnimator animator_left = ObjectAnimator.ofFloat(layout_menu_bar_left, "translationX", -width, 0);
            animator_right.setDuration(200);
            animator_left.setDuration(200);
            animator_menu_enter.play(animator_right).with(animator_left);
            animator_menu_enter.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation, boolean isReverse) {
                    layout_menu_bar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    //恢复右部可点击
                    layout_menu_bar_right.setClickable(true);
                }
            });
        }

    }

    @Override
    protected void initWindow() {
        WindowUnit.setStatusBarTextColor(this, false);
        WindowUnit.setTranslucentStatus(this);
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
}