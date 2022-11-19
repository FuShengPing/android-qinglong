package auto.qinglong.activity.module.log;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import auto.qinglong.R;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.network.RequestManager;
import auto.qinglong.network.WebJsManager;

public class LogActivity extends BaseActivity {
    public static String ExtraName = "logName";
    public static String ExtraPath = "logPath";
    private String logPath;
    private String logName;
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
        logName = getIntent().getStringExtra(ExtraName);

        layout_back = findViewById(R.id.bar_back);
        layout_tip = findViewById(R.id.log_name);
        layout_refresh = findViewById(R.id.log_refresh);
        layout_web_container = findViewById(R.id.web_container);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        //设置日志名称
        layout_tip.setText(logName);

        //返回监听
        layout_back.setOnClickListener(v -> finish());

        //刷新监听
        layout_refresh.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getClassName())) {
                return;
            }
            //禁用点击
            layout_refresh.setEnabled(false);
            //开启动画
            Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    layout_refresh.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(1000);
            layout_refresh.startAnimation(animation);
            WebJsManager.refreshLog(webView);
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
                WebJsManager.initLog(webView, AccountSP.getCurrentAccount().getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization(), logPath);
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
     * 窗体销毁
     * 释放web编辑器
     */
    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

}