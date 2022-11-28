package auto.qinglong.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import auto.qinglong.MyApplication;

public class ToastUnit {
    public static final String TAG = "ToastUnit";
    private static Toast shortToast = null;

    public static void showShort(Context context, String content) {
        showShort(content);
    }

    public static void showShort(String content) {
        boolean flag = false;
        if (Looper.myLooper() == null) {
            Looper.prepare();
            flag = true;
        }

        if (shortToast != null) {
            shortToast.cancel();
            shortToast.setView(null);
        }
        shortToast = Toast.makeText(MyApplication.getContext(), content, Toast.LENGTH_SHORT);
        shortToast.setText(content);
        shortToast.show();

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
    }

}
