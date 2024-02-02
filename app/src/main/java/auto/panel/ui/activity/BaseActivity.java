package auto.panel.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;

import auto.panel.MyApplication;
import auto.panel.net.NetManager;
import auto.panel.utils.ActivityUtils;
import auto.panel.utils.ToastUnit;

public abstract class BaseActivity extends auto.base.BaseActivity {
    public static final String TAG = "BaseActivity";
    protected Activity mActivity;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(MyApplication.getInstance());
        mContext = getBaseContext();
        mActivity = this;
        ActivityUtils.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ToastUnit.cancel();
    }

    @Override
    protected void onDestroy() {
        ActivityUtils.removeActivity(this);
        NetManager.cancelAllCall(getClass().getName());
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, event)) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], right = left + v.getWidth(), bottom = top + v.getHeight();
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

    protected String getNetRequestID() {
        return getClass().getName();
    }

    protected void init() {

    }

}
