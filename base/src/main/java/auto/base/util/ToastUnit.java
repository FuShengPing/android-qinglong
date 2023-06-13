package auto.base.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import auto.base.BaseApplication;


public class ToastUnit {
    public static final String TAG = "ToastUnit";
    private static Toast mToast = null;
    private static final Handler mHandler;

    static {
        mHandler = new Handler(Looper.myLooper());
    }

    public static void showShort(String content) {
        mHandler.post(() -> {
            cancel();
            mToast = Toast.makeText(BaseApplication.getContext(), content, Toast.LENGTH_SHORT);
            mToast.show();
        });
    }

    public static void showShort(Object content) {
        mHandler.post(() -> {
            cancel();
            mToast = Toast.makeText(BaseApplication.getContext(), String.valueOf(content), Toast.LENGTH_SHORT);
            mToast.show();
        });
    }

    /**
     * Cancel.
     * 取消toast
     */
    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast.setView(null);
            mToast = null;
        }
    }

}
