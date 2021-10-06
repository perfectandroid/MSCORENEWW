package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.gsonmodel.SyncMain;

import java.util.Map;

/**
 * Created by muthukrishnan on 24/09/15.
 */
public class UserDetailsDAO {
    public static final String USER_TABLE = "PB_USERDETAILS";

    private static final String FIELD_USER_ID = "PB_USER_ID";
    private static final String FIELD_USER_CUSTOMER_ID = "customerId";
    private static final String FIELD_USER_CUSTOMER_NUMBER = "customerNo";
    private static final String FIELD_CUSTOMER_NAME = "customerName";
    private static final String FIELD_CUSTOMER_ADDRESS1 = "customerAddress1";
    private static final String FIELD_CUSTOMER_ADDRESS2 = "customerAddress2";
    private static final String FIELD_CUSTOMER_ADDRESS3 = "customerAddress3";
    private static final String FIELD_CUSTOMER_MOBILENO = "mobileNo";
    private static final String FIELD_CUSTOMER_PIN = "pin";
    private static final String FIELD_DEFAULT = "default1";
    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_ONLINE_NEFT = "online_neft";
    private static final String QUERY_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS "+USER_TABLE+" ("+FIELD_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_USER_CUSTOMER_ID + " varchar(50)," +
                    FIELD_USER_CUSTOMER_NUMBER + " varchar(50)," +
                    FIELD_CUSTOMER_NAME + " varchar(75)," +
                    FIELD_CUSTOMER_ADDRESS1 + " varchar(75)," +
                    FIELD_CUSTOMER_ADDRESS2 + " varchar(75)," +
                    FIELD_CUSTOMER_ADDRESS3 + " varchar(75)," +
                    FIELD_CUSTOMER_MOBILENO + " varchar(12)," +
                    FIELD_CUSTOMER_PIN + " BIGINT," +
                    FIELD_DEFAULT + " varchar(50), " +
                    FIELD_ONLINE_NEFT+" INTEGER, "+
                    FIELD_LOGIN + " varchar(50))";
    private static final String[] COLUMNS = {FIELD_USER_ID, FIELD_USER_CUSTOMER_ID, FIELD_USER_CUSTOMER_NUMBER,
                                        FIELD_CUSTOMER_NAME, FIELD_CUSTOMER_ADDRESS1, FIELD_CUSTOMER_ADDRESS2, FIELD_CUSTOMER_ADDRESS3,
                                        FIELD_CUSTOMER_MOBILENO, FIELD_CUSTOMER_PIN, FIELD_DEFAULT, FIELD_ONLINE_NEFT, FIELD_LOGIN};
    private static UserDetailsDAO mInstance;

    public static String getCreateTableString() {
        return QUERY_USER_TABLE;
    }

    public static synchronized UserDetailsDAO getInstance() {
        if (mInstance == null) {
            mInstance = new UserDetailsDAO();

        }

        return mInstance;
    }

   /* public void insertUserDetails(UserDetails userDetails) {

        if(isAlreadyExits(userDetails.customerId)) {
            return;
        }
        ContentValues User_insert = new ContentValues();

        User_insert.put( FIELD_USER_CUSTOMER_ID, userDetails.customerId );
        User_insert.put( FIELD_USER_CUSTOMER_NUMBER, userDetails.userCustomerNo);
        User_insert.put( FIELD_CUSTOMER_NAME, userDetails.userCustomerName);
        User_insert.put( FIELD_CUSTOMER_ADDRESS1, userDetails.userCustomerAddress1);
        User_insert.put( FIELD_CUSTOMER_ADDRESS2, userDetails.userCustomerAddress2);
        User_insert.put( FIELD_CUSTOMER_ADDRESS3, userDetails.userCustomerAddress3);
        User_insert.put( FIELD_CUSTOMER_MOBILENO, userDetails.userMobileNo);
        User_insert.put( FIELD_CUSTOMER_PIN, userDetails.userPin);
        User_insert.put( FIELD_DEFAULT, userDetails.userDefault1);
        User_insert.put( FIELD_LOGIN, userDetails.userLogin);

        long i = IScoreDatabase.getInstance().insert(USER_TABLE, null, User_insert);

        if (IScoreApplication.DEBUG)
            Log.d("insert_result", Long.toString( i));
    }*/
    public void insertGsonUserDetails(SyncMain syncMain){
        if(isAlreadyExits(syncMain.getCustomerId() )) {
            return;
        }
        ContentValues userInsert = new ContentValues();

        userInsert.put(FIELD_USER_CUSTOMER_ID, syncMain.getCustomerId());
        userInsert.put(FIELD_USER_CUSTOMER_NUMBER, syncMain.getCustomerNo() );
        userInsert.put(FIELD_CUSTOMER_NAME, syncMain.getCustomerName() );
        userInsert.put(FIELD_CUSTOMER_ADDRESS1, syncMain.getCustomerAddress1() );
        userInsert.put(FIELD_CUSTOMER_ADDRESS2, syncMain.getCustomerAddress2() );
        userInsert.put(FIELD_CUSTOMER_ADDRESS3, syncMain.getCustomerAddress3() );
        userInsert.put(FIELD_CUSTOMER_MOBILENO, syncMain.getMobileNo() );
        userInsert.put(FIELD_CUSTOMER_PIN, syncMain.getPin() );
        userInsert.put(FIELD_DEFAULT, syncMain.getDefault1() );
        userInsert.put(FIELD_LOGIN, syncMain.getUserLogin() );

        long i = IScoreDatabase.getInstance().insert(USER_TABLE, null, userInsert);

        if (IScoreApplication.DEBUG)
            Log.d("insert_result", Long.toString( i));
    }

    private boolean isAlreadyExits(String customer) {
        Cursor cursor = null;

        try {
            String where = FIELD_USER_CUSTOMER_ID + " = ?";
            String[] whereArgs = {customer};

            cursor = IScoreDatabase.getInstance()
                    .query(USER_TABLE, null, where, whereArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }

        } catch (Exception e) {
           //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public UserDetails getUserDetail() {
         /*UserDetails userDetails = new UserDetails();

       Cursor cursor = IScoreDatabase.getInstance().query(USER_TABLE, null, null, null, null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            userDetails = new UserDetails();

            cursor.moveToFirst();

            userDetails.customerId              = cursor.getString(cursor.getColumnIndex(FIELD_USER_CUSTOMER_ID));
            userDetails.userCustomerNo          = cursor.getString(cursor.getColumnIndex(FIELD_USER_CUSTOMER_NUMBER));
            userDetails.userCustomerName        = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_NAME));
            userDetails.userCustomerAddress1    = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ADDRESS1));
            userDetails.userCustomerAddress2    = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ADDRESS2));
            userDetails.userCustomerAddress3    = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_ADDRESS3));
            userDetails.userMobileNo            = cursor.getString(cursor.getColumnIndex(FIELD_CUSTOMER_MOBILENO));

        }

        if(cursor != null) {
            cursor.close();
        }*/

        Map<String,String> map = IScoreDatabase.getInstance().getSingleRow( USER_TABLE, COLUMNS, null, null, null, null, null,
                true );
        UserDetails userDetails = new UserDetails();
        if ( map.size() > 0 ){

            userDetails.customerId              = map.get(FIELD_USER_CUSTOMER_ID);
            userDetails.userCustomerNo          = map.get(FIELD_USER_CUSTOMER_NUMBER);
            userDetails.userCustomerName        = map.get(FIELD_CUSTOMER_NAME);
            userDetails.userCustomerAddress1    = map.get(FIELD_CUSTOMER_ADDRESS1);
            userDetails.userCustomerAddress2    = map.get(FIELD_CUSTOMER_ADDRESS2);
            userDetails.userCustomerAddress3    = map.get(FIELD_CUSTOMER_ADDRESS3);
            userDetails.userMobileNo            = map.get(FIELD_CUSTOMER_MOBILENO);
        }else userDetails = null;

        return userDetails;
    }


    /**
     * To delete all the user data.
     */
    public void deleteAllRows() {
        long i = IScoreDatabase.getInstance().delete(USER_TABLE, null, null);
        if (IScoreApplication.DEBUG)
            Log.d("deleted userdetails", Long.toString( i));
    }
}
