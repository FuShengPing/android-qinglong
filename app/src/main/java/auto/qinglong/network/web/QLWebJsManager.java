package auto.qinglong.network.web;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import auto.qinglong.utils.ToastUnit;

/**
 * WebView与JS交互辅助类 实现代码显示和编辑功能.
 */
public class QLWebJsManager {

    /**
     * 设置编辑器是否可用编辑
     */
    public static void setEditable(WebView webView, boolean editable) {
        if (webView == null) {
            return;
        }
        String script;
        if (editable) {
            script = String.format("javascript:setEditable(%1$s)", "true");
        } else {
            script = String.format("javascript:setEditable(%1$s)", "false");
        }
        webView.evaluateJavascript(script, null);
    }

    public static void setContent(WebView webView, String content) {
        if (webView == null) {
            return;
        }
        try {
            content = URLEncoder.encode(content, "UTF-8").replaceAll("\\+", "%20");
            String script = String.format("javascript:setCode('%1$s')", content);
            webView.evaluateJavascript(script, null);
        } catch (UnsupportedEncodingException e) {
            ToastUnit.showShort(e.getMessage());
        }
    }

    public static void getContent(WebView webView, ValueCallback<String> callback) {
        if (webView == null) {
            return;
        }
        String script = "javascript:getContent()";
        webView.evaluateJavascript(script, callback);
    }

    public static void initConfig(WebView webView, String host, String authorization) {
        if (webView == null) {
            return;
        }
        String script = String.format("javascript:initConfig('%1$s','%2$s')", host, authorization);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshConfig(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:refreshConfig()";
        webView.evaluateJavascript(script, null);
    }

    public static void backConfig(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:backConfig()";
        webView.evaluateJavascript(script, null);
    }

    public static void saveConfig(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:saveConfig()";
        webView.evaluateJavascript(script, null);
    }
}
