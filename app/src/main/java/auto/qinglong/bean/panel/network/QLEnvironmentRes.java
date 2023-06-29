package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLEnvironment;

public class QLEnvironmentRes extends BaseRes {
    private List<QLEnvironment> data;

    public List<QLEnvironment> getData() {
        return data;
    }

    public void setData(List<QLEnvironment> data) {
        this.data = data;
    }


}
