package auto.qinglong.bean.panel.network;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.bean.panel.QLDependence;

public class QLDependenciesRes extends BaseRes {
    private List<QLDependence> data;

    public List<QLDependence> getData() {
        return data;
    }

    public void setData(List<QLDependence> data) {
        this.data = data;
    }
}
