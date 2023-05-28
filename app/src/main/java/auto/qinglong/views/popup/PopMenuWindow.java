package auto.qinglong.views.popup;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PopMenuWindow {
    private List<PopMenuItem> items = new ArrayList<>();
    private OnActionListener onActionListener;
    private View targetView;
    private int gravity;

    public PopMenuWindow() {
    }

    public PopMenuWindow(View view, int gravity) {
        this.targetView = view;
        this.gravity = gravity;
    }

    public List<PopMenuItem> getItems() {
        return items;
    }

    public void addItems(List<PopMenuItem> items) {
        this.items = items;
    }

    public void addItem(PopMenuItem item) {
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
