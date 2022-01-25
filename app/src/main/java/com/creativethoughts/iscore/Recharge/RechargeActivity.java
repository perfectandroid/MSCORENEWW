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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

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
    LinearLayout ll_accountno;
    LinearLayout ll_recent_history;
    Spinner mOperatorSpinner,mCircleSpinner,mAccountSpinner;
    ImageButton browse_offer_image,selectContactImgBtn;
    AppCompatEditText mAmountEt;
    AutoCompleteTextView mMobileNumEt;
    FullLenghRecyclertview rv_recarge_history;
    ScrollView scrl_main;


    private int mSelectedType = 0;
    String operatorIds = "";
    String BranchName = "";
    private static final int REACHARGE_OFFER = 10;
    private static final int PICK_CONTACT = 1;

    JSONArray Jarray;
    public JSONArray JarrayOperator;
    RecentHistoryAdapter adapter;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist;

    AlertDialog dialog =  null;


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


//        mOperatorSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        mOperatorSpinner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG,"mOperatorSpinner  159   onTouch");
//                return false;
//            }
//        });

    }


    private void setInitialise() {

        accountlist = new ArrayList<String>();
        tv_header = (TextView)findViewById(R.id.tv_header);
        tv_operator = (TextView)findViewById(R.id.tv_operator);
        tv_circle = (TextView)findViewById(R.id.tv_circle);

        ll_accountno = (LinearLayout) findViewById(R.id.ll_accountno);
        ll_recent_history = (LinearLayout) findViewById(R.id.ll_recent_history);

        mOperatorSpinner = (Spinner) findViewById(R.id.operator_spinner);
        mCircleSpinner = (Spinner) findViewById(R.id.circle_spinner);
        mAccountSpinner = (Spinner) findViewById(R.id.spnAccountNum);

        browse_offer_image = (ImageButton)findViewById(R.id.browse_offer_image);
        selectContactImgBtn = (ImageButton)findViewById(R.id.select_contact_image);
        mAmountEt = (AppCompatEditText)findViewById(R.id.amount);
        mMobileNumEt =   (AutoCompleteTextView)findViewById(R.id.phoneno);

        rv_recarge_history =   (FullLenghRecyclertview) findViewById(R.id.rv_recarge_history);
        scrl_main = (ScrollView)findViewById(R.id.scrl_main);

    }

    private void setRegister() {
       // mOperatorSpinner.setOnClickListener(this);
        browse_offer_image.setOnClickListener(this);
        selectContactImgBtn.setOnClickListener(this);
        tv_operator.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
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

                    SharedPreferences TokenNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                    String token=TokenNoSP.getString("TokenNo", null);
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

                    SharedPreferences TokenNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                    String token=TokenNoSP.getString("TokenNo", null);
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
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                if (operatorIds.equals("")){

                }else {
                    Intent intent = new Intent(getApplicationContext(), ReachargeOfferActivity.class);
                    intent.putExtra("operatorIds", operatorIds);
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

                    SharedPreferences TokenNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                    String token=TokenNoSP.getString("TokenNo", null);
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

                            Log.e(TAG," getRechargeHistory    1360       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeHistory");
                                JarrayOperator  = jsonObj1.getJSONArray("RechargeHistoryList");

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(RechargeActivity.this);
                                final View customLayout1 = getLayoutInflater().inflate(R.layout.pop_circlelist, null);
                                RecyclerView rvCircle = customLayout1.findViewById(R.id.rvCircle);
                                builder1.setView(customLayout1);


//                                GridLayoutManager lLayout1 = new GridLayoutManager(getApplicationContext(), 1);
//                                rvCircle.setLayoutManager(lLayout1);
//                                rvCircle.setHasFixedSize(true);
//                                CircleAdapter adapter1 = new CircleAdapter(getApplicationContext(), JarrayOperator);
//                                rvCircle.setAdapter(adapter1);
//                                adapter1.setOnItemClickListener(RechargeActivity.this);

//                                AlertDialog dialog = builder.create();
                                dialog = builder1.create();
                                dialog.setCancelable(true);
                                dialog.show();
                            }
                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

                    SharedPreferences TokenNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                    String token=TokenNoSP.getString("TokenNo", null);
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

                            Log.e(TAG," getRechargeHistory    1360       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeHistory");
                                JarrayOperator  = jsonObj1.getJSONArray("RechargeHistoryList");

                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                final View customLayout = getLayoutInflater().inflate(R.layout.pop_operatorlist, null);
                                RecyclerView rvOperator = customLayout.findViewById(R.id.rvOperator);
                                builder.setView(customLayout);


//                                GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
//                                rvOperator.setLayoutManager(lLayout);
//                                rvOperator.setHasFixedSize(true);
//                                OperatorAdapter adapter = new OperatorAdapter(getApplicationContext(), JarrayOperator);
//                                rvOperator.setAdapter(adapter);
//                                adapter.setOnItemClickListener(RechargeActivity.this);

//                                AlertDialog dialog = builder.create();
                                dialog = builder.create();
                                dialog.setCancelable(true);
                                dialog.show();
                            }
                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
           try {
               JSONObject jsonObject = JarrayOperator.getJSONObject(position);
               Log.e(TAG,"jsonObject   7651   "+jsonObject.getString("ID_Recharge"));
               tv_operator.setText(""+jsonObject.getString("ID_Recharge"));

           }catch (Exception e){

           }
        }

        if (data.equals("circle")){
            dialog.dismiss();
            try {
                JSONObject jsonObject = JarrayOperator.getJSONObject(position);
                Log.e(TAG,"jsonObject   76512   "+jsonObject.getString("ID_Recharge"));
                tv_circle.setText(""+jsonObject.getString("ID_Recharge"));

            }catch (Exception e){

            }
        }

    }
}
