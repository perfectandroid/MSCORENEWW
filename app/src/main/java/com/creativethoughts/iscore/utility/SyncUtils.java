package com.creativethoughts.iscore.utility;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.receiver.KeepUpdateBroadcastReceiver;

/**
 * Created by muthukrishnan on 09/10/15.
 */
public class SyncUtils {

    /**
     * start the alarm manager
     */
    public static void startAlarmManage(Context context) {

        stopAlarmManager(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

        Intent intent = new Intent(context, KeepUpdateBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, 90190, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        long intervalTime = SettingsDAO.getInstance().getIntervalTime();

        if (intervalTime < 1000) {
            return;
        }

        alarmManager
                .setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalTime,
                        intervalTime, pendingIntent);
    }

    /**
     * stops the alarm manager
     */
    private static void stopAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

        Intent intentStop = new Intent(context, KeepUpdateBroadcastReceiver.class);
        PendingIntent senderStop = PendingIntent.getBroadcast(context, 90190, intentStop, 0);

        alarmManager.cancel(senderStop);
    }
}
