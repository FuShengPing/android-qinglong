package auto.qinglong.bean.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import auto.base.util.TimeUnit;

public class QLEnvironment implements Comparable<QLEnvironment> {
    /* 接口字段 */
    private String _id;
    private int status;
    private String name;
    private float position;
    private long created;
    private String remarks;
    private String timestamp;
    private String value;
    /* 自定义字段 */
    private int index = -1;
    private int realIndex;
    private String mFormatName;
    private String mFormatCreated;

    public int getStatus() {
        return status;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRealIndex() {
        return realIndex;
    }

    public void setRealIndex(int realIndex) {
        this.realIndex = realIndex;
    }

    public String getFormatName() {
        if (mFormatName == null) {
            mFormatName = String.format(Locale.CHINA, "[%d] %s", index, name);
        }
        return mFormatName;
    }

    public void resetFormatName() {
        this.mFormatName = null;
    }

    public String getFormatCreated() {
        if (mFormatCreated == null) {
            mFormatCreated = TimeUnit.formatDatetimeA(created);
        }
        return mFormatCreated;
    }

    @Override
    public int compareTo(QLEnvironment o) {
        return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public static List<QLEnvironment> parse(String str, String remarks) {
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
