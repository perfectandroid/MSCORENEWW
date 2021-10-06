/*
 *  Copyright (C) 2013-2014 Zaark
 *  All rights reserved.
 *
 *  All code contained herein is and remains the property of Zaark.
 *  It may only be used only in accordance with the terms of the license agreement.
 *
 *  Contact: contact@zaark.com
 */

package com.creativethoughts.iscore.utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preference Utility class to store the value in preference.
 * 
 */
public class PreferenceUtil {


    public static final String IS_FIRST_TIME_SYNC = "IS_FIRST_TIME_SYNC";
    private static final String KEY_PREFERENCE = "mscore_preference";
    private static PreferenceUtil instance;
    /**
     * SharedPreferences object.
     */
    private SharedPreferences sharedPrefs;

    /**
     * Preference editor object.
     */
    private Editor prefsEditor;

    /**
     * Constructor for ZaarkPreferenceUtil.
     * 
     * @param context
     */
    private PreferenceUtil(final Context context) {
        sharedPrefs = context.getSharedPreferences(KEY_PREFERENCE,
                Activity.MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();
    }

    /**
     * Initialize at starting of the application using application context.
     * 
     * @param application application object.
     */
    public static void init(final Application application) {
        if (instance == null) {
            instance = new PreferenceUtil(
                    application.getApplicationContext());
        }
    }

    /**
     * Get the ZaarkPreferenceUtil singleton object.
     * 
     * @return ZaarkPreferenceUtil object
     */
    public static PreferenceUtil getInstance() {
        if (instance == null) {
            throw new RuntimeException("Must run init(Application application)"
                    + " before an instance can be obtained");
        }
        return instance;
    }

    /**
     * To get the Stored string value in Preference.
     * 
     * @param key
     * @param defaultvalue
     * @return stored string value.
     */
    public String getStringValue(final String key, final String defaultvalue) {
        return sharedPrefs.getString(key, defaultvalue);
    }

    /**
     * To store the string value in prefernce.
     * 
     * @param key
     * @param value
     */
    public void setStringValue(final String key, final String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    /**
     * To get the stored integer value in the preference.
     * 
     * @param key
     * @param defaultvalue
     * @return stored integer value.
     */
    public int getIntValue(final String key, final int defaultvalue) {
        return sharedPrefs.getInt(key, defaultvalue);
    }

    /**
     * To stored the integer value in preference.
     * 
     * @param key
     * @param value
     */
    public void setIntValue(final String key, final int value) {
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    /**
     * To get the stored long value in the preference.
     * 
     * @param key
     * @param defaultvalue
     * @return stored long value.
     */
    public long getLongValue(final String key, final long defaultvalue) {
        return sharedPrefs.getLong(key, defaultvalue);
    }

    /**
     * To stored the long value in preference.
     * 
     * @param key
     * @param value
     */
    public void setLongValue(final String key, final long value) {
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    /**
     * To get the stored boolean value from the preference.
     * 
     * @param key
     * @param defaultvalue
     * @return
     */
    public boolean getBooleanValue(final String key, final Boolean defaultvalue) {
        return sharedPrefs.getBoolean(key, defaultvalue);
    }

    /**
     * To set the stored boolean value in preference.
     * 
     * @param key
     * @param value
     */
    public void setBooleanValue(final String key, final boolean value) {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }
}
