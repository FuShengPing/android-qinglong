package auto.qinglong.bean.ql;

import java.util.List;

import auto.base.util.TextUnit;
import auto.base.util.TimeUnit;

public class QLDependence {
    /*接口属性*/
    private String _id;
    private String name;
    private String remark;
    private String timestamp;//Date格式
    private List<String> log;
    private long created;//13位时间戳
    private int status;
    private int type;
    /*自定义属性*/
    private String mFormatCreated;//格式化的创建时间

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLogStr() {
        return TextUnit.join(this.log, "\n");
    }

    public String getFormatCreated() {
        if (mFormatCreated == null) {
            mFormatCreated = TimeUnit.formatTimeA(created);
        }
        return mFormatCreated;
    }
}
