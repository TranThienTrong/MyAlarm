package com.patecan.myalarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

public class AlarmService extends Service {

    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = MediaPlayer.create(this, R.raw.chuongbaothuc);
    }


    /**
     * @param intent: This parameter is receives from AlarmReceiver which send to this Service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int alarmState = intent.getIntExtra("state", 0);

        /*
         * If The Alarm State In Intent Is 1, start the media player, otherwise turn off.
         */

        if (alarmState == 1) {

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = MediaPlayer.create(this, R.raw.chuongbaothuc);
            }
            
            mMediaPlayer.start();
            Log.d("onStartCommand", "Start Alarm ");
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            Log.d("onStartCommand", "Stop Alarm ");

        }

        return START_NOT_STICKY;
    }
}
