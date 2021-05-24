package com.patecan.myalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

/*
 * This Class Have The Role To Receive The Pending Intent From AlarmActivity.
 * Then start the Alarm Service
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /* Receive The Intent From AlarmActivity, get the state value, put it to new Intent */
        int alarmState = intent.getIntExtra("state", 0);
        Intent myIntent = new Intent(context, AlarmService.class);
        myIntent.putExtra("state", alarmState);

        /*  Start the Alarm Service With State Value In myIntent. */
        context.startService(myIntent);
    }

}

