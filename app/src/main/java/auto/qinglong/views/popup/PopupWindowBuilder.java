package auto.qinglong.views.popup;

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

import auto.qinglong.R;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.FixScrollView;

public class PopupWindowBuilder {
    public static final String TAG = "PopupWindowManager";

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void buildMenuWindow(Activity activity, PopMenuWindow popMenuWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_menu, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), true);
        popWindow.setContentView(view);

        LinearLayout ui_ll_container = view.findViewById(R.id.pop_common_ll_container);

        for (PopMenuItem item : popMenuWindow.getItems()) {
            View itemView = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_item_menu, null, false);
            ImageView ui_icon = itemView.findViewById(R.id.pop_common_mini_more_icon);
            TextView ui_name = itemView.findViewById(R.id.pop_common_mini_more_name);
            ui_icon.setImageDrawable(activity.getDrawable(item.getIcon()));
            ui_name.setText(item.getName());
            if (popMenuWindow.getOnActionListener() != null) {
                itemView.setOnClickListener(v -> {
                    if (popMenuWindow.getOnActionListener().onClick(item.getKey())) {
                        popWindow.dismiss();
                    }
                });
            }
            ui_ll_container.addView(itemView);
        }

        popWindow.setOnDismissListener(() -> {
            popMenuWindow.setOnActionListener(null);
            popWindow.setOnDismissListener(null);
        });

        popWindow.showAsDropDown(popMenuWindow.getTargetView(), popMenuWindow.getGravity(), 0, 0);
    }

    public static void buildEditWindow(@NonNull Activity activity, PopEditWindow popEditWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_edit, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), true);
        popWindow.setContentView(view);
        popEditWindow.setView(view);
        popEditWindow.setPopupWindow(popWindow);

        TextView ui_tv_title = view.findViewById(R.id.pop_common_tv_title);
        Button ui_bt_cancel = view.findViewById(R.id.pop_common_bt_cancel);
        Button ui_bt_confirm = view.findViewById(R.id.pop_common_bt_confirm);
        LinearLayout ui_ll_container = view.findViewById(R.id.pop_common_ll_container);
        FixScrollView ui_sl_container = view.findViewById(R.id.pop_common_sl_container);

        ui_sl_container.setMaxHeight(popEditWindow.getMaxHeight());
        ui_tv_title.setText(popEditWindow.getTitle());
        ui_bt_cancel.setText(popEditWindow.getCancelTip());
        ui_bt_confirm.setText(popEditWindow.getConfirmTip());

        //添加item
        List<EditText> itemViews = new ArrayList<>();
        List<PopEditItem> items = popEditWindow.getItems();
        for (PopEditItem item : items) {
            View itemView = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_item_edit, null, false);
            TextView ui_item_label = itemView.findViewById(R.id.pop_common_tv_label);
            EditText ui_item_value = itemView.findViewById(R.id.pop_common_et_value);
            ui_item_label.setText(item.getLabel());
            ui_item_value.setHint(item.getHint());
            ui_item_value.setText(item.getValue());
            ui_item_value.setFocusable(item.isFocusable());
            ui_item_value.setEnabled(item.isEditable());
            itemViews.add(ui_item_value);
            ui_ll_container.addView(itemView);
        }

        if (popEditWindow.getActionListener() != null) {
            ui_bt_cancel.setOnClickListener(v -> {
                boolean flag = popEditWindow.getActionListener().onCancel();
                if (flag) {
                    popWindow.dismiss();
                }
            });

            ui_bt_confirm.setOnClickListener(v -> {
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

    public static PopupWindow buildListWindow(Activity activity, PopListWindow listWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_list, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), true);
        popWindow.setContentView(view);

        TextView ui_title = view.findViewById(R.id.pop_common_tv_title);
        RecyclerView ui_recyclerView = view.findViewById(R.id.recycler_view);
        Button ui_cancel = view.findViewById(R.id.pop_common_bt_cancel);

        ui_title.setText(listWindow.getTitle());
        ui_cancel.setText(listWindow.getCancelTip());
        ui_recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        ui_recyclerView.setAdapter((RecyclerView.Adapter) listWindow.getAdapter());

        ui_cancel.setOnClickListener(v -> {
            if (listWindow.getListener() == null || listWindow.getListener().onCancel()) {
                popWindow.dismiss();
            }
        });

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            ui_recyclerView.setAdapter(null);
            listWindow.setListener(null);
            popWindow.setOnDismissListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

        return popWindow;
    }

    public static PopupWindow buildConfirmWindow(Activity activity, PopConfirmWindow popConfirmWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_confirm, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), popConfirmWindow.isFocusable());
        popWindow.setContentView(view);

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
                if (popConfirmWindow.getOnActionListener().onConfirm(false)) {
                    popWindow.dismiss();
                }
            });

            ui_bt_confirm.setOnClickListener(v -> {
                if (popConfirmWindow.getOnActionListener().onConfirm(true)) {
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

    public static PopProgressWindow buildProgressWindow(Activity activity, PopupWindow.OnDismissListener dismissListener) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_window_loading, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), false);
        popWindow.setContentView(view);

        TextView ui_tip = view.findViewById(R.id.pop_common_progress_tip);

        PopProgressWindow progressPopWindow = new PopProgressWindow(activity, popWindow, ui_tip);

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

    private static PopupWindow build(Context context, boolean isFocusable) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.anim_pop_common);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(isFocusable);
        popupWindow.setTouchable(true);

        return popupWindow;
    }
}
