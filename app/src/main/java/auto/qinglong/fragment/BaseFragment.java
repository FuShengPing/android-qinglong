package auto.qinglong.fragment;

import androidx.fragment.app.Fragment;

import auto.qinglong.tools.CallManager;

public class BaseFragment extends Fragment {
    protected boolean haveFirstSuccess = false;//是否已经加载成功过数据

    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public void onPause() {
        //请求本页面的网络请求
        CallManager.cancelCall(getClass().getName());
        super.onPause();
    }

    public boolean onBackPressed() {
        return false;
    }
}
