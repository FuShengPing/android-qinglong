package auto.panel.utils;

import android.util.Log;

public class LogUnit {
    private final static String TAG = "LogUnit";

    private final static boolean DEBUG = true;

    public static void log(Object msg) {
        if (DEBUG) {
            Log.e(TAG, String.valueOf(msg));
        }
    }

    public static void log(String tag, Object msg) {
        if (DEBUG) {
            Log.e(tag, String.valueOf(msg));
        }
    }

}
