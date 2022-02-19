package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;

import com.creativethoughts.iscore.model.Receivers;
import com.creativethoughts.iscore.model.SenderReceiver;
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

public class QuickPayMoneyTransferActivity extends AppCompatActivity implements View.OnClickListener,Spinner.OnItemSelectedListener {
    public String TAG ="QuickPayMoneyTransferActivity";
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
    private String token,cusid,msg;
    long senderid,userid1,fkuserid1,fksndrid,receiverid;
    private boolean mCanLoadSenderReceiver = false;
    public String myString = "Select";
    String reference;
    ArrayList<SenderReceiver> arrayList2 = new ArrayList<>();
    ArrayList<SenderReceiver> arrayList3 = new ArrayList<>();
    ArrayList<Receivers> arrayList4 = new ArrayList<Receivers>();


    private LinearLayout mLnrAnimatorContainer;
    private RelativeLayout mRltvError;
    private TextView mTxtError,txt_amtinword;
    String BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();
    long userId,fkSenderId;
    ArrayAdapter senderReceiverArrayAdapter = null;
    int mode,mode1;

    String senderName,senderMobile,receiveraccno,filename,accno1,sendrname1,mob1;

    private JSONArray jresultSender = new JSONArray();
    private JSONArray jresultReceiver = new JSONArray();
    private ArrayList<String> Senderlist;
    private ArrayList<String> Receiverlist;

