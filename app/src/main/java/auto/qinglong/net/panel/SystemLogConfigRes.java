package auto.qinglong.net.panel;

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

    public static class SystemLogConfigObject {
        private int frequency;

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}


