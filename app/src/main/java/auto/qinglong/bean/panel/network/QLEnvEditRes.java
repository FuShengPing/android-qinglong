package auto.qinglong.bean.panel.network;

import auto.qinglong.bean.panel.QLEnvironment;
import auto.qinglong.net.panel.BaseRes;

public class QLEnvEditRes extends BaseRes {
    private QLEnvironment data;

    public QLEnvironment getData() {
        return data;
    }

    public void setData(QLEnvironment data) {
        this.data = data;
    }
}
