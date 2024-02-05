package auto.panel.net.panel.v15;

import java.util.List;

import auto.panel.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class TasksRes extends BaseRes {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        List<TaskObject> data;
        int size;

        public List<TaskObject> getData() {
            return data;
        }

        public void setData(List<TaskObject> data) {
            this.data = data;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public static class TaskObject {
        private int id;
        private String name;
        private String command;
        private String schedule;
        private String saved;
        private String timestamp;
        private float status;
        private int isSystem;
        private String pid;
        private int isDisabled;
        private int isPinned;
        private String log_path;
        private long last_running_time;
        private long last_execution_time;
        private String createdAt;
        private String updatedAt;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCommand() {
            return command;
        }

        public String getSchedule() {
            return schedule;
        }

        public String getSaved() {
            return saved;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public float getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIsSystem() {
            return isSystem;
        }

        public String getPid() {
            return pid;
        }

        public int getIsDisabled() {
            return isDisabled;
        }

        public int getIsPinned() {
            return isPinned;
        }

        public String getLogPath() {
            return log_path;
        }

        public long getLast_running_time() {
            return last_running_time;
        }

        public long getLastExecutionTime() {
            return last_execution_time;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}


