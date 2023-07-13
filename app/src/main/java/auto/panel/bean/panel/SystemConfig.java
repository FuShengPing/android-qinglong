package auto.panel.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.07.03
 */
public class SystemConfig {
    private int logRemoveFrequency;
    private int cronConcurrency;

    public int getCronConcurrency() {
        return cronConcurrency;
    }

    public void setCronConcurrency(int cronConcurrency) {
        this.cronConcurrency = cronConcurrency;
    }

    public int getLogRemoveFrequency() {
        return logRemoveFrequency;
    }

    public void setLogRemoveFrequency(int logRemoveFrequency) {
        this.logRemoveFrequency = logRemoveFrequency;
    }
}
