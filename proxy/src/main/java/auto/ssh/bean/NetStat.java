package auto.ssh.bean;

/**
 * @author wsfsp4
 * @version 2023.06.20
 */
public class NetStat {
    private String proto;
    private String recvQ;
    private String sendQ;
    private String localAddress;
    private String foreignAddress;
    private String state;
    private int pid;
    private String program;
    private String name;

    public NetStat() {

    }

    public NetStat(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        String[] params = str.split("\\s+");

        this.proto = params[0];
        this.recvQ = params[1];
        this.sendQ = params[2];
        this.localAddress = params[3];
        this.foreignAddress = params[4];
        this.state = params[5];
        this.pid = Integer.parseInt(params[6].split("/")[0]);
        this.program = params[6].split("/")[1];
        this.name = params.length >= 8 ? params[7] : "";
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getRecvQ() {
        return recvQ;
    }

    public void setRecvQ(String recvQ) {
        this.recvQ = recvQ;
    }

    public String getSendQ() {
        return sendQ;
    }

    public void setSendQ(String sendQ) {
        this.sendQ = sendQ;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getForeignAddress() {
        return foreignAddress;
    }

    public void setForeignAddress(String foreignAddress) {
        this.foreignAddress = foreignAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
