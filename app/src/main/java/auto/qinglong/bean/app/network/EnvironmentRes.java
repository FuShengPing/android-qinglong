package auto.qinglong.bean.app.network;

import java.util.List;

import auto.qinglong.bean.ql.QLEnvironment;

public class EnvironmentRes {
    private int code;
    private String msg;
    private List<QLEnvironment> envs;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<QLEnvironment> getEnvs() {
        return envs;
    }

    public void setEnvs(List<QLEnvironment> envs) {
        this.envs = envs;
    }
}
