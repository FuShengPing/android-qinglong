package auto.qinglong.net;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class SystemLogConfigRes extends BaseRes {
    private SystemLogConfigObject data;

    public SystemLogConfigObject getData() {
        return data;
    }

    public void setData(SystemLogConfigObject data) {
        this.data = data;
    }
}

class SystemLogConfigObject{
    private int frequency;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
