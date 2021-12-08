
package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AccountListAdapter;
import com.creativethoughts.iscore.adapters.AccountSummaryListAdapter;
import com.creativethoughts.iscore.adapters.PassbookTranscationListAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.AccountToTransfer;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.android.material.tabs.TabLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

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

public class PassbookTranscationActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    private ArrayList<String> accountlist;
    Spinner spnAccountNum;
    private JSONArray jresult, jstatement;
    private TextView Account;
    private TextView available_balance;
    private TextView unclear_balance;
    private TextView txtLastUpdatedAt;
    private TextView empty_list;
    private TextView tv_list_days;
    private RecyclerView rv_passbook;
    private LinearLayout ll_balance;
    int noofdays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);
        tv_list_days = findViewById(R.id.tv_list_days);
        ll_balance = findViewById(R.id.ll_balance);
        spnAccountNum = findViewById(R.id.spnAccountNum);
        Account = findViewById(R.id.Account);
        available_balance = findViewById(R.id.available_balance);
        unclear_balance = findViewById(R.id.unclear_balance);
        txtLastUpdatedAt = findViewById(R.id.txtLastUpdatedAt);
        empty_list = findViewById(R.id.empty_list);
        rv_passbook = findViewById(R.id.rv_passbook);
        spnAccountNum.setOnItemSelectedListener(this);

        accountlist = new ArrayList<String>();

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        if (settingsModel != null && settingsModel.lastSyncTime > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy hh:mma z", Locale.ENGLISH);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(settingsModel.lastSyncTime);

            txtLastUpdatedAt.setText(formatter.format(calendar.getTime()));
        }
        else {
            txtLastUpdatedAt.setText("");
        }


        if (settingsModel == null || settingsModel.days <= 0) {
            noofdays = 30;
        } else {
            noofdays = settingsModel.days;
        }
        tv_list_days.setText("**Listing Data For Past "+noofdays+" Days.\nYou Can Change It From Settings.");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getAccList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void getPassBookAccountStatement(String fkaccount, String SubModule, int NoOfDays) {
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("28"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Account",   IScoreApplication.encryptStart(fkaccount));
                    requestObject1.put("SubModule",   IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("NoOfDays",   IScoreApplication.encryptStart(""+NoOfDays));
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
                Call<String> call = apiService.getPassBookAccountStatement(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("PassBookAccountStatement");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                jstatement  = object.getJSONArray("PassBookAccountStatementList");
                                if(jstatement.length()!=0) {
                                    tv_list_days.setVisibility(View.VISIBLE);
                                    rv_passbook.setVisibility(View.VISIBLE);
                                    empty_list.setVisibility(View.GONE);
                                    GridLayoutManager lLayout = new GridLayoutManager(PassbookTranscationActivity.this, 1);
                                    rv_passbook.setLayoutManager(lLayout);
                                    rv_passbook.setHasFixedSize(true);
                                    PassbookTranscationListAdapter adapter = new PassbookTranscationListAdapter(PassbookTranscationActivity.this, jstatement, SubModule);
                                    rv_passbook.setAdapter(adapter);
                                }
                                else{
                                    rv_passbook.setVisibility(View.GONE);
                                    tv_list_days.setVisibility(View.GONE);
                                    empty_list.setVisibility(View.VISIBLE);
                                    empty_list.setText("There are no transactions to display for the last " + noofdays + " days");

                                }
                            }
                            else {
                                tv_list_days.setVisibility(View.GONE);
                                rv_passbook.setVisibility(View.GONE);
                                empty_list.setVisibility(View.VISIBLE);
                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    empty_list.setText(ResponseMessage);
                                }
                                catch (Exception e) {
                                    empty_list.setText(jsonObj.getString("EXMessage"));

                                }                            }
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
            DialogUtil.showAlert(PassbookTranscationActivity.this,"Network is currently unavailable. Please try again later.");
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("27"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
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
                Call<String> call = apiService.getPassBookAccountDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("PassBookAccountDetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                 jresult  = object.getJSONArray("PassBookAccountDetailsList");


                                for(int i=0;i<jresult.length();i++){
                                    try {
                                        JSONObject json = jresult.getJSONObject(i);
                                        accountlist.add(json.getString("AccountNumber"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                spnAccountNum.setAdapter(new ArrayAdapter<String>(PassbookTranscationActivity.this, android.R.layout.simple_spinner_dropdown_item, accountlist));


                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
                                spnAccountNum.setSelection(getIndex(spnAccountNum, settingsModel.customerId));


                            }
                            else {

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PassbookTranscationActivity.this);
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PassbookTranscationActivity.this);
                                    builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
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
            DialogUtil.showAlert(PassbookTranscationActivity.this,"Network is currently unavailable. Please try again later.");
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

    //this method will execute when we pic an item from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try {
            JSONObject json = jresult.getJSONObject(position);
            if (json.getString("IsShowBalance").equals("1")){
                ll_balance.setVisibility(View.VISIBLE);

                available_balance.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(json.getDouble("AvailableBalance")));
                if (json.getDouble("UnclearAmount") <= 0) {
                    unclear_balance.setText("\u20B9 " + CommonUtilities.getDecimelFormate(json.getDouble("UnclearAmount")));
                    unclear_balance.setTextColor(Color.RED);
                }
                else{
                    unclear_balance.setText("\u20B9 " + CommonUtilities.getDecimelFormate(json.getDouble("UnclearAmount")));
                    unclear_balance.setTextColor(Color.parseColor("#7E5858"));
                }
            }
            else{
                ll_balance.setVisibility(View.GONE);
            }
            Account.setText(json.getString("AccountType"));

            getPassBookAccountStatement(json.getString("FK_Account"),json.getString("SubModule"),noofdays);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        available_balance.setText("");
        unclear_balance.setText("");
        Account.setText("");
    }



}