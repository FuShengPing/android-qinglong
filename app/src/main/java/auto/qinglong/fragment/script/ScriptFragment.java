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
import auto.qinglong.api.res.ScriptRes;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.SortUnit;
import auto.qinglong.tools.ToastUnit;


public class ScriptFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "ScriptFragment";
    private MenuClickInterface menuClickInterface;
    private ScriptAdapter scriptAdapter;
    private List<Script> oData;
    private boolean canBack = false;
    private boolean isSuccess = false;

    private ImageView layout_menu;
    private SwipeRefreshLayout layout_swipe;
    private TextView layout_dir;
    private RecyclerView layout_recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_script, null, false);

        layout_swipe = view.findViewById(R.id.script_swipe);
        layout_dir = view.findViewById(R.id.script_dir_tip);
        layout_menu = view.findViewById(R.id.scrip_menu);
        layout_recyclerView = view.findViewById(R.id.script_recycler);

        scriptAdapter = new ScriptAdapter(requireContext());

        initViewSetting();

        return view;
    }

    @Override
    public void initViewSetting() {
        layout_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(layout_recyclerView.getItemAnimator()).setChangeDuration(0);
        layout_recyclerView.setAdapter(scriptAdapter);

        scriptAdapter.setScriptInterface(new ScriptInterface() {
            @Override
            public void onEdit(Script script) {
                if (script.getChildren() != null) {
                    canBack = true;
                    setData(script.getChildren(), script.getTitle());
                } else {
                    Intent intent = new Intent(getContext(), ScriptActivity.class);
                    intent.putExtra(ScriptActivity.EXTRA_URL, script.getUrl());
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


        layout_swipe.setColorSchemeColors(requireContext().getColor(R.color.theme_color));
        layout_swipe.setRefreshing(true);
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getScripts();
            }
        });

        layout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClickInterface.onMenuClick();
            }
        });
    }

    private void getScripts() {
        ApiController.getScripts(getClassName(), new ApiController.GetScriptsCallback() {
            @Override
            public void onSuccess(ScriptRes data) {
                setData(data.getData(), "");
                oData = data.getData();
                canBack = false;
                isSuccess = true;
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
    public void onResume() {
        if (!isSuccess && !CallManager.isRequesting(getClassName())) {
            getScripts();
        }
        super.onResume();
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