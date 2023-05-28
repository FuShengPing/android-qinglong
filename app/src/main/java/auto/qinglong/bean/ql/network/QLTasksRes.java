package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLTask;

public class QLTasksRes extends QLBaseRes {
    private List<QLTask> data;

    public List<QLTask> getData() {
        return data;
    }

    public void setData(List<QLTask> data) {
        this.data = data;
    }
}
