package auto.qinglong.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.07.08
 */
public class Dependence {
    public static final String TYPE_NODEJS = "nodejs";
    public static final String TYPE_PYTHON = "python3";
    public static final String TYPE_LINUX = "linux";

    public static int STATUS_INSTALLING = 0;
    public static int STATUS_INSTALLED = 1;
    public static int STATUS_INSTALL_FAILURE = 2;
    public static int STATUS_UNINSTALLING = 3;
    public static int STATUS_UNINSTALL_FAILURE = 5;
    public static int STATUS_UNKOWN = -1;

    private Object key;
    private String title;
    private String status;
    private int statusCode;
    private String createTime;
    private String type;
    private int typeCode;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;

        if (statusCode == Dependence.STATUS_INSTALLING) {
            this.status = "安装中";
        } else if (statusCode == Dependence.STATUS_INSTALLED) {
            this.status = "已安装";
        } else if (statusCode == Dependence.STATUS_INSTALL_FAILURE) {
            this.status = "安装失败";
        } else if (statusCode == Dependence.STATUS_UNINSTALLING) {
            this.status = "卸载中";
        } else if (statusCode == Dependence.STATUS_UNINSTALL_FAILURE) {
            this.status = "卸载失败";
        } else {
            this.status = "未知";
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (TYPE_NODEJS.equals(type)) {
            this.typeCode = 0;
        } else if (TYPE_PYTHON.equals(type)) {
            this.typeCode = 1;
        } else {
            this.typeCode = 2;
        }
    }

    public int getTypeCode() {
        return this.typeCode;
    }
}
