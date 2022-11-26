package auto.qinglong.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import auto.qinglong.MyApplication;

public class ToastUnit {
    private static Toast shortToast = null;
    private static Toast longToast = null;

    public static void showShort(Context context,String str) {
        cancel();
        boolean flag = false;
        if (Looper.myLooper() == null) {
            Looper.prepare();
            flag = true;
        }

        if (shortToast == null) {
            shortToast = Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_SHORT);
        }

        shortToast.setText(str);
        shortToast.show();
        shortToast = null;

        if (flag) {
            Looper.loop();
        }

    }

    public static void showShort(String str) {
        cancel();
        boolean flag = false;
        if (Looper.myLooper() == null) {
            Looper.prepare();
            flag = true;
        }

        if (shortToast == null) {
            shortToast = Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_SHORT);
        }

        shortToast.setText(str);
        shortToast.show();
        shortToast = null;

        if (flag) {
            Looper.loop();
        }

    }

    public static void showLong(Context context,String str) {
        boolean flag = false;
        if (Looper.myLooper() == null) {
            Looper.prepare();
            flag = true;
        }

        if (longToast == null) {
            longToast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        }
        longToast.setText(str);
        longToast.show();
        longToast = null;

        if (flag) {
            Looper.loop();
        }
    }

    /**
     * Cancel.
     * 取消toast
     */
    public static void cancel() {
        if (shortToast != null) {
            shortToast.cancel();
        }

        if (longToast != null) {
            longToast.cancel();
        }
    }

}
