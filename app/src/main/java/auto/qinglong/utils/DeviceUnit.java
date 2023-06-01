package auto.qinglong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class DeviceUnit {
    public static final String TAG = "DeviceUnit";

    /**
     * 获取设备号.
     *
     * @param activity the activity
     * @return the android id
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Activity activity) {
        String id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id == null ? "" : id;
    }

    /**
     * 调用系统选择，分享文本
     *
     * @param context
     * @param text
     */
    public static void shareText(Context context, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share text using"));
    }
}
