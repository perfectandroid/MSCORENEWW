package com.creativethoughts.iscore.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Common {

    private Context context;
    public static final int timeInMillisecond=180000;
    private static final boolean HOSTNAMEVERFICATION_MANUAL=true;


    private static final String HOSTNAME_SUBJECT="STATIC-VM";
    private static final String CERTIFICATE_ASSET_NAME="mscoredemo.pem";/*
    public static final String BASE_URL="https://202.164.150.65:14264";
    private static final String API_NAME= "/Mscore/api/MV3";*/


    SharedPreferences pref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
    public static final String BASE_URL=pref.getString("baseurl", null);
    //public static final String BASE_URL="https://202.164.150.65:14264/Mscore/";
    private static final String API_NAME= "api/MV3";

    //==== ==== ==== ===== ===== ===== ==== ==== ==== ==== ==== ==== ==== ==== ==== ====

    public static boolean isHostnameverficationManual() {
        return HOSTNAMEVERFICATION_MANUAL;
    }

    public static String getHostnameSubject() {
        return HOSTNAME_SUBJECT;
    }

    public static String getCertificateAssetName() {
        return CERTIFICATE_ASSET_NAME;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getApiName() {
        return API_NAME;
    }



}
