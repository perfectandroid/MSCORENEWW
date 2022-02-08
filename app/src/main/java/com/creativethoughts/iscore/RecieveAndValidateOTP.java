package com.creativethoughts.iscore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.db.dao.DbSync;
import com.creativethoughts.iscore.gsonmodel.SyncParent;
import com.creativethoughts.iscore.receiver.MySMSBroadcastReceiver;
import com.creativethoughts.iscore.receiver.SMSReceiver;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.SyncUtils;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RecieveAndValidateOTP extends Activity implements MySMSBroadcastReceiver.SmsReceiveHandler {
    public String TAG = "RecieveAndValidateOTP";
    private EditText mEtVerificationCode;
    SweetAlertDialog mSweetAlertDialog;

//    private SMSVerificationCodeReceiver mSmsVerificationCodeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

     /*   if (mSmsVerificationCodeReceiver == null) {
            mSmsVerificationCodeReceiver = new SMSVerificationCodeReceiver();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mSmsVerificationCodeReceiver,
                new IntentFilter(SMSReceiver.SMS_VERIFICATION));*/

        mEtVerificationCode =   findViewById(R.id.verificationCode);

        Button button =   findViewById(R.id.btnVerify);

        SmsRetrieverClient smsRetrieverClient = SmsRetriever.getClient( this );
        Task<Void> task = smsRetrieverClient.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText( getApplicationContext(), "started", Toast.LENGTH_LONG ).show();
            }
        });
        task.addOnFailureListener(e -> {
//                Toast.makeText( getApplicationContext(), "Failed", Toast.LENGTH_LONG ).show();
        });

        button.setOnClickListener(v -> {

//            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

//            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
//            String BASE_URL=pref.getString("baseurl", null);

            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null)+"api/MV3";


            if (NetworkUtil.isOnline()) {

//                UserCredential userCredential =
//                        UserCredentialDAO.getInstance().getLoginCredential();

                SharedPreferences countryCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                String countryCode = countryCodeSP.getString("countryCode","");
                SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                String mobileNumber = mobileNoSP.getString("mobileNo","");


                try{
                    mSweetAlertDialog = new SweetAlertDialog( RecieveAndValidateOTP.this, SweetAlertDialog.PROGRESS_TYPE );
                    mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    mSweetAlertDialog.setTitleText( "Verify PIN" );
                    mSweetAlertDialog.setCancelable( false );
                    mSweetAlertDialog.show();
                    String otp = mEtVerificationCode.getText().toString();
                    final String url =
                            BASE_URL + "/VerifyOTP?" +
                                    "Mobno=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(countryCode +
                                    mobileNumber ))+
                                    "&OTP=" +  IScoreApplication.encodedUrl(IScoreApplication.encryptStart( otp )) +
                                    "&NoOfDays=" +IScoreApplication.encodedUrl(IScoreApplication.encryptStart("30"));

                    Log.e("TAG","url  109    "+url);
                    Log.e("TAG","mobileNumber  109    "+IScoreApplication.encryptStart(countryCode + mobileNumber ));
                    Log.e("TAG","otp  109    "+IScoreApplication.encryptStart( otp ));
                    Log.e("TAG","url  109    "+IScoreApplication.encryptStart("30"));

                    NetworkManager.getInstance().connector(url, new ResponseManager() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e("TAG","result   109   "+result);
                            processResponse( analyzeResponse( result, otp ) );
                            if ( mSweetAlertDialog != null )
                                mSweetAlertDialog.dismiss();
                        }

                        @Override
                        public void onError(String error) {
                            if ( mSweetAlertDialog != null )
                                mSweetAlertDialog.dismiss();
                            DialogUtil.showAlert(RecieveAndValidateOTP.this,
                                    error);
                        }
                    }, null , "Verifying otp");
                }catch ( Exception e ){
                    DialogUtil.showAlert(RecieveAndValidateOTP.this,
                            "App blocked");
                }

            } else {
                DialogUtil.showAlert(RecieveAndValidateOTP.this,
                        "Network is currently unavailable. Please try again later.");
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        MySMSBroadcastReceiver.smsReceiveHandler = this;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

       /* if  (mSmsVerificationCodeReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mSmsVerificationCodeReceiver);
        }*/
    }

    @Override
    public void received(String message) {
        if ( mEtVerificationCode != null ){

            String[] splited = message.split("\\s+");
            if ( splited.length > 6 ){
                String otpString = splited[5];
                int otpInt;
                try{
                    otpInt = Integer.parseInt( otpString );
                }catch ( NumberFormatException e ){
                    otpInt = 0;
                }
                if ( otpInt > 0 ){
                    mEtVerificationCode.setText( otpString );
                }
            }
        }
    }

    @Override
    public void timout(String data) {

    }

    class SMSVerificationCodeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction() != null && intent.getAction().equalsIgnoreCase(SMSReceiver.SMS_VERIFICATION))
            {
                String verificationCode = intent.getStringExtra(SMSReceiver.SMS_VERIFICATION_CODE);

                if (mEtVerificationCode != null) {
                    mEtVerificationCode.setText(verificationCode);
                }
            }
        }
    }
    private int analyzeResponse( String response, String otp){

        if ( !TextUtils.isEmpty( response ) ){
            String nullInfo = "{\"acInfo\":null}";
            response = response.trim();
            if ( response.equals( nullInfo ) ){
                return -1;
            }else if ( response.equals( IScoreApplication.SERVICE_NOT_AVAILABLE ) ){
                return 0;
            }else {

                Log.e("TAG","response  123   "+response.toString());




                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(response.toString());
                    // JSONObject jobjt = jObject.getJSONObject("acInfo");

                    JSONArray jArray3 = jObject.getJSONArray("acInfo");

                    for(int i = 0; i < jArray3 .length(); i++)
                    {
                        JSONObject object3 = jArray3.getJSONObject(i);


                        SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                        SharedPreferences.Editor customerIdEditer = customerIdSP.edit();
                        customerIdEditer.putString("customerId",  object3.getString("customerId"));
                        customerIdEditer.commit();

                        SharedPreferences customerNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF27, 0);
                        SharedPreferences.Editor customerNoEditer = customerNoSP.edit();
                        customerNoEditer.putString("customerNo",  object3.getString("customerNo"));
                        customerNoEditer.commit();

                        SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
                        SharedPreferences.Editor customerNameEditer = customerNameSP.edit();
                        customerNameEditer.putString("customerName", object3.getString("customerName"));
                        customerNameEditer.commit();

                        SharedPreferences customerAddress1SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF29, 0);
                        SharedPreferences.Editor customerAddress1Editer = customerAddress1SP.edit();
                        customerAddress1Editer.putString("customerAddress1", object3.getString("customerAddress1"));
                        customerAddress1Editer.commit();

                        SharedPreferences customerAddress2SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF30, 0);
                        SharedPreferences.Editor customerAddress2Editer = customerAddress2SP.edit();
                        customerAddress2Editer.putString("customerAddress2", object3.getString("customerAddress2"));
                        customerAddress2Editer.commit();

                        SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                        SharedPreferences.Editor mobileNoEditer = mobileNoSP.edit();
                        mobileNoEditer.putString("mobileNo",  object3.getString("mobileNo"));
                        mobileNoEditer.commit();

                        SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                        SharedPreferences.Editor BankVerifierEditer = BankVerifierSP.edit();
                        BankVerifierEditer.putString("BankVerifier",  "1");
                        BankVerifierEditer.commit();

                        SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF33, 0);
                        SharedPreferences.Editor loginEditer = loginSP.edit();
                        loginEditer.putString("login", "0");
                        loginEditer.commit();

                        SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                        SharedPreferences.Editor tokenIdSPEditer = tokenIdSP.edit();
                        tokenIdSPEditer.putString("Token", object3.getString("TokenNo"));
                        tokenIdSPEditer.commit();


                        Log.e(TAG,"Token     287    "+object3.getString("TokenNo"));

                        SharedPreferences pinIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                        SharedPreferences.Editor pinIdSPEditer = pinIdSP.edit();
                        pinIdSPEditer.putString("pinlog", object3.getString("pin"));
                        pinIdSPEditer.commit();

                        SharedPreferences customerAddress3SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF42, 0);
                        SharedPreferences.Editor customerAddress3Editer = customerAddress3SP.edit();
                        customerAddress3Editer.putString("customerAddress3", object3.getString("customerAddress3"));
                        customerAddress3Editer.commit();


