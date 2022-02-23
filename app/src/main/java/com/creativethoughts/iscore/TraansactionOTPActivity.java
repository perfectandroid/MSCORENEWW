package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.AddSenderReceiverResponseModel;
import com.creativethoughts.iscore.model.ResendOtp;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TraansactionOTPActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String BUNDLE_DATA_SENDER_ID = "sender_id";
    private static final String BUNDLE_DATA_RECEIVER_ID = "receiver_id";
    private static final String BUNDLE_DATA_TRANSACTION_ID = "transaction_id";
    private static final String BUNDLE_DATA_IS_TRANSACTION = "is_transaction";
    private static final String BUNDLE_OTP_REFERENCE = "otpReference";
    private static final String BUNDLE_MOBILE = "mobile";
    private static final String BUNDLE_RESEND_LINK = "resend_link";
    private static final String BUNDLE_IS_SENDER = "is_sender";
    private static final String BUNDLE_SENDER_RECEIVER_OBJ = "sender_reciever_obj";
    protected Button button, btnResendOtp;
    private String mSenderId;
    private String mReceiverId;
    private String mTransactionId;
    private boolean mIsForTransaction;
    private boolean mIsSender;
    public TextView txt_amtinword;
    private AppCompatEditText mOTPEt;
    private String mOtpReferenceNo;
    private String mMobileNo;
    private ProgressDialog mProgressDialog;
    private String mResendLink;
    private ResendOtp resendOtp;
    String cusid,from;
    String otp,sender,otprefno;
    String otpresend,otpresend1,otpresend2;
    private ArrayList<ResendOtp> resendOtps = new ArrayList<ResendOtp>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_transaction_ot);


        setRegViews();
      //  Log.i("FRom",from);
        from = getIntent().getStringExtra("from");

       // resendOtp=new ResendOtp();
    }

    private void getVerifyreceiverOTP( String otp,String otprefno, String senderid, String receivrid) {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {


                    SharedPreferences cusidpref = TraansactionOTPActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(senderid));
                    requestObject1.put("receiverid", IScoreApplication.encryptStart(receivrid));
                    requestObject1.put("OTP", IScoreApplication.encryptStart(otp));
                    requestObject1.put("otpRefNo", IScoreApplication.encryptStart(otprefno));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));



                    Log.e("requestObject1 addrecvr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getVerifyReceiverOTP(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response rcvrotp   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());

                            String statuscode = jObject.getString("StatusCode");
                            String status = jObject.getString("Status");
                            String statusmsg = jObject.getString("message");
                            String senderid = jObject.getString("ID_Sender");
                            String receiverid = jObject.getString("ID_Receiver");
                            String otprefno = jObject.getString("otpRefNo");
                            if(statuscode.equals("200"))
                            {
                                QuickSuccess(senderid,receiverid,otprefno);
                            }
                            else if(statuscode.equals("500"))
                            {
                                alertMessage1("", statusmsg);
                            }
                            else
                            {
                                alertMessage1("", statusmsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                            alertMessage1("", "Some technical issues.");
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("", "Some technical issues.");
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
                alertMessage1("", "Some technical issues.");
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void getVerifysenderOTP(String otp, String senderid, String otprefno,String mob) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {




                    SharedPreferences cusidpref = TraansactionOTPActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(senderid));
                    requestObject1.put("OTP", IScoreApplication.encryptStart(otp));
                    requestObject1.put("otpRefNo", IScoreApplication.encryptStart(otprefno));
                    requestObject1.put("MobileNo", IScoreApplication.encryptStart(mob));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));



                    Log.e("requestObject1senderotp",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getVerifySenderOTP(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response snderotp   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statuscode = jObject.getString("StatusCode");
                            String status = jObject.getString("Status");
                            String statusmsg = jObject.getString("message");
                            String senderid = jObject.getString("ID_Sender");
                            String receiverid = jObject.getString("ID_Receiver");
                            String otprefno = jObject.getString("otpRefNo");
                            if(statuscode.equals("200"))
                            {
                                QuickSuccess(senderid,receiverid,otprefno);
                            }
                            else if(statuscode.equals("500"))
                            {
                                alertMessage1("", statusmsg);
                            }
                            else
                            {
                                alertMessage1("", statusmsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                            alertMessage1("", " Some technical issues.");
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("", "Some technical issues.");
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
                alertMessage1("", " Some technical issues.");
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void getVerifypaymentOTP(String otp, String senderid, String receivrid, String otprefno, String transid) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {


                    SharedPreferences cusidpref = TraansactionOTPActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(senderid));
                    requestObject1.put("receiverid", IScoreApplication.encryptStart(receivrid));
                    requestObject1.put("transcationID", IScoreApplication.encryptStart(transid));
                    requestObject1.put("OTP", IScoreApplication.encryptStart(otp));
                    requestObject1.put("otpRefNo", IScoreApplication.encryptStart(otprefno));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));

                    Log.e("requestObject1 transotp",""+requestObject1);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getVerifyPaymentOTP(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response quickpay   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());

                            String statuscode = jObject.getString("StatusCode");
                            String status = jObject.getString("Status");
                            String statusmsg = jObject.getString("message");
                            String senderid = jObject.getString("ID_Sender");
                            String receiverid = jObject.getString("ID_Receiver");
                            String otprefno = jObject.getString("otpRefNo");
                            if(statuscode.equals("200"))
                            {
                                QuickSuccess(senderid,receiverid,otprefno);
                            }
                            else if(statuscode.equals("500"))
                            {
                                alertMessage1("", statusmsg);
                            }
                            else
                            {
                                alertMessage1("", statusmsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                            alertMessage1("", "Some technical issues.");
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("", "Some technical issues.");
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
                alertMessage1("", "Some technical issues.");
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void setRegViews() {

        mOTPEt =   findViewById(R.id.otp);

        button =   findViewById(R.id.btn_submit);
        //txt_amtinword =   view.findViewById(R.id.txt_amtinword);

        button.setOnClickListener( this );
        btnResendOtp  =  findViewById( R.id.btn_resend_otp );
        btnResendOtp.setOnClickListener( this );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_submit:
                if (isValid()) {
                    if (NetworkUtil.isOnline()) {
                      //  button.setEnabled(false);



                        //  String mob = addSenderReceiverResponseModel.getMobileno();

                        otp = mOTPEt.getText().toString();

                        if(from.equals("quickpay"))

                        {

                            String senderid = getIntent().getStringExtra("sender");
                            String receivrid = getIntent().getStringExtra("recvr");
                             otprefno = getIntent().getStringExtra("otprefno");
                            String transid = getIntent().getStringExtra("transid");

                            getVerifypaymentOTP(otp,senderid,receivrid,otprefno,transid);
                        }
                         if(from.equals("receiver"))
                        {
                            String senderid = getIntent().getStringExtra("sender");
                            String receivrid = getIntent().getStringExtra("receiver");
                             otprefno = getIntent().getStringExtra("otprefno");
                            getVerifyreceiverOTP(otp,otprefno,senderid,receivrid);
                        }
                         if(from.equals("sender"))
                        {

                            String senderid = getIntent().getStringExtra("sender");
                             otprefno = getIntent().getStringExtra("otprefno");
                            String mob =getIntent().getStringExtra("mob");

                                getVerifysenderOTP(otp,senderid,otprefno,mob);






                        }
                    } else {

                        alertMessage1("",  "Network is currently unavailable. Please try again later.");

                       /* DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");*/
                    }
                }
                break;
            case R.id.btn_resend_otp:
                Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
                if(from.equals("quickpay"))
                {
                    String senderid = getIntent().getStringExtra("sender");
                    String receivrid = getIntent().getStringExtra("recvr");
                     otprefno = getIntent().getStringExtra("otprefno");
                    String transid = getIntent().getStringExtra("transid");
                    getVerifypaymentOTP(otp, senderid, receivrid, otprefno, transid);
                }
                else if(from.equals("receiver"))
                {
                    String senderid = getIntent().getStringExtra("sender");
                    String receivrid = getIntent().getStringExtra("recvr");
                     otprefno = getIntent().getStringExtra("otprefno");
                    getVerifyreceiverOTP(otp, senderid, receivrid, otprefno);
                }
                else if(from.equals("sender"))
                {
                    String senderid =  getIntent().getStringExtra("sender");


                    getResendersenderOTP(senderid);
                }
                break;
        }
    }

    private void getResendersenderOTP(String senderid) {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {


                    SharedPreferences cusidpref = TraansactionOTPActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(senderid));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));



                    Log.e("requestObject1 resndr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getResendersenderotp(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response rcvrotp   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());

                            String statuscode = jObject.getString("StatusCode");
                            String statusmsg = jObject.getString("EXMessage");

                            if(statuscode.equals("0"))
                            {
                                JSONObject jobj = jObject.getJSONObject("MTResendSenderOTPDetails");
                                String msg = jobj.getString( "ResponseMessage");
                                otprefno = jobj.getString( "otpRefNo");

                               /* resendOtp.otprefno=otpresend;
                                resendOtps.add(resendOtp);*/
                            }

                            else
                            {
                                alertMessage1("", statusmsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                            alertMessage1("", "Some technical issues.");
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("", "Some technical issues.");
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
                alertMessage1("", "Some technical issues.");
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }


    }


    private void QuickSuccess(String senderid, String receiverid, String otprefno) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater1 = this.getLayoutInflater();

        View layout = inflater1.inflate(R.layout.quick_pay_success_popup, null);
        TextView tvbranch =  layout.findViewById(R.id.tvbranch);
        RelativeLayout rltv_share = layout.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = layout.findViewById( R.id.lay_share );
        TextView tv_sender_name =  layout.findViewById(R.id.tv_sender_name);
        TextView tv_sender_acc_no =  layout.findViewById(R.id.tv_sender_acc_no);
        TextView tv_sender_mob_no =  layout.findViewById(R.id.tv_sender_mob_no);

        TextView tv_reciever_name =  layout.findViewById(R.id.tv_reciever_name);
        TextView tv_reciever_acc_no =  layout.findViewById(R.id.tv_reciever_acc_no);
        TextView tv_reciever_mob_no =  layout.findViewById(R.id.tv_reciever_mob_no);
        ImageView img_aapicon1=  layout.findViewById(R.id.img_aapicon);
        TextView tv_msg =  layout.findViewById(R.id.txtv_msg);
        // TextView tv_msg1 =  layout.findViewById(R.id.txtv_msg1);
        // TextView tv_status =  layout.findViewById(R.id.txtv_status);
        // CardView crdSuccess =  layout.findViewById(R.id.crdSuccess);

        TextView tv_amount =  layout.findViewById(R.id.tv_amount);
        TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
        //  TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
        TextView tvdate =  layout.findViewById(R.id.tvdate);
        TextView tvtime =  layout.findViewById(R.id.tvtime);
        //TextView bt_ok1 =  layout.findViewById(R.id.bt_ok1);
        TextView txtv_typeamt =  layout.findViewById(R.id.txtv_typeamt);
        TextView txtTo =  layout.findViewById(R.id.txtTo);
        TextView txtpfrom =  layout.findViewById(R.id.txtpfrom);
        ImageView img_applogo = layout.findViewById(R.id.img_aapicon);


        //  crdSuccess.setVisibility(View.VISIBLE);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(TraansactionOTPActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

        tv_msg.setText("status");
        tv_sender_name.setText("msenderName");
        tv_sender_mob_no.setText("msenderMobile");
        tv_sender_acc_no.setText("mAccNo");
        // tvbranch.setText(mbranch);
        txtv_typeamt.setText("Paid Amount");
        txtpfrom.setText("Sender Details");
        txtTo.setText("Reciever Details");

        tv_reciever_name.setText("mrecievererName");
        tv_reciever_acc_no.setText("mreceiverAccountno");
        tv_reciever_mob_no.setText("mrecieverMobile");

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        double num =Double.parseDouble(""+"mAmount");
        String stramnt = CommonUtilities.getDecimelFormate(num);
        //  text_confirmationmsg.setText(message);
        // text_confirmationmsg.setVisibility(View.VISIBLE);
        String[] netAmountArr = "mAmount".split("\\.");
        String amountInWordPop = "";
        if ( netAmountArr.length > 0 ){
            int integerValue = Integer.parseInt( netAmountArr[0] );
            amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
            if ( netAmountArr.length > 1 ){
                int decimalValue = Integer.parseInt( netAmountArr[1] );
                if ( decimalValue != 0 ){
                    amountInWordPop += " And " + NumberToWord.convertNumberToWords( decimalValue ) + " Paise" ;
                }
            }
            amountInWordPop += " Only";
        }
        tv_amount_words.setText(""+amountInWordPop);
        tv_amount.setText("₹ " + stramnt );



        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();


        layout.findViewById( R.id.rltv_footer ).setOnClickListener( view1 -> {
            try{
//                getFragmentManager().beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY") )
//                        .commit();
                Intent i=new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );
        lay_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("img_share","img_share   1170   ");
                Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                        rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                rltv_share.draw(canvas);

                try {


                    Uri bmpUri = getLocalBitmapUri(bitmap);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception","Exception   117   "+e.toString());
                }

            }
        });


    /*    bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });*/
        alertDialog.show();
    }

    private boolean isValid() {
        String otp = mOTPEt.getText().toString();

        if (TextUtils.isEmpty(otp)) {
            mOTPEt.setError("Please enter the OTP");

            return false;
        }

        mOTPEt.setError(null);

        return true;
    }

    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TraansactionOTPActivity.this);

        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);


        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(TraansactionOTPActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
        String asset_Name=sslnamepref.getString("certificateassetname", null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput =  IScoreApplication.getAppContext().
                getAssets().open(asset_Name);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        Log.e("File  ","File   142   "+file);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
