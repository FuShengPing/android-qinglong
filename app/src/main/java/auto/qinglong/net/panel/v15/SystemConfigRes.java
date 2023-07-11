package auto.qinglong.net.panel.v15;

import auto.qinglong.net.panel.BaseRes;

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

    public static class DataObject{
        private SystemConfigObject info;

        public SystemConfigObject getInfo() {
            return info;
        }

        public void setInfo(SystemConfigObject info) {
            this.info = info;
        }
    }

    public static class SystemConfigObject{
        private int logRemoveFrequency;
        private int cronConcurrency;

        public int getLogRemoveFrequency() {
            return logRemoveFrequency;
        }

        public void setLogRemoveFrequency(int logRemoveFrequency) {
            this.logRemoveFrequency = logRemoveFrequency;
        }

        public int getCronConcurrency() {
            return cronConcurrency;
        }

        public void setCronConcurrency(int cronConcurrency) {
            this.cronConcurrency = cronConcurrency;
        }
    }
}
