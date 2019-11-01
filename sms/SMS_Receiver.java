package sms;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.provider.ContactsContract;

import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.paulvinh.send_up.Event;
import com.paulvinh.send_up.MainActivity;
import com.paulvinh.send_up.TTS;
import com.paulvinh.send_up.Tab1;

import java.util.Date;

// Fonction qui s'exécute dès que l'on reçoit un SMS
public class SMS_Receiver extends BroadcastReceiver {

    String expediteur, num, sendBody;
    private boolean active = false;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                byte[] pdu = (byte[]) pdus[i];
                SmsMessage message = SmsMessage.createFromPdu(pdu);
                String texte = message.getDisplayMessageBody();
                num = message.getOriginatingAddress().toString();
                expediteur = getContactName(context, message.getOriginatingAddress().toString());
                //Calendar cal = Calendar.getInstance();
                //int mois_faux = cal.get(Calendar.MONTH);
                //cal.set(Calendar.MONTH, mois_faux + 1); // Rajout de +1 pour le cal.get(Calendar.MONTH) car il affiche le mois d'avant et non actuel !
                Date d_now = new Date();

                if(!Event.nom.isEmpty()) {
                for (int in = 0 ; in < Event.nom.size() ; in++) {
                    Date d_debut = new Date((int) Event.d_dannee.get(in) - 1900, (int) Event.d_dmois.get(in) - 1, (int) Event.d_djour.get(in), Integer.parseInt(Event.d_dheure.get(in).toString()), Integer.parseInt(Event.d_dmin.get(in).toString()));
                    Date d_fin = new Date((int) Event.d_fannee.get(in) - 1900, (int) Event.d_fmois.get(in) - 1, (int) Event.d_fjour.get(in), (int) Event.d_fheure.get(in), Integer.parseInt(Event.d_fheure.get(in).toString()), Integer.parseInt(Event.d_fmin.get(in).toString()));

                    if(d_now.after(d_debut) && d_now.before(d_fin) || d_now.equals(d_debut) && d_now.before(d_fin) || d_now.after(d_debut) && d_now.equals(d_fin) || d_now.equals(d_debut) && d_now.equals(d_fin)) {

                        // Mettre le téléphone en mode silencieux
                        AudioManager audio_mngr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audio_mngr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        active = false; // Désactiver le Text-To-Speech

                        if(Event.num.get(in).toString().contains(num))
                            sendBody = Event.nom_autre.get(in).toString() + "\n\n(Ce message a été envoyé via l'application SendUp)";
                        else
                            sendBody = Event.nom.get(in).toString() + "\n\n(Ce message a été envoyé via l'application SendUp)";
                        
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(num, null, sendBody, null, null);
                    }
                }
            }
                else
                    active = true;

                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                boolean tts = prefs.getBoolean("tts_coche", false);
            if(MainActivity.ready && active && tts) {
                    MainActivity.myTts.speak("Message reçu de " + expediteur + " : " + texte, TextToSpeech.QUEUE_FLUSH, null);
                }
            }

        }
    }

    private String getContactName(Context context, String phone) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
        Cursor curseur = context.getContentResolver().query(uri, projection, null, null, null);
        if (curseur.moveToFirst()) {
            return curseur.getString(0);
        } else {
            return phone;
        }
    }
}

