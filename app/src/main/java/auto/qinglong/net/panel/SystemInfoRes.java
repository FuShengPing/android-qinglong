package auto.qinglong.net.panel;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class SystemInfoRes extends BaseRes {
    private SystemInfoObject data;

    public SystemInfoObject getData() {
        return data;
    }

    public void setData(SystemInfoObject data) {
        this.data = data;
    }

    public static class SystemInfoObject {
        private boolean isInitialized;
        private String version;

        public boolean isInitialized() {
            return isInitialized;
        }

        public void setInitialized(boolean initialized) {
            isInitialized = initialized;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}



