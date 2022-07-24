package auto.qinglong.api.res;

import java.util.List;

import auto.qinglong.api.object.Script;

public class ScriptRes {
    private int code;
    private List<Script> data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Script> getData() {
        return data;
    }

    public void setData(List<Script> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
