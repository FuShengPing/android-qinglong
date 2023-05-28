package auto.qinglong.activity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import auto.qinglong.network.http.NetManager;

public abstract class BaseFragment extends Fragment {
    public static final String TAG = "BaseFragment";
    protected boolean initDataFlag = false;//数据加载标志

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
        NetManager.cancelAllCall(getClass().getName());
    }

    public String getNetRequestID() {
        return getClass().getName() + this;
    }


    /**
     * @return 是否需要拦截返回键
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * @return 是否需要拦截点击事件
     */
    public boolean onDispatchTouchEvent() {
        return false;
    }

    protected void init() {
    }

    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
    }

    public interface MenuClickListener {
        void onMenuClick();
    }
}


