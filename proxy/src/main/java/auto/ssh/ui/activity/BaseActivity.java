package auto.ssh.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import auto.base.util.WindowUnit;

/**
 * @author wsfsp4
 * @version 2023.06.22
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowUnit.setStatusBarTextColor(this,false);
    }
}
