package com.pmaptechnotech.travelbot.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            public void run() {
                Intent intent = new Intent(getApplicationContext(),
                        UserLoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}