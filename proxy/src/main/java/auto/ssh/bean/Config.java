package auto.ssh.bean;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class Config {
    private String localAddress;
    private int localPort;
    private String remoteAddress;
    private int remotePort;
    private String remoteUsername;
    private String remotePassword;
    private String remoteForwardAddress;
    private int remoteForwardPort;

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteUsername() {
        return remoteUsername;
    }

    public void setRemoteUsername(String remoteUsername) {
        this.remoteUsername = remoteUsername;
    }

    public String getRemotePassword() {
        return remotePassword;
    }

    public void setRemotePassword(String remotePassword) {
        this.remotePassword = remotePassword;
    }

    public String getRemoteForwardAddress() {
        return remoteForwardAddress;
    }

    public void setRemoteForwardAddress(String remoteForwardAddress) {
        this.remoteForwardAddress = remoteForwardAddress;
    }

    public int getRemoteForwardPort() {
        return remoteForwardPort;
    }

    public void setRemoteForwardPort(int remoteForwardPort) {
        this.remoteForwardPort = remoteForwardPort;
    }
}
