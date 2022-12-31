package auto.qinglong.bean.ql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QLEnvironment implements Comparable<QLEnvironment> {
    public static final String TAG = "QLEnvironment";

    private String _id;
    private int status;
    private String name;
    private float position;
    private long created;
    private String remarks = "";
    private String timestamp;
    private String value;
    //自定义序号 同变量名区分用
    private int index = -1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(QLEnvironment o) {
        return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public static List<QLEnvironment> parseExport(String str, String remarks) {
        List<QLEnvironment> qlEnvironments = new ArrayList<>();
        Pattern pattern = Pattern.compile("export \\w+=\"[^\"]+\"");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            QLEnvironment qlEnvironment = new QLEnvironment();
            String[] ss = matcher.group().split("=", 2);
            String name = ss[0].split(" ", 2)[1];
            String value = ss[1].replace("\"", "");

            qlEnvironment.setName(name);
            qlEnvironment.setValue(value);
            qlEnvironment.setRemarks(remarks);
            qlEnvironments.add(qlEnvironment);
        }
        return qlEnvironments;
    }
}
