package auto.qinglong.api.object;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Log {
    boolean isDir;
    String name;
    String parentName;
    List<String> files;

    public String getParentName() {
        return parentName;
    }

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

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<Log> getChildren() {
        List<Log> logs = new ArrayList<>();
        for (String name : this.files) {
            Log log = new Log();
            log.setDir(false);
            log.setName(name);
            log.setParentName(this.name);
            logs.add(log);
        }
        return logs;
    }

    public String getLogPath() {
        if (TextUtils.isEmpty(parentName)) {
            return "api/logs/" + name;
        } else {
            return "api/logs/" + parentName + "/" + name;
        }

    }
}
