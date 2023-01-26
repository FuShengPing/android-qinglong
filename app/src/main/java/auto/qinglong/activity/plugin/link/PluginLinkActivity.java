package auto.qinglong.activity.plugin.link;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.bean.app.Link;
import auto.qinglong.network.http.ApiController;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WebUnit;

public class PluginLinkActivity extends BaseActivity {
    public static final String TAG = "LinkActivity";

    private ImageView ui_back;
    private TextView ui_contribute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        ui_back = findViewById(R.id.common_bar_back);
        ui_contribute = findViewById(R.id.bar_contribute);

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_contribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        netGetLinks();
    }

    protected void netGetLinks() {
        String url = getString(R.string.url_extension_link);
        ApiController.getLinks(this.getNetRequestID(), WebUnit.getHost(url), WebUnit.getPath(url, ""), new ApiController.NetLinkCallback() {
            @Override
            public void onSuccess(List<Link> links) {
                ToastUnit.showShort(links.get(0).getTitle());
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }
}