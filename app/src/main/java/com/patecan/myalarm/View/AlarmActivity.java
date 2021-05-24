package com.patecan.myalarm.View;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.patecan.myalarm.AlarmReceiver;
import com.patecan.myalarm.Data.AlarmDatabaseHandler;
import com.patecan.myalarm.Model.Alarm;
import com.patecan.myalarm.R;

import java.util.Calendar;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private TimePicker mTimePicker;
    private AlarmDatabaseHandler mDatabase;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mTimePicker = findViewById(R.id.alarmActivity_timePicker);
        mDatabase = new AlarmDatabaseHandler(AlarmActivity.this);

        Button btnCancel = findViewById(R.id.alarmActivity_btnCancel);
        Button btnAddAlarm = findViewById(R.id.alarmActivity_btnAddAlarm);
        final EditText editAlarmName = findViewById(R.id.alarmActivity_edtName);


        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the hour and minute that set on the clock.
                int currentHour = mTimePicker.getCurrentHour();
                int currentMinute = mTimePicker.getCurrentMinute();
                String alarmName = editAlarmName.getText().toString();

                // Initialize new Alarm instance that have attribute on the clock and already turn on.
                Alarm activeAlarm = new Alarm(alarmName, currentHour, currentMinute, 1);

                // Insert new Alarm and return the ID of that alarm.
                mId = mDatabase.addAlarm(activeAlarm);
                if (mId > 0) { // Inserted successful
                    onTimeSet(mTimePicker, currentHour, currentMinute);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();

        // Set Calender attributes to the time we had set.
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        /* This method help alarm to started at the Calender time */
        setAlarm(calendar);
    }


    /**
     * @param calendar: This parameter is passed from onTimeSet method
     */
    private void setAlarm(Calendar calendar) {
        // Create Intent that have the operation to start Broadcast and put Information of Alarm to it */
        Intent intentAlarm = new Intent(AlarmActivity.this, AlarmReceiver.class);
        intentAlarm.putExtra("id", mId);
        intentAlarm.putExtra("state", 1);

        // This PendingIntent that will perform a broadcast.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AlarmActivity.this,
                mId,
                intentAlarm,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Using the AlarmManager to Schedule Tasks at the System Level.
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // The AlarmManager will fire off a PendingIntent at the time we have set.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Close Database and move to MainActivity.
        mDatabase.close();
        startActivity(new Intent(AlarmActivity.this, MainActivity.class));
    }
}