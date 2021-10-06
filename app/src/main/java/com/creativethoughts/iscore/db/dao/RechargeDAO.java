package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.RechargeModel;

import java.util.ArrayList;

/**
 * Created by vishnu on 3/20/2017 - 10:39 AM.
 */

public class RechargeDAO {

    private static final String RECHARGE_BILL_TABLE = "RECHARGE_TABLE";

    private static final String FIELD_ID = "_id";
    private static final String FIELD_MOBILE_NO = "mobile_no";
    private static final String FIELD_TYPE = "type"; // enter whether type of recharge
    private static final String FIELD_SERVICE_PROVIDER = "service_provider";
    private static final String QUERY_KSEB_BILL_TABLE =
            " CREATE TABLE IF NOT EXISTS  "+ RECHARGE_BILL_TABLE + " ( "+
                    FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_TYPE+ " INTEGER, "+
                    FIELD_MOBILE_NO + " VARCHAR(12) UNIQUE, "+
                    FIELD_SERVICE_PROVIDER + " VARCHAR(100) )";
    private static RechargeDAO rechargeDAO;

    public static String getCreateTableString(){ return QUERY_KSEB_BILL_TABLE; }

    public static RechargeDAO getInstance(){
        if(rechargeDAO == null){
            synchronized (RechargeDAO.class){
                if(rechargeDAO == null){
                    rechargeDAO = new RechargeDAO();
                }
            }
        }
        return rechargeDAO;
    }
    public void deleteAllRows(){
        IScoreDatabase.getInstance().delete(RECHARGE_BILL_TABLE, null, null);
    }
    public String insertValues(RechargeModel rechargeModelParamObj) {
        try {
            Cursor cursor = IScoreDatabase.getInstance().query(RECHARGE_BILL_TABLE, null, " "+
                    FIELD_MOBILE_NO+" = "+rechargeModelParamObj.mobileNo ,null, null, null, null);
            if(cursor.getCount() > 0) return "";
            RechargeModel rechargeModel;
            rechargeModel = rechargeModelParamObj;
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_MOBILE_NO, rechargeModel.mobileNo);
            contentValues.put(FIELD_TYPE, rechargeModel.type);
            contentValues.put(FIELD_SERVICE_PROVIDER, rechargeModel.serviceProvider);
            try{
                IScoreDatabase.getInstance().insertHandleExc(RECHARGE_BILL_TABLE,null, contentValues);
            }catch (Exception s){
                if(IScoreApplication.DEBUG) Log.e("unique", s.toString());
            }
            cursor.close();
        }catch (SQLiteConstraintException e){
            if(IScoreApplication.DEBUG) Log.e("RechargeModelExc", e.toString());

        }
        return "";
    }


    public ArrayList<String> getPhoneDthNumber(int type){
        String temp;
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = IScoreDatabase.getInstance().query(RECHARGE_BILL_TABLE, null, " "+
                FIELD_TYPE+" = "+type ,null, null, null, null);
        while (cursor.moveToNext()){
            temp = cursor.getString(cursor.getColumnIndex(FIELD_MOBILE_NO));
            if(!arrayList.contains(temp))
                arrayList.add(temp);
        }
        cursor.close();
        return arrayList;
    }
    public String getServiceProvider(RechargeModel mobDthNo){
        String result = "";
        Cursor cursor = null;
        try{
            cursor = IScoreDatabase.getInstance().query(RECHARGE_BILL_TABLE, null, " "+
                    FIELD_MOBILE_NO +" = "+mobDthNo.mobileNo , null, null,null,null);
            if(cursor.getCount() > 0)
                cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(FIELD_SERVICE_PROVIDER));
        }catch (Exception e){
            result = "ex";
            if(IScoreApplication.DEBUG) Log.e("GetServiceProvider", e.toString());
        }
        cursor.close();
        return  result;
    }
}