//                        SharedPreferences TokenNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
//                        SharedPreferences.Editor TokenNoEditer = TokenNoSP.edit();
//                        TokenNoEditer.putString("TokenNo", "");
//                        TokenNoEditer.commit();

//                        SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
//                        SharedPreferences.Editor BankVerifierEditer = BankVerifierSP.edit();
//                        BankVerifierEditer.putString("BankVerifier", "1");
//                        BankVerifierEditer.commit();



                        JSONObject jOBJ = jArray3.getJSONObject(i);
                        JSONArray jArray4 = jOBJ.getJSONArray("accounts");

                        SharedPreferences accntIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF43, 0);
                        SharedPreferences.Editor accntSPEditer = accntIdSP.edit();
                        accntSPEditer.putString("accountNoarray", String.valueOf(jArray4));
                        accntSPEditer.commit();

//                        String strDMenu =object3.getString("DMenu");
//                        JSONObject jobjDMenu = object3.getString("DMenu"));

                        try{
                            Log.e(TAG,"object3  3111   "+object3.getString("DMenu"));
                            JSONObject jobjDMenu = new JSONObject(object3.getString("DMenu"));
                            Log.e(TAG,"jobjDMenu  3112   "+jobjDMenu);
                            Log.e(TAG,"Recharge  3113   "+jobjDMenu.getString("Recharge"));

                            SharedPreferences RechargeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF44, 0);
                            SharedPreferences.Editor RechargeEditer = RechargeSP.edit();
                            RechargeEditer.putString("Recharge",jobjDMenu.getString("Recharge") );
                            RechargeEditer.commit();

                            SharedPreferences ImpsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF45, 0);
                            SharedPreferences.Editor ImpsEditer = ImpsSP.edit();
                            ImpsEditer.putString("Imps",jobjDMenu.getString("Imps") );
                            ImpsEditer.commit();

                            SharedPreferences RtgsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF46, 0);
                            SharedPreferences.Editor RtgsEditer = RtgsSP.edit();
                            RtgsEditer.putString("Rtgs",jobjDMenu.getString("Rtgs") );
                            RtgsEditer.commit();

                            SharedPreferences KsebSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF47, 0);
                            SharedPreferences.Editor KsebEditer = KsebSP.edit();
                            KsebEditer.putString("Kseb",jobjDMenu.getString("Kseb") );
                            KsebEditer.commit();

                            SharedPreferences NeftSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF48, 0);
                            SharedPreferences.Editor NeftEditer = NeftSP.edit();
                            NeftEditer.putString("Neft",jobjDMenu.getString("Neft") );
                            NeftEditer.commit();

                            SharedPreferences OwnImpsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF49, 0);
                            SharedPreferences.Editor OwnImpsEditer = OwnImpsSP.edit();
                            OwnImpsEditer.putString("OwnImps",jobjDMenu.getString("OwnImps") );
                            OwnImpsEditer.commit();

                        }
                        catch (Exception e){

                            Log.e(TAG,"Exception  3114   "+e.toString());
                        }




                      /*  for (int j = 0; j < jArray4.length(); j++)
                        {
                            JSONObject obj2 = jArray4.getJSONObject(j);


                            SharedPreferences accntIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF43, 0);
                            SharedPreferences.Editor accntSPEditer = accntIdSP.edit();
                            accntSPEditer.putString("accountNo",  obj2 .getString("acno"));
                            accntSPEditer.commit();

                            SharedPreferences typeshrtSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF44, 0);
                            SharedPreferences.Editor typeshrtSPEditer = typeshrtSP.edit();
                            typeshrtSPEditer.putString("typeShort",  obj2 .getString("typeShort"));
                            typeshrtSPEditer.commit();

                        }*/
                    }





                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"JSONException  308    "+e.toString());
                }


                ///

