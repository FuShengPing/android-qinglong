package auto.ssh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import auto.base.util.WindowUnit;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowUnit.setStatusBarTextColor(this,false);

        setContentView(R.layout.activity_config);
    }
}