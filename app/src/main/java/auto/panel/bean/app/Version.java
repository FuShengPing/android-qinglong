package auto.panel.bean.app;

import java.util.List;

public class Version {
    private int versionCode;
    private String versionName;
    private String updateTime;
    private String downloadUrl;
    private List<String> updateDetail;
    private boolean isForce;


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

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }
}
