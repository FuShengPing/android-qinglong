package auto.qinglong.activity.plugin.web;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.WebViewBuilder;

public class PluginWebActivity extends BaseActivity {
    public static final String TAG = "PluginWebActivity";
    private CookieManager cookieManager;


    private ImageView ui_back;
    private FrameLayout ui_webView_container;
    private WebView ui_webView;
    private EditText ui_et_url;
    private Button ui_bt_load;
    private Button ui_bt_read;
    private Button ui_bt_import;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_web);

        ui_back = findViewById(R.id.common_bar_back);
        ui_webView_container = findViewById(R.id.web_container);
        ui_et_url = findViewById(R.id.et_url);
        ui_bt_load = findViewById(R.id.bt_load);
        ui_bt_read = findViewById(R.id.bt_read);
        ui_bt_import = findViewById(R.id.bt_import);

        cookieManager = CookieManager.getInstance();

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_bt_load.setOnClickListener(v -> {
            cookieManager.removeAllCookies(null);
            String url = ui_et_url.getText().toString().trim();
            LogUnit.log(TAG, "加载：" + url);
            if (!url.isEmpty()) {
                ui_webView.loadUrl(url);
                WindowUnit.hideKeyboard(ui_bt_load);
            }
        });

        ui_bt_read.setOnClickListener(v -> {
            String url = ui_webView.getUrl();
            LogUnit.log(TAG, "读取：" + url);
            if (url != null && !url.isEmpty()) {
                String cookies = cookieManager.getCookie(url);
            }
        });

        ui_webView = WebViewBuilder.build(getBaseContext(), ui_webView_container, new WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return !url.startsWith("https") && !url.startsWith("http");
            }
        });
    }

    /**
     * 窗体销毁
     */
    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }
}