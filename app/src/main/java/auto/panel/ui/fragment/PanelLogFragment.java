package auto.panel.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mobstat.StatService;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import auto.panel.R;
import auto.panel.bean.panel.PanelFile;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.activity.TextEditorActivity;
import auto.panel.ui.adapter.PanelLogItemAdapter;
import auto.panel.utils.ToastUnit;

@SuppressLint({"SetTextI18n", "InflateParams"})
public class PanelLogFragment extends BaseFragment {
    public static String TAG = "PanelLogFragment";
    public static String NAME = "日志管理";

    private Stack<List<PanelFile>> fileStack;
    private MenuClickListener menuClickListener;
    private PanelLogItemAdapter adapter;

    private ImageView uiNav;
    private SmartRefreshLayout uiRefresh;
    private TextView uiDir;
    private RecyclerView uiRecycler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_log, null);

        uiNav = view.findViewById(R.id.log_nav);
        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiDir = view.findViewById(R.id.log_dir_tip);
        uiRecycler = view.findViewById(R.id.recycler_view);

        fileStack = new Stack<>();

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(requireContext(), NAME);
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            StatService.onPageEnd(requireContext(), NAME);
        } else {
            StatService.onPageStart(requireContext(), NAME);
            initData();
        }
    }

    @Override
    public boolean onDispatchBackKey() {
        if (fileStack.size() <= 1) {
            fileStack.clear();
            return false;
        } else {
            fileStack.pop();
            adapter.setData(fileStack.peek());
            updateCurrentDir(fileStack.peek().get(0).getParentPath());
            return true;
        }
    }

    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.menuClickListener = mMenuClickListener;
    }

    private void updateCurrentDir(String dir) {
        uiDir.setText("/" + dir);
    }

    @Override
    public void init() {
        adapter = new PanelLogItemAdapter(requireContext());
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);
        uiRecycler.setAdapter(adapter);

        adapter.setItemActionListener(file -> {
            if (file.isDir()) {
                sortAndSetData(file.getChildren(), file.getTitle());
            } else {
                Intent intent = new Intent(getContext(), TextEditorActivity.class);
                intent.putExtra(TextEditorActivity.EXTRA_TYPE, TextEditorActivity.TYPE_LOG);
                intent.putExtra(TextEditorActivity.EXTRA_TITLE, file.getTitle());
                intent.putExtra(TextEditorActivity.EXTRA_LOG_NAME, file.getTitle());
                intent.putExtra(TextEditorActivity.EXTRA_LOG_DIR, file.getParentPath());
                startActivity(intent);
            }
        });

        uiNav.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick();
            }
        });

        uiRefresh.setOnRefreshListener(refreshLayout -> getLogFiles());
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getLogFiles();
            }
        }, 1000);
    }

    private void sortAndSetData(List<PanelFile> files, String dir) {
        Collections.sort(files);
        fileStack.add(files);
        adapter.setData(files);
        updateCurrentDir(dir);
    }

    private void getLogFiles() {
        auto.panel.net.panel.ApiController.getLogs( new ApiController.FileListCallBack() {
            @Override
            public void onSuccess(List<PanelFile> files) {
                sortAndSetData(files, "");
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }
}