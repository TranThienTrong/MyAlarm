package com.patecan.myalarm.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.patecan.myalarm.Model.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

/*
 * This Class Have The Role To Create Alarm Database.
 */
public class AlarmDatabaseHandler extends SQLiteOpenHelper {

    // Constraint of Database information.
    public static final String DATABASE_NAME = "alarm_db";
    public static final int DATABASE_VERSION = 1;

    // Constraint of Table information In Database.
    public static final String TABLE_NAME = "alarms";
    // Name of Columns in alarms table.
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_STATE = "state";


    public AlarmDatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Create Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTable =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_NAME + " TEXT, " +
                        KEY_HOUR + " INTEGER, " +
                        KEY_MINUTE + " INTEGER, " +
                        KEY_STATE + " INTEGER" +
                        ")";

        sqLiteDatabase.execSQL(queryCreateTable);
    }

    // Update Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String queryDropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(queryDropTable);
        onCreate(sqLiteDatabase);
    }

    public Alarm getAlarm(int id) {
        SQLiteDatabase database = this.getReadableDatabase();

        /*
         * The cursor stores the rows of data returned by the query
         * along with a position that points to the current row of data in the result set.
         */
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{KEY_ID, KEY_NAME, KEY_HOUR, KEY_MINUTE, KEY_STATE},
                KEY_ID + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        /*
         * When a cursor is returned from a query() method, its position points to the spot before the first row of data.
         * So we must set the position of cursor to valid row, which is first row
         */
        if (cursor != null) {
            cursor.moveToNext();
        }


        // Create new alarm instance from the columns data of the cursor
        Alarm alarm = new Alarm(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getInt(cursor.getColumnIndex(KEY_HOUR)),
                cursor.getInt(cursor.getColumnIndex(KEY_MINUTE)),
                cursor.getInt(cursor.getColumnIndex(KEY_STATE)));

        cursor.close();
        database.close();
        return alarm;
    }

    /**
     * Get all Alarms from table then add to new List
     *
     * @return List of Alarms in Database.
     */
    public List<Alarm> getAllAlarm() {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String queryGetAll = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = database.rawQuery(queryGetAll, null);


        // Move cursor to the first row from querying, then iterator to each row.
        if (cursor.moveToFirst()) {
            do {
                // Get alarms from its ID, then add to list
                Alarm alarm = getAlarm(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return alarmList;
    }


    /**
     * Add Alarm To Database
     *
     * @param alarm: Use attribute of this parameter to insert into database.
     * @return id of alarm have added.
     */
    public int addAlarm(Alarm alarm) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, alarm.getName());
        values.put(KEY_HOUR, alarm.getHour());
        values.put(KEY_MINUTE, alarm.getMinute());
        values.put(KEY_STATE, alarm.getState());

        int rowInserted = (int) database.insert(TABLE_NAME, null, values);
        database.close();

        return rowInserted;
    }

    /**
     * Update Existing Alarm Row In Table Base On ID
     *
     * @param name, hour, minute, state: Use values of this parameter to change.
     * @param id:   ID of alarm we want to update.
     * @return id of alarm have updated.
     */
    public int updateAlarm(String name, int hour, int minute, int state, int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_HOUR, hour);
        values.put(KEY_MINUTE, minute);
        values.put(KEY_STATE, state);

        int rowIdUpdated = database.update(TABLE_NAME,
                values,
                KEY_ID + "= ?",
                new String[]{String.valueOf(id)});

        database.close();
        return rowIdUpdated;
    }

    /**
     * Delete Existing Alarm Row In Table Base On ID
     *
     * @param id: of alarm we want to delete.
     */
    public void deleteAlarm(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(
                TABLE_NAME,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.close();
    }
}
