package auto.qinglong.bean.ql;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class QLLog implements Comparable<QLLog> {
    /* 接口属性 */
    private boolean isDir;
    private String name;
    private String parentName;
    private List<String> files;

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<QLLog> getChildren() {
        List<QLLog> QLLogs = new ArrayList<>();
        for (String name : this.files) {
            QLLog QLLog = new QLLog();
            QLLog.setDir(false);
            QLLog.setName(name);
            QLLog.setParentName(this.name);
            QLLogs.add(QLLog);
        }
        return QLLogs;
    }

    public String getLogPath() {
        if (TextUtils.isEmpty(parentName)) {
            return "api/logs/" + name;
        } else {
            return "api/logs/" + parentName + "/" + name;
        }
    }

    @Override
    public int compareTo(QLLog o) {
        if (this.isDir && o.isDir()) {
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        } else if (this.isDir && !o.isDir()) {
            return -1;
        } else if (!this.isDir && o.isDir()) {
            return 1;
        } else {
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        }
    }
}
