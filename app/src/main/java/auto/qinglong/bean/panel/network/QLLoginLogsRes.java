package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLLoginLog;

public class QLLoginLogsRes extends BaseRes {
    private List<QLLoginLog> data;

    public List<QLLoginLog> getData() {
        return data;
    }

    public void setData(List<QLLoginLog> data) {
        this.data = data;
    }
}
