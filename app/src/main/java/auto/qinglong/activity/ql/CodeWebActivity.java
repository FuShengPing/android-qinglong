package auto.qinglong.activity.ql;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.web.CommonJSInterface;
import auto.qinglong.network.web.QLWebJsManager;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.WebViewBuilder;

public class CodeWebActivity extends BaseActivity {
    public static final String TAG = "CodeWebActivity";

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_LOG_PATH = "logPath";
    public static final String EXTRA_SCRIPT_NAME = "scriptName";
    public static final String EXTRA_SCRIPT_PARENT = "scriptParent";
    public static final String EXTRA_CAN_REFRESH = "canRefresh";
    public static final String EXTRA_CAN_EDIT = "canEdit";
    public static final String TYPE_LOG = "log";
    public static final String TYPE_SCRIPT = "script";
    public static final String TYPE_DEPENDENCE = "dependence";

    private String title;
    private String type;
    private boolean canRefresh;
    private boolean canEdit;
    private String scriptName;
    private String scriptParent;
    private String logPath;

    private LinearLayout ui_nav_bar;
    private ImageView ui_back;
    private TextView ui_tip;
    private ImageView ui_edit;
    private ImageView ui_refresh;
    private LinearLayout ui_edit_bar;
    private ImageView ui_edit_back;
    private ImageView ui_edit_save;
    private LinearLayout ui_web_container;
    private WebView ui_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_web);

        title = getIntent().getStringExtra(EXTRA_TITLE);
        type = getIntent().getStringExtra(EXTRA_TYPE);
        canRefresh = getIntent().getBooleanExtra(EXTRA_CAN_REFRESH, true);
        canEdit = getIntent().getBooleanExtra(EXTRA_CAN_EDIT, false);
        scriptName = getIntent().getStringExtra(EXTRA_SCRIPT_NAME);
        scriptParent = getIntent().getStringExtra(EXTRA_SCRIPT_PARENT);
        logPath = getIntent().getStringExtra(EXTRA_LOG_PATH);

        ui_nav_bar = findViewById(R.id.script_bar);
        ui_back = findViewById(R.id.script_back);
        ui_tip = findViewById(R.id.script_name);
        ui_edit = findViewById(R.id.script_edit);
        ui_refresh = findViewById(R.id.script_refresh);
        ui_edit_bar = findViewById(R.id.script_edit_bar);
        ui_edit_back = findViewById(R.id.script_edit_back);
        ui_edit_save = findViewById(R.id.script_edit_save);
        ui_web_container = findViewById(R.id.web_container);

        init();
    }


    @Override
    protected void init() {
        //设置标题
        ui_tip.setText(title);

        //返回监听
        ui_back.setOnClickListener(v -> finish());

        //刷新监听
        if (canRefresh) {
            ui_refresh.setVisibility(View.VISIBLE);
            ui_refresh.setOnClickListener(v -> {
                //禁用点击
                ui_refresh.setEnabled(false);
                //开启动画
                Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ui_refresh.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animation.setDuration(1000);
                ui_refresh.startAnimation(animation);

                switch (type) {
                    case TYPE_SCRIPT:
                        QLWebJsManager.refreshScript(ui_webView);
                        break;
                    case TYPE_LOG:
                        QLWebJsManager.refreshLog(ui_webView);
                        break;
                    default:
                        break;
                }
            });
        }

        if (canEdit) {
            ui_edit.setVisibility(View.VISIBLE);
            ui_edit.setOnClickListener(v -> {
                ui_nav_bar.setVisibility(View.INVISIBLE);
                ui_edit_bar.setVisibility(View.VISIBLE);
                QLWebJsManager.setEditable(ui_webView, true);
            });

            ui_edit_back.setOnClickListener(v -> {
                ui_edit_bar.setVisibility(View.INVISIBLE);
                ui_nav_bar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(ui_webView);
                QLWebJsManager.setEditable(ui_webView, false);

                if (TYPE_SCRIPT.equals(type)) {
                    QLWebJsManager.backScript(ui_webView);
                }
            });

            ui_edit_save.setOnClickListener(v -> {
                if (TYPE_SCRIPT.equals(type)) {
                    QLWebJsManager.saveScript(ui_webView);
                }
            });
        }

        ui_webView = WebViewBuilder.build(getBaseContext(), ui_web_container, new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                switch (type) {
                    case TYPE_SCRIPT:
                        QLWebJsManager.initScript(ui_webView, Objects.requireNonNull(AccountSP.getCurrentAccount()).getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization(), scriptName, scriptParent);
                        break;
                    case TYPE_LOG:
                        QLWebJsManager.initLog(ui_webView, Objects.requireNonNull(AccountSP.getCurrentAccount()).getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization(), logPath);
                        break;
                    default:
                        break;
                }
            }
        }, new CommonJSInterface());
        //加载本地网页
        ui_webView.loadUrl("file:///android_asset/web/editor.html");

    }

    /**
     * 窗体销毁
     * 释放web编辑器
     */
    @Override
    protected void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }
}
