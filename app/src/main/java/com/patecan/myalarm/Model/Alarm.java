package com.patecan.myalarm.Model;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

/*
 * This Class Have The Role To Create Each Alarm.
 */
public class Alarm {

    private int mId;
    private String mName;
    private int mHour;
    private int mMinute;
    private int mState;


    public Alarm(String name, int hour, int minute, int state) {
        this.mName = name;
        this.mHour = hour;
        this.mMinute = minute;
        this.mState = state;
    }



    public Alarm(int id, String name, int hour, int minute, int state) {
        this.mName = name;
        this.mHour = hour;
        this.mMinute = minute;
        this.mState = state;
        this.mId = id;
    }

    public Alarm() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int mHour) {
        this.mHour = mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    public int getState() {
        return mState;
    }

    public void setState(int mState) {
        this.mState = mState;
    }
}
