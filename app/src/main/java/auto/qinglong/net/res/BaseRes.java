package auto.qinglong.net.res;

public class BaseRes {
    private int code;//响应码
    private String message;//响应提示

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
}
