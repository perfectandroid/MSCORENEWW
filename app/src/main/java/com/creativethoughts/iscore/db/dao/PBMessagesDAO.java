package com.creativethoughts.iscore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.creativethoughts.iscore.db.IScoreDatabase;
import com.creativethoughts.iscore.db.dao.model.Message;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.utility.CommonUtilities;

import java.util.ArrayList;

/**
 * Created by muthukrishnan on 24/09/15.
 * <p/>
 * This table is used to store both messages and offers
 */
public class PBMessagesDAO {

    public static final String MESSAGE_TABLE = "PB_MESSAGES";

    private static final String FIELD_ID = "_id";
    private static final String FIELD_MSG_ID = "msg_id";
    private static final String FIELD_CUSTOMER_ID = "customer_id";
    private static final String FIELD_MESSAGE_HEAD = "message_head";
    private static final String FIELD_MESSAGE_DETAIL = "message_detail";
    private static final String FIELD_MESSAGE_DATE = "message_date";
    private static final String FIELD_MESSAGE_TYPE = "message_type";
    private static final String FIELD_MESSAGE_MODE = "message_mode";
    private static final String FIELD_MESSAGE_SEEN = "seen_status";
    private static final String QUERY_MESSAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE + " (" +
                    FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FIELD_MSG_ID + " INTEGER," +
                    FIELD_CUSTOMER_ID + " TEXT," +
                    FIELD_MESSAGE_HEAD + " VARCHAR," +
                    FIELD_MESSAGE_DETAIL + " VARCHAR," +
                    FIELD_MESSAGE_DATE + " VARCHAR," +
                    FIELD_MESSAGE_TYPE + " INTEGER," +
                    FIELD_MESSAGE_MODE + " INTEGER," +
                    FIELD_MESSAGE_SEEN + " INTEGER)";
    private static PBMessagesDAO mInstance;

    public static String getCreateTableString() {
        return QUERY_MESSAGE_TABLE;
    }

    public static PBMessagesDAO getInstance() {
        if (mInstance == null) {
            synchronized (PBMessagesDAO.class) {
                if (mInstance == null) {
                    mInstance = new PBMessagesDAO();
                }
            }
        }

        return mInstance;
    }

    /**
     * To insert the messages and offer in messages table.
     *
     * @param customerId
     * @param messageId
     * @param head
     * @param detail
     * @param date
     * @param type
     * @param mode
     */

    public void insertMessage(String customerId, int messageId, String head, String detail, String date, int type, int mode) {

        // check message before insert in to table.
        if (isMessageAlreadyExists(messageId)) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_MSG_ID, messageId);
        contentValues.put(FIELD_MESSAGE_HEAD, head);
        contentValues.put(FIELD_CUSTOMER_ID, customerId);
        contentValues.put(FIELD_MESSAGE_DETAIL, detail);
        contentValues.put(FIELD_MESSAGE_DATE, date);
        contentValues.put(FIELD_MESSAGE_TYPE, type);
        contentValues.put(FIELD_MESSAGE_MODE, mode);
        contentValues.put(FIELD_MESSAGE_SEEN, 0);

