package auto.base.ui.popup;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MenuPopupWindow {
    private List<MenuItem> items = new ArrayList<>();
    private OnActionListener onActionListener;
    private View targetView;

    public MenuPopupWindow() {
    }

    public MenuPopupWindow(View view) {
        this.targetView = view;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void addItems(List<MenuItem> items) {
        this.items = items;
    }

    public void addItem(MenuItem item) {
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

    public interface OnActionListener {
        boolean onClick(String key);
    }


}
