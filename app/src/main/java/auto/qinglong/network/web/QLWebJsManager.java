package auto.qinglong.network.web;

import android.webkit.WebView;

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

    public static void initLog(WebView webView, String host, String authorization, String path) {
        if (webView == null) {
            return;
        }
        String script = String.format("javascript:initLog('%1$s','%2$s','%3$s')", host, authorization, path);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshLog(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:refreshLog()";
        webView.evaluateJavascript(script, null);
    }

    public static void initDependence(WebView webView, String host, String authorization, String id) {
        if (webView == null) {
            return;
        }
        String script = String.format("javascript:initDependence('%1$s','%2$s','%3$s')", host, authorization, id);
        webView.evaluateJavascript(script, null);
    }

    public static void refreshDependence(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:refreshDependence()";
        webView.evaluateJavascript(script, null);
    }

    public static void initScript(WebView webView, String host, String authorization, String filename, String path) {
        if (webView == null) {
            return;
        }
        String script = String.format("javascript:initScript('%1$s','%2$s','%3$s','%4$s')", host, authorization, filename, path);
        webView.evaluateJavascript(script, null);
    }

    public static void saveScript(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:saveScript()";
        webView.evaluateJavascript(script, null);
    }

    public static void backScript(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:backScript()";
        webView.evaluateJavascript(script, null);
    }

    public static void refreshScript(WebView webView) {
        if (webView == null) {
            return;
        }
        String script = "javascript:refreshScript()";
        webView.evaluateJavascript(script, null);
    }

}
