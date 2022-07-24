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

import com.bumptech.glide.Glide;

import auto.qinglong.tools.CallManager;

public abstract class BaseActivity extends AppCompatActivity {
    //是否活跃
    public boolean isAlive;
    //弹窗处理
    public Handler popHandler;
    //进度处理
    public Handler progressHandler;
    //弹窗
    public PopupWindow popupWindow;

    public Context myContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive = true;
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
        isAlive = false;
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
        //停止Glide加载 防止界面销毁导致Glide异常
        Glide.with(this.getBaseContext()).pauseRequests();
        Glide.with(this).pauseRequests();

        super.onPause();

    }

    @Override
    protected void onResume() {
        // 重启Glide加载
        Glide.with(this.getBaseContext()).resumeRequests();
        Glide.with(this).resumeRequests();
        super.onResume();

    }

    /**
     * 设置背景透明度 实现蒙层效果
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setAttributes(lp);
    }

    protected abstract void initViewSetting();

    protected abstract void initWindow();
}
