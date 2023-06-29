package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLTask;

public class QLTasksRes extends BaseRes {
    private List<QLTask> data;

    public List<QLTask> getData() {
        return data;
    }

    public void setData(List<QLTask> data) {
        this.data = data;
    }
}
