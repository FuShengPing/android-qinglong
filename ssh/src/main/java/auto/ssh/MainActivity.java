package auto.ssh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
    private ImageView uiLocalImg;
    private TextView uiLocalTip;
    private CardView uiForward;
    private ImageView uiForwardImg;
    private TextView uiForwardTip;
    private View uiConfig;
    private View uiSetting;
    private View uiLog;
    private View uiHelp;

    private volatile int proxyState = ProxyService.STATE_CLOSE;
    private volatile int forwardState = ForwardService.STATE_CLOSE;

    private BroadcastReceiver proxyBroadcastReceiver;
    private BroadcastReceiver forwardBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowUnit.setStatusBarTextColor(this, false);

        setContentView(R.layout.activity_main);

        uiLocal = findViewById(R.id.proxy_local);
        uiLocalImg = findViewById(R.id.proxy_local_img);
        uiLocalTip = findViewById(R.id.proxy_local_tip);
        uiForward = findViewById(R.id.proxy_forward);
        uiForwardImg = findViewById(R.id.proxy_forward_img);
        uiForwardTip = findViewById(R.id.proxy_forward_tip);
        uiConfig = findViewById(R.id.proxy_config);
        uiSetting = findViewById(R.id.proxy_setting);
        uiLog = findViewById(R.id.proxy_log);
        uiHelp = findViewById(R.id.proxy_help);

        init();

        // 注册代理、转发状态变动广播
        IntentFilter proxyFilter = new IntentFilter(ProxyService.BROADCAST_ACTION_STATE);
        IntentFilter forwardFilter = new IntentFilter(ForwardService.BROADCAST_ACTION_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(forwardBroadcastReceiver, forwardFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(proxyBroadcastReceiver, proxyFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销代理状态变动广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(proxyBroadcastReceiver);
    }

    private void onProxyOpen() {
        this.proxyState = ProxyService.STATE_OPEN;
        this.uiLocal.setCardBackgroundColor(getResources().getColor(R.color.blue_deep, null));
        this.uiLocalImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiLocalTip.setText(R.string.proxy_local_close);
    }

    private void onProxyClose() {
        this.proxyState = ProxyService.STATE_CLOSE;
        this.uiLocal.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiLocalImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiLocalTip.setText(R.string.proxy_local_open);
    }

    private void onForwardOpen() {
        this.forwardState = ForwardService.STATE_OPEN;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.blue_deep, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_remove_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_disconnect);
    }

    private void onForwardClose() {
        this.forwardState = ForwardService.STATE_CLOSE;
        this.uiForward.setCardBackgroundColor(getResources().getColor(R.color.gray_80, null));
        this.uiForwardImg.setBackgroundResource(auto.base.R.drawable.ic_check_circle_outline_white);
        this.uiForwardTip.setText(R.string.proxy_forward_connect);
    }

    private void init() {
        proxyBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(ProxyService.EXTRA_STATE, ProxyService.STATE_CLOSE);
                if (state == ProxyService.STATE_OPEN) {
                    onProxyOpen();
                } else {
                    onProxyClose();
                }
            }
        };

        forwardBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(ForwardService.EXTRA_STATE, ForwardService.STATE_CLOSE);
                if (state == ForwardService.STATE_OPEN) {
                    onForwardOpen();
                } else {
                    onForwardClose();
                }
            }
        };

        uiLocal.setOnClickListener(v -> {
            Intent intent = new Intent(BaseApplication.getContext(), ProxyService.class);
            if (this.proxyState == ProxyService.STATE_CLOSE) {//开启服务
                startService(intent);
            } else {//结束服务
                stopService(intent);
            }
        });

        uiForward.setOnClickListener(v -> {
            Intent intent = new Intent(BaseApplication.getContext(), ForwardService.class);
            if (this.forwardState == ForwardService.STATE_CLOSE) {//开启服务
                intent.putExtra(ForwardService.EXTRA_HOSTNAME,"60.205.228.46");
                intent.putExtra(ForwardService.EXTRA_USERNAME,"root");
                intent.putExtra(ForwardService.EXTRA_PASSWORD,"aly@123456Fsp");
                intent.putExtra(ForwardService.EXTRA_REMOTE_ADDRESS,"0.0.0.0");
//                intent.putExtra(ForwardService.EXTRA_REMOTE_PORT,9100);
                intent.putExtra(ForwardService.EXTRA_LOCAL_ADDRESS,"0.0.0.0");
//                intent.putExtra(ForwardService.EXTRA_LOCAL_PORT,9100);
                startService(intent);
            } else {//结束服务
                stopService(intent);
            }
        });
    }

}