package auto.qinglong.network.response;

import auto.qinglong.network.object.SystemData;

public class SystemRes {
    private int code;
    private String message;
    private SystemData data;
    
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


}
