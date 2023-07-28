package auto.base.ui.popup;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MenuPopupWindow {
    private List<MenuPopupObject> items = new ArrayList<>();
    private OnActionListener onActionListener;
    private View targetView;
    private int gravity;

    public MenuPopupWindow() {
    }

    public MenuPopupWindow(View view, int gravity) {
        this.targetView = view;
        this.gravity = gravity;
    }

    public List<MenuPopupObject> getItems() {
        return items;
    }

    public void addItems(List<MenuPopupObject> items) {
        this.items = items;
    }

    public void addItem(MenuPopupObject item) {
        this.items.add(item);
    }

    public OnActionListener getOnActionListener() {
        return onActionListener;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public View getTargetView() {
        return targetView;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public interface OnActionListener {
        boolean onClick(String key);
    }


}
