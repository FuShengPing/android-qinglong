package auto.qinglong.api.res;

import auto.qinglong.api.object.SystemData;

public class SystemRes {
    private int code;

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

    public SystemData getData() {
        return data;
    }

    public void setData(SystemData data) {
        this.data = data;
    }

    private String message;
    private SystemData data;
}
