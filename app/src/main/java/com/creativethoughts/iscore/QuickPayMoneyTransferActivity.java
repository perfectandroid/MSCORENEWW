package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
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
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.SenderReceiver;
import com.creativethoughts.iscore.money_transfer.OtherbankFundTransferActivity;
import com.creativethoughts.iscore.money_transfer.QuickPayMoneyTransferFragment;
import com.creativethoughts.iscore.utility.DialogUtil;
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
    private LinearLayout mLnrAnimatorContainer;
    private RelativeLayout mRltvError;
    private TextView mTxtError,txt_amtinword;
    String BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();
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

        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);

        Button mBtnForgotMpin = findViewById(R.id.btn_forgot_mpin);
        mBtnForgotMpin.setOnClickListener( this );

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
}