    public static ArrayList<SenderReceiver> mSenders ;
    public static ArrayList<SenderReceiver> mReceivers ;
    public static ArrayList<Receivers> mReceivers1 ;
    static ArrayAdapter<SenderReceiver> senderAdapter = null;
    static ArrayAdapter<Receivers> receiverAdapter = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_transfer);
        
        setRegViews();

        Senderlist = new ArrayList<String>();
        accountlist = new ArrayList<String>();

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


        SharedPreferences toknpref = QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF35, 0);
        token=toknpref.getString("Token", null);

        SharedPreferences cusidpref = QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
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
                        mBtnSubmit.setText( "MAKE PAYMENT OF  "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num));
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


                    SharedPreferences cusidpref = QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
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

                            if (jObject.getString("StatusCode").equals("0")) {

                                JSONObject j1 = jObject.getJSONObject("GenerateSenderReceiverList");
                                Log.e("j1   3091   ",""+j1);
                                JSONArray jresult = j1.getJSONArray("SenderReceiverDetailedList");
                                Log.e("jresult   3092   ",""+jresult);
//                                JSONObject j1 = jObject.getJSONObject("QuickPaySenderReciver");
//                                JSONObject jsonobj2 = new JSONObject(j1.toString());

                               // JSONArray jresult = jsonobj2.getJSONArray("QuickPaySenderReciverlist");

                                List<String> meModels = new ArrayList<>();
                                List<String> receivers = new ArrayList<>();
                                List<String> receivers1 = new ArrayList<>();
                                mSenders  = new ArrayList<>();
                                mReceivers  = new ArrayList<>();
                                mReceivers1  = new ArrayList<>();
                                for (int i = 0; i < jresult.length(); i++) {

                                        JSONObject json = jresult.getJSONObject(i);



                                        if (json.getString("Mode").equals("1")) {

                                          //  jresultSender.put(json);
                                        //    Senderlist.add(json.getString("SenderName"));
                                            meModels.add(json.getString("UserID"));
                                            meModels.add(json.getString("FK_SenderID"));
                                            meModels.add(json.getString("SenderMobile"));
                                            meModels.add(json.getString("ReceiverAccountno"));
                                            meModels.add(json.getString("Mode"));
                                            meModels.add(json.getString("SenderName"));
                                            mSenders.add(new SenderReceiver( json.getLong("UserID"), json.getLong("FK_SenderID"), json.getString("SenderName"), json.getString("SenderMobile"), json.getString("ReceiverAccountno"), json.getInt("Mode")));


                                          /*  senders.userId = json.getLong("UserID");
                                            senders.fkSenderId = json.getLong("FK_SenderID");
                                            senders.senderName = json.getString("SenderName");
                                            senders.senderMobile = json.getString("SenderMobile");
                                            senders.receiverAccountno = json.getString("ReceiverAccountno");*/
                                        }
                                    if (json.getString("Mode").equals("2")) {

                                        // jresultReceiver.put(json);
                                        // Receiverlist.add(json.getString("SenderName"));
                                        receivers.add(json.getString("UserID"));
                                        receivers.add(json.getString("FK_SenderID"));
                                        receivers.add(json.getString("SenderMobile"));
                                        receivers.add(json.getString("ReceiverAccountno"));
                                        receivers.add(json.getString("Mode"));
                                        receivers.add(json.getString("SenderName"));
                                        mReceivers.add(new SenderReceiver(json.getLong("UserID"), json.getLong("FK_SenderID"), json.getString("SenderName"), json.getString("SenderMobile"), json.getString("ReceiverAccountno"), json.getInt("Mode")));
                                    }
                                      /*  arrayList2.add(senders);
                                        arrayList3.add(receivers);

                                        Log.e(TAG,"senders  337  "+senders);*/
                                        Log.e(TAG,"arrayList2  3371  "+arrayList2);
                                        Log.e(TAG,"arrayList3  3372  "+arrayList3);





                                }

                                if (meModels.size()>0){
                                    senderAdapter = new ArrayAdapter<>(QuickPayMoneyTransferActivity.this,  android.R.layout.simple_spinner_dropdown_item, mSenders);

                                    //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                    mSenderSpinner.setAdapter(senderAdapter);
                                }
                               /* if (receivers.size()>0){
                                    receiverAdapter = new ArrayAdapter<>(QuickPayMoneyTransferActivity.this,  android.R.layout.simple_spinner_dropdown_item, mReceivers);
                                    //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                    mReceiverSpinner.setAdapter(receiverAdapter);
                                }*/
                                mSenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        // TextView textView = (TextView)mAccountTypeSpinner.getSelectedView();
                                        senderid = senderAdapter.getItem(position).getUserID();
                                        fksndrid= senderAdapter.getItem(position).getFkSenderId();

                                        for (int i = 0; i < mReceivers.size(); i++) {

                                            userid1=mReceivers.get(i).getUserID();
                                            fkuserid1=mReceivers.get(i).getFkSenderId();
                                            mob1=mReceivers.get(i).getSenderMobile();
                                            accno1=mReceivers.get(i).getReceiverAccountno();
                                            mode1=mReceivers.get(i).getMode();
                                            sendrname1=mReceivers.get(i).getSenderName();



                                            if(senderid==fkuserid1)
                                            {
                                                receivers1.add(String.valueOf(userid1));
                                                receivers1.add(String.valueOf(fkuserid1));
                                                receivers1.add(mob1);
                                                receivers1.add(accno1);
                                                receivers1.add(String.valueOf(mode1));
                                                receivers1.add(sendrname1);
                                                mReceivers1.add(new Receivers( sendrname1,accno1));

                                            }

                                        }
                                        //   Toast.makeText(activity,AccountAdapter.getItem(position).getBranchName(),Toast.LENGTH_LONG).show();

                                        receiverAdapter = new ArrayAdapter<>(QuickPayMoneyTransferActivity.this,  android.R.layout.simple_spinner_dropdown_item, mReceivers1);
                                        //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                        mReceiverSpinner.setAdapter(receiverAdapter);
                                    }




                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //Do nothing
                                    }
                                });
                                mReceiverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        // TextView textView = (TextView)mAccountTypeSpinner.getSelectedView();
                                     //   receiverid = receiverAdapter.getItem(position).ge;



                                        //   Toast.makeText(activity,AccountAdapter.getItem(position).getBranchName(),Toast.LENGTH_LONG).show();


                                    }




                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //Do nothing
                                    }
                                });
                              /*  mSenderSpinner.setAdapter(new ArrayAdapter<String>(QuickPayMoneyTransferActivity.this, android.R.layout.simple_spinner_dropdown_item, Senderlist));
                                mSenderSpinner.setOnItemSelectedListener(QuickPayMoneyTransferActivity.this);*/

