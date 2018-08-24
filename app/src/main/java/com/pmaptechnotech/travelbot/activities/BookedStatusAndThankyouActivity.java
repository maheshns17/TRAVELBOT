package com.pmaptechnotech.travelbot.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;

import java.util.HashMap;
import java.util.Locale;

public class BookedStatusAndThankyouActivity extends AppCompatActivity {
    private Context context;
    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_status_and_thankyou);
        context = BookedStatusAndThankyouActivity.this;



        speechInit();
        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {


            }
        });

        askForSelectTrain(4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 10000);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    private void askForSelectTrain(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech("Congratulations, Your ticket has been confirmed, Thank You for using travel bot", P.SELECT_TRAIN);
            }
        }, delay);
    }
    private void textToSpeech(String msg, int requestCode) {

        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, requestCode + "");
        t1.speak(msg, TextToSpeech.QUEUE_FLUSH, myHashRender);
    }

    private void speechInit() {
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }
}
