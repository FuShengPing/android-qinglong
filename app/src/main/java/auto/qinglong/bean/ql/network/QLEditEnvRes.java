package auto.qinglong.bean.ql.network;

import auto.qinglong.bean.ql.QLEnvironment;

public class QLEditEnvRes {
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

    public QLEnvironment getData() {
        return data;
    }

    public void setData(QLEnvironment data) {
        this.data = data;
    }

    private int code;
    private String message;
    private QLEnvironment data;
}