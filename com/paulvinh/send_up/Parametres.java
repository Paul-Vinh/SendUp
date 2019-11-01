package com.paulvinh.send_up;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import Classe.MyCheckBoxPreference;
import Classe.MyListPreference;
import Classe.MyPreference;
import explications.Aide;

public class Parametres extends PreferenceActivity {

    private float mPreviousX;
    private Boolean nuit = false;
    private Boolean autom = false;
    private Boolean lecture = false;
    private SharedPreferences restart;
    private SharedPreferences.Editor ed;
    public static final String PREFS_NAME = "MyPrefsFile";
    private ActionBar actionBar;
    public static String color = "#84BE58";
    public static String color_int = "5";
    private String couleur_actuelle;
    private MyCheckBoxPreference auto;
    private MyCheckBoxPreference night;
    private MyCheckBoxPreference tts;
    private MyListPreference theme;
    private MyPreference button;
    private MyPreference tutoriel;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private ColorDrawable colorDrawable;
    private int coloris;
    public static Boolean n;
    private String c = "";
    /* Tuto */
    private int image = 0; // variable qui gère l'image qui apparaît dans le tuto
    private ImageButton chevron_avant;
    private ImageButton chevron_apres;
    private ImageView tuto_image;
    private TextView page;
    private int[] image_tab = {R.drawable.tuto1, R.drawable.tuto2, R.drawable.tuto3, R.drawable.tuto4, R.drawable.tuto5}; // Tableau regroupant toutes les images pour le tuto
    private View tuto;
    // AlertDialog Tuto
    private AlertDialog dialogue;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Initialisation des Preferences selon le constructeur de la classe que l'on a réimplémentée
        auto = new MyCheckBoxPreference(getApplicationContext());
        night = new MyCheckBoxPreference(getApplicationContext());
        tts = new MyCheckBoxPreference(getApplicationContext());
        theme = new MyListPreference(getApplicationContext());
        tutoriel = new MyPreference(getApplicationContext());
        button = new MyPreference(getApplicationContext());

        // Mode Nuit
        SharedPreferences pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        n = pref.getBoolean("nuit", false);

        if(n) {
            coloris = R.color.blanc;
            colorDrawable = new ColorDrawable(Color.parseColor("#080808"));
            this.findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.noir));
        }

        else {
            // Couleur verte pour l'ActionBar
            coloris = R.color.noir;
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            c = prefs.getString("couleur_actionbar", "0");
            if (c == "0")
                c = "#84BE58";
            colorDrawable = new ColorDrawable(Color.parseColor(c));
        }

        // Auto Restart
        restart = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayUseLogoEnabled(false);

        // Auto Start
        auto = (MyCheckBoxPreference) getPreferenceManager().findPreference("auto");
        auto.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                autom = (Boolean) newValue;
                if (autom) {
                    ed = restart.edit();
                    ed.putBoolean("auto", true);
                    ed.commit();
                } else {
                    ed = restart.edit();
                    ed.putBoolean("auto", false);
                    ed.commit();
                }
                return true;
            }
        });

        // Mode Nuit
        night = (MyCheckBoxPreference) getPreferenceManager().findPreference("nuit");
        night.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                nuit = (Boolean) newValue;

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("nuit", nuit);
                editor.commit();

                recreate();
                return true;
            }
        });

        // Text-to-Speech
        tts = (MyCheckBoxPreference) getPreferenceManager().findPreference("tts");
        tts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                lecture = (Boolean) newValue;
                if (lecture) {
                    MainActivity.ready = true;
                }
                else
                    MainActivity.ready = false;

                SharedPreferences.Editor editors = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editors.putBoolean("tts_coche", lecture);
                editors.commit();

                return true;
            }
        });

        // Thèmes

        // Actualiser summary
        couleur_actuelle = color_int;

        theme = (MyListPreference) getPreferenceManager().findPreference("theme");
        theme.setValueIndex(Integer.parseInt(couleur_actuelle) - 1);
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                                                public boolean onPreferenceChange(Preference preference, Object newValue) {

                                                    String coloris = (String) newValue;

                                                    if (coloris.equals("1")) {
                                                        color = "#0099CC";
                                                        color_int = "1";
                                                    } else if (coloris.equals("2")) {
                                                        color = "#FFFF33";
                                                        color_int = "2";
                                                    } else if (coloris.equals("3")) {
                                                        color = "#663300";
                                                        color_int = "3";
                                                    } else if (coloris.equals("4")) {
                                                        color = "#CC0000";
                                                        color_int = "4";
                                                    } else if (coloris.equals("5")) {
                                                        color = "#84BE58";
                                                        color_int = "5";
                                                    }
                                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                    editor.putString("couleur_actionbar", color);
                                                    editor.commit();
                                                    recreate();
                                                    return false;
                                                }
                                            }
        );


        // Tuturiel
        tutoriel = (MyPreference) findPreference("tuto");
        tutoriel.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                tuto();
                AlertDialog.Builder build = new AlertDialog.Builder(Parametres.this);
                if(n)
                    build = new AlertDialog.Builder(Parametres.this, AlertDialog.THEME_HOLO_DARK);
                build.setTitle(getResources().getText(R.string.tuto_pas_a_pas));
                build.setView(tuto);
                build.setCancelable(true);
                build.setNeutralButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                dialogue = build.create();
                dialogue.show();
                return true;
            }
        });

        // A propos de nous
        button = (MyPreference) findPreference("nous");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent message = new Intent(Parametres.this, Aide.class);
                startActivity(message);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

         /* Méthodes pour le Swipe */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;

                if(dx < -50) {
                    Intent sms = new Intent(Parametres.this, MainActivity.class);
                    startActivity(sms);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                }
                else if(dx > 50) {
                    Intent shut = new Intent(Parametres.this, Eteindre.class);
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

        if (!n) {
            switch (c) {
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void tuto() {

        // Remettre au début du tuto à chaque fois
        image = 0;

        tuto = View.inflate(this, R.layout.tuto, null);

        page = (TextView) tuto.findViewById(R.id.page);
        page.setTypeface(null, Typeface.BOLD);
        if(n)
            page.setTextColor(getResources().getColor(R.color.blanc));

        tuto_image = (ImageView) tuto.findViewById(R.id.checkbox);
        tuto_image.setImageResource(image_tab[0]);

        chevron_avant = (ImageButton) tuto.findViewById(R.id.avant);
        chevron_avant.setImageResource(R.mipmap.chevron_avant);

        // Gère le fait que le bouton "avant" n'est pas visible à l'ouverture du tuto
        if (image == 0)
            chevron_avant.setVisibility(View.INVISIBLE);

        chevron_apres = (ImageButton) tuto.findViewById(R.id.apres);
        chevron_apres.setImageResource(R.mipmap.chevron_apres);

        // Gère l'appui sur les boutons "avant" et "après" pour le tuto //
        chevron_apres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                image++;
                page.setText(String.valueOf(image + 1) + " / " + " 5");
                if (image == 4) {
                    chevron_apres.setVisibility(View.INVISIBLE);
                }
                tuto_image.setImageResource(image_tab[image]);
                if (image > 0) {
                    chevron_avant.setVisibility(View.VISIBLE);
                }
            }
        });

        chevron_avant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                image--;
                page.setText(String.valueOf(image + 1) + " / " + " 5");
                if (image == 0) {
                    chevron_avant.setVisibility(View.INVISIBLE);
                }
                tuto_image.setImageResource(image_tab[image]);
                if (image < 4) {
                    chevron_apres.setVisibility(View.VISIBLE);
                }
            }
        });
        // ******************************************** //
    }
}