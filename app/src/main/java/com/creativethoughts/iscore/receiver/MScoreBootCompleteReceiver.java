package com.creativethoughts.iscore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.utility.SyncUtils;

/**
 * Created by muthukrishnan on 09/10/15.
 */
public class MScoreBootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SyncUtils.startAlarmManage(context);
        } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo =
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            if (isConnected) {
                long intervalTime = SettingsDAO.getInstance().getIntervalTime();

                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

                if (settingsModel != null) {
                    long lastSync = settingsModel.lastSyncTime;

                    long currentTime = System.currentTimeMillis();

                    if ((currentTime - lastSync) > intervalTime) {
                        SyncUtils.startAlarmManage(context);
                    }
                }
            }
        }
    }
}
