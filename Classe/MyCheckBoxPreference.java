package Classe;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.paulvinh.send_up.Parametres;
import com.paulvinh.send_up.R;

public class MyCheckBoxPreference extends CheckBoxPreference {

    public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCheckBoxPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView titre = (TextView) view.findViewById(android.R.id.title); // Titre
        TextView indic = (TextView) view.findViewById(android.R.id.summary); // Sommaire - Indications
        CheckBox cb = (CheckBox) view.findViewById(android.R.id.checkbox);

        // Savoir si Mode Nuit
        if(Parametres.n) {
            titre.setTextColor(getContext().getResources().getColor(R.color.blanc));
            indic.setTextColor(getContext().getResources().getColor(R.color.blanc));
            cb.setButtonDrawable(R.xml.check_night);
        }
        else {
            titre.setTextColor(getContext().getResources().getColor(R.color.noir));
            indic.setTextColor(getContext().getResources().getColor(R.color.noir));
        }
    }
}