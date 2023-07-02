package auto.qinglong.net.panel;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class LoginRes extends BaseRes {
    private LoginResultObject data;

    public LoginResultObject getData() {
        return data;
    }

    public void setData(LoginResultObject data) {
        this.data = data;
    }

    public static class LoginResultObject {
        private String token;
        private String lastip;
        private String lastaddr;
        private String platform;
        private long lastlogon;
        private int retries;

        public String getToken() {
            return token;
        }
    }
}


