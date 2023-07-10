package auto.qinglong.net.panel.v10;

import auto.qinglong.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class SystemConfigRes extends BaseRes {
    private SystemConfigObject data;

    public SystemConfigObject getData() {
        return data;
    }

    public void setData(SystemConfigObject data) {
        this.data = data;
    }

    public static class SystemConfigObject {
        private int frequency;

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}


