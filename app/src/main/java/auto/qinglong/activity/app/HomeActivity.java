package auto.qinglong.activity.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Objects;

import auto.base.util.WindowUnit;
import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.extension.web.PluginWebActivity;
import auto.qinglong.activity.ql.CodeWebActivity;
import auto.qinglong.activity.ql.dependence.DepPagerFragment;
import auto.qinglong.activity.ql.environment.EnvFragment;
import auto.qinglong.activity.ql.log.LogFragment;
import auto.qinglong.activity.ql.script.ScriptFragment;
import auto.qinglong.activity.ql.setting.SettingFragment;
import auto.qinglong.activity.ql.task.TaskFragment;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.ql.QLSystem;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.database.sp.SettingSP;
import auto.qinglong.network.http.ApiController;
import auto.qinglong.utils.EncryptUtil;
import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.qinglong.utils.WebUnit;
import auto.base.view.popup.PopConfirmWindow;
import auto.base.view.popup.PopupWindowBuilder;

public class HomeActivity extends BaseActivity {
    public static final String TAG = "HomeActivity";

    private long mLastBackPressedTime = 0;//上次按下返回键时间
    private BaseFragment mCurrentFragment;//当前帧
    private String mCurrentMenu;
    private BaseFragment.MenuClickListener mMenuClickListener;
    // 碎片界面列表
    private TaskFragment fg_task;
    private LogFragment fg_log;
    private ScriptFragment fg_script;
    private EnvFragment fg_environment;
    private DepPagerFragment fg_dependence;
    private SettingFragment fg_setting;
    //布局变量
    private DrawerLayout ui_drawer;
    private LinearLayout ui_drawer_left;
    private PopupWindow ui_pop_notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ui_drawer = findViewById(R.id.drawer_layout);
        ui_drawer_left = findViewById(R.id.drawer_left);

