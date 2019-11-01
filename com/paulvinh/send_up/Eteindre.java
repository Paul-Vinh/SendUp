package com.paulvinh.send_up;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Eteindre extends Activity implements AdapterView.OnItemSelectedListener {

    private CalendarView pickerDate2;
    private TimePicker pickerTime2;
    private Spinner spin;
    private Button shut;
    private PendingIntent pendingIntent;
    private Calendar objectif;
    private long tps;
    public static int choix3 = 0;
    public static int choice = 0;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private int eviter; // variable qui récupère la valeur du SharedPreferences
    private float mPreviousX;
    private ColorDrawable colorDrawable;
    private int color;
    private TextView info;
    private TextView info_suite;
    private SimpleDateFormat sdf;
    private int jour;
    private int mois;
    private int annee;
    private Boolean nuit;
    private String coloris = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shutdown);

        // Mode Nuit
        SharedPreferences pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nuit = pref.getBoolean("nuit", false);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        eviter = prefs.getInt("eviter_eteindre", 0);
        View checkBoxView = View.inflate(this, R.layout.checkbox_alertdialog, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setText(" " + getResources().getText(R.string.pas_rappeler));
        if(nuit) {
            checkBox.setTextColor(getResources().getColor(R.color.blanc));
            checkBox.setButtonDrawable(R.xml.check_night);
        }

        if (eviter != 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(nuit)
                builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
            builder.setTitle(getResources().getText(R.string.infos_importantes));
            builder.setMessage(getResources().getText(R.string.info_root_1) + "\n" + getResources().getText(R.string.info_root_2))
                    .setView(checkBoxView)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.connaissance), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (checkBox.isChecked()) {
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putInt("eviter_eteindre", 1);
                                editor.commit();
                            }
                        }
                    }).show();
        }

        if(nuit) {
            color = R.color.blanc;
            colorDrawable = new ColorDrawable(Color.parseColor("#080808"));
            this.findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.noir));
        }

        else {
            // Couleur verte pour l'ActionBar
            color = R.color.noir;
            SharedPreferences p = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            coloris = p.getString("couleur_actionbar", "0");
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

        info = (TextView) findViewById(R.id.info);
        info.setTextColor(getResources().getColor(color));
        info_suite = (TextView) findViewById(R.id.info_suite);
        info_suite.setTextColor(getResources().getColor(color));

        pickerTime2 = (TimePicker) findViewById(R.id.time_pick);
        pickerTime2.setIs24HourView(true);
        pickerDate2 = (CalendarView) findViewById(R.id.date_pick);
        pickerDate2.setShowWeekNumber(false);
        pickerDate2.setFirstDayOfWeek(2);
        shut = (Button) findViewById(R.id.shut);
        shut.setTextColor(getResources().getColor(color));

        spin = (Spinner) findViewById(R.id.list);
        String[] eteindre = { getResources().getText(R.string.eteindre_tel).toString(), getResources().getText(R.string.eteindre_wifi).toString(),
                getResources().getText(R.string.eteindre_bluetooth).toString(), getResources().getText(R.string.eteindre_musique).toString()};

        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        eteindre) {

                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        ((TextView) v).setTextColor(
                                getResources()
                                        .getColorStateList(color));
                        return v;
                    }

                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView,
                                parent);
                        if(nuit)
                            v.setBackgroundResource(R.color.noir);

                        ((TextView) v).setTextColor(getResources().getColorStateList(color));

                        return v;
                    }
                };
        spin.setAdapter(stringArrayAdapter);
        spin.setOnItemSelectedListener(this);

        shut.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                shutdown();
            }
        });

        // Mettre en blanc, si Mode Nuit, le TimePicker
        if (nuit) {
            Resources system = Resources.getSystem();
            int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
            int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
            int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

            NumberPicker hour_numberpicker = (NumberPicker) pickerTime2.findViewById(hour_numberpicker_id);
            NumberPicker minute_numberpicker = (NumberPicker) pickerTime2.findViewById(minute_numberpicker_id);
            NumberPicker ampm_numberpicker = (NumberPicker) pickerTime2.findViewById(ampm_numberpicker_id);

            set_numberpicker_text_colour(hour_numberpicker);
            set_numberpicker_text_colour(minute_numberpicker);
            set_numberpicker_text_colour(ampm_numberpicker);

            // CalendarView
            ViewGroup vg = (ViewGroup) pickerDate2.getChildAt(0);
            View child = vg.getChildAt(0);

            if(child instanceof TextView) {
                ((TextView)child).setTextColor(getResources().getColor(color));
            }
            pickerDate2.setUnfocusedMonthDateColor(Color.GRAY);
            pickerDate2.setFocusedMonthDateColor(getResources().getColor(color));
            pickerDate2.setWeekSeparatorLineColor(getResources().getColor(color));
            pickerDate2.setWeekDayTextAppearance(R.style.Header);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void set_numberpicker_text_colour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.blanc);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                number_picker.invalidate();
            }
            catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException e){
                Log.w("setNumberPickerTextColor", e);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView,View v,int position,long id) {
        switch(position) {
            case 0:
                choice = 1;
                break;
            case 1:
                choice = 2;
                break;
            case 2:
                choice = 3;
                break;
            case 3:
                choice = 4;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void shutdown() {
        choix3 = 3;

        Intent myIntent = new Intent(Eteindre.this, AlarmeService.class);

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        jour = Integer.parseInt(sdf.format(new Date(pickerDate2.getDate())).substring(0, 2));
        mois = Integer.parseInt(sdf.format(new Date(pickerDate2.getDate())).substring(3, 5));
        annee = Integer.parseInt(sdf.format(new Date(pickerDate2.getDate())).substring(6));

        pendingIntent = PendingIntent.getService(Eteindre.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        tps = convert();
        calendar.add(Calendar.MILLISECOND, (int) tps);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Régler problème du '0' manquant dans les minutes
        String minute = String.valueOf(objectif.get(Calendar.MINUTE));

        if(Integer.parseInt(minute) < 10){
            char min = minute.charAt(0);
            minute = "";
            minute += '0';
            minute += min;
        }

        switch(choice){
            case 1:
                Toast.makeText(Eteindre.this,
                        getResources().getText(R.string.shut_tel) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                                "h" + minute,
                        Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(Eteindre.this,
                        getResources().getText(R.string.shut_wifi) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                                "h" + minute,
                        Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(Eteindre.this,
                        getResources().getText(R.string.shut_bluetooth) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                                "h" + minute,
                        Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(Eteindre.this,
                        getResources().getText(R.string.shut_musique) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                                "h" + minute,
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }

    // Fonction gérant le calcul du temps programmé - le temps actuel (durée d'attente avant l'envoi)
    public long convert() {
        // date et heure actuelle récupérée
        Calendar current = Calendar.getInstance();

        // Calendar qui gère la date précise de l'envoi programmé

        objectif = Calendar.getInstance();
        objectif.set(Calendar.MILLISECOND, 0);
        objectif.set(Calendar.SECOND, 0);
        objectif.set(Calendar.MINUTE, pickerTime2.getCurrentMinute());
        objectif.set(Calendar.HOUR_OF_DAY, pickerTime2.getCurrentHour());
        objectif.set(Calendar.DAY_OF_MONTH, jour);
        objectif.set(Calendar.MONTH, mois - 1);
        objectif.set(Calendar.YEAR, annee);

        return objectif.getTimeInMillis() - (current.getTimeInMillis() - current.getTimeInMillis() % 60000);
    }
    
        /* Méthodes pour le Swipe */

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;

                if(dx < -50) {
                    Intent options = new Intent(Eteindre.this, Parametres.class);
                    startActivity(options);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                }
                else if(dx > 50) {
                    Intent event = new Intent(Eteindre.this, Event.class);
                    startActivity(event);
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
        menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_selec);

        if (!nuit) {
            switch (coloris) {
                case "#0099CC":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_bleu);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_bleu);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_bleu);
                    break;
                case "#CC0000":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_rouge);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_rouge);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_rouge);
                    break;
                case "#84BE58":
                    menu.findItem(R.id.message).setIcon(R.mipmap.lettre_vert);
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_vert);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_vert);
                    break;
                default:
                    break;
            }
        }
        else {
            menu.findItem(R.id.message).setIcon(R.mipmap.lettre_blanc);
            menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_blanc);
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
                Intent sms = new Intent(this, MainActivity.class);
                startActivity(sms);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.calendrier:
                Intent event = new Intent(this, Event.class);
                startActivity(event);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.options:
                Intent options = new Intent(this, Parametres.class);
                startActivity(options);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
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
}
