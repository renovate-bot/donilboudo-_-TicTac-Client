package com.nsidetech.tictac.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.nsidetech.tictac.R;
import com.nsidetech.tictac.notification.NotificationHelper;
import com.nsidetech.tictac.util.DeviceManager;

public class SplashActivity extends AppCompatActivity {
    //Duration of wait
    private final static int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //we will subscribe this device to a topic
        NotificationHelper.getInstance().subscribeToTopic();

        //create a unique identifier and save in DB
        DeviceManager.getInstance().createDeviceId(this);

        /* New Handler to start the Menu-Activity and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MenuActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
}
