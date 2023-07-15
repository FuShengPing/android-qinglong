package auto.panel.bean.app;

import java.util.List;

public class Version {
    private int versionCode;
    private int minVersionCode;
    private String versionName;
    private String updateTime;
    private String downloadUrl;
    private List<String> updateDetail;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<String> getUpdateDetail() {
        return updateDetail;
    }

    public void setUpdateDetail(List<String> updateDetail) {
        this.updateDetail = updateDetail;
    }

    public int getMinVersionCode() {
        return minVersionCode;
    }

    public void setMinVersionCode(int minVersionCode) {
        this.minVersionCode = minVersionCode;
    }
}
