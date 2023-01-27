package auto.qinglong.activity;

import androidx.fragment.app.Fragment;

import auto.qinglong.network.http.RequestManager;

public abstract class BaseFragment extends Fragment {
    //是否已经加载成功过数据标志
    protected boolean initDataFlag = false;

    @Override
    public void onStop() {
        super.onStop();
        //取消本页面的网络请求
        RequestManager.cancelAllCall(getClass().getName());
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

    public void setMenuClickListener(MenuClickListener menuClickListener) {
    }

    public interface MenuClickListener {
        void onMenuClick();
    }
}


