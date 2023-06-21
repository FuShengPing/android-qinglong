package auto.ssh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import auto.base.BaseApplication;
import auto.base.util.LogUnit;
import auto.base.util.WindowUnit;

public class MainActivity extends AppCompatActivity {
    private CardView uiLocal;
    private CardView uiRemote;
    private View uiConfig;
    private View uiSetting;
    private View uiLog;
    private View uiHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowUnit.setStatusBarTextColor(this, false);

        setContentView(R.layout.activity_main);

        uiLocal = findViewById(R.id.proxy_local);
        uiRemote = findViewById(R.id.proxy_remote);
        uiConfig = findViewById(R.id.proxy_config);
        uiSetting = findViewById(R.id.proxy_setting);
        uiLog = findViewById(R.id.proxy_log);
        uiHelp = findViewById(R.id.proxy_help);

        init();
    }

    private void init() {
        uiLocal.setOnClickListener(v -> {
            LogUnit.log("startProxyService in");
            startProxyService();
        });

        uiRemote.setOnClickListener(v -> {
            LogUnit.log("startSSHSerVice in");
            startSSHSerVice();
        });
    }

    private void startProxyService() {
        Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
        startService(intent);
    }

    private void startSSHSerVice() {
        SSHClient ssh = new SSHClient();

        ssh.addHostKeyVerifier(new HostKeyVerifier());

        new Thread(() -> {
            try {
                // 连接远程服务
                ssh.connect("60.205.228.46", 22);
                // 设置登录用户名和密码
                ssh.authPassword("root", "aly@123456Fsp");
                // 设置保活间隔
                ssh.getConnection().getKeepAlive().setKeepAliveInterval(5);

                Session session = ssh.startSession();

                Session.Command command = session.exec("netstat -tunlp -t -l | grep 9100");

                command.join(3, TimeUnit.SECONDS);

                String result = IOUtils.readFully(command.getInputStream()).toString();

                LogUnit.log("status：" + command.getExitStatus());
                LogUnit.log("result：" + result);

                if (!result.isEmpty()) {
                    NetStat netStat = new NetStat(result);
                    String[] params = result.split("\\s+");
                    LogUnit.log(Arrays.toString(params));
                    LogUnit.log("端口已被占用：" + netStat.getPid());

                    session = ssh.startSession();

                    session.exec("kill -9 " + netStat.getPid()).join(3, TimeUnit.SECONDS);
                }

                RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward("0.0.0.0", 9100);
                SocketForwardingConnectListener connectListener = new SocketForwardingConnectListener(new InetSocketAddress("0.0.0.0", 9100));

                // 新建端口转发
                ssh.getRemotePortForwarder().bind(forward, connectListener);
                // 线程阻塞
                ssh.getTransport().join();

                LogUnit.log("RemotePortForward End");

                ssh.disconnect();
            } catch (IOException e) {
                LogUnit.log("RemotePortForward IOException");
                e.printStackTrace();
            }
        }).start();
    }


}