package auto.base.ui.popup;


public class ConfirmPopupWindow {
    private String title;
    private String content;
    private String cancelTip = "取消";
    private String confirmTip = "确定";
    private boolean isFocusable = true;
    private int maxHeight = 0;

    private OnActionListener onActionListener;

    public ConfirmPopupWindow() {

    }

    public ConfirmPopupWindow(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ConfirmPopupWindow(String title, String content, String cancelTip, String confirmTip) {
        this.title = title;
        this.content = content;
        this.cancelTip = cancelTip;
        this.confirmTip = confirmTip;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OnActionListener getOnActionListener() {
        return onActionListener;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;
    }

    public interface OnActionListener {
        boolean onConfirm();

        default boolean onCancel() {
            return true;
        }
    }
}
