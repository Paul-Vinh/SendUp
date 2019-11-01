package explications;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvinh.send_up.Eteindre;
import com.paulvinh.send_up.Event;
import com.paulvinh.send_up.Parametres;
import com.paulvinh.send_up.Fichier;
import com.paulvinh.send_up.MainActivity;
import com.paulvinh.send_up.R;

import mail.Tab3;

public class Aide extends Activity implements TextWatcher {

    private TextView intro;
    private TextView texte;
    private EditText bugs;
    private RatingBar ratingBar;
    private Button btnSubmit;
    private float mPreviousX;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private ColorDrawable colorDrawable;
    private int color;
    private Boolean nuit;
    private String coloris = "";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aide);

        // Mode Nuit
        SharedPreferences pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nuit = pref.getBoolean("nuit", false);

        if(nuit) {
            color = R.color.blanc;
            colorDrawable = new ColorDrawable(Color.parseColor("#080808"));
            this.findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.noir));
        }

        else {
            // Couleur verte pour l'ActionBar
            color = R.color.noir;
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            coloris = prefs.getString("couleur_actionbar", "0");
            if (coloris == "0")
                coloris = "#84BE58";
            colorDrawable = new ColorDrawable(Color.parseColor(coloris));
        }

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayUseLogoEnabled(false);

        addListenerOnButton();
    }

             /* Méthodes pour le Swipe */

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;

                if(dx < -50) {
                    Intent sms = new Intent(Aide.this, MainActivity.class);
                    startActivity(sms);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                }
                else if(dx > 50) {
                    Intent shut = new Intent(Aide.this, Eteindre.class);
                    startActivity(shut);
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                }
        }

        mPreviousX = x;
        return true;
    }
    // ***************************************** //

    // Initialise le menu quand tu cliques sur la touche Menu ou sur la barre d'outils (ActionBar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.options).setIcon(R.mipmap.options_selec);

        if (!nuit) {
            switch (coloris) {
                case "#0099CC":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_bleu);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_bleu);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_bleu);
                    break;
                case "#CC0000":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_rouge);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_rouge);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_rouge);
                    break;
                case "#84BE58":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_vert);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_vert);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_vert);
                    break;
                default:
                    break;
            }
        }
        else {
            menu.findItem(R.id.message).setIcon(R.mipmap.lettre_blanc);
            menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_blanc);
            menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_blanc);
        }

        return true;
    }

    // Gère les clics sur les items du Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.message:
                Intent sms = new Intent(this, MainActivity.class);
                startActivity(sms);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.calendrier:
                Intent event = new Intent(this, Event.class);
                startActivity(event);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.shutdown:
                Intent eteindre = new Intent(this, Eteindre.class);
                startActivity(eteindre);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.options:
                Intent options = new Intent(this, Parametres.class);
                startActivity(options);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_out, R.anim.push_up_in);
    }

    public void addListenerOnButton() {

        intro = (TextView) findViewById(R.id.intro);
        intro.setTextColor(getResources().getColor(color));
        texte = (TextView) findViewById(R.id.text);
        texte.setTextColor(getResources().getColor(color));
        bugs = (EditText) findViewById(R.id.bugs);
        bugs.addTextChangedListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTextColor(getResources().getColor(color));

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Aide.this, Tab3.class);
                Fichier.add = "";
                Fichier.pass = "";
                Fichier.sub = "";
                Fichier.chemin = "";
                Fichier.destination = "sendup.app@gmail.com";
                Fichier.sub = getResources().getText(R.string.ameliorations_pb).toString();
                Fichier.body = getResources().getText(R.string.note) + " " + String.valueOf(ratingBar.getRating()) + "/5.0" + "\n\n" + getResources().getText(R.string.pb_ameliorations) + "\n" + bugs.getText().toString();
                startActivity(intent);
                Toast.makeText(Aide.this,
                        String.valueOf(ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    // Colorer les EditTexts en focntion du mode (Jour/Nuit)
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        bugs.setTextColor(getResources().getColor(color));
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void afterTextChanged(Editable editable) {
    }
}
