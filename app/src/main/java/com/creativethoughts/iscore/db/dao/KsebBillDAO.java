package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.KsebBillModel;

import java.util.ArrayList;

/**
 * Created by vishnu on 3/13/2017
 */

public class KsebBillDAO {
    private static final String KSEB_BILL_TABLE = "KSEB_BILL";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_CONSUMER_NAME = "consumer_name";
    private static final String FIELD_CONSUMER_MOBILE = "mobile_no";
    private static final String FIELD_CONSUMER_NO = "consumer_no";
    private static final String FIELD_SECTION = "section";
    private static final String ACCOUNT_NO = "account_no";
    private static final String TRANSACTION_ID = "transaction_id";
    private static final String QUERY_KSEB_BILL_TABLE =
            "CREATE TABLE IF NOT EXISTS " + KSEB_BILL_TABLE + " ( " +
            FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_CONSUMER_NAME + " VARCHAR(100), "+
            FIELD_CONSUMER_NO + " VARCHAR(100), " +
            FIELD_CONSUMER_MOBILE + " VARCHAR(100), "+
            FIELD_SECTION + " VARCHAR(100), "+
            ACCOUNT_NO + " VARCHAR(100), "+
            TRANSACTION_ID + " VARCHAR(100)) ";
    private static KsebBillDAO ksebBillDAO;

    public static String getCreateTableString(){
        return QUERY_KSEB_BILL_TABLE;
    }

    public static KsebBillDAO getInstance(){
        if(ksebBillDAO == null){
            synchronized (KsebBillDAO.class){
                if(ksebBillDAO == null)
                    ksebBillDAO = new KsebBillDAO();
            }
        }
        return ksebBillDAO;
    }

    public String insertValues(String consumerName, String mobileNo, String consumerNo, String section, String accountNo, String transactionId){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_CONSUMER_NAME, IScoreApplication.encryptStart(consumerName));
            contentValues.put(FIELD_CONSUMER_NO, IScoreApplication.encryptStart(consumerNo) );
            contentValues.put(FIELD_CONSUMER_MOBILE, IScoreApplication.encryptStart(mobileNo)  );
            contentValues.put(FIELD_SECTION, IScoreApplication.encryptStart(section) );
            contentValues.put(ACCOUNT_NO, IScoreApplication.encryptStart(accountNo) );
            contentValues.put(TRANSACTION_ID, IScoreApplication.encryptStart(transactionId));
            IScoreDatabase.getInstance().insert(KSEB_BILL_TABLE, null, contentValues);
            return "3";
        }catch (Exception e){
            return e.toString();
        }
    }
    public ArrayList<String> getListFromDb(String columnName){
        Cursor cursor = null;
        ArrayList<String> name = new ArrayList<>();
        String[] columns = {columnName};
            try{
                cursor = IScoreDatabase.getInstance().query(KSEB_BILL_TABLE, columns, null, null, null, null, null);
               while(cursor.moveToNext()){
                   if(!name.contains(IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(columnName)))))
                    name.add(IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(columnName))));
               }
                cursor.close();
            }catch (Exception e){
                cursor.close();
            }
        return name;
    }



    public KsebBillModel getRow(String columnName, String value){
        String tempValue = value;
        try{
            tempValue = IScoreApplication.encryptStart(value);
        }catch (Exception e){

        }
        KsebBillModel ksebBillModel = new KsebBillModel();
        Cursor cursor = null;
        String[] column = {FIELD_CONSUMER_NO, FIELD_CONSUMER_NAME, FIELD_CONSUMER_MOBILE};
        String selection = columnName+"  = ? ";
        String[] selectionArgs = {tempValue};
        try{
            cursor = IScoreDatabase.getInstance().query(KSEB_BILL_TABLE, column, selection, selectionArgs, null,null,null);
            while (cursor.moveToNext()){
                ksebBillModel.consumerName = IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(FIELD_CONSUMER_NAME)));
                ksebBillModel.consumerNo = IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(FIELD_CONSUMER_NO)));
                ksebBillModel.mobileNo = IScoreApplication.decryptStart(cursor.getString(cursor.getColumnIndex(FIELD_CONSUMER_MOBILE)));
            }
        }catch (Exception e){

        }
        cursor.close();
        return ksebBillModel;
    }

    public boolean ifExists(String columnName, String value){
        boolean flag = false;
        Cursor cursor = null;
        String[] selectionArgs = {value};
        try {
            String tempValue = IScoreApplication.encryptStart(value);
            cursor = IScoreDatabase.getInstance().query(KSEB_BILL_TABLE, null, columnName+" =? ", selectionArgs, null, null, null );
            if(cursor.getCount() > 0)
                flag = true;
        }catch (Exception e){

        }
        return flag;
    }

    public void deleteAll(){
        IScoreDatabase.getInstance().delete( KSEB_BILL_TABLE, null, null);
    }

}
