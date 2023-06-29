package auto.qinglong.ui.activity.panel.dependence;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;

public class PagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "PagerAdapter";

    private HashMap<Integer, DepFragment> fragmentList;
    private PagerActionListener pagerActionListener;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DepFragment depFragment = new DepFragment();

        if (position == 0) {
            depFragment.setType("nodejs");
        } else if (position == 1) {
            depFragment.setType("python3");
        } else if (position == 2) {
            depFragment.setType("linux");
        }

        if (fragmentList == null) {
            fragmentList = new HashMap<>();
        }

        fragmentList.put(position, depFragment);
        depFragment.setPagerActionListener(pagerActionListener);
        return depFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public DepFragment getCurrentFragment(int position) {
        return fragmentList.get(position);
    }

    public void setPagerActionListener(PagerActionListener pagerActionListener) {
        this.pagerActionListener = pagerActionListener;
    }

    public interface PagerActionListener {
        void onMulAction();
    }


}
