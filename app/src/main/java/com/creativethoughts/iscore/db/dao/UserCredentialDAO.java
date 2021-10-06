package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.UserCredential;

import java.util.Map;

/**
 * Created by muthukrishnan on 22/09/15.
 */
public class UserCredentialDAO {

    public static final String USER_TABLE = "PB_AUTHENTICATE";

    private static final String FIELD_USER_ID = "PB_REG_ID";
    private static final String FIELD_USER_PIN = "pin";
    private static final String FIELD_USER_MOBILE_NUMBER = "mobileNumber";
    private static final String FIELD_COUNTRY_CODE = "countryCode";
    private static final String FIELD_LOGIN_FLAG = "loginFlag";
    private static final String FIELD_TOKEN = "token";
    private final static String QUERY_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE
                    + " (" + FIELD_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_USER_PIN + " VARCHAR(15), " +
                    FIELD_USER_MOBILE_NUMBER + " VARCHAR(15), " +
                    FIELD_COUNTRY_CODE + " VARCHAR(15), " +
                    FIELD_LOGIN_FLAG + " INTEGER, "+
                    FIELD_TOKEN+"  TEXT );";
    private static final String QUERY_UPGRADE_VERSION_TWO=
            "ALTER TABLE "+USER_TABLE+" ADD COLUMN "+
                    FIELD_TOKEN+"  TEXT";

    private static final String[] COLUMNS = {FIELD_USER_ID, FIELD_USER_PIN, FIELD_USER_MOBILE_NUMBER, FIELD_COUNTRY_CODE, FIELD_LOGIN_FLAG, FIELD_TOKEN };
    private static UserCredentialDAO mInstance;

    public static String getCreateTableString() {
        return QUERY_USER_TABLE;
    }

    public static String getUpgradeCredentialTable(){
        return QUERY_UPGRADE_VERSION_TWO;
    }

    public static UserCredentialDAO getInstance() {
        if (mInstance == null) {
            synchronized (UserCredentialDAO.class) {
                if (mInstance == null) {
                    mInstance = new UserCredentialDAO();
                }
            }
        }

        return mInstance;
    }

    public void insertUserDetails(String mobileNumber, String countryCode) {
        ContentValues cv = new ContentValues();

//        cv.put(FIELD_USER_PIN, pin);
        cv.put(FIELD_COUNTRY_CODE, countryCode);
        cv.put(FIELD_USER_MOBILE_NUMBER, mobileNumber);
        // Initially they will not login, this happen when before we get OTP, after we get OTP change to 1.
        cv.put(FIELD_LOGIN_FLAG, 0);

        IScoreDatabase.getInstance().insert(USER_TABLE, null, cv);
    }

    public void updateUserLogin() {
        ContentValues cv = new ContentValues();

        cv.put(FIELD_LOGIN_FLAG, 1);

        IScoreDatabase.getInstance().update(USER_TABLE, cv, null, null);
    }

    public void updateToken(String tokenPass){
        ContentValues cv = new ContentValues();
        String newToken="";
        try {
            newToken=IScoreApplication.encryptStart(tokenPass);
        } catch (Exception e) {

            if(IScoreApplication.DEBUG)   Log.e("exception",e.toString()+"");
        }
        cv.put(FIELD_TOKEN,newToken );

        IScoreDatabase.getInstance().update(USER_TABLE, cv, null, null);
    }


    public void updateNewPin(String newPinPass) {
        String newPin="";
        try {
            newPin=IScoreApplication.encryptStart(newPinPass);
        } catch (Exception e) {

         if(IScoreApplication.DEBUG)   Log.e("exception",e.toString()+"");
        }
        ContentValues cv = new ContentValues();

        cv.put(FIELD_USER_PIN, newPin);

        IScoreDatabase.getInstance().update(USER_TABLE, cv, null, null);
    }

    public boolean isUserAlreadyLogin() {
        Cursor cursor = null;

        try {
            cursor = IScoreDatabase.getInstance()
                    .query(USER_TABLE, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                int flag = cursor.getInt(cursor.getColumnIndex(FIELD_LOGIN_FLAG));

                if (flag == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public UserCredential getLoginCredential() {
        Cursor cursor = null;

        try {
            cursor = IScoreDatabase.getInstance()
                    .query(USER_TABLE, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                UserCredential userCredential = new UserCredential();
                userCredential.countryCode = cursor.getString(cursor.getColumnIndex(FIELD_COUNTRY_CODE));
                userCredential.mobileNumber = cursor.getString(cursor.getColumnIndex(FIELD_USER_MOBILE_NUMBER));
                userCredential.flag = cursor.getInt(cursor.getColumnIndex(FIELD_LOGIN_FLAG));
                userCredential.pin = IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(FIELD_USER_PIN)));
                userCredential.token=IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(FIELD_TOKEN)));

                return userCredential;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
        /*Map<String,String> userData = IScoreDatabase.getInstance().getSingleRow(USER_TABLE, COLUMNS, null, null, null, null, null,
                true);
        if ( userData.size() > 0 ){
            try{
                UserCredential userCredential = new UserCredential();
                userCredential.countryCode = userData.get(FIELD_COUNTRY_CODE);
                userCredential.mobileNumber = userData.get(FIELD_USER_MOBILE_NUMBER);
                userCredential.flag = Integer.parseInt(userData.get(FIELD_LOGIN_FLAG));
                userCredential.pin = IScoreApplication.decryptStart(userData.get(FIELD_USER_PIN));
                userCredential.token = IScoreApplication.decryptStart( userData.get( FIELD_TOKEN ) );
                return userCredential;
            }catch ( Exception e ){
                return null;
            }

        }else return null;*/
    }

    /**
     * To delete all the user data.
     */
    public void deleteAllUserData() {
        IScoreDatabase.getInstance().delete(USER_TABLE, null, null);
    }
}
