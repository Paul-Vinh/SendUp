package com.paulvinh.send_up;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.FROYO)
public class Event extends Activity {

    public static int swipe = 1;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private ColorDrawable colorDrawable;
    private int color;
    private Boolean nuit;
    private String coloris = "";
    private SharedPreferences pref;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);

        // Mode Nuit
        pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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

        if(nuit) {
            //setTextColor(getResources().getColor(color));
        }

        // Mettre en blanc, si Mode Nuit, le TimePicker, les CalendarViews et la Checkbox
        if (nuit) {
            //setButtonDrawable(R.xml.check_night);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_out, R.anim.push_up_in);
    }

    // Initialise le menu quand tu cliques sur la touche Menu ou sur la barre d'outils (ActionBar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_selec);

        if (!nuit) {
            switch (coloris) {
                case "#0099CC":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_bleu);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_bleu);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_bleu);
                    break;
                case "#CC0000":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_rouge);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_rouge);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_rouge);
                    break;
                case "#84BE58":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_vert);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_vert);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_vert);
                    break;
                default:
                    break;
            }
        }
        else {
            menu.findItem(R.id.message).setIcon(R.mipmap.lettre_blanc);
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
            case R.id.message:
                swipe = 0;
                Intent sms = new Intent(this, MainActivity.class);
                startActivity(sms);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.calendrier:
                swipe = 0;
                Intent event = new Intent(this, Event.class);
                startActivity(event);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.shutdown:
                swipe = 0;
                Intent eteindre = new Intent(this, Eteindre.class);
                startActivity(eteindre);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.options:
                swipe = 0;
                Intent options = new Intent(this, Parametres.class);
                startActivity(options);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}