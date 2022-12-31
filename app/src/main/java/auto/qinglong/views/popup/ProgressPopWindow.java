package auto.qinglong.views.popup;

import android.app.Activity;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ProgressPopWindow {
    private final Activity mActivity;
    private final PopupWindow ui_popupWindow;
    private final TextView ui_tip;

    ProgressPopWindow(Activity activity, PopupWindow popupWindow, TextView textView) {
        mActivity = activity;
        ui_popupWindow = popupWindow;
        ui_tip = textView;
    }

    public void setText(String text) {
        mActivity.runOnUiThread(() -> ui_tip.setText(text));
    }

    public void setTextAndShow(String text) {
        this.setText(text);
        ui_popupWindow.showAtLocation(mActivity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        if (ui_popupWindow.isShowing()) {
            ui_popupWindow.dismiss();
        }
    }
}
