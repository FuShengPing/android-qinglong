package auto.qinglong.bean.app;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto.qinglong.bean.panel.QLEnvironment;
import auto.base.util.TextUnit;
import auto.qinglong.utils.WebUnit;

public class WebRule {
    private int id;
    private String envName;
    private String name;
    private String url;
    private String target;
    private String main;
    private String joinChar;
    private boolean isChecked;
    private String envValue;
    private String mainValue;

    public WebRule() {

    }

    public WebRule(int id, String envName, String name, String url, String target, String main, String joinChar, boolean isChecked) {
        this.id = id;
        this.envName = envName;
        this.name = name;
        this.url = url;
        this.target = target;
        this.main = main;
        this.joinChar = joinChar;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getJoinChar() {
        return joinChar;
    }

    public void setJoinChar(String joinChar) {
        this.joinChar = joinChar;
    }

    public String getEnvValue() {
        return envValue;
    }

    public QLEnvironment buildObject() {
        QLEnvironment qlEnvironment = new QLEnvironment();
        qlEnvironment.setName(this.envName);
        qlEnvironment.setValue(this.envValue);
        qlEnvironment.setRemarks(this.mainValue);
        return qlEnvironment;
    }

    public boolean match(String url, Map<String, String> ckMap) {
        boolean flag = url.contains(this.url) && TextUnit.isFull(ckMap.get(this.main));
        if (!flag) {
            return false;
        }
        //主键
        this.mainValue = ckMap.get(this.main);

        //*
        if (this.target.equals("*")) {
            this.envValue = WebUnit.joinMap(ckMap, this.joinChar);
            return true;
        }
        //*;keyA=>keyB
        if (this.target.matches("\\*;((\\w+>>\\w+);?)+")) {
            String[] keys = this.target.split(";");
            for (String key : keys) {
                if (!key.equals("*")) {
                    String fromKey = key.split(">>")[0];
                    String toKey = key.split(">>")[1];
                    if (ckMap.get(fromKey) == null) {
                        return false;
                    } else {
                        ckMap.put(toKey, ckMap.get(fromKey));
                        ckMap.remove(fromKey);
                    }
                }
            }
            this.envValue = WebUnit.joinMap(ckMap, this.joinChar);
            return true;
        }
        //keyA=;[keyA=>keyB]
        if (this.target.matches("(((\\w+=)|(\\w+>>\\w+=));?)+")) {
            Map<String, String> targetMap = new HashMap<>();
            String[] keys = this.target.split(";");
            for (String key : keys) {
                if (key.contains(">>")) {
                    String fromKey = key.split(">>")[0];
                    String toKey = key.split(">>")[1].replace("=", "");
                    if (ckMap.get(fromKey) == null) {
                        return false;
                    } else {
                        targetMap.put(toKey, ckMap.get(fromKey));
                    }
                } else {
                    String k = key.replace("=", "");
                    if (TextUnit.isEmpty(ckMap.get(k))) {
                        return false;
                    } else {
                        targetMap.put(k, ckMap.get(k));
                    }
                }
            }
            this.envValue = WebUnit.joinMap(targetMap, this.joinChar);
            return true;
        }
        //keyA
        if (this.target.matches("(\\w+;?)+")) {
            List<String> targetValue = new ArrayList<>();
            String[] keys = this.target.split(";");
            for (String key : keys) {
                if (ckMap.get(key) == null) {
                    return false;
                } else {
                    targetValue.add(ckMap.get(key));
                }
            }
            this.envValue = TextUnit.join(targetValue, this.joinChar);
            return true;
        }
        return false;
    }

    public boolean isValid() {
        return TextUnit.isFull(name) && TextUnit.isFull(url) && TextUnit.isFull(main)
                && TextUnit.isFull(target) && isTargetValid(target)
                && TextUnit.isFull(envName) && envName.matches("\\w+")
                && TextUnit.isFull(joinChar) && joinChar.matches("[;&%#@]");
    }

    public static boolean isTargetValid(String target) {
        return target.equals("*")
                || target.matches("\\*;((\\w+>>\\w+);?)+")
                || target.matches("(((\\w+=)|(\\w+>>\\w+=));?)+")
                || target.matches("(\\w+;?)+");
    }


    @NonNull
    public String toString() {
        return String.format("%s：%s %s %s", this.name, this.url, this.target, this.main);
    }
}
