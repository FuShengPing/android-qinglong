package auto.qinglong.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import auto.qinglong.R;
import auto.qinglong.api.ApiController;
import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.web.JsInterface;
import auto.qinglong.tools.web.WebJsManager;

public class ScriptActivity extends BaseActivity {
    public static String EXTRA_URL = "scriptURL";
    public static String EXTRA_NAME = "scriptName";
    public static String EXTRA_PARENT = "scriptParent";

    private String scriptPath;
    private String scriptName;
    private String scriptParent;
    private String scriptContent = "";

    private LinearLayout layout_nav_bar;
    private ImageView layout_back;
    private TextView layout_tip;
    private ImageView layout_edit;
    private ImageView layout_refresh;
    private LinearLayout layout_edit_bar;
    private ImageView layout_edit_back;
    private ImageView layout_edit_save;
    private LinearLayout layout_web_container;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);

        scriptPath = getIntent().getStringExtra(EXTRA_URL);
        scriptName = getIntent().getStringExtra(EXTRA_NAME);
        scriptParent = getIntent().getStringExtra(EXTRA_PARENT);

        layout_nav_bar = findViewById(R.id.script_bar);
        layout_back = findViewById(R.id.script_back);
        layout_tip = findViewById(R.id.script_name);
        layout_edit = findViewById(R.id.script_edit);
        layout_refresh = findViewById(R.id.script_refresh);
        layout_edit_bar = findViewById(R.id.script_edit_bar);
        layout_edit_back = findViewById(R.id.script_edit_back);
        layout_edit_save = findViewById(R.id.script_edit_save);
        layout_web_container = findViewById(R.id.web_container);
        initViewSetting();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViewSetting() {
        //设置脚本名称
        layout_tip.setText(scriptName);

        //返回监听
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //刷新监听
        layout_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                //禁用点击
                layout_refresh.setEnabled(false);
                //开启动画
                Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                animation.setRepeatCount(Animation.INFINITE);
                layout_refresh.startAnimation(animation);
                //加载
                loadScript(scriptPath);
            }
        });


        layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_nav_bar.setVisibility(View.INVISIBLE);
                layout_edit_bar.setVisibility(View.VISIBLE);
                WebJsManager.setEditable(webView, true);
            }
        });

        layout_edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_edit_bar.setVisibility(View.INVISIBLE);
                layout_nav_bar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(webView);
                WebJsManager.setEditable(webView, false);
                WebJsManager.setCode(webView, scriptContent);
            }
        });

        layout_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                WindowUnit.hideKeyboard(webView);
                WebJsManager.getCode(webView, new WebJsManager.WebCallback() {
                    @Override
                    public void onContent(String content) {
                        saveScript(content);
                    }
                });

            }
        });

        createWebView();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView = new WebView(getApplicationContext());
        webView.setLayoutParams(layoutParams);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadScript(scriptPath);
            }
        });
        webView.addJavascriptInterface(new JsInterface(), "Android");
        //加载本地网页
        webView.loadUrl("file:///android_asset/web/editor.html");
        layout_web_container.addView(webView);
    }

    private void destroyWebView() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    private void loadScript(String scriptPath) {
        if (scriptPath == null || scriptPath.isEmpty()) {
            WebJsManager.setCode(webView, "无效脚本路径");
            return;
        }

        ApiController.getScriptDetail(getClass().getName(), scriptPath, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String msg) {
                if (!scriptContent.equals(msg)) {
                    scriptContent = msg;
                    WebJsManager.setCode(webView, msg);
                }
                clearRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                WebJsManager.setCode(webView, msg);
                clearRefresh(false);
            }
        });
    }

    private void saveScript(String content) {
        ApiController.saveScript(getClassName(), content, scriptName, scriptParent, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String msg) {
                ToastUnit.showShort(getBaseContext(), "保存成功");
                scriptContent = content;
                layout_edit_back.performClick();
            }

            @Override
            public void onFailure(String msg) {
                LogUnit.log(msg);
                ToastUnit.showShort(getBaseContext(), msg);
            }
        });
    }

    /**
     * 清除刷新效果
     */
    private void clearRefresh(boolean isSuccess) {
        if (isSuccess) {
            layout_edit.setVisibility(View.VISIBLE);
        } else {
            layout_edit.setVisibility(View.INVISIBLE);
        }
        layout_refresh.setVisibility(View.VISIBLE);
        layout_refresh.clearAnimation();
        layout_refresh.setEnabled(true);
    }

    /**
     * 窗体销毁
     * 释放web编辑器
     */
    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

    @Override
    protected void initWindow() {

    }
}