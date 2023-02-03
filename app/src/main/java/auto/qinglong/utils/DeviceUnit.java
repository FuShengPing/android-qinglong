package auto.qinglong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    public static void shareText(Activity activity, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        activity.startActivity(intent);
    }
}
