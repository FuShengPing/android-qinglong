package auto.qinglong.module;

import androidx.fragment.app.Fragment;

import auto.qinglong.net.RequestManager;

public class BaseFragment extends Fragment {
    protected boolean haveFirstSuccess = false;//是否已经加载成功过数据

    public String getNetRequestID() {
        return getClass().getName() + this;
    }

    @Override
    public void onStop() {
        super.onStop();
        //请求本页面的网络请求
        RequestManager.cancelCall(getClass().getName());
    }

    public boolean onBackPressed() {
        return false;
    }

    public interface FragmentInterFace {
        void init();

        void setMenuClickListener(MenuClickListener menuClickListener);
    }

    public interface MenuClickListener {
        void onMenuClick();
    }
}


