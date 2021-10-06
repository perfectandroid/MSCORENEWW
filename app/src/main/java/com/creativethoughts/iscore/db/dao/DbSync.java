package com.creativethoughts.iscore.db.dao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.UserRegistrationActivity;
import com.creativethoughts.iscore.gsonmodel.SyncAccount;
import com.creativethoughts.iscore.gsonmodel.SyncDMenu;
import com.creativethoughts.iscore.gsonmodel.SyncMain;
import com.creativethoughts.iscore.gsonmodel.SyncMessage;
import com.creativethoughts.iscore.gsonmodel.SyncParent;
import com.creativethoughts.iscore.gsonmodel.SyncTransactions;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by vishnu on 7/23/2018 - 3:12 PM.
 */
public class DbSync {
    private static volatile DbSync mdbsync;
    public static synchronized DbSync getInstance(){
        if ( mdbsync == null )
            mdbsync = new DbSync();
        return mdbsync;
    }
    public int sync(SyncParent syncParent, boolean isValidateOtp ){
        try{

            if ( syncParent.getAcInfo() == null || syncParent.getAcInfo().isEmpty() ){
                return -1;
            }
            List<SyncMain> syncMainList = syncParent.getAcInfo();
            SyncMain syncMain = syncMainList.get(0);
            SyncDMenu dMenu = new Gson().fromJson( syncMain.getdMenuString(), SyncDMenu.class);
            syncMain.setdMenu( dMenu );
            syncMain.setUserLogin("1");

            int customerId = Integer.parseInt(syncMain.getCustomerId());

            if ( customerId <= 0 )
                return -1;
            List<SyncAccount> syncAccountList = syncMain.getAccountList();
            List<SyncMessage> messages = syncMain.getMessages();

            if ( syncAccountList == null || syncAccountList.isEmpty() ){
                return  -2;
            }

            UserCredentialDAO.getInstance().updateToken( syncMain.getTokenNo() );
            UserDetailsDAO.getInstance().insertGsonUserDetails( syncMain );
            DynamicMenuDao.getInstance().insertGsonValue( syncMain.getdMenu() );

            for ( int i = 0; i < syncAccountList.size(); i++ ){
                SyncAccount syncAccount = syncAccountList.get(i);
                if ( isValidateOtp  && i == 0){
                    SettingsDAO.getInstance()
                            .insertValues("30", 12, 0, syncAccount.getAccNo() );

                    SettingsDAO.getInstance().updateSyncTime();
                }
                PBAccountInfoDAO.getInstance().insertGsonAccountInfo( syncAccount );
                List<SyncTransactions> transactionsList =  syncAccount.getTransactionsList();
                for (SyncTransactions syncTransaction : transactionsList ) {
                    NewTransactionDAO.getInstance().insertGsonTransaction( syncTransaction, syncAccount.getAccNo() );
                }
            }
            for ( SyncMessage syncMessage : messages ){
                PBMessagesDAO.getInstance().insertMessage( syncMain.getCustomerId(), syncMessage.getMessageId(),
                        syncMessage.getMessageHead(), syncMessage.getMessageDetail(), syncMessage.getMessageDate(), syncMessage.getMessageType(),
                        syncMessage.getMessageMode() );
            }
            return 1;
        }catch ( Exception e ){
            return -1;
        }
    }
    public void logout( Activity activity ){

        UserCredentialDAO.getInstance().deleteAllUserData();
        UserDetailsDAO.getInstance().deleteAllRows();
        PBAccountInfoDAO.getInstance().deleteAllRows();
        PBMessagesDAO.getInstance().deleteAllRows();
        RechargeDAO.getInstance().deleteAllRows();
        NewTransactionDAO.getInstance().deleteAllRow();
        SettingsDAO.getInstance().deleteAllRows();
        BankVerifier.getInstance().deleteAllRows();
        DynamicMenuDao.getInstance().deleteAll();
        KsebBillDAO.getInstance().deleteAll();

        Intent intent = new Intent( activity, UserRegistrationActivity.class );
        intent.putExtra("from","true");
        activity.startActivity( intent );
        activity.finish();
    }
}
