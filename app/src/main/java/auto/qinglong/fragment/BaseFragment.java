package auto.qinglong.fragment;

import androidx.fragment.app.Fragment;

import auto.qinglong.tools.CallManager;

public class BaseFragment extends Fragment {

    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public void onDestroy() {
        //请求本页面的网络请求
        CallManager.cancelCall(getClass().getName());
        super.onDestroy();
    }

    public boolean onBackPressed() {
        return false;
    }
}
