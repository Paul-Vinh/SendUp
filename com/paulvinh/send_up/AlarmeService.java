package com.paulvinh.send_up;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.*;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;
import java.lang.Process;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import mail.Tab3;

// Fonction prévoyant l'envoi d'un SMS, MMS et Email programmé
public class AlarmeService extends Service {

    String use, passw, dest, obj, mes;
    Session session = null; // Mail
    MimeBodyPart messageBodyPart2; // Mail
    private String _host;
    private String _port;
    private String _sport;
    private MimeMultipart _multipart;
    private boolean _debuggable;
    private boolean _auth;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
    }

                @Override
                public void onStart(Intent intent, int startId) {
                    super.onStart(intent, startId);

                    Bundle bundle = intent.getExtras();

                    // Si la variable choix est égale à 1, c'est le cas d'un SMS
                    if (Tab1.choix == 1) {
                        if (!Tab1.contact.isEmpty()) {
                            for (int i = 0; i < Tab1.array_message.size(); i++) {
                                Date d = new Date(Tab1.array_annee.get(i) - 1900, Tab1.array_mois.get(i), Tab1.array_jour.get(i), Integer.parseInt(Tab1.array_heure.get(i).toString()), Integer.parseInt(Tab1.array_minute.get(i).toString()));
                                Date now = new Date();

                                if (d.getYear() == now.getYear() && d.getMonth() == now.getMonth() && d.getDay() == now.getDay() && d.getHours() == now.getHours() && d.getMinutes() == now.getMinutes() && Tab1.deja_envoi_ou_pas.get(i).equals("1")) {
                                    for (int j = 0; j < Tab1.contact.get(i).size(); j++) {
                                        try {
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(String.valueOf(Tab1.contact.get(i).get(j)), null, String.valueOf(Tab1.array_message.get(i)), null, null);
                                            Toast.makeText(getApplicationContext(), "SMS envoyé !",
                                                    Toast.LENGTH_SHORT).show();
                                            Tab1.deja_envoi_ou_pas.add(i, "0");
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Erreur: le SMS n'a pas été envoyé ! Veuillez réessayer ultérieurement.",
                                                    Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    // Permettre aux SMS d'être visibles depuis l'application standard "Message"
                                    Uri sms = Uri.parse("content://sms/");
                                    ContentValues cv2 = new ContentValues();
                                    for (int k = 0; k < Tab1.array.size(); k++) {
                                        cv2.put("address", Tab1.array.get(k));
                                    }
                                    Calendar maintenant = Calendar.getInstance();
                                    cv2.put("date", String.valueOf(maintenant.getTimeInMillis()));
                                    cv2.put("read", 1);
                                    cv2.put("type", 2);
                                    cv2.put("body", Tab1.array_message.get(Tab1.array_message.size()-1));
                                    getContentResolver().insert(sms, cv2);
                                    getContentResolver().delete(Uri.parse("content://sms/conversations/-1"), null, null);
                                    cv2.clear();
                                }
                            }
                        }
                    }

                    Tab1.choix = 0;

                        // Si la variable choix est égale à 3, c'est le cas d'un Email GMAIL
                        if (Tab3.choix2 == 2) {
                            use = (String) bundle.getCharSequence("User");
                            passw = (String) bundle.getCharSequence("Pass");
                            dest = (String) bundle.getCharSequence("To");
                            obj = (String) bundle.getCharSequence("Subject");
                            mes = (String) bundle.getCharSequence("Body");

                            _host = "smtp.gmail.com";
                            _port = "25";
                            _sport = "465";
                            _debuggable = false;
                            _auth = true;
                            _multipart = new MimeMultipart();

                            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
                            CommandMap.setDefaultCommandMap(mc);

                            RetreiveFeedTask task = new RetreiveFeedTask();
                            task.execute();
                        }

                    Tab3.choix2 = 0;

                        if (Eteindre.choix3 == 3) {
                            Eteindre.choice = Eteindre.choice;
                            switch (Eteindre.choice) {
                                case 1:
                                    Process shutProcess = null;
                                    try {
                                        shutProcess = Runtime.getRuntime().exec("su -c am start -a android.intent.action.ACTION_REQUEST_SHUTDOWN");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 2:
                                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                    wifi.setWifiEnabled(false);
                                    break;
                                case 3:
                                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    mBluetoothAdapter.disable();
                                    break;
                                case 4:
                                    Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                                    synchronized (this) {
                                        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP));
                                        getApplicationContext().sendOrderedBroadcast(i, null);


                                        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP));
                                        getApplicationContext().sendOrderedBroadcast(i, null);
                                    }
                                    break;
                            }
                        }
                    Eteindre.choix3 = 0;
                    Eteindre.choice = 0;
                    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

                        @Override
                        protected String doInBackground(String... params) {

                                if (!Tab3.contact.isEmpty()) {
                                    for (int i = 0 ; i < Tab3.mail_body.size() ; i++) {
                                        Date d = new Date((int) Tab3.mail_annee.get(i) - 1900, (int) Tab3.mail_mois.get(i), (int) Tab3.mail_jour.get(i), Integer.parseInt(Tab3.mail_heure.get(i).toString()), Integer.parseInt(Tab3.mail_minute.get(i).toString()));
                                        Date now = new Date();

                                        if (d.getYear() == now.getYear() && d.getMonth() == now.getMonth() && d.getDay() == now.getDay() && d.getHours() == now.getHours() && d.getMinutes() == now.getMinutes() && Tab3.deja_envoi_ou_pas.get(i).equals("1")) {

                                                // toutes les propriétés ainsi que les différents ports et serveurs nécessaires pour l'envoi d'un mail GMAIL
                                                    Properties props = new Properties();
                                                    props.put("mail.smtp.host", _host);
                                                    if (_debuggable) {
                                                        props.put("mail.debug", "true");
                                                    }
                                                    if (_auth) {
                                                        props.put("mail.smtp.auth", "true");
                                                    }
                                                    props.put("mail.smtp.port", _port);
                                                    props.put("mail.smtp.socketFactory.port", _sport);
                                                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                                                    props.put("mail.smtp.socketFactory.fallback", "false");

                                                    Session session = Session.getInstance(props, new Authenticator() {
                                                        protected PasswordAuthentication getPasswordAuthentication() {
                                                            return new PasswordAuthentication(use, passw);
                                                        }
                                                    });
                                                    Transport transport = null;
                                                    try {
                                                        transport = session.getTransport("smtp");
                                                    } catch (NoSuchProviderException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (!transport.isConnected()) {
                                                        /* ouvre la connexion si elle ne l'est pas faite ! */
                                                        try {
                                                            transport.connect(use, passw);
                                                        } catch (MessagingException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    MimeMessage msg = new MimeMessage(session);
                                                    try {
                                                        msg.setFrom(new InternetAddress(use));
                                                    } catch (MessagingException e) {
                                                        e.printStackTrace();
                                                    }
                                                    InternetAddress[] addressTo = new InternetAddress[Tab3.contact.get(i).size()];
                                                    for (int k = 0 ; k < Tab3.contact.get(i).size() ; k++) {
                                                        try {
                                                            addressTo[k] = new InternetAddress(String.valueOf(Tab3.contact.get(i).get(k)));
                                                        } catch (AddressException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    try {
                                                        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
                                                    } catch (MessagingException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        msg.setSubject(String.valueOf(Tab3.mail_objet.get(i)));
                                                    } catch (MessagingException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        msg.setText(String.valueOf(Tab3.mail_body.get(i)));
                                                    } catch (MessagingException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        transport.sendMessage(msg, msg.getAllRecipients());
                                                    } catch (MessagingException e) {
                                                        e.printStackTrace();
                                                    }
                                                /*MimeMessage message = new MimeMessage(session);

                                                message.setFrom(new InternetAddress(String.valueOf(Tab3.mail_use.get(i))));
                                                message.setRecipient(Message.RecipientType.TO, new InternetAddress(String.valueOf(Tab3.contact.get(i).get(j))));
                                                message.setSubject(String.valueOf(Tab3.mail_objet.get(i)));
                                                message.setText(String.valueOf(Tab3.mail_body.get(i)));

                                                /*MimeBodyPart messageBodyPart1 = new MimeBodyPart();
                                                messageBodyPart1.setText(String.valueOf(Tab3.mail_body.get(i)));

                                                if (!Tab3.file.get(i).equals("Pas de fichier !")) {
                                                    messageBodyPart2 = new MimeBodyPart();
                                                    FileDataSource fdatasource = new FileDataSource(String.valueOf(Tab3.file.get(i)));
                                                    messageBodyPart2.setDataHandler(new DataHandler(fdatasource));
                                                    messageBodyPart2.setFileName(fdatasource.getName());
                                                }

                                                Multipart mpart = new MimeMultipart();
                                                mpart.addBodyPart(messageBodyPart1);
                                                if (!Tab3.file.get(i).equals("Pas de fichier !"))
                                                    mpart.addBodyPart(messageBodyPart2);

                                                message.setContent(mpart);
                                                MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                                                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                                                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                                                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                                                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                                                mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
                                                CommandMap.setDefaultCommandMap(mc);
                                                    Transport.send(message);
                                                    Tab3.deja_envoi_ou_pas.add(i, "0");*/
                                                /*Transport transport = session.getTransport("smtp");
                                                transport.connect("smtp.gmail.com", use, passw);
                                                transport.sendMessage(message, message.getAllRecipients());*/
                                            }
                                    }
                                }
                            return null;
                        }


                        @Override
                        protected void onPostExecute(String result) {
                            Toast.makeText(getApplicationContext(), "Mail envoyé", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

