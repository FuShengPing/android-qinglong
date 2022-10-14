package auto.qinglong.activity.service.script;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.network.ApiController;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.network.RequestManager;
import auto.qinglong.tools.ToastUnit;


public class ScriptFragment extends BaseFragment{
    public static String TAG = "ScriptFragment";

    private MenuClickListener menuClickListener;
    private ScriptAdapter scriptAdapter;
    //原始数据
    private List<Script> oData;
    //可返回操作
    private boolean canBack = false;

    private ImageView layout_menu;
    private SmartRefreshLayout layout_refresh;
    private TextView layout_dir_tip;
    private RecyclerView layout_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_script, null, false);

        layout_dir_tip = view.findViewById(R.id.script_dir_tip);
        layout_menu = view.findViewById(R.id.scrip_menu);
        layout_refresh = view.findViewById(R.id.refreshLayout);
        layout_recycler = view.findViewById(R.id.recyclerView);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firstLoad();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            firstLoad();
        }
    }

    private void firstLoad() {
        if (!haveFirstSuccess && !RequestManager.isRequesting(getNetRequestID())) {
            new Handler().postDelayed(() -> {
                if (isVisible()) {
                    getScripts();
                }
            }, 1000);
        }

    }

    @Override
    public void init() {
        scriptAdapter = new ScriptAdapter(requireContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(scriptAdapter);

        //item回调
        scriptAdapter.setScriptInterface(new ScriptItemListener() {
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
                ToastUnit.showShort(requireContext(), getString(R.string.tip_copy_path_ready));
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> getScripts());

        //唤起主导航栏
        layout_menu.setOnClickListener(v -> menuClickListener.onMenuClick());
    }

    private void getScripts() {
        ApiController.getScripts(getNetRequestID(), new ApiController.GetScriptsCallback() {
            @Override
            public void onSuccess(List<Script> scripts) {
                setData(scripts, "");
                oData = scripts;
                canBack = false;
                haveFirstSuccess = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "加载失败：" + msg);
                this.onEnd(false);
            }

            protected void onEnd(boolean isSuccess) {
                if (layout_refresh.isRefreshing()) {
                    layout_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void setData(List<Script> data, String dir) {
        scriptAdapter.setData(ScriptFragment.sortScript(data));
        String text = getString(R.string.char_path_split) + dir;
        layout_dir_tip.setText(text);
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (canBack) {
            scriptAdapter.setData(oData);
            layout_dir_tip.setText(getString(R.string.char_path_split));
            canBack = false;
            return true;
        } else {
            return false;
        }
    }

    public static List<Script> sortScript(@NonNull List<Script> fromData) {
        List<Script> folderData = new ArrayList<>();
        List<Script> fileData = new ArrayList<>();
        Map<String, Script> map = new HashMap<>();
        List<String> order = new ArrayList<>();

        for (Script script : fromData) {
            if (script.getChildren() != null) {
                order.add(script.getTitle());
                map.put(script.getTitle(), script);
            }
        }
        Collections.sort(order);
        for (String title : order) {
            folderData.add(map.get(title));
        }
        map.clear();
        order.clear();


        for (Script script : fromData) {
            if (script.getChildren() == null) {
                order.add(script.getTitle());
                map.put(script.getTitle(), script);
            }
        }
        Collections.sort(order);
        for (String title : order) {
            fileData.add(map.get(title));
        }
        map.clear();
        order.clear();

        fromData.clear();
        fromData.addAll(folderData);
        fromData.addAll(fileData);
        return fromData;
    }
}