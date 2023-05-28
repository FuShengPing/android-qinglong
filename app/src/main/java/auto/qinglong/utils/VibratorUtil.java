package auto.qinglong.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import auto.qinglong.database.sp.SettingSP;

/**
 * @author wsfsp4
 * @version 2023.03.02
 */
public class VibratorUtil {
    public static final int VIBRATE_SHORT = 100;
    public static final int VIBRATE_LONG = 150;
    private static Vibrator vibrator;

    /**
     * 震动反馈
     *
     * @param context      上下文
     * @param milliseconds 持续时间
     */
    public static void vibrate(Context context, long milliseconds) {
        if (SettingSP.isVibrate()) {
            if (vibrator == null) {
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
            VibrationEffect effect = VibrationEffect.createOneShot(milliseconds, 1);
            vibrator.vibrate(effect);
        }
    }
}
