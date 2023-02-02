package auto.qinglong.activity.ql.config;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.web.CommonJSInterface;
import auto.qinglong.network.web.QLWebJsManager;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.WebViewBuilder;

public class ConfigFragment extends BaseFragment {
    public static String TAG = "ConfigFragment";

    private MenuClickListener menuClickListener;

    private RelativeLayout ui_menu_bar;
    private ImageView ui_menu;
    private ImageView ui_edit;
    private ImageView ui_refresh;
    private RelativeLayout ui_edit_bar;
    private ImageView ui_edit_back;
    private ImageView ui_edit_save;
    private LinearLayout ui_web_container;
    private WebView ui_webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_config, null);

        ui_menu_bar = view.findViewById(R.id.config_menu_bar);
        ui_menu = view.findViewById(R.id.config_menu);
        ui_edit = view.findViewById(R.id.config_edit);
        ui_refresh = view.findViewById(R.id.config_refresh);
        ui_edit_bar = view.findViewById(R.id.config_edit_bar);
        ui_edit_back = view.findViewById(R.id.config_edit_back);
        ui_edit_save = view.findViewById(R.id.config_edit_save);
        ui_web_container = view.findViewById(R.id.web_container);

        init();

        return view;
    }

    @Override
    protected void init() {
        ui_menu.setOnClickListener(v -> menuClickListener.onMenuClick());

        //进入编辑状态
        ui_edit.setOnClickListener(v -> {
            ui_menu_bar.setVisibility(View.INVISIBLE);
            ui_edit_bar.setVisibility(View.VISIBLE);
            QLWebJsManager.setEditable(ui_webView, true);
        });

        //退出编辑状态
        ui_edit_back.setOnClickListener(v -> {
            ui_edit_bar.setVisibility(View.GONE);
            ui_menu_bar.setVisibility(View.VISIBLE);
            WindowUnit.hideKeyboard(ui_webView);
            QLWebJsManager.setEditable(ui_webView, false);
            QLWebJsManager.backConfig(ui_webView);
        });

        //刷新
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
            QLWebJsManager.refreshConfig(ui_webView);
        });

        //保存编辑
        ui_edit_save.setOnClickListener(v -> QLWebJsManager.saveConfig(ui_webView));

        //初始化webView
        ui_webView = WebViewBuilder.build(getContext(), ui_web_container, new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                QLWebJsManager.initConfig(ui_webView, AccountSP.getCurrentAccount().getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization());
            }
        }, new CommonJSInterface());
        //加载本地网页
        ui_webView.loadUrl("file:///android_asset/web/editor.html");
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public void onDestroy() {
        WebViewBuilder.destroy(ui_webView);
        super.onDestroy();
    }

    private void netGetConfig() {
        QLApiController.getConfigDetail(getNetRequestID(), new QLApiController.NetConfigCallback() {
            @Override
            public void onSuccess(String content) {
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
