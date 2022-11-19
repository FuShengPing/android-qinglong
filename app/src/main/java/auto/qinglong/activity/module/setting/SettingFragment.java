package auto.qinglong.activity.module.setting;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;


public class SettingFragment extends BaseFragment{
    public static String TAG = "SettingFragment";

    private MenuClickListener menuClickListener;


    private ImageView layout_menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_setting, null);

        layout_menu = view.findViewById(R.id.setting_top_bar_menu);

        init();

        return view;
    }

    @Override
    public void init() {
        layout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClickListener.onMenuClick();
            }
        });
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }
}