package auto.panel.ui.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import auto.panel.R;
import auto.panel.net.web.WebViewBuilder;
import auto.panel.utils.DeviceUnit;
import auto.panel.utils.ToastUnit;
import auto.panel.utils.thread.AppLogTask;
import auto.panel.utils.thread.ThreadPoolUtil;

public class MarkdownActivity extends BaseActivity {
    private static final String STATIC_FILE_HTML_PATH = "file:///android_asset/web/markdown/index.html";
    public static final String STATIC_FILE_DOCUMENT_PATH = "web/markdown/files/document.md";
    public static final String STATIC_FILE_VERSION_PATH = "web/markdown/files/version.md";

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_PATH = "EXTRA_PATH";

    private boolean init = false;
    private String mTitle;
    private String mPath;

    private ImageView uiBarBack;
    private TextView uiBarTitle;
    private ImageView uiBarRefresh;
    private FrameLayout uiWebContainer;
    private WebView uiWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mPath = getIntent().getStringExtra(EXTRA_PATH);

        uiWebContainer = findViewById(R.id.web_container);
        uiBarBack = findViewById(R.id.bar_nav_back);
        uiBarTitle = findViewById(R.id.bar_nav_title);
        uiBarRefresh = findViewById(R.id.bar_nav_refresh);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!init) {
            initWebView();
        }
    }

    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(uiWebView);
        uiWebView = null;
        super.onDestroy();
    }

    @Override
    protected void init() {
        //设置标题
        uiBarTitle.setText(mTitle);

        //返回监听
        uiBarBack.setOnClickListener(v -> finish());
    }

    protected void initWebView() {
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 返回true表示拦截该url，不再继续加载
                // 返回false表示允许WebView加载该url
                DeviceUnit.copyText(getBaseContext(), request.getUrl().toString());
                ToastUnit.showShort("已复制链接到剪贴板");
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadContent();
            }
        };
        uiWebView = WebViewBuilder.build(getBaseContext(), uiWebContainer, client, null);

        uiWebView.setFocusable(false);
        uiWebView.loadUrl(STATIC_FILE_HTML_PATH);
        init = true;
    }

    private void loadContent() {
        String content = readAssert(STATIC_FILE_VERSION_PATH);
        setContent(content);
    }

    private void setContent(String content) {
        if (uiWebView == null || content == null) {
            return;
        }
        try {
            content = URLEncoder.encode(content, "UTF-8").replaceAll("\\+", "%20");
            String script = String.format("javascript:setContent('%1$s')", content);
            uiWebView.evaluateJavascript(script, null);
        } catch (UnsupportedEncodingException e) {
            ToastUnit.showShort(e.getMessage());
        }
    }

    private String readAssert(String path) {
        // 获取AssetManager
        AssetManager assetManager = this.getAssets();

        try {
            // 打开文件
            InputStream inputStream = assetManager.open(path);

            // 从输入流中读取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // 关闭输入流
            inputStream.close();

            // 处理读取到的数据
            return stringBuilder.toString();
        } catch (IOException e) {
            ToastUnit.showShort(e.getMessage());
            ThreadPoolUtil.execute(new AppLogTask(e));
            e.printStackTrace();
            return null;
        }
    }


}