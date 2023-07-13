package auto.panel.ui.activity.panel.dependence;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;

import auto.panel.bean.panel.Dependence;

public class PagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "PagerAdapter";

    private HashMap<Integer, DepFragment> fragmentList;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DepFragment depFragment = new DepFragment();

        if (position == 0) {
            depFragment.setType(Dependence.TYPE_NODEJS);
        } else if (position == 1) {
            depFragment.setType(Dependence.TYPE_PYTHON);
        } else if (position == 2) {
            depFragment.setType(Dependence.TYPE_LINUX);
        }

        if (fragmentList == null) {
            fragmentList = new HashMap<>();
        }

        fragmentList.put(position, depFragment);
        return depFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public DepFragment getCurrentFragment(int position) {
        return fragmentList.get(position);
    }
}
