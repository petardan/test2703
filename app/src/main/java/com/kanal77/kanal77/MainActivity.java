package com.kanal77.kanal77;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String TAG = "Main Activity";
    String play = "Play";
    String pause = "Pause";

    String radio_url = "http://92.55.71.42:8023/kanal77.mp3";
    String homepage_url = "http://kanal77.mk/";

    MediaPlayer mPlayer;

    ArrayList<CityWeather> weather = new ArrayList<>();

    Button button_startstop;
    Button button_homepage;
    Button button_contact;
    Button button_weather;
    Button button_alarm;

    MediaMetadataRetriever metaRetriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_startstop = (Button)findViewById(R.id.button_startstop);
        button_homepage = (Button)findViewById(R.id.button_homepage);
        button_contact  = (Button)findViewById(R.id.button_contact);
        button_weather = (Button)findViewById(R.id.button_weather);
        button_alarm = (Button)findViewById(R.id.button_alarm);

        //Print weather
        for( int j=0; j<weather.size(); j++){
            Log.d("Weather", weather.get(j).toString());
        }

        //Start playing the radio stream
            playRadio();


        //Buttons onClickListeners

        //Start/Stop functionality for starting/stopping the stream
        button_startstop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mPlayer.isPlaying()){
                    button_startstop.setText(play);
                    //mPlayer.release();
                    mPlayer.pause();
                }
                else{
                    button_startstop.setText(pause);
                    //mPlayer.release();
                    //playRadio();
                    mPlayer.start();
                }

            }
        });

        //Homepage button that opens Kanal77 homepage in web browser
        button_homepage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage_url));
                startActivity(browserIntent);
            }
        });

        //Contact button that opens contact activity
        button_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Contact.class);
                startActivity(i);
            }
        });

        //Wheather button that opens weather info
        button_weather.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Weather.class);
                startActivity(i);
            }
        });

        //Alarm button
        button_alarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Alarm2.class);
                startActivity(i);
            }
        });

    }

    private void playRadio() {


                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(radio_url);
                } catch (IllegalArgumentException e) {
                    //Toast.makeText(getApplicationContext(), "IllegalArgumentException!", Toast.LENGTH_LONG).show();
                } catch (SecurityException e) {
                    //Toast.makeText(getApplicationContext(), "SecurityException!", Toast.LENGTH_LONG).show();
                } catch (IllegalStateException e) {
                    //Toast.makeText(getApplicationContext(), "YIllegalStateException!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e) {
                    //Toast.makeText(getApplicationContext(), "IllegalStateException!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), "IOException!", Toast.LENGTH_LONG).show();
                }
                mPlayer.start();

        //getMetadata();





    }

    private void getMetadata() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(radio_url);
                    IcyStreamMeta icy = new IcyStreamMeta(url);

                    Log.d("SONG",icy.getTitle());
                    Log.d("ARTITSi",icy.getArtist());
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
