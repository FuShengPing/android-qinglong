package auto.qinglong.network.response;

import java.util.List;

import auto.qinglong.activity.module.task.QLTask;

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

    /**
     * @return 排序：运行>顶置>空闲>禁止
     */
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
