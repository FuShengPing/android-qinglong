package auto.ssh.ui.popup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class SelectPopup {
    private List<SelectItem> items;
    private OnSelectListener selectListener;
    private OnDismissListener dismissListener;

    public SelectPopup() {
        this.items = new ArrayList<>();
    }

    public void addItem(SelectItem item) {
        this.items.add(item);
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }

    public OnSelectListener getSelectListener() {
        return selectListener;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public OnDismissListener getDismissListener() {
        return dismissListener;
    }

    public void setDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface OnSelectListener {
        boolean onSelect(Object value);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
