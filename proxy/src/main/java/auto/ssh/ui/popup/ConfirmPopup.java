package auto.ssh.ui.popup;

/**
 * @author wsfsp4
 * @version 2023.07.30
 */
public class ConfirmPopup {
    private String title;
    private String content;
    private boolean cancel;
    private boolean confirm;
    private OnActionListener actionListener;
    private OnDismissListener dismissListener;

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

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public OnActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public OnDismissListener getDismissListener() {
        return dismissListener;
    }

    public void setDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface OnActionListener {
        boolean onConfirm();

        boolean onCancel();
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
