package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLEnvironment;

public class QLEnvironmentRes {
    private int code;
    private String message;
    private List<QLEnvironment> data;

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

    public List<QLEnvironment> getData() {
        return data;
    }

    public void setData(List<QLEnvironment> data) {
        this.data = data;
    }


}
