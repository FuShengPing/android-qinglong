package auto.qinglong.activity.ql.log;

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

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.CodeWebActivity;
import auto.qinglong.bean.ql.QLLog;
import auto.qinglong.network.http.NetManager;
import auto.qinglong.network.http.QLApiController;
import auto.base.util.ToastUnit;


public class LogFragment extends BaseFragment {
    public static String TAG = "LogFragment";
    private boolean canBack = false;
    private List<QLLog> oData;
    private MenuClickListener menuClickListener;
    private LogAdapter logAdapter;

    private ImageView ui_nav;
    private SmartRefreshLayout ui_refresh;
    private TextView ui_dir;
    private RecyclerView ui_recycler;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, null);

        ui_nav = view.findViewById(R.id.log_nav);
        ui_refresh = view.findViewById(R.id.refresh_layout);
        ui_dir = view.findViewById(R.id.log_dir_tip);
        ui_recycler = view.findViewById(R.id.recycler_view);

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

    private void initData() {
        if (initDataFlag || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ui_refresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getLogs();
            }
        }, 1000);
    }

    @Override
    public void init() {
        logAdapter = new LogAdapter(requireContext());
        ui_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);
        ui_recycler.setAdapter(logAdapter);

        ui_nav.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick();
            }
        });

        logAdapter.setItemActionListener(qlLog -> {
            if (qlLog.isDir()) {
                canBack = true;
                sortAndSetData(qlLog.getChildren(), qlLog.getName());
            } else {
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_LOG);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, qlLog.getName());
                intent.putExtra(CodeWebActivity.EXTRA_LOG_PATH, qlLog.getLogPath());
                startActivity(intent);
            }
        });

        ui_refresh.setOnRefreshListener(refreshLayout -> getLogs());
    }


    private void getLogs() {
        QLApiController.getLogs(getNetRequestID(), new QLApiController.NetGetLogsCallback() {
            @Override
            public void onSuccess(List<QLLog> logs) {
                sortAndSetData(logs, "");
                oData = logs;
                canBack = false;
                initDataFlag = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            protected void onEnd(boolean isSuccess) {
                if (ui_refresh.isRefreshing()) {
                    ui_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void sortAndSetData(List<QLLog> data, String dir) {
        Collections.sort(data);
        logAdapter.setData(data);
        ui_dir.setText(getString(R.string.char_path_split) + dir);
    }

    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.menuClickListener = mMenuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (canBack) {
            logAdapter.setData(oData);
            ui_dir.setText(getString(R.string.char_path_split));
            canBack = false;
            return true;
        } else {
            return false;
        }

    }
}