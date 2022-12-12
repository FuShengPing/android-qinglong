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
import auto.qinglong.bean.ql.QLLog;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.ToastUnit;


public class LogFragment extends BaseFragment {
    public static String TAG = "LogFragment";
    private boolean canBack = false;
    private List<QLLog> oData;
    private MenuClickListener menuClickListener;
    private LogAdapter logAdapter;

    private ImageView layout_nav;
    private SmartRefreshLayout layout_refresh;
    private TextView layout_dir;
    private RecyclerView layout_recycler;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_log, null);

        layout_nav = view.findViewById(R.id.log_nav);
        layout_refresh = view.findViewById(R.id.refreshLayout);
        layout_dir = view.findViewById(R.id.log_dir_tip);
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
        if (!loadSuccessFlag && !RequestManager.isRequesting(getNetRequestID())) {
            new Handler().postDelayed(() -> {
                if (isVisible()) {
                    getLogs();
                }
            }, 1000);
        }

    }

    @Override
    public void init() {
        logAdapter = new LogAdapter(requireContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(logAdapter);

        layout_nav.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick();
            }
        });

        logAdapter.setItemActionListener(qlLog -> {
            if (qlLog.isDir()) {
                canBack = true;
                sortAndSetData(qlLog.getChildren(), qlLog.getName());
            } else {
                Intent intent = new Intent(getContext(), LogDetailActivity.class);
                intent.putExtra(LogDetailActivity.ExtraName, qlLog.getName());
                intent.putExtra(LogDetailActivity.ExtraPath, qlLog.getLogPath());
                startActivity(intent);
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> getLogs());
    }


    private void getLogs() {
        QLApiController.getLogs(getNetRequestID(), new QLApiController.GetLogsCallback() {
            @Override
            public void onSuccess(List<QLLog> QLLogs) {
                sortAndSetData(QLLogs, "");
                oData = QLLogs;
                canBack = false;
                loadSuccessFlag = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            protected void onEnd(boolean isSuccess) {
                if (layout_refresh.isRefreshing()) {
                    layout_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void sortAndSetData(List<QLLog> data, String dir) {
        Collections.sort(data);
        logAdapter.setData(data);
        layout_dir.setText(getString(R.string.char_path_split) + dir);
    }

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (canBack) {
            logAdapter.setData(oData);
            layout_dir.setText(getString(R.string.char_path_split));
            canBack = false;
            return true;
        } else {
            return false;
        }

    }
}