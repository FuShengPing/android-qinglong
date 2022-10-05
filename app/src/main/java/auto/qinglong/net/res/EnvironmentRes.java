package auto.qinglong.net.res;

import java.util.List;

import auto.qinglong.module.environment.Environment;

public class EnvironmentRes {
    private int code;
    private String message;
    private List<Environment> data;

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

    public List<Environment> getData() {
        return data;
    }

    public void setData(List<Environment> data) {
        this.data = data;
    }


}
