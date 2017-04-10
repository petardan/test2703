package com.kanal77.kanal77;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {


    Button setAlarm;
    Button cancelAlarm;
    FloatingActionButton addAlarm;
    ImageButton xCancelAlarm;

    TextView alarmTime;
    TextView repeatIntervalInfo;

    RadioGroup alarmIntervals;

    RelativeLayout currentAlarmLayout;

    SharedPreferences mPrefs;
    Boolean alarmStatus;

    String alarmHour;
    String alarmMinute;

    int alarmInterval = 0;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        setAlarm = (Button)findViewById(R.id.button_set_alarm);
        cancelAlarm = (Button)findViewById(R.id.button_stop_alarm);
        addAlarm = (FloatingActionButton) findViewById(R.id.alarm_fab);
        xCancelAlarm = (ImageButton)findViewById(R.id.button_x_cancel);
        alarmTime = (TextView)findViewById(R.id.alarm_time);
        repeatIntervalInfo = (TextView)findViewById(R.id.repeat_interval_info);
        alarmIntervals = (RadioGroup)findViewById(R.id.radio_repeat_interval);

        currentAlarmLayout = (RelativeLayout)findViewById(R.id.current_alarm_layout);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(Alarm.this);

        alarmTime.setText(mPrefs.getString("ALARM_TIME", "00:00"));
        repeatIntervalInfo.setText(mPrefs.getString("ALARM_INTERVAL", "No repeat"));
        alarmStatus = mPrefs.getBoolean("ALARM_STATUS", false);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(Alarm.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, myIntent, 0);

        //Show enable/disable alarm button based on alarm status
        if(alarmStatus){
            alarmIntervals.setVisibility(View.GONE);
            setAlarm.setVisibility(View.GONE);
            addAlarm.setVisibility(View.GONE);

            currentAlarmLayout.setVisibility(View.VISIBLE);
            cancelAlarm.setVisibility(View.VISIBLE);

        }
        else {
            addAlarm.setVisibility(View.VISIBLE);

            setAlarm.setVisibility(View.GONE);
            alarmIntervals.setVisibility(View.GONE);
            currentAlarmLayout.setVisibility(View.GONE);
            cancelAlarm.setVisibility(View.GONE);

        }


        //Add alarm show alarm settings
        addAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentAlarmLayout.setVisibility(View.VISIBLE);
                alarmIntervals.setVisibility(View.VISIBLE);
                addAlarm.setVisibility(View.GONE);
                setAlarm.setVisibility(View.VISIBLE);
                cancelAlarm.setVisibility(View.VISIBLE);
                xCancelAlarm.setVisibility(View.GONE);
            }
        });


        //Opens time picker on click
        alarmTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                alarmIntervals.setVisibility(View.VISIBLE);
                setAlarm.setVisibility(View.VISIBLE);
            }
        });


        //Sets repeat text info based on selected radio button
        alarmIntervals.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                SharedPreferences.Editor editorAlarmInterval = mPrefs.edit();
                editorAlarmInterval.putString("ALARM_INTERVAL", radioButton.getText().toString());
                editorAlarmInterval.apply();
                repeatIntervalInfo.setText(radioButton.getText());
            }
        });


        //Setting the alarm intent
        setAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                long alarmTimeInMillis = 0;

                //Save alarm in shared prefs
                SharedPreferences.Editor editorAlarm = mPrefs.edit();
                editorAlarm.putString("ALARM_TIME", alarmTime.getText().toString());
                editorAlarm.apply();

                String alarmTimeString = alarmTime.getText().toString();
                String[] separatedHoursAndMinutes = alarmTimeString.split(":");
                alarmHour = separatedHoursAndMinutes[0].trim();
                alarmMinute = separatedHoursAndMinutes[1].trim();



                //Setting calendar variable for the alarm
                //If alarm time is in the past add 24 hours so that the alarm is not fired for past time
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarmHour));
                calendar.set(Calendar.MINUTE, Integer.valueOf(alarmMinute));

                if(calendar.getTimeInMillis() <= System.currentTimeMillis())
                    alarmTimeInMillis = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
                else
                    alarmTimeInMillis = calendar.getTimeInMillis();

                alarmInterval = alarmIntervals.getCheckedRadioButtonId();

                // Check which radio button was clicked
                switch(alarmInterval) {
                    case R.id.interval_fifteen_minutes:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                        break;
                    case R.id.interval_half_hour:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
                        break;
                    case R.id.interval_hour:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_HOUR, pendingIntent);
                        break;
                    case R.id.interval_half_day:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
                        break;
                    case R.id.interval_day:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
                        break;
                    case R.id.interval_off:
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
                        break;
                }

                //Set alarm status to true
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ALARM_STATUS", true);
                editor.apply();

                currentAlarmLayout.setVisibility(View.VISIBLE);
                alarmIntervals.setVisibility(View.GONE);
                addAlarm.setVisibility(View.GONE);
                setAlarm.setVisibility(View.GONE);
                cancelAlarm.setVisibility(View.GONE);
                xCancelAlarm.setVisibility(View.VISIBLE);

            }
        });


        //Cancel alarm
        cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set alarm status to false
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ALARM_STATUS", false);
                editor.apply();

                cancelAlarm();

                currentAlarmLayout.setVisibility(View.GONE);
                alarmIntervals.setVisibility(View.GONE);
                addAlarm.setVisibility(View.VISIBLE);
                setAlarm.setVisibility(View.GONE);
                cancelAlarm.setVisibility(View.GONE);
            }
        });

        xCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set alarm status to false
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ALARM_STATUS", false);
                editor.apply();

                cancelAlarm();

                currentAlarmLayout.setVisibility(View.GONE);
                alarmIntervals.setVisibility(View.GONE);
                addAlarm.setVisibility(View.VISIBLE);
                setAlarm.setVisibility(View.GONE);
                cancelAlarm.setVisibility(View.GONE);
            }
        });


    }

    private void cancelAlarm() {
        if (alarmManager!= null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
