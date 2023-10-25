package auto.panel.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
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

import auto.base.ui.popup.ConfirmPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.util.EncryptUtil;
import auto.base.util.LogUnit;
import auto.base.util.NetUnit;
import auto.base.util.PackageUtil;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WebUnit;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.bean.app.Extension;
import auto.panel.bean.app.Extensions;
import auto.panel.bean.app.Version;
import auto.panel.database.sp.PanelPreference;
import auto.panel.database.sp.SettingPreference;
import auto.panel.net.app.ApiController;
import auto.panel.ui.fragment.BaseFragment;
import auto.panel.ui.fragment.PanelDependencePagerFragment;
import auto.panel.ui.fragment.PanelEnvironmentFragment;
import auto.panel.ui.fragment.PanelLogFragment;
import auto.panel.ui.fragment.PanelScriptFragment;
import auto.panel.ui.fragment.PanelSettingFragment;
import auto.panel.ui.fragment.PanelTaskFragment;

public class HomeActivity extends BaseActivity {
    public static final String TAG = "HomeActivity";

    private boolean initCheckVersion = false;
    private long lastBackPressedTime = 0;//上次按下返回键时间
    private BaseFragment.MenuClickListener menuClickListener;
    private Map<String, BaseFragment> fragmentMap;
    private BaseFragment currentFragment;//当前Fragment

