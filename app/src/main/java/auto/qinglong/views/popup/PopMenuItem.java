package auto.qinglong.views.popup;

public class PopMenuItem {
    private String key;
    private String name;
    private int icon;

    public PopMenuItem(String key, String name, int icon) {
        this.key = key;
        this.name = name;
        this.icon = icon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
