/*
 *  Copyright (C) 2013 Zaark
 *  All rights reserved.
 *
 *  All code contained herein is and remains the property of Zaark.
 *  It may only be used only in accordance with the terms of the license agreement.
 *
 *  Contact: contact@zaark.com
 */
package com.creativethoughts.iscore.utility;

import android.content.Context;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;

        import com.creativethoughts.iscore.IScoreApplication;

        import java.util.ArrayList;

/**
 * This Utility class use for getting the common actions related to networks.
 */
public class NetworkUtil {

    /**
     * To check Whether the network is available or not.
     *
     * @return (true -> Network available false -> Not network)
     */
    public static boolean isOnline() {
        boolean isConnected = false;
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) IScoreApplication.getAppContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        if (networkInfo != null) {
            final int size = networkInfo.length;
            for (int i = 0; i < size; i++) {
                final NetworkInfo netInfo = networkInfo[i];
                if (netInfo.isConnected()) {
                    isConnected = true;
                    break;
                }
            }
        }

        return isConnected;
    }

}
