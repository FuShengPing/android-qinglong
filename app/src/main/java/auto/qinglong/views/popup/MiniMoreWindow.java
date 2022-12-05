package auto.qinglong.views.popup;

import java.util.ArrayList;
import java.util.List;

public class MiniMoreWindow {
    private List<MiniMoreItem> items = new ArrayList<>();
    private OnActionListener onActionListener;

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

    public interface OnActionListener {
        boolean onClick(String key);
    }


}
