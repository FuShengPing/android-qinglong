package auto.qinglong.bean.app;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import auto.qinglong.utils.TextUnit;

public class WebRule {
    private int id;
    private String envName;
    private String name;
    private String url;
    private String target;
    private String main;
    private boolean isChecked;
    private String envValue;

    public WebRule() {

    }

    public WebRule(int id, String name, String url, String target, String main, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.target = target;
        this.main = main;
        this.isChecked = isChecked;
    }

    public WebRule(int id, String envName, String name, String url, String target, String main, boolean isChecked) {
        this.id = id;
        this.envName = envName;
        this.name = name;
        this.url = url;
        this.target = target;
        this.main = main;
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

    public String buildObject(String cookies, Map<String, String> ckMap) {
        return null;
    }

    public boolean match(String url, Map<String, String> ckMap) {
        boolean flag = url.contains(this.url) && TextUnit.isFull(ckMap.get(this.main));
        if (!flag) {
            return false;
        }

//        Map<String, String> finalMap = new HashMap<>();
//        String[] tgs = this.target.split(";");
//        if(this.target.equals("*")
//        for(String tg:tgs){
//
//        }

        return false;
    }

    @NonNull
    public String toString() {
        return String.format("%s：%s %s %s", this.name, this.url, this.target, this.main);
    }
}
