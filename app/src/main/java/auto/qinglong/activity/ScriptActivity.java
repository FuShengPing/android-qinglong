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
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.tools.net.CallManager;
import auto.qinglong.tools.net.WebJsManager;

public class ScriptActivity extends BaseActivity {
    public static String EXTRA_NAME = "scriptName";
    public static String EXTRA_PARENT = "scriptParent";

    private String scriptName;
    private String scriptParent;

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
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        //设置脚本名称
        layout_tip.setText(scriptName);

        //返回监听
        layout_back.setOnClickListener(v -> finish());

        //刷新监听
        layout_refresh.setOnClickListener(v -> {
            if (CallManager.isRequesting(getClassName())) {
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
            //加载
            WebJsManager.refreshScript(webView);
        });

        layout_edit.setOnClickListener(v -> {
            layout_nav_bar.setVisibility(View.INVISIBLE);
            layout_edit_bar.setVisibility(View.VISIBLE);
            WebJsManager.setEditable(webView, true);
        });

        layout_edit_back.setOnClickListener(v -> {
            layout_edit_bar.setVisibility(View.INVISIBLE);
            layout_nav_bar.setVisibility(View.VISIBLE);
            WindowUnit.hideKeyboard(webView);
            WebJsManager.setEditable(webView, false);
            WebJsManager.backScript(webView);
        });

        layout_edit_save.setOnClickListener(v -> WebJsManager.saveScript(webView));

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
                WebJsManager.initScript(webView, AccountSP.getCurrentAccount().getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization(), scriptName, scriptParent);
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

    @Override
    protected void initWindow() {

    }
}