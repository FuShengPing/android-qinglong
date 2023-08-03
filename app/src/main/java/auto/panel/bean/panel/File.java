package auto.panel.bean.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class File implements Comparable<File> {
    private boolean isDir;
    private String title;
    private String content;
    private String parent;
    private String path;
    private String createTime;
    private List<File> children;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<File> getChildren() {
        return children;
    }

    public void setChildren(List<File> children) {
        this.children = children;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int compareTo(File o) {
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
