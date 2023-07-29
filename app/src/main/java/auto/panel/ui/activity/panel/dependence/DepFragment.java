package auto.panel.ui.activity.panel.dependence;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.ui.popup.EditPopupObject;
import auto.base.ui.popup.EditPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.panel.R;
import auto.panel.bean.panel.Dependence;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.fragment.BaseFragment;
import auto.panel.ui.activity.panel.CodeWebActivity;

public class DepFragment extends BaseFragment {
    private String type;

    private DepItemAdapter itemAdapter;

    private SmartRefreshLayout uiRefresh;
    private EditPopupWindow uiPopEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_dep_pager, container, false);

        uiRefresh = view.findViewById(R.id.refresh_layout);

        itemAdapter = new DepItemAdapter(requireContext());
        RecyclerView uiRecycler = view.findViewById(R.id.recycler_view);
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        uiRecycler.setAdapter(itemAdapter);
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    protected void onCheckStateChange(boolean checkState) {
        itemAdapter.setCheckState(checkState);
    }

    protected void onSelectAllChange(boolean isChecked) {
        if (itemAdapter.getCheckState()) {
            itemAdapter.setAllChecked(isChecked);
        }
    }

    protected void onDeleteClick() {
        List<Object> ids = getSelectedItemIds();
        if (ids.size() > 0) {
            deleteDependence(ids);
        } else {
            ToastUnit.showShort(getString(R.string.tip_empty_select));
        }
    }

    protected void onAddClick() {
        uiPopEdit = new EditPopupWindow("新建依赖", "取消", "确定");
        uiPopEdit.setMaxHeight(WindowUnit.getWindowHeightPix(requireContext()) / 3);
        uiPopEdit.addItem(new EditPopupObject("type", this.type, "类型", null, false, false));
        uiPopEdit.addItem(new EditPopupObject("name", null, "名称", "请输入依赖名称"));

        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String type = map.get("type");
                String name = map.get("name");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_dependence_name));
                    return false;
                }

                List<Dependence> dependencies = new ArrayList<>();
                Dependence dependence = new Dependence();
                dependence.setTitle(name);
                dependence.setType(type);
                dependencies.add(dependence);
                addDependencies(dependencies);
                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });
        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    @Override
    protected void init() {
        itemAdapter.setItemInterface(new DepItemAdapter.ItemActionListener() {
            @Override
            public void onDetail(Dependence dependence, int position) {
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_DEPENDENCE);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, dependence.getTitle());
                intent.putExtra(CodeWebActivity.EXTRA_DEPENDENCE_ID, String.valueOf(dependence.getKey()));
                startActivity(intent);
            }

            @Override
            public void onReinstall(Dependence dependence, int position) {
                List<Object> ids = new ArrayList<>();
                ids.add(dependence.getKey());
                reinstallDependencies(ids);
            }
        });

        uiRefresh.setOnRefreshListener(refreshLayout -> getDependencies());
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getDependencies();
            }
        }, 1000);
    }

    public void setType(String type) {
        this.type = type;
    }

    private List<Object> getSelectedItemIds() {
        List<Object> ids = new ArrayList<>();
        for (Dependence dependence : itemAdapter.getCheckedItems()) {
            ids.add(dependence.getKey());
        }
        return ids;
    }

    private void getDependencies() {
        auto.panel.net.panel.ApiController.getDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), "", this.type, new auto.panel.net.panel.ApiController.DependenceListCallBack() {
            @Override
            public void onSuccess(List<Dependence> dependencies) {
                itemAdapter.setData(dependencies);
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void addDependencies(List<Dependence> dependencies) {
        ApiController.addDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), dependencies, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiPopEdit.dismiss();
                ToastUnit.showShort("新建成功");
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建失败：" + msg);
            }
        });
    }

    private void reinstallDependencies(List<Object> keys) {
        ApiController.reinstallDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("执行成功");
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("重装失败：" + msg);
                getDependencies();
            }
        });
    }

    private void deleteDependence(List<Object> keys) {
        auto.panel.net.panel.ApiController.deleteDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("执行成功");
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
                getDependencies();
            }
        });
    }
}
