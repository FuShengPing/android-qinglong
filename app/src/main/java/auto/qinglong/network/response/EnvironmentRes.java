package auto.qinglong.network.response;

import java.util.List;

import auto.qinglong.activity.module.environment.QLEnvironment;

public class EnvironmentRes {
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
