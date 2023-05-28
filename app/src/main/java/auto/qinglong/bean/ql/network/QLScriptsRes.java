package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLScript;

public class QLScriptsRes extends QLBaseRes {
    private List<QLScript> data;

    public List<QLScript> getData() {
        return data;
    }

    public void setData(List<QLScript> data) {
        this.data = data;
    }
}
