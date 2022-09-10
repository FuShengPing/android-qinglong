package auto.qinglong.fragment.dep;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PagerFragment pagerFragment = new PagerFragment();

        if (position == 0) {
            pagerFragment.setType("nodejs");
        } else if (position == 1) {
            pagerFragment.setType("python");
        } else if (position == 2) {
            pagerFragment.setType("linux");
        }
        return pagerFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
