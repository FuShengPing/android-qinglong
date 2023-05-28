package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLEnvironment;

public class QLEnvironmentRes extends QLBaseRes {
    private List<QLEnvironment> data;

    public List<QLEnvironment> getData() {
        return data;
    }

    public void setData(List<QLEnvironment> data) {
        this.data = data;
    }


}
