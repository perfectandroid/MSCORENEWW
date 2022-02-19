package com.creativethoughts.iscore.Recharge;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.FullLenghRecyclertview;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.ReachargeOfferActivity;
import com.creativethoughts.iscore.RechargeHistoryActivity;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.RecentHistoryAdapter;

import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.android.material.textfield.TextInputLayout;
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

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {

    String TAG = "RechargeActivity";
    private ProgressDialog progressDialog;
    TextView tv_header;
    TextView tv_operator,tv_circle,tv_accountno;
    TextView txt_amtinword;
    TextView tv_number_hint;

    LinearLayout ll_accountno;
    LinearLayout ll_recent_history;

    Spinner mOperatorSpinner,mCircleSpinner,mAccountSpinner;

    ImageButton browse_offer_image,selectContactImgBtn;

    AppCompatEditText mAmountEt;
    AppCompatEditText mAccNumEdt;

    AutoCompleteTextView mMobileNumEt;

    TextInputLayout phonenumberLayout;

    FullLenghRecyclertview rv_recarge_history;

    ScrollView scrl_main;

    Button mSubmitButton;
    Button btn_clear;


    private String mSelectedType = "";
    String operatorIds = "";
    String BranchName = "";
    private static final int REACHARGE_OFFER = 10;
    private static final int PICK_CONTACT = 1;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    JSONArray Jarray;
    public JSONArray JarrayOperator;
    public JSONArray JarrayCircle;
    RecentHistoryAdapter adapter;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist;

    AlertDialog dialog =  null;

    String ID_Providers="";
    String ProvidersName="";
    String ProvidersCode="";

    String ID_RechargeCircle="";
    String CircleName="";
    String CircleMode="";

    String typeShort="";
    String SubModule="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacharge);

        setInitialise();
        setRegister();

        if(getIntent().getStringExtra("from").equals("prepaid")){
            mSelectedType = "0";
            tv_header.setText("Prepaid");
            phonenumberLayout.setHint("Mobile number");
            tv_number_hint.setText("Mobile number");
        }
        if(getIntent().getStringExtra("from").equals("postpaid")){
            mSelectedType = "1";
            tv_header.setText("Postpaid");
            phonenumberLayout.setHint("Mobile number");
            tv_number_hint.setText("Mobile number");
        }
        if(getIntent().getStringExtra("from").equals("Landline")){
            mSelectedType = "2";
            tv_header.setText("Landline");
            phonenumberLayout.setHint("Phone Number");
            tv_number_hint.setText("Phone number");
            selectContactImgBtn.setVisibility(View.GONE);
        }
        if(getIntent().getStringExtra("from").equals("DTH")){
            mSelectedType = "3";
            tv_header.setText("DTH");
            phonenumberLayout.setHint("SUBSCRIBER ID");
            tv_number_hint.setText("SUBSCRIBER ID");
            selectContactImgBtn.setVisibility(View.GONE);
        }
        if(getIntent().getStringExtra("from").equals("datacard")){
            mSelectedType = "4";
            tv_header.setText("Datacard");
            phonenumberLayout.setHint("SUBSCRIBER ID");
            tv_number_hint.setText("SUBSCRIBER ID");
            selectContactImgBtn.setVisibility(View.VISIBLE);
        }

        ll_accountno.setVisibility(View.GONE);
        if (( mSelectedType.equals("1") || mSelectedType.equals("2")) && isCircleAccountNumberMandatory() ) {

            ll_accountno.setVisibility(View.VISIBLE);
           // mAccNumEdt.setVisibility(View.VISIBLE);
        }

        getHistory(mSelectedType);




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
                        mSubmitButton.setText( "PAY  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        mSubmitButton.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }

            }
        });

    }


    private void setInitialise() {

        accountlist = new ArrayList<String>();

        tv_header = (TextView)findViewById(R.id.tv_header);
        tv_operator = (TextView)findViewById(R.id.tv_operator);
        tv_circle = (TextView)findViewById(R.id.tv_circle);
        tv_accountno = (TextView)findViewById(R.id.tv_accountno);
        txt_amtinword = (TextView)findViewById(R.id.txt_amtinword);
        tv_number_hint = (TextView)findViewById(R.id.tv_number_hint);

        ll_accountno = (LinearLayout) findViewById(R.id.ll_accountno);
        ll_recent_history = (LinearLayout) findViewById(R.id.ll_recent_history);

        mOperatorSpinner = (Spinner) findViewById(R.id.operator_spinner);
        mCircleSpinner = (Spinner) findViewById(R.id.circle_spinner);
        mAccountSpinner = (Spinner) findViewById(R.id.spnAccountNum);

        browse_offer_image = (ImageButton)findViewById(R.id.browse_offer_image);
        selectContactImgBtn = (ImageButton)findViewById(R.id.select_contact_image);

        mAmountEt = (AppCompatEditText)findViewById(R.id.amount);
        mMobileNumEt =   (AutoCompleteTextView)findViewById(R.id.phoneno);
        mAccNumEdt =   (AppCompatEditText)findViewById(R.id.account_number);

        phonenumberLayout =   (TextInputLayout)findViewById(R.id.phoneno_layout);


        rv_recarge_history =   (FullLenghRecyclertview) findViewById(R.id.rv_recarge_history);
        scrl_main = (ScrollView)findViewById(R.id.scrl_main);

        mSubmitButton = (Button) findViewById(R.id.btn_submit);
        btn_clear = (Button) findViewById(R.id.btn_clear);

    }

    private void setRegister() {
       // mOperatorSpinner.setOnClickListener(this);
        browse_offer_image.setOnClickListener(this);
        selectContactImgBtn.setOnClickListener(this);
        tv_operator.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
        tv_accountno.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }


    private boolean isCircleAccountNumberMandatory() {
       // return (mSelectedType == 1 || mSelectedType == 2) && ((mOperatorSpinner.getSelectedItem().toString().contains("MTNL") && mCircleSpinner.getSelectedItem().toString().contains("Delhi")) || mOperatorSpinner.getSelectedItem().toString().contains("BSNL"));
        return (mSelectedType.equals("1") || mSelectedType.equals("2")) && ((tv_operator.getText().toString().contains("MTNL") && tv_circle.getText().toString().contains("Delhi")) || tv_operator.getText().toString().contains("BSNL"));
      //  return (mSelectedType == 1 || mSelectedType == 2);


    }

    private void getHistory(String mSelectedType) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {

            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {


                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("BranchCode", IScoreApplication.encryptStart("0"));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("RechargeType", IScoreApplication.encryptStart(String.valueOf(mSelectedType)));

                    Log.e(TAG,"requestObject1     1341   "+requestObject1);
                    Log.e(TAG,"mSelectedType     1341   "+mSelectedType);


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRechargeHistory(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"response  232   "+response.body());
                        try{
                            ll_recent_history.setVisibility(View.GONE);
                            Log.e(TAG," getRechargeHistory    1360       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeHistory");
                                Jarray  = jsonObj1.getJSONArray("RechargeHistoryList");
                                if(Jarray.length()!=0) {
                                    ll_recent_history.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(RechargeActivity.this, 1);
                                    rv_recarge_history.setLayoutManager(lLayout);
                                    rv_recarge_history.setHasFixedSize(true);
                                    adapter = new RecentHistoryAdapter(RechargeActivity.this,Jarray);
                                    rv_recarge_history.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position, String data, String mode) {


                                            Log.e(TAG,"onItemClick     1375    "+position);
                                            try {
                                                JSONObject jsonObject = Jarray.getJSONObject(position);
                                                Log.e(TAG,"onItemClick     1375    "+jsonObject.getString("MobileNo")+"     "+jsonObject.getString("Operator"));
                                                mMobileNumEt.setText(""+jsonObject.getString("MobileNo"));
//                                                setOperator(Integer.parseInt(jsonObject.getString("Operator")));
//                                                setOperator(1,jsonObject.getString("OperatorName"));
//                                                operatorIds = jsonObject.getString("Operator");
//                                                setCircle();


                                                tv_operator.setText(""+jsonObject.getString("OperatorName"));
                                                ID_Providers=jsonObject.getString("ID_Operator");
                                                ProvidersName=jsonObject.getString("OperatorName");
                                                ProvidersCode=jsonObject.getString("Operator");

                                                tv_circle.setText(""+jsonObject.getString("CircleName"));
                                                ID_RechargeCircle = jsonObject.getString("Circle");
                                                CircleName = jsonObject.getString("CircleName");
                                                CircleMode = jsonObject.getString("CircleMode");

                                                mAmountEt.setText(""+jsonObject.getInt("RechargeRs"));
                                                scrl_main.scrollTo(0,0);



                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            }
                            else {
                                ll_recent_history.setVisibility(View.GONE);

                            }

                        }
                        catch (JSONException e)
                        {
                            ll_recent_history.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        ll_recent_history.setVisibility(View.GONE);
                    }
                });

            }
            catch (Exception e)
            {
                ll_recent_history.setVisibility(View.GONE);
            }
        }
        else {

            ll_recent_history.setVisibility(View.GONE);
        }
    }

    private void getAccList() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {
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

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1   501   "+requestObject1);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
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
                                Log.e(TAG,"accountlist   501   "+accountlist);
                                Log.e(TAG,"jresult   501   "+jresult);
//                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));
//
//                                mAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                        try {
//                                            BranchName = jresult.getJSONObject(position).getString("BranchName");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> parent) {
//
//                                    }
//                                })
//                                ;
//                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));

                                if (jresult.length()>0){
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(RechargeActivity.this);
                                    final View customLayout2 = getLayoutInflater().inflate(R.layout.popup_accountno, null);
                                    RecyclerView rvAccountno = customLayout2.findViewById(R.id.rvAccountno);
                                    builder2.setView(customLayout2);


                                    GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
                                    rvAccountno.setLayoutManager(lLayout);
                                    rvAccountno.setHasFixedSize(true);
                                    AccountAdapter adapter = new AccountAdapter(getApplicationContext(), jresult);
                                    rvAccountno.setAdapter(adapter);
                                    adapter.setOnItemClickListener(RechargeActivity.this);

                                    // dialog = builder2.create();
                                    dialog = builder2.create();
                                    dialog.setCancelable(true);
                                    dialog.show();
                                }



                            }
                            else {

                                try{
//                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
//                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
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
                        catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
                progressDialog.dismiss();
            }
        }
        else {

            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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


    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse_offer_image:
              //  operatorIds = ID_Providers
                if (ProvidersCode.equals("")){
                    showToast("Select operator");
                }else {
                    Intent intent = new Intent(getApplicationContext(), ReachargeOfferActivity.class);
                    intent.putExtra("operatorIds", ProvidersCode);
                    intent.putExtra("operatorName", tv_operator.getText().toString());
                    startActivityForResult(intent, REACHARGE_OFFER);
                }
                break;

            case R.id.select_contact_image:
              //  contactSelect();

                requestContactPermission();
                break;

            case R.id.tv_operator:
                getOperatorList();
                break;

            case R.id.tv_circle:
                getCircleList();
                break;

            case R.id.tv_accountno:
                getAccList();
                break;

            case R.id.btn_submit:
                onClickSubmit();
                break;

            case R.id.btn_clear:
                onClickClear();
                break;


        }
    }

    private void onClickClear() {

        ID_Providers = "";
        ProvidersName = "";
        ProvidersCode = "";
        tv_operator.setText("");

        ID_RechargeCircle = "";
        CircleName = "";
        CircleMode = "";
        tv_circle.setText("");

        BranchName = "";
        SubModule = "";
        typeShort = "";
        tv_accountno.setText("");

        mAmountEt.setText("");
        mMobileNumEt.setText("");
        mAccNumEdt.setText("");



    }


    private void getCircleList() {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);


                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("31"));
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1     1341   "+requestObject1);


                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRechargeCircleDetails(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"response  232   "+response.body());
                        try{
                            progressDialog.dismiss();

                            Log.e(TAG," getRechargeCircleDetails    728       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeCircleDetails");
                                JarrayCircle  = jsonObj1.getJSONArray("RechargeCircleDetailsList");



                                AlertDialog.Builder builder1 = new AlertDialog.Builder(RechargeActivity.this);
                                final View customLayout1 = getLayoutInflater().inflate(R.layout.pop_circlelist, null);
                                RecyclerView rvCircle = customLayout1.findViewById(R.id.rvCircle);
                                builder1.setView(customLayout1);


                                GridLayoutManager lLayout1 = new GridLayoutManager(getApplicationContext(), 1);
                                rvCircle.setLayoutManager(lLayout1);
                                rvCircle.setHasFixedSize(true);
                                CircleAdapter adapter1 = new CircleAdapter(getApplicationContext(), JarrayCircle);
                                rvCircle.setAdapter(adapter1);
                                adapter1.setOnItemClickListener(RechargeActivity.this);

//                                AlertDialog dialog = builder.create();
                                dialog = builder1.create();
                                dialog.setCancelable(true);
                                dialog.show();
                            }
                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                builder.setMessage(jsonObj.getString("EXMessage"))
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
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                            builder.setMessage(e.toString())
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

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            }
            catch (Exception e)
            {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                builder.setMessage(e.toString())
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
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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

    private void getOperatorList() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("Reqmode",IScoreApplication.encryptStart("30"));
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("ProvidersMode", IScoreApplication.encryptStart(String.valueOf(mSelectedType)));

                    Log.e(TAG,"requestObject1     876   "+requestObject1);
                    Log.e(TAG,"mSelectedType     876   "+mSelectedType);


                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getProvidersList(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"response  232   "+response.body());
                        try{
                            progressDialog.dismiss();
                            Log.e(TAG," getRechargeHistory    1360       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("ProvidersDetailsInfo");

                                if (jsonObj1.getString("ResponseCode").equals("0")){
                                    JarrayOperator  = jsonObj1.getJSONArray("ProvidersDetails");
                                    Log.e(TAG," JarrayOperator    1360       "+JarrayOperator);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                    final View customLayout = getLayoutInflater().inflate(R.layout.pop_operatorlist, null);
                                    RecyclerView rvOperator = customLayout.findViewById(R.id.rvOperator);
                                    builder.setView(customLayout);


                                    GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
                                    rvOperator.setLayoutManager(lLayout);
                                    rvOperator.setHasFixedSize(true);
                                    OperatorAdapter adapter = new OperatorAdapter(getApplicationContext(), JarrayOperator);
                                    rvOperator.setAdapter(adapter);
                                    adapter.setOnItemClickListener(RechargeActivity.this);

                                    dialog = builder.create();
                                    dialog = builder.create();
                                    dialog.setCancelable(true);
                                    dialog.show();

                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                    builder.setMessage(jsonObj1.getString("ResponseMessage"))
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

//                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeHistory");
//                                JarrayOperator  = jsonObj1.getJSONArray("RechargeHistoryList");
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
//                                final View customLayout = getLayoutInflater().inflate(R.layout.pop_operatorlist, null);
//                                RecyclerView rvOperator = customLayout.findViewById(R.id.rvOperator);
//                                builder.setView(customLayout);
//
//
////                                GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
////                                rvOperator.setLayoutManager(lLayout);
////                                rvOperator.setHasFixedSize(true);
////                                OperatorAdapter adapter = new OperatorAdapter(getApplicationContext(), JarrayOperator);
////                                rvOperator.setAdapter(adapter);
////                                adapter.setOnItemClickListener(RechargeActivity.this);
//
////                                AlertDialog dialog = builder.create();
//                                dialog = builder.create();
//                                dialog.setCancelable(true);
//                                dialog.show();
                            }
                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                builder.setMessage(jsonObj.getString("EXMessage"))
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
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            }
            catch (Exception e)
            {
                progressDialog.dismiss();
            }
        }
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }


    private void getContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        intent.setType( ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE );
        startActivityForResult(intent, PICK_CONTACT);
    }

    private void contactSelect() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        intent.setType( ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE );
        startActivityForResult(intent, PICK_CONTACT);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.operator_spinner:
//                Log.e(TAG,"operator_spinner  339   ");
//                break;
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK && getApplicationContext() != null ){
            try{
                Uri uriContact = data.getData();
                Log.e(TAG,"uriContact   1182   "+uriContact);
                assert uriContact != null;
                Cursor cursor =
                        getApplicationContext().getContentResolver().query(
                                uriContact, null, null, null, null);

                assert cursor != null;
                Log.e(TAG,"cursor   11822   "+cursor);
                cursor.moveToFirst();
                Log.e(TAG,"cursor   11823   "+cursor.getCount());
//                Log.e(TAG,"NUMBER   11822   "+cursor.getString(cursor.getColumnIndex("ContactNumber")));
                String tempContact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mMobileNumEt.setText(extractPhoneNumber(tempContact));

                closeCursor(cursor);
            }catch (Exception e){
                if(IScoreApplication.DEBUG)Log.e("contact ex", e.toString());
                Log.e(TAG,"Exception   11824   "+e.toString());
            }
        }
        if (requestCode == 10) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                Log.e(TAG,"strEditText  770   "+strEditText);
                mAmountEt.setText(""+strEditText);
            }
        }
    }

    private String extractPhoneNumber(String resultPhoneNumber){
        String result;
        try{
            result = resultPhoneNumber.replaceAll("\\D+","");
            if(result.length() > 10){
                result = result.substring( result.length()-10,result.length());
            }
        }catch (Exception e){
            result = "";
        }
        return  result;
    }

    private void closeCursor(Cursor cursor){
        try {
            cursor.close();
        }catch (Exception e){
            if(IScoreApplication.DEBUG) Log.e("Null pointer ex", e.toString());
        }
    }

    @Override
    public void onItemClick(View v, int position, String data, String mode) {

        Log.e(TAG,"onItemClick   765   "+position+"   "+data);
        if (data.equals("operator")){
            dialog.dismiss();
            ID_Providers = "";
            ProvidersName = "";
            ProvidersCode = "";
            tv_operator.setText("");
           try {
               JSONObject jsonObject = JarrayOperator.getJSONObject(position);
               Log.e(TAG,"jsonObject   7651   "+jsonObject.getString("ProvidersName"));
               tv_operator.setText(""+jsonObject.getString("ProvidersName"));

               ID_Providers=jsonObject.getString("ID_Providers");
               ProvidersName=jsonObject.getString("ProvidersName");
               ProvidersCode=jsonObject.getString("ProvidersCode");

               if (isCircleAccountNumberMandatory()) {
                   ll_accountno.setVisibility(View.VISIBLE);
                     mAccNumEdt.setText("");
               } else {
                   ll_accountno.setVisibility(View.GONE);
                   mAccNumEdt.setText("");
               }


           }catch (Exception e){

           }
        }

        if (data.equals("circle")){
            dialog.dismiss();

            ID_RechargeCircle = "";
            CircleName = "";
            CircleMode = "";
            tv_circle.setText("");
            try {
                JSONObject jsonObject = JarrayCircle.getJSONObject(position);
                Log.e(TAG,"jsonObject   76512   "+jsonObject.getString("CircleName"));
               // tv_circle.setText("Delhi");
                tv_circle.setText(""+jsonObject.getString("CircleName"));
                ID_RechargeCircle = jsonObject.getString("ID_RechargeCircle");
                CircleName = jsonObject.getString("CircleName");
                CircleMode = jsonObject.getString("CircleMode");

                if (isCircleAccountNumberMandatory()) {
                    ll_accountno.setVisibility(View.VISIBLE);
                    mAccNumEdt.setText("");
                } else {
                    ll_accountno.setVisibility(View.GONE);
                    mAccNumEdt.setText("");
                }


            }catch (Exception e){

            }
        }

        if (data.equals("account")){
            dialog.dismiss();

            BranchName = "";
            SubModule = "";
            typeShort = "";
            tv_accountno.setText("");
            try {
                JSONObject jsonObject = jresult.getJSONObject(position);
//                Log.e(TAG,"jsonObject   76512   "+jsonObject.getString("CircleName"));
//                tv_circle.setText(""+jsonObject.getString("CircleName"));
//                ID_RechargeCircle = jsonObject.getString("ID_RechargeCircle");
                BranchName = jsonObject.getString("BranchName");
                typeShort = jsonObject.getString("typeShort");
               // SubModule = jsonObject.getString("SubModule");
                SubModule = jsonObject.getString("typeShort");

                tv_accountno.setText(""+jsonObject.getString("AccountNumber"));
            }catch (Exception e){

            }
        }

    }

    private void onClickSubmit() {

        final String mobileNumber   = mMobileNumEt.getText().toString();
        String amount         = mAmountEt.getText().toString();
        final String operatorName = tv_operator.getText().toString();
        final String circleName = tv_circle.getText().toString();

        String tempAmountInr = " And Amount INR.";
        String tempForRecharging = " For Recharging";
        String confirmSubscriberId = "Please Confirm The SUBSCRIBER ID ";

       // final String accountNumber = mAccountSpinner.getSelectedItem().toString();
        final String accountNumber = tv_accountno.getText().toString();
        final String circleAccountNo = mAccNumEdt.getText().toString();

        if( (mSelectedType.equals("0") || mSelectedType.equals("1")) && mobileNumber.length() != 10 ){
            showToast("Please enter valid 10 digit mobile number");
        } else if( (mSelectedType.equals("2")) && ( (mobileNumber.length() <10 ) || (mobileNumber.length() > 15  ) ) ){
            showToast("Please enter valid land line number");
        }
        else if( (mSelectedType.equals("3")) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else if( (mSelectedType.equals("4")) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else if( ID_Providers.equals("") ){
            showToast("Select operator");
        }
        else if( ID_RechargeCircle.equals("") ){
            showToast("Select circle");
        }
        else if( accountNumber.equals("") ){
            showToast("Select Account");
        }
        else{
            if(amount.length() > 0){
                String Finamount = amount.replace(",","");
                amount = amount.replace(",","");

                try {
                    Integer.parseInt(Finamount);

                }catch (Exception e){
                    showToast("Please enter valid amount");
                    return;
                }
                if(TextUtils.isDigitsOnly(Finamount.trim())){
                    long value = Long.parseLong(Finamount.trim());
                    if( value < 10 || value > 10000){
                        showToast("Please enter the amount 10 to 10000 to recharge");
                        return;
                    }
                }
                else return;
            }
            else{
                showToast("Please enter the amount to recharge");
                return;
            }


            if (NetworkUtil.isOnline() && getApplicationContext() != null ) {

             //   RechargeConfirmation(accountNumber, mSelectedType, mobileNumber, circleId, operatorId, amount, circleAccountNo, operatorName );
//                String mAccountNumber = mAccountNumber.replace(mAccountNumber.substring(mAccountNumber.indexOf(" (")+1, mAccountNumber.indexOf(')')+1), "");
//                mAccountNumber = mAccountNumber.replace(" ","");
                Log.e(TAG,"RECHARGE"
                +"\n"+"MobileNumer    "+mobileNumber
                        +"\n"+"Operator    "+ID_Providers
                        +"\n"+"Circle    "+ID_RechargeCircle
                        +"\n"+"Amount    "+amount
                        +"\n"+"AccountNo    "+accountNumber
                        +"\n"+"circleAccountNo    "+circleAccountNo
                        +"\n"+"Module vvv   "+SubModule
                        +"\n"+"Pin vvv    "+mSelectedType
                        +"\n"+"Type    "+mSelectedType
                        +"\n"+"OperatorName    "+operatorName);
//                        +"\n"+"imei    "+
//                        +"\n"+"token    "+
//                        +"\n"+"BankKey    "+
//                        +"\n"+"BankHeader    "+
//                        +"\n"+"BankVerified    "+)
//
//                mAmount

//                if (mSelectedType.equals("1")){
//
//                }



                RechargeConfirmation(accountNumber, mSelectedType, mobileNumber, ID_RechargeCircle, ID_Providers, amount, circleAccountNo, operatorName );




            }else {

                showToast("Network error occured. Please try again later");
            }
        }



    }
//    RechargeConfirmation(accountNumber, mSelectedType, mobileNumber, circleId, operatorId, amount, circleAccountNo, operatorName );
    private void RechargeConfirmation(String mAccountNumber, String mSelectedType, String mMobileNumber, String mCircleId,
                                      String mOperatorId, String mAmount, String mCircleAccNo, String operatorName) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater)this.getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.recharge_confirmation_popup, null);
            TextView tvAcntno =  layout.findViewById(R.id.tvAcntno);
            ImageView img_aapicon =  layout.findViewById(R.id.img_aapicon);
            TextView tvbranch =  layout.findViewById(R.id.tvbranch);
            TextView tv_mob =  layout.findViewById(R.id.tv_mob);
            TextView tv_oper =  layout.findViewById(R.id.tv_oper);
            TextView tv_cir =  layout.findViewById(R.id.tv_cir);
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
            PicassoTrustAll.getInstance(RechargeActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

            tvAcntno.setText(""+mAccountNumber);
            tvbranch.setText(BranchName);
            tv_mob.setText(""+mMobileNumber);
            tv_oper.setText(tv_operator.getText().toString());
            tv_cir.setText(tv_circle.getText().toString());

            PaymentModel paymentModel = new PaymentModel( );
            paymentModel.setAccNo( tvAcntno.getText().toString()  );
            paymentModel.setOperator(tv_operator.getText().toString());
            paymentModel.setCircle(tv_circle.getText().toString());
            paymentModel.setAmount( mAmount  );
            paymentModel.setBranch( BranchName  );


            Log.e(TAG,"operatorName     955    "+operatorName+"   "+mOperatorId+"   "+mAccountNumber+"   "+mCircleAccNo);

            String stramnt = mAmount.replace(",","");
            text_confirmationmsg.setText("Proceed Recharge With Above Amount"+ "..?");
            String[] netAmountArr = stramnt.split("\\.");
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
            tv_amount.setText("₹ " + CommonUtilities.getDecimelFormate(Double.parseDouble(stramnt)) );
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();

                    Log.e(TAG,"mAccountNumber   1483   "+mAccountNumber);

                    String AccountNumber = mAccountNumber.replace(mAccountNumber.substring(mAccountNumber.indexOf(" (")+1, mAccountNumber.indexOf(')')+1), "");
                    AccountNumber = AccountNumber.replace(" ","");

                    if (mSelectedType.equals("0")){
                        rechargePrepaid( AccountNumber, mSelectedType, mMobileNumber, mCircleId, mOperatorId, mAmount, mCircleAccNo ,operatorName,ProvidersCode);
                    }

                    if (mSelectedType.equals("1") || mSelectedType.equals("2")){
                        rechargePostLand( AccountNumber, mSelectedType, mMobileNumber, mCircleId, mOperatorId, mAmount, mCircleAccNo ,operatorName,ProvidersCode);
                    }

                    if (mSelectedType.equals("3") || mSelectedType.equals("4")){
                        rechargeDthDatacard( AccountNumber, mSelectedType, mMobileNumber, mCircleId, mOperatorId, mAmount, mCircleAccNo ,operatorName,ProvidersCode);
                    }


                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Exception     955    "+e.toString());
        }
    }

    private void rechargeDthDatacard(String AccountNumber, String mSelectedType, String mMobileNumber, String mCircleId,
                                     String mOperatorId, String mAmount, String mCircleAccNo, String operatorName,String ProvidersCode) {



        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    String iemi =   IScoreApplication.getIEMI();
                    Log.e("imei","         15381     "+iemi);

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", "");
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", "");
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", "");
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", "");
                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                    SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                    String BankVerifier =BankVerifierSP.getString("BankVerifier","");

                    requestObject1.put("SUBSCRIBER_ID",IScoreApplication.encryptStart(mMobileNumber));
                    requestObject1.put("Operator",IScoreApplication.encryptStart(ProvidersCode));
                    requestObject1.put("Circle",IScoreApplication.encryptStart(mCircleId));
                  //  requestObject1.put("Circleaccount",IScoreApplication.encryptStart(mCircleAccNo));
                    requestObject1.put("amount",IScoreApplication.encryptStart(mAmount));
                    requestObject1.put("AccountNo",IScoreApplication.encryptStart(AccountNumber));
                    requestObject1.put("Module",IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("Pin",IScoreApplication.encryptStart(pin));
                    requestObject1.put("Type",IScoreApplication.encryptStart(mSelectedType));
                    requestObject1.put("OperatorName",IScoreApplication.encryptStart(operatorName));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("token", IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(BankVerifier));

                    Log.e(TAG,"DthDatacard requestObject1     15382   "+requestObject1);
                    Log.e(TAG,"DthDatacard mAmount     15383   "+mAmount);

                    Log.e(TAG,"RECHARGE rechargeDthDatacard   18888"
                            +"\n"+"MobileNumer      "+mMobileNumber
                            +"\n"+"Operator         "+ProvidersCode
                            +"\n"+"Circle           "+mCircleId
                            +"\n"+"Amount           "+mAmount
                            +"\n"+"AccountNo        "+AccountNumber
                            +"\n"+"Module           "+SubModule
                            +"\n"+"Pin              "+pin
                            +"\n"+"Type             "+mSelectedType
                            +"\n"+"OperatorName     "+operatorName);






                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDTHRecharge(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"DthDatacard response  15384   "+response.body());
                        try{
                            progressDialog.dismiss();
                            Log.e(TAG,"DthDatacard response    15385       "+response.body());

                            JSONObject jsonObj = new JSONObject(response.body());
                            Log.e(TAG,"DthDatacard jsonObj    15386       "+jsonObj.getString("EXMessage"));
                            if(jsonObj.getString("StatusCode").equals("0")) {


                                alertMessage2("0",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("1")) {


                                alertMessage2("1",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("2")) {


                                alertMessage2("2",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("3")) {


                                alertMessage2("3",jsonObj.getString("EXMessage"));

                            }
                            else {

                                alertMessage2("",jsonObj.getString("EXMessage"));

                            }

                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            }
            catch (Exception e)
            {
                progressDialog.dismiss();
            }
        }
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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

    private void rechargePostLand(String AccountNumber, String mSelectedType, String mMobileNumber, String mCircleId,
                                  String mOperatorId, String mAmount, String mCircleAccNo, String operatorName,String ProvidersCode) {



        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    String iemi =   IScoreApplication.getIEMI();
                    Log.e("imei","         16901     "+iemi);

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", "");
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", "");
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", "");
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", "");
                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                    SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                    String BankVerifier =BankVerifierSP.getString("BankVerifier","");

                    requestObject1.put("MobileNumer",IScoreApplication.encryptStart(mMobileNumber));
                    requestObject1.put("Operator",IScoreApplication.encryptStart(ProvidersCode));
                    requestObject1.put("Circle",IScoreApplication.encryptStart(mCircleId));
                    requestObject1.put("Circleaccount",IScoreApplication.encryptStart(mCircleAccNo));
                    requestObject1.put("amount",IScoreApplication.encryptStart(mAmount));
                    requestObject1.put("AccountNo",IScoreApplication.encryptStart(AccountNumber));
                    requestObject1.put("Module",IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("Pin",IScoreApplication.encryptStart(pin));
                    requestObject1.put("Type",IScoreApplication.encryptStart(mSelectedType));
                    requestObject1.put("OperatorName",IScoreApplication.encryptStart(operatorName));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("token", IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(BankVerifier));

                    Log.e(TAG,"PostLand requestObject1     16902   "+requestObject1);
                    Log.e(TAG,"PostLand mAmount     16903   "+mAmount);

                    Log.e(TAG,"RECHARGE rechargePostLand   18888"
                            +"\n"+"MobileNumer      "+mMobileNumber
                            +"\n"+"Operator         "+ProvidersCode
                            +"\n"+"Circle           "+mCircleId
                            +"\n"+"Circleaccount    "+mCircleAccNo
                            +"\n"+"Amount           "+mAmount
                            +"\n"+"AccountNo        "+AccountNumber
                            +"\n"+"Module           "+SubModule
                            +"\n"+"Pin              "+pin
                            +"\n"+"Type             "+mSelectedType
                            +"\n"+"OperatorName     "+operatorName);



                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getPOSTPaidBilling(body);
                call.enqueue(new Callback<String>() {


                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"PostLand response  14884   "+response.body());
                        try{
                            progressDialog.dismiss();
                            Log.e(TAG," PostLand response    14885       "+response.body());

                            JSONObject jsonObj = new JSONObject(response.body());
                            Log.e(TAG,"PostLand  jsonObj    14886       "+jsonObj.getString("EXMessage"));

                            if(jsonObj.getString("StatusCode").equals("0")) {


                                alertMessage2("0",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("1")) {


                                alertMessage2("1",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("2")) {


                                alertMessage2("2",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("3")) {


                                alertMessage2("3",jsonObj.getString("EXMessage"));

                            }
                            else {

                                alertMessage2("",jsonObj.getString("EXMessage"));

                            }

                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            }
            catch (Exception e)
            {
                progressDialog.dismiss();
            }
        }
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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

    private void rechargePrepaid(String AccountNumber, String mSelectedType, String mMobileNumber, String mCircleId,
                          String mOperatorId, String mAmount, String mCircleAccNo, String operatorName,String ProvidersCode) {



        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(RechargeActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    String iemi =   IScoreApplication.getIEMI();
                    Log.e("imei","         1488     "+iemi);


                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", "");
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", "");
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", "");
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", "");
                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                    SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                    String BankVerifier =BankVerifierSP.getString("BankVerifier","");

                    requestObject1.put("MobileNumer",IScoreApplication.encryptStart(mMobileNumber));
                    requestObject1.put("Operator",IScoreApplication.encryptStart(ProvidersCode));
                    requestObject1.put("Circle",IScoreApplication.encryptStart(mCircleId));
                    requestObject1.put("Amount",IScoreApplication.encryptStart(mAmount));
                    requestObject1.put("AccountNo",IScoreApplication.encryptStart(AccountNumber));
                    requestObject1.put("Module",IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("Pin",IScoreApplication.encryptStart(pin));
                    requestObject1.put("Type",IScoreApplication.encryptStart(mSelectedType));
                    requestObject1.put("OperatorName",IScoreApplication.encryptStart(operatorName));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("token", IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(BankVerifier));

                    Log.e(TAG,"RECHARGE rechargePrepaid   18888"
                            +"\n"+"MobileNumer      "+mMobileNumber
                            +"\n"+"Operator         "+ProvidersCode
                            +"\n"+"Circle           "+mCircleId
                            +"\n"+"Amount           "+mAmount
                            +"\n"+"AccountNo        "+AccountNumber
                            +"\n"+"Module           "+SubModule
                            +"\n"+"Pin              "+pin
                            +"\n"+"Type             "+mSelectedType
                            +"\n"+"OperatorName     "+operatorName);


                    Log.e(TAG,"Prepaid requestObject1     18711   "+requestObject1);
                    Log.e(TAG,"Prepaid mAmount     18712   "+mAmount+"   "+AccountNumber+"  "+SubModule+"   "+ProvidersCode+"  "+CircleMode);



                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getMobileRecharge(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"Prepaid response  18713   "+response.body());
                        try{
                            progressDialog.dismiss();
                            Log.e(TAG," response    18714       "+response.body());

                            JSONObject jsonObj = new JSONObject(response.body());
                            Log.e(TAG," jsonObj    18715       "+jsonObj.getString("EXMessage"));
//                            if(jsonObj.getString("StatusCode").equals("0")) {
//
//                                alertMessage1("",jsonObj.getString("EXMessage"));
//                            }
//                            else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
//                                builder.setMessage(jsonObj.getString("EXMessage"))
////                                builder.setMessage("No data found.")
//                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//
//                                            }
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
//
//                            }

                            if(jsonObj.getString("StatusCode").equals("0")) {


                                alertMessage2("0",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("1")) {


                                alertMessage2("1",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("2")) {


                                alertMessage2("2",jsonObj.getString("EXMessage"));

                            }
                            else if(jsonObj.getString("StatusCode").equals("3")) {


                                alertMessage2("3",jsonObj.getString("EXMessage"));

                            }
                            else {

                                alertMessage2("",jsonObj.getString("EXMessage"));

                            }

                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            }
            catch (Exception e)
            {
                progressDialog.dismiss();
            }
        }
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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


    private void showToast(String value) {
        if ( getApplicationContext()  == null )
            return;
//        Toast toast = Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT);
//        LayoutInflater layoutInflater = getLayoutInflater();
//        View toastView = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.container));
//        TextView textView = toastView.findViewById(R.id.text);
//        textView.setText(value);
//        toast.setView(toastView);
//        toast.show();


        AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
        builder.setMessage(value)
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


    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RechargeActivity.this);

        LayoutInflater inflater = RechargeActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_recharge, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_ok =  dialogView.findViewById(R.id.tv_ok);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(RechargeActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
       // tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//
//            }
//        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();
                Intent intent = new Intent(RechargeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }

    private void alertMessage2(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RechargeActivity.this);

        LayoutInflater inflater = RechargeActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_recharge, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_ok =  dialogView.findViewById(R.id.tv_ok);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);
        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(RechargeActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
        // tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//
//            }
//        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();
//                Intent intent = new Intent(RechargeActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
                if (msg1.equals("0")){

                }
                else if (msg1.equals("1") || msg1.equals("2") || msg1.equals("3")){
                    Intent intent = new Intent(RechargeActivity.this, RechargeHistoryActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(RechargeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
        alertDialog.show();
    }


}