//                SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
//                SharedPreferences.Editor customerIdEditer = customerIdSP.edit();
//                customerIdEditer.putString("customerId", "23");
//                customerIdEditer.commit();
//
//                SharedPreferences customerNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF27, 0);
//                SharedPreferences.Editor customerNoEditer = customerNoSP.edit();
//                customerNoEditer.putString("customerNo", "001001000025");
//                customerNoEditer.commit();
//
//                SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
//                SharedPreferences.Editor customerNameEditer = customerNameSP.edit();
//                customerNameEditer.putString("customerName", "ABOOBAKKAR.T.P");
//                customerNameEditer.commit();
//
//                SharedPreferences customerAddress1SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF29, 0);
//                SharedPreferences.Editor customerAddress1Editer = customerAddress1SP.edit();
//                customerAddress1Editer.putString("customerAddress1", "");
//                customerAddress1Editer.commit();
//
//                SharedPreferences customerAddress2SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF30, 0);
//                SharedPreferences.Editor customerAddress2Editer = customerAddress2SP.edit();
//                customerAddress2Editer.putString("customerAddress2", "");
//                customerAddress2Editer.commit();
//
//                SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
//                SharedPreferences.Editor mobileNoEditer = mobileNoSP.edit();
//                mobileNoEditer.putString("mobileNo", "9456328197");
//                mobileNoEditer.commit();
//
//                SharedPreferences pinSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
//                SharedPreferences.Editor pinEditer = pinSP.edit();
//                pinEditer.putString("pin", "123456");
//                pinEditer.commit();
//
//                SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF33, 0);
//                SharedPreferences.Editor loginEditer = loginSP.edit();
//                loginEditer.putString("login", "0");
//                loginEditer.commit();
//
//                SharedPreferences TokenNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
//                SharedPreferences.Editor TokenNoEditer = TokenNoSP.edit();
//                TokenNoEditer.putString("TokenNo", "");
//                TokenNoEditer.commit();
//
//                SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
//                SharedPreferences.Editor BankVerifierEditer = BankVerifierSP.edit();
//                BankVerifierEditer.putString("BankVerifier", "1");
//                BankVerifierEditer.commit();



                /////////////


