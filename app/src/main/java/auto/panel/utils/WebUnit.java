package auto.panel.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class WebUnit {
    public static final String TAG = "WebUnit";

    /**
     * 通过手机浏览器打开指定网页.
     *
     * @param url the url
     */
    public static void open(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