        init();
    }

    @Override
    protected void init() {
        //变量初始化
        mMenuClickListener = () -> ui_drawer.openDrawer(ui_drawer_left);
        //导航栏初始化
        initDrawerBar();
        //初始化第一帧页面
        showFragment(TaskFragment.TAG);
        //版本检查
        netGetVersion();
    }

    @Override
    public void onBackPressed() {
        if (!mCurrentFragment.onBackPressed()) {
            long current = System.currentTimeMillis();
            if (current - mLastBackPressedTime < 2000) {
                finish();
            } else {
                mLastBackPressedTime = current;
                ToastUnit.showShort(getString(R.string.tip_exit_app));
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //进度pop存在阻止点击
        if (ui_pop_notice != null && ui_pop_notice.isShowing()) {
            return false;
        }
        //询问当前帧是否阻止点击
        if (mCurrentFragment != null && mCurrentFragment.onDispatchTouchEvent()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }


    private void initDrawerBar() {
        ui_drawer_left.setVisibility(View.INVISIBLE);
        //用户名
        TextView ui_username = ui_drawer_left.findViewById(R.id.menu_top_info_username);
        ui_username.setText(Objects.requireNonNull(AccountSP.getCurrentAccount()).getUsername());
        //面板地址
        TextView ui_address = ui_drawer_left.findViewById(R.id.menu_top_info_address);
        ui_address.setText(AccountSP.getCurrentAccount().getAddress());
        //面板版本
        TextView ui_version = ui_drawer_left.findViewById(R.id.menu_top_info_version);
        ui_version.setText(String.format(getString(R.string.format_tip_version), QLSystem.getStaticVersion()));

        //导航监听
        LinearLayout menu_task = ui_drawer_left.findViewById(R.id.menu_task);
        LinearLayout menu_log = ui_drawer_left.findViewById(R.id.menu_log);
        LinearLayout menu_config = ui_drawer_left.findViewById(R.id.menu_config);
        LinearLayout menu_script = ui_drawer_left.findViewById(R.id.menu_script);
        LinearLayout menu_env = ui_drawer_left.findViewById(R.id.menu_env);
        LinearLayout menu_setting = ui_drawer_left.findViewById(R.id.menu_setting);
        LinearLayout menu_dep = ui_drawer_left.findViewById(R.id.menu_dep);
        LinearLayout menu_extension_web = ui_drawer_left.findViewById(R.id.menu_extension_web);
        LinearLayout menu_app_exit = ui_drawer_left.findViewById(R.id.menu_exit);
        LinearLayout menu_app_setting = ui_drawer_left.findViewById(R.id.menu_app_setting);

        //定时任务
        menu_task.setOnClickListener(v -> showFragment(TaskFragment.TAG));
        //任务日志
        menu_log.setOnClickListener(v -> showFragment(LogFragment.TAG));
        //配置文件
        menu_config.setOnClickListener(v -> {
            Intent intent = new Intent(this, CodeWebActivity.class);
            intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_CONFIG);
            intent.putExtra(CodeWebActivity.EXTRA_TITLE, "config.sh");
            intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
            startActivity(intent);
        });
        //脚本管理
        menu_script.setOnClickListener(v -> showFragment(ScriptFragment.TAG));
        //依赖管理
        menu_env.setOnClickListener(v -> showFragment(EnvFragment.TAG));
        //任务日志
        menu_dep.setOnClickListener(v -> showFragment(DepPagerFragment.TAG));
        //系统设置
        menu_setting.setOnClickListener(v -> showFragment(SettingFragment.TAG));

        //Web助手
        menu_extension_web.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PluginWebActivity.class);
            startActivity(intent);
        });

        //退出登录
        menu_app_exit.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        });
        //APP设置
        menu_app_setting.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SettingActivity.class);
            startActivity(intent);
        });
    }

    private void showFragment(String menu) {
        //点击当前界面导航则直接返回
        if (menu.equals(mCurrentMenu)) {
            return;
        } else {
            mCurrentMenu = menu;
        }
        //记录之前帧
        BaseFragment old = mCurrentFragment;

        if (menu.equals(TaskFragment.TAG)) {
            if (fg_task == null) {
                fg_task = new TaskFragment();
                fg_task.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_task, TaskFragment.TAG).commit();
            }
            mCurrentFragment = fg_task;
        } else if (menu.equals(LogFragment.TAG)) {
            if (fg_log == null) {
                fg_log = new LogFragment();
                fg_log.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_log, LogFragment.TAG).commit();
            }
            mCurrentFragment = fg_log;
        } else if (menu.equals(ScriptFragment.TAG)) {
            if (fg_script == null) {
                fg_script = new ScriptFragment();
                fg_script.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_script, ScriptFragment.TAG).commit();
            }
            mCurrentFragment = fg_script;
        } else if (menu.equals(EnvFragment.TAG)) {
            if (fg_environment == null) {
                fg_environment = new EnvFragment();
                fg_environment.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_environment, EnvFragment.TAG).commit();
            }
            mCurrentFragment = fg_environment;
        } else if (menu.equals(DepPagerFragment.TAG)) {
            if (fg_dependence == null) {
                fg_dependence = new DepPagerFragment();
                fg_dependence.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_dependence, EnvFragment.TAG).commit();
            }
            mCurrentFragment = fg_dependence;
        } else if (menu.equals(SettingFragment.TAG)) {
            if (fg_setting == null) {
                fg_setting = new SettingFragment();
                fg_setting.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_setting, EnvFragment.TAG).commit();
            }
            mCurrentFragment = fg_setting;
        }

        //隐藏旧页面
        if (old != null) {
            getSupportFragmentManager().beginTransaction().hide(old).commit();
        }
        //显示新页面
        getSupportFragmentManager().beginTransaction().show(mCurrentFragment).commit();
        //关闭导航栏
        if (ui_drawer.isDrawerOpen(ui_drawer_left)) {
            ui_drawer.closeDrawer(ui_drawer_left);
        }
    }

    private void checkVersion(Version version){
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            //若版本强制更新 即使停用更新推送仍会要求更新
            if (version.getVersionCode() > versionCode && (version.isForce() || SettingSP.isNotify())) {
                showVersionNotice(version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showVersionNotice(Version version) {
        String content = "最新版本：" + version.getVersionName() + "\n\n";
        content += "更新时间：" + version.getUpdateTime() + "\n\n";
        content += TextUnit.join(version.getUpdateDetail(), "\n\n");

        PopConfirmWindow popConfirmWindow = new PopConfirmWindow("版本更新", content, "取消", "更新");
        popConfirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix(getBaseContext()) / 3);
        popConfirmWindow.setFocusable(false);
        popConfirmWindow.setOnActionListener(isConfirm -> {
            if (isConfirm) {
                WebUnit.open(this, version.getDownloadUrl());
                return !version.isForce();
            } else {
                if (version.isForce()) {
                    finish();
                }
                return true;
            }
        });
        ui_pop_notice = PopupWindowBuilder.buildConfirmWindow(this, popConfirmWindow);
    }

    private void netGetVersion() {
        ApiController.getProject(getNetRequestID());
        String uid = EncryptUtil.md5(AccountSP.getAddress());
        ApiController.getVersion(getNetRequestID(), uid, new ApiController.VersionCallback() {
            @Override
            public void onSuccess(Version version) {
                checkVersion(version);
            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(TAG, msg);
            }
        });
    }
}