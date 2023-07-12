package auto.qinglong.ui.activity.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.view.popup.PopConfirmWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.app.Version;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.database.sp.SettingPreference;
import auto.qinglong.net.app.ApiController;
import auto.qinglong.ui.BaseActivity;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.panel.CodeWebActivity;
import auto.qinglong.ui.activity.panel.dependence.DepPagerFragment;
import auto.qinglong.ui.activity.panel.environment.EnvFragment;
import auto.qinglong.ui.activity.panel.log.LogFragment;
import auto.qinglong.ui.activity.panel.script.ScriptFragment;
import auto.qinglong.ui.activity.panel.setting.SettingFragment;
import auto.qinglong.ui.activity.panel.task.TaskFragment;
import auto.qinglong.utils.EncryptUtil;
import auto.qinglong.utils.WebUnit;

public class HomeActivity extends BaseActivity {
    public static final String TAG = "HomeActivity";

    private long mLastBackPressedTime = 0;//上次按下返回键时间
    private BaseFragment.MenuClickListener mMenuClickListener;
    private Map<String, BaseFragment> fragmentMap;
    private BaseFragment mCurrentFragment;//当前Fragment

    private DrawerLayout uiDrawer;
    private LinearLayout uiDrawerLeft;
    private PopupWindow uiPopNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        uiDrawer = findViewById(R.id.drawer_layout);
        uiDrawerLeft = findViewById(R.id.drawer_left);

        fragmentMap = new HashMap<>();

        //清空Fragment
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        init();
    }

    @Override
    protected void init() {
        //变量初始化
        mMenuClickListener = () -> uiDrawer.openDrawer(uiDrawerLeft);
        //导航栏初始化
        initDrawerBar();
        //初始化第一帧页面
        showFragment(TaskFragment.class);
        //版本检查
        netGetVersion();
    }

    @Override
    public void onBackPressed() {
        if (!mCurrentFragment.onDispatchBackKey()) {
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
        if (uiPopNotice != null && uiPopNotice.isShowing()) {
            return false;
        }
        //询问当前帧是否阻止点击
        if (mCurrentFragment != null && mCurrentFragment.onDispatchTouchEvent()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initDrawerBar() {
        uiDrawerLeft.setVisibility(View.INVISIBLE);
        //用户名
        TextView ui_username = uiDrawerLeft.findViewById(R.id.menu_top_info_username);
        ui_username.setText(Objects.requireNonNull(PanelPreference.getCurrentAccount()).getUsername());
        //面板地址
        TextView ui_address = uiDrawerLeft.findViewById(R.id.menu_top_info_address);
        ui_address.setText(PanelPreference.getCurrentAccount().getAddress());
        //面板版本
        TextView ui_version = uiDrawerLeft.findViewById(R.id.menu_top_info_version);
        ui_version.setText(String.format(getString(R.string.format_tip_version), PanelPreference.getVersion()));

        //导航监听
        LinearLayout menu_task = uiDrawerLeft.findViewById(R.id.menu_task);
        LinearLayout menu_log = uiDrawerLeft.findViewById(R.id.menu_log);
        LinearLayout menu_config = uiDrawerLeft.findViewById(R.id.menu_config);
        LinearLayout menu_script = uiDrawerLeft.findViewById(R.id.menu_script);
        LinearLayout menu_env = uiDrawerLeft.findViewById(R.id.menu_env);
        LinearLayout menu_setting = uiDrawerLeft.findViewById(R.id.menu_setting);
        LinearLayout menu_dep = uiDrawerLeft.findViewById(R.id.menu_dep);
        LinearLayout menu_app_exit = uiDrawerLeft.findViewById(R.id.menu_exit);
        LinearLayout menu_app_setting = uiDrawerLeft.findViewById(R.id.menu_app_setting);

        //定时任务
        menu_task.setOnClickListener(v -> showFragment(TaskFragment.class));
        //任务日志
        menu_log.setOnClickListener(v -> showFragment(LogFragment.class));
        //配置文件
        menu_config.setOnClickListener(v -> {
            Intent intent = new Intent(this, CodeWebActivity.class);
            intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_CONFIG);
            intent.putExtra(CodeWebActivity.EXTRA_TITLE, "config.sh");
            intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
            startActivity(intent);
        });
        //脚本管理
        menu_script.setOnClickListener(v -> showFragment(ScriptFragment.class));
        //依赖管理
        menu_env.setOnClickListener(v -> showFragment(EnvFragment.class));
        //任务日志
        menu_dep.setOnClickListener(v -> showFragment(DepPagerFragment.class));
        //系统设置
        menu_setting.setOnClickListener(v -> showFragment(SettingFragment.class));

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

    private void showFragment(Class<?> cls) {
        String name = cls.getName();
        //获取指定帧
        BaseFragment targetFragment = fragmentMap.get(name);
        //相同帧则返回
        if (mCurrentFragment != null && mCurrentFragment.equals(targetFragment)) {
            return;
        }
        //不存在则创建
        if (targetFragment == null) {
            try {
                targetFragment = (BaseFragment) cls.newInstance();
                targetFragment.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, targetFragment, name).commit();
                fragmentMap.put(name, targetFragment);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return;
            }
        }
        //隐藏旧页面
        if (mCurrentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
        }
        //替换
        mCurrentFragment = targetFragment;
        //显示新页面
        getSupportFragmentManager().beginTransaction().show(mCurrentFragment).commit();
        //关闭导航栏
        if (uiDrawer.isDrawerOpen(uiDrawerLeft)) {
            uiDrawer.closeDrawer(uiDrawerLeft);
        }
    }

    private void checkVersion(Version version) {
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            //若版本强制更新 即使停用更新推送仍会要求更新
            if (version.getVersionCode() > versionCode && (version.isForce() || SettingPreference.isNotify())) {
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
        uiPopNotice = PopupWindowBuilder.buildConfirmWindow(this, popConfirmWindow);
    }

    private void netGetVersion() {
        ApiController.getProject(getNetRequestID());
        String uid = EncryptUtil.md5(PanelPreference.getAddress());
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