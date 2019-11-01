package com.paulvinh.send_up;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.Locale;

import mail.Tab3;

public class MainActivity extends TabActivity implements TextToSpeech.OnInitListener {

    // Swipe
    private float mPreviousX;
    private TextView actif; // TextView de l'onglet actif
    private TextView inactif; // TextView de l'onglet inactif
    private View tab_actuel;
    private View tab_inactuel;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    // TTS
    public static TextToSpeech myTts;
    public static boolean ready = false;
    private ColorDrawable colorDrawable;
    private int color;
    private String coloris = "";
    private Boolean nuit;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myTts = new TextToSpeech(this, this);

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

        TabHost mTabHost = getTabHost();
        mTabHost.getTabWidget().setBackgroundDrawable(colorDrawable);
        mTabHost.getTabWidget().setStripEnabled(true);

        mTabHost.addTab(mTabHost.newTabSpec("premier").setIndicator("SMS").setContent(new Intent(this, Test.class)));
        mTabHost.addTab(mTabHost.newTabSpec("deuxieme").setIndicator("Email").setContent(new Intent(this, Tab3.class)));

        tab_actuel = mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getCurrentTab());
        tab_inactuel = mTabHost.getTabWidget().getChildTabViewAt(1);
        // Actif
        actif = (TextView)tab_actuel.findViewById(android.R.id.title);
        actif.setTextColor(getResources().getColor(R.color.blanc));
        // Inactif
        inactif = (TextView)tab_inactuel.findViewById(android.R.id.title);
        inactif.setTextColor(getResources().getColor(R.color.blanc));

        mTabHost.setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.ligne);

        // Méthode pour savoir si l'onglet courrant a changé
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                int i = getTabHost().getCurrentTab();

                getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ligne);
            }
        });
    }

     /* Méthodes pour le Swipe */

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;

                if(dx < -50) {
                    Intent event = new Intent(MainActivity.this, Event.class);
                    startActivity(event);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                }
                else if(dx > 50) {
                    Intent parametres = new Intent(MainActivity.this, Parametres.class);
                    startActivity(parametres);
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
        menu.findItem(R.id.message).setIcon(R.mipmap.lettre_selec);
        if (!nuit) {
            switch (coloris) {
                case "#0099CC":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_bleu);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_bleu);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_bleu);
                    break;
                case "#CC0000":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_rouge);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_rouge);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_rouge);
                    break;
                case "#84BE58":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_vert);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_vert);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_vert);
                    break;
                default:
                    break;
            }
        }
        else {
            menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_blanc);
            menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_blanc);
            menu.findItem(R.id.options).setIcon(R.mipmap.options_blanc);
        }
        return true;
    }

    // Gère les clics sur les items du Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.calendrier:
                Intent event = new Intent(this, Event.class);
                startActivity(event);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.shutdown:
                Intent eteindre = new Intent(this, Eteindre.class);
                startActivity(eteindre);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
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

    // Initialise le Text-To-Speech (TTS) --> la voix qui lit les SMS
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            myTts.setLanguage(Locale.FRANCE);
            ready = true;
        } else {
            ready = false;
        }
    }
}