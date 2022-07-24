package auto.qinglong.tools.web;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import auto.qinglong.tools.LogUnit;

public class WebJsManager {

    /**
     * 初始化编辑器
     *
     * @param mode 脚本语言
     */
    public static void initWeb(WebView webView, String mode) {
        if (webView == null) {
            return;
        }
        webView.evaluateJavascript("javascript:init('" + mode + "')", null);
    }

    /**
     * 设置编辑器是否可用编辑
     *
     * @param editable
     */
    public static void setEditable(WebView webView, boolean editable) {
        if (webView == null) {
            return;
        }

        if (editable) {
            webView.evaluateJavascript("javascript:setEditable(true)", null);
        } else {
            webView.evaluateJavascript("javascript:setEditable(false)", null);
        }
    }

    /**
     * 设置编辑器内容（代码)
     *
     * @param code 显示的代码
     */
    public static void setCode(WebView webView, String code) {
        if (webView == null) {
            return;
        }
        //必须替换字符 否则传递会错乱或不显示
        String content = code.replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        webView.evaluateJavascript("javascript:setCode('" + content + "')", null);
    }

    /**
     * 获取编辑器内容（代码)
     *
     * @param webView
     * @return 编辑器内容
     */
    public static void getCode(WebView webView, WebCallback webCallback) {
        if (webView == null && webCallback != null) {
            webCallback.onContent(null);
        }
        assert webView != null;
        webView.evaluateJavascript("javascript:getCode()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (webCallback != null) {
                    //必须替换字符 否则传递会错乱或不显示
                    String content = value.replace("\\'", "'")
                            .replace("\\\"", "\"")
                            .replace("\\n", "\n");

                    //去掉字符串两端“”符号
                    StringBuilder stringBuilder = new StringBuilder(content);
                    if (stringBuilder.charAt(0) == '\"') {
                        stringBuilder.replace(0, 1, "");
                    }
                    if (stringBuilder.charAt(stringBuilder.length() - 1) == '\"') {
                        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
                    }
                    webCallback.onContent(stringBuilder.toString());
                }
            }
        });
    }

    public interface WebCallback {
        void onContent(String content);
    }
}
