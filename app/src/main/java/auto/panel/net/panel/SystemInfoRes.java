package auto.panel.net.panel;

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

        public String getVersion() {
            return version;
        }
    }
}



