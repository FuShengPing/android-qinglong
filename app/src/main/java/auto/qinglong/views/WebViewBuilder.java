package auto.qinglong.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class WebViewBuilder {
    public static final String TAG = "WebViewBuilder";

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public static WebView build(Context context, ViewGroup viewGroup, WebViewClient webViewClient, Object jsInterface) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        WebView webView = new WebView(context);
        webView.setLayoutParams(layoutParams);

        WebSettings webSettings = webView.getSettings();
        //适应Html5
        webSettings.setDomStorageEnabled(true);
        //允许执行js脚本
        webSettings.setJavaScriptEnabled(true);
        //设置https和http混合加载
        webSettings.setMinimumLogicalFontSize(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //不加载缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(webViewClient);
        if (jsInterface != null) {
            webView.addJavascriptInterface(jsInterface, "Android");
        }
        viewGroup.addView(webView);
        return webView;
    }

    public static void destroy(WebView webView) {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
        }
    }
}
