package Classe;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.paulvinh.send_up.Parametres;
import com.paulvinh.send_up.R;

public class MyListPreference extends ListPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView titre1 = (TextView) view.findViewById(android.R.id.title); // Titre
        TextView indic1 = (TextView) view.findViewById(android.R.id.summary); // Sommaire - Indications

        // Savoir si Mode Nuit
        if(Parametres.n) {
            titre1.setTextColor(getContext().getResources().getColor(R.color.blanc));
            indic1.setTextColor(getContext().getResources().getColor(R.color.blanc));
        }
        else {
            titre1.setTextColor(getContext().getResources().getColor(R.color.noir));
            indic1.setTextColor(getContext().getResources().getColor(R.color.noir));
        }
    }
}
