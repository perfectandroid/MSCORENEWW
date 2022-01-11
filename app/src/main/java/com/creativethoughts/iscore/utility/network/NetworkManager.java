package com.creativethoughts.iscore.utility.network;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NetworkManager {
    private static NetworkManager mNetWorkManager;
    private ResponseManager mResponseManager;
    private String mUrl;
    private SweetAlertDialog mSweetAlertDialog;
    public static synchronized NetworkManager getInstance() {
        if ( mNetWorkManager == null )
            mNetWorkManager = new NetworkManager();
        return mNetWorkManager;
    }

    public void connector(String url , ResponseManager responseManager, Activity activity, String loadingMessage){
        if ( !NetworkUtil.isOnline() && activity != null ){
            Toast.makeText( activity, "Please turn on Wi-Fi or mobile data", Toast.LENGTH_SHORT ).show();
        }

        mResponseManager = responseManager;
        mUrl = url;
        if ( activity != null ){
            mSweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE );
            mSweetAlertDialog.getProgressHelper().setBarColor( Color.parseColor("#A5DC86") );
            mSweetAlertDialog.setTitleText( loadingMessage );
            mSweetAlertDialog.setCancelable( false );
            mSweetAlertDialog.show();
        }
        getObservable().
                subscribeOn(Schedulers.io() )
                .observeOn(AndroidSchedulers.mainThread()) 
                .subscribe( getObserver() );
    }
    private Observable<String> getObservable(){

            return Observable.fromCallable( this::talkingToServer );
    }
    private Observer<String> getObserver(){
        return new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {
              //Do nothing
            }

            @Override
            public void onNext( String result ) {
                if ( mSweetAlertDialog != null ){
                    mSweetAlertDialog.dismissWithAnimation();
                }
                Log.e("TAG","result  66   "+result);
                if ( result.equals( IScoreApplication.SERVICE_NOT_AVAILABLE ) )
                    mResponseManager.onError( IScoreApplication.SERVICE_NOT_AVAILABLE );
                else mResponseManager.onSuccess( result );
            }

            @Override
            public void onError(Throwable e) {
                if ( mSweetAlertDialog != null ){
                    mSweetAlertDialog.dismissWithAnimation();
                }
                mResponseManager.onError( IScoreApplication.SERVICE_NOT_AVAILABLE );
            }

            @Override
            public void onComplete() {
                if ( mSweetAlertDialog != null ){
                    mSweetAlertDialog.dismissWithAnimation();
                }
            }
        };
    }
    private String talkingToServer(){
        return ConnectionUtil.getResponse( mUrl );
    }
}
