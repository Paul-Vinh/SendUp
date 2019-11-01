package com.paulvinh.send_up;

// Tous les imports nécessaires au fonctionnement des différentes fonctions (les fameuses librairies :) )

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab1 extends Activity implements TextWatcher {
    private EditText smsBody;
    private Button smsManagerBtn;
    private CalendarView pickerDate;
    private TimePicker pickerTime;
    private Button buttonReadContact;
    private Button buttonReadGroupes;
    private final int RQS_PICKCONTACT = 1;
    private long tps;
    public static int choix = 0;
    private Calendar objectif;
    String smsNumero, smsTexte, destinataires = "", numeros = "";
    public static List<String> array;
    public static List<String> array_nom;
    private String smsContact;
    private String time_dest;
    public static ArrayList<String> array_message = new ArrayList<String>();
    public static ArrayList<String> array_contact = new ArrayList<String>();
    public static ArrayList<String> array_time = new ArrayList<String>();
    public static ArrayList<Integer> array_annee = new ArrayList<Integer>();
    public static ArrayList<Integer> array_mois = new ArrayList<Integer>();
    public static ArrayList<Integer> array_jour = new ArrayList<Integer>();
    public static ArrayList<Integer> array_heure = new ArrayList<Integer>(); // ArrayList qui donne l'information de l'heure à AlarmeService
    public static ArrayList<String> array_minute = new ArrayList<String>(); // ArrayList qui donne l'information de la minute à AlarmeService

    public static ArrayList<List<String>> contact = new ArrayList<List<String>>();
    public static ArrayList<String> deja_envoi_ou_pas = new ArrayList<String>();
    private static final int MODE_AVION = 9;
    private static final int CONTACT_PB = 10;
    private static final int MESSAGE_VIDE = 11;
    private static final int QUITTER = 12;

    public static int infos;
    public static String dest_hist_sms = null;
    private MultiAutoCompleteTextView smsNumber;
    public static int swipe = 1;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private PendingIntent pendingIntent; // AlarmService SMS
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private ColorDrawable colorDrawable;
    private int color;
    private TextView info1;
    private TextView info2;
    private TextView tel;
    private Boolean nuit;
    private SimpleDateFormat sdf;
    private int jour;
    private int mois;
    private int annee;
    private String coloris = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        // Mode Nuit
        SharedPreferences pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nuit = pref.getBoolean("nuit", false);

        if (nuit) {
            color = R.color.blanc;
            colorDrawable = new ColorDrawable(Color.parseColor("#080808"));
            this.findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.noir));
        } else {
            // Couleur verte pour l'ActionBar
            color = R.color.noir;
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            coloris = prefs.getString("couleur_actionbar", "0");
            if (coloris == "0")
                coloris = "#84BE58";
            colorDrawable = new ColorDrawable(Color.parseColor(coloris));
        }

        info1 = (TextView) findViewById(R.id.info);
        info1.setTextColor(getResources().getColor(color));
        info2 = (TextView) findViewById(R.id.info_suite);
        info2.setTextColor(getResources().getColor(color));
        tel = (TextView) findViewById(R.id.text);
        tel.setTextColor(getResources().getColor(color));
        smsBody = (EditText) findViewById(R.id.smsBody);
        smsBody.addTextChangedListener(this);
        smsManagerBtn = (Button) findViewById(R.id.smsManager);
        smsManagerBtn.setTextColor(getResources().getColor(color));
        pickerDate = (CalendarView) findViewById(R.id.date_picker);
        pickerDate.setShowWeekNumber(false);
        pickerDate.setFirstDayOfWeek(2);
        pickerTime = (TimePicker) findViewById(R.id.time_picker);
        pickerTime.setIs24HourView(true);
        buttonReadContact = (Button) findViewById(R.id.contact);
        buttonReadContact.setTextColor(getResources().getColor(color));
        buttonReadGroupes = (Button) findViewById(R.id.groupes);
        buttonReadGroupes.setTextColor(getResources().getColor(color));

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayUseLogoEnabled(false);

        dest_hist_sms = null;

        if (infos == 0) {
            showDialog(QUITTER);
            infos = 1;
        }

        boolean isEnabled = Settings.System.getInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if (isEnabled) {
            showDialog(MODE_AVION);
        }

        /* --------------------- Autocomplétion ------------------------- */
        mPeopleList = new ArrayList<Map<String, String>>();
        ListeContactAutoComplete();
        smsNumber = (MultiAutoCompleteTextView) findViewById(R.id.phoneNumber);
        smsNumber.addTextChangedListener(this);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.autocomplete_sms,
                new String[]{"Name", "Phone"}, new int[]{R.id.nom, R.id.numero});
        smsNumber.setAdapter(mAdapter);
        smsNumber.setThreshold(1);
        smsNumber.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        smsNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                // Méthode pour mettre à jour l'EditText des numéros de téléphone
                numeros = smsNumber.getText().toString();
                int start = numeros.indexOf('{');
                int end = numeros.indexOf('}');
                numeros = numeros.replace(numeros.substring(start, end + 2), "");
                numeros = numeros.replaceAll("[^\\p{Punct}\\w]", "");

                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
                String number = map.get("Phone");
                numeros += number + ",";
                smsNumber.setText(numeros);
                smsNumber.setSelection(smsNumber.getText().toString().length());
            }
        });
        /* ---------------------------------------------------------------- */

        if(nuit) {
            smsBody.setTextColor(getResources().getColor(color));
            smsNumber.setTextColor(getResources().getColor(color));

            // CalendarView
            ViewGroup vg = (ViewGroup) pickerDate.getChildAt(0);
            View child = vg.getChildAt(0);

            if(child instanceof TextView) {
                ((TextView)child).setTextColor(getResources().getColor(color));
            }
            pickerDate.setUnfocusedMonthDateColor(Color.GRAY);
            pickerDate.setFocusedMonthDateColor(getResources().getColor(color));
            pickerDate.setWeekSeparatorLineColor(getResources().getColor(color));
            pickerDate.setWeekDayTextAppearance(R.style.Header);
        }

        buttonReadContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Montre tous les contacts du téléphone
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, RQS_PICKCONTACT);
            }
        });

        smsManagerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                test_champs();
            }
        });

        // Mettre en blanc, si Mode Nuit, le TimePicker
        if (nuit) {
            Resources system = Resources.getSystem();
            int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
            int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
            int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

            NumberPicker hour_numberpicker = (NumberPicker) pickerTime.findViewById(hour_numberpicker_id);
            NumberPicker minute_numberpicker = (NumberPicker) pickerTime.findViewById(minute_numberpicker_id);
            NumberPicker ampm_numberpicker = (NumberPicker) pickerTime.findViewById(ampm_numberpicker_id);

            set_numberpicker_text_colour(hour_numberpicker);
            set_numberpicker_text_colour(minute_numberpicker);
            set_numberpicker_text_colour(ampm_numberpicker);
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

    public void ListeContactAutoComplete() {
            mPeopleList.clear();
            Cursor people = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (people.moveToNext()) {
                String contactName = people.getString(people
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = people.getString(people
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhone = people
                        .getString(people
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if ((Integer.parseInt(hasPhone) > 0)){
                    // You know have the number so now query it like this
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null, null);
                    while (phones.moveToNext()){
                        //store numbers and display a dialog letting the user select which.
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replaceAll("[^\\p{Punct}\\w]", "");
                        Map<String, String> NamePhone = new HashMap<String, String>();
                        NamePhone.put("Name", contactName);
                        NamePhone.put("Phone", phoneNumber);
                        //Then add this map to the list.
                        mPeopleList.add(NamePhone);
                    }
                }
            }
            startManagingCursor(people);
        }

    // Gère la pub avec AdBuddiz
    @Override
    protected void onResume() {
        super.onResume();
                AdBuddiz.setPublisherKey("b1a12d4f-4029-4c70-a45d-66f806c502b2");
                AdBuddiz.cacheAds(this);
                AdBuddiz.showAd(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void envoi() {
        if(smsNumber.getText().toString().endsWith(",")) {
            String texte = smsNumber.getText().toString();
            smsNumber.setText(texte.replace(texte.substring(texte.length()-1), ""));
        }
        array = Arrays.asList(smsNumber.getText().toString().split(","));

        String nom_associe_num = "";
        for(int a = 0 ; a < array.size() ; a++) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(array.get(a)));
            String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
            Cursor curseur = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
            if (curseur.moveToFirst()) {
                nom_associe_num = curseur.getString(0);
            } else {
                nom_associe_num = array.get(a);
            }
            array_nom.add(nom_associe_num);
        }

        choix = 1;
        smsNumero = smsNumber.getText().toString();
        smsTexte = smsBody.getText().toString();

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        jour = Integer.parseInt(sdf.format(new Date(pickerDate.getDate())).substring(0, 2));
        mois = Integer.parseInt(sdf.format(new Date(pickerDate.getDate())).substring(3, 5));
        annee = Integer.parseInt(sdf.format(new Date(pickerDate.getDate())).substring(6));

        tps = convert();

        // Régler problème du '0' manquant dans les minutes
        String minute = String.valueOf(objectif.get(Calendar.MINUTE));

        if (Integer.parseInt(minute) < 10) {
            char min = minute.charAt(0);
            minute = "";
            minute += '0';
            minute += min;
        }

        time_dest = String.valueOf(jour) + "/" + String.valueOf(mois) + "/" + String.valueOf(annee) + " à " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) + "h" + minute;

        array_contact.add(smsNumero);
        array_message.add(smsTexte);
        array_time.add(time_dest);
        array_annee.add(annee);
        array_mois.add(mois - 1);
        array_jour.add(jour);
        array_heure.add(objectif.get(Calendar.HOUR_OF_DAY));
        array_minute.add(minute);
        contact.add(array);
        deja_envoi_ou_pas.add("1");

        Intent myIntent = new Intent(Tab1.this, AlarmeService.class);

        pendingIntent = PendingIntent.getService(Tab1.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, (int) tps);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        for(int i = 0 ; i < array.size() ; i++) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(array.get(i)));
            String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
            Cursor curseur = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
            if (curseur.moveToFirst()) {
                smsContact = curseur.getString(0);
            } else {
                smsContact = smsNumero;
            }
            if(dest_hist_sms == null)
                dest_hist_sms = smsContact;
            else {
                dest_hist_sms += ", " + smsContact;
            }
        }

        Toast.makeText(Tab1.this,
                getResources().getText(R.string.le_message) + " \" " + smsTexte + " \" " + getResources().getText(R.string.bien_envoye) + smsContact + " " + getResources().getText(R.string.le) +" " + String.valueOf(jour) + "/" +
                        String.valueOf(mois) + "/" + String.valueOf(annee) + " " + getResources().getText(R.string.a) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                        "h" + minute,
                Toast.LENGTH_SHORT).show();

        smsNumber.setText("");
        smsBody.setText("");
    }

    @Override
    public void onBackPressed() {
        swipe = 0;
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_out, R.anim.push_up_in);
    }

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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                Intent option = new Intent(this, Parametres.class);
                startActivity(option);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Génère deux boîtes de dialogue: une popur prévenir l'utilisateur de désactiver le mode avion (s'il est activé !) et l'autre pour prévenir que s'il quitte, quelques fonctions vont cesser
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected Dialog onCreateDialog ( int id){
        switch (id) {
            case MODE_AVION:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if(nuit)
                    builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builder.setMessage(getResources().getText(R.string.mode_avion));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case CONTACT_PB:
                AlertDialog.Builder built = new AlertDialog.Builder(this);
                if(nuit)
                    built = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                built.setMessage(getResources().getText(R.string.num_oublie));
                built.setCancelable(true);
                built.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dial = built.create();
                dial.show();
                break;
            case MESSAGE_VIDE:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                if(nuit)
                    build = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                build.setMessage(getResources().getText(R.string.confirmation_vide));
                build.setCancelable(true);
                build.setPositiveButton(getResources().getText(R.string.oui), new ConfirmerOnClickListener());
                build.setNegativeButton(getResources().getText(R.string.non), new AnnulerOnClickListener());
                AlertDialog dialogue = build.create();
                dialogue.show();
                break;
        }
        return super.onCreateDialog(id);
    }

    // Colorer les EditTexts en focntion du mode (Jour/Nuit)
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        smsNumber.setTextColor(getResources().getColor(color));
        smsBody.setTextColor(getResources().getColor(color));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private final class AnnulerOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private final class ConfirmerOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            envoi();
        }
    }

    // Vérifie si tous les champs obligatoires sont remplis et vérifie les choix de l'utilisateur (message vide)
    private void test_champs() {
        boolean isEnabled = Settings.System.getInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if (isEnabled) {
            showDialog(MODE_AVION);
        }
        else {
            if (smsNumber.getText().toString().trim().length() == 0)
                showDialog(CONTACT_PB);
            if (smsNumber.getText().toString().trim().length() != 0 && smsBody.getText().toString().trim().length() == 0)
                showDialog(MESSAGE_VIDE);
            if (smsNumber.getText().toString().trim().length() != 0 && smsBody.getText().toString().trim().length() != 0)
                envoi();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (requestCode == RQS_PICKCONTACT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For the sake of simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor c = getContentResolver().query(contactUri, projection, null, null, null);
                c.moveToFirst();
                // Retrieve the phone number from the NUMBER column
                int column = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = c.getString(column);
                // Do something with the phone number...
                destinataires = smsNumber.getText().toString();
                destinataires += number + ",";
                destinataires = destinataires.replaceAll("[^\\p{Punct}\\w]", "");
                smsNumber.setText(destinataires);
                smsNumber.setSelection(smsNumber.getText().toString().length());
                //c.close();
            }
        }
    }

    // Fonction gérant le calcul du temps programmé - le temps actuel (durée d'attente avant l'envoi)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public long convert() {

        // date et heure actuelle récupérée
        Calendar current = Calendar.getInstance();

        // Calendar qui gère la date précise de l'envoi programmé

        objectif = Calendar.getInstance();
        objectif.set(Calendar.MILLISECOND, 0);
        objectif.set(Calendar.SECOND, 0);
        objectif.set(Calendar.MINUTE, pickerTime.getCurrentMinute());
        objectif.set(Calendar.HOUR_OF_DAY, pickerTime.getCurrentHour());
        objectif.set(Calendar.DAY_OF_MONTH, jour);
        objectif.set(Calendar.MONTH, mois - 1);
        objectif.set(Calendar.YEAR, annee);

        return objectif.getTimeInMillis() - (current.getTimeInMillis() - current.getTimeInMillis() % 60000);
    }
}
