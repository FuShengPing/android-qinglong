package auto.qinglong.bean.ql.network;

import auto.qinglong.bean.ql.QLSystemData;

public class QLSystemRes {
    private int code;
    private String message;
    private QLSystemData data;
    
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

    public QLSystemData getData() {
        return data;
    }

    public void setData(QLSystemData data) {
        this.data = data;
    }


}
