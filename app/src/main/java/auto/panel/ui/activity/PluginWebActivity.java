package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import auto.base.ui.popup.ConfirmPopupWindow;
import auto.base.ui.popup.MenuPopupObject;
import auto.base.ui.popup.MenuPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.net.web.WebViewBuilder;

public class PluginWebActivity extends BaseActivity {
    public static final String TAG = "PluginWebActivity";

    private CookieManager cookieManager;

    private ImageView ui_back;
    private ImageView ui_options;
    private EditText ui_et_url;
    private FrameLayout ui_webView_container;
    private WebView ui_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_plugin_web);

        ui_back = findViewById(R.id.action_bar_back);
        ui_options = findViewById(R.id.action_bar_options);
        ui_webView_container = findViewById(R.id.web_container);
        ui_et_url = findViewById(R.id.et_url);

        cookieManager = CookieManager.getInstance();

        init();
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (ui_webView != null && ui_webView.canGoBack()) {
            ui_webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_options.setOnClickListener(v -> {
            ui_et_url.clearFocus();
            WindowUnit.hideKeyboard(ui_et_url);
            showPopMenu(v);
        });

        //加载网页操作 同时清除所有cookies
        ui_et_url.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_GO) {
                return false;
            }

            cookieManager.removeAllCookies(null);
            String url = ui_et_url.getText().toString().trim();

            if (TextUnit.isFull(url)) {
                ui_et_url.clearFocus();
                WindowUnit.hideKeyboard(ui_et_url);
                ui_webView.loadUrl(url);
            } else {
                ToastUnit.showShort("请输入网页地址");
            }
            return true;
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

    private void showPopWindowConfirm(String content) {
        //配置pop窗体信息
        ConfirmPopupWindow popConfirmWindow = new ConfirmPopupWindow();
        popConfirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix(getBaseContext()) / 3);//限制最大高度
        popConfirmWindow.setConfirmTip("拷贝");
        popConfirmWindow.setCancelTip("取消");
        popConfirmWindow.setTitle("Cookies");
        popConfirmWindow.setContent(content);
        popConfirmWindow.setOnActionListener(() -> {
            ClipboardManager clipboardManager = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
            ToastUnit.showShort("已复制到剪切板");
            return true;
        });

        //构建并显示pop窗体
        PopupWindowBuilder.buildConfirmWindow(this, popConfirmWindow);
    }

    private void showPopMenu(View view) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(view);
        popMenuWindow.addItem(new MenuPopupObject("read_normal", "ck提取", R.drawable.ic_gray_crop_free));
//        popMenuWindow.addItem(new MenuPopupObject("rule", "规则配置", R.drawable.ic_gray_mul_setting));
//        popMenuWindow.addItem(new MenuPopupObject("read_rule", "规则提取", R.drawable.ic_gray_rule));
//        popMenuWindow.addItem(new MenuPopupObject("import", "导入变量", R.drawable.ic_gray_upload));

        popMenuWindow.setOnActionListener(key -> {
            if ("read_normal".equals(key)) {
                readNormal();
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(this, popMenuWindow);
    }

    private void readNormal() {
        WindowUnit.hideKeyboard(ui_et_url);
        String cookies, url;
        url = ui_webView.getOriginalUrl();

        if (url != null && !url.isEmpty()) {
            cookies = cookieManager.getCookie(url);
            showPopWindowConfirm(cookies);
        } else {
            ToastUnit.showShort("请先加载网页");
        }

    }
}