package auto.base.ui.popup;

import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditPopupWindow {
    private String title;
    private String cancelTip = "取消";
    private String confirmTip = "确定";
    private int maxHeight = 0;
    private PopupWindow popupWindow;
    private View view;
    private List<EditItem> items;
    private OnActionListener actionListener;

    public EditPopupWindow() {
        this.items = new ArrayList<>();
    }

    public EditPopupWindow(String title, String cancelTip, String confirmTip) {
        this.title = title;
        this.cancelTip = cancelTip;
        this.confirmTip = confirmTip;
        this.items = new ArrayList<>();
    }

    public void setItems(List<EditItem> items) {
        this.items = items;
    }

    public OnActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void addItem(EditItem item) {
        this.items.add(item);
    }

    public List<EditItem> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCancelTip() {
        return cancelTip;
    }

    public void setCancelTip(String cancelTip) {
        this.cancelTip = cancelTip;
    }

    public String getConfirmTip() {
        return confirmTip;
    }

    public void setConfirmTip(String confirmTip) {
        this.confirmTip = confirmTip;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public void dismiss() {
        if (this.popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public interface OnActionListener {
        boolean onConfirm(@NonNull Map<String, String> map);

        default boolean onCancel() {
            return true;
        }
    }
}
