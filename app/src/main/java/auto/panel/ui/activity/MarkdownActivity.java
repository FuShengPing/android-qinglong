package auto.panel.ui.activity;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import auto.panel.R;
import auto.panel.net.web.WebViewBuilder;
import auto.panel.utils.DeviceUnit;
import auto.panel.utils.LogUnit;
import auto.panel.utils.ToastUnit;

public class MarkdownActivity extends BaseActivity {
    private static final String STATIC_FILE_PATH = "file:///android_asset/web/markdown/index.html";

    private FrameLayout uiWebContainer;
    private WebView uiWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown);

        uiWebContainer = findViewById(R.id.web_container);

        init();
    }

    @Override
    protected void init() {
        uiWebView = WebViewBuilder.build(getBaseContext(), uiWebContainer, new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 返回true表示拦截该url，不再继续加载
                // 返回false表示允许WebView加载该url
                DeviceUnit.copyText(getBaseContext(), request.getUrl().toString());
                ToastUnit.showShort("已复制链接到剪贴板");
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        }, null);

        uiWebView.setFocusable(false);
        uiWebView.loadUrl(STATIC_FILE_PATH);
    }
}