package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.Transaction;
import com.creativethoughts.iscore.gsonmodel.SyncTransactions;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.ArrayList;

/**
 * Created by muthukrishnan on 03/10/15. <BR>
 * TODO: Should rename the class later version, current version is 1.
 */
public class NewTransactionDAO {
    private static final String TR_TABLE = "PB_TRANSACTIONS_v2";

    private static final String FIELD_TRANS_ID = "_id";
    private static final String FIELD_TRANSACTION_ID = "transaction_id";
    private static final String FIELD_ACNO = "acno";
    private static final String FIELD_DEMAND_DEPOSIT = "fk_DemandDeposit";
    private static final String FIELD_EFFECT_FULL_DATE = "effectDate";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_CHEQUE_NO = "chequNo";
    private static final String FIELD_CHEQUE_DATE = "chequeDate";
    private static final String FIELD_NARRATION = "narration";
    private static final String FIELD_TRANS_TYPE = "transType";
    private static final String FIELD_REMARKS = "remarks";
    private static final String FIELD_IS_NEW = "is_new";
    private static final  String QUERY_TR_TABLE = "CREATE TABLE IF NOT EXISTS " + TR_TABLE + " (" +
            FIELD_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_TRANSACTION_ID + " BIGINT," +
            FIELD_ACNO + " varchar(50)," +
            FIELD_DEMAND_DEPOSIT + " BIGINT," +
            FIELD_EFFECT_FULL_DATE + " varchar(50)," +
            FIELD_AMOUNT + " varchar(50)," +
            FIELD_CHEQUE_NO + " varchar(45)," +
            FIELD_CHEQUE_DATE + " varchar(50)," +
            FIELD_NARRATION + " varchar(50)," +
            FIELD_TRANS_TYPE + " varchar(50)," +
            FIELD_REMARKS + " varchar(50)," +
            FIELD_IS_NEW + " INTEGER)";
    private static NewTransactionDAO mInstance;

    public static String getCreateTableString() {
        return QUERY_TR_TABLE;
    }

    public static NewTransactionDAO getInstance() {
        if (mInstance == null) {
            synchronized (NewTransactionDAO.class) {
                if (mInstance == null) {
                    mInstance = new NewTransactionDAO();
                }
            }
        }

        return mInstance;
    }

    /**
     * To insert in a transaction table
     *
     * @param transaction
     */
    public void insertTransaction(Transaction transaction) {

        // No need to insert if already exits.
        if (transaction == null || isTransactionAlreadyExists(transaction.transactionNo)) {
            return;
        }

        ContentValues transaction_insert = new ContentValues();

        transaction_insert.put(FIELD_TRANSACTION_ID, transaction.transactionNo);
        transaction_insert.put(FIELD_DEMAND_DEPOSIT, transaction.demandDepositNo);
        transaction_insert.put(FIELD_ACNO, transaction.accNo);
        transaction_insert.put(FIELD_EFFECT_FULL_DATE, transaction.effectDate);
        transaction_insert.put(FIELD_AMOUNT, transaction.amount);
        transaction_insert.put(FIELD_CHEQUE_NO, transaction.chequeNo);
        transaction_insert.put(FIELD_CHEQUE_DATE, transaction.chequeDate);
        transaction_insert.put(FIELD_NARRATION, transaction.narration);
        transaction_insert.put(FIELD_TRANS_TYPE, transaction.transType);
        transaction_insert.put(FIELD_REMARKS, transaction.remarks);
        transaction_insert.put(FIELD_IS_NEW, 0);

        IScoreDatabase.getInstance().insert(TR_TABLE, null, transaction_insert);
    }
    public void insertGsonTransaction(SyncTransactions syncTransactions, String accNo) {

        // No need to insert if already exits.
        if (syncTransactions == null || isTransactionAlreadyExists(syncTransactions.getTransaction())) {
            return;
        }

        ContentValues transaction_insert = new ContentValues();

        transaction_insert.put(FIELD_TRANSACTION_ID, syncTransactions.getTransaction());
        transaction_insert.put(FIELD_DEMAND_DEPOSIT, syncTransactions.getFkDemandDeposit() );
        transaction_insert.put(FIELD_ACNO, accNo);
        transaction_insert.put(FIELD_EFFECT_FULL_DATE, syncTransactions.getEffectDate() );
        transaction_insert.put(FIELD_AMOUNT, syncTransactions.getAmount() );
        transaction_insert.put(FIELD_CHEQUE_NO, syncTransactions.getChequeNo() );
        transaction_insert.put(FIELD_CHEQUE_DATE, syncTransactions.getChequeDate());
        transaction_insert.put(FIELD_NARRATION, syncTransactions.getNarration() );
        transaction_insert.put(FIELD_TRANS_TYPE, syncTransactions.getTransType() );
        transaction_insert.put(FIELD_REMARKS, syncTransactions.getRemarks() );
        transaction_insert.put(FIELD_IS_NEW, 0);

        IScoreDatabase.getInstance().insert(TR_TABLE, null, transaction_insert);


    }

