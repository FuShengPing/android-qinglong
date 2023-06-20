package auto.ssh;

import net.schmizz.sshj.SSHClient;

import java.io.IOException;

/**
 * @author wsfsp4
 * @version 2023.06.20
 */
public class MySSHClient {
    private final static SSHClient sshClient;

    static {
        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new HostKeyVerifier());
    }

    public static boolean connect(String hostname, int port) {
        try {
            sshClient.connect(hostname, port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
