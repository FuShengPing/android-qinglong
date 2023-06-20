package auto.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import auto.base.R;


/**
 * @author wsfsp4
 * @version 2023.06.15
 */
@SuppressLint("AppCompatCustomView")
public class EditText extends android.widget.EditText {
    public EditText(Context context) {
        super(context);
        init();
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.style_edit_text_border_gray_blue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTextCursorDrawable(R.drawable.style_cursor_blue);
        }
    }
}
