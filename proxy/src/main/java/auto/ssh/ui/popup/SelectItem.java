package auto.ssh.ui.popup;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class SelectItem {
    private String title;
    private Object value;
    private boolean selected;

    public SelectItem() {

    }

    public SelectItem(String title, Object value, boolean selected) {
        this.title = title;
        this.value = value;
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
