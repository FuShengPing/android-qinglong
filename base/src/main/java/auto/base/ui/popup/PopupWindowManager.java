package auto.base.ui.popup;

import android.widget.PopupWindow;

public class PopupWindowManager {

    public static boolean dismiss(PopupWindow popupWindow) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        } else {
            return false;
        }
    }
}
