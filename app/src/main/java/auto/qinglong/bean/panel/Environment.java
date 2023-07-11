package auto.qinglong.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.07.11
 */
public class Environment implements Comparable<Environment> {
    public static final int STATUS_ENABLE = 0;
    public static final int STATUS_DISABLE = 1;

    private Object key;
    private String name;
    private String value;
    private String remark;
    private String status;
    private int statusCode;
    private String time;
    private double position;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;

        if (statusCode == STATUS_ENABLE) {
            this.status = "已启用";
        } else {
            this.status = "已禁用";
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public int compareTo(Environment o) {
        return 0;
    }
}
