package auto.panel.bean.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return this.name.compareTo(o.getName());
    }

    public static List<Environment> parse(String str, String remarks) {
        List<Environment> environments = new ArrayList<>();
        Pattern pattern = Pattern.compile("export \\w+=\"[^\"]+\"");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            Environment environment = new Environment();
            String[] ss = matcher.group().split("=", 2);
            String name = ss[0].split(" ", 2)[1];
            String value = ss[1].replace("\"", "");

            environment.setName(name);
            environment.setValue(value);
            environment.setRemark(remarks);
            environments.add(environment);
        }
        return environments;
    }
}
