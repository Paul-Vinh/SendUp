package problème_scrollview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.TextView;

public class CustomDatePicker extends DatePicker
{
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public CustomDatePicker(Context context, AttributeSet attrs, int
            defStyle)
    {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public CustomDatePicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public CustomDatePicker(Context context)
    {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomDatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }
}