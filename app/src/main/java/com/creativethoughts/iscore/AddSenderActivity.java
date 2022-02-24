package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.AddSenderReceiverResponseModel;
import com.creativethoughts.iscore.model.FundTransferResult1;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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

public class AddSenderActivity extends AppCompatActivity implements View.OnClickListener{
    private AppCompatEditText mFirstNameEt;
    private AppCompatEditText mLastNameEt;
    private AppCompatEditText mMobileNumberEt;
    String token,cusid,pin,msg;
    private TextView mDOBTv;
    private ArrayList<AddSenderReceiverResponseModel> addSenderReceiverResponseModels = new ArrayList<AddSenderReceiverResponseModel>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_sender);

        setRegViews();
    }

    private void setRegViews() {
        mFirstNameEt = findViewById(R.id.first_name);
        mLastNameEt = findViewById(R.id.last_name);
        mMobileNumberEt = findViewById(R.id.mobile_number);

        mDOBTv = findViewById(R.id.txtDOB);
        String defaultDate = "01-01-1990";
        mDOBTv.setText( defaultDate );

        mDOBTv.setOnClickListener(this);

        Button button = findViewById(R.id.btn_submit);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId())
       {
           case R.id.txtDOB:
               showDOBDatePicker();
               break;
           case R.id.btn_submit:
               if (isValid()) {
                   String firstName = mFirstNameEt.getText().toString();
                   String lastName = mLastNameEt.getText().toString();
                   String mobileNumber = mMobileNumberEt.getText().toString();
                   String dob =mDOBTv.getText().toString();
                   getSender(firstName,lastName,mobileNumber,dob);
               }
               break;
       }
    }

    private void getSender(String firstName, String lastName, String mobileNumber, String dob) {
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


                    SharedPreferences cusidpref = AddSenderActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);



                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("sender_fname", IScoreApplication.encryptStart(firstName));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("sender_lname", IScoreApplication.encryptStart(lastName));
                    requestObject1.put("sender_dob", IScoreApplication.encryptStart(dob));
                    requestObject1.put("sender_mobile", IScoreApplication.encryptStart(mobileNumber));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));
                    Log.e("requestObject1 addsndr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAddsender(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response addsender   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statscode =jObject.getString("StatusCode");
                             msg =jObject.getString("message");
                            String otprefno =jObject.getString("otpRefNo");
                            String status =jObject.getString("Status");
                            String sender =jObject.getString("ID_Sender");
                          //  String receiver =jObject.getString("ID_Receiver");
                            String mobileno =mMobileNumberEt.getText().toString();
                          /*  AddSenderReceiverResponseModel addSenderReceiverResponseModel = new AddSenderReceiverResponseModel();


                            addSenderReceiverResponseModel.status =status;
                            addSenderReceiverResponseModel.statusCode = statscode;
                            addSenderReceiverResponseModel.senderid=sender;
                            addSenderReceiverResponseModel.mobileno=mobileno;
                            addSenderReceiverResponseModel.message=msg;
                           // addSenderReceiverResponseModel.receiverid=receiver;
                            addSenderReceiverResponseModel.otprefno=otprefno;
                            addSenderReceiverResponseModels.add(addSenderReceiverResponseModel);*/


                            SharedPreferences fromSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF50, 0);
                            SharedPreferences.Editor fromSPEditer = fromSP.edit();
                            fromSPEditer.putString("from", "sender");
                            fromSPEditer.commit();

                            SharedPreferences sndridSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF51, 0);
                            SharedPreferences.Editor sndridSPSPEditer = sndridSP.edit();
                            sndridSPSPEditer.putString("senderid", sender);
                            sndridSPSPEditer.commit();



                            SharedPreferences mobilSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF53, 0);
                            SharedPreferences.Editor mobilSPEditer = mobilSP.edit();
                            mobilSPEditer.putString("mobileno", mobileno);
                            mobilSPEditer.commit();

                            SharedPreferences otprefSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF54, 0);
                            SharedPreferences.Editor otprefSPEditer = otprefSP.edit();
                            otprefSPEditer.putString("otprefno", otprefno);
                            otprefSPEditer.commit();


                            if(statscode.equals("0"))
                            {

                            }
                           else if(statscode.equals("200")&& otprefno.equals("0"))
                            {
                               // Intent i = new Intent(AddSenderActivity.this,TraansactionOTPActivity.class);
                              //  startActivity(i);
                              /*  String from ="sender";
                                Intent i = new Intent(AddSenderActivity.this,TraansactionOTPActivity.class);
                                i.putExtra("from",from);
                                i.putExtra("otprefno",addSenderReceiverResponseModels.get(0).getOtprefno());
                                i.putExtra("sender",addSenderReceiverResponseModels.get(0).getSenderid());
                                i.putExtra("mob",addSenderReceiverResponseModels.get(0).getMobileno());
                                Log.i("sender",addSenderReceiverResponseModels.get(0).getSenderid());

                                startActivity(i);*/
                                alertMessage1("" ,msg );
                            }
                            else if(statscode.equals("200")&& !otprefno.equals("0"))
                            {
                             //   String from ="sender";
                               Intent i = new Intent(AddSenderActivity.this,TraansactionOTPActivity.class);
                             /*  i.putExtra("from",from);
                                i.putExtra("otprefno",addSenderReceiverResponseModels.get(0).getOtprefno());
                                i.putExtra("sender",addSenderReceiverResponseModels.get(0).getSenderid());
                                i.putExtra("mob",addSenderReceiverResponseModels.get(0).getMobileno());*/
                               startActivity(i);
                            }
                            else if(statscode.equals("500"))
                            {
                                alertMessage1("" ,msg );
                              /*  Intent i = new Intent(AddSenderActivity.this,TraansactionOTPActivity.class);
                                i.putExtra("from",from);
                                startActivity(i);*/
                            }
                            else
                            {
                                alertMessage1("" ,msg );
                            }
                    /*        JSONObject j1 = jObject.getJSONObject("FundTransferIntraBankList");
                            String responsemsg = j1.getString("ResponseMessage");
                            String statusmsg = j1.getString("StatusMessage");
                            int statusCode=j1.getInt("StatusCode");*/
                        /*    if(statusCode==1){
                                String refid;
                                JSONArray jArray3 = j1.getJSONArray("FundTransferIntraBankList");


                                alertMessage("", keyValuePairs, statusmsg, true, false);
                                //  JSONArray jarray = jobj.getJSONArray( "Data");

                            }
                            else if ( statusCode == 2 ){
                                alertMessage1("" ,statusmsg );
                            }
                            else if ( statusCode == 3 ){
                                alertMessage1("", statusmsg);
                            }
                            else if ( statusCode == 4 ){
                                alertMessage1("", statusmsg);
                            }
                            else  if ( statusCode == 5 ){
                                alertMessage1("", statusmsg);
                            }

                            else{


                                try{
                                  *//*  JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");*//*
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
                                    builder.setMessage(responsemsg)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (Exception e){
                                    String EXMessage = j1.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
                                    builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }
*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                            alertMessage1("" ,"Some technical issues" );
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("" ,"Some technical issues" );
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void alertMessage1(String s, String s1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddSenderActivity.this);

        LayoutInflater inflater = AddSenderActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);

        tv_msg.setText(s);
        tv_msg2.setText(s1);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(AddSenderActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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

    private void showDOBDatePicker() {

        String fromParse = mDOBTv.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null;
        try {
            date = formatter.parse(fromParse);
        } catch (ParseException e) {
            //Do nothing
        }


        final Calendar c = Calendar.getInstance();
        c.setTime(date);


        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(1990, 1,1);


        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dateOfBirth = getTwoDigitNumber(dayOfMonth) + "-" + getTwoDigitNumber(monthOfYear + 1) + "-" + year1;
                    mDOBTv.setText( dateOfBirth );
                    mDOBTv.setError(null);
                }, 1990, 0, 1);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();
    }
    private String getTwoDigitNumber(int value) {
        return new DecimalFormat("00").format(value);
    }
    private boolean isValid() {

        String firstName = mFirstNameEt.getText().toString();
        String lastName = mLastNameEt.getText().toString();
        String mobileNumber = mMobileNumberEt.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEt.setError("Please enter first name");

            return false;
        }
        mFirstNameEt.setError(null);

        if (TextUtils.isEmpty(lastName)) {
            mLastNameEt.setError("Please enter last name");

            return false;
        }
        mLastNameEt.setError(null);

        if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberEt.setError("Please enter mobile number");

            return false;
        }

        if (mobileNumber.length() > 10 || mobileNumber.length() < 10) {
            mMobileNumberEt.setError("Please enter valid 10 digit mobile number");

            return false;
        }

        mMobileNumberEt.setError(null);

        return true;
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
}

