package auto.qinglong.bean.ql.response;

import java.util.List;

import auto.qinglong.bean.ql.QLDependence;

public class DependenceRes {
    private int code;
    private String message;
    private List<QLDependence> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<QLDependence> getData() {
        return data;
    }

    public void setData(List<QLDependence> data) {
        this.data = data;
    }
}
