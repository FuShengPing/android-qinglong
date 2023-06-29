package auto.qinglong.net.panel.v10;

import java.util.List;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class TaskRes extends BaseRes {
    List<TaskObject> data;

    public List<TaskObject> getData() {
        return data;
    }

    public void setData(List<TaskObject> data) {
        this.data = data;
    }
}

class TaskObject {
    private String _id;
    private String name;
    private String command;
    private String schedule;
    private String saved;
    private String timestamp;
    private int status;
    private int isSystem;
    private String pid;
    private int isDisabled;
    private int isPinned;
    private String log_path;
    private int last_running_time;
    private long last_execution_time;
    private String createdAt;
    private String updatedAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(int isDisabled) {
        this.isDisabled = isDisabled;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
        this.isPinned = isPinned;
    }

    public String getLog_path() {
        return log_path;
    }

    public void setLog_path(String log_path) {
        this.log_path = log_path;
    }

    public int getLast_running_time() {
        return last_running_time;
    }

    public void setLast_running_time(int last_running_time) {
        this.last_running_time = last_running_time;
    }

    public long getLast_execution_time() {
        return last_execution_time;
    }

    public void setLast_execution_time(long last_execution_time) {
        this.last_execution_time = last_execution_time;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}