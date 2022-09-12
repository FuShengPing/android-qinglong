package auto.qinglong.fragment.log;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.LogActivity;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.object.Log;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.ToastUnit;


public class LogFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "LogFragment";
    private boolean canBack = false;
    private List<Log> oData;
    private MenuClickInterface menuClickInterface;
    private LogAdapter logAdapter;

    private ImageView layout_nav;
    private SwipeRefreshLayout layout_swipe;
    private TextView layout_dir;
    private RecyclerView layout_recycler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_log, null);
        layout_nav = view.findViewById(R.id.log_nav);
        layout_swipe = view.findViewById(R.id.log_swipe);
        layout_dir = view.findViewById(R.id.log_dir_tip);
        layout_recycler = view.findViewById(R.id.log_recycler);

        logAdapter = new LogAdapter(requireContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(logAdapter);

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
        if(!hidden && !haveFirstSuccess && !CallManager.isRequesting(getClassName())){
            firstLoad();
        }
        super.onHiddenChanged(hidden);
    }

    private void firstLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isVisible()) {
                    getLogs();
                }
            }
        }, 1000);
    }

    @Override
    public void init() {
        layout_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuClickInterface != null) {
                    menuClickInterface.onMenuClick();
                }
            }
        });

        logAdapter.setLogInterFace(new LogInterFace() {
            @Override
            public void onItemClick(Log log) {
                if (log.isDir()) {
                    canBack = true;
                    setData(log.getChildren(), log.getName());
                } else {
                    Intent intent = new Intent(getContext(), LogActivity.class);
                    intent.putExtra(LogActivity.ExtraName, log.getName());
                    intent.putExtra(LogActivity.ExtraPath, log.getLogPath());
                    startActivity(intent);
                }
            }
        });

        layout_swipe.setColorSchemeColors(requireContext().getColor(R.color.theme_color));
        layout_swipe.setRefreshing(true);
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLogs();
            }
        });
    }


    private void getLogs() {
        ApiController.getLogs(getClassName(), new ApiController.GetLogsCallback() {
            @Override
            public void onSuccess(List<Log> logs) {
                setData(logs, "");
                oData = logs;
                canBack = false;
                haveFirstSuccess = true;
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
                ToastUnit.showShort(requireContext(), "加载成功");
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
    private void setData(List<Log> data, String dir) {
        logAdapter.setData(data);
        layout_dir.setText("/" + dir);
    }

    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }

    @Override
    public boolean onBackPressed() {
        if (canBack) {
            logAdapter.setData(oData);
            layout_dir.setText("/");
            canBack = false;
            return true;
        } else {
            return false;
        }

    }
}