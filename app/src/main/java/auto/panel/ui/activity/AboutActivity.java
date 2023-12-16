package auto.panel.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import auto.panel.utils.PackageUtil;
import auto.panel.utils.WebUnit;
import auto.panel.R;
import auto.panel.database.sp.SettingPreference;

@SuppressLint("SetTextI18n")
public class AboutActivity extends BaseActivity {
    private View uiExit;
    private TextView uiLinkGitee;
    private TextView uiLinkGithub;
    private TextView uiVersionNow;
    private TextView uiVersionNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_activity_about);

        uiExit = findViewById(R.id.exit);
        uiVersionNew = findViewById(R.id.version_new);
        uiVersionNow = findViewById(R.id.version_now);
        uiLinkGitee = findViewById(R.id.link_gitee);
        uiLinkGithub = findViewById(R.id.link_github);

        init();
    }

    protected void init() {
        uiExit.setOnClickListener(v -> finish());

        uiLinkGitee.setOnClickListener(v -> WebUnit.open(getBaseContext(), SettingPreference.getGiteeUrl()));

        uiLinkGithub.setOnClickListener(v -> WebUnit.open(getBaseContext(), SettingPreference.getGithubUrl()));

        PackageUtil.Version version = PackageUtil.getVersion(this);

        uiVersionNow.setText("Version " + version.getVersionName());

        if (SettingPreference.getNewVersionCode() > version.getVersionCode()) {
            uiVersionNew.setText("New " + SettingPreference.getNewVersionName());
            uiVersionNew.setOnClickListener(v -> WebUnit.open(this, SettingPreference.getDownloadUrl()));
        } else {
            uiVersionNew.setVisibility(View.INVISIBLE);
        }
    }
}