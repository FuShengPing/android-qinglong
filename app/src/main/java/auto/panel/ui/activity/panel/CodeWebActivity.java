package auto.panel.ui.activity.panel;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.panel.ApiController;
import auto.panel.net.web.PanelWebJsManager;
import auto.panel.net.web.WebViewBuilder;
import auto.panel.ui.activity.BaseActivity;

public class CodeWebActivity extends BaseActivity {
    public static final String TAG = "CodeWebActivity";

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_LOG_ID = "logId";
    public static final String EXTRA_LOG_NAME = "logFileName";
    public static final String EXTRA_LOG_DIR = "logFileDir";
    public static final String EXTRA_SCRIPT_NAME = "scriptName";
    public static final String EXTRA_SCRIPT_DIR = "scriptDir";
    public static final String EXTRA_DEPENDENCE_ID = "dependenceId";
    public static final String EXTRA_CAN_REFRESH = "canRefresh";
    public static final String EXTRA_CAN_EDIT = "canEdit";

    public static final String TYPE_LOG = "log";
    public static final String TYPE_SCRIPT = "script";
    public static final String TYPE_DEPENDENCE = "dependence";
    public static final String TYPE_CONFIG = "config";

    private boolean init = false;
    private String mContent;
    private String mTitle;
    private String mType;
    private boolean mCanRefresh;
    private boolean mCanEdit;
    private String mScriptName;
    private String mScriptParent;
    private String mLogId;
    private String mLogFileName;
    private String mLogFileDir;
    private String mDependenceId;

