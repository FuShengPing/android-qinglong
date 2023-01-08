package auto.qinglong.views.popup;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditWindow {
    private String title;
    private String cancelTip = "取消";
    private String confirmTip = "确定";
    private int maxHeight = 0;
    private List<EditWindowItem> items;
    private OnActionListener actionListener;
    private View view;


    public EditWindow() {
        this.items = new ArrayList<>();
    }

    public EditWindow(String title, String cancelTip, String confirmTip) {
        this.title = title;
        this.cancelTip = cancelTip;
        this.confirmTip = confirmTip;
        this.items = new ArrayList<>();
    }

    public void setItems(List<EditWindowItem> items) {
        this.items = items;
    }

    public OnActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void addItem(EditWindowItem item) {
        this.items.add(item);
    }

    public List<EditWindowItem> getItems() {
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

    public interface OnActionListener {
        boolean onConfirm(Map<String, String> map);

        boolean onCancel();
    }
}
