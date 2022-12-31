package auto.qinglong.bean.app.network;

import java.util.List;

import auto.qinglong.bean.app.WebRule;

public class WebRuleRes {
    private int code;
    private String msg;
    private List<WebRule> rules;

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

    public List<WebRule> getRules() {
        return rules;
    }

    public void setRules(List<WebRule> rules) {
        this.rules = rules;
    }
}
