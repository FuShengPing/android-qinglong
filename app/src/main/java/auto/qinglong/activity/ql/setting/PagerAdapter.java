package auto.qinglong.activity.ql.setting;

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
                return new LoginLogFragment();
            case 1:
                return new AppFragment();
            default:
                return new CommonFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
