package auto.qinglong.bean.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class LogFile implements Comparable<LogFile> {
    private boolean isDir;
    private String title;
    private String path;
    private String parent;
    private List<LogFile> children;

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<LogFile> getChildren() {
        return children;
    }

    public void setChildren(List<LogFile> children) {
        this.children = children;
    }

    @Override
    public int compareTo(LogFile o) {
        if (this.isDir && o.isDir()) {
            return this.title.toLowerCase().compareTo(o.title.toLowerCase());
        } else if (this.isDir && !o.isDir()) {
            return -1;
        } else if (!this.isDir && o.isDir()) {
            return 1;
        } else {
            return this.title.toLowerCase().compareTo(o.title.toLowerCase());
        }
    }
}
