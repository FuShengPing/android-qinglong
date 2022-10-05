package auto.qinglong.net.res;

import auto.qinglong.module.environment.Environment;

public class EditEnvRes {
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

    public Environment getData() {
        return data;
    }

    public void setData(Environment data) {
        this.data = data;
    }

    private int code;
    private String message;
    private Environment data;
}
