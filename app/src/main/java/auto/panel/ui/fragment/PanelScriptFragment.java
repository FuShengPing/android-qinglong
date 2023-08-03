package auto.panel.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import auto.base.ui.popup.EditItem;
import auto.base.ui.popup.EditPopupWindow;
import auto.base.ui.popup.MenuItem;
import auto.base.ui.popup.MenuPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.panel.R;
import auto.panel.bean.panel.File;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.activity.CodeWebActivity;
import auto.panel.ui.adapter.PanelScriptItemAdapter;
import auto.panel.utils.FileUtil;

@SuppressLint("SetTextI18n")
public class PanelScriptFragment extends BaseFragment {
    public static String TAG = "PanelScriptFragment";

    private Stack<List<File>> fileStack;
    private String fileDir;
    private MenuClickListener menuClickListener;
    private PanelScriptItemAdapter adapter;

    private ImageView uiMenu;
    private ImageView uiMore;
    private TextView uiDirTip;

    private SmartRefreshLayout uiRefresh;
    private RecyclerView uiRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_script, null, false);

        uiMenu = view.findViewById(R.id.scrip_menu);
        uiMore = view.findViewById(R.id.script_more);
        uiDirTip = view.findViewById(R.id.script_dir_tip);
        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        fileStack = new Stack<>();

        adapter = new PanelScriptItemAdapter(requireContext());
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        uiRecycler.setAdapter(adapter);
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onDispatchBackKey() {
        if (fileStack.size() == 1) {
            fileStack.clear();
            return false;
        } else {
            fileStack.pop();
            adapter.setData(fileStack.peek());
            updateDir(fileStack.peek().get(0).getParent());
            return true;
        }
    }

    @Override
    public void init() {
        adapter.setScriptInterface(new PanelScriptItemAdapter.ItemActionListener() {
            @Override
            public void onEdit(File file) {
                if (file.isDir()) {
                    sortAndSetData(file.getChildren(), file.getPath());
                } else {
                    Intent intent = new Intent(getContext(), CodeWebActivity.class);
                    intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_SCRIPT);
                    intent.putExtra(CodeWebActivity.EXTRA_TITLE, file.getTitle());
                    intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_NAME, file.getTitle());
                    intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_DIR, file.getParent());
                    intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
                    startActivity(intent);
                }
            }

            @Override
            public void onMenu(View view, File file, int position) {
                showPopItemMenu(view, file, position);
            }
        });

        uiRefresh.setOnRefreshListener(refreshLayout -> getScriptFiles());

        uiMenu.setOnClickListener(v -> menuClickListener.onMenuClick());

        uiMore.setOnClickListener(this::showPopWindowMenu);
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getScriptFiles();
            }
        }, 1000);
    }

    private void updateDir(String dir) {
        fileDir = dir;
        uiDirTip.setText("/" + dir);
    }

    @Override
    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.menuClickListener = mMenuClickListener;
    }

    private void showPopItemMenu(View v, File file, int position) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(v);

        popMenuWindow.addItem(new MenuItem("copy", "复制路径", R.drawable.ic_gray_crop_free));

        if (file.isDir()) {
            popMenuWindow.addItem(new MenuItem("delete", "删除目录", R.drawable.ic_gray_delete));
        } else {
            popMenuWindow.addItem(new MenuItem("update", "更新脚本", R.drawable.ic_gray_delete));
            popMenuWindow.addItem(new MenuItem("delete", "删除脚本", R.drawable.ic_gray_delete));
        }

        popMenuWindow.setOnActionListener(key -> {
            if ("copy".equals(key)) {
                ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, file.getPath()));
                ToastUnit.showShort("已复制");
            } else if ("delete".equals(key)) {
                deleteScript(file, position);
            } else if ("update".equals(key)) {

            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopWindowMenu(View v) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(v);
        popMenuWindow.addItem(new MenuItem("addDir", "新建目录", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new MenuItem("addFile", "新建脚本", R.drawable.ic_gray_file));

        popMenuWindow.setOnActionListener(key -> {
            if ("addDir".equals(key)) {
                showPopAddDir();
            } else if ("addFile".equals(key)) {
                showPopAddFile();
            }
            return true;
        });
        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopAddDir() {
        EditPopupWindow editPopupWindow = new EditPopupWindow();
        editPopupWindow.setTitle("新建目录");
        EditItem itemName = new EditItem("name", null, "目录名", "请输入目录名");
        EditItem itemDir = new EditItem("dir", fileDir, "父目录", "", false, false);

        editPopupWindow.addItem(itemName);
        editPopupWindow.addItem(itemDir);

        editPopupWindow.setActionListener(map -> {
            String name = map.get("name");
            String dir = map.get("dir");

            if (TextUnit.isEmpty(name)) {
                ToastUnit.showShort("请输入目录名");
                return false;
            }

            File file = new File();
            file.setTitle(name);
            file.setParent(dir);
            file.setDir(true);

            addScript(file);

            return true;
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), editPopupWindow);
    }

    private void showPopAddFile() {
        EditPopupWindow editPopupWindow = new EditPopupWindow();
        editPopupWindow.setTitle("新建文件");
        EditItem itemName = new EditItem("name", null, "文件名", "请输入文件名");
        EditItem itemFile = new EditItem("path", null, "本地文件", "请输入本地文件路径(可选)");
        EditItem itemDir = new EditItem("dir", fileDir, "父目录", "", false, false);

        editPopupWindow.addItem(itemName);
        editPopupWindow.addItem(itemFile);
        editPopupWindow.addItem(itemDir);

        editPopupWindow.setActionListener(map -> {
            String name = map.get("name");
            String path = map.get("path");
            String dir = map.get("dir");

            if (TextUnit.isEmpty(name)) {
                ToastUnit.showShort("请输入文件名");
                return false;
            }

            File file = new File();
            file.setTitle(name);
            file.setParent(dir);
            file.setDir(false);

            if (TextUnit.isFull(path)) {
                if (!FileUtil.checkStoragePermission()) {
                    ToastUnit.showShort("请授予读写权限");
                    FileUtil.requestStoragePermission(requireActivity());
                    return false;
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(path);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        file.setContent(stringBuilder.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUnit.showShort("读取文件失败");
                        return false;
                    }
                }
            }

            addScript(file);

            return true;
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), editPopupWindow);
    }

    private void sortAndSetData(List<File> files, String dir) {
        Collections.sort(files);
        fileStack.add(files);
        adapter.setData(files);
        updateDir(dir);
    }

    private void getScriptFiles() {
        auto.panel.net.panel.ApiController.getScripts(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.panel.net.panel.ApiController.FileListCallBack() {
            @Override
            public void onSuccess(List<File> files) {
                fileStack.clear();
                sortAndSetData(files, "");
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_load_failure_header) + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void addScript(File file) {
        ApiController.addScript(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), file, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("新建成功");
                getScriptFiles();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建失败：" + msg);
            }
        });
    }

    private void update(File file, String content) {

    }

    private void deleteScript(File file, int position) {
        ApiController.deleteScript(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), file, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort(getString(R.string.tip_delete_success));
                adapter.removeItem(position);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_delete_failure_header) + msg);
            }
        });
    }
}