package auto.qinglong.net.panel.v15;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class LogFileListRes extends BaseRes {
    private List<FileObject> data;

    public List<FileObject> getData() {
        return data;
    }

    public void setData(List<FileObject> data) {
        this.data = data;
    }

    public static class FileObject {
        private boolean isLeaf;
        private String title;
        private long mtime;
        private List<FileObject> children;

        public long getMtime() {
            return mtime;
        }

        public void setMtime(long mtime) {
            this.mtime = mtime;
        }


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
    }
}
