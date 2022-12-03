package auto.qinglong.views.popup;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.MyScrollView;

public class PopupWindowManager {

    public static final String TAG = "PopupWindowManager";

    public static PopupWindow buildContentWindow() {
        return null;
    }

    public static PopupWindow buildEditWindow(Activity activity, EditWindow editWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_common_edit, null, false);

        TextView ui_tv_title = view.findViewById(R.id.pop_common_tv_title);
        Button ui_bt_cancel = view.findViewById(R.id.pop_common_bt_cancel);
        Button ui_bt_confirm = view.findViewById(R.id.pop_common_bt_confirm);
        LinearLayout ui_ll_container = view.findViewById(R.id.pop_common_ll_container);
        MyScrollView ui_sl_container = view.findViewById(R.id.pop_common_sl_container);

        ui_sl_container.setMaxHeight(editWindow.getMaxHeight());
        ui_tv_title.setText(editWindow.getTitle());
        ui_bt_cancel.setText(editWindow.getCancelTip());
        ui_bt_confirm.setText(editWindow.getConfirmTip());

        //添加item
        List<EditText> itemViews = new ArrayList<>();
        List<EditWindowItem> items = editWindow.getItems();
        for (EditWindowItem item : items) {
            View itemView = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.item_pop_common_edit, null, false);
            TextView ui_item_label = itemView.findViewById(R.id.pop_common_tv_label);
            EditText ui_item_value = itemView.findViewById(R.id.pop_common_et_value);
            ui_item_label.setText(item.getLabel());
            ui_item_value.setHint(item.getHint());
            ui_item_value.setText(item.getValue());
            ui_item_value.setFocusable(item.isFocusable());
            ui_item_value.setEnabled(item.isEditable());
            ui_ll_container.addView(itemView);
            itemViews.add(ui_item_value);
        }

        PopupWindow popWindow = build(activity.getBaseContext(), true, view);
        popWindow.setContentView(view);

        //取消
        ui_bt_cancel.setOnClickListener(v -> {
            boolean flag = editWindow.getActionListener().onCancel();
            if (flag) {
                popWindow.dismiss();
            }
        });

        //确定
        ui_bt_confirm.setOnClickListener(v -> {
            Map<String, String> map = new HashMap<>();

            for (int k = 0; k < itemViews.size(); k++) {
                map.put(items.get(k).getKey(), itemViews.get(k).getText().toString().trim());
            }

            boolean flag = editWindow.getActionListener().onConfirm(map);
            if (flag) {
                popWindow.dismiss();
            }
        });

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            itemViews.clear();
            items.clear();
            editWindow.setActionListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
        return popWindow;
    }

    public static PopupWindow buildConfirmWindow(Activity activity, ConfirmWindow confirmWindow) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.pop_common_confirm, null, false);

        TextView ui_tv_title = view.findViewById(R.id.pop_common_tv_title);
        TextView ui_tv_content = view.findViewById(R.id.pop_common_tv_content);
        Button ui_bt_cancel = view.findViewById(R.id.pop_common_bt_cancel);
        Button ui_bt_confirm = view.findViewById(R.id.pop_common_bt_confirm);
        MyScrollView ui_sl_container = view.findViewById(R.id.pop_common_sl_container);

        ui_sl_container.setMaxHeight(confirmWindow.getMaxHeight());
        ui_tv_title.setText(confirmWindow.getTitle());
        ui_tv_content.setText(confirmWindow.getContent());
        ui_bt_confirm.setText(confirmWindow.getConfirmTip());
        ui_bt_cancel.setText(confirmWindow.getCancelTip());

        PopupWindow popWindow = build(activity.getBaseContext(), confirmWindow.isFocusable(), view);
        popWindow.setContentView(view);

        //取消
        ui_bt_cancel.setOnClickListener(v -> {
            boolean flag = confirmWindow.getConfirmInterface().onConfirm(false);
            if (flag) {
                popWindow.dismiss();
            }
        });

        //确定
        ui_bt_confirm.setOnClickListener(v -> {
            boolean flag = confirmWindow.getConfirmInterface().onConfirm(true);
            if (flag) {
                popWindow.dismiss();
            }
        });

        //窗体消失监听
        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            confirmWindow.setConfirmInterface(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
        return popWindow;
    }

    private static PopupWindow build(Context context, boolean isFocusable, View view) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(isFocusable);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.anim_pop_common);

        view.setFocusable(true);
        view.setClickable(true);

        return popupWindow;
    }
}
