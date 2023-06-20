package auto.ssh;

import java.security.PublicKey;
import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.06.20
 */
public class HostKeyVerifier implements net.schmizz.sshj.transport.verification.HostKeyVerifier {
    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        return true;
    }

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
        return null;
    }
}
