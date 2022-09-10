package auto.qinglong.api.res;

import java.util.List;

import auto.qinglong.api.object.Environment;

public class DependenceRes {
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
