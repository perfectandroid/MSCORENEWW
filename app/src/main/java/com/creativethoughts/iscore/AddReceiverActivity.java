package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

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

public class AddReceiverActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<SenderReceiver> mSenders ;
    static ArrayAdapter<SenderReceiver> senderAdapter = null;
    private AppCompatEditText mReceiverEt;
    private AppCompatEditText mMobileNumberEt;
    private AppCompatEditText mIFSCCodeEt;
    private AppCompatEditText mAccountNumberEt;
    private AppCompatEditText mConfirmAccountNumberEt;
    private ProgressDialog mProgressDialog;
    private Spinner mSenderSpinner;
    long senderid;
    private String url,cusid,msg;

    List<String> senders = new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_receiver);

        setRegViews();
        getSenderList();


    }

    private void getSenderList() {

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


                    SharedPreferences cusidpref = AddReceiverActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);



                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));


                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1 addrecvr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getSenderList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response receiver   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("GenerateSenderReceiverList");
                            JSONArray jsonArray = jobj.getJSONArray("SenderReceiverDetailedList");
                            if (jsonArray != null) {
                             //  ArrayList<SenderReceiver> senderReceivers = new ArrayList<SenderReceiver>();
                                List<String> senderList = new ArrayList< >();
                                List<String> senderItems = new ArrayList< >();
                                mSenders  = new ArrayList<>();

                             //    SenderReceiver senderReceiver = new SenderReceiver();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    if (json.getString("Mode").equals("1")) {
                                        senderList.add(json.getString("UserID"));
                                        senderList.add(json.getString("FK_SenderID"));
                                        senderList.add(json.getString("SenderMobile"));
                                        senderList.add(json.getString("ReceiverAccountno"));
                                        senderList.add(json.getString("Mode"));
                                        senderItems.add(json.getString("SenderName"));
                                        mSenders.add(new SenderReceiver( json.getLong("UserID"), json.getLong("FK_SenderID"), json.getString("SenderName"), json.getString("SenderMobile"), json.getString("ReceiverAccountno"), json.getInt("Mode")));

                                     /*  senderReceiver.userId = json.getLong("UserID");
                                        senderReceiver.fkSenderId = json.getLong("FK_SenderID");
                                        senderReceiver.senderName = json.getString("SenderName");
                                        senderReceiver.senderMobile = json.getString("SenderMobile");
                                        senderReceiver.receiverAccountno = json.getString("ReceiverAccountno");*/

                                    }

                                }

                              //  mSenderSpinner.setAdapter(new ArrayAdapter<String>(AddReceiverActivity.this, android.R.layout.simple_spinner_dropdown_item, senderReceivers));
                              // ArrayAdapter<SenderReceiver> senderReceiverArrayAdapter  = new ArrayAdapter<SenderReceiver>(AddReceiverActivity.this,  R.layout.list_content_spin, R.id.textview, senderReceivers);
                              //  mSenderSpinner.setAdapter(senderReceiverArrayAdapter);

                                if (senderItems.size()>0){
                                    senderAdapter = new ArrayAdapter<>(AddReceiverActivity.this, android.R.layout.simple_spinner_dropdown_item, mSenders);
                                    //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                    mSenderSpinner.setAdapter(senderAdapter);
                                }



                                mSenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        // TextView textView = (TextView)mAccountTypeSpinner.getSelectedView();
                                        senderid = senderAdapter.getItem(position).getUserID();



                                        //   Toast.makeText(activity,AccountAdapter.getItem(position).getBranchName(),Toast.LENGTH_LONG).show();


                                    }




                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //Do nothing
                                    }
                                });


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
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

    private void setRegViews() {

        mSenderSpinner = findViewById(R.id.sender_name_spinner);

        mReceiverEt = findViewById(R.id.receiver_name);
        mMobileNumberEt = findViewById(R.id.mobile_number);
        mIFSCCodeEt =findViewById(R.id.ifsc_code);
        mAccountNumberEt = findViewById(R.id.account_number);
        mConfirmAccountNumberEt = findViewById(R.id.confirm_account_number);

        Button button = findViewById(R.id.btn_submit);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_submit:
                if (isValid()) {
                    String receiverName = mReceiverEt.getText().toString();
                    String mobileNumber = mMobileNumberEt.getText().toString();
                    String ifscCode = mIFSCCodeEt.getText().toString();
                    String accNumber = mConfirmAccountNumberEt.getText().toString();

                    getAddReceiver(senderid,receiverName,mobileNumber,ifscCode,accNumber);
                }
                break;
        }
    }

    private void getAddReceiver(long senderid, String receiverName, String mobileNumber, String ifscCode, String accNumber) {

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


                    SharedPreferences cusidpref = AddReceiverActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);



                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(String.valueOf(senderid)));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("receiver_name", IScoreApplication.encryptStart(receiverName));
                    requestObject1.put("receiver_mobile", IScoreApplication.encryptStart(mobileNumber));
                    requestObject1.put("receiver_IFSCcode", IScoreApplication.encryptStart(ifscCode));
                    requestObject1.put("receiver_accountno", IScoreApplication.encryptStart(accNumber));
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

                    Log.e("requestObject1 addrecvr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAddReceiver(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response receivr   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statscode =jObject.getString("StatusCode");
                            msg =jObject.getString("message");
                            String otprefno =jObject.getString("otpRefNo");
                            if(statscode.equals("0"))
                            {

                            }
                            else if(statscode.equals("200")&& otprefno.equals("0"))
                            {
                                String from ="receiver";
                                Intent i = new Intent(AddReceiverActivity.this,TraansactionOTPActivity.class);
                                i.putExtra("from",from);
                                startActivity(i);
                            }
                            else if(statscode.equals("500"))
                            {
                                alertMessage1("" ,msg );

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
                            alertMessage1("" ,"Some technical issues." );
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("" ,"Some technical issues." );
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

    private boolean isValid() {

        SenderReceiver senderReceiver = ((SenderReceiver) mSenderSpinner.getSelectedItem());

     /*   if (senderReceiver == null) {
            if (mSenderReceivers.isEmpty()) {
                showToast("Please add minimum one sender, and then add receiver");
            }
            return false;
        }

        if(senderReceiver.fkSenderId == -100) {
            showToast("Please select sender");

            return false;
        }
*/
        String receiverName = mReceiverEt.getText().toString();
        String mobileNumber = mMobileNumberEt.getText().toString();
        String ifscCode = mIFSCCodeEt.getText().toString();
        String accNumber = mAccountNumberEt.getText().toString();
        String confirmAccNumber = mConfirmAccountNumberEt.getText().toString();

        if (TextUtils.isEmpty(receiverName)) {
            mReceiverEt.setError("Please enter receiver name");

            return false;
        }
        mReceiverEt.setError(null);

        if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberEt.setError("Please enter mobile number");

            return false;
        }

        if (mobileNumber.length() > 10 || mobileNumber.length() < 10) {
            mMobileNumberEt.setError("Please enter valid 10 digit mobile number");
            return false;
        }

        mMobileNumberEt.setError(null);

        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(mMobileNumberEt.getText().toString());
        }catch (Exception e){
            return false;
        }

        if (TextUtils.isEmpty(ifscCode)) {
            mIFSCCodeEt.setError("Please enter IFSC code");

            return false;
        }
        else {
            if (! isIfscIsValid(ifscCode)){
                mIFSCCodeEt.setError("Invalid ifsc");
                return false;
            }
        }
        mIFSCCodeEt.setError(null);

        if (TextUtils.isEmpty(accNumber)) {
            mAccountNumberEt.setError("Please enter account number");

            return false;
        }
        mAccountNumberEt.setError(null);

        if (TextUtils.isEmpty(confirmAccNumber)) {
            mConfirmAccountNumberEt.setError("Please enter confirm account number");

            return false;
        }

        if (!accNumber.equalsIgnoreCase(confirmAccNumber)) {
            mConfirmAccountNumberEt.setError("Account number and Confirm Account number not matching");

            return false;
        }
        mConfirmAccountNumberEt.setError(null);

        return true;

    }
    private void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    private boolean isIfscIsValid(String ifsCode){

        return ifsCode.length() > 0;
    }
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private void alertMessage1(String s, String s1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddReceiverActivity.this);

        LayoutInflater inflater = AddReceiverActivity.this.getLayoutInflater();
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
        PicassoTrustAll.getInstance(AddReceiverActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

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
