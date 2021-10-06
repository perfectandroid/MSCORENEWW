package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.gsonmodel.SyncAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by muthukrishnan on 24/09/15.
 */
public class PBAccountInfoDAO {
    private static final String USER_AC_INFO = "PB_ACCOUNTINFO";

    private static final String FIELD_PB_AC_ID = "PB_AC_ID";
    private static final String FIELD_AC_NO = "acno";
    private static final String FIELD_MODULE = "module";
    private static final String FIELD_AC_TYPE = "acType";
    private static final String FIELD_TYPE_SHORT = "typeShort";
    private static final String FIELD_BRANCH_CODE = "branchCode";
    private static final String FIELD_BRANCH_NAME = "branchName";
    private static final String FIELD_BRANCH_SHORT = "branchShort";
    private static final String FIELD_DEPOSIT_DATE = "depositDate";
    private static final String FIELD_OPPMODE = "oppmode";
    private static final String FIELD_FK_CUSTOMER_ID = "fk_customerID";
    private static final String FIELD_AVAILABLE_BAL = "availableBal";
    private static final String FIELD_UNCLR_BAL = "unClrBal";
    private static final String FIELD_LAST_AC_TIMESTAMP = "lastactimestamp";
    private static final String FIELD_FK_DEMAND_DEPOSIT = "fk_pbdemandDeposit";
    private static final String FIELD_LAST_AC_DATE = "lastacDate";
    private static final String QUERY_AC_INFO =
            "CREATE TABLE IF NOT EXISTS " + USER_AC_INFO + " (" + FIELD_PB_AC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_AC_NO + " varchar(50)," +
                    FIELD_MODULE + " varchar(45)," +
                    FIELD_AC_TYPE + " varchar(75)," +
                    FIELD_TYPE_SHORT + " varchar(20)," +
                    FIELD_BRANCH_CODE + " varchar(10)," +
                    FIELD_BRANCH_NAME + " varchar(100)," +
                    FIELD_BRANCH_SHORT + " varchar(25)," +
                    FIELD_DEPOSIT_DATE + " varchar(25)," +
                    FIELD_OPPMODE + " varchar(25)," +
                    FIELD_FK_CUSTOMER_ID + " varchar(25)," +
                    FIELD_AVAILABLE_BAL + " varchar(25)," +
                    FIELD_UNCLR_BAL + " varchar(25)," +
                    FIELD_LAST_AC_TIMESTAMP + " date," +
                    FIELD_FK_DEMAND_DEPOSIT + " varchar(50)," +
                    FIELD_LAST_AC_DATE + " varchar(50))";

    private static PBAccountInfoDAO mInstance;
    private static final String[] COLUMNS = { FIELD_PB_AC_ID, FIELD_AC_NO, FIELD_MODULE, FIELD_AC_TYPE, FIELD_TYPE_SHORT, FIELD_BRANCH_CODE,
    FIELD_BRANCH_NAME, FIELD_BRANCH_SHORT, FIELD_DEPOSIT_DATE, FIELD_OPPMODE, FIELD_FK_CUSTOMER_ID, FIELD_AVAILABLE_BAL, FIELD_UNCLR_BAL,
    FIELD_LAST_AC_TIMESTAMP, FIELD_FK_DEMAND_DEPOSIT, FIELD_LAST_AC_DATE };

    public static String getCreateTableString() {
        return QUERY_AC_INFO;
    }

