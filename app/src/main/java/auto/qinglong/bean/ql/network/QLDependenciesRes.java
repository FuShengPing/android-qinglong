package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLDependence;

public class QLDependenciesRes extends QLBaseRes {
    private List<QLDependence> data;

    public List<QLDependence> getData() {
        return data;
    }

    public void setData(List<QLDependence> data) {
        this.data = data;
    }
}
