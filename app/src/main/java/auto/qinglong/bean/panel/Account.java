package auto.qinglong.bean.panel;

public class Account {
    private String username;
    private String password;
    private String address;
    private String token;
    private int state;
    //是否为当前账号
    private boolean isCurrent;

    public Account(String username, String password, String address, String token) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return 当前账号请求授权头
     */
    public String getAuthorization() {
        return "Bearer " + token;
    }

    /**
     * @return 当前账号的URL HOST
     */
    public String getBaseUrl() {
        return (address.startsWith("http") ? "" : "http://") + address + "/";
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
