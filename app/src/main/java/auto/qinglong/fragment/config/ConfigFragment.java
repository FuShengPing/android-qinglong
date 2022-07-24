package auto.qinglong.fragment.config;

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
import auto.qinglong.api.ApiController;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.web.JsInterface;
import auto.qinglong.tools.web.WebJsManager;

public class ConfigFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "ConfigFragment";
    private String configContent = "";

    private MenuClickInterface menuClickInterface;

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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fg_config, null);

        layout_menu_bar = view.findViewById(R.id.config_menu_bar);
        layout_menu = view.findViewById(R.id.config_menu);
        layout_edit = view.findViewById(R.id.config_edit);
        layout_refresh = view.findViewById(R.id.config_refresh);
        layout_edit_bar = view.findViewById(R.id.config_edit_bar);
        layout_edit_back = view.findViewById(R.id.config_edit_back);
        layout_edit_save = view.findViewById(R.id.config_edit_save);
        layout_web_container = view.findViewById(R.id.config_web_container);
        initViewSetting();

        return view;
    }

    @Override
    public void initViewSetting() {
        layout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClickInterface.onMenuClick();
            }
        });

        layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_menu_bar.setVisibility(View.INVISIBLE);
                layout_edit_bar.setVisibility(View.VISIBLE);
                WebJsManager.setEditable(webView, true);
            }
        });

        layout_edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_edit_bar.setVisibility(View.GONE);
                layout_menu_bar.setVisibility(View.VISIBLE);
                WindowUnit.hideKeyboard(webView);
                WebJsManager.setCode(webView, configContent);
                WebJsManager.setEditable(webView, false);
            }
        });

        layout_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                //禁用点击
                layout_refresh.setEnabled(false);
                //开启动画
                Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                animation.setRepeatCount(Animation.INFINITE);
                layout_refresh.startAnimation(animation);
                loadConfig();
            }
        });

        layout_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                WindowUnit.hideKeyboard(webView);
                WebJsManager.getCode(webView, new WebJsManager.WebCallback() {
                    @Override
                    public void onContent(String content) {
                        saveConfig(content);
                    }
                });
            }
        });

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
                loadConfig();
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

    private void loadConfig() {
        ApiController.loadConfig(getClassName(), new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String data) {
                if (!configContent.equals(data)) {
                    configContent = data;
                    WebJsManager.setCode(webView, data);
                    layout_edit.setVisibility(View.VISIBLE);
                }
                clearRefresh();
            }

            @Override
            public void onFailure(String msg) {
                layout_edit.setVisibility(View.INVISIBLE);
                clearRefresh();
            }
        });
    }

    private void saveConfig(String content) {
        ApiController.saveConfig(getClassName(), content, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String data) {
                configContent = content;
                ToastUnit.showShort(getContext(), "保存成功");
                layout_edit_back.performClick();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "保存失败：" + msg);
            }
        });
    }

    /**
     * 清除刷新效果
     */
    private void clearRefresh() {
        layout_refresh.setVisibility(View.VISIBLE);
        layout_refresh.clearAnimation();
        layout_refresh.setEnabled(true);
    }

    @Override
    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        //避免占用过多内存，切换界面时销毁WebView
        if (hidden) {
            destroyWebView();
        } else {
            configContent = "";
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
