package auto.panel.bean.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class PanelFile implements Comparable<PanelFile> {
    private boolean isDir;
    private String title;
    private int size = -1;
    private String time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<PanelFile> getChildren() {
        return children;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setChildren(List<PanelFile> children) {
        this.children = children;
    }

    /**
     * 文件夹优先，文件夹按字母排序，文件按字母倒序排序
     *
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(PanelFile o) {
        if (this.isDir && o.isDir()) {
            return this.title.toLowerCase().compareTo(o.title.toLowerCase());
        } else if (this.isDir && !o.isDir()) {
            return -1;
        } else if (!this.isDir && o.isDir()) {
            return 1;
        } else {
            return -this.title.toLowerCase().compareTo(o.title.toLowerCase());
        }
    }
}
