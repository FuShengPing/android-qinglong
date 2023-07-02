package auto.qinglong.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class SystemInfo {
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
