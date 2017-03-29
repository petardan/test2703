package com.kanal77.kanal77;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    Button button_startstop;
    Button button_homepage;
    Button button_contact;
    Button button_weather;
    Button button_alarm;
    Button button_youtube;

    MediaMetadataRetriever metaRetriver;

    Context context;

    SharedPreferences mPrefs;

    Boolean radioIsPlaying;
    Boolean alarmActivated = false;

    ProgressBar radioProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_startstop = (Button)findViewById(R.id.button_startstop);
        button_homepage = (Button)findViewById(R.id.button_homepage);
        button_contact  = (Button)findViewById(R.id.button_contact);
        button_weather = (Button)findViewById(R.id.button_weather);
        button_alarm = (Button)findViewById(R.id.button_alarm);
        button_youtube = (Button) findViewById(R.id.button_youtube);

        radioProgress = (ProgressBar)findViewById(R.id.radioProgressBar);
        radioProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        context = this;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        alarmActivated = mPrefs.getBoolean("ALARM_ACTIVATED", false);
        radioIsPlaying = mPrefs.getBoolean("RADIO_IS_PLAYING", false);
        
        //Check if alarm is activated
        checkAlarmActivated(alarmActivated);

        //Start Media Player in separate thread
        controlMediaPlayer("Play");

        //Buttons onClickListeners

        //Start/Stop functionality for starting/stopping the stream
        button_startstop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mPlayer.isPlaying()){
                    button_startstop.setText(play);
                    //mPlayer.release();
                    mPlayer.pause();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("RADIO_IS_PLAYING", false);
                    editor.apply();

                }
                else{
                    button_startstop.setText(pause);
                    mPlayer.start();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("RADIO_IS_PLAYING", true);
                    editor.apply();
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

        //Youtube button, stops the player when the activity is started
        button_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("RADIO_IS_PLAYING", false);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, YouTube.class );
                startActivity(intent);
            }
        });

    }

    private void checkAlarmActivated(Boolean alarmActivated) {
        if(alarmActivated){
            showAlarmDialog();
        }
        else{
            
        }
    }

    private void showAlarmDialog() {
        //Set ALARM_ACTIVATED back to false so that dialog won't appear again
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("ALARM_ACTIVATED", false);
        editor.apply();

        //Showing Alert Dialog
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alarm");
        alertDialog.setMessage("Разбудете се наспани со Канал 77 !");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    //For controling the mPlayer with messages
    //Message types: Init, Play, Stop
    public void controlMediaPlayer(final String msgString){

        Thread playerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Initialize mPlayer
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

                //mPlayer onError listener, sets RADIO_IS_PLAYING to false
                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        threadMsg("Error");
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("RADIO_IS_PLAYING", false);
                        editor.apply();
                        return false;
                    }
                });

                //mPlayer onPrepare listener, hides progressbar
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        threadMsg("Hide");
                    }
                });


                try {
                    threadMsg(msgString);
                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {
                    String controlMessage = msg.getData().getString("message");

                    if(controlMessage.equalsIgnoreCase("Play")){
                        mPlayer.start();
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("RADIO_IS_PLAYING", true);
                        editor.apply();
                    }

                    else{
                        if(controlMessage.equalsIgnoreCase("Stop")){
                            mPlayer.stop();
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putBoolean("RADIO_IS_PLAYING", false);
                            editor.apply();
                        }
                        else{
                            if(controlMessage.equalsIgnoreCase("Hide")){
                                radioProgress.setVisibility(View.GONE);
                            }
                            else {
                                if(controlMessage.equalsIgnoreCase("Error")){
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Error playing radio stream",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Error receiving player control message",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                }
            };

        });
        playerThread.start();


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
        //Start playing the radio stream if music is not active
        if(mPrefs.getBoolean("RADIO_IS_PLAYING", false)){
        }
        else {
            radioProgress.setVisibility(View.VISIBLE);
            controlMediaPlayer("Play");
        }
    }

    @Override
    protected void onDestroy() {
        controlMediaPlayer("Stop");
        super.onDestroy();

    }
}
