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

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import auto.base.util.ToastUnit;
import auto.panel.R;
import auto.panel.bean.panel.File;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.activity.CodeWebActivity;
import auto.panel.ui.adapter.PanelLogItemAdapter;

@SuppressLint({"SetTextI18n", "InflateParams"})
public class PanelLogFragment extends BaseFragment {
    public static String TAG = "PanelLogFragment";

    private Stack<List<File>> fileStack;
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
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @Override
    public boolean onDispatchBackKey() {
        if (fileStack.size() == 1) {
            fileStack.clear();
            return false;
        } else {
            fileStack.pop();
            adapter.setData(fileStack.peek());
            if (fileStack.peek().isEmpty() || fileStack.peek().get(0).getParent().isEmpty()) {
                uiDir.setText("/");
            } else {
                uiDir.setText("/" + fileStack.peek().get(0).getParent());
            }
            return true;
        }
    }

    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.menuClickListener = mMenuClickListener;
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
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_LOG);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, file.getTitle());
                intent.putExtra(CodeWebActivity.EXTRA_LOG_NAME, file.getTitle());
                intent.putExtra(CodeWebActivity.EXTRA_LOG_DIR, file.getParent());
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

    private void sortAndSetData(List<File> files, String dir) {
        Collections.sort(files);
        fileStack.add(files);
        adapter.setData(files);
        uiDir.setText("/" + dir);
    }

    private void getLogFiles() {
        auto.panel.net.panel.ApiController.getLogs(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new ApiController.FileListCallBack() {
            @Override
            public void onSuccess(List<File> files) {
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