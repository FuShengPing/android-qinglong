package auto.qinglong.tools;

import android.util.Log;

public class LogUnit {
    final static String TAG = "QINGLONG";

    public static void log(String str){
        Log.e(TAG,str);
    }

    public static void log(int str){
        Log.e(TAG,String.valueOf(str));
    }

    public static void log(long str){
        Log.e(TAG,String.valueOf(str));
    }

    public static void log(float str){
        Log.e(TAG,String.valueOf(str));
    }

}
