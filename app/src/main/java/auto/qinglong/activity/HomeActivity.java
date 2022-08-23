package auto.qinglong.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import auto.qinglong.R;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.config.ConfigFragment;
import auto.qinglong.fragment.env.EnvFragment;
import auto.qinglong.fragment.log.LogFragment;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.fragment.script.ScriptFragment;
import auto.qinglong.fragment.task.TaskFragment;
import auto.qinglong.tools.NetUnit;
import auto.qinglong.tools.WindowUnit;

public class HomeActivity extends BaseActivity {
    private TaskFragment taskFragment;
    private LogFragment logFragment;
    private ConfigFragment configFragment;
    private ScriptFragment scriptFragment;
    private EnvFragment envFragment;

    private BaseFragment currentFragment;
    private String currentMenu = "";
    private PopupWindow popWindowMenu;

    private RelativeLayout layout_root;
    private View layout_popGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layout_root = findViewById(R.id.home_root);
        layout_popGuide = findViewById(R.id.home_pop_guide);
        initViewSetting();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initViewSetting();
    }

    @Override
    protected void initViewSetting() {
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
            if (popWindowMenu != null) {
                popWindowMenu.dismiss();
            }
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
                taskFragment.setMenuClickInterface(new MenuClickInterface() {
                    @Override
                    public void onMenuClick() {
                        showPopWindowMenu();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, taskFragment, TaskFragment.TAG).commit();
            }
            currentFragment = taskFragment;
        } else if (menu.equals(LogFragment.TAG)) {
            if (logFragment == null) {
                logFragment = new LogFragment();
                logFragment.setMenuClickInterface(new MenuClickInterface() {
                    @Override
                    public void onMenuClick() {
                        showPopWindowMenu();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, logFragment, LogFragment.TAG).commit();
            }
            currentFragment = logFragment;
        } else if (menu.equals(ConfigFragment.TAG)) {
            if (configFragment == null) {
                configFragment = new ConfigFragment();
                configFragment.setMenuClickInterface(new MenuClickInterface() {
                    @Override
                    public void onMenuClick() {
                        showPopWindowMenu();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, configFragment, ConfigFragment.TAG).commit();
            }
            currentFragment = configFragment;
        } else if (menu.equals(ScriptFragment.TAG)) {
            if (scriptFragment == null) {
                scriptFragment = new ScriptFragment();
                scriptFragment.setMenuClickInterface(new MenuClickInterface() {
                    @Override
                    public void onMenuClick() {
                        showPopWindowMenu();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, scriptFragment, ScriptFragment.TAG).commit();
            }
            currentFragment = scriptFragment;
        } else if (menu.equals(EnvFragment.TAG)) {
            if (envFragment == null) {
                envFragment = new EnvFragment();
                envFragment.setMenuClickInterface(new MenuClickInterface() {
                    @Override
                    public void onMenuClick() {
                        showPopWindowMenu();
                    }
                });
                getSupportFragmentManager().beginTransaction().add(R.id.home_fragment_layout, envFragment, EnvFragment.TAG).commit();
            }
            currentFragment = envFragment;
        }

        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        //关闭导航栏
        if (popWindowMenu != null) {
            popWindowMenu.dismiss();
        }

    }


    @SuppressLint("SetTextI18n")
    private void showPopWindowMenu() {
        if (popWindowMenu == null) {
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.pop_home_nav, null);
            popWindowMenu = new PopupWindow(getBaseContext());
            popWindowMenu.setContentView(view);
            popWindowMenu.setOutsideTouchable(true);
            popWindowMenu.setFocusable(true);
            popWindowMenu.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            popWindowMenu.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popWindowMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popWindowMenu.setAnimationStyle(R.style.anim_activity_home_pop_menu);
            popWindowMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //取消半透明效果
                    layout_root.setForeground(null);
                }
            });

            //用户名和地址
            TextView layout_username = view.findViewById(R.id.menu_info_username);
            TextView layout_address = view.findViewById(R.id.menu_info_address);
            layout_username.setText(AccountSP.getCurrentAccount().getUsername());
            layout_address.setText(AccountSP.getCurrentAccount().getAddress());
            String ip = NetUnit.getIP();
            if (ip != null) {
                TextView layout_ip = view.findViewById(R.id.menu_info_inner_ip);
                layout_ip.setText("本地：" + ip);
                layout_ip.setVisibility(View.VISIBLE);
            }
            //导航监听
            LinearLayout menu_task = view.findViewById(R.id.menu_task);
            LinearLayout menu_log = view.findViewById(R.id.menu_log);
            LinearLayout menu_config = view.findViewById(R.id.menu_config);
            LinearLayout menu_script = view.findViewById(R.id.menu_script);
            LinearLayout menu_env = view.findViewById(R.id.menu_env);
            LinearLayout menu_account = view.findViewById(R.id.menu_app_account);

            menu_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragment(TaskFragment.TAG);
                }
            });

            menu_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragment(LogFragment.TAG);
                }
            });

            menu_config.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragment(ConfigFragment.TAG);
                }
            });

            menu_script.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragment(ScriptFragment.TAG);
                }
            });

            menu_env.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragment(EnvFragment.TAG);
                }
            });

            menu_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), AccountActivity.class);
                    startActivity(intent);
                }
            });
        }
        popWindowMenu.showAsDropDown(layout_popGuide, 0, 0);
        //显示半透明效果
        layout_root.setForeground(new ColorDrawable(0x66000000));
    }

    @Override
    protected void initWindow() {
        WindowUnit.setStatusBarTextColor(this, false);
        WindowUnit.setTranslucentStatus(this);
    }

    @Override
    public void onBackPressed() {
        if (!currentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}