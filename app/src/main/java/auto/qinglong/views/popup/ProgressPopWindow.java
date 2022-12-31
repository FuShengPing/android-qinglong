package auto.qinglong.views.popup;

import android.app.Activity;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ProgressPopWindow {
    private Activity mActivity;
    private PopupWindow ui_popupWindow;
    private TextView ui_tip;

    ProgressPopWindow(Activity activity, PopupWindow popupWindow, TextView textView) {
        mActivity = activity;
        ui_popupWindow = popupWindow;
        ui_tip = textView;

    }

    public void setText(String text) {
        mActivity.runOnUiThread(() -> ui_tip.setText(text));
    }

    public void destroy() {
        if (ui_popupWindow.isShowing()) {
            ui_popupWindow.dismiss();
        }
        ui_popupWindow = null;
        ui_tip = null;
        mActivity = null;
    }
}
