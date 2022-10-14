package auto.qinglong.network;

import android.webkit.WebView;

import auto.qinglong.tools.LogUnit;

public class WebJsManager {

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
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void initConfig(WebView webView, String host, String authorization) {
        if (webView == null) {
            return;
        }

        String script = String.format("javascript:initConfig('%1$s','%2$s')", host, authorization);
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshConfig(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:refreshConfig()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void backConfig(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:backConfig()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void saveConfig(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:saveConfig()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }


    public static void initLog(WebView webView, String host, String authorization, String path) {
        if (webView == null) {
            return;
        }

        String script = String.format("javascript:initLog('%1$s','%2$s','%3$s')", host, authorization, path);
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshLog(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:refreshLog()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }


    public static void initScript(WebView webView, String host, String authorization, String filename, String path) {
        if (webView == null) {
            return;
        }

        String script = String.format("javascript:initScript('%1$s','%2$s','%3$s','%4$s')", host, authorization, filename, path);
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void saveScript(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:saveScript()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void backScript(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:backScript()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshScript(WebView webView) {
        if (webView == null) {
            return;
        }

        String script = "javascript:refreshScript()";
        LogUnit.log(script);
        webView.evaluateJavascript(script, null);
    }

}
