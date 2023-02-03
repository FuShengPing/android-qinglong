package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLLoginLog;

public class QLLoginLogRes extends QLBaseRes {
    private List<QLLoginLog> data;

    public List<QLLoginLog> getData() {
        return data;
    }

    public void setData(List<QLLoginLog> data) {
        this.data = data;
    }
}
