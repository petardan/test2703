package com.kanal77.kanal77;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Petar on 3/14/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    SharedPreferences mPrefs;

    @Override
    public void onReceive(final Context context, Intent intent) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        //Show message when alarm wakes
        //Alarm.getAlarmMsg().setText("Enough Rest. Do Work Now!");

        //Start playing alarm ringtone
        /*
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
        */

        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        if(manager.isMusicActive())
        {
            Log.d("Radio Playing", "ON");
            Intent i = new Intent();
            i.setClassName("com.kanal77.kanal77", "com.kanal77.kanal77.Alarm");
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("ALARM_ACTIVATED", true);
            editor.apply();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 5000 milliseconds
            v.vibrate(5000);
        }
        else {
            Log.d("Radio Playing", "OFF");
            //Start radio
            //start activity
            Intent i = new Intent();
            i.setClassName("com.kanal77.kanal77", "com.kanal77.kanal77.Loading");
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("ALARM_ACTIVATED", true);
            editor.apply();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("AlarmActivated", true);
            context.startActivity(i);

        }

    }
}