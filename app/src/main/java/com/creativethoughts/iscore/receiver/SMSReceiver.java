package com.creativethoughts.iscore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by muthukrishnan on 07/07/16.
 */
public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_VERIFICATION = "sms_verification";
    public static final String SMS_VERIFICATION_CODE = "sms_verification_code";
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

//                    Log.d("MM", "phoneNumber");

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

//                    Log.d("MM", "message : " + message);

                    String code = getCode(message);

//                    Log.d("MM", "code : " + code);

                    if(!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code)) {
                        sendVerificationCode(context, code);
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private String getCode(String message) {
        if(TextUtils.isEmpty(message) || !message.contains("Mscore Verfication code")) {
            return "";
        }

        String code = message.substring(message.length() - 4, message.length());

        if(TextUtils.isDigitsOnly(code)) {

        }

        return code;
    }

    private void sendVerificationCode(Context context, String code) {
        Intent intent = new Intent(SMS_VERIFICATION);

        intent.putExtra(SMS_VERIFICATION_CODE, code);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
