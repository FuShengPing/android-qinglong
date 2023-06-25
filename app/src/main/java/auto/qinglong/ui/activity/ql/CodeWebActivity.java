package auto.qinglong.ui.activity.ql;

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
import auto.qinglong.R;
import auto.qinglong.ui.activity.BaseActivity;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.web.QLWebJsManager;
import auto.base.util.ToastUnit;
import auto.qinglong.bean.views.WebViewBuilder;

public class CodeWebActivity extends BaseActivity {
    public static final String TAG = "CodeWebActivity";

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_LOG_PATH = "logPath";
    public static final String EXTRA_SCRIPT_NAME = "scriptName";
    public static final String EXTRA_SCRIPT_PARENT = "scriptParent";
    public static final String EXTRA_DEPENDENCE_ID = "dependenceId";
    public static final String EXTRA_CAN_REFRESH = "canRefresh";
    public static final String EXTRA_CAN_EDIT = "canEdit";
    public static final String TYPE_LOG = "log";
    public static final String TYPE_SCRIPT = "script";
    public static final String TYPE_DEPENDENCE = "dependence";
    public static final String TYPE_CONFIG = "config";

    private boolean mInitFlag = false;
    private String mContent;
    private String mTitle;
    private String mType;
    private boolean mCanRefresh;
    private boolean mCanEdit;
    private String mScriptName;
    private String mScriptParent;
    private String mLogPath;
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
        setContentView(R.layout.activity_code_web);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        mCanRefresh = getIntent().getBooleanExtra(EXTRA_CAN_REFRESH, true);
        mCanEdit = getIntent().getBooleanExtra(EXTRA_CAN_EDIT, false);
        mScriptName = getIntent().getStringExtra(EXTRA_SCRIPT_NAME);
        mScriptParent = getIntent().getStringExtra(EXTRA_SCRIPT_PARENT);
        mLogPath = getIntent().getStringExtra(EXTRA_LOG_PATH);
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
                QLWebJsManager.setEditable(ui_webView, true);
            });

            ui_edit_back.setOnClickListener(v -> {
                ui_edit_bar.setVisibility(View.INVISIBLE);
                ui_nav_bar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(ui_webView);
                ui_webView.clearFocus();
                ui_webView.setFocusable(false);
                ui_webView.setFocusableInTouchMode(false);
                QLWebJsManager.setEditable(ui_webView, false);
                QLWebJsManager.setContent(ui_webView, mContent);
            });

            ui_edit_save.setOnClickListener(v -> QLWebJsManager.getContent(ui_webView, value -> {
                try {
                    ui_webView.clearFocus();
                    WindowUnit.hideKeyboard(ui_webView);
                    StringBuilder stringBuilder = new StringBuilder(URLDecoder.decode(value, "UTF-8"));
                    if (stringBuilder.length() >= 2) {
                        stringBuilder.deleteCharAt(0);
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    save(stringBuilder.toString());
                } catch (UnsupportedEncodingException e) {
                    ToastUnit.showShort(e.getMessage());
                }
            }));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mInitFlag) {
            initWebView();
        }
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        ui_webView = null;
        super.onDestroy();
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

        mInitFlag = true;
    }

    /**
     * 加载对应类型内容
     *
     * @param type 类型
     */
    private void load(String type) {
        switch (type) {
            case TYPE_SCRIPT:
                netGetScriptDetail("api/scripts/" + mScriptName + "?path=" + mScriptParent);
                break;
            case TYPE_LOG:
                netGetLogDetail(mLogPath);
                break;
            case TYPE_DEPENDENCE:
                netGetDependenceLog("api/dependencies/" + mDependenceId);
                break;
            case TYPE_CONFIG:
                netGetConfig();
                break;
        }
    }

    /**
     * 网络加载结束 关闭刷新动画
     */
    private void loadFinish() {
        if (ui_refresh.getAnimation() != null) {
            ui_refresh.getAnimation().cancel();
        }
    }

    /**
     * 保存内容，当前仅支持配置文件和脚本文件
     *
     * @param content 内容
     */
    private void save(String content) {
        if (Objects.equals(mType, TYPE_CONFIG)) {
            netSaveConfig(content);
        } else if (Objects.equals(mType, TYPE_SCRIPT)) {
            netSaveScript(content);
        }
    }

    private void netGetConfig() {
        QLApiController.getConfigDetail(getNetRequestID(), new QLApiController.NetConfigCallback() {
            @Override
            public void onSuccess(String content) {
                mContent = content;
                ui_edit.setVisibility(View.VISIBLE);
                QLWebJsManager.setContent(ui_webView, content);
                loadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                ui_edit.setVisibility(View.INVISIBLE);
                loadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netSaveConfig(String content) {
        QLApiController.saveConfig(getNetRequestID(), content, new QLApiController.NetBaseCallback() {
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

    private void netGetScriptDetail(String path) {
        QLApiController.getScriptDetail(getNetRequestID(), path, new QLApiController.NetSimpleCallBack() {
            @Override
            public void onSuccess(String content) {
                //防止内容过大导致崩溃
                if (content.length() > 1500000) {
                    ToastUnit.showShort(getString(R.string.tip_text_too_long));
                    ui_refresh.setVisibility(View.GONE);
                    return;
                }
                mContent = content;
                ui_edit.setVisibility(View.VISIBLE);
                QLWebJsManager.setContent(ui_webView, content);
                loadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                ui_edit.setVisibility(View.INVISIBLE);
                loadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netSaveScript(String content) {
        QLApiController.saveScript(getNetRequestID(), content, mScriptName, mScriptParent, new QLApiController.NetBaseCallback() {
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

    private void netGetLogDetail(String path) {
        QLApiController.getLogDetail(getNetRequestID(), path, new QLApiController.NetSimpleCallBack() {
            @Override
            public void onSuccess(String content) {
                QLWebJsManager.setContent(ui_webView, content);
                loadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                loadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netGetDependenceLog(String path) {
        QLApiController.getDependence(getNetRequestID(), path, new QLApiController.NetGetDependenceCallback() {
            @Override
            public void onSuccess(QLDependence dependence) {
                QLWebJsManager.setContent(ui_webView, dependence.getLogStr());
                loadFinish();
                ToastUnit.showShort(getString(R.string.tip_load_success));
            }

            @Override
            public void onFailure(String msg) {
                loadFinish();
                ToastUnit.showShort(msg);
            }
        });
    }
}