//                UserCredentialDAO.getInstance().
//                        updateNewPin(otp);
//
//                UserCredentialDAO.getInstance().updateUserLogin();
//                BankVerifier.getInstance().insertValue("1");
//                //Remove all the cached data in DB
//                UserDetailsDAO.getInstance().deleteAllRows();
//                PBAccountInfoDAO.getInstance().deleteAllRows();
//                NewTransactionDAO.getInstance().deleteAllRow();
//                PBMessagesDAO.getInstance().deleteAllRows();
             //   SettingsDAO.getInstance().deleteAllRows();

                SyncParent syncParent = new Gson().fromJson( response, SyncParent.class );

                return DbSync.getInstance().sync( syncParent,true );
            }

        }else {
            return IScoreApplication.FLAG_NETWORK_EXCEPTION;
        }
    }
    private void processResponse( int value ){
        if (IScoreApplication.checkPermissionIemi(value,RecieveAndValidateOTP.this)) {
            switch (value) {
                case -1:

                    DialogUtil.showAlert(RecieveAndValidateOTP.this,
                            "OTP Mismatch.");

                    break;

                case 0:
                    IScoreApplication.simpleAlertDialog(RecieveAndValidateOTP.this, new IScoreApplication.AlertProcess() {
                        @Override
                        public void ok() {
                            //Do nothing
                        }

                        @Override
                        public void cancel() {
                            //Do nothing
                        }
                    }, IScoreApplication.SERVICE_NOT_AVAILABLE );
                    break;

                case 1:


                    SyncUtils.startAlarmManage(RecieveAndValidateOTP.this);
                    try{
                        mSweetAlertDialog.dismissWithAnimation();
                    }catch (Exception e ){
                        //Do nothing
                    }
                    moveToHomeScreen();

                    break;

                case IScoreApplication.FLAG_NETWORK_EXCEPTION:
                    DialogUtil.showAlert(RecieveAndValidateOTP.this,
                            IScoreApplication.MSG_EXCEPTION_NETWORK);
                    break;

                default:
                    break;
            }

        }
    }
    private void moveToHomeScreen() {
         Date date = Calendar.getInstance().getTime();
         SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
         String formattedDate = df.format(date);

//        UserCredential userCredential = UserCredentialDAO.getInstance().getLoginCredential();
//        String strMobileNo =userCredential.mobileNumber;

        SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
        String strMobileNo =mobileNoSP.getString("mobileNo","");

        SharedPreferences TestingMobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
        SharedPreferences.Editor TestingMobileNoEditer = TestingMobileNoSP.edit();
        TestingMobileNoEditer.putString("LoginMobileNo", strMobileNo);
        TestingMobileNoEditer.commit();

        SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                      SharedPreferences.Editor loginEditer = loginSP.edit();
                                      loginEditer.putString("logintime", formattedDate);
                                      loginEditer.commit();

         SharedPreferences ImageSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                      SharedPreferences.Editor imageEditer = ImageSP.edit();
                                      imageEditer.putString("custimage", "");
                                      imageEditer.commit();
                                                                                                                                 
     //   UserCredentialDAO.getInstance().updateUserLogin();
        Intent passBookAccount = new Intent(RecieveAndValidateOTP.this, HomeActivity.class);
        startActivity(passBookAccount);
        finish();
    }
}
