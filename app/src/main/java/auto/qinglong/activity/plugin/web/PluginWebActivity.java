package auto.qinglong.activity.plugin.web;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
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
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.WebViewBuilder;
import auto.qinglong.views.popup.ConfirmWindow;
import auto.qinglong.views.popup.PopupWindowManager;

public class PluginWebActivity extends BaseActivity {
    public static final String TAG = "PluginWebActivity";
    private CookieManager cookieManager;


    private ImageView ui_bar_back;
    private ImageView ui_bar_rule;
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

        ui_bar_back = findViewById(R.id.common_bar_back);
        ui_bar_rule = findViewById(R.id.bar_rule);
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
        ui_bar_back.setOnClickListener(v -> finish());

        //规则列表界面
        ui_bar_rule.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PluginWebRuleActivity.class);
            startActivity(intent);
        });

        //加载网页操作
        ui_bt_load.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            cookieManager.removeAllCookies(null);
            String url = ui_et_url.getText().toString().trim();
            if (!url.isEmpty()) {
                ui_webView.loadUrl(url);
            } else {
                ToastUnit.showShort(getBaseContext(), "请输入网页地址");
            }
        });

        //读取Cookies操作
        ui_bt_read.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            String cookies, url;
            url = ui_webView.getOriginalUrl();

            if (url != null && !url.isEmpty()) {
                cookies = cookieManager.getCookie(url);
            } else {
                ToastUnit.showShort(getBaseContext(), "请先加载网页");
                return;
            }

            //配置窗体信息
            ConfirmWindow confirmWindow = new ConfirmWindow();
            confirmWindow.setConfirmTip("拷贝");
            confirmWindow.setCancelTip("取消");
            confirmWindow.setTitle("Cookies");
            confirmWindow.setContent(cookies);
            confirmWindow.setConfirmInterface(isConfirm -> {
                if (isConfirm) {
                    ClipboardManager clipboardManager = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, cookies));
                    ToastUnit.showShort("已复制到剪切板");
                }
                return true;
            });
            //构建并显示窗体
            PopupWindowManager.buildConfirmWindow(this, confirmWindow);
        });

        //导入变量操作
        ui_bt_import.setOnClickListener(v -> {

        });

        //构建web控件
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
        }, null);
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