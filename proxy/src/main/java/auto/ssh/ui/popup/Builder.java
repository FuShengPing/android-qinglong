package auto.ssh.ui.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import auto.base.util.WindowUnit;
import auto.ssh.R;

/**
 * @author wsfsp4
 * @version 2023.07.28
 */
public class Builder {
    public static void buildInputWindow(Activity activity, InputPopup inputPopup) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.proxy_pop_input, null, false);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);

        TextView uiTitle = view.findViewById(R.id.proxy_pop_input_title);
        EditText uiValue = view.findViewById(R.id.proxy_pop_input_value);
        View uiCancel = view.findViewById(R.id.proxy_pop_input_cancel);
        View uiConfirm = view.findViewById(R.id.proxy_pop_input_confirm);

        uiTitle.setText(inputPopup.getTitle());
        uiValue.setHint(inputPopup.getHint());
        uiValue.setText(inputPopup.getValue());
        uiValue.setInputType(inputPopup.getType());
        uiValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputPopup.getLength())});

        if (inputPopup.getActionListener() != null) {
            uiCancel.setOnClickListener(v -> {
                boolean dismiss = inputPopup.getActionListener().onCancel();
                if (dismiss) {
                    popWindow.dismiss();
                }
            });

            uiConfirm.setOnClickListener(v -> {
                String value = uiValue.getText().toString().trim();
                if (value.isEmpty() && !inputPopup.isNullable()) {
                    return;
                }

                boolean dismiss = inputPopup.getActionListener().onConfirm(value);
                if (dismiss) {
                    popWindow.dismiss();
                }
            });
        }

        popWindow.setOnDismissListener(() -> {
            WindowUnit.setBackgroundAlpha(activity, 1.0f);
            if (inputPopup.getDismissListener() != null) {
                inputPopup.getDismissListener().onDismiss();
            }
            inputPopup.setActionListener(null);
            inputPopup.setDismissListener(null);
            popWindow.setOnDismissListener(null);
        });

        WindowUnit.setBackgroundAlpha(activity, 0.5f);
        popWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public static void buildSelectWindow(Activity activity, View target, SelectPopup selectPopup) {
        View view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.proxy_pop_select, null, false);
        LinearLayout layout = view.findViewById(R.id.proxy_pop_select);
        PopupWindow popWindow = build(activity.getBaseContext(), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setContentView(view);

        List<SelectItem> items = selectPopup.getItems();

        for (SelectItem item : items) {
            TextView itemView = (TextView) LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.proxy_pop_select_item, null, false);
            itemView.setText(item.getTitle());
            if (item.isSelected()) {
                itemView.setBackgroundColor(activity.getResources().getColor(R.color.proxy_pop_select_item_selected, null));
                itemView.setTextColor(activity.getResources().getColor(R.color.proxy_pop_select_text_selected, null));
            } else {
                itemView.setTextColor(activity.getResources().getColor(R.color.proxy_pop_select_text, null));
            }

            itemView.setOnClickListener(v -> {
                boolean dismiss = selectPopup.getSelectListener().onSelect(item.getValue());
                if (dismiss) {
                    popWindow.dismiss();
                }
            });

            layout.addView(itemView);
        }

        popWindow.setOnDismissListener(() -> {
            if (selectPopup.getDismissListener() != null) {
                selectPopup.getDismissListener().onDismiss();
            }
            selectPopup.setDismissListener(null);
            popWindow.setOnDismissListener(null);
        });

        popWindow.showAtLocation(target, Gravity.BOTTOM, 0, 0);
    }

    private static PopupWindow build(Context context, int width, int height) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setAnimationStyle(auto.base.R.style.base_anim_pop_common);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return popupWindow;
    }
}
