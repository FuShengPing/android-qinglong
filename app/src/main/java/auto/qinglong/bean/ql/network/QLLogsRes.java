package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLLog;

public class QLLogsRes extends QLBaseRes {
    List<QLLog> dirs;

    public List<QLLog> getDirs() {
        return dirs;
    }

    public void setDirs(List<QLLog> dirs) {
        this.dirs = dirs;
    }


}
