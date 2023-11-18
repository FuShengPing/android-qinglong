package auto.panel.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;

import auto.panel.bean.panel.PanelDependence;
import auto.panel.ui.fragment.PanelDependenceFragment;

public class PanelDependencePagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "PanelDependencePagerAdapter";

    private HashMap<Integer, PanelDependenceFragment> fragmentList;

    public PanelDependencePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PanelDependenceFragment depFragment = new PanelDependenceFragment();

        if (position == 0) {
            depFragment.setType(PanelDependence.TYPE_NODEJS);
        } else if (position == 1) {
            depFragment.setType(PanelDependence.TYPE_PYTHON);
        } else if (position == 2) {
            depFragment.setType(PanelDependence.TYPE_LINUX);
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

    public PanelDependenceFragment getCurrentFragment(int position) {
        return fragmentList.get(position);
    }
}
