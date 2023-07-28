package auto.ssh.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import auto.base.util.WindowUnit;

/**
 * @author wsfsp4
 * @version 2023.06.22
 */
public class BaseActivity extends AppCompatActivity {
    protected Activity self;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = this;

        WindowUnit.setStatusBarTextColor(this, false);
    }
}
