package auto.qinglong.network.response;

import auto.qinglong.activity.service.task.Task;

public class EditTaskRes {
    private int code;
    private String message;
    private Task data;

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

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
    }


}
