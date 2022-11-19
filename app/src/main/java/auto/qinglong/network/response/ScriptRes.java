package auto.qinglong.network.response;

import java.util.List;

import auto.qinglong.activity.module.script.QLScript;

public class ScriptRes {
    private int code;
    private List<QLScript> data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<QLScript> getData() {
        return data;
    }

    public void setData(List<QLScript> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
