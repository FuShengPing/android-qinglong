package auto.qinglong.api.object;

public class LoginData {
    private String token;
    private String lastip;
    private String lastaddr;
    private String platform;
    private long lastlogon;
    private int retries;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastip() {
        return lastip;
    }

    public void setLastip(String lastip) {
        this.lastip = lastip;
    }

    public String getLastaddr() {
        return lastaddr;
    }

    public void setLastaddr(String lastaddr) {
        this.lastaddr = lastaddr;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getLastlogon() {
        return lastlogon;
    }

    public void setLastlogon(long lastlogon) {
        this.lastlogon = lastlogon;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
