package auto.qinglong.module.config;

import android.annotation.SuppressLint;
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

import auto.qinglong.MyApplication;
import auto.qinglong.R;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.module.BaseFragment;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.net.WebJsManager;

public class ConfigFragment extends BaseFragment implements BaseFragment.FragmentInterFace {
    public static String TAG = "ConfigFragment";

    private MenuClickListener menuClickListener;

    private RelativeLayout layout_menu_bar;
    private ImageView layout_menu;
    private ImageView layout_edit;
    private ImageView layout_refresh;
    private RelativeLayout layout_edit_bar;
    private ImageView layout_edit_back;
    private ImageView layout_edit_save;
    private LinearLayout layout_web_container;
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_config, null);

        layout_menu_bar = view.findViewById(R.id.config_menu_bar);
        layout_menu = view.findViewById(R.id.config_menu);
        layout_edit = view.findViewById(R.id.config_edit);
        layout_refresh = view.findViewById(R.id.config_refresh);
        layout_edit_bar = view.findViewById(R.id.config_edit_bar);
        layout_edit_back = view.findViewById(R.id.config_edit_back);
        layout_edit_save = view.findViewById(R.id.config_edit_save);
        layout_web_container = view.findViewById(R.id.config_web_container);

        init();

        return view;
    }

    @Override
    public void init() {
        layout_menu.setOnClickListener(v -> menuClickListener.onMenuClick());

        //进入编辑状态
        layout_edit.setOnClickListener(v -> {
            layout_menu_bar.setVisibility(View.INVISIBLE);
            layout_edit_bar.setVisibility(View.VISIBLE);
            WebJsManager.setEditable(webView, true);
        });

        //退出编辑状态
        layout_edit_back.setOnClickListener(v -> {
            layout_edit_bar.setVisibility(View.GONE);
            layout_menu_bar.setVisibility(View.VISIBLE);
            WindowUnit.hideKeyboard(webView);
            WebJsManager.setEditable(webView, false);
            WebJsManager.backConfig(webView);
        });

        //刷新
        layout_refresh.setOnClickListener(v -> {
            //禁用点击
            layout_refresh.setEnabled(false);
            //开启动画
            Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    layout_refresh.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(1000);
            layout_refresh.startAnimation(animation);
            WebJsManager.refreshConfig(webView);
        });

        //保存编辑
        layout_edit_save.setOnClickListener(v -> WebJsManager.saveConfig(webView));

        createWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView = new WebView(MyApplication.getContext());
        webView.setLayoutParams(layoutParams);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setSelected(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WebJsManager.initConfig(webView, AccountSP.getCurrentAccount().getBaseUrl(), AccountSP.getCurrentAccount().getAuthorization());
            }
        });
        //加载本地网页
        webView.loadUrl("file:///android_asset/web/editor.html");
        layout_web_container.addView(webView);
    }

    private void destroyWebView() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        //避免占用过多内存，切换界面时销毁WebView
        if (hidden) {
            destroyWebView();
        } else {
            createWebView();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }
}
