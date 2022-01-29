package com.creativethoughts.iscore.money_transfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.AddSenderReceiverActivity;
import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.OwnAccountFundTransferActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.TransactionOTPFragment;
import com.creativethoughts.iscore.adapters.SenderReceiverSpinnerAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickPayMoneyTransferFragment extends Fragment implements View.OnClickListener {

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
    private boolean mCanLoadSenderReceiver = false;
    String reference;
    private LinearLayout mLnrAnimatorContainer;
    private RelativeLayout mRltvError;
    private TextView mTxtError,txt_amtinword;
    String BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();

    public QuickPayMoneyTransferFragment() {
        // Required empty public constructor
    }

    public static QuickPayMoneyTransferFragment newInstance() {

        return new QuickPayMoneyTransferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_money_transfer, container, false);

        mSenderSpinner = view.findViewById(R.id.sender_spinner);
        mReceiverSpinner = view.findViewById(R.id.receiver_spinner);
        txt_amtinword= view.findViewById(R.id.txt_amtinword);
        mAccountSpinner = view.findViewById(R.id.spn_account_num);

        TextView mAddNewSender = view.findViewById(R.id.add_new_sender);
        TextView mAddNewReceiver = view.findViewById(R.id.add_new_receiver);

        mAmountEt = view.findViewById(R.id.amount);
        mMessageEt = view.findViewById(R.id.message);
        mPin = view.findViewById( R.id.mpin );

        mLnrAnimatorContainer = view.findViewById( R.id.linear_animation_container );
        mRltvError = view.findViewById( R.id.rltv_error );
        mTxtError  = view.findViewById( R.id.txt_error );

        mAddNewSender.setPaintFlags(mAddNewSender.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mAddNewReceiver.setPaintFlags(mAddNewSender.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAddNewSender.setOnClickListener(this);
        mAddNewReceiver.setOnClickListener(this);

        setAccountNumber();

        mBtnSubmit = view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);

        Button mBtnForgotMpin = view.findViewById(R.id.btn_forgot_mpin);
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
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new SenderReceiverAsyncTask().execute();
    }

    private void setSenderSpinner() {
        Activity activity = getActivity();
        if ( activity == null )
            return;
        final SenderReceiverSpinnerAdapter senderAdapter = new SenderReceiverSpinnerAdapter( activity );
        senderAdapter.setIsSender(true);
        final SenderReceiverSpinnerAdapter receiverAdapter = new SenderReceiverSpinnerAdapter( activity );
        receiverAdapter.setIsSender(false);

        ArrayList<SenderReceiver> senders = new ArrayList<>();
        ArrayList<SenderReceiver> receivers = new ArrayList<>();

        final SenderReceiver defaultSelect = new SenderReceiver();
        defaultSelect.senderName = "Select";
        defaultSelect.fkSenderId = -100;

        senders.add(defaultSelect);
        receivers.add(defaultSelect);

        for (int i = 0; i < mSenderReceivers.size(); i++) {
            final SenderReceiver senderReceiver = mSenderReceivers.get(i);

            if (senderReceiver == null) {
                continue;
            }

            if (senderReceiver.mode == 1) {
                senders.add(senderReceiver);
            } else {
                receivers.add(senderReceiver);
            }
        }

        senderAdapter.addItems(senders);
        receiverAdapter.addItems(receivers);

        mSenderSpinner.setAdapter(senderAdapter);
        mReceiverSpinner.setAdapter(receiverAdapter);

        mSenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SenderReceiver senderRec = (SenderReceiver) senderAdapter.getItem(position);

                ArrayList<SenderReceiver> newSenders = new ArrayList<>();

                newSenders.add(defaultSelect);
                for (int i = 0; i < mSenderReceivers.size(); i++) {
                    final SenderReceiver senderReceiver = mSenderReceivers.get(i);

                    if (senderReceiver == null) {
                        continue;
                    }

                    if (senderReceiver.mode == 2 &&  senderRec.userId == senderReceiver.fkSenderId) {
                        newSenders.add(senderReceiver);
                    }
                }

                receiverAdapter.addItems(newSenders);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCanLoadSenderReceiver) {
            mCanLoadSenderReceiver = false;

            new SenderReceiverAsyncTask().execute();
        }
    }

    private void setAccountNumber() {

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        if (settingsModel == null) {

            settingAccountNumber(null);
        } else {

            settingAccountNumber(settingsModel.customerId);
        }
    }
    private void settingAccountNumber(String customerId){
        if ( customerId != null )
//            CommonUtilities.transactionActivitySetAccountNumber(customerId, mAccountSpinner, getActivity());
            getAccList();
    }


    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
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

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
    private void getAccList() {

        if (NetworkUtil.isOnline()) {
            try {
                SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
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
                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));

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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            DialogUtil.showAlert(getContext(),"Network is currently unavailable. Please try again later.");
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
        int id = v.getId();
        if ( getContext() == null ){
            return;
        }

        switch (id) {
            case R.id.add_new_sender:
                mCanLoadSenderReceiver = true;
                AddSenderReceiverActivity.openActivity(getActivity(), true);
                break;
            case R.id.add_new_receiver:
                mCanLoadSenderReceiver = true;
                AddSenderReceiverActivity.openActivity(getActivity(), false);
                break;
            case R.id.btn_submit:
                QuickConfirmation();
                break;
            case R.id.btn_forgot_mpin:
                forgotMpin();
            break;
            default:
                break;
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater1 = (LayoutInflater) getContext().getApplicationContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
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
                    builder.setView(layout);
                    final AlertDialog alertDialog = builder.create();

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



                            new MoneyTransferAsyncTask(accountNumber, sender, receiver, amount, message, mPinString,senderName,senderAccountno,senderMobile,recievererName,receiverAccountno,recieverMobile,branch).execute();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DialogUtil.showAlert(getActivity(),
                        "Network is currently unavailable. Please try again later.");
            }
        }


    }

    private void forgotMpin(){
        SenderReceiver senderObj = ((SenderReceiver) mSenderSpinner.getSelectedItem());
        if ( senderObj.fkSenderId == -100 ){
            Toast.makeText(getActivity(), "Please select sender", Toast.LENGTH_LONG).show();
            return;
        }
        String sender = String.valueOf(senderObj.userId);
        new ChangeMpinAsync(sender).execute();
    }

    private void showToast(String value) {
        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
    }

    private boolean isValid() {

        SenderReceiver sender = ((SenderReceiver) mSenderSpinner.getSelectedItem());

        if (sender == null) {
            showToast("Please add minimum one sender");
            return false;
        }

        if(sender.fkSenderId == -100) {
            showToast("Please select sender");

            return false;
        }

        SenderReceiver receiver = ((SenderReceiver) mReceiverSpinner.getSelectedItem());

        if (receiver == null) {
            showToast("Please add minimum one receiver");
            return false;
        }

        if(receiver.fkSenderId == -100) {
            showToast("Please select receiver");

            return false;
        }

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

    private String transferMoney(String accountNo, String sender, String receiver, String amount, String message, String mPinString) {

        try {



            UserCredential loginCredential = UserCredentialDAO.getInstance().getLoginCredential();

            UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

            String custId = user.customerId;


            /*Extract account number*/
            accountNo = accountNo.replace(accountNo.substring(accountNo.indexOf(" (")+1, accountNo.indexOf(")")+1), "");
            accountNo = accountNo.replace(" ","");
            AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accountNo);
            String accountType = accountInfo.accountTypeShort;
            /*End of Extract account number*/

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            String url =
                    BASE_URL +
                            "/MoneyTransferPayment?senderid=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(sender.trim()))
                            + "&receiverid=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(receiver.trim()))
                            + "&IDCustomer=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId))
                            + "&amount=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(amount.trim()))
                            + "&Messages=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(message.trim()))
                            + "&AccountNo=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountNo))
                            + "&Module=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType ))
                    + "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin))
                    +"&MPIN="+ IScoreApplication.encodedUrl(mPinString);
            mOtpResendLink = url;
            Log.e(TAG, "url : " + url);
            if (IScoreApplication.DEBUG)
                Log.d(TAG, "url : " + url);

            return ConnectionUtil.getResponse(url);


        } catch (Exception e) {
            if ( IScoreApplication.DEBUG ){
                Log.d("payment_error", e.toString() );
            }
            return e.toString();
        }

    }



    private class SenderReceiverAsyncTask extends AsyncTask<String, android.R.integer, ArrayList<SenderReceiver>> {


        @Override
        protected void onPreExecute() {
            mBtnSubmit.setEnabled(false);
            mProgressDialog = ProgressDialog
                    .show(getActivity(), "", "Fetching Sender and Receiver...");
        }

        @Override
        protected ArrayList<SenderReceiver> doInBackground(String... params) {

            UserDetails user = UserDetailsDAO.getInstance().getUserDetail();

            String custId = user.customerId;

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            String url ;
            try {
                url = BASE_URL +
                        "/GenerateSenderReceiverList?ID_Customer=" +
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(custId));
            } catch (Exception e) {
                url = BASE_URL +
                        "/GenerateSenderReceiverList?";

            }

            Log.e(TAG,"url  888   "+url);

            String strResponse = ConnectionUtil.getResponse(url);


            if (!TextUtils.isEmpty(strResponse)) {

                if (!IScoreApplication.containAnyKnownException(strResponse)) {

                    try {
                        JSONObject jsonObject = new JSONObject(strResponse);

                        JSONArray jsonArray = jsonObject.optJSONArray("senderreciverlist");

                        if (jsonArray != null) {
                            ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject senRecObj = jsonArray.optJSONObject(i);

                                if (senRecObj == null) {
                                    continue;
                                }

                                SenderReceiver senderReceiver = new SenderReceiver();

                                senderReceiver.userId = senRecObj.optLong("UserID");
                                senderReceiver.fkSenderId = senRecObj.optLong("FK_SenderID");
                                senderReceiver.senderName = senRecObj.optString("SenderName");
                                senderReceiver.senderMobile = senRecObj.optString("SenderMobile");
                                senderReceiver.receiverAccountno = senRecObj.optString("ReceiverAccountno");

                                String modeStr = senRecObj.optString("Mode");

                                if (!TextUtils.isEmpty(modeStr)) {
                                    modeStr = modeStr.trim();

                                    if (TextUtils.isDigitsOnly(modeStr)) {
                                        senderReceiver.mode = Integer.parseInt(modeStr);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }

                                senderReceivers.add(senderReceiver);
                            }

                            return senderReceivers;
                        }
                    } catch (JSONException e) {
                       if ( IScoreApplication.DEBUG )
                           Log.e("Ex", e.toString() );
                    }
                }else{
                    int flag= IScoreApplication.getFlagException(strResponse);
                    ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                    SenderReceiver senderReceiver = new SenderReceiver();
                    senderReceiver.checkError =flag;
                    return senderReceivers;
                }
            }else{

                ArrayList<SenderReceiver> senderReceivers = new ArrayList<>();
                SenderReceiver senderReceiver = new SenderReceiver();
                senderReceiver.checkError = -50800;
                return senderReceivers;

            }

            return null;
        }

        private SenderReceiverAsyncTask() {

        }

        @Override
        protected void onPostExecute(ArrayList<SenderReceiver> result) {
            mBtnSubmit.setEnabled(true);
            mProgressDialog.dismiss();


            mSenderReceivers.clear();

            //noinspection StatementWithEmptyBody
            if ( (result == null || result.isEmpty()) && getContext() != null ) {
                /*new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No list found. Please add sender")
                        .show();*/
                showErrorAnimation( "No list found. Please add sender" );
            } else {
                if (result.get(0).checkError != -50800) {
                    if (IScoreApplication.checkPermissionIemi(result.get(0).checkError, getActivity())) {
                        mSenderReceivers.addAll(result);
                    }
                }else {
                    DialogUtil.showAlert(getActivity(),
                            IScoreApplication.MSG_EXCEPTION_NETWORK);
                }
            }
            setSenderSpinner();
        }

        private void showErrorAnimation( String message ){
            if ( Build.VERSION.SDK_INT > 18 ){
                TransitionManager.beginDelayedTransition( mLnrAnimatorContainer );
            }

            mTxtError.setText( message );
            mRltvError.setVisibility( View.VISIBLE  );

            new CountDownTimer( 5000, 1000){
                public void onTick( long milisUntilFinished ){
                    //Do nothing
                }

                @Override
                public void onFinish() {
                    if ( Build.VERSION.SDK_INT > 18 ){
                        TransitionManager.beginDelayedTransition( mLnrAnimatorContainer );
                    }
                    mRltvError.setVisibility( View.GONE );
                }
            }.start();
        }
    }

    private class MoneyTransferAsyncTask extends AsyncTask<String, android.R.integer, String> {
        private final String mAccNo;
        private final String mSender;
        private final String mReceiver;
        private final String mAmount;
        private final String mMessage;
        private final String mPinString;
        private final String msenderName;
        private final String msenderAccountno;
        private final String  msenderMobile;
        private final String mrecievererName;
        private final String mreceiverAccountno;
        private final String mrecieverMobile;
        private final String mbranch;

        private MoneyTransferAsyncTask(String accountNo, String sender, String receiver, String amount, String message, String mPinString, String senderName, String senderAccountno, String senderMobile, String recievererName, String receiverAccountno, String recieverMobile, String branch) {
            mAccNo = accountNo;
            mSender = sender;
            mReceiver = receiver;
            mAmount = amount;
            mMessage = message;
            msenderName = senderName;
            msenderAccountno = senderAccountno;
            msenderMobile = senderMobile;
            mrecievererName = recievererName;
            mreceiverAccountno = receiverAccountno;
            mrecieverMobile = recieverMobile;
            mbranch = branch;
            this.mPinString = mPinString;

        }



        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show(getActivity(), "", "Transferring Amount...");
            mBtnSubmit.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {

            return transferMoney(mAccNo, mSender, mReceiver, mAmount, mMessage, mPinString);
        }

        @Override
        protected void onPostExecute(String result) {

            if ( getContext() == null ){
                if ( IScoreApplication.DEBUG )
                    Log.d("getcontext_error", "No context ");
                return;
            }

            mProgressDialog.dismiss();
            mBtnSubmit.setEnabled(true);

            mAmountEt.setText("");
            mMessageEt.setText("");
            mPin.setText("");

            setAccountNumber();
            setSenderSpinner();

            if ( result.isEmpty() || result.equals( IScoreApplication.SERVICE_NOT_AVAILABLE ) ){
//                new SweetAlertDialog( getContext(), SweetAlertDialog.ERROR_TYPE )
//                        .setTitleText( "Oops....!" )
//                        .setContentText( IScoreApplication.SERVICE_NOT_AVAILABLE )
//                        .show();
                alertMessage1("Oops....!", IScoreApplication.SERVICE_NOT_AVAILABLE);

            }

            try{

                Log.e("TAG","result  1072  "+result);
                Gson gson = new Gson();
                MoneyTransferResponseModel moneyTransferResponseModel = gson.fromJson( result, MoneyTransferResponseModel.class );
                reference = moneyTransferResponseModel.getTransactionId();

                moneyTransferResponseModel.setmAccNo( mAccNo  );
                moneyTransferResponseModel.setMsenderName( msenderName  );
                moneyTransferResponseModel.setMsenderMobile( msenderMobile  );
                moneyTransferResponseModel.setMreceiverAccountno( mreceiverAccountno  );
                moneyTransferResponseModel.setMrecievererName( mReceiver  );
                moneyTransferResponseModel.setMrecieverMobile( mrecieverMobile  );
                moneyTransferResponseModel.setMbranch( mbranch  );
                moneyTransferResponseModel.setmAmount( mAmount  );



                if ( moneyTransferResponseModel.getStatusCode() != null && moneyTransferResponseModel.getStatusCode().equals("200") &&
                        !moneyTransferResponseModel.getOtpRefNo().equals("0")){
                    TransactionOTPFragment.openTransactionOTP(getActivity(), mSender, mReceiver,
                            moneyTransferResponseModel.getTransactionId(), new AddSenderReceiverResponseModel(),
                            moneyTransferResponseModel.getOtpRefNo(), mOtpResendLink);
                    Log.e(TAG,"1091   "+moneyTransferResponseModel.getMessage());
                }
                else if ( moneyTransferResponseModel.getStatusCode() != null && moneyTransferResponseModel.getStatusCode().equals("200") &&
                        moneyTransferResponseModel.getOtpRefNo().equals("0")){

                     QuickSuccess(mAccNo,moneyTransferResponseModel.getStatus(),moneyTransferResponseModel.getMessage(),"",
                       moneyTransferResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch,mAmount);

                    Log.e(TAG,"10912   "+moneyTransferResponseModel.getMessage());

                }
                else if ( moneyTransferResponseModel.getStatusCode().equals("500")){
                 /*   TransactionOTPFragment.openTransactionOTP(getActivity(), mSender, mReceiver,
                            moneyTransferResponseModel.getTransactionId(), new AddSenderReceiverResponseModel(),
                            moneyTransferResponseModel.getOtpRefNo(), mOtpResendLink);*/
                    Log.e(TAG,"10913   "+moneyTransferResponseModel.getMessage());
                   alertMessage1(moneyTransferResponseModel.getStatus(), moneyTransferResponseModel.getMessage());
                }
                else {
//                    QuickSuccess(mAccNo,"Oops....","Something went wrong",moneyTransferResponseModel.getStatusCode(),
//                            moneyTransfer
//                            ResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch, mAmount);
                    Log.e(TAG,"10914   "+moneyTransferResponseModel.getMessage());
                    alertMessage1("Oops....!", "Something went wrong");
                }

            }catch ( Exception ignored){

            }


        }

    }

    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
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


    private void QuickSuccess(String mAccNo, String status, String message, String statusCode, String otpRefNo, String msenderName, String msenderMobile, String mreceiverAccountno, String mrecievererName, String mrecieverMobile, String mbranch, String mAmount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater1 = (LayoutInflater) getContext().getApplicationContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
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


      //  crdSuccess.setVisibility(View.VISIBLE);

        tv_msg.setText(status);
        tv_sender_name.setText(msenderName);
        tv_sender_mob_no.setText(msenderMobile);
        tv_sender_acc_no.setText(mAccNo);
       // tvbranch.setText(mbranch);
        txtv_typeamt.setText("Paid Amount");
        txtpfrom.setText("Sender Details");
        txtTo.setText("Reciever Details");

        tv_reciever_name.setText(mrecievererName);
        tv_reciever_acc_no.setText(mreceiverAccountno);
        tv_reciever_mob_no.setText(mrecieverMobile);

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        double num =Double.parseDouble(""+mAmount);
        String stramnt = CommonUtilities.getDecimelFormate(num);
      //  text_confirmationmsg.setText(message);
        // text_confirmationmsg.setVisibility(View.VISIBLE);
        String[] netAmountArr = mAmount.split("\\.");
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
                Intent i=new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();
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

                    File file = saveBitmap(bitmap, mAccNo+".png");
                    Log.e("chase  2044   ", "filepath: "+file.getAbsolutePath());
                    Uri bmpUri = Uri.fromFile(file);
                    //   Uri bmpUri = getLocalBitmapUri(bitmap);

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

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()){
//                        Log.e("img_share","img_share   1170   ");
//                        Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
//                                rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(bitmap);
//                        rltv_share.draw(canvas);
//
//                        try {
//
//                            File file = saveBitmap(bitmap, mAccNo+".png");
//                            Log.e("chase  2044   ", "filepath: "+file.getAbsolutePath());
//                            Uri bmpUri = Uri.fromFile(file);
//                            //   Uri bmpUri = getLocalBitmapUri(bitmap);
//
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                            shareIntent.setType("image/*");
//                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivity(Intent.createChooser(shareIntent, "Share"));
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.e("Exception","Exception   117   "+e.toString());
//                        }
//
//                    }
//                    else {
//                        final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                        final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                }
//                else{
//                    Log.e("img_share","img_share   1170   ");
//                    Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
//                            rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    rltv_share.draw(canvas);
//
//                    try {
//
//                        File file = saveBitmap(bitmap, mAccNo+".png");
//                        Log.e("chase  2044   ", "filepath: "+file.getAbsolutePath());
//                        Uri bmpUri = Uri.fromFile(file);
//                        //   Uri bmpUri = getLocalBitmapUri(bitmap);
//
//                        Intent shareIntent = new Intent();
//                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                        shareIntent.setType("image/*");
//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(shareIntent, "Share"));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("Exception","Exception   117   "+e.toString());
//                    }
//
//                }




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



    private class ChangeMpinAsync extends AsyncTask<String, android.R.integer, String> {
        private final String mSenderId;
        private ChangeMpinAsync(String senderId){
            mSenderId = senderId;
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog
                    .show(getActivity(), "", "Resending M-Pin...");
        }

        @Override
        protected String doInBackground(String... params) {

            return resendingOtp( mSenderId );
        }

        @Override
        protected void onPostExecute(String response ) {
            mProgressDialog.dismiss();

            if (response.trim().equals("1"))
                Toast.makeText(getActivity(), "M-Pin is resend to your mobile", Toast.LENGTH_LONG).show();
        }
        private String resendingOtp(String senderId){

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            try{
                String url = BASE_URL+
                        "/MTResendMPIN?senderid="+senderId;
                return ConnectionUtil.getResponse(url);

            }catch ( Exception ignored){

            }

            return "";

        }

    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
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

    private File saveBitmap(Bitmap bm, String fileName){
//        String mess = getResources().getString(R.string.app_name);
//        Log.e("Resources","Resources   117   "+mess);
//        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+mess;
//        File dir = new File(path);
//        if(!dir.exists())
//            dir.mkdirs();
//        File file = new File(dir, fileName);
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download"+ "/");
        boolean isPresent = true;
        Log.e("photoURI","StatementDownloadViewActivity   5682   ");
        if (!docsFolder.exists()) {
            // isPresent = docsFolder.mkdir();
            docsFolder.mkdir();
            Log.e("photoURI","StatementDownloadViewActivity   5683   ");
        }

        File file = new File(docsFolder, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


}
