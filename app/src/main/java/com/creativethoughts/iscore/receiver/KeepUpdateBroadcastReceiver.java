package com.creativethoughts.iscore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.NotificationMgr;
import com.creativethoughts.iscore.db.dao.DbSync;
import com.creativethoughts.iscore.db.dao.NewTransactionDAO;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.gsonmodel.SyncParent;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.google.gson.Gson;

/**
 * Created by muthukrishnan on 09/10/15.
 */
public class KeepUpdateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        new SyncServer().execute();
    }


    class SyncServer extends AsyncTask<String, android.R.integer, Integer> {

        public SyncServer() {
            //Do nothing
        }


        @Override
        protected Integer doInBackground(String... params) {

            return getSearching();
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);

            if(result > 0) {
                NotificationMgr.getInstance().showNotification("Your account is updated with new transactions or messages");
            }

            //Try to remove old entries
            NewTransactionDAO.getInstance().removeOldTransaction();
            PBMessagesDAO.getInstance().removeOldMessages();
        }

        private int getSearching() {
            int transactions = 0;

            try {

                UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

                String custId = user.customerId;

                UserCredential userCred = UserCredentialDAO.getInstance().getLoginCredential();

                String pin1 = userCred.pin;


                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

                if (settingsModel == null) {
                    return -1;
                }

                final int days;

                if(settingsModel.days <= 0) {
                    days = 30;
                } else {
                    days = settingsModel.days;
                }

                final String url =
                        CommonUtilities.getUrl() +
                                "/SyncNormal?All="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart("false"))+
                                "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                                "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin1)) +
                                "&NoOfDays=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(days+""));


                String text1 = ConnectionUtil.getResponse(url);

                if (!TextUtils.isEmpty(text1)) {
                    // May be some TIME_IN_MILLISECOND no transactions.
                    SettingsDAO.getInstance().updateSyncTime();

                    String s2 = "{\"acInfo\":null}";

                    String s3 = text1.trim();

                    if (s3.equals(s2)) {

                        return -1;

                    } else {
                        SyncParent syncParent = new Gson().fromJson( text1, SyncParent.class );
                        transactions = DbSync.getInstance().sync( syncParent, false );

                    }
                }
            } catch (Exception js) {
                if ( IScoreApplication.DEBUG )
                    Log.e("ex", js.toString() );
            }
            return transactions;

        }
    }

}
