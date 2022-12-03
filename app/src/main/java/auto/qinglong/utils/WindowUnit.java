package auto.qinglong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import auto.qinglong.MyApplication;

public class WindowUnit {
    /**
     * 隐藏虚拟键盘
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

    }

    /**
     * Get window height dp float.
     * 获取屏幕高度 dp
     */
    public static float getWindowHeightDp() {
        return px2dip(MyApplication.getContext().getResources().getDisplayMetrics().heightPixels);
    }

    /**
     * Gets window height pix.
     * 获取屏幕高度 pix
     */
    public static int getWindowHeightPix() {
        return MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Gets window height pix.
     * 获取屏幕高度 pix
     */
    public static int getWindowHeightPix(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度 pix
     */
    public static int getWindowWidthPix() {
        return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Gets status bar height.
     * 获取状态栏高度
     */
    @SuppressLint("InternalInsetResource")
    public static int getStatusBarHeight() {
        Resources resources = MyApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");//获取状态栏
        int height = resources.getDimensionPixelSize(resourceId);

        return height;
    }


    /**
     * Sets status bar text color.
     * 设置状态栏字体颜色
     *
     * @param activity the activity
     * @param isWhite  the is white
     */
    public static void setStatusBarTextColor(Activity activity, boolean isWhite) {
        if (isWhite) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//设置状态栏白色字体
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体黑色
        }
    }

    /**
     * Sets translucent status.
     * 设置透明状态栏
     */
    public static void setTranslucentStatus(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * Sets translucent navigation.
     * 设置透明导航栏，ps:唤醒导航栏不会上推界面
     */
    public static void setTranslucentNavigation(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    /**
     * Set navigation color.
     * 设置导航栏颜色
     */
    public static void setNavigationColor(Activity activity, int color) {
        activity.getWindow().setNavigationBarColor(color);
    }

    /**
     * 设置背景透明度 实现蒙层效果
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * Dip 2 px int.
     * dp转化成px
     */
    public static int dip2px(float dp) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Px 2 dip int.
     * px转化成dp @param px the px
     *
     * @param px the px
     * @return the int
     */
    public static float px2dip(int px) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }
}
