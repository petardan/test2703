package com.kanal77.kanal77;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class Loading extends AppCompatActivity {

    ImageView backgroundLogo;

    CountDownTimer timer;

    SharedPreferences mPrefs;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fullscreen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loading);

        context = this;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(Loading.this);

        //Define elements
        backgroundLogo = (ImageView)findViewById(R.id.backgroundLogo);

        //Start animation
        startAnimation();

        //Countdown timer that performs login check after animation is done (3sec)
        timer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent i = new Intent(Loading.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }.start();
    }




    private void startAnimation() {
        backgroundLogo.setAlpha(0f);
        backgroundLogo.setVisibility(View.VISIBLE);
        backgroundLogo.animate().alpha(1f).setDuration(2000).setListener(null);

    }
}