    public static synchronized PBAccountInfoDAO getInstance() {
        if (mInstance == null) {
            mInstance = new PBAccountInfoDAO();
        }
        return mInstance;
    }


// --Commented out by Inspection START (04-08-2018 10:28):
//    public void insertAccountInfo(AccountInfo accountInfo) {
//
//        if (isAccountNoExits(accountInfo.accountAcno)) {
//
//            updateAccountInfo(accountInfo);
//            return;
//        }
//
//        ContentValues accountInsert = new ContentValues();
//
//        accountInsert.put(FIELD_AC_NO, accountInfo.accountAcno);
//        accountInsert.put(FIELD_MODULE, accountInfo.accountModule);
//        accountInsert.put(FIELD_AC_TYPE, accountInfo.accountAcType);
//        accountInsert.put(FIELD_TYPE_SHORT, accountInfo.accountTypeShort);
//        accountInsert.put(FIELD_BRANCH_CODE, accountInfo.accountBranchCode);
//        accountInsert.put(FIELD_BRANCH_NAME, accountInfo.accountBranchName);
//        accountInsert.put(FIELD_BRANCH_SHORT, accountInfo.accountBranchShort);
//        accountInsert.put(FIELD_DEPOSIT_DATE, accountInfo.accountDepositDate);
//        accountInsert.put(FIELD_OPPMODE, accountInfo.accountOppMode);
//        accountInsert.put(FIELD_FK_DEMAND_DEPOSIT, accountInfo.fkDemandDepositID);
//        accountInsert.put(FIELD_LAST_AC_DATE, accountInfo.userLastAcessDate);
//        accountInsert.put(FIELD_FK_CUSTOMER_ID, accountInfo.accountCustomerID);
//        accountInsert.put(FIELD_LAST_AC_TIMESTAMP, accountInfo.lastTimeStamp);
//
//        accountInsert.put(FIELD_AVAILABLE_BAL, accountInfo.availableBal);
//        accountInsert.put(FIELD_UNCLR_BAL, accountInfo.unClrBal);
//
//
//        IScoreDatabase.getInstance().insert(USER_AC_INFO, null, accountInsert);
//    }
// --Commented out by Inspection STOP (04-08-2018 10:28)
    public void insertGsonAccountInfo(SyncAccount syncAccount) {

        if (isAccountNoExits(syncAccount.getAccNo() )) {

            updateGsonAccountInfo(syncAccount);
            return;
        }

        ContentValues accountInsert = new ContentValues();

        accountInsert.put(FIELD_AC_NO, syncAccount.getAccNo() );
        accountInsert.put(FIELD_MODULE, syncAccount.getModule() );
        accountInsert.put(FIELD_AC_TYPE, syncAccount.getAcType() );
        accountInsert.put(FIELD_TYPE_SHORT, syncAccount.getTypeShort() );
        accountInsert.put(FIELD_BRANCH_CODE, syncAccount.getBranchCode() );
        accountInsert.put(FIELD_BRANCH_NAME, syncAccount.getBranchName() );
        accountInsert.put(FIELD_BRANCH_SHORT, syncAccount.getBranchShort() );
        accountInsert.put(FIELD_DEPOSIT_DATE, syncAccount.getDepositDate() );
        accountInsert.put(FIELD_OPPMODE, syncAccount.getOppMode() );
        accountInsert.put(FIELD_FK_DEMAND_DEPOSIT, syncAccount.getDemandDepositId() );
        accountInsert.put(FIELD_LAST_AC_DATE, syncAccount.getLastAccessDate() );
        accountInsert.put(FIELD_FK_CUSTOMER_ID, syncAccount.getFkCustomerId() );
        accountInsert.put(FIELD_LAST_AC_TIMESTAMP, syncAccount.getLastAccessDate() );

        accountInsert.put(FIELD_AVAILABLE_BAL, syncAccount.getAvailableBal() );
        accountInsert.put(FIELD_UNCLR_BAL, syncAccount.getUnClrBal() );


        IScoreDatabase.getInstance().insert(USER_AC_INFO, null, accountInsert);
    }

    private void updateGsonAccountInfo( SyncAccount syncAccount) {
        String where = FIELD_AC_NO + " = ?";
        String[] whereArgs = {syncAccount.getAccNo()};

        ContentValues contentValues = new ContentValues();


        contentValues.put(FIELD_DEPOSIT_DATE, syncAccount.getDepositDate() );
        contentValues.put(FIELD_OPPMODE, syncAccount.getOppMode() );
        contentValues.put(FIELD_FK_DEMAND_DEPOSIT, syncAccount.getDemandDepositId() );
        contentValues.put(FIELD_LAST_AC_DATE, syncAccount.getLastAccessDate() );
        contentValues.put(FIELD_FK_CUSTOMER_ID, syncAccount.getFkCustomerId() );
        contentValues.put(FIELD_LAST_AC_TIMESTAMP, syncAccount.getLastAccessDate() );

        contentValues.put(FIELD_AVAILABLE_BAL, syncAccount.getAvailableBal() );
        contentValues.put(FIELD_UNCLR_BAL, syncAccount.getUnClrBal());

        IScoreDatabase.getInstance().update(USER_AC_INFO, contentValues, where, whereArgs);
    }
    private void updateAccountInfo(AccountInfo accountInfo) {
        String where = FIELD_AC_NO + " = ?";
        String[] whereArgs = {accountInfo.accountAcno};

        ContentValues contentValues = new ContentValues();


        contentValues.put(FIELD_DEPOSIT_DATE, accountInfo.accountDepositDate);
        contentValues.put(FIELD_OPPMODE, accountInfo.accountOppMode);
        contentValues.put(FIELD_FK_DEMAND_DEPOSIT, accountInfo.fkDemandDepositID);
        contentValues.put(FIELD_LAST_AC_DATE, accountInfo.userLastAcessDate);
        contentValues.put(FIELD_FK_CUSTOMER_ID, accountInfo.accountCustomerID);
        contentValues.put(FIELD_LAST_AC_TIMESTAMP, accountInfo.lastTimeStamp);

        contentValues.put(FIELD_AVAILABLE_BAL, accountInfo.availableBal);
        contentValues.put(FIELD_UNCLR_BAL, accountInfo.unClrBal);

        IScoreDatabase.getInstance().update(USER_AC_INFO, contentValues, where, whereArgs);
    }

