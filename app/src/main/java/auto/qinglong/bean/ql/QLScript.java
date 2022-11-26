package auto.qinglong.bean.ql;

import android.text.TextUtils;

import java.util.List;

public class QLScript implements Comparable<QLScript> {
    private float mtime;
    private String key;
    private String parent;
    private String title;
    private String value;
    private boolean disabled;
    private List<QLScript> children;

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
        if (TextUtils.isEmpty(parent)) {
            return "";
        } else {
            return parent;
        }
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

    public List<QLScript> getChildren() {
        return children;
    }

    public void setChildren(List<QLScript> children) {
        this.children = children;
    }

    @Override
    public int compareTo(QLScript o) {
        if (this.children != null && o.getChildren() != null) {
            return this.title.toLowerCase().compareTo(o.getTitle().toLowerCase());
        } else if (this.children != null && o.getChildren() == null) {
            return -1;
        } else if (this.children == null && o.getChildren() != null) {
            return 1;
        } else {
            return this.title.toLowerCase().compareTo(o.getTitle().toLowerCase());
        }
    }
}
