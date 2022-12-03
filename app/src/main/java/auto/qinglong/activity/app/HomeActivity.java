package auto.qinglong.activity.app;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.plugin.web.PluginWebActivity;
import auto.qinglong.activity.ql.config.ConfigFragment;
import auto.qinglong.activity.ql.dependence.DepFragment;
import auto.qinglong.activity.ql.environment.EnvFragment;
import auto.qinglong.activity.ql.log.LogFragment;
import auto.qinglong.activity.ql.script.ScriptFragment;
import auto.qinglong.activity.ql.setting.SettingFragment;
import auto.qinglong.activity.ql.task.TaskFragment;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.Api;
import auto.qinglong.network.http.ApiController;
import auto.qinglong.utils.DeviceUnit;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.NetUnit;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.views.popup.ConfirmWindow;
import auto.qinglong.views.popup.PopupWindowManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Tag;

public class HomeActivity extends BaseActivity {
    public static final String TAG = "HomeActivity";

    private long mLastBackPressedTime = 0;
    private BaseFragment mCurrentFragment;
    private String mCurrentMenu = "";
    private BaseFragment.MenuClickListener mMenuClickListener;
    private PopupWindow popupWindowNotice;
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
        if (!mCurrentFragment.onBackPressed()) {
            long current = System.currentTimeMillis();
            if (current - mLastBackPressedTime < 2000) {
                finish();
            } else {
                mLastBackPressedTime = current;
                ToastUnit.showShort("再按一次退出");
            }
        }
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
        netCheckVersion();

        //日志上报
        netLogReport();
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
        } else if (menu.equals(ConfigFragment.TAG)) {
            if (fg_config == null) {
                fg_config = new ConfigFragment();
                fg_config.setMenuClickListener(mMenuClickListener);
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fg_config, ConfigFragment.TAG).commit();
            }
            mCurrentFragment = fg_config;
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
        } else if (menu.equals(DepFragment.TAG)) {
            if (fg_dependence == null) {
                fg_dependence = new DepFragment();
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

    private void netCheckVersion() {
        ApiController.getVersion(getClassName(), new ApiController.VersionCallback() {
            @Override
            public void onSuccess(Version version) {
                try {
                    int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    if (versionCode < version.getVersionCode()) {
                        showVersionNotice(version);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(TAG, msg);
            }
        });
    }

    private void netLogReport() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", DeviceUnit.getAndroidID(this));

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        ApiController.logReport(getClassName(), requestBody, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(BaseRes baseRes) {

            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(TAG, msg);
            }
        });
    }

    private void showVersionNotice(Version version) {
        String content = "最新版本：" + version.getVersionName() + "\n\n";
        content += "更新时间：" + version.getUpdateTime() + "\n\n";
        content += TextUnit.join(version.getUpdateDetail(), "\n\n");

        ConfirmWindow confirmWindow = new ConfirmWindow("版本更新", content, "取消", "更新");
        confirmWindow.setFocusable(false);
        confirmWindow.setConfirmInterface(isConfirm -> {
            if (isConfirm) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(version.getDownloadUrl());
                intent.setData(uri);
                startActivity(intent);
                return !version.isForce();
            } else {
                if (version.isForce()) {
                    finish();
                }
                return true;
            }
        });
        popupWindowNotice = PopupWindowManager.buildConfirmWindow(this, confirmWindow);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (popupWindowNotice != null && popupWindowNotice.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}