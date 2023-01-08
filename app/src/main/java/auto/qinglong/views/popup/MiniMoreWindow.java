package auto.qinglong.views.popup;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MiniMoreWindow {
    private List<MiniMoreItem> items = new ArrayList<>();
    private OnActionListener onActionListener;
    private View targetView;
    private int gravity;

    public List<MiniMoreItem> getItems() {
        return items;
    }

    public void setItems(List<MiniMoreItem> items) {
        this.items = items;
    }

    public void addItem(MiniMoreItem item) {
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
