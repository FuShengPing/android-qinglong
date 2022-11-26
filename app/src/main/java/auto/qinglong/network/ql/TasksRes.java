package auto.qinglong.network.ql;

import java.util.List;

import auto.qinglong.bean.ql.QLTask;

public class TasksRes {
    private int code;
    private String message;
    private List<QLTask> data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<QLTask> getData() {
        return data;
    }

    public void setData(List<QLTask> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
