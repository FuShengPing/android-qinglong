package auto.ssh;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import auto.base.BaseApplication;
import auto.base.util.Logger;
import auto.ssh.data.SettingPrefence;

/**
 * @author wsfsp4
 * @version 2023.06.20
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        // Confirm that positioning this provider at the end works for your needs!
        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());

        // 日志
        Logger.setLevel(SettingPrefence.getLogLevel());
    }
}
