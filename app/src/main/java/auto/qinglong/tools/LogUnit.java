package auto.qinglong.tools;

import android.util.Log;

public class LogUnit {
    final static String TAG = "QINGLONG";

    public static void log(Object msg) {
        Log.e(TAG, String.valueOf(msg));
    }


}
