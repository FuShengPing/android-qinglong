package auto.qinglong.network.web;

import android.webkit.JavascriptInterface;

import auto.qinglong.utils.ToastUnit;

public class CommonJSInterface {
    @JavascriptInterface
    public void toast(String content) {
        ToastUnit.showShort(content);
    }
}
