package com.creativethoughts.iscore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSBroadcastReceiver extends BroadcastReceiver {
    public static SmsReceiveHandler smsReceiveHandler;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ( SmsRetriever.SMS_RETRIEVED_ACTION.equals( intent.getAction() ) ){
            Bundle extras = intent.getExtras();
            //AsyncTask.Status  status = (AsyncTask.Status) extras.get( SmsRetriever.EXTRA_STATUS );
            Status status = ( Status ) extras.get( SmsRetriever.EXTRA_STATUS );
            switch ( status.getStatusCode() ){
                case CommonStatusCodes
                        .SUCCESS :
                    String message = ( String ) extras.get( SmsRetriever.EXTRA_SMS_MESSAGE );
                    if ( smsReceiveHandler != null ){
                        smsReceiveHandler.received( message );
                    }
                    //Toast.makeText( context, message, Toast.LENGTH_LONG ).show();
                break;
                case CommonStatusCodes.TIMEOUT:
                    try{
                        smsReceiveHandler.timout( "time out");
                    }catch ( Exception e ){
                        //Do nothing
                    }
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
//                    Toast.makeText( context, "Time out", Toast.LENGTH_LONG ).show();
                    break;
                    default:break;
            }
        }
    }
    public interface SmsReceiveHandler{
        void received( String message );
        void timout( String data );
    }
}
