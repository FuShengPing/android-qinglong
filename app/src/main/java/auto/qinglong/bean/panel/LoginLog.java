package auto.qinglong.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.07.10
 */
public class LoginLog {
    public final static int STATUS_SUCCESS = 0;
    public final static int STATUS_FAILURE = 1;

    private String address;
    private String ip;
    private String platform;
    private String status;
    private int statusCode;
    private String time;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        if (statusCode == STATUS_FAILURE) {
            this.status = "失败";
        } else {
            this.status = "成功";
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
