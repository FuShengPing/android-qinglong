package auto.qinglong.activity.extension.web;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import auto.qinglong.database.db.WebRuleDBHelper;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WebUnit;
import auto.base.util.WindowUnit;
import auto.qinglong.views.WebViewBuilder;
import auto.qinglong.views.popup.PopConfirmWindow;
import auto.qinglong.views.popup.PopMenuItem;
import auto.qinglong.views.popup.PopMenuWindow;
import auto.qinglong.views.popup.PopupWindowBuilder;

public class PluginWebActivity extends BaseActivity {
    public static final String TAG = "PluginWebActivity";

    private CookieManager cookieManager;

    private ImageView ui_back;
    private ImageView ui_options;
    private EditText ui_et_url;
    private FrameLayout ui_webView_container;
    private WebView ui_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_web);

        ui_back = findViewById(R.id.action_bar_back);
        ui_options = findViewById(R.id.action_bar_options);
        ui_webView_container = findViewById(R.id.web_container);
        ui_et_url = findViewById(R.id.et_url);

        cookieManager = CookieManager.getInstance();

        init();
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (ui_webView != null && ui_webView.canGoBack()) {
            ui_webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_options.setOnClickListener(v -> {
            ui_et_url.clearFocus();
            WindowUnit.hideKeyboard(ui_et_url);
            showPopMenu(v);
        });

        //加载网页操作 同时清除所有cookies
        ui_et_url.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_GO) {
                return false;
            }

            cookieManager.removeAllCookies(null);
            String url = ui_et_url.getText().toString().trim();

            if (TextUnit.isFull(url)) {
                ui_et_url.clearFocus();
                WindowUnit.hideKeyboard(ui_et_url);
                ui_webView.loadUrl(url);
            } else {
                ToastUnit.showShort("请输入网页地址");
            }
            return true;
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
    }

    private void showPopWindowConfirm(String content) {
        //配置pop窗体信息
        PopConfirmWindow popConfirmWindow = new PopConfirmWindow();
        popConfirmWindow.setMaxHeight(WindowUnit.getWindowHeightPix(getBaseContext()) / 3);//限制最大高度
        popConfirmWindow.setConfirmTip("拷贝");
        popConfirmWindow.setCancelTip("取消");
        popConfirmWindow.setTitle("Cookies");
        popConfirmWindow.setContent(content);
        popConfirmWindow.setOnActionListener(isConfirm -> {
            if (isConfirm) {
                ClipboardManager clipboardManager = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
                ToastUnit.showShort("已复制到剪切板");
            }
            return true;
        });
        //构建并显示pop窗体
        PopupWindowBuilder.buildConfirmWindow(this, popConfirmWindow);
    }

    private void showPopMenu(View view) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(view,Gravity.END);
        popMenuWindow.addItem(new PopMenuItem("rule", "规则配置", R.drawable.ic_gray_mul_setting));
        popMenuWindow.addItem(new PopMenuItem("read_normal", "常规提取", R.drawable.ic_gray_crop_free));
        popMenuWindow.addItem(new PopMenuItem("read_rule", "规则提取", R.drawable.ic_gray_rule));
        popMenuWindow.addItem(new PopMenuItem("import", "导入变量", R.drawable.ic_gray_upload));

        popMenuWindow.setOnActionListener(key -> {
            switch (key) {
                case "rule":
                    Intent intent = new Intent(getBaseContext(), PluginWebRuleActivity.class);
                    startActivity(intent);
                    break;
                case "read_normal":
                    readNormal();
                    break;
                case "read_rule":
                    readRule();
                    break;
                default:
                    startImport();
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(this, popMenuWindow);
    }

    private void readNormal() {
        WindowUnit.hideKeyboard(ui_et_url);
        String cookies, url;
        url = ui_webView.getOriginalUrl();

        if (url != null && !url.isEmpty()) {
            cookies = cookieManager.getCookie(url);
            showPopWindowConfirm(cookies);
        } else {
            ToastUnit.showShort("请先加载网页");
        }

    }

    private void readRule() {
        WindowUnit.hideKeyboard(ui_et_url);
        String url = ui_webView.getOriginalUrl();
        if (TextUnit.isEmpty(url)) {
            ToastUnit.showShort("请先加载网页");
            return;
        }

        //删除URL参数
        url = url.split("\\?", 2)[0];
        //读取cookies
        String cookies = cookieManager.getCookie(url);

        //获取键值对
        Map<String, String> cks = WebUnit.parseCookies(cookies);
        //获取规则列表
        List<WebRule> rules = WebRuleDBHelper.getAll();
        //规则匹配 取第一个匹配成功规则
        for (WebRule rule : rules) {
            if (rule.match(url, cks)) {
                ToastUnit.showShort("匹配成功：" + rule.getName());
                showPopWindowConfirm(rule.getEnvValue());
                return;
            }
        }
        ToastUnit.showShort("无匹配规则");
    }

    private void startImport() {
        WindowUnit.hideKeyboard(ui_et_url);
        String url = ui_webView.getOriginalUrl();
        if (TextUnit.isEmpty(url)) {
            ToastUnit.showShort("请先加载网页");
            return;
        }

        //删除URL参数
        url = url.split("\\?", 2)[0];
        //读取cookies
        String cookies = cookieManager.getCookie(url);

        Map<String, String> cks = WebUnit.parseCookies(cookies);
        List<WebRule> rules = WebRuleDBHelper.getAll();
        for (WebRule rule : rules) {
            if (rule.match(url, cks)) {
                ToastUnit.showShort("匹配规则成功：" + rule.getName());
                netGetEnvironments(rule.buildObject());
                return;
            }
        }
        ToastUnit.showShort("匹配规则失败");
    }

    private void netGetEnvironments(QLEnvironment environment) {
        QLApiController.getEnvironments(getNetRequestID(), "", new QLApiController.NetGetEnvironmentsCallback() {
            @Override
            public void onSuccess(List<QLEnvironment> environments) {
                for (QLEnvironment qlEnvironment : environments) {
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
        QLApiController.updateEnvironment(getNetRequestID(), environment, new QLApiController.NetEditEnvCallback() {
            @Override
            public void onSuccess(QLEnvironment environment) {
                ToastUnit.showShort("导入成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("导入失败：" + msg);
            }
        });
    }

    public void netAddEnvironments(List<QLEnvironment> environments) {
        QLApiController.addEnvironment(getNetRequestID(), environments, new QLApiController.NetGetEnvironmentsCallback() {
            @Override
            public void onSuccess(List<QLEnvironment> qlEnvironments) {
                ToastUnit.showShort("导入成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("导入失败：" + msg);
            }
        });
    }


}