package com.creativethoughts.iscore.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Common {

    public static final int timeInMillisecond=180000;
    private static final boolean HOSTNAMEVERFICATION_MANUAL=true;


//    private static final String HOSTNAME_SUBJECT="STATIC-VM";
//    private static final String CERTIFICATE_ASSET_NAME="mscoredemo.pem";

    public static boolean isHostnameverficationManual() {
        return HOSTNAMEVERFICATION_MANUAL;
    }

   /* public static String getHostnameSubject() {
        return HOSTNAME_SUBJECT;
    }

    public static String getCertificateAssetName() {
        return CERTIFICATE_ASSET_NAME;
    }*/



}
