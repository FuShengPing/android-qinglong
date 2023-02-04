package auto.qinglong.bean.ql.network;

import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.bean.ql.QLLogRemove;

public class QLLogRemoveRes extends BaseRes {
    private QLLogRemove data;

    public QLLogRemove getData() {
        return data;
    }

    public void setData(QLLogRemove data) {
        this.data = data;
    }
}
