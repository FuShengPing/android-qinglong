package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.bean.panel.QLEnvironment;
import auto.qinglong.net.panel.BaseRes;

public class QLEnvironmentRes extends BaseRes {
    private List<QLEnvironment> data;

    public List<QLEnvironment> getData() {
        return data;
    }

    public void setData(List<QLEnvironment> data) {
        this.data = data;
    }
}
