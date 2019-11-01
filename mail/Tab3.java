package mail;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
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

import com.paulvinh.send_up.AlarmeService;
import com.paulvinh.send_up.Eteindre;
import com.paulvinh.send_up.Event;
import com.paulvinh.send_up.Fichier;
import com.paulvinh.send_up.MainActivity;
import com.paulvinh.send_up.Parametres;
import com.paulvinh.send_up.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab3 extends FragmentActivity implements TextWatcher {

    private final int RQS_PICKCONTACT = 1;
    private MultiAutoCompleteTextView toEmail;
    private EditText emailSubject;
    private EditText emailBody;
    private EditText user;
    private EditText pass;
    private CalendarView pickerDate3;
    private TimePicker pickerTime3;
    private Button envoyer;
    private Button effacer;
    private Button buttonLireContact;
    private Button fich;
    private Button buttonLireGroupe;
    private static final int PB_INTERNET = 10;
    private static final int ADRESSE = 11;
    private static final int PB_GMAIL = 12;
    private static final int MOT_DE_PASSE = 13;
    private static final int MESSAGE_VIDE = 14;
    private static final int OBJET_VIDE = 15;
    private static final int DESTINATAIRE = 16;
    private static final int DEST_INVALIDE = 17;
    private static final int OBJET_MESSAGE_VIDES = 18;
    public static long tps;
    private Calendar objectif;
    public static String dest = "";
    public static String obj = "";
    public static String mes = "";
    public static String passw = "";
    public static String use = "";
    String destinataires = "", emails = "";
    public static int choix2 = 0;
    private TextView fichier;
    private String chemins;
    public static int code = 0;
    private String time;
    public static List<String> array;
    public static ArrayList mail_objet = new ArrayList();
    public static ArrayList mail_dest = new ArrayList();
    public static ArrayList mail_use = new ArrayList();
    public static ArrayList mail_body = new ArrayList();
    public static ArrayList mail_time = new ArrayList();
    public static ArrayList mail_annee = new ArrayList();
    public static ArrayList mail_mois = new ArrayList();
    public static ArrayList mail_jour = new ArrayList();
    public static ArrayList mail_heure = new ArrayList(); // ArrayList qui donne l'information de l'heure à AlarmeService
    public static ArrayList mail_minute = new ArrayList(); // ArrayList qui donne l'information de la minute à AlarmeService
    public static ArrayList<List> contact = new ArrayList<List>();
    public static ArrayList deja_envoi_ou_pas = new ArrayList();
    public static ArrayList file = new ArrayList();
    private ConnectivityManager connMgr;
    private NetworkInfo wifi;
    private TelephonyManager mobile;
    public static int swipe = 1;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private ColorDrawable colorDrawable;
    private int color;
    private TextView info1;
    private TextView info2;
    private TextView u;
    private TextView mdp;
    private TextView d;
    private TextView o;
    private TextView f;
    private TextView m;
    private PendingIntent pendingIntent;
    private SimpleDateFormat sdf;
    private int jour;
    private int annee;
    private int mois;
    private Boolean nuit;
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private String coloris = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3);
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

        info1 = (TextView) findViewById(R.id.info);
        info1.setTextColor(getResources().getColor(color));
        info2 = (TextView) findViewById(R.id.info_suite);
        info2.setTextColor(getResources().getColor(color));
        u = (TextView) findViewById(R.id.texte_user);
        u.setTextColor(getResources().getColor(color));
        mdp = (TextView) findViewById(R.id.texte_mdp);
        mdp.setTextColor(getResources().getColor(color));
        d = (TextView) findViewById(R.id.texte_dest);
        d.setTextColor(getResources().getColor(color));
        o = (TextView) findViewById(R.id.texte_objet);
        o.setTextColor(getResources().getColor(color));
        fichier = (TextView) findViewById(R.id.f);
        fichier.setTextColor(getResources().getColor(color));
        m = (TextView) findViewById(R.id.texte_message);
        m.setTextColor(getResources().getColor(color));

        // Wifi
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // Data mobile
        mobile = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (!wifi.isAvailable() && mobile.getDataState() != mobile.DATA_CONNECTED)
            showDialog(PB_INTERNET);

        toEmail = (MultiAutoCompleteTextView) findViewById(R.id.destinataire);
        toEmail.setText(Fichier.destination);
        toEmail.addTextChangedListener(this);
        emailSubject = (EditText) findViewById(R.id.objet);
        emailSubject.setText(Fichier.sub);
        emailSubject.addTextChangedListener(this);
        emailBody = (EditText) findViewById(R.id.email);
        emailBody.setText(Fichier.body);
        emailBody.addTextChangedListener(this);
        user = (EditText) findViewById(R.id.user);
        user.setText(Fichier.add);
        user.addTextChangedListener(this);
        pass = (EditText) findViewById(R.id.pass);
        pass.setText(Fichier.pass);
        pass.addTextChangedListener(this);
        pickerDate3 = (CalendarView) findViewById(R.id.date);
        pickerDate3.setShowWeekNumber(false);
        pickerDate3.setFirstDayOfWeek(2);
        pickerTime3 = (TimePicker) findViewById(R.id.time);
        pickerTime3.setIs24HourView(true);
        envoyer = (Button) findViewById(R.id.envoyer);
        envoyer.setTextColor(getResources().getColor(color));
        effacer = (Button) findViewById(R.id.effacer);
        effacer.setTextColor(getResources().getColor(color));
        buttonLireContact = (Button) findViewById(R.id.contact);
        buttonLireContact.setTextColor(getResources().getColor(color));
        buttonLireGroupe = (Button) findViewById(R.id.groupes);
        buttonLireGroupe.setTextColor(getResources().getColor(color));
        fich = (Button) findViewById(R.id.file);
        fich.setTextColor(getResources().getColor(color));

        chemins = Fichier.chemin;
        if (chemins == null)
            chemins = getResources().getText(R.string.pas_fichier).toString();
        fichier.setText(getResources().getText(R.string.fichier_joint) + " " + chemins);

        envoyer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                test_champs();
            }
        });

        fich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = 1;
                Intent intent = new Intent(Tab3.this, Fichier.class);
                dest = toEmail.getText().toString();
                obj = emailSubject.getText().toString();
                mes = emailBody.getText().toString();
                passw = pass.getText().toString();
                use = user.getText().toString();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });

        mPeopleList = new ArrayList<Map<String, String>>();
        ListeContactAutoComplete();
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.autocomplete_sms,
                new String[]{"Nom", "Email"}, new int[]{R.id.nom, R.id.numero});
        toEmail.setAdapter(mAdapter);
        toEmail.setThreshold(1);
        toEmail.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        toEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                // Méthode pour mettre à jour l'EditText des numéros de téléphone
                emails = toEmail.getText().toString();
                int start = emails.indexOf('{');
                int end = emails.indexOf('}');
                emails = emails.replace(emails.substring(start, end + 2), "");
                emails = emails.replaceAll("[^\\p{Punct}\\w]", "");

                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
                String adresse = map.get("Email");
                emails += adresse + ",";
                toEmail.setText(emails);
                toEmail.setSelection(toEmail.getText().toString().length());
            }
        });

        if(nuit) {
            // Au cas où il y a déjà du texte lors du Mode Nuit
            toEmail.setTextColor(getResources().getColor(color));
            emailSubject.setTextColor(getResources().getColor(color));
            emailBody.setTextColor(getResources().getColor(color));
            user.setTextColor(getResources().getColor(color));
            pass.setTextColor(getResources().getColor(color));

            // CalendarViews
            ViewGroup vg = (ViewGroup) pickerDate3.getChildAt(0);
            View child = vg.getChildAt(0);

            if(child instanceof TextView) {
                ((TextView)child).setTextColor(getResources().getColor(color));
            }
            pickerDate3.setUnfocusedMonthDateColor(Color.GRAY);
            pickerDate3.setFocusedMonthDateColor(getResources().getColor(color));
            pickerDate3.setWeekSeparatorLineColor(getResources().getColor(color));
            pickerDate3.setWeekDayTextAppearance(R.style.Header);
        }

        effacer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clear();
            }
        });

        buttonLireContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Start activity to get contact
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, RQS_PICKCONTACT);
            }
        });

        // Mettre en blanc, si Mode Nuit, le TimePicker
        if (nuit) {
            Resources system = Resources.getSystem();
            int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
            int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
            int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

            NumberPicker hour_numberpicker = (NumberPicker) pickerTime3.findViewById(hour_numberpicker_id);
            NumberPicker minute_numberpicker = (NumberPicker) pickerTime3.findViewById(minute_numberpicker_id);
            NumberPicker ampm_numberpicker = (NumberPicker) pickerTime3.findViewById(ampm_numberpicker_id);

            set_numberpicker_text_colour(hour_numberpicker);
            set_numberpicker_text_colour(minute_numberpicker);
            set_numberpicker_text_colour(ampm_numberpicker);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void ListeContactAutoComplete() {
        mPeopleList.clear();

        Cursor cur2 = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,  ContactsContract.CommonDataKinds.Email.CONTACT_ID,null, null);

        while(cur2.moveToNext())
        {
            String mail = cur2.getString(cur2.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            mail = mail.replaceAll("[^\\p{Punct}\\w]", "");
            String nom = cur2.getString(cur2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Map Email = new HashMap();
            Email.put("Email", mail);
            if(nom.equals(mail))
                Email.put("Nom", "");
            else
                Email.put("Nom", nom);
            mPeopleList.add(Email);
        }
            startManagingCursor(cur2);
        }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void envoi() {
        array = Arrays.asList(toEmail.getText().toString().split(","));

        choix2 = 2;
        dest = toEmail.getText().toString();
        obj = emailSubject.getText().toString();
        mes = emailBody.getText().toString();
        passw = pass.getText().toString();
        use = user.getText().toString();

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        jour = Integer.parseInt(sdf.format(new Date(pickerDate3.getDate())).substring(0, 2));
        mois = Integer.parseInt(sdf.format(new Date(pickerDate3.getDate())).substring(3, 5));
        annee = Integer.parseInt(sdf.format(new Date(pickerDate3.getDate())).substring(6));

        Intent myIntent = new Intent(Tab3.this, AlarmeService.class);

        Bundle bundle3 = new Bundle();
        bundle3.putCharSequence("User", use);
        bundle3.putCharSequence("Pass", passw);
        bundle3.putCharSequence("To", dest);
        bundle3.putCharSequence("Subject", obj);
        bundle3.putCharSequence("Body", mes);
        bundle3.putCharSequence("Fichier", chemins);
        myIntent.putExtras(bundle3);

        tps = convert();

        pendingIntent = PendingIntent.getService(Tab3.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, (int) tps);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Régler problème du '0' manquant dans les minutes
        String minute = String.valueOf(objectif.get(Calendar.MINUTE));

        if (Integer.parseInt(minute) < 10) {
            char min = minute.charAt(0);
            minute = "";
            minute += '0';
            minute += min;
        }

        time = String.valueOf(jour) + "/" +
                String.valueOf(mois) + "/" + String.valueOf(annee) + " à " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                "h" + minute;

        mail_objet.add(obj);
        mail_body.add(mes);
        mail_dest.add(dest);
        mail_use.add(use);
        mail_time.add(time);
        mail_annee.add(annee);
        mail_mois.add(mois - 1);
        mail_jour.add(jour);
        mail_heure.add(objectif.get(Calendar.HOUR_OF_DAY));
        mail_minute.add(minute);
        contact.add(array);
        deja_envoi_ou_pas.add("1");
        file.add(chemins);

        Toast.makeText(Tab3.this,
                getResources().getText(R.string.le_mail) + " \" " + mes + " \"" + getResources().getText(R.string.bien_envoye)  + dest + " " + getResources().getText(R.string.le) + " " + String.valueOf(jour) + "/" +
                        String.valueOf(mois) + "/" + String.valueOf(annee) + " " + getResources().getText(R.string.a) + " " + String.valueOf(objectif.get(Calendar.HOUR_OF_DAY)) +
                        "h" + minute,
                Toast.LENGTH_SHORT).show();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PB_INTERNET:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                if(nuit)
                    build = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                build.setMessage(getResources().getText(R.string.data_mobile));
                build.setCancelable(true);
                build.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dialogue = build.create();
                dialogue.show();
                break;
            case ADRESSE:
                AlertDialog.Builder building = new AlertDialog.Builder(this);
                if(nuit)
                    building = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                building.setMessage(getResources().getText(R.string.id_gmail));
                building.setCancelable(true);
                building.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dialogues = building.create();
                dialogues.show();
                break;
            case MOT_DE_PASSE:
                AlertDialog.Builder built = new AlertDialog.Builder(this);
                if(nuit)
                    built = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                built.setMessage(getResources().getText(R.string.mdp_gmail));
                built.setCancelable(true);
                built.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dial = built.create();
                dial.show();
                break;
            case DESTINATAIRE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if(nuit)
                    builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builder.setMessage(getResources().getText(R.string.pb_mail_dest));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case DEST_INVALIDE:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                if(nuit)
                    b = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                b.setMessage(getResources().getText(R.string.mail_valide));
                b.setCancelable(true);
                b.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog d = b.create();
                d.show();
                break;
            case PB_GMAIL:
                AlertDialog.Builder bild = new AlertDialog.Builder(this);
                if(nuit)
                    bild = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                bild.setMessage(getResources().getText(R.string.suffixe_gmail) + " \"@gmail.com\"");
                bild.setCancelable(true);
                bild.setPositiveButton(getResources().getText(R.string.ok), new OkOnClickListener());
                AlertDialog dia = bild.create();
                dia.show();
                break;
            case OBJET_VIDE:
                AlertDialog.Builder builde = new AlertDialog.Builder(this);
                if(nuit)
                    builde = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builde.setMessage(getResources().getText(R.string.pas_objet));
                builde.setCancelable(true);
                builde.setPositiveButton(getResources().getText(R.string.oui), new ConfirmerEnvoiOnClickListener());
                builde.setNegativeButton(getResources().getText(R.string.non), new AnnulerOnClickListener());
                AlertDialog dialogs = builde.create();
                dialogs.show();
                break;
            case MESSAGE_VIDE:
                AlertDialog.Builder buildings = new AlertDialog.Builder(this);
                if(nuit)
                    buildings = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                buildings.setMessage(getResources().getText(R.string.pas_mes));
                buildings.setCancelable(true);
                buildings.setPositiveButton(getResources().getText(R.string.oui), new ConfirmerEnvoiOnClickListener());
                buildings.setNegativeButton(getResources().getText(R.string.non), new AnnulerOnClickListener());
                AlertDialog dialoguing = buildings.create();
                dialoguing.show();
                break;
            case OBJET_MESSAGE_VIDES:
                AlertDialog.Builder builts = new AlertDialog.Builder(this);
                if(nuit)
                    builts = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builts.setMessage(getResources().getText(R.string.pas_mes_pas_objet));
                builts.setCancelable(true);
                builts.setPositiveButton(getResources().getText(R.string.oui), new ConfirmerEnvoiOnClickListener());
                builts.setNegativeButton(getResources().getText(R.string.non), new AnnulerOnClickListener());
                AlertDialog dials = builts.create();
                dials.show();
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
        toEmail.setTextColor(getResources().getColor(color));
        emailSubject.setTextColor(getResources().getColor(color));;
        emailBody.setTextColor(getResources().getColor(color));
        user.setTextColor(getResources().getColor(color));
        pass.setTextColor(getResources().getColor(color));;
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

    private final class ConfirmerEnvoiOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            if(toEmail.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && pass.getText().toString().trim().length() != 0)
                envoi();
        }
    }

    // Vérifie si tous les champs obligatoires sont remplis et vérifie les choix de l'utilisateur (message vide ou sans objet)
    private void test_champs() {
        if(emailSubject.getText().toString().trim().length() == 0 && emailBody.getText().toString().trim().length() == 0 && pass.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && user.getText().toString().endsWith("@gmail.com"))
            showDialog(OBJET_MESSAGE_VIDES);
        if(emailBody.getText().toString().trim().length() == 0 && emailSubject.getText().toString().trim().length() != 0 && pass.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && user.getText().toString().endsWith("@gmail.com"))
            showDialog(MESSAGE_VIDE);
        if(emailSubject.getText().toString().trim().length() == 0 && emailBody.getText().toString().trim().length() != 0 && pass.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && user.getText().toString().endsWith("@gmail.com"))
            showDialog(OBJET_VIDE);
        if(toEmail.getText().toString().trim().length() == 0 && pass.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && user.getText().toString().endsWith("@gmail.com"))
            showDialog(DESTINATAIRE);

        List<String> destinations = Arrays.asList(toEmail.getText().toString().split(","));
        int invalide = 0;
        for(int i = 0 ; i < destinations.size() ; i++) {
            if (!isValidEmail(destinations.get(i)))
                invalide = 1;
        }
        if(invalide == 1)
            showDialog(DEST_INVALIDE);
        if(pass.getText().toString().trim().length() == 0)
            showDialog(MOT_DE_PASSE);
        if(user.getText().toString().trim().length() == 0)
            showDialog(ADRESSE);
        if(!user.getText().toString().endsWith("@gmail.com"))
            showDialog(PB_GMAIL);
        // Wifi
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // Data mobile
        mobile = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (!wifi.isAvailable() && mobile.getDataState() != mobile.DATA_CONNECTED)
            showDialog(PB_INTERNET);
        if(toEmail.getText().toString().trim().length() != 0 && user.getText().toString().trim().length() != 0 && pass.getText().toString().trim().length() != 0
            && emailBody.getText().toString().trim().length() != 0 && emailSubject.getText().toString().trim().length() != 0 && user.getText().toString().endsWith("@gmail.com"))
                envoi();
    }

    public void clear() {
        toEmail.setText("");
        emailBody.setText("");
        emailSubject.setText("");
        chemins = getResources().getText(R.string.pas_fichier).toString();
        fichier.setText(getResources().getText(R.string.fichier_joint) + " " + chemins);
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
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        objectif.set(Calendar.MINUTE, pickerTime3.getCurrentMinute());
        objectif.set(Calendar.HOUR_OF_DAY, pickerTime3.getCurrentHour());
        objectif.set(Calendar.DAY_OF_MONTH, jour);
        objectif.set(Calendar.MONTH, mois - 1);
        objectif.set(Calendar.YEAR, annee);

        return objectif.getTimeInMillis() - (current.getTimeInMillis() - current.getTimeInMillis() % 60000);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
                String[] projection = {ContactsContract.CommonDataKinds.Email.ADDRESS};

                // Perform the query on the contact to get the NUMBER column
                // The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For the sake of simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                String mail = cursor.getString(column);
                // Do something with the phone number...
                destinataires = toEmail.getText().toString();
                destinataires += mail + ",";
                toEmail.setText(destinataires);
                toEmail.setSelection(toEmail.getText().toString().length());
            }
            }
        }
    }