    /**
     * To check whether the account info already exists or not.
     *
     * @return
     */
    public boolean isAccountInfoExits() {
        Cursor cursor = null;

        try {
            cursor = IScoreDatabase.getInstance()
                    .query(USER_AC_INFO, null, null, null, null, null, null);

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
        /*Map<String,String> userAcc = IScoreDatabase.getInstance().getSingleRow( USER_AC_INFO, COLUMNS, null, null, null, null
                , null, true);
        return !userAcc.isEmpty();*/
    }

    private boolean isAccountNoExits(String accountNo) {
        /*Cursor cursor = null;

        try {
            String where = FIELD_AC_NO + " = ?";
            String[] whereArgs = {accountNo};

            cursor = IScoreDatabase.getInstance()
                    .query(USER_AC_INFO, null, where, whereArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;*/
        String where = FIELD_AC_NO + " = ?";
        String[] whereArgs = {accountNo};
        Map<String,String> userAcc = IScoreDatabase.getInstance().getSingleRow( USER_AC_INFO, COLUMNS,
                where, whereArgs, null, null
                , null, true);
        return !userAcc.isEmpty();
    }

    /**
     * To get all the account info for the customer
     *
     * @return list of account numbers.
     */
    public List<String> getAccountNos() {
        List<String> accNoList = new ArrayList< >();

        /* List< Map<String,String> > dataList = IScoreDatabase.getInstance().getMultipleRow( USER_AC_INFO, COLUMNS, null, null, FIELD_AC_NO, null,
                null );
        for (Map<String, String> map : dataList ) {
            accNoList.add( map.get( FIELD_AC_NO) +" (" + map.get(FIELD_TYPE_SHORT) +")");
        }*/
        Cursor cursor = null;

        try {
            cursor = IScoreDatabase.getInstance()
                    .query(USER_AC_INFO, new String[]{FIELD_AC_NO,FIELD_TYPE_SHORT}, null, null, FIELD_AC_NO, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    String accountNo = cursor.getString(cursor.getColumnIndex(FIELD_AC_NO))+" ("+cursor.getString(cursor.getColumnIndex(FIELD_TYPE_SHORT))+")";

                    if (!TextUtils.isEmpty(accountNo)) {
                        accNoList.add(accountNo);
                    }
                }
            }



        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return accNoList;
    }


    public AccountInfo getAccountInfo(String accountNo) {

        AccountInfo accountInfo = new AccountInfo();
        Cursor cursor = null;

        try {
            String where = FIELD_AC_NO + " = ?";
            String[] whereArgs = {accountNo};
            cursor = IScoreDatabase.getInstance()
                    .query(USER_AC_INFO, null, where, whereArgs, null, null, FIELD_LAST_AC_DATE + " ASC");

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToLast();

                accountInfo.availableBal = cursor.getString(cursor.getColumnIndex(FIELD_AVAILABLE_BAL));
                accountInfo.unClrBal = cursor.getString(cursor.getColumnIndex(FIELD_UNCLR_BAL));
                accountInfo.accountAcType = cursor.getString(cursor.getColumnIndex(FIELD_AC_TYPE));
                accountInfo.accountTypeShort = cursor.getString(cursor.getColumnIndex(FIELD_TYPE_SHORT)).trim();
                accountInfo.userLastAcessDate = cursor.getString(cursor.getColumnIndex(FIELD_LAST_AC_DATE));
                accountInfo.fkDemandDepositID = cursor.getString(cursor.getColumnIndex(FIELD_FK_DEMAND_DEPOSIT));
                accountInfo.accountBranchCode = cursor.getString(cursor.getColumnIndex(FIELD_BRANCH_CODE));
            }

        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return accountInfo;
    }


    /**
     * To delete all account information.
     */
    public void deleteAllRows() {
        IScoreDatabase.getInstance().delete(USER_AC_INFO, null, null);
    }
}
