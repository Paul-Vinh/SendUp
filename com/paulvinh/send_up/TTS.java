package com.paulvinh.send_up;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public class TTS extends Activity {
    public static TextToSpeech myTts;
    public static boolean dispo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.message_m);
        myTts = new TextToSpeech(this,ttsInitListener);
    }

    private TextToSpeech.OnInitListener ttsInitListener=new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int version) {
            dispo = true;
            // myTts.speak(""+o, 0 ,null);
        }
    };
    public static void speakSMS(String sms)
    {
        myTts.speak(sms,0,null);
    }
}