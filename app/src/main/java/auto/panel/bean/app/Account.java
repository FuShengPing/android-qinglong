package auto.panel.bean.app;

/**
 * @author: ASman
 * @date: 2024/2/4
 * @description:
 */
public class Account {
    private String address;
    private String username;
    private String password;
    private String token;
    private String version;

    public Account(){

    }

    public Account(String address, String username, String password) {
        this.username = username;
        this.password = password;
        this.address = address;
    }

    public Account(String address, String username, String password,String token) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.token = token;
    }

    public Account(String address, String username, String password,String token,String version) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.token = token;
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
