package auto.base.ui.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto.base.R;
import auto.base.ui.view.FixScrollView;
import auto.base.util.WindowUnit;

public class PopupWindowBuilder {
    public static final String TAG = "PopupWindowManager";

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void buildMenuWindow(Activity activity, MenuPopupWindow popMenuWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_menu, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);

        LinearLayout uiLlContainer = view.findViewById(R.id.pop_common_ll_container);

        for (MenuItem item : popMenuWindow.getItems()) {
            View itemView = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_menu_item, null, false);
            ImageView uiIcon = itemView.findViewById(R.id.pop_common_mini_more_icon);
            TextView uiName = itemView.findViewById(R.id.pop_common_mini_more_name);

            uiIcon.setImageDrawable(activity.getDrawable(item.getIcon()));
            uiName.setText(item.getName());

            if (popMenuWindow.getOnActionListener() != null) {
                itemView.setOnClickListener(v -> {
                    if (popMenuWindow.getOnActionListener().onClick(item.getKey())) {
                        popWindow.dismiss();
                    }
                });
            }
            uiLlContainer.addView(itemView);
        }

        popWindow.setOnDismissListener(() -> {
            popMenuWindow.setOnActionListener(null);
            popWindow.setOnDismissListener(null);
        });

        // 获取指定视图的位置
        int[] location = new int[2];
        popMenuWindow.getTargetView().getLocationOnScreen(location);

        popWindow.showAtLocation(popMenuWindow.getTargetView(), Gravity.NO_GRAVITY, location[0], location[1] + popMenuWindow.getTargetView().getHeight());
    }

    public static void buildEditWindow(@NonNull Activity activity, EditPopupWindow popEditWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_edit_window, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);
        popEditWindow.setView(view);
        popEditWindow.setPopupWindow(popWindow);

        TextView uiTitle = view.findViewById(R.id.pop_common_tv_title);
        Button uiCancel = view.findViewById(R.id.pop_common_bt_cancel);
        Button uiConfirm = view.findViewById(R.id.pop_common_bt_confirm);
        LinearLayout uiLlContainer = view.findViewById(R.id.pop_common_ll_container);
        FixScrollView uiSlContainer = view.findViewById(R.id.pop_common_sl_container);

        uiSlContainer.setMaxHeight(popEditWindow.getMaxHeight());
        uiTitle.setText(popEditWindow.getTitle());
        uiCancel.setText(popEditWindow.getCancelTip());
        uiConfirm.setText(popEditWindow.getConfirmTip());

        //添加item
        List<EditText> itemViews = new ArrayList<>();
        List<EditItem> items = popEditWindow.getItems();
        for (EditItem item : items) {
            View itemView = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_edit_item, null, false);
            TextView ui_item_label = itemView.findViewById(R.id.pop_common_tv_label);
            EditText ui_item_value = itemView.findViewById(R.id.pop_common_et_value);
            ui_item_label.setText(item.getLabel());
            ui_item_value.setHint(item.getHint());
            ui_item_value.setText(item.getValue());
            ui_item_value.setFocusable(item.isFocusable());
            ui_item_value.setEnabled(item.isEditable());
            itemViews.add(ui_item_value);
            uiLlContainer.addView(itemView);
        }

        if (popEditWindow.getActionListener() != null) {
            uiCancel.setOnClickListener(v -> {
                boolean flag = popEditWindow.getActionListener().onCancel();
                if (flag) {
                    popWindow.dismiss();
                }
            });

            uiConfirm.setOnClickListener(v -> {
                Map<String, String> map = new HashMap<>();
                for (int k = 0; k < itemViews.size(); k++) {
                    map.put(items.get(k).getKey(), itemViews.get(k).getText().toString().trim());
                }
                if (popEditWindow.getActionListener().onConfirm(map)) {
                    popWindow.dismiss();
                }
            });
        }

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            itemViews.clear();
            items.clear();
            popEditWindow.setActionListener(null);
            popWindow.setOnDismissListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public static PopupWindow buildListWindow(Activity activity, ListPopupWindow listWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_list, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);

        TextView uiTitle = view.findViewById(R.id.pop_common_tv_title);
        RecyclerView uiRecyclerView = view.findViewById(R.id.recycler_view);
        Button uiCancel = view.findViewById(R.id.pop_common_bt_cancel);

        uiTitle.setText(listWindow.getTitle());
        uiCancel.setText(listWindow.getCancelTip());
        uiRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        uiRecyclerView.setAdapter((RecyclerView.Adapter) listWindow.getAdapter());

        uiCancel.setOnClickListener(v -> {
            if (listWindow.getListener() == null || listWindow.getListener().onCancel()) {
                popWindow.dismiss();
            }
        });

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            uiRecyclerView.setAdapter(null);
            listWindow.setListener(null);
            popWindow.setOnDismissListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

        return popWindow;
    }

    public static PopupWindow buildConfirmWindow(Activity activity, ConfirmPopupWindow popConfirmWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_confirm_window, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);
        popWindow.setFocusable(popConfirmWindow.isFocusable());

        TextView ui_tv_title = view.findViewById(R.id.pop_common_tv_title);
        TextView ui_tv_content = view.findViewById(R.id.pop_common_tv_content);
        Button ui_bt_cancel = view.findViewById(R.id.pop_common_bt_cancel);
        Button ui_bt_confirm = view.findViewById(R.id.pop_common_bt_confirm);
        FixScrollView ui_sl_container = view.findViewById(R.id.pop_common_sl_container);

        ui_sl_container.setMaxHeight(popConfirmWindow.getMaxHeight());
        ui_tv_title.setText(popConfirmWindow.getTitle());
        ui_tv_content.setText(popConfirmWindow.getContent());
        ui_bt_confirm.setText(popConfirmWindow.getConfirmTip());
        ui_bt_cancel.setText(popConfirmWindow.getCancelTip());

        if (popConfirmWindow.getOnActionListener() != null) {
            ui_bt_cancel.setOnClickListener(v -> {
                if (popConfirmWindow.getOnActionListener().onCancel()) {
                    popWindow.dismiss();
                }
            });

            ui_bt_confirm.setOnClickListener(v -> {
                if (popConfirmWindow.getOnActionListener().onConfirm()) {
                    popWindow.dismiss();
                }
            });
        }

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            popConfirmWindow.setOnActionListener(null);
            popWindow.setOnDismissListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
        return popWindow;
    }

    public static ProgressPopupWindow buildProgressWindow(Activity activity, PopupWindow.OnDismissListener dismissListener) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_loading_window, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);
        popWindow.setFocusable(false);

        TextView uiTip = view.findViewById(R.id.pop_common_progress_tip);

        ProgressPopupWindow progressPopWindow = new ProgressPopupWindow(activity, popWindow, uiTip);

        popWindow.setOnDismissListener(() -> {
            if (dismissListener != null) {
                dismissListener.onDismiss();
            }
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
        return progressPopWindow;
    }

    private static PopupWindow build(Context context, int width, int height) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setAnimationStyle(R.style.base_anim_pop_common);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return popupWindow;
    }
}
