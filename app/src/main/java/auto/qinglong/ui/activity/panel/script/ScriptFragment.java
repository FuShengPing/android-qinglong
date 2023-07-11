package auto.qinglong.ui.activity.panel.script;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import auto.base.util.ToastUnit;
import auto.base.view.popup.PopMenuObject;
import auto.base.view.popup.PopMenuWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.panel.File;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.ApiController;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.panel.CodeWebActivity;


public class ScriptFragment extends BaseFragment {
    public static String TAG = "ScriptFragment";

    private Stack<List<File>> fileStack;
    private MenuClickListener menuClickListener;
    private ScriptAdapter adapter;

    private ImageView uiMenu;
    private ImageView uiMore;
    private TextView uiDirTip;

    private SmartRefreshLayout uiRefresh;
    private RecyclerView uiRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_script, null, false);

        uiMenu = view.findViewById(R.id.scrip_menu);
        uiMore = view.findViewById(R.id.script_more);
        uiDirTip = view.findViewById(R.id.script_dir_tip);
        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        fileStack = new Stack<>();

        adapter = new ScriptAdapter(requireContext());
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        uiRecycler.setAdapter(adapter);
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onDispatchBackKey() {
        if (fileStack.size() == 1) {
            fileStack.clear();
            return false;
        } else {
            fileStack.pop();
            adapter.setData(fileStack.peek());
            if (fileStack.peek().isEmpty() || fileStack.peek().get(0).getParent().isEmpty()) {
                uiDirTip.setText("/");
            } else {
                uiDirTip.setText("/" + fileStack.peek().get(0).getParent());
            }
            return true;
        }
    }

    @Override
    public void init() {
        adapter.setScriptInterface(new ScriptAdapter.ItemActionListener() {
            @Override
            public void onEdit(File file) {
                if (file.isDir()) {
                    sortAndSetData(file.getChildren(), file.getPath());
                } else {
                    Intent intent = new Intent(getContext(), CodeWebActivity.class);
                    intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_SCRIPT);
                    intent.putExtra(CodeWebActivity.EXTRA_TITLE, file.getTitle());
                    intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_NAME, file.getTitle());
                    intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_DIR, file.getParent());
                    intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
                    startActivity(intent);
                }
            }

            @Override
            public void onMenu(View view, File file, int position) {
                showPopMenu(view, file, position);
            }
        });

        uiRefresh.setOnRefreshListener(refreshLayout -> getScriptFiles());

        uiMenu.setOnClickListener(v -> menuClickListener.onMenuClick());

        uiMore.setOnClickListener(this::showPopMenu);
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getScriptFiles();
            }
        }, 1000);

    }

    @Override
    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.menuClickListener = mMenuClickListener;
    }

    private void showPopMenu(View v, File file, int position) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(v, Gravity.CENTER);
        popMenuWindow.addItem(new PopMenuObject("copy", "复制路径", R.drawable.ic_gray_crop_free));
        //popMenuWindow.addItem(new PopMenuObject("backup", "脚本备份", R.drawable.ic_gray_download));
        //popMenuWindow.addItem(new PopMenuObject("replace", "脚本替换", R.drawable.ic_gray_copy));
        if (!file.isDir()) {
            popMenuWindow.addItem(new PopMenuObject("delete", "删除脚本", R.drawable.ic_gray_delete));
        }

        popMenuWindow.setOnActionListener(key -> {
            switch (key) {
                case "copy":
                    ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, file.getPath()));
                    ToastUnit.showShort(getString(R.string.tip_copy_path_ready));
                    break;
                case "backup":
                    break;
                case "delete":
                    deleteScript(file, position);
                    break;
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopMenu(View v) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(v, Gravity.END);
        popMenuWindow.addItem(new PopMenuObject("add", "新建脚本", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new PopMenuObject("import", "本地导入", R.drawable.ic_gray_upload));
        popMenuWindow.addItem(new PopMenuObject("backup", "脚本备份", R.drawable.ic_gray_download));

        popMenuWindow.setOnActionListener(key -> true);
        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    @SuppressLint("SetTextI18n")
    private void sortAndSetData(List<File> files, String dir) {
        Collections.sort(files);
        fileStack.add(files);
        adapter.setData(files);
        uiDirTip.setText("/" + dir);
    }

    private void getScriptFiles() {
        auto.qinglong.net.panel.ApiController.getScripts(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.qinglong.net.panel.ApiController.FileListCallBack() {
            @Override
            public void onSuccess(List<File> files) {
                fileStack.clear();
                sortAndSetData(files, "");
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_load_failure_header) + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void deleteScript(File file, int position) {
        ApiController.deleteScript(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), file, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort(getString(R.string.tip_delete_success));
                adapter.removeItem(position);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_delete_failure_header) + msg);
            }
        });
    }

}