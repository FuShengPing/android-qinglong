package auto.qinglong.bean.ql.network;

import auto.qinglong.bean.ql.QLLoginData;

public class QLLoginRes extends QLBaseRes {
    private QLLoginData data;

    public QLLoginData getData() {
        return data;
    }

    public void setData(QLLoginData data) {
        this.data = data;
    }
}
