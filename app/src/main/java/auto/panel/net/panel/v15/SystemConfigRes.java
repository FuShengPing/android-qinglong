package auto.panel.net.panel.v15;

import auto.panel.net.panel.BaseRes;

/**
 * @author wsfsp4
 * @version 2023.07.10
 */
public class SystemConfigRes extends BaseRes {
    private DataObject data;

    public DataObject getData() {
        return data;
    }

    public void setData(DataObject data) {
        this.data = data;
    }

    public static class DataObject {
        private SystemConfigObject info;

        public SystemConfigObject getInfo() {
            return info;
        }
    }

    public static class SystemConfigObject {
        private int logRemoveFrequency;
        private int cronConcurrency;

        public int getLogRemoveFrequency() {
            return logRemoveFrequency;
        }

        public int getCronConcurrency() {
            return cronConcurrency;
        }
    }
}
