package auto.qinglong.views.popup;

import android.app.Activity;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.TextView;

import auto.qinglong.utils.WindowUnit;

public class ProgressWindow {
    private final Activity mActivity;
    private final PopupWindow ui_popupWindow;
    private final TextView ui_tip;

    ProgressWindow(Activity activity, PopupWindow popupWindow, TextView textView) {
        mActivity = activity;
        ui_popupWindow = popupWindow;
        ui_tip = textView;
    }

    public void setText(String text) {
        mActivity.runOnUiThread(() -> ui_tip.setText(text));
    }

    public void setTextAndShow(String text) {
        if (this.ui_popupWindow.isShowing()) {
            this.setText(text);
        } else {
            mActivity.runOnUiThread(() -> {
                ui_tip.setText(text);
                WindowUnit.setBackgroundAlpha(mActivity, 0.5f);
                ui_popupWindow.showAtLocation(mActivity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
            });
        }
    }

    public boolean isShowing() {
        return ui_popupWindow != null && ui_popupWindow.isShowing();
    }

    public void dismiss() {
        if (ui_popupWindow != null && ui_popupWindow.isShowing()) {
            mActivity.runOnUiThread(ui_popupWindow::dismiss);
        }
    }
}
