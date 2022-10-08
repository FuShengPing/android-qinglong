package auto.qinglong.net.response;

import java.util.List;

import auto.qinglong.module.server.log.Log;

public class LogRes {
    int code;
    List<Log> dirs;
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

    public List<Log> getDirs() {
        return dirs;
    }

    public void setDirs(List<Log> dirs) {
        this.dirs = dirs;
    }


}
