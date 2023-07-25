package auto.base.ui.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import auto.base.R;
import auto.base.util.WindowUnit;

/**
 * @author wsfsp4
 * @version 2023.06.20
 */
public class MyEditText extends LinearLayout {
    private final Context context;
    private TextView mTextView;
    private EditText mEditText;

    public MyEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MyEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public void setTitleVisible(boolean visible) {
        if (visible) {
            this.mTextView.setVisibility(VISIBLE);
        } else {
            this.mTextView.setVisibility(GONE);
        }
    }

    public void setTitle(String str) {
        mTextView.setText(str);
    }

    public String getValue() {
        return mEditText.getText().toString();
    }

    private void init() {
        setOrientation(VERTICAL);

        mTextView = new TextView(getContext());
        mEditText = new EditText(getContext());

        mTextView.setText("远程地址");
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mTextView.setTextColor(getResources().getColor(R.color.text_color_49, null));
        mTextView.setPadding(0, 0, 0, WindowUnit.dip2px(context, 10));

        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mEditText.setPadding(10, 15, 10, 15);
        mEditText.setBackgroundResource(R.drawable.style_edit_text_border_gray_blue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mEditText.setTextCursorDrawable(R.drawable.style_edit_cursor_blue);
        }

        mEditText.setSelected(true);

        addView(mTextView);
        addView(mEditText);
    }
}
