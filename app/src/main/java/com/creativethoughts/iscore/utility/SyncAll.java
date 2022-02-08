package com.creativethoughts.iscore.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.UserRegistrationActivity;
import com.creativethoughts.iscore.db.dao.NewTransactionDAO;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthukrishnan on 24/10/15
 */
public class SyncAll{
    private final OnSyncStateListener mOnSyncStateListener;
    private Context mContex = null;
    private boolean mIsRefresh;
    private SyncAll(OnSyncStateListener onSyncStateListener, boolean isRefresh) {
        mOnSyncStateListener = onSyncStateListener;
        mIsRefresh = isRefresh;
    }

    public static void syncAllAccounts(OnSyncStateListener onSyncStateListener, Context context) {
        syncAllAccounts(onSyncStateListener, false, context);
    }

    public static void syncAllAccounts(OnSyncStateListener onSyncStateListener, boolean isRefresh, Context context) {

        SyncAll syncAll = new SyncAll(onSyncStateListener, isRefresh);
        syncAll.sync(context);
    }





    private void sync(Context context) {
        mContex = context;
        getObservable()
               .subscribeOn(Schedulers.io() )
               .observeOn(AndroidSchedulers.mainThread() )
               .subscribe( getObserver());
    }


    public interface OnSyncStateListener {
        void onCompleted();

        void onFailed();
    }

    private Observable<Integer> getObservable(){
        return Observable.fromCallable(this::getSearching);
    }
    private Observer< Integer > getObserver(   ){
        return new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Do nothing
            }

            @Override
            public void onNext(Integer result ) {
                if(mIsRefresh) {
                    //Try to remove old entries
                    NewTransactionDAO.getInstance().removeOldTransaction();
                    PBMessagesDAO.getInstance().removeOldMessages();
                }

                if (result > 0) {
                    mOnSyncStateListener.onCompleted();
                } else {
                    mOnSyncStateListener.onFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                //Do nothing
            }
        };
    }

    private int getSearching() {
        int transactions = 0;

        try {

         //   UserDetails user = UserDetailsDAO.getInstance().getUserDetail();
         //   String custId = user.customerId;
            SharedPreferences customerIdSP = mContex.getSharedPreferences(Config.SHARED_PREF26, 0);
            String custId = customerIdSP.getString("customerId","");


            //   UserCredential userCred = UserCredentialDAO.getInstance().getLoginCredential();
           // String pin1 = userCred.pin;
            SharedPreferences pinIdSP = mContex.getSharedPreferences(Config.SHARED_PREF36, 0);
            String pin1 =  pinIdSP.getString("pinlog","");


       //     SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

//            if (settingsModel == null) {
//                return -1;
//            }
//
//            final int days;
//
//            if (settingsModel.days <= 0) {
//                days = 30;
//            } else {
//                days = settingsModel.days;
//            }

            SharedPreferences SelectedDaysSP = mContex.getSharedPreferences(Config.SHARED_PREF37, 0);
            String SelectedDays = SelectedDaysSP.getString("SelectedDays",null);
            if (SelectedDays == null) {
                return -1;
            }

            final int days;

            if (Integer.parseInt(SelectedDays) <= 0) {
                days = 30;
            } else {
                days = Integer.parseInt(SelectedDays);
            }

            final String url;


//            SharedPreferences pref =mContex.getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =mContex.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            if( mIsRefresh ) {
                url =
                        BASE_URL + "/SyncNormal?" +
                                "All="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart("false")) +
                                "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                                "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin1)) +
                                "&NoOfDays=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(days+""));
            } else {
                url =
                        BASE_URL + "/SyncNormal?" +
                                "All="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart("true")) +
                                "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId)) +
                                "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin1)) +
                                "&NoOfDays=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(days+""));
            }
            String text1 = ConnectionUtil.getResponse(url);

            if (!TextUtils.isEmpty(text1)) {

                //Remove all the cached data in DB
                if( !mIsRefresh ) {
                    //As per 13-Dec-2015 discussion, we should not delete while manual refresh
//                    UserDetailsDAO.getInstance().deleteAllRows();
//                    PBAccountInfoDAO.getInstance().deleteAllRows();
//                    NewTransactionDAO.getInstance().deleteAllRow();
//                    PBMessagesDAO.getInstance().deleteAllRows();
                }


                // May be some TIME_IN_MILLISECOND no transactions.
             //   SettingsDAO.getInstance().updateSyncTime();

                String s2 = "{\"acInfo\":null}";

                String s3 = text1.trim();

                if (s3.equals(s2)) {

//                    UserCredentialDAO.getInstance().deleteAllUserData();
//                    UserDetailsDAO.getInstance().deleteAllRows();
//                    PBAccountInfoDAO.getInstance().deleteAllRows();
//                    PBMessagesDAO.getInstance().deleteAllRows();
//                    RechargeDAO.getInstance().deleteAllRows();
//                    NewTransactionDAO.getInstance().deleteAllRow();
//                  //  SettingsDAO.getInstance().deleteAllRows();
//                    BankVerifier.getInstance().deleteAllRows();
//                    DynamicMenuDao.getInstance().deleteAll();
//                    KsebBillDAO.getInstance().deleteAll();

                    Intent intent = new Intent(mContex,UserRegistrationActivity.class);
                    mContex.startActivity(intent);
                    ((Activity)mContex).finish();
                }
                else {
//                    SyncParent syncParent = new Gson().fromJson( text1, SyncParent.class );
//                    transactions = DbSync.getInstance().sync( syncParent, false );

                }
            } else {
                return -6;
            }
        } catch (Exception js) {
            //Do nothing
        }
        return transactions;

    }

}