    public void removeOldTransaction() {
        Cursor cursor = IScoreDatabase.getInstance().getDatabase()
                .query(true, TR_TABLE, null, null, null, null, null,
                        FIELD_EFFECT_FULL_DATE + " DESC", "");

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        final int days;

        if (settingsModel == null || settingsModel.days <= 0) {
            days = 30;
        } else {
            days = settingsModel.days;
        }

        ArrayList<Transaction> transactions = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                Transaction transaction = new Transaction();
                transaction.transactionId = cursor.getString(cursor.getColumnIndex(FIELD_TRANSACTION_ID));
                transaction.effectDate = cursor.getString(cursor.getColumnIndex(FIELD_EFFECT_FULL_DATE));


                long diffMillis = CommonUtilities.getTimeDifferenceFromNow(transaction.effectDate);

                int daysDiff = (int) (diffMillis / (1000 * 60 * 60 * 24));

                if (daysDiff > days) {
                    transactions.add(transaction);
                }
            }
        }

        if(cursor != null) {
            cursor.close();
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);

            if (transaction == null) {
                continue;
            }

            deleteTransaction(transaction.transactionId);
        }
    }

    public void markAllTransactionAsRead(String accountNo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_IS_NEW, 1);

        String where = FIELD_ACNO + " = ?";
        String[] whereArgs = {accountNo};

        IScoreDatabase.getInstance().update(TR_TABLE, contentValues, where, whereArgs);
    }

    /**
     * To check whether the transaction is already exits or not by transaction id.
     *
     * @param transactionNo
     * @return true -> if already exits, false otherwise.
     */
    private boolean isTransactionAlreadyExists(String transactionNo) {
        Cursor cursor = null;

        try {
            String where = FIELD_TRANSACTION_ID + " = ?";
            String[] whereArgs = {transactionNo};

            cursor = IScoreDatabase.getInstance()
                    .query(TR_TABLE, null, where, whereArgs, null, null, null);

            return cursor != null && cursor.getCount() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }


    /**
     * To get all the transaction list for the account number <BR>
     * Note : sort based on effective date of the transaction
     *
     * @param accountNo
     * @return list of {@link Transaction}
     */
    public ArrayList<Transaction> getTransactions(String accountNo) {

        ArrayList<Transaction> transactions = new ArrayList<>();
        Cursor cursor = null;

        try {
            String where = FIELD_ACNO + " = ?";
            String[] whereArgs = {accountNo};
            cursor = IScoreDatabase.getInstance().getDatabase()
                    .query(true, TR_TABLE, null, where, whereArgs, null, null,
                            FIELD_EFFECT_FULL_DATE + " DESC", "");

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    Transaction transaction = new Transaction();
                    transaction.accNo = cursor.getString(cursor.getColumnIndex(FIELD_ACNO));
                    transaction.amount = cursor.getString(cursor.getColumnIndex(FIELD_AMOUNT));
                    transaction.narration = cursor.getString(cursor.getColumnIndex(FIELD_NARRATION));
                    transaction.transType = cursor.getString(cursor.getColumnIndex(FIELD_TRANS_TYPE));
                    transaction.chequeNo = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_NO));
                    transaction.chequeDate = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_DATE));
                    transaction.remarks = cursor.getString(cursor.getColumnIndex(FIELD_REMARKS));
                    transaction.effectDate = cursor.getString(cursor.getColumnIndex(FIELD_EFFECT_FULL_DATE));
                    transaction.isNew = cursor.getInt(cursor.getColumnIndex(FIELD_IS_NEW)) == 0;

                    transactions.add(transaction);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return transactions;
    }



    public ArrayList<Transaction> getTransactions1(String accountNo) {

        ArrayList<Transaction> transactions = new ArrayList<>();
        Cursor cursor = null;

        try {
            String where = FIELD_ACNO + " = ?";
            String[] whereArgs = {accountNo};
            cursor = IScoreDatabase.getInstance().getDatabase()
                    .query(true, TR_TABLE, null, where, whereArgs, null, null,
                            FIELD_EFFECT_FULL_DATE + " DESC", "");

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    Transaction transaction = new Transaction();
                    transaction.accNo = cursor.getString(cursor.getColumnIndex(FIELD_ACNO));
                    transaction.amount = cursor.getString(cursor.getColumnIndex(FIELD_AMOUNT));
                    transaction.narration = cursor.getString(cursor.getColumnIndex(FIELD_NARRATION));
                    transaction.transType = cursor.getString(cursor.getColumnIndex(FIELD_TRANS_TYPE));
                    transaction.chequeNo = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_NO));
                    transaction.chequeDate = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_DATE));
                    transaction.remarks = cursor.getString(cursor.getColumnIndex(FIELD_REMARKS));
                    transaction.effectDate = cursor.getString(cursor.getColumnIndex(FIELD_EFFECT_FULL_DATE));
                    transaction.isNew = cursor.getInt(cursor.getColumnIndex(FIELD_IS_NEW)) == 0;

                    transactions.add(transaction);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return transactions;
    }

    public ArrayList<Transaction> getTransactions2(String accountNo) {

        ArrayList<Transaction> transactions = new ArrayList<>();
        Cursor cursor = null;

        try {
            String where = FIELD_ACNO + " = ?";
            String[] whereArgs = {accountNo};
            cursor = IScoreDatabase.getInstance().getDatabase()
                    .query(true, TR_TABLE, null, where, whereArgs, null, null,
                            FIELD_EFFECT_FULL_DATE + " DESC", "10");

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    Transaction transaction = new Transaction();
                    transaction.accNo = cursor.getString(cursor.getColumnIndex(FIELD_ACNO));
                    transaction.amount = cursor.getString(cursor.getColumnIndex(FIELD_AMOUNT));
                    transaction.narration = cursor.getString(cursor.getColumnIndex(FIELD_NARRATION));
                    transaction.transType = cursor.getString(cursor.getColumnIndex(FIELD_TRANS_TYPE));
                    transaction.chequeNo = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_NO));
                    transaction.chequeDate = cursor.getString(cursor.getColumnIndex(FIELD_CHEQUE_DATE));
                    transaction.remarks = cursor.getString(cursor.getColumnIndex(FIELD_REMARKS));
                    transaction.effectDate = cursor.getString(cursor.getColumnIndex(FIELD_EFFECT_FULL_DATE));
                    transaction.isNew = cursor.getInt(cursor.getColumnIndex(FIELD_IS_NEW)) == 0;

                    transactions.add(transaction);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return transactions;
    }


    private void deleteTransaction(String transactionId) {
        String where = FIELD_TRANSACTION_ID + " = ?";
        String[] whereArgs = {transactionId};

        IScoreDatabase.getInstance().delete(TR_TABLE, where, whereArgs);
    }

    /**
     * To delete all the transactions.
     */
    public void deleteAllRow() {
        IScoreDatabase.getInstance().delete(TR_TABLE, null, null);
    }


}
