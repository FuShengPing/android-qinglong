package auto.panel.bean.app;

import java.util.List;


/**
 * The type Version.
 */
public class Version {
    private String updateTime;
    private int versionCode;
    private String versionName;
    private int minVersionCode;
    private String minPanelVersion;
    private String downloadUrl;
    private List<String> updateDetail;

    public String getMinPanelVersion() {
        return minPanelVersion;
    }

    public void setMinPanelVersion(String minPanelVersion) {
        this.minPanelVersion = minPanelVersion;
    }

    /**
     * Gets version code.
     *
     * @return the version code
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * Sets version code.
     *
     * @param versionCode the version code
     */
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * Gets version name.
     *
     * @return the version name
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets version name.
     *
     * @param versionName the version name
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * Gets update time.
     *
     * @return the update time
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets update time.
     *
     * @param updateTime the update time
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Gets download url.
     *
     * @return the download url
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Sets download url.
     *
     * @param downloadUrl the download url
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * Gets update detail.
     *
     * @return the update detail
     */
    public List<String> getUpdateDetail() {
        return updateDetail;
    }

    /**
     * Sets update detail.
     *
     * @param updateDetail the update detail
     */
    public void setUpdateDetail(List<String> updateDetail) {
        this.updateDetail = updateDetail;
    }

    /**
     * Gets min version code.
     *
     * @return the min version code
     */
    public int getMinVersionCode() {
        return minVersionCode;
    }

    /**
     * Sets min version code.
     *
     * @param minVersionCode the min version code
     */
    public void setMinVersionCode(int minVersionCode) {
        this.minVersionCode = minVersionCode;
    }
}
