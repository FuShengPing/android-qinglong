package auto.panel.net.panel;

import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.07.10
 */
public class LoginLogsRes extends  BaseRes{
    private List<LoginLogObject> data;

    public List<LoginLogObject> getData() {
        return data;
    }

    public void setData(List<LoginLogObject> data) {
        this.data = data;
    }

    public static class LoginLogObject{
        private String address;
        private String ip;
        private String platform;
        private int status;
        private long timestamp;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
