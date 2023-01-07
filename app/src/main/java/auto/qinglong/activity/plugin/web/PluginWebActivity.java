package auto.qinglong.activity.plugin.web;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.ql.QLEnvironment;
import auto.qinglong.bean.ql.network.QLEnvironmentRes;
import auto.qinglong.database.db.WebRuleDBHelper;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WebUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.WebViewBuilder;
import auto.qinglong.views.popup.ConfirmWindow;
import auto.qinglong.views.popup.PopupWindowManager;

public class PluginWebActivity extends BaseActivity {
    public static final String TAG = "PluginWebActivity";

    private String urlLoaded = "";
    private CookieManager cookieManager;

    private ImageView ui_bar_back;
    private ImageView ui_bar_rule;
    private FrameLayout ui_webView_container;
    private WebView ui_webView;
    private EditText ui_et_url;
    private Button ui_bt_load;
    private Button ui_bt_read;
    private Button ui_bt_read_rule;
    private Button ui_bt_import;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_web);

        ui_bar_back = findViewById(R.id.common_bar_back);
        ui_bar_rule = findViewById(R.id.bar_rule);
        ui_webView_container = findViewById(R.id.web_container);
        ui_et_url = findViewById(R.id.et_url);
        ui_bt_load = findViewById(R.id.bt_load);
        ui_bt_read = findViewById(R.id.bt_read);
        ui_bt_read_rule = findViewById(R.id.bt_read_rule);
        ui_bt_import = findViewById(R.id.bt_import);

        cookieManager = CookieManager.getInstance();

        init();
    }

    @Override
    protected void init() {
        ui_bar_back.setOnClickListener(v -> finish());

        //规则列表界面
        ui_bar_rule.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PluginWebRuleActivity.class);
            startActivity(intent);
        });

        //加载网页操作 同时清除所有cookies
        ui_bt_load.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            cookieManager.removeAllCookies(null);
            String url = ui_et_url.getText().toString().trim();

            if (TextUnit.isFull(url)) {
                urlLoaded = url;
                ui_webView.loadUrl(url);
            } else {
                ToastUnit.showShort("请输入网页地址");
            }
        });

        //常规读取Cookies操作
        ui_bt_read.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            String cookies, url;
            url = ui_webView.getOriginalUrl();

            if (url != null && !url.isEmpty()) {
                cookies = cookieManager.getCookie(url);
            } else {
                ToastUnit.showShort("请先加载网页");
                return;
            }
            showCookies(cookies);

        });

        //规则读取Cookies操作
        ui_bt_read_rule.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            String url = ui_webView.getOriginalUrl();
            if (TextUnit.isFull(url)) {
                //删除URL参数
                url = url.split("\\?", 2)[0];
                //读取cookies
                String cookies = cookieManager.getCookie(url);
                //开始读取变量流程
                startRead(urlLoaded, cookies);
            } else {
                ToastUnit.showShort("请先加载网页");
            }

        });

        //导入变量操作
        ui_bt_import.setOnClickListener(v -> {
            WindowUnit.hideKeyboard(ui_bt_load);
            String url = ui_webView.getOriginalUrl();
            if (TextUnit.isFull(url)) {
                //删除URL参数
                url = url.split("\\?", 2)[0];
                //读取cookies
                String cookies = cookieManager.getCookie(url);
                //开始导入变量流程
                startImport(urlLoaded, cookies);
            } else {
                ToastUnit.showShort("请先加载网页");
            }
        });

        //构建web控件
        ui_webView = WebViewBuilder.build(getBaseContext(), ui_webView_container, new WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return !url.startsWith("https") && !url.startsWith("http");
            }
        }, null);
        ui_webView.setBackgroundColor(getColor(R.color.bg_gray));
    }

    private void startRead(String url, String cookies) {
        //获取键值对
        Map<String, String> cks = WebUnit.parseCookies(cookies);
        //获取规则列表
        List<WebRule> rules = WebRuleDBHelper.getAllWebRule();
        //规则匹配 取第一个匹配成功规则
        for (WebRule rule : rules) {
            if (rule.match(url, cks)) {
                ToastUnit.showShort("匹配规则成功：" + rule.getName());
                showCookies(rule.getEnvValue());
                return;
            }
        }
        ToastUnit.showShort("匹配规则失败");
    }

    private void startImport(String url, String cookies) {
        Map<String, String> cks = WebUnit.parseCookies(cookies);
        List<WebRule> rules = WebRuleDBHelper.getAllWebRule();
        for (WebRule rule : rules) {
            if (rule.match(url, cks)) {
                ToastUnit.showShort("匹配规则成功：" + rule.getName());
                netGetEnvironments(rule.buildObject());
                return;
            }
        }
        ToastUnit.showShort("匹配规则失败");
    }

    private void showCookies(String cookies) {
        //配置pop窗体信息
        ConfirmWindow confirmWindow = new ConfirmWindow();
        confirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix() / 3);//限制最大高度
        confirmWindow.setConfirmTip("拷贝");
        confirmWindow.setCancelTip("取消");
        confirmWindow.setTitle("Cookies");
        confirmWindow.setContent(cookies);
        confirmWindow.setConfirmInterface(isConfirm -> {
            if (isConfirm) {
                ClipboardManager clipboardManager = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, cookies));
                ToastUnit.showShort("已复制到剪切板");
            }
            return true;
        });
        //构建并显示pop窗体
        PopupWindowManager.buildConfirmWindow(this, confirmWindow);
    }

    private void netGetEnvironments(QLEnvironment environment) {
        QLApiController.getEnvironments(getNetRequestID(), "", new QLApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(QLEnvironmentRes res) {
                List<QLEnvironment> qlEnvironments = res.getData();
                for (QLEnvironment qlEnvironment : qlEnvironments) {
                    if (environment.getName().equals(qlEnvironment.getName()) && environment.getRemarks().equals(qlEnvironment.getRemarks())) {
                        qlEnvironment.setValue(environment.getValue());
                        netUpdateEnvironment(qlEnvironment);
                        return;
                    }
                }
                List<QLEnvironment> envList = new ArrayList<>();
                envList.add(environment);
                netAddEnvironments(envList);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("获取变量列表失败：" + msg);
            }
        });
    }

    public void netUpdateEnvironment(QLEnvironment environment) {
        QLApiController.updateEnvironment(getNetRequestID(), environment, new QLApiController.EditEnvCallback() {
            @Override
            public void onSuccess(QLEnvironment data) {
                ToastUnit.showShort("导入成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("导入失败：" + msg);
            }
        });
    }

    public void netAddEnvironments(List<QLEnvironment> environments) {
        QLApiController.addEnvironment(getNetRequestID(), environments, new QLApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(QLEnvironmentRes res) {
                ToastUnit.showShort("导入成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("导入失败：" + msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }
}