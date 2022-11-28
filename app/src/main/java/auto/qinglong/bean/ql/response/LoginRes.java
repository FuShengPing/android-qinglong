package auto.qinglong.bean.ql.response;

import auto.qinglong.bean.ql.QLLoginData;

public class LoginRes {
    private int code;
    private QLLoginData data;
    private String message;

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

    public QLLoginData getData() {
        return data;
    }

    public void setData(QLLoginData data) {
        this.data = data;
    }
}
