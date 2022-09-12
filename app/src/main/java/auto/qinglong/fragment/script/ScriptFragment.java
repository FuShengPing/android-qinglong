package auto.qinglong.fragment.script;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.ScriptActivity;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.object.Script;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.SortUnit;
import auto.qinglong.tools.ToastUnit;


public class ScriptFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "ScriptFragment";
    private MenuClickInterface menuClickInterface;
    private ScriptAdapter scriptAdapter;
    private List<Script> oData;
    private boolean canBack = false;//存在可返回操作

    private ImageView layout_menu;
    private SwipeRefreshLayout layout_swipe;
    private TextView layout_dir;
    private RecyclerView layout_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_script, null, false);

        layout_swipe = view.findViewById(R.id.script_swipe);
        layout_dir = view.findViewById(R.id.script_dir_tip);
        layout_menu = view.findViewById(R.id.scrip_menu);
        layout_recycler = view.findViewById(R.id.script_recycler);

        scriptAdapter = new ScriptAdapter(requireContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(scriptAdapter);

        init();

        return view;
    }

    @Override
    public void onResume() {
        if (!haveFirstSuccess && !CallManager.isRequesting(getClassName())) {
            firstLoad();
        }
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && !haveFirstSuccess && !CallManager.isRequesting(getClassName())) {
            firstLoad();
        }
        super.onHiddenChanged(hidden);
    }

    private void firstLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isVisible()) {
                    getScripts();
                }
            }
        }, 1000);
    }

    @Override
    public void init() {
        //item回调
        scriptAdapter.setScriptInterface(new ScriptInterface() {
            @Override
            public void onEdit(Script script) {
                if (script.getChildren() != null) {
                    canBack = true;
                    setData(script.getChildren(), script.getTitle());
                } else {
                    Intent intent = new Intent(getContext(), ScriptActivity.class);
                    intent.putExtra(ScriptActivity.EXTRA_NAME, script.getTitle());
                    intent.putExtra(ScriptActivity.EXTRA_PARENT, script.getParent());
                    startActivity(intent);
                }
            }

            @Override
            public void onAction(Script script) {
                ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, script.getKey()));
                ToastUnit.showShort(requireContext(), "已复制路径到粘贴板");
            }
        });

        //刷新控件
        layout_swipe.setColorSchemeColors(requireContext().getColor(R.color.theme_color));
        layout_swipe.setRefreshing(true);
        layout_swipe.setOnRefreshListener(this::getScripts);

        //唤起主导航栏
        layout_menu.setOnClickListener(v -> menuClickInterface.onMenuClick());
    }

    private void getScripts() {
        ApiController.getScripts(getClassName(), new ApiController.GetScriptsCallback() {
            @Override
            public void onSuccess(List<Script> scripts) {
                setData(scripts, "");
                oData = scripts;
                canBack = false;
                haveFirstSuccess = true;
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "加载失败：" + msg);
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setData(List<Script> data, String dir) {
        scriptAdapter.setData(SortUnit.sortScript(data));
        layout_dir.setText("/" + dir);
    }

    @Override
    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }

    @Override
    public boolean onBackPressed() {
        if (canBack) {
            scriptAdapter.setData(oData);
            layout_dir.setText("/");
            canBack = false;
            return true;
        } else {
            return false;
        }

    }
}