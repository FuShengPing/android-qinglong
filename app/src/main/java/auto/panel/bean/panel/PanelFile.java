package auto.panel.bean.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class PanelFile implements Comparable<PanelFile> {
    private boolean isDir;
    private String title;
    private String createTime;
    private String content;
    private String parentPath;
    private String path;
    private List<PanelFile> children;

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<PanelFile> getChildren() {
        return children;
    }

    public void setChildren(List<PanelFile> children) {
        this.children = children;
    }

    @Override
    public int compareTo(PanelFile o) {
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
