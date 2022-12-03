package auto.qinglong.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import auto.qinglong.MyApplication;

public class ToastUnit {
    public static final String TAG = "ToastUnit";
    private static Toast mToast = null;
    private static View mView = null;
    private static Handler mHandler;

    static {
        mHandler = new Handler(Looper.myLooper());
    }

    public static void showShort(Context context, String content) {
        showShort(content);
    }

    public static void showShort(String content) {

        mHandler.post(() -> {
            if (mToast == null) {
                mToast = Toast.makeText(MyApplication.getContext(), content, Toast.LENGTH_SHORT);
                mView = mToast.getView();
            } else {
                mToast.setView(mView);
                mToast.setText(content);
            }
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
        }
    }

}
