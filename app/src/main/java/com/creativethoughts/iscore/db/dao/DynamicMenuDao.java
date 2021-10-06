package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.DynamicMenuDetails;
import com.creativethoughts.iscore.gsonmodel.SyncDMenu;

import java.util.Map;


/**
 * Created by vishnu on 12/13/2017 - 10:05 AM.
 */

public class DynamicMenuDao {

    private static final String DYNAMIC_MENU_TABLE = "dynamic_menu";
    private static final String FIELD_RECHARGE = "recharge";
    private static final String FIELD_KSEB      = "kseb";
    private static final String FIELD_IMPS      = "imps";
    private static final String FIELD_RTGS      = "rtgs";
    //private static final String FIELD_OWN_IMPS  = "own_imps";


    public static final String QUERY_CREATE_DYANAMIC_MENU_TABLE = " CREATE TABLE IF NOT EXISTS "+ DYNAMIC_MENU_TABLE + " ( "+
            FIELD_IMPS+" VARCHAR(100), "+
           /* FIELD_OWN_IMPS +" VARCHAR(100), "+*/
            FIELD_RTGS+" VARCHAR(100), "+
            FIELD_KSEB+ " VARCHAR(100), "+
            FIELD_RECHARGE +" VARCHAR(100) "+ ")";
    /*public static final String ADD_OWN_IMPS= "ALTER TABLE"+ DYNAMIC_MENU_TABLE +" ADD COLUMN "+ FIELD_OWN_IMPS +" VARCHAR(100) ";*/
    private static final String[] COLUMNS = {FIELD_IMPS/*, FIELD_OWN_IMPS*/, FIELD_RTGS, FIELD_KSEB, FIELD_RECHARGE };
    private static DynamicMenuDao dynamicMenuDao;

    public static synchronized DynamicMenuDao getInstance(){
        if ( dynamicMenuDao == null ){
            dynamicMenuDao = new DynamicMenuDao();
        }
        return dynamicMenuDao;
    }

    public boolean insertValue(DynamicMenuDetails dynamicMenuDetails){
        boolean result;

        String recharge, imps, rtgs, kseb, neft ;
        try {
            
            recharge = Boolean.valueOf( dynamicMenuDetails.getRecharge()) ? "1":"0";
            imps = Boolean.valueOf( dynamicMenuDetails.getImps()) ? "1":"0";
            rtgs = Boolean.valueOf(dynamicMenuDetails.getRtgs() ) ? "1":"0";
            kseb = Boolean.valueOf( dynamicMenuDetails.getKseb() ) ? "1":"0";
            neft = Boolean.valueOf( dynamicMenuDetails.getNeft() ) ? "1":"0";
            String impsNeftRtgs = neft+rtgs;

            ContentValues contentValues = new ContentValues();

            contentValues.put( FIELD_KSEB, IScoreApplication.encryptStart(kseb) );
            contentValues.put( FIELD_RECHARGE, IScoreApplication.encryptStart(recharge));
            contentValues.put( FIELD_IMPS, IScoreApplication.encryptStart(imps));
            contentValues.put( FIELD_RTGS, IScoreApplication.encryptStart(impsNeftRtgs));
            long insertCount = IScoreDatabase.getInstance().insert( DYNAMIC_MENU_TABLE, null, contentValues );
            result = insertCount > 0;
        }catch ( Exception e ){
            result = false;
        }
        return result;
    }
    public boolean insertGsonValue(SyncDMenu sy){
        boolean result;

        String recharge, imps, rtgs, kseb, neft, ownImps ;
        try {

            recharge = Boolean.valueOf( sy.getRecharge()) ? "1":"0";
            imps    = Boolean.valueOf( sy.getImps()) ? "1":"0";
            rtgs    = Boolean.valueOf(sy.getRtgs() ) ? "1":"0";
            kseb    = Boolean.valueOf( sy.getKseb() ) ? "1":"0";
            neft    = Boolean.valueOf( sy.getNeft() ) ? "1":"0";
            ownImps = Boolean.valueOf( sy.getOwnImps())? "1":"0";
            String impsNeftRtgs = neft+rtgs+ownImps;

            ContentValues contentValues = new ContentValues();

            contentValues.put( FIELD_KSEB, IScoreApplication.encryptStart(kseb) );
            contentValues.put( FIELD_RECHARGE, IScoreApplication.encryptStart(recharge));
            contentValues.put( FIELD_IMPS, IScoreApplication.encryptStart(imps));
            contentValues.put( FIELD_RTGS, IScoreApplication.encryptStart(impsNeftRtgs));
            /*contentValues.put( FIELD_OWN_IMPS, IScoreApplication.encryptStart( ownImps ));*/
            long insertCount = IScoreDatabase.getInstance().insert( DYNAMIC_MENU_TABLE, null, contentValues );
            result = insertCount > 0;
        }catch ( Exception e ){
            result = false;
        }
        return result;
    }
    public DynamicMenuDetails getMenuDetails(){

        DynamicMenuDetails dynamicMenuDetails = new DynamicMenuDetails();
        try{
        //DynamicMenuDetails dynamicMenuDetails = new DynamicMenuDetails();
            Cursor cursor;
            String[] columns = { FIELD_RTGS, FIELD_KSEB, FIELD_IMPS, FIELD_RECHARGE/*, FIELD_OWN_IMPS*/};
            cursor = IScoreDatabase.getInstance().query(DYNAMIC_MENU_TABLE, columns, null, null, null, null, null);
            while ( cursor.moveToNext() ){
                cursor.moveToLast();
                dynamicMenuDetails.setRecharge( cursor.getString( cursor.getColumnIndex(FIELD_RECHARGE) ) );
                dynamicMenuDetails.setImps( cursor.getString( cursor.getColumnIndex(FIELD_IMPS) ) );
                dynamicMenuDetails.setRtgs( cursor.getString( cursor.getColumnIndex(FIELD_RTGS ) ) );
                dynamicMenuDetails.setKseb( cursor.getString( cursor.getColumnIndex(FIELD_KSEB) ) );
                /*dynamicMenuDetails.setOwnImps( cursor.getString( cursor.getColumnIndex( FIELD_OWN_IMPS ) ) );*/
            }
            cursor.close();
            return  dynamicMenuDetails;

        }catch ( Exception e){
            return null;
        }
       /* Map<String,String> menuMap = IScoreDatabase.getInstance().getSingleRow( DYNAMIC_MENU_TABLE, COLUMNS,
                null, null, null, null, null, false );
        if ( !menuMap.isEmpty() ){
            dynamicMenuDetails = new DynamicMenuDetails();
            dynamicMenuDetails.setRecharge( menuMap.get(FIELD_RECHARGE));
            dynamicMenuDetails.setImps( menuMap.get(FIELD_IMPS));
            dynamicMenuDetails.setRtgs(  menuMap.get(FIELD_RTGS ) );
            dynamicMenuDetails.setKseb(  menuMap.get(FIELD_KSEB ) );
            return dynamicMenuDetails;
        }*/

    }
    public void deleteAll(){
        IScoreDatabase.getInstance().delete(DYNAMIC_MENU_TABLE, null, null);
    }
}
