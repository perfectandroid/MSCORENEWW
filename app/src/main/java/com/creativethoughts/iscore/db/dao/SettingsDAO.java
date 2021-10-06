package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;

/**
 * Created by muthukrishnan on 26/09/15
 */
public class SettingsDAO {
    public static final String SETTINGS_TABLE = "AC_SETTINGS";

    private static final String FIELD_ID = "_id";
    private static final String FIELD_DAYS = "days";
    private static final String FIELD_SYNC_INTERVAL_TIME_MILLIS = "sync_interval_time_ms";
    private static final String FIELD_HOURS = "hours";
    private static final String FIELD_MINUTES = "min";
    private static final String FIELD_LAST_ACCESSED_TIME = "last_accessed_time";
    private static final String FIELD_CUSTOMER_ID = "customerId";
    private final static String QUERY_SETTINGS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE + " (" +
                    FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_DAYS + " INTEGER," +
                    FIELD_SYNC_INTERVAL_TIME_MILLIS + " VARCHAR," +
                    FIELD_HOURS + " INTEGER," +
                    FIELD_MINUTES + " INTEGER," +
                    FIELD_LAST_ACCESSED_TIME + " VARCHAR," +
                    FIELD_CUSTOMER_ID + " VARCHAR)";
    private static SettingsDAO mInstance;

    public static String getCreateTableString() {
        return QUERY_SETTINGS_TABLE;
    }

    public static SettingsDAO getInstance() {
        if (mInstance == null) {
            synchronized (SettingsDAO.class) {
                if (mInstance == null) {
                    mInstance = new SettingsDAO();
                }
            }
        }

        return mInstance;
    }

    /**
     * To update the settings value.
     *
     * @param days
     * @param hour
     * @param minutes
     * @param accountNo
     */
    public void insertValues(String days, int hour, int minutes, String accountNo) {

        deleteAllRows();

        ContentValues contentValues = new ContentValues();

        long intervalTimeInMillis = ((hour * 60 * 60) + (minutes * 60)) * 1000;

        contentValues.put(FIELD_DAYS, days);
        contentValues.put(FIELD_HOURS, hour);
        contentValues.put(FIELD_MINUTES, minutes);
        contentValues.put(FIELD_SYNC_INTERVAL_TIME_MILLIS, intervalTimeInMillis);
        contentValues.put(FIELD_CUSTOMER_ID, accountNo);

        IScoreDatabase.getInstance().insert(SETTINGS_TABLE, null, contentValues);
    }

    /**
     * To get the interval TIME_IN_MILLISECOND.
     *
     * @return
     */
    public long getIntervalTime() {

        long intervalTime = 0;

        Cursor cursor = IScoreDatabase.getInstance()
                .query(SETTINGS_TABLE, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            intervalTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(FIELD_SYNC_INTERVAL_TIME_MILLIS)));
        }

        if (cursor != null) {
            cursor.close();
        }

        return intervalTime;
    }


    /**
     * To get the setting details.
     *
     * @return
     */
    public SettingsModel getDetails() {
        SettingsModel settingsModel = null;

        Cursor cursor = IScoreDatabase.getInstance()
                .query(SETTINGS_TABLE, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            settingsModel = new SettingsModel();

            cursor.moveToFirst();

            settingsModel.customerId = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ID));
            settingsModel.days = Integer.valueOf(cursor.getString(cursor.getColumnIndex(FIELD_DAYS)));
            settingsModel.hours = cursor.getInt(cursor.getColumnIndex(FIELD_HOURS));
            settingsModel.minutes = cursor.getInt(cursor.getColumnIndex(FIELD_MINUTES));

            String lastTime = cursor.getString(cursor.getColumnIndex(FIELD_LAST_ACCESSED_TIME));

            if (TextUtils.isEmpty(lastTime)) {
                settingsModel.lastSyncTime = 0;
            } else {
                settingsModel.lastSyncTime = Long.parseLong(lastTime);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return settingsModel;
    }

    public long getId() {
        long settingsModel = -1;

        Cursor cursor = IScoreDatabase.getInstance()
                .query( SETTINGS_TABLE, null, null, null, null, null, null );

        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            settingsModel = cursor.getLong( cursor.getColumnIndex(FIELD_ID) );
        }

        if (cursor != null) {
            cursor.close();
        }

        return settingsModel;
    }

    /**
     * To update sync TIME_IN_MILLISECOND.
     */
    public void updateSyncTime() {

        long lastId = getId();

        if(lastId == -1) {
            return;
        }

        String where = FIELD_ID + " = ?";
        String[] whereArgs = {String.valueOf(lastId)};

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_LAST_ACCESSED_TIME, String.valueOf(System.currentTimeMillis()));

        IScoreDatabase.getInstance().update(SETTINGS_TABLE, contentValues, where, whereArgs);
    }


    /**
     * To delete all setting data.
     */
    public void deleteAllRows() {
        IScoreDatabase.getInstance().delete(SETTINGS_TABLE, null, null);
    }

    public String getTables(){
        Cursor c = IScoreDatabase.getInstance().rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        String name = "";
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                name += c.getString(0);
                c.moveToNext();
            }
        }
        return name;
    }
}
