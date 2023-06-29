package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLLog;

public class QLLogsRes extends BaseRes {
    List<QLLog> dirs;

    public List<QLLog> getDirs() {
        return dirs;
    }

    public void setDirs(List<QLLog> dirs) {
        this.dirs = dirs;
    }


}
