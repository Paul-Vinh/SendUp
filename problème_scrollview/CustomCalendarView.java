package problème_scrollview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.CalendarView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CustomCalendarView extends CalendarView
{
    public CustomCalendarView(Context context, AttributeSet attrs, int
            defStyle)
    {
        super(context, attrs, defStyle);
    }

    public CustomCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomCalendarView(Context context)
    {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        /* Prevent parent controls from stealing our events once we've
gotten a touch down */
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}