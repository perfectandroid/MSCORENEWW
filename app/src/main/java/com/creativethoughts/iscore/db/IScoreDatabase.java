package com.creativethoughts.iscore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.creativethoughts.iscore.IScoreApplication;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muthukrishnan on 22/09/15.
 */
public class IScoreDatabase {

    private static final String DB_FILENAME = "mScoreV2.db";
    private static final int DATABASE_VERSION =/* 4*/ /*5*//*7*/7;
    private static volatile IScoreDatabase mInstance;
    private SQLiteDatabase mDatabase;
    private static class DatabaseWrapper extends SQLiteOpenHelper {
        private DatabaseWrapper(Context context) {
            super(context, DB_FILENAME, null, DATABASE_VERSION);
            SQLiteDatabase.loadLibs(context);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

           // db.execSQL( UserCredentialDAO.getCreateTableString() );
//            db.execSQL( SettingsDAO.getCreateTableString() );
//            db.execSQL( NewTransactionDAO.getCreateTableString() );
//            db.execSQL( UserDetailsDAO.getCreateTableString() );
//            db.execSQL( PBAccountInfoDAO.getCreateTableString() );
//            db.execSQL( PBMessagesDAO.getCreateTableString() );
//            db.execSQL( KsebBillDAO.getCreateTableString() );
//            db.execSQL( RechargeDAO.getCreateTableString() );
          //  db.execSQL( DynamicMenuDao.QUERY_CREATE_DYANAMIC_MENU_TABLE );
          //  db.execSQL( BankVerifier.QUERY_CREATE_BANK_VERIFY );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            db.execSQL( KsebBillDAO.getCreateTableString() );
//            db.execSQL( RechargeDAO.getCreateTableString() );
           // db.execSQL( UserDetailsDAO.alterTableQuery );
          //  db.execSQL( DynamicMenuDao.QUERY_CREATE_DYANAMIC_MENU_TABLE );
         //  db.execSQL(BankVerifier.QUERY_CREATE_BANK_VERIFY);

            for (int i = oldVersion+1;i <= newVersion; i++){

                switch (i){
                    case 2:
                     //   db.execSQL(UserCredentialDAO.getUpgradeCredentialTable());
                        break;
                    /*case 8:
                        db.execSQL( DynamicMenuDao.ADD_OWN_IMPS );*/
                    default:
                        break;
                }
            }

            //logout if old version is one
            if (oldVersion==1){
                IScoreApplication.logoutNow();

            }
        }
    }
    private IScoreDatabase(Context context) {
        if (mDatabase == null) {
            try {
                mDatabase = new DatabaseWrapper(context).getWritableDatabase
                        (IScoreApplication.decryptStart("u9//ZTytrZY="));
            } catch (Exception e) {
                if (IScoreApplication.DEBUG) Log.e("Exception",e.toString()+"");
            }
        }
    }

    /**
     * Initializes database singleton object while application starts from Application class.
     *
     * @param application the application object
     */
    public static void initDataBase(Context application) {
        if (mInstance == null) {
            mInstance = new IScoreDatabase(application);
        }
    }

    public static IScoreDatabase getInstance() {
        if (mInstance == null) {
            throw new RuntimeException(
                    "Must run initDataBase(Application application) before an instance can be obtained");
        }
        return mInstance;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param table          the table to insert the row into.
     * @param nullColumnHack optional; may be null. SQL doesn't allow inserting a completely empty
     *                       row without naming at least one column name. If your provided values is empty, no
     *                       column names are known and an empty row can't be inserted. If not set to null, the
     *                       nullColumnHack parameter provides the name of nullable column name to explicitly
     *                       insert a NULL into in the case where your values is empty.
     * @param values         this map contains the initial column values for the row. The keys should be the
     *                       column names and the values the column values.
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
     public long insert(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.insert(table, null, values);
    }

    public long insertHandleExc(String table, String nullColumnHack, ContentValues contentValues){
        long result = 0;
        try{
            result = mDatabase.insertOrThrow(table, null, contentValues);
        }catch (Exception e){
            if(IScoreApplication.DEBUG) Log.e("unique error", e.toString());
        }
        return result;
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table       the table to update in.
     * @param values      a map from column names to new column values. null is a valid value that will
     *                    be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating. Passing null will update
     *                    all rows.
     * @param whereArgs
     * @return the number of rows affected
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        return mDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will return all columns, which
     *                      is discouraged to prevent reading data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL WHERE clause
     *                      (excluding the WHERE itself). Passing null will return all rows for the given
     *                      table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *                      from selectionArgs, in order that they appear in the selection. The values will be
     *                      bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL GROUP BY clause
     *                      (excluding the GROUP BY itself). Passing null will cause the rows to not be
     *                      grouped.
     * @param having        A filter declare which row groups to include in the cursor, if row grouping is
     *                      being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     *                      Passing null will cause all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause (excluding the
     *                      ORDER BY itself ). Passing null will use the default sort order, which may be
     *                      unordered.
     * @return A Cursor object, which is positioned before the first entry. Note that Cursors are
     * not synchronized, see the documentation for more details.
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
    public Map<String,String> getSingleRow( String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                                           String orderBy, boolean first ) {
        Cursor cursor =  mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        Map<String,String> map = new HashMap<>();
        if ( cursor != null ){
            try{
                if ( cursor.getCount() > 0 ){
                    if ( first )
                        cursor.moveToFirst();
                    else
                        cursor.moveToLast();
                    for (String column : columns ) {
                        map.put( column, cursor.getString( cursor.getColumnIndex( column ) ) );
                    }
                }
                cursor.close();
            }catch ( Exception e ){
                return new HashMap<>();
            }
        }
        return map;
    }

    public List<Map<String,String>> getMultipleRow(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                                                   String orderBy) {

        List<Map<String,String>> list = new ArrayList<>();
        Cursor cursor =  mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if ( cursor != null && cursor.getCount() > 0){
            while ( cursor.moveToNext() ){
                Map<String,String> map = new HashMap<>();
                for ( String column: columns ) {
                    map.put( column, cursor.getString( cursor.getColumnIndex( column ) ) );
                }
                list.add( map );
            }
            cursor.close();
        }
        return list;
    }


    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will return all columns, which
     *                      is discouraged to prevent reading data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL WHERE clause
     *                      (excluding the WHERE itself). Passing null will return all rows for the given
     *                      table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *                      from selectionArgs, in order that they appear in the selection. The values will be
     *                      bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL GROUP BY clause
     *                      (excluding the GROUP BY itself). Passing null will cause the rows to not be
     *                      grouped.
     * @param having        A filter declare which row groups to include in the cursor, if row grouping is
     *                      being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     *                      Passing null will cause all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause (excluding the
     *                      ORDER BY itself). Passing null will use the default sort order, which may be
     *                      unordered.
     * @param limit         Total number of item need to fetch from this query.
     * @return A Cursor object, which is positioned before the first entry. Note that Cursors are
     * not synchronized, see the documentation for more details.
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mDatabase
                .query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }



}
