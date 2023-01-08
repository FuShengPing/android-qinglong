package auto.qinglong.activity;

import androidx.fragment.app.Fragment;

import auto.qinglong.network.http.RequestManager;

public abstract class BaseFragment extends Fragment {
    //是否已经加载成功过数据标志
    protected boolean loadSuccessFlag = false;

    public String getNetRequestID() {
        return getClass().getName() + this;
    }

    @Override
    public void onStop() {
        super.onStop();
        //请求本页面的网络请求
        RequestManager.cancelAllCall(getClass().getName());
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void init() {
    }

    public void setMenuClickListener(MenuClickListener menuClickListener) {
    }

    public interface MenuClickListener {
        void onMenuClick();
    }
}


