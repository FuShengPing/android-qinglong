package auto.qinglong.bean.panel;

public class QLSystem {
    /* 接口属性 */
    private boolean isInitialized;
    private String version;
    /* 自定义属性 */
    private static String VERSION;

    public boolean isInitialized() {
        return isInitialized;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static String getStaticVersion() {
        return VERSION;
    }

    public static void setStaticVersion(String version) {
        VERSION = version;
    }
}
