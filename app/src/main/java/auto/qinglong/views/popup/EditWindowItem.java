package auto.qinglong.views.popup;

public class EditWindowItem {
    private String key;
    private String value;
    private String label;
    private String hint;
    private boolean focusable = true;
    private boolean editable = true;

    public EditWindowItem() {

    }

    public EditWindowItem(String key, String value, String label, String hint) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.hint = hint;
    }

    public EditWindowItem(String key, String value, String label, String hint, boolean focusable, boolean editable) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.hint = hint;
        this.focusable = focusable;
        this.editable = editable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
