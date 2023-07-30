package auto.panel.bean.app;

/**
 * @author wsfsp4
 * @version 2023.07.30
 */
public class Extensions {
    private Extension proxy;
    private Extension web;
    private Extension docker;

    public Extension getProxy() {
        return proxy;
    }

    public void setProxy(Extension proxy) {
        this.proxy = proxy;
    }

    public Extension getWeb() {
        return web;
    }

    public void setWeb(Extension web) {
        this.web = web;
    }

    public Extension getDocker() {
        return docker;
    }

    public void setDocker(Extension docker) {
        this.docker = docker;
    }
}
