package com.paulvinh.send_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Auto_Start extends BroadcastReceiver {

    public static final String PREFS_NAME = "MyPrefsFile";
    private Boolean autoRestart = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            autoRestart = prefs.getBoolean("auto", false);

            if (autoRestart) {
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}