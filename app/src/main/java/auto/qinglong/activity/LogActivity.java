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
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.web.WebJsManager;

public class LogActivity extends BaseActivity {
    public static String ExtraName = "logName";
    public static String ExtraPath = "logPath";
    private String logPath;
    private String logName;
    private String logContent = "";
    private ImageView layout_back;
    private TextView layout_tip;
    private ImageView layout_refresh;
    private LinearLayout layout_web_container;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logPath = getIntent().getStringExtra(ExtraPath);
        logName = getIntent().getStringExtra(ExtraPath);

        layout_back = findViewById(R.id.bar_back);
        layout_tip = findViewById(R.id.log_name);
        layout_refresh = findViewById(R.id.log_refresh);
        layout_web_container = findViewById(R.id.web_container);
        initViewSetting();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViewSetting() {
        //设置日志名称
        layout_tip.setText(logName);

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
                loadLog(logPath);
            }
        });

        createWebView();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView = new WebView(getApplicationContext());
        webView.setLayoutParams(layoutParams);
        webView.setFocusable(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadLog(logPath);
            }
        });
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

    /**
     * 从服务器加载日志
     *
     * @param logPath 日志地址
     */
    private void loadLog(String logPath) {
        if (logPath == null || logPath.isEmpty()) {
            WebJsManager.setCode(webView, "无效日志路径");
            return;
        }

        ApiController.loadLog(getClass().getName(), logPath, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String data) {
                if (!logContent.equals(data)) {
                    logContent = data;
                    WebJsManager.setCode(webView, data);
                }else if (data.isEmpty()){
                    logContent = data;
                    WebJsManager.setCode(webView, "暂无日志信息");
                }
                clearRefresh();
            }

            @Override
            public void onFailure(String msg) {
                WebJsManager.setCode(webView, msg);
                clearRefresh();
            }
        });
    }


    /**
     * 清除刷新效果
     */
    private void clearRefresh() {
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