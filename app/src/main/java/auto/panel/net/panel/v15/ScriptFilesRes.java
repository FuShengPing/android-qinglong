package auto.panel.net.panel.v15;

import java.util.List;

import auto.panel.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class ScriptFilesRes extends BaseRes {
    private List<FileObject> data;

    public List<FileObject> getData() {
        return data;
    }

    public void setData(List<FileObject> data) {
        this.data = data;
    }

    public static class FileObject {
        private double mtime;
        private String title;
        private boolean isLeaf;
        private List<FileObject> children;

        public boolean isDir() {
            return !isLeaf;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<FileObject> getChildren() {
            return children;
        }

        public void setChildren(List<FileObject> children) {
            this.children = children;
        }

        public double getMtime() {
            return mtime;
        }

        public void setMtime(double mtime) {
            this.mtime = mtime;
        }
    }
}
