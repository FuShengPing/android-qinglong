package auto.qinglong.network.web;

import android.webkit.JavascriptInterface;

import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.ToastUnit;

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
