package auto.qinglong.activity.ql.log;

import android.os.Bundle;
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
import auto.qinglong.views.WebViewBuilder;

public class LogDetailActivity extends BaseActivity {
    public static final String ExtraName = "logName";
    public static final String ExtraPath = "logPath";
    private String logPath;
    private String logName;

    //布局变量
    private ImageView ui_bar_back;
    private TextView ui_bar_title;
    private ImageView ui_refresh;
    private LinearLayout ui_web_container;
    private WebView ui_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logPath = getIntent().getStringExtra(ExtraPath);
        logName = getIntent().getStringExtra(ExtraName);

        ui_bar_back = findViewById(R.id.bar_back);
        ui_bar_title = findViewById(R.id.log_name);
        ui_refresh = findViewById(R.id.log_refresh);
        ui_web_container = findViewById(R.id.web_container);

        init();
    }

    @Override
    protected void init() {
        //设置日志名称
        ui_bar_title.setText(logName);

        //返回监听
        ui_bar_back.setOnClickListener(v -> finish());

        //刷新监听
        ui_refresh.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getClassName())) {
                return;
            }
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
            animation.setDuration(1000);
            ui_refresh.startAnimation(animation);
            WebJsManager.refreshLog(ui_webView);
        });

        ui_webView = WebViewBuilder.build(getBaseContext(), ui_web_container, new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                WebJsManager.initLog(ui_webView, AccountSP.getCurrentAccount().getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization(), logPath);
            }
        });
        //加载本地网页
        ui_webView.loadUrl("file:///android_asset/web/editor.html");

    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }

}