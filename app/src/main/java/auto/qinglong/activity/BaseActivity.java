package auto.qinglong.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;

import auto.qinglong.tools.net.CallManager;

public abstract class BaseActivity extends AppCompatActivity {
    //弹窗
    public PopupWindow popupWindow;

    public Context myContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getBaseContext();
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

    @Override
    protected void onDestroy() {
        //关闭弹窗
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        //取消本页面的网络请求
        CallManager.cancelCall(getClass().getName());
        super.onDestroy();
    }

    protected String getClassName() {
        return getClass().getName();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 设置背景透明度 实现蒙层效果
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setAttributes(lp);
    }

    protected abstract void init();

    protected abstract void initWindow();
}
