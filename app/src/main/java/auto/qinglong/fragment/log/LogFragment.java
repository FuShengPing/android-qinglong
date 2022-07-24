package auto.qinglong.fragment.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import auto.qinglong.R;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.fragment.task.TaskAdapter;


public class LogFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "LogFragment";

    private boolean isRequesting = false;
    private boolean isFirst = false;
    private MenuClickInterface menuClickInterface;
    private TaskAdapter taskAdapter;

    private ImageView layout_nav;
    private SwipeRefreshLayout layout_swipe;
    private RecyclerView layout_recycler;

    private String currentSearcValue = "";

    public LogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_log, null);
        layout_nav = view.findViewById(R.id.log_nav);

        initViewSetting();
        return view;
    }


    @Override
    public void initViewSetting() {
        layout_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuClickInterface != null) {
                    menuClickInterface.onMenuClick();
                }
            }
        });
    }

    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }
}