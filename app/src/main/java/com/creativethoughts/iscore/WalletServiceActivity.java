package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.SourceAccListAdapter;
import com.creativethoughts.iscore.adapters.StandingInstructionAdapter1;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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

public class WalletServiceActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressDialog progressDialog;
    TextView txt_userdetails,  txt_userid, txtv_totalbal;
    TextView tvTransaction,tvLoadmoney;
    LinearLayout ll_loadmoney, llministatement;
    RecyclerView rv_ministatmnt;
    private Spinner spn_account_type;
    ArrayList<ToAccountDetails> AccountDetails = new ArrayList<>();
    ArrayAdapter<ToAccountDetails> AccountAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walletservice);

        tvTransaction = findViewById(R.id.tvTransaction);
        tvTransaction.setOnClickListener(this);
        tvLoadmoney = findViewById(R.id.tvLoadmoney);
        tvLoadmoney.setOnClickListener(this);
        txt_userid =  findViewById(R.id.txt_userid);
        txt_userdetails =  findViewById(R.id.txt_userdetails);
        txtv_totalbal =  findViewById(R.id.txtv_totalbal);
        ll_loadmoney =  findViewById(R.id.ll_loadmoney);
        llministatement =  findViewById(R.id.llministatement);
        rv_ministatmnt =  findViewById(R.id.rv_ministatmnt);
        spn_account_type = findViewById(R.id.spn_account_type);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        txt_userid.setText( "Customer Id : "+userDetails.customerId);
        txt_userdetails.setText( userDetails.userCustomerName);
        showOwnAccToList();
        getWalletAmount();
        getTransactiondetails();
    }


    private void showOwnAccToList() {
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

                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("2"));
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
                        try{
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
                                if(Jarray.length()!=0) {

//                                    Date date = Calendar.getInstance().getTime();
//                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//                                    String formattedDate = df.format(date);
//                                    tv_as_on_date.setText("As On "+ formattedDate+"");
                                    JSONObject jsonobject= (JSONObject) Jarray.get(0);
                                    AccountDetails = new ArrayList<>();
                                    AccountDetails.add(new ToAccountDetails("", "Select Account","","", "", ""));

                                    for (int k = 0; k < Jarray.length(); k++) {
                                        JSONObject kjsonObject = Jarray.getJSONObject(k);

                                        AccountDetails.add(new ToAccountDetails( kjsonObject.getString("FK_Account"), kjsonObject.getString("AccountNumber"), kjsonObject.getString("SubModule"), kjsonObject.getString("Balance"), kjsonObject.getString("typeShort"), kjsonObject.getString("BranchName")));
                                    }

                                    AccountAdapter = new ArrayAdapter<>(WalletServiceActivity.this,  R.layout.list_content_spin,R.id.textview, AccountDetails);
//                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                    spn_account_type.setAdapter(AccountAdapter);




                                    spn_account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            if (position!=0) {

                                               // result = AccountAdapter.getItem(position).getBranchName();

                                                Object item = parent.getItemAtPosition(position);

                                              //  BalanceSplitUpDetails(AccountAdapter.getItem(position).getSubModule(),AccountAdapter.getItem(position).getFK_Account());
                                            }
                                            else {

                                                /*ll_needTochange.setVisibility(View.GONE);
                                                ll_needToPayAdvance.setVisibility(View.GONE);
                                                ll_remittance.setVisibility(View.GONE);
                                                rv_split_details.setVisibility(View.GONE);*/
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            //Do nothing
                                        }
                                    });
                                }
                                else {

//                                    tv_as_on_date.setVisibility(View.GONE);
                                }
                            }
                            else {

//                                tv_as_on_date.setVisibility(View.GONE);
                            }

                        }
                        catch (JSONException e) {

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
           // alertMessage1("", "Network is currently unavailable. Please try again later.");
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }

    }

    public void getTransactiondetails(){

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(WalletServiceActivity.this, R.style.Progress);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(this.getResources()
                        .getDrawable(R.drawable.progress));
                progressDialog.show();
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


                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));


                    requestObject1.put("AccNo",IScoreApplication.encryptStart(BankKey) );
                    requestObject1.put("Fk_AccountCode",IScoreApplication.encryptStart(BankKey) );
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(BankKey) );

                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    requestObject1.put("ID_Customer",IScoreApplication.encryptStart(userDetails.customerId));
                    requestObject1.put("MobNo",IScoreApplication.encryptStart(userDetails.userMobileNo));
                    requestObject1.put("CustId",IScoreApplication.encryptStart(userDetails.userCustomerNo));
                    requestObject1.put("CorpCode",IScoreApplication.encryptStart(BankKey) );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCardMiniStatement(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                      /*  try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());

                            if(jObject.getString("StatusCode").equals("0")) {
                                ll_standnginstr.setVisibility(View.VISIBLE);
                                JSONObject jobj = jObject.getJSONObject("StandingInstructionInfo");

                                JSONArray jarray = jobj.getJSONArray("StandingInstructionDetailsList");
                                if(jarray.length()!=0){
                                    GridLayoutManager lLayout = new GridLayoutManager(WalletServiceActivity.this, 1);
                                    rv_standinginst.setLayoutManager(lLayout);
                                    rv_standinginst.setHasFixedSize(true);
                                    StandingInstructionAdapter1 adapter = new StandingInstructionAdapter1(WalletServiceActivity.this, jarray);
                                    rv_standinginst.setAdapter(adapter);
                                }else {
                                    ll_standnginstr.setVisibility(View.GONE);
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }
                            else {

                                try{
                                    JSONObject jobj = jObject.getJSONObject("StandingInstructionInfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }

                        }
                        catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }*/
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { progressDialog.dismiss();}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }


    public void getWalletAmount(){

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(WalletServiceActivity.this, R.style.Progress);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(this.getResources()
                        .getDrawable(R.drawable.progress));
                progressDialog.show();
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
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    requestObject1.put("ID_Customer",IScoreApplication.encryptStart(userDetails.customerId));
                    requestObject1.put("CorpCode",IScoreApplication.encryptStart(BankKey));
                   /* requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCardBalance(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {

                                JSONObject jobj = jObject.getJSONObject("BalanceDetails");
                                txtv_totalbal.setText("\u20B9 "+  CommonUtilities.getDecimelFormate(jobj.getDouble("Balance")));

                            }
                            else{
//                                ll_standnginstr.setVisibility(View.GONE);
//                                llreminder.setVisibility(View.GONE);
                                try{
                                    JSONObject jobj = jObject.getJSONObject("BalanceDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }

                        } catch (JSONException e) {
//                            ll_standnginstr.setVisibility(View.GONE);
//                            llreminder.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { progressDialog.dismiss();}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvLoadmoney:
                tvTransaction.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle3));
                tvLoadmoney.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle1));
                tvTransaction.setTextColor(Color.parseColor("#000000"));
                tvLoadmoney.setTextColor(Color.parseColor("#ffffff"));
                ll_loadmoney.setVisibility(View.VISIBLE);

//                acctype ="1";
//                strHeader="Load Money";
                break;
            case R.id.tvTransaction:
                tvTransaction.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle));
                tvLoadmoney.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle4));
                tvTransaction.setTextColor(Color.parseColor("#ffffff"));
                tvLoadmoney.setTextColor(Color.parseColor("#000000"));
                llministatement.setVisibility(View.VISIBLE);

//                acctype ="2";
//                strHeader="Transactions";
                break;
        }
    }

}

