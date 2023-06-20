package auto.ssh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    Button ui_button;
    EditText ui_edit;
    TextView ui_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ui_button = findViewById(R.id.main_button);
        ui_edit = findViewById(R.id.main_edit);
        ui_text = findViewById(R.id.main_text);

        new Thread(() -> {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SSH:ProxyKeepAlive");
        }).start();

        Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
        intent.putExtra(ProxyService.EXTRA_PORT, ProxyService.DEFAULT_PORT);
        startService(intent);

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

        ui_button.setOnClickListener(v -> {
            String cmd = ui_edit.getText().toString().trim();

            new Thread(() -> {
                try {
                    if (!ssh.isConnected()) {
                        return;
                    }

                    Session session = ssh.startSession();

                    Session.Command command = session.exec(cmd);

                    command.join(3, TimeUnit.SECONDS);

                    String result = IOUtils.readFully(command.getInputStream()).toString();

                    runOnUiThread(() -> ui_text.setText(result));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}