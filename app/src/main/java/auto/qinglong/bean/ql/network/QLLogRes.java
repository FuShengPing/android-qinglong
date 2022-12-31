package auto.qinglong.bean.ql.network;

import java.util.List;

import auto.qinglong.bean.ql.QLLog;

public class QLLogRes {
    int code;
    List<QLLog> dirs;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<QLLog> getDirs() {
        return dirs;
    }

    public void setDirs(List<QLLog> dirs) {
        this.dirs = dirs;
    }


}
