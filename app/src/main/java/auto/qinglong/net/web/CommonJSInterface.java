package auto.qinglong.net.web;

import android.webkit.JavascriptInterface;

import auto.base.util.LogUnit;
import auto.base.util.ToastUnit;

public class CommonJSInterface {
    public final static String TAG = "CommonJSInterface";

    @JavascriptInterface
    public void toast(String content) {
        ToastUnit.showShort(content);
    }

    @JavascriptInterface
    public void log(String content) {
        LogUnit.log(TAG, content);
    }
}
