package com.creativethoughts.iscore.Recharge;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.ReachargeOfferActivity;
import com.creativethoughts.iscore.Recharge.OptionFragment;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.DepositListInfoAdapter;
import com.creativethoughts.iscore.adapters.RecentHistoryAdapter;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.creativethoughts.iscore.utility.RechargeValue;
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
    TextView tv_header;
    TextView tv_operator,tv_circle;
    TextView txt_amtinword;

    LinearLayout ll_accountno;
    LinearLayout ll_recent_history;

    Spinner mOperatorSpinner,mCircleSpinner,mAccountSpinner;

    ImageButton browse_offer_image,selectContactImgBtn;

    AppCompatEditText mAmountEt;
    AppCompatEditText mAccNumEdt;

    AutoCompleteTextView mMobileNumEt;

    FullLenghRecyclertview rv_recarge_history;

    ScrollView scrl_main;

    Button mSubmitButton;


    private int mSelectedType = 0;
    String operatorIds = "";
    String BranchName = "";
    private static final int REACHARGE_OFFER = 10;
    private static final int PICK_CONTACT = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacharge);

        setInitialise();
        setRegister();

        if(getIntent().getStringExtra("from").equals("prepaid")){
            mSelectedType = 0;
            tv_header.setText("Prepaid");
        }
        if(getIntent().getStringExtra("from").equals("postpaid")){
            mSelectedType = 1;
            tv_header.setText("Postpaid");
        }
        if(getIntent().getStringExtra("from").equals("Landline")){
            mSelectedType = 2;
            tv_header.setText("Landline");
        }
        if(getIntent().getStringExtra("from").equals("DTH")){
            mSelectedType = 3;
            tv_header.setText("DTH");
        }
        if(getIntent().getStringExtra("from").equals("datacard")){
            mSelectedType = 4;
            tv_header.setText("Datacard");
        }

        ll_accountno.setVisibility(View.GONE);
        if (( mSelectedType == 1 || mSelectedType == 2 ) && isCircleAccountNumberMandatory() ) {

            ll_accountno.setVisibility(View.VISIBLE);
           // mAccNumEdt.setVisibility(View.VISIBLE);
        }

        getHistory(mSelectedType);
        getAccList();


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
        txt_amtinword = (TextView)findViewById(R.id.txt_amtinword);

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


        rv_recarge_history =   (FullLenghRecyclertview) findViewById(R.id.rv_recarge_history);
        scrl_main = (ScrollView)findViewById(R.id.scrl_main);

        mSubmitButton = (Button) findViewById(R.id.btn_submit);

    }

    private void setRegister() {
       // mOperatorSpinner.setOnClickListener(this);
        browse_offer_image.setOnClickListener(this);
        selectContactImgBtn.setOnClickListener(this);
        tv_operator.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
    }


    private boolean isCircleAccountNumberMandatory() {
       // return (mSelectedType == 1 || mSelectedType == 2) && ((mOperatorSpinner.getSelectedItem().toString().contains("MTNL") && mCircleSpinner.getSelectedItem().toString().contains("Delhi")) || mOperatorSpinner.getSelectedItem().toString().contains("BSNL"));
        return (mSelectedType == 1 || mSelectedType == 2) && ((tv_operator.getText().toString().contains("MTNL") && tv_circle.getText().toString().contains("Delhi")) || tv_operator.getText().toString().contains("BSNL"));
      //  return (mSelectedType == 1 || mSelectedType == 2);


    }

    private void getHistory(int mSelectedType) {

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
                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));

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
//                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));


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
                        catch (JSONException e) { }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) { }
        }
        else {
           // alertMessage1("", "Network is currently unavailable. Please try again later.");

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
                if (ID_Providers.equals("")){

                }else {
                    Intent intent = new Intent(getApplicationContext(), ReachargeOfferActivity.class);
                    intent.putExtra("operatorIds", ID_Providers);
                    startActivityForResult(intent, REACHARGE_OFFER);
                }
                break;

            case R.id.select_contact_image:
                contactSelect();
                break;

            case R.id.tv_operator:
                getOperatorList();
                break;

            case R.id.tv_circle:
                getCircleList();
                break;

            case R.id.btn_submit:
                onClickSubmit();
                break;
        }
    }




    private void getCircleList() {

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

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRechargeCircleDetails(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"response  232   "+response.body());
                        try{

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

                    }
                });

            }
            catch (Exception e)
            {
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

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getProvidersList(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"response  232   "+response.body());
                        try{

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

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
            catch (Exception e)
            {

            }
        }
        else {
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
                assert uriContact != null;
                Cursor cursor =
                        getApplicationContext().getContentResolver().query(
                                uriContact, null, null, null, null);

                assert cursor != null;
                cursor.moveToFirst();
                String tempContact = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mMobileNumEt.setText(extractPhoneNumber(tempContact));

                closeCursor(cursor);
            }catch (Exception e){
                if(IScoreApplication.DEBUG)Log.e("contact ex", e.toString());
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
                tv_circle.setText(""+jsonObject.getString("CircleName"));
                ID_RechargeCircle = jsonObject.getString("ID_RechargeCircle");
                CircleName = jsonObject.getString("CircleName");
                CircleMode = jsonObject.getString("CircleMode");


            }catch (Exception e){

            }
        }

    }

    private void onClickSubmit() {

        final String mobileNumber   = mMobileNumEt.getText().toString();
        final String amount         = mAmountEt.getText().toString();
        final String operatorName = tv_operator.getText().toString();
        final String circleName = tv_circle.getText().toString();

        String tempAmountInr = " And Amount INR.";
        String tempForRecharging = " For Recharging";
        String confirmSubscriberId = "Please Confirm The SUBSCRIBER ID ";

        final String accountNumber = mAccountSpinner.getSelectedItem().toString();
        final String circleAccountNo = mAccNumEdt.getText().toString();

        if( (mSelectedType == 0 || mSelectedType == 1) && mobileNumber.length() != 10 ){
            showToast("Please enter valid 10 digit mobile number");
        } else if( (mSelectedType == 2) && ( (mobileNumber.length() <10 ) || (mobileNumber.length() > 15  ) ) ){
            showToast("Please enter valid land line number");
        }
        else if( (mSelectedType == 3) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else if( (mSelectedType == 4) && ( (mobileNumber.length() < 6) || (mobileNumber.length()>15) ) ){
            showToast("Please enter valid subscriber ID");
        }
        else if( ID_Providers.equals("") ){
            showToast("Select operator");
        }
        else if( ID_RechargeCircle.equals("") ){
            showToast("Select circle");
        }
        else{
            if(amount.length() > 0){
                String Finamount = amount.replace(",","");

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
//                mAccountNumber = mAccountNumber.replace(mAccountNumber.substring(mAccountNumber.indexOf(" (")+1, mAccountNumber.indexOf(')')+1), "");
//                mAccountNumber = mAccountNumber.replace(" ","");
//                Log.e(TAG,"RECHARGE"
//                +"\n"+"MobileNumer    "+mobileNumber
//                        +"\n"+"Operator    "+ID_Providers
//                        +"\n"+"Circle    "+ID_RechargeCircle
//                        +"\n"+"AccountNo    "+mAccountNumber
//                        +"\n"+"Module    "+
//                        +"\n"+"Pin    "+
//                        +"\n"+"Type    "+
//                        +"\n"+"OperatorName    "+
//                        +"\n"+"imei    "+
//                        +"\n"+"token    "+
//                        +"\n"+"BankKey    "+
//                        +"\n"+"BankHeader    "+
//                        +"\n"+"BankVerified    "+)
//
//                mAmount




            }else {

                showToast("Network error occured. Please try again later");
            }
        }



    }


    private void showToast(String value) {
        if ( getApplicationContext()  == null )
            return;
        Toast toast = Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT);
        LayoutInflater layoutInflater = getLayoutInflater();
        View toastView = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.container));
        TextView textView = toastView.findViewById(R.id.text);
        textView.setText(value);
        toast.setView(toastView);
        toast.show();
    }

}
