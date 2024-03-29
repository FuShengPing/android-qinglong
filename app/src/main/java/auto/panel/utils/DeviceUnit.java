package auto.panel.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
     * @param activity
     * @param text
     */
    public static void shareText(Activity activity, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    /**
     * 复制文本到剪贴板
     *
     * @param context
     * @param text
     */

    public static void copyText(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
    }
}