    private LinearLayout ui_nav_bar;
    private ImageView ui_back;
    private TextView ui_tip;
    private ImageView ui_edit;
    private ImageView ui_refresh;
    private LinearLayout ui_edit_bar;
    private ImageView ui_edit_back;
    private ImageView ui_edit_save;
    private FrameLayout ui_web_container;
    private WebView ui_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_code_web);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        mCanRefresh = getIntent().getBooleanExtra(EXTRA_CAN_REFRESH, true);
        mCanEdit = getIntent().getBooleanExtra(EXTRA_CAN_EDIT, false);
        mScriptName = getIntent().getStringExtra(EXTRA_SCRIPT_NAME);
        mScriptParent = getIntent().getStringExtra(EXTRA_SCRIPT_DIR);
        mLogId = getIntent().getStringExtra(EXTRA_LOG_ID);
        mLogFileName = getIntent().getStringExtra(EXTRA_LOG_NAME);
        mLogFileDir = getIntent().getStringExtra(EXTRA_LOG_DIR);
        mDependenceId = getIntent().getStringExtra(EXTRA_DEPENDENCE_ID);

        ui_nav_bar = findViewById(R.id.script_bar);
        ui_back = findViewById(R.id.script_back);
        ui_tip = findViewById(R.id.script_name);
        ui_edit = findViewById(R.id.script_edit);
        ui_refresh = findViewById(R.id.script_refresh);
        ui_edit_bar = findViewById(R.id.code_bar_edit);
        ui_edit_back = findViewById(R.id.code_bar_edit_back);
        ui_edit_save = findViewById(R.id.code_bar_edit_save);
        ui_web_container = findViewById(R.id.web_container);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!init) {
            initWebView();
        }
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        ui_webView = null;
        super.onDestroy();
    }

    /**
     * 保存内容，当前仅支持配置文件和脚本文件
     *
     * @param content 内容
     */
    private void onSave(String content) {
        if (Objects.equals(mType, TYPE_CONFIG)) {
            saveConfigContent(content);
        } else if (Objects.equals(mType, TYPE_SCRIPT)) {
            saveScriptContent(content);
        }
    }

    /**
     * 网络加载结束 关闭刷新动画
     */
    private void onLoadFinish() {
        if (ui_refresh.getAnimation() != null) {
            ui_refresh.getAnimation().cancel();
        }
    }

    @Override
    protected void init() {
        //设置标题
        ui_tip.setText(mTitle);

        //返回监听
        ui_back.setOnClickListener(v -> finish());

        //刷新监听
        if (mCanRefresh) {
            ui_refresh.setOnClickListener(v -> {
                //禁用点击
                ui_refresh.setEnabled(false);
                //开启动画
                Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ui_refresh.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animation.setRepeatCount(-1);
                animation.setDuration(1000);
                ui_refresh.startAnimation(animation);
                load(mType);
            });
        }

        //编辑
        if (mCanEdit) {
            ui_edit.setOnClickListener(v -> {
                ui_nav_bar.setVisibility(View.INVISIBLE);
                ui_edit_bar.setVisibility(View.VISIBLE);
                ui_webView.setFocusable(true);
                ui_webView.setFocusableInTouchMode(true);
                PanelWebJsManager.setEditable(ui_webView, true);
            });

            ui_edit_back.setOnClickListener(v -> {
                ui_edit_bar.setVisibility(View.INVISIBLE);
                ui_nav_bar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(ui_webView);
                ui_webView.clearFocus();
                ui_webView.setFocusable(false);
                ui_webView.setFocusableInTouchMode(false);
                PanelWebJsManager.setEditable(ui_webView, false);
                PanelWebJsManager.setContent(ui_webView, mContent);
            });

            ui_edit_save.setOnClickListener(v -> PanelWebJsManager.getContent(ui_webView, value -> {
                try {
                    ui_webView.clearFocus();
                    WindowUnit.hideKeyboard(ui_webView);
                    StringBuilder stringBuilder = new StringBuilder(URLDecoder.decode(value, "UTF-8"));
                    if (stringBuilder.length() >= 2) {
                        stringBuilder.deleteCharAt(0);
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    onSave(stringBuilder.toString());
                } catch (UnsupportedEncodingException e) {
                    ToastUnit.showShort(e.getMessage());
                }
            }));
        }
    }

    private void initWebView() {
        if (mCanRefresh) {
            ui_refresh.setVisibility(View.VISIBLE);
        }

        ui_webView = WebViewBuilder.build(getBaseContext(), ui_web_container, new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                load(mType);
            }
        }, null);

        ui_webView.setFocusable(false);
        ui_webView.loadUrl("file:///android_asset/web/editor.html");

        init = true;
    }

    /**
     * 加载对应类型内容
     *
     * @param type 类型
     */
    private void load(String type) {
        switch (type) {
            case TYPE_SCRIPT:
                getScriptContent(mScriptName, mScriptParent);
                break;
            case TYPE_LOG:
                getLogFileContent(mLogId, mLogFileName, mLogFileDir);
                break;
            case TYPE_DEPENDENCE:
                getDependenceLogContent(mDependenceId);
                break;
            case TYPE_CONFIG:
                getConfigContent();
                break;
        }
    }

    private void saveConfigContent(String content) {
        ApiController.saveConfigFileContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), content, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                mContent = content;
                ToastUnit.showShort("保存成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void saveScriptContent(String content) {
       ApiController.saveScriptContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), mScriptName, mScriptParent, content, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                mContent = content;
                ToastUnit.showShort("保存成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void getConfigContent() {
       ApiController.getConfigContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                mContent = content;
                ui_edit.setVisibility(View.VISIBLE);
                PanelWebJsManager.setContent(ui_webView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                ui_edit.setVisibility(View.INVISIBLE);
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void getScriptContent(String fileName, String fileParent) {
        ApiController.getScriptContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), fileName, fileParent, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                //防止内容过大导致崩溃
                if (content.length() > 1024 * 1024) {
                    ToastUnit.showShort(getString(R.string.tip_text_too_long));
                    ui_refresh.setVisibility(View.GONE);
                    return;
                }
                mContent = content;
                ui_edit.setVisibility(View.VISIBLE);
                PanelWebJsManager.setContent(ui_webView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                ui_edit.setVisibility(View.INVISIBLE);
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void getLogFileContent(String scriptKey, String fileName, String fileParent) {
        ApiController.getLogContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), scriptKey, fileName, fileParent, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                PanelWebJsManager.setContent(ui_webView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void getDependenceLogContent(String key) {
        ApiController.getDependenceLogContent(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), key, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                PanelWebJsManager.setContent(ui_webView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }
}