//                                ArrayAdapter<SenderReceiver> senderReceiverArrayAdapter = new ArrayAdapter<SenderReceiver>(getApplicationContext(), android.R.layout.simple_spinner_item, (List<SenderReceiver>) senders);
//                             //   senderReceiverArrayAdapter = new ArrayAdapter<>(getApplicationContext(),  R.layout.list_content_spin, R.id.textview, senders);
//                                mSenderSpinner.setAdapter(senderReceiverArrayAdapter);
//
//
//                                mSenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//
//
//                                        Log.e(TAG,"START  361  ");
//                                        SenderReceiver senderRec = (SenderReceiver) arrayList2.get(pos);
//
//                                        Log.e(TAG,"senderRec  3611  "+senderRec.getUserID());
////                                        userId = senderRec.getUserID();
////                                        fkSenderId = senderRec.getFkSenderId();
////                                        senderName = senderRec.getSenderName();
////                                        senderMobile = senderRec.getSenderMobile();
////                                        receiveraccno = senderRec.getReceiverAccountno();
////                                        mode=senderRec.getMode();
////                                        for (int i = 0; i < arrayList3.size(); i++) {
////                                            SenderReceiver senderRec1 = (SenderReceiver) arrayList2.get(i);
////                                            int mod =senderRec1.getMode();
////                                            long receivers =senderRec1.getUserID();
////                                            long fkuser =senderRec1.getFkSenderId();
////
////
////                                            if (userId==fkuser)
////                                            {
////                                                //  receivers1.senderName=senderRec1.getSenderName();
////                                                // receivers1.receiverAccountno=senderRec1.getReceiverAccountno();
////
////
////
////                                            }
////                                        }
//
//                                    }
//                                    public void onNothingSelected(AdapterView<?> parent) {
//                                    }
//                                });


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

                        } catch (Exception e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                            Log.e(TAG,"Exception   449    "+e.toString());
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

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode", IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
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

            case R.id.btn_submit:
                QuickConfirmation();
               /* SharedPreferences pref1 =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                String pin=pref1.getString("pinlog", null);
                QuickPayTransfer("001001001510", "466677", "4556", "100", "TEST", pin,"hina","0010015855","9656789056","ananya","00100","8656789021","headoffice");*/
                break;
            case R.id.btn_forgot_mpin:
                if ( fksndrid == -100 ){
                    Toast.makeText(this, "Please select sender", Toast.LENGTH_LONG).show();
                    return;
                }
                forgotMpin(senderid);
                break;
        }
    }

    private void forgotMpin(long senderid) {

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


                    SharedPreferences cusidpref = QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);



                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart("7864"));
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
                    Log.e("requestObjectforgotmpin",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getForgotMpin(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response forgotMpin   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statscode =jObject.getString("StatusCode");
                            msg =jObject.getString("EXMessage");
                            if(statscode.equals("1"))
                            {
                                alertMessage1("" ,"M-Pin is resend to your mobile." );
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
                            alertMessage1("" ,msg );
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        alertMessage1("" ,msg );
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
                alertMessage1("" ,"Some technical issues." );
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void QuickConfirmation() {

        if (isValid()) {
            if (NetworkUtil.isOnline()) {


                try {

                    final String amount = mAmountEt.getText().toString().replaceAll(",","");
                    final SenderReceiver receiverObj = ((SenderReceiver) mReceiverSpinner.getSelectedItem());
                    final String accountNumber = mAccountSpinner.getSelectedItem().toString();


                    String message = mMessageEt.getText().toString();

                    SenderReceiver senderObj = ((SenderReceiver) mSenderSpinner.getSelectedItem());


                    String sender = String.valueOf(senderObj.userId);
                    String senderName = String.valueOf(senderObj.senderName);
                    String senderAccountno = String.valueOf(senderObj.receiverAccountno);
                    String senderMobile = String.valueOf(senderObj.senderMobile);


                    String recievererName = String.valueOf(receiverObj.senderName);
                    String receiverAccountno = String.valueOf(receiverObj.receiverAccountno);
                    String recieverMobile = String.valueOf(receiverObj.senderMobile);

                    String receiver = String.valueOf(receiverObj.userId);

                    String mPinString = mPin.getText().toString().trim();
                    String branch = BranchName;

                    AlertDialog.Builder builder = new AlertDialog.Builder(QuickPayMoneyTransferActivity.this);
                    LayoutInflater inflater1 = this.getLayoutInflater();
                    View layout = inflater1.inflate(R.layout.quick_pay_confirmation_popup, null);
                    TextView tvbranch =  layout.findViewById(R.id.tvbranch);
                    TextView tv_sender_name =  layout.findViewById(R.id.tv_sender_name);
                    TextView tv_sender_acc_no =  layout.findViewById(R.id.tv_sender_acc_no);
                    TextView tv_sender_mob_no =  layout.findViewById(R.id.tv_sender_mob_no);
                    TextView tv_reciever_name =  layout.findViewById(R.id.tv_reciever_name);
                    TextView tv_reciever_acc_no =  layout.findViewById(R.id.tv_reciever_acc_no);
                    TextView tv_reciever_mob_no =  layout.findViewById(R.id.tv_reciever_mob_no);


                    TextView tv_amount =  layout.findViewById(R.id.tv_amount);
                    TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
                    TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
                    TextView bt_ok =  layout.findViewById(R.id.bt_ok);
                    TextView bt_cancel =  layout.findViewById(R.id.bt_cancel);
                    ImageView img_applogo = layout.findViewById(R.id.img_aapicon);

                    builder.setView(layout);
                    final AlertDialog alertDialog = builder.create();

                    SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                    String IMAGEURL = imageurlSP.getString("imageurl","");
                    SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                    String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
                    PicassoTrustAll.getInstance(QuickPayMoneyTransferActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

                    tvbranch.setText(BranchName);
                    tv_sender_name.setText(senderName);
                    tv_sender_acc_no.setText(accountNumber);
                    tv_sender_mob_no.setText(senderMobile);
                    tv_reciever_name.setText(recievererName);
                    tv_reciever_acc_no.setText(receiverAccountno);
                    tv_reciever_mob_no.setText(recieverMobile);

                    double num =Double.parseDouble(""+amount);
                    String stramnt = CommonUtilities.getDecimelFormate(num);
                    text_confirmationmsg.setText("Proceed Payment With Above Amount"+ "..?");
                    String[] netAmountArr = amount.split("\\.");
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
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                             QuickPayTransfer(accountNumber, sender, receiver, amount, message, mPinString,senderName,senderAccountno,senderMobile,recievererName,receiverAccountno,recieverMobile,branch);
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DialogUtil.showAlert(this,
                        "Network is currently unavailable. Please try again later.");
            }
        }
    }

    private void QuickPayTransfer(String accountNumber, String sender, String receiver, String amount, String message, String mPinString, String senderName, String senderAccountno, String senderMobile, String recievererName, String receiverAccountno, String recieverMobile, String branch) {




        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        SharedPreferences pref1 =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
        String pin=pref1.getString("pinlog", null);
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


                    SharedPreferences cusidpref = QuickPayMoneyTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);
                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");
                    String iemi =   IScoreApplication.getIEMI();
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);


//                    CID    : 86111
//                    UID    : 6918
//                    NAME   : AKHIL PV
//                    MOBILE : 7293132504

                //    requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart(sender));
                    requestObject1.put("receiverid", IScoreApplication.encryptStart(receiver) );
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("amount", IScoreApplication.encryptStart(amount));
                    requestObject1.put("Messages", IScoreApplication.encryptStart(message));
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart(accountNumber));
                    requestObject1.put("Module", IScoreApplication.encryptStart("SB"));
                   requestObject1.put("Pin", IScoreApplication.encryptStart(pin));
                   requestObject1.put("MPIN", IScoreApplication.encryptStart(senderMobile));

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));




//                    {"senderid":"6918","receiverid":"10920","FK_Customer":"86111","amount":"10","Messages":"test","AccountNo":"001001234567",
//                            "Module":"SB","Pin":"123456","MPIN":"12345","imei":"1ba6f19bfae2630a","token":"55F6AA8D-8B29-4756-9153-1428E31EA13B","BankKey":"d.22333",
//                            "BankHeader":"Perfect","BankVerified":""}
//

//                    requestObject1.put("senderid", IScoreApplication.encryptStart("6918"));
//                    requestObject1.put("receiverid", IScoreApplication.encryptStart("10920") );
//                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart("86111"));
//                    requestObject1.put("amount", IScoreApplication.encryptStart("1"));
//                    requestObject1.put("Messages", IScoreApplication.encryptStart("TEST"));
//                    requestObject1.put("AccountNo", IScoreApplication.encryptStart("001001999311"));
//                    requestObject1.put("Module", IScoreApplication.encryptStart("SB"));
//                    requestObject1.put("Pin", IScoreApplication.encryptStart("123456"));
//                    requestObject1.put("MPIN", IScoreApplication.encryptStart("123456"));
//                    requestObject1.put("imei", IScoreApplication.encryptStart(iemi));
//                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
//                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
//                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                 /*   requestObject1.put("senderid", IScoreApplication.encryptStart("3702"));
                    requestObject1.put("receiverid", IScoreApplication.encryptStart("11211") );
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart("86112"));
                    requestObject1.put("amount", IScoreApplication.encryptStart("1"));
                    requestObject1.put("Messages", IScoreApplication.encryptStart("TEST"));
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart("001001999311"));
                    requestObject1.put("Module", IScoreApplication.encryptStart("SB"));
                    requestObject1.put("Pin", IScoreApplication.encryptStart("123456"));
                    requestObject1.put("MPIN", IScoreApplication.encryptStart("123456"));
                    requestObject1.put("imei", IScoreApplication.encryptStart(iemi));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));*/

                    Log.e("requestObject1 addsndr"," 10311    "+requestObject1);
                /*    Log.e(TAG,"QUICK PAY  10311    "
                            +"\n"+"senderid    "+"6918"
                            +"\n"+"receiverid    "+"10920"
                            +"\n"+"FK_Customer    "+"86111"
                            +"\n"+"cusid    "+cusid
                            +"\n"+"amount    "+"1"
                            +"\n"+"Messages    "+"test"
                            +"\n"+"AccountNo    "+"001001999311"
                            +"\n"+"Module   "+"SB"
                            +"\n"+"Pin    "+"123456"
                            +"\n"+"MPIN    "+"123456");*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getMoneytransferPayment(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response QUICKPAY 10312  "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statusCode=jObject.getString("StatusCode");
                            String statusmsg = jObject.getString("StatusMessage");
                            String otpref = jObject.getString("otpRefNo");

                            if ( statusCode!= null && statusCode.equals("200") &&!otpref.equals("0")){
                                String from ="quickpay";
                                Intent i = new Intent(QuickPayMoneyTransferActivity.this,TraansactionOTPActivity.class);
                                i.putExtra("from",from);
                                startActivity(i);

                            }
                            if ( statusCode!= null && statusCode.equals("200") &&!otpref.equals("0")){

                            }
                            else if ( statusCode!= null && statusCode.equals("200")){
                                 //   moneyTransferResponseModel.getOtpRefNo().equals("0")){

                               /* QuickSuccess(mAccNo,moneyTransferResponseModel.getStatus(),moneyTransferResponseModel.getMessage(),"",
                                        moneyTransferResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch,mAmount);*/

                              //  Log.e(TAG,"10912   "+moneyTransferResponseModel.getMessage());

                            }
                            else if (  statusCode.equals("500")){
                                Intent i = new Intent(QuickPayMoneyTransferActivity.this,TraansactionOTPActivity.class);
                                startActivity(i);

                              //  alertMessage1(statusmsg, statusmsg);
                            }
                            else {
//
                                alertMessage1("Oops....!", "Something went wrong");
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

    private boolean isValid() {

        String amount = mAmountEt.getText().toString();


        if (TextUtils.isEmpty(amount)) {
            mAmountEt.setError("Please enter the amount");
            return false;
        }
        double amt;
        try{
            amt = Double.parseDouble(amount.replaceAll(",",""));
        }catch (Exception e){
            mAmountEt.setError("Invalid format");
            return false;
        }

        if(amt < 1) {
            mAmountEt.setError("Please enter the amount");
            return false;
        }

        mAmountEt.setError(null);

        String mPinString = mPin.getText().toString();
        if ( mPinString.trim().length() == 0 ){
            mPin.setError("Please enter the M-PIN");
            return false;
        }
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

    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(QuickPayMoneyTransferActivity.this);

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
        PicassoTrustAll.getInstance(QuickPayMoneyTransferActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        try {
            JSONObject json = jresultSender.getJSONObject(position);
            Log.e(TAG,"   1287    "+json.getString("SenderName")+"   "+json.getString("SenderMobile")+"   ");

        }catch (Exception e){
            Log.e(TAG,"Exception 1287   "+e.toString());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
