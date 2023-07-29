package auto.ssh.bean;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class SettingParams {
    private boolean serviceWakeup;
    private int serviceRefreshInterval;
    private int logLevel;
    private int logDeleteFrequency;

    public boolean isServiceWakeup() {
        return serviceWakeup;
    }

    public void setServiceWakeup(boolean serviceWakeup) {
        this.serviceWakeup = serviceWakeup;
    }

    public int getServiceRefreshInterval() {
        return serviceRefreshInterval;
    }

    public void setServiceRefreshInterval(int serviceRefreshInterval) {
        this.serviceRefreshInterval = serviceRefreshInterval;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public int getLogDeleteFrequency() {
        return logDeleteFrequency;
    }

    public void setLogDeleteFrequency(int logDeleteFrequency) {
        this.logDeleteFrequency = logDeleteFrequency;
    }
}
