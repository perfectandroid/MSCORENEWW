package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;

import java.util.Map;

/**
 * Created by vishnu on 7/21/2018 - 11:07 AM.
 */
public class BankVerifier {
    private static BankVerifier mBankVerifier;
    private static final String BANK_VERIFIER_TABLE = "bank_verifier";
    private static final String ID = "_id";
    private static final String VERIFY_STATUS = "verify_status";

    public static final String QUERY_CREATE_BANK_VERIFY = " CREATE TABLE IF NOT EXISTS "+ BANK_VERIFIER_TABLE + " ( "+
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "+
            VERIFY_STATUS + " VARCHAR(5) )";
    private static final String[] COLUMNS= { ID, VERIFY_STATUS };
    public static synchronized BankVerifier getInstance(){
        if ( mBankVerifier == null ){
            mBankVerifier = new BankVerifier();
        }
        return mBankVerifier;
    }
    public long insertValue( String status ){
        try{
            status = IScoreApplication.encryptStart( status );
            ContentValues contentValues = new ContentValues();
            contentValues.put( VERIFY_STATUS, status );
            return IScoreDatabase.getInstance().insert( BANK_VERIFIER_TABLE, null, contentValues );
        }catch ( Exception e ){
            return  -1;
        }
    }
    public String getVerifyStatus(){
        String status;
        String[] columns = { VERIFY_STATUS };
        try{
            Cursor cursor = IScoreDatabase.getInstance().query( BANK_VERIFIER_TABLE, columns, null, null, null, null, null);
            if ( cursor != null && cursor.getCount() > 0 ){
                cursor.moveToLast();
                status = IScoreApplication.decryptStart( cursor.getString( cursor.getColumnIndex( VERIFY_STATUS ) ) );

            }else {
                status = "0";
            }
            cursor.close();
        }catch ( Exception e ){
            status = "0";
        }


        return status;
    }
    public long updateVerifyStatus( String value ){
        long result;
        try{
            value = IScoreApplication.encryptStart( value );
            ContentValues contentValues = new ContentValues();
            contentValues.put( VERIFY_STATUS, value);
            String where = ID + " = ? ";
            String[] whereArgs = { "1" };
            result = IScoreDatabase.getInstance().update( BANK_VERIFIER_TABLE, contentValues, where, whereArgs );
        }catch ( Exception e ){
            result = -1;
        }
        return result;
    }
    public void deleteAllRows() {
        IScoreDatabase.getInstance().delete( BANK_VERIFIER_TABLE, null, null);
    }
}
