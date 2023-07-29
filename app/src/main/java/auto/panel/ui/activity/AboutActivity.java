package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import auto.panel.R;
import auto.panel.bean.app.Version;
import auto.panel.database.sp.SettingPreference;
import auto.panel.ui.activity.BaseActivity;
import auto.panel.utils.PackageUtil;
import auto.panel.utils.WebUnit;

@SuppressLint("SetTextI18n")
public class AboutActivity extends BaseActivity {

    private TextView uiLinkGitee;
    private TextView uiLinkGithub;
    private TextView uiVersionNow;
    private TextView uiVersionNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_about);

        uiVersionNew = findViewById(R.id.version_new);
        uiVersionNow = findViewById(R.id.version_now);
        uiLinkGitee = findViewById(R.id.link_gitee);
        uiLinkGithub = findViewById(R.id.link_github);

        init();
    }

    protected void init() {
        uiLinkGitee.setOnClickListener(v -> WebUnit.open(getBaseContext(), SettingPreference.getGiteeUrl()));

        uiLinkGithub.setOnClickListener(v -> WebUnit.open(getBaseContext(), SettingPreference.getGithubUrl()));

        Version version = PackageUtil.getVersion(this);

        uiVersionNow.setText("Version " + version.getVersionName());

        if (SettingPreference.getNewVersionCode() > version.getVersionCode()) {
            uiVersionNew.setText("New " + SettingPreference.getNewVersionName());
            uiVersionNew.setOnClickListener(v -> WebUnit.open(this, SettingPreference.getDownloadUrl()));
        } else {
            uiVersionNew.setVisibility(View.INVISIBLE);
        }
    }
}