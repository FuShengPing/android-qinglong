package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLScript;

public class QLScriptsRes extends BaseRes {
    private List<QLScript> data;

    public List<QLScript> getData() {
        return data;
    }

    public void setData(List<QLScript> data) {
        this.data = data;
    }
}
