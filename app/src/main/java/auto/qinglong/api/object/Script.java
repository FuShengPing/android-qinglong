package auto.qinglong.api.object;

import java.util.List;

public class Script {
    private float mtime;
    private String key;
    private String parent;
    private String title;
    private String value;
    private boolean disabled;
    private List<Script> children;

    public float getMtime() {
        return mtime;
    }

    public void setMtime(float mtime) {
        this.mtime = mtime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<Script> getChildren() {
        return children;
    }

    public void setChildren(List<Script> children) {
        this.children = children;
    }

    public String getUrl() {
        if (parent != null && !parent.isEmpty()) {
            return "api/scripts/" + title + "?path=" + parent;
        } else {
            return "api/scripts/" + title + "?path=";
        }
    }

}
