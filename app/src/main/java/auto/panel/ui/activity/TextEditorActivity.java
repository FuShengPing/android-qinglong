package auto.panel.ui.activity;

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

import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.net.panel.ApiController;
import auto.panel.net.web.PanelWebJsManager;
import auto.panel.net.web.WebViewBuilder;
import auto.panel.utils.ToastUnit;

public class TextEditorActivity extends BaseActivity {
    public static final String TAG = "CodeViewActivity";
    private static final String STATIC_FILE_HTML_PATH = "file:///android_asset/web/editor/index.html";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_LOG_ID = "EXTRA_LOG_ID";
    public static final String EXTRA_LOG_NAME = "EXTRA_LOG_NAME";
    public static final String EXTRA_LOG_DIR = "EXTRA_LOG_DIR";
    public static final String EXTRA_SCRIPT_NAME = "EXTRA_SCRIPT_NAME";
    public static final String EXTRA_SCRIPT_DIR = "EXTRA_SCRIPT_DIR";
    public static final String EXTRA_DEPENDENCE_ID = "EXTRA_DEPENDENCE_ID";
    public static final String EXTRA_CAN_REFRESH = "EXTRA_CAN_REFRESH";
    public static final String EXTRA_CAN_EDIT = "EXTRA_CAN_EDIT";
    public static final String TYPE_LOG = "TYPE_LOG";
    public static final String TYPE_SCRIPT = "TYPE_SCRIPT";
    public static final String TYPE_DEPENDENCE = "TYPE_DEPENDENCE";
    public static final String TYPE_CONFIG = "TYPE_CONFIG";

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

    private LinearLayout uiNavBar;
    private ImageView uiBack;
    private TextView uiTitle;
    private ImageView uiEdit;
    private ImageView uiRefresh;
    private LinearLayout uiEditBar;
    private ImageView uiEditBack;
    private ImageView uiEditSave;
    private FrameLayout uiWebContainer;
    private WebView uiWebView;

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

        uiNavBar = findViewById(R.id.code_bar_nav);
        uiBack = findViewById(R.id.code_bar_nav_back);
        uiTitle = findViewById(R.id.code_bar_nav_title);
        uiEdit = findViewById(R.id.code_bar_nav_edit);
        uiRefresh = findViewById(R.id.code_bar_nav_refresh);
        uiEditBar = findViewById(R.id.code_bar_edit);
        uiEditBack = findViewById(R.id.code_bar_edit_back);
        uiEditSave = findViewById(R.id.code_bar_edit_save);
        uiWebContainer = findViewById(R.id.web_container);

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
        WebViewBuilder.destroy(uiWebView);
        uiWebView = null;
        super.onDestroy();
    }

    /**
     * 保存内容，当前仅支持配置文件和脚本文件
     *
     * @param content 内容
     */
    private void onSave(String content) {
        if (Objects.equals(mType, TYPE_CONFIG)) {
            netSaveConfigContent(content);
        } else if (Objects.equals(mType, TYPE_SCRIPT)) {
            netSaveScriptContent(content);
        }
    }

    /**
     * 网络加载结束 关闭刷新动画
     */
    private void onLoadFinish() {
        if (uiRefresh.getAnimation() != null) {
            uiRefresh.getAnimation().cancel();
        }
    }

    @Override
    protected void init() {
        //设置标题
        uiTitle.setText(mTitle);

        //返回监听
        uiBack.setOnClickListener(v -> finish());

        //刷新监听
        if (mCanRefresh) {
            uiRefresh.setOnClickListener(v -> {
                //禁用点击
                uiRefresh.setEnabled(false);
                //开启动画
                Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        uiRefresh.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animation.setRepeatCount(-1);
                animation.setDuration(1000);
                uiRefresh.startAnimation(animation);
                loadContent();
            });
        }

        //编辑
        if (mCanEdit) {
            uiEdit.setOnClickListener(v -> {
                uiNavBar.setVisibility(View.INVISIBLE);
                uiEditBar.setVisibility(View.VISIBLE);
                PanelWebJsManager.setEditable(uiWebView, true);
                uiWebView.setFocusable(true);
                uiWebView.setFocusableInTouchMode(true);
                uiWebView.requestFocus();
            });

            uiEditBack.setOnClickListener(v -> {
                uiEditBar.setVisibility(View.INVISIBLE);
                uiNavBar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(uiWebView);
                uiWebView.clearFocus();
                uiWebView.setFocusable(false);
                uiWebView.setFocusableInTouchMode(false);
                PanelWebJsManager.setEditable(uiWebView, false);
                PanelWebJsManager.setContent(uiWebView, mContent);
            });

            uiEditSave.setOnClickListener(v -> PanelWebJsManager.getContent(uiWebView, value -> {
                try {
                    uiWebView.clearFocus();
                    WindowUnit.hideKeyboard(uiWebView);
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
            uiRefresh.setVisibility(View.VISIBLE);
        }

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadContent();
            }
        };
        uiWebView = WebViewBuilder.build(getBaseContext(), uiWebContainer, client, null);
        uiWebView.setFocusable(false);
        uiWebView.loadUrl(STATIC_FILE_HTML_PATH);
        init = true;
    }

    /**
     * 加载对应类型内容
     */
    private void loadContent() {
        switch (mType) {
            case TYPE_SCRIPT:
                netGetScriptContent(mScriptName, mScriptParent);
                break;
            case TYPE_LOG:
                netGetLogFileContent(mLogId, mLogFileName, mLogFileDir);
                break;
            case TYPE_DEPENDENCE:
                netGetDependenceLogContent(mDependenceId);
                break;
            case TYPE_CONFIG:
                netGetConfigContent();
                break;
            default:
                ToastUnit.showShort("参数异常");
        }
    }

    private void netGetConfigContent() {
        ApiController.getConfigFileContent(new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                mContent = content;
                uiEdit.setVisibility(View.VISIBLE);
                PanelWebJsManager.setContent(uiWebView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                uiEdit.setVisibility(View.INVISIBLE);
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netGetScriptContent(String fileName, String fileParent) {
        ApiController.getScriptContent(fileName, fileParent, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                //防止内容过大导致崩溃
                if (content.length() > 1024 * 1024) {
                    ToastUnit.showShort(getString(R.string.tip_text_too_long));
                    uiRefresh.setVisibility(View.GONE);
                    return;
                }
                mContent = content;
                uiEdit.setVisibility(View.VISIBLE);
                PanelWebJsManager.setContent(uiWebView, content);
                onLoadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                uiEdit.setVisibility(View.INVISIBLE);
                onLoadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netGetLogFileContent(String scriptKey, String fileName, String fileParent) {
        ApiController.getLogContent(scriptKey, fileName, fileParent, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                PanelWebJsManager.setContent(uiWebView, content);
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

    private void netGetDependenceLogContent(String key) {
        ApiController.getDependenceLogContent(key, new auto.panel.net.panel.ApiController.ContentCallBack() {
            @Override
            public void onSuccess(String content) {
                PanelWebJsManager.setContent(uiWebView, content);
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

    private void netSaveScriptContent(String content) {
        ApiController.updateScriptContent(mScriptName, mScriptParent, content, new auto.panel.net.panel.ApiController.BaseCallBack() {
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

    private void netSaveConfigContent(String content) {
        ApiController.updateConfigFileContent(content, new auto.panel.net.panel.ApiController.BaseCallBack() {
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
}
