package auto.qinglong.ui.activity.panel.setting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "PagerAdapter";

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CommonFragment();
            case 1:
                return new LoginLogFragment();
            default:
                return new AppFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
