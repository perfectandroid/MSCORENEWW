package com.creativethoughts.iscore;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

/**
 * @author Muthu.Krishnan
 */
public class NotificationMgr {

    private static final int MESSAGE_NOTIFY_ID = 31;

    /**
     * Notification manager singleton object.
     */
    private static NotificationMgr mNotificationMgr;
    /**
     * Application context object
     */
    private Context mContext;
    /**
     * Notification mangager instance.
     */
    private NotificationManager mNotificationManager;
    /**
     * Notification builder object.
     */
    private NotificationCompat.Builder builder;

    private NotificationMgr(Context context) {
        mContext = context;

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void init(Application application) {
        if (mNotificationMgr == null) {
            mNotificationMgr = new NotificationMgr(application);
        }
    }

    public static NotificationMgr getInstance() {
        if (mNotificationMgr == null) {
            throw new RuntimeException(
                    "Must run init(Application application)" + " before an instance can be obtained");
        }

        return mNotificationMgr;
    }

    /**
     * Call this method to cancel notification.
     */
    public void cancelNotification() {
        mNotificationManager.cancel(MESSAGE_NOTIFY_ID);
    }

    /**
     * Show the message content
     *
     * @param message
     */
    public void showNotification(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        builder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Mscore").setContentText(message).setAutoCancel(
                        true) // clear it when user click on it
                .setSmallIcon(R.drawable.aappicon)
                .setOnlyAlertOnce(true);

        Intent resultIntent = new Intent(mContext, PinLoginActivity.class);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // do nothing yet, just show the app
        PendingIntent resultPendingIntent = PendingIntent
                .getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setTicker(message);

        mNotificationManager.notify(MESSAGE_NOTIFY_ID, builder.build());
    }

}