        IScoreDatabase.getInstance().insert(MESSAGE_TABLE, null, contentValues);
    }

    /**
     * To check whether the message if already exits or not before insert in message table.
     *
     * @param messageId
     * @return true > if already exits, false -> otherwise.
     */
    public boolean isMessageAlreadyExists(int messageId) {
        Cursor cursor = null;

        try {
            String where = FIELD_MSG_ID + " = ?";
            String[] whereArgs = {String.valueOf(messageId)};

            cursor = IScoreDatabase.getInstance()
                    .query(MESSAGE_TABLE, null, where, whereArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                return true;
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

    /**
     * To mark all the message as read.
     *
     * @param customerId
     */
    public void markMessageAsRead(String customerId) {
        markAsRead(customerId, 0);
    }

    /**
     * To mark all the message as read
     *
     * @param customerId
     */
    public void markOffersAsRead(String customerId) {
        markAsRead(customerId, 1);
    }

    private void markAsRead(String customerId, int mode) {
        ContentValues contentValues = new ContentValues();
        //        contentValues.put(FIELD_MESSAGE_TYPE, mode);
        contentValues.put(FIELD_MESSAGE_SEEN, 1);

        String where = FIELD_CUSTOMER_ID + " = ? AND " + FIELD_MESSAGE_TYPE + " = ?";
        String[] whereArgs = {customerId, String.valueOf(mode)};

        IScoreDatabase.getInstance().update(MESSAGE_TABLE, contentValues, where, whereArgs);
    }

    /**
     * To get all the message for the customer id.
     *
     * @param customerId
     * @return
     */
    public ArrayList<Message> getAllMessages(String customerId) {
        return getAllMessageForCustomerId(customerId, 0);
    }

    /**
     * To get all the offers for the customer id.
     *
     * @param customerId
     * @return
     */
    public ArrayList<Message> getAllOffers(String customerId) {
        return getAllMessageForCustomerId(customerId, 1);
    }

    /**
     * To get the unread message count for the customer id.
     *
     * @param customerId
     * @return
     */
    public int getMessageUnReadCount(String customerId) {
        return getUnreadCount(customerId, 0);
    }

    /**
     * To get the unread offers count for the customer id.
     *
     * @param customerId
     * @return
     */
    public int getOffersUnReadCount(String customerId) {
        return getUnreadCount(customerId, 1);
    }

    /**
     * To get the unread message or offer based on customer id and the mode.
     *
     * @param customerId
     * @param mode       mode == 1 -> offer; mode == 0 -> messages
     * @return
     */
    private int getUnreadCount(String customerId, int mode) {
        Cursor cursor = null;

        try {
            String where =
                    FIELD_CUSTOMER_ID + " = ? AND " + FIELD_MESSAGE_TYPE + " = ? AND " + FIELD_MESSAGE_SEEN + " = ?";
            String[] whereArgs = {customerId, String.valueOf(mode), String.valueOf(0)};

            cursor = IScoreDatabase.getInstance().getDatabase()
                    .query(true, MESSAGE_TABLE, null, where, whereArgs, null, null, null, "");

            if (cursor != null && cursor.getCount() > 0) {
                return cursor.getCount();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * To get all the messages or offer for the customer id and mode.
     *
     * @param customerId
     * @param mode       mode == 1 -> offer; mode == 0 -> messages
     * @return
     */
    private ArrayList<Message> getAllMessageForCustomerId(String customerId, int mode) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor = null;

        try {
            String where = FIELD_CUSTOMER_ID + " = ? AND " + FIELD_MESSAGE_TYPE + " = ? ";
            String[] whereArgs = {customerId, String.valueOf(mode)};

            cursor = IScoreDatabase.getInstance().getDatabase()
                    .query(true, MESSAGE_TABLE, null, where, whereArgs, null, null, null, "");

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    Message transaction = new Message();
                    transaction.id = cursor.getLong(cursor.getColumnIndex(FIELD_ID));
                    transaction.messageId = cursor.getLong(cursor.getColumnIndex(FIELD_MSG_ID));
                    transaction.head = cursor.getString(cursor.getColumnIndex(FIELD_MESSAGE_HEAD));
                    transaction.detail = cursor.getString(cursor.getColumnIndex(FIELD_MESSAGE_DETAIL));
                    transaction.date = cursor.getString(cursor.getColumnIndex(FIELD_MESSAGE_DATE));
                    transaction.type = cursor.getInt(cursor.getColumnIndex(FIELD_MESSAGE_TYPE));
                    transaction.mode = cursor.getInt(cursor.getColumnIndex(FIELD_MESSAGE_MODE));
                    transaction.isSeen =
                            (cursor.getInt(cursor.getColumnIndex(FIELD_MESSAGE_SEEN)) == 1) ? true : false;

//                    System.out.println("Date  : " + transaction.date);

                    messages.add(transaction);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messages;
    }



    public void deleteMessage(long messageId) {
        String where = FIELD_MSG_ID + " = ?";
        String[] whereArgs = {String.valueOf(messageId)};

        IScoreDatabase.getInstance().delete(MESSAGE_TABLE, where, whereArgs);
    }

    /**
     * To delete all messages and offer.
     */
    public void deleteAllRows() {
        IScoreDatabase.getInstance().delete(MESSAGE_TABLE, null, null);
    }

}
