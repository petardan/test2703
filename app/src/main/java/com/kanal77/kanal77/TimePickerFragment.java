package com.kanal77.kanal77;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Petar on 3/22/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    TextView alarmTime;
    SharedPreferences mPrefs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //Do something when the time is set
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){



        alarmTime = (TextView)getActivity().findViewById(R.id.alarm_time);
        if(minute<10){
            alarmTime.setText(hourOfDay + ":0" + minute);
        }
        else{
            alarmTime.setText(hourOfDay + ":" + minute);
        }


    }
}