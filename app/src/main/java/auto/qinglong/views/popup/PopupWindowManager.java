package auto.qinglong.views.popup;

import android.widget.PopupWindow;

public class PopupWindowManager {

    public static void dismiss(PopupWindow popupWindow) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
