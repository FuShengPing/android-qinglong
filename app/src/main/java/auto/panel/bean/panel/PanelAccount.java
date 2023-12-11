package auto.panel.bean.panel;

public class PanelAccount {
    private String username;
    private String password;
    private String token;
    private String code;
    private String address;
    private int state;
    //是否为当前账号
    private boolean isCurrent;

    public PanelAccount(String username, String password, String address, String token) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return 当前账号请求授权头
     */
    public String getAuthorization() {
        return "Bearer " + token;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getBaseUrl() {
        // BaseUrl
        StringBuilder sb = new StringBuilder();
        if (!address.startsWith("http")) {
            sb.append("http://");
        }
        sb.append(address);
        if (!address.endsWith("/")) {
            sb.append("/");
        }
        return sb.toString();
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
