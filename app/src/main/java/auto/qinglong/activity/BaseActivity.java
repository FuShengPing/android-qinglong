package auto.qinglong.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.views.popup.PopupWindowManager;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";
    public Context mContext;
    protected PopupWindow popupWindowEdit;
    protected PopupWindow popupWindowConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
    }

    //设置字体大小不随系统变化
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return res;
    }

    protected String getClassName() {
        return getClass().getName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ToastUnit.cancel();
    }

    @Override
    protected void onDestroy() {
        //取消本页面的网络请求
        RequestManager.cancelCall(getClass().getName());
        super.onDestroy();
    }

    protected void init() {

    }

}
