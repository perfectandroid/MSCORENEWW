package com.creativethoughts.iscore.utility;

/**
 * Created by Brijesh on 26-03-2015.
 */


public final class DBQuery {


   public static final String CREATE_TABLE_IF_NECCESSARY_PB_AUTHENTICATION = "CREATE TABLE IF NOT EXISTS PB_AUTHENTICATE(PB_REG_ID INTEGER PRIMARY KEY AUTOINCREMENT," + " pin varchar(15)," + " mobileNumber varchar(15)," + " countryCode varchar(15)," + " loginFlag integer)";


}
