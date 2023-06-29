package auto.qinglong.bean.panel.network;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLLoginData;

public class QLLoginRes extends BaseRes {
    private QLLoginData data;

    public QLLoginData getData() {
        return data;
    }

    public void setData(QLLoginData data) {
        this.data = data;
    }
}
