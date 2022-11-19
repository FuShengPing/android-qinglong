package auto.qinglong.network.response;

import auto.qinglong.activity.module.task.QLTask;

public class EditTaskRes {
    private int code;
    private String message;
    private QLTask data;

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

    public QLTask getData() {
        return data;
    }

    public void setData(QLTask data) {
        this.data = data;
    }


}
