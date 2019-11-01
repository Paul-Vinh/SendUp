package problème_swipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.paulvinh.send_up.Eteindre;
import com.paulvinh.send_up.Event;
import com.paulvinh.send_up.MainActivity;
import com.paulvinh.send_up.Parametres;
import com.paulvinh.send_up.R;
import com.paulvinh.send_up.Tab1;

import mail.Tab3;

public class Verticale_ScrollView extends ScrollView {

    private float mPreviousX;

    public Verticale_ScrollView(Context context) {
        super(context);
    }

    public Verticale_ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Verticale_ScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                if(dx < -50) {
                    if(Tab1.swipe == 1 || Tab3.swipe == 1) {
                        Tab1.swipe = 0;
                        Tab3.swipe = 0;
                        Intent event = new Intent(getContext(), Event.class);
                        getContext().startActivity(event);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    }
                    else if(Event.swipe == 1) {
                        Event.swipe = 0;
                        Intent shut = new Intent(getContext(), Eteindre.class);
                        getContext().startActivity(shut);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    }
                }
                else if(dx > 50) {
                    if(Tab1.swipe == 1 || Tab3.swipe == 1) {
                        Tab1.swipe = 0;
                        Tab3.swipe = 0;
                        Intent options = new Intent(getContext(), Parametres.class);
                        getContext().startActivity(options);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                    }
                    else if(Event.swipe == 1) {
                        Event.swipe = 0;
                        Intent sms = new Intent(getContext(), MainActivity.class);
                        getContext().startActivity(sms);
                        ((Activity)getContext()).overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                    }
                }
                break;
        }
        mPreviousX = x;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}