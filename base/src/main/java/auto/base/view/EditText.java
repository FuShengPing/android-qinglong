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
    private final Context context;


    public EditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.style_edit_text_border_gray_blue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTextCursorDrawable(R.drawable.style_edit__cursor_blue);
        }
    }
}