    private DrawerLayout uiDrawer;
    private LinearLayout uiDrawerLeft;
    private PopupWindow uiPopNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_home);

        uiDrawer = findViewById(R.id.drawer_layout);
        uiDrawerLeft = findViewById(R.id.drawer_left);

        fragmentMap = new HashMap<>();

        //清空Fragment
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!initCheckVersion) {
            //版本检查
            netGetVersion();
        }
    }

    @Override
    public void onBackPressed() {
        if (!currentFragment.onDispatchBackKey()) {
            long current = System.currentTimeMillis();
            if (current - lastBackPressedTime < 2000) {
                finish();
            } else {
                lastBackPressedTime = current;
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
        if (currentFragment != null && currentFragment.onDispatchTouchEvent()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void init() {
        //变量初始化
        menuClickListener = () -> uiDrawer.openDrawer(uiDrawerLeft);
        //导航栏初始化
        initDrawerBar();
        //初始化第一帧页面
        showFragment(PanelTaskFragment.class);
        //获取拓展
        //getExtension();
    }

    private void initDrawerBar() {
        uiDrawerLeft.setVisibility(View.INVISIBLE);
        //用户名
        TextView uiUsername = uiDrawerLeft.findViewById(R.id.menu_top_info_username);
        uiUsername.setText(Objects.requireNonNull(PanelPreference.getCurrentAccount()).getUsername());
        //面板地址
        TextView uiAddress = uiDrawerLeft.findViewById(R.id.menu_top_info_address);
        String address = PanelPreference.getCurrentAccount().getAddress();
        uiAddress.setText(NetUnit.getHost(address));
        //面板版本
        TextView uiVersion = uiDrawerLeft.findViewById(R.id.menu_top_info_version);
        uiVersion.setText(String.format(getString(R.string.format_tip_version), PanelPreference.getVersion()));

        //导航监听
        LinearLayout menuTask = uiDrawerLeft.findViewById(R.id.panel_menu_task);
        LinearLayout menuLog = uiDrawerLeft.findViewById(R.id.panel_menu_log);
        LinearLayout menuConfig = uiDrawerLeft.findViewById(R.id.panel_menu_config);
        LinearLayout menuScript = uiDrawerLeft.findViewById(R.id.panel_menu_script);
        LinearLayout menuEnvironment = uiDrawerLeft.findViewById(R.id.panel_menu_env);
        LinearLayout menuSetting = uiDrawerLeft.findViewById(R.id.panel_menu_setting);
        LinearLayout menuDependence = uiDrawerLeft.findViewById(R.id.panel_menu_dep);
        LinearLayout menuAppLogout = uiDrawerLeft.findViewById(R.id.panel_menu_app_logout);
        LinearLayout menuAppSetting = uiDrawerLeft.findViewById(R.id.panel_menu_app_setting);

        //定时任务
        menuTask.setOnClickListener(v -> showFragment(PanelTaskFragment.class));
        //任务日志
        menuLog.setOnClickListener(v -> showFragment(PanelLogFragment.class));
        //配置文件
        menuConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, CodeWebActivity.class);
            intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_CONFIG);
            intent.putExtra(CodeWebActivity.EXTRA_TITLE, "config.sh");
            intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
            startActivity(intent);
        });
        //脚本管理
        menuScript.setOnClickListener(v -> showFragment(PanelScriptFragment.class));
        //依赖管理
        menuEnvironment.setOnClickListener(v -> showFragment(PanelEnvironmentFragment.class));
        //任务日志
        menuDependence.setOnClickListener(v -> showFragment(PanelDependencePagerFragment.class));
        //系统设置
        menuSetting.setOnClickListener(v -> showFragment(PanelSettingFragment.class));

        //退出登录
        menuAppLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_alpha_out);
            finish();
        });
        //APP设置
        menuAppSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SettingActivity.class);
            startActivity(intent);
        });
    }

    private void initExtension(Extensions extensions) {
        if (extensions.getProxy() != null) {            //拓展--代理
            Extension proxy = extensions.getProxy();

            LinearLayout uiExtensionProxy = uiDrawerLeft.findViewById(R.id.panel_menu_extension_proxy);
            TextView uiExtensionProxyTitle = uiExtensionProxy.findViewById(R.id.panel_menu_extension_proxy_title);

            uiExtensionProxy.setVisibility(View.VISIBLE);
            uiExtensionProxyTitle.setText(proxy.getName());

            uiExtensionProxy.setOnClickListener(v -> {
                if (PackageUtil.isAppInstalled(mActivity, proxy.getPackageName())) {
                    Intent intent = new Intent();
                    intent.putExtra("from", "panel");
                    intent.putExtra("token", "qinglong");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName comp = new ComponentName(proxy.getPackageName(), proxy.getActivityName());
                    intent.setComponent(comp);
                    startActivity(intent);
                } else {
                    ConfirmPopupWindow popConfirmWindow = new ConfirmPopupWindow("模块缺失", "\n模块未安装，是否下载安装\n", "取消", "下载");
                    popConfirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix(getBaseContext()) / 3);
                    popConfirmWindow.setFocusable(true);
                    popConfirmWindow.setOnActionListener(() -> {
                        WebUnit.open(mActivity, proxy.getUrl());
                        return true;
                    });
                    PopupWindowBuilder.buildConfirmWindow(this, popConfirmWindow);
                }

            });

        }
    }

    private void showFragment(Class<?> cls) {
        String name = cls.getName();
        //获取指定帧
        BaseFragment targetFragment = fragmentMap.get(name);
        //相同帧则返回
        if (currentFragment != null && currentFragment.equals(targetFragment)) {
            return;
        }
        //不存在则创建
        if (targetFragment == null) {
            try {
                targetFragment = (BaseFragment) cls.newInstance();
                targetFragment.setMenuClickListener(menuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, targetFragment, name).commit();
                fragmentMap.put(name, targetFragment);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return;
            }
        }
        //隐藏旧页面
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        //替换
        currentFragment = targetFragment;
        //显示新页面
        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        //关闭导航栏
        if (uiDrawer.isDrawerOpen(uiDrawerLeft)) {
            uiDrawer.closeDrawer(uiDrawerLeft);
        }
    }

    private void showUpdateNotice(Version version, boolean force) {
        String content = "最新版本：" + version.getVersionName() + "\n\n";
        content += "更新时间：" + version.getUpdateTime() + "\n\n";
        content += TextUnit.join(version.getUpdateDetail(), "\n\n");

        ConfirmPopupWindow popConfirmWindow = new ConfirmPopupWindow("版本更新", content, "取消", "更新");
        popConfirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix(getBaseContext()) / 3);
        popConfirmWindow.setFocusable(false);
        popConfirmWindow.setOnActionListener(new ConfirmPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm() {
                WebUnit.open(mActivity, version.getDownloadUrl());
                return !force;
            }

            @Override
            public boolean onCancel() {
                if (force) {
                    finish();
                }
                return true;
            }
        });
        uiPopNotice = PopupWindowBuilder.buildConfirmWindow(mActivity, popConfirmWindow);
    }

    private void checkVersion(Version version) {
        //更新检查标志
        initCheckVersion = true;
        //获取当前版本对象
        PackageUtil.Version versionNow = PackageUtil.getVersion(mContext);
        //若版本强制更新 即使停用更新推送仍会要求更新
        if (version.getMinVersionCode() > versionNow.getVersionCode()) {
            showUpdateNotice(version, true);
        } else if (version.getVersionCode() > versionNow.getVersionCode() && SettingPreference.isNotify()) {
            showUpdateNotice(version, false);
        }
    }

    private void netGetVersion() {
        String uid = EncryptUtil.md5(PanelPreference.getAddress());

        ApiController.getVersion(uid, new ApiController.VersionCallBack() {
            @Override
            public void onSuccess(Version version) {
                SettingPreference.updateVersion(version);
                checkVersion(version);
            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(msg);
            }
        });
    }

    private void netGetExtension() {
        String uid = EncryptUtil.md5(PanelPreference.getAddress());

        ApiController.getExtensions(uid, this::initExtension);
    }
}