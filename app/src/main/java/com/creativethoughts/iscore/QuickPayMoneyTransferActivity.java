package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.model.Receivers;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.money_transfer.QuickPayMoneyTransferFragment;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
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

public class QuickPayMoneyTransferActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = QuickPayMoneyTransferFragment.class.getSimpleName();
    private final ArrayList<SenderReceiver> mSenderReceivers = new ArrayList<>();
    private Button mBtnSubmit;
    private Spinner mAccountSpinner;
    private AppCompatEditText mAmountEt;
    private AppCompatEditText mMessageEt;
    private AppCompatEditText mPin;
    private ProgressDialog mProgressDialog;
    private Spinner mSenderSpinner;
    private Spinner mReceiverSpinner;
    private String mOtpResendLink;
    private String token,cusid;
    private boolean mCanLoadSenderReceiver = false;
    String reference;
    ArrayList<SenderReceiver> arrayList2 = new ArrayList<>();
    ArrayList<SenderReceiver> arrayList3 = new ArrayList<>();
    ArrayList<Receivers> arrayList4 = new ArrayList<Receivers>();
    SenderReceiver senders = new SenderReceiver();
    SenderReceiver receivers = new SenderReceiver();
    SenderReceiver receivers1 = new SenderReceiver();

    private LinearLayout mLnrAnimatorContainer;
    private RelativeLayout mRltvError;
    private TextView mTxtError,txt_amtinword;
    String BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();
    ArrayAdapter senderReceiverArrayAdapter = null;
    long userId,fkSenderId;

    int mode;
    String senderName,senderMobile,receiveraccno;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_transfer);
        
        setRegViews();
    }

    private void setRegViews() {

        mSenderSpinner = findViewById(R.id.sender_spinner);
        mReceiverSpinner = findViewById(R.id.receiver_spinner);
        txt_amtinword= findViewById(R.id.txt_amtinword);
        mAccountSpinner = findViewById(R.id.spn_account_num);

        TextView mAddNewSender = findViewById(R.id.add_new_sender);
        TextView mAddNewReceiver = findViewById(R.id.add_new_receiver);

        mAmountEt = findViewById(R.id.amount);
        mMessageEt = findViewById(R.id.message);
        mPin = findViewById( R.id.mpin );

        mLnrAnimatorContainer = findViewById( R.id.linear_animation_container );
        mRltvError = findViewById( R.id.rltv_error );
        mTxtError  = findViewById( R.id.txt_error );

        mAddNewSender.setPaintFlags(mAddNewSender.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mAddNewReceiver.setPaintFlags(mAddNewSender.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAddNewSender.setOnClickListener(this);
        mAddNewReceiver.setOnClickListener(this);


        SharedPreferences toknpref =QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF35, 0);
        token=toknpref.getString("Token", null);

        SharedPreferences cusidpref =QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
        cusid=cusidpref.getString("customerId", null);


        setAccountNumber();
        getSenderReceiver();

        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);

        Button mBtnForgotMpin = findViewById(R.id.btn_forgot_mpin);
        mBtnForgotMpin.setOnClickListener( this );

        mAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mAmountEt.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Double longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Double.parseDouble(originalString);
                    String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    mAmountEt.setText(formattedString);
                    mAmountEt.setSelection(mAmountEt.getText().length());


                    String amnt = mAmountEt.getText().toString().replaceAll(",", "");
                    String[] netAmountArr = amnt.split("\\.");
                    String amountInWordPop = "";
                    if ( netAmountArr.length > 0 ){
                        int integerValue = Integer.parseInt( netAmountArr[0] );
                        amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
                        if ( netAmountArr.length > 1 ){
                            int decimalValue = Integer.parseInt( netAmountArr[1] );
                            if ( decimalValue != 0 ){
                                amountInWordPop += " and " + NumberToWord.convertNumberToWords( decimalValue ) + " paise" ;
                            }
                        }
                        amountInWordPop += " only";
                    }
                    txt_amtinword.setText(""+amountInWordPop);


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                mAmountEt.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() != 0) {
                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        mBtnSubmit.setText( "MAKE PAYMENT OF  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        mBtnSubmit.setText( "MAKE PAYMENT");
                    }
                }
                catch(NumberFormatException e)
                {

                }


              /*  if(s.length() != 0) {
//                    mBtnSubmit.setText( "MAKE PAYMENT OF "+"\u20B9 "+mAmountEt.getText());
                    double num =Double.parseDouble(""+mAmountEt.getText());
                    mBtnSubmit.setText( "MAKE PAYMENT OF "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                }
                else{
                    mBtnSubmit.setText( "MAKE PAYMENT ");
                }*/
            }
        });

    }

    private void getSenderReceiver() {
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


                    SharedPreferences cusidpref =QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
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

                    Log.e("requestObjectsndrrcvr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getSenderList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                                Log.e("TAG","Response ownaccount   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            if (jObject.getString("StatusCode") == "0") {

                                JSONObject j1 = jObject.getJSONObject("QuickPaySenderReciver");
                                JSONObject jsonobj2 = new JSONObject(j1.toString());

                                JSONArray jresult = jsonobj2.getJSONArray("QuickPaySenderReciverlist");


                                for (int i = 0; i < jresult.length(); i++) {

                                        JSONObject json = jresult.getJSONObject(i);

                                        if (json.getString("Mode").equals("1")) {

                                            senders.userId = json.getLong("UserID");
                                            senders.fkSenderId = json.getLong("FK_SenderID");
                                            senders.senderName = json.getString("SenderName");
                                            senders.senderMobile = json.getString("SenderMobile");
                                            senders.receiverAccountno = json.getString("ReceiverAccountno");
                                        }
                                        if (json.getString("Mode").equals("2")) {

                                            receivers.userId = json.getLong("UserID");
                                            receivers.fkSenderId = json.getLong("FK_SenderID");
                                            receivers.senderName = json.getString("SenderName");
                                            receivers.senderMobile = json.getString("SenderMobile");
                                            receivers.receiverAccountno = json.getString("ReceiverAccountno");
                                        }
                                        arrayList2.add(senders);
                                        arrayList3.add(receivers);





                                }
                               // senderReceiverArrayAdapter = new ArrayAdapter<SenderReceiver>(QuickPayMoneyTransferActivity.this,R.layout.list_content_spin, R.id.textview, senders);
                                //mSenderSpinner.setAdapter(senderReceiverArrayAdapter);

                                mSenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                        SenderReceiver senderRec = (SenderReceiver) arrayList2.get(pos);
                                         userId = senderRec.getUserID();
                                         fkSenderId = senderRec.getFkSenderId();
                                         senderName = senderRec.getSenderName();
                                         senderMobile = senderRec.getSenderMobile();
                                        receiveraccno = senderRec.getReceiverAccountno();
                                        mode=senderRec.getMode();
                                        for (int i = 0; i < arrayList3.size(); i++) {
                                            SenderReceiver senderRec1 = (SenderReceiver) arrayList2.get(i);
                                            int mod =senderRec1.getMode();
                                            long receivers =senderRec1.getUserID();
                                            long fkuser =senderRec1.getFkSenderId();


                                            if (userId==fkuser)
                                            {
                                              //  receivers1.senderName=senderRec1.getSenderName();
                                               // receivers1.receiverAccountno=senderRec1.getReceiverAccountno();



                                            }
                                        }

                                    }
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
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

    private void setAccountNumber() {
        settingAccountNumber(cusid);
    }

    private void settingAccountNumber(String cusid) {
        if ( cusid != null )
//            CommonUtilities.transactionActivitySetAccountNumber(customerId, mAccountSpinner, getActivity());
            getAccList();

    }
    private void getAccList() {

        if (NetworkUtil.isOnline()) {
            try {
                SharedPreferences pref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL=pref.getString("baseurl", null);
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
                 /*   UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;*/
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray newJArray = object.getJSONArray("OwnAccountdetailsList");

                                for(int i=0;i<newJArray.length();i++){
                                    try {
                                        JSONObject json = newJArray.getJSONObject(i);
//                                        if (json.getString("IsShowBalance").equals("1")){
                                        jresult.put(json);
                                        accountlist.add(json.getString("AccountNumber"));
//                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(QuickPayMoneyTransferActivity.this, android.R.layout.simple_spinner_dropdown_item, accountlist));

                                mAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            BranchName = jresult.getJSONObject(position).getString("BranchName");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                })
                                ;
                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));


                            }
                            else {

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuickPayMoneyTransferActivity.this);
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuickPayMoneyTransferActivity.this);
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
                        }
                        catch (Exception e) {
                            Log.e(TAG,"Exception  572   "+e.toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"Exception  578  "+e.toString());
            }
        }
        else {
            DialogUtil.showAlert(this,"Network is currently unavailable. Please try again later.");
        }

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.add_new_sender:
                Intent i = new Intent(QuickPayMoneyTransferActivity.this, AddSenderActivity.class);
                startActivity(i);
                break;
            case R.id.add_new_receiver:
                Intent in = new Intent(QuickPayMoneyTransferActivity.this, AddReceiverActivity.class);
                startActivity(in);
                break;
        }
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
    private void alertMessage1(String s, String s1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(QuickPayMoneyTransferActivity.this);

        LayoutInflater inflater = QuickPayMoneyTransferActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(s);
        tv_msg2.setText(s1);
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

}
