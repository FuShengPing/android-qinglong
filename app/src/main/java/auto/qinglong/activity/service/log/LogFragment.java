package auto.qinglong.activity.service.log;

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

import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.network.ApiController;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.network.RequestManager;
import auto.qinglong.tools.ToastUnit;


public class LogFragment extends BaseFragment{
    public static String TAG = "LogFragment";
    private boolean canBack = false;
    private List<Log> oData;
    private MenuClickListener menuClickListener;
    private LogAdapter logAdapter;

    private ImageView layout_nav;
    private SmartRefreshLayout layout_refresh;
    private TextView layout_dir;
    private RecyclerView layout_recycler;


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
        if (!haveFirstSuccess && !RequestManager.isRequesting(getNetRequestID())) {
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

        logAdapter.setLogInterFace(log -> {
            if (log.isDir()) {
                canBack = true;
                setData(log.getChildren(), log.getName());
            } else {
                Intent intent = new Intent(getContext(), LogActivity.class);
                intent.putExtra(LogActivity.ExtraName, log.getName());
                intent.putExtra(LogActivity.ExtraPath, log.getLogPath());
                startActivity(intent);
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> getLogs());
    }


    private void getLogs() {
        ApiController.getLogs(getNetRequestID(), new ApiController.GetLogsCallback() {
            @Override
            public void onSuccess(List<Log> logs) {
                setData(logs, "");
                oData = logs;
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

    @SuppressLint("SetTextI18n")
    private void setData(List<Log> data, String dir) {
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