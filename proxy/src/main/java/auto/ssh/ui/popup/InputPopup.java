package auto.ssh.ui.popup;

import android.text.InputType;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class InputPopup {
    private String title;
    private String hint;
    private String value;
    private int type;
    private int length;
    private boolean nullable;
    private OnActionListener actionListener;
    private OnDismissListener dismissListener;

    public InputPopup() {
    }

    public InputPopup(String title, String hint, String value) {
        this.title = title;
        this.hint = hint;
        this.value = value;
        this.type = InputType.TYPE_CLASS_TEXT;
        this.length = Integer.MAX_VALUE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
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
        boolean onConfirm(String value);

        default boolean onCancel() {
            return true;
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
