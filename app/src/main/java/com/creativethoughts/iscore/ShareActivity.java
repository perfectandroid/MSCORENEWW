package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.ShareListInfoAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.Share;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
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
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener{
    public String TAG = "ShareActivity";
    private ProgressDialog progressDialog;
    RecyclerView rv_banklist;
    String token,cusid,type,loantype,name;
    Button btnShare;
    JSONArray Jarray;
    JSONObject jsonObject = null;
    String s,text1,text2;
    public List<Share> shareArrayList ;
    ShareListInfoAdapter adapter;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setRegViews();
        showActiveList();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setRegViews() {

        rv_banklist = (RecyclerView) findViewById(R.id.rv_accountSummaryDetails);
        btnShare = (Button) findViewById(R.id.btnShare);

        btnShare.setOnClickListener(this);
    }

    private void showActiveList() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(ShareActivity.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("14");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    token = tokenIdSP.getString("Token","");
                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid = customerIdSP.getString("customerId","");
                    SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
                    name = customerNameSP.getString("customerName","");
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);


                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode", IScoreApplication.encryptStart("1"));
                    requestObject1.put("LoanType", IScoreApplication.encryptStart("1"));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1   152  "+requestObject1);
                    Log.e(TAG,"requestObject1   152  "+token+"   "+name+"   "+cusid);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCustomerLoanandDeposit(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.e(TAG,"response   1521  "+response.body());

                            Log.i("DepositDetails",response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
                                Log.i("First ",String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ",String.valueOf(object));
                                Jarray  = object.getJSONArray("CustomerLoanAndDepositDetailsList");
                                int length = object.length()+1;
                                if(Jarray.length()!=0) {
                                    rv_banklist.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(ShareActivity.this, 1);
                                    rv_banklist.setLayoutManager(lLayout);
                                    rv_banklist.setHasFixedSize(true);
                                    adapter = new ShareListInfoAdapter(ShareActivity.this, getListData(),Jarray);
                                    rv_banklist.setAdapter(adapter);
                                }
                            }
                            else {
                                rv_banklist.setVisibility(View.GONE);
                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShareActivity.this);
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
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShareActivity.this);
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
            DialogUtil.showAlert(ShareActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

    public   List<Share> getListData() {
        shareArrayList = new ArrayList<>();
        for (int k = 0; k < Jarray.length(); k++) {

            try {

                jsonObject = Jarray.getJSONObject(k);
                shareArrayList.add(new Share(jsonObject.getString("AccountNumber"), jsonObject.getString("IFSCCode"),
                        jsonObject.getString("FundTransferAccount"), jsonObject.getString("BranchName"), jsonObject.getString("LoanType"),name));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return  shareArrayList;
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
        switch (v.getId())
        {
            case R.id.btnShare:

                try {
                    s="";
                    text1="";
                    for (Share model : shareArrayList) {
                        if (model.isSelected()) {

                            text1+=model.getLoantype();
                            s += "Account Type : "+model.getLoantype()+"\n"+"Beneficiary Account : "+model.getFundTransferAccount()+"\n"+"IFSC Code: " +model.getIFSCCode()+"\n"+"\n";
                        }
                    }

                    if (s.length() != 0){
                        Log.i("TAG","Output : " + s);

                        String s1 = s.replaceAll("null", "");
                        String s2 = text1.replaceAll("null", "");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater1.inflate(R.layout.share_popup4, null);
                        TextView tv_msg =  layout.findViewById(R.id.txtv_name);
                        TextView tv_acc =  layout.findViewById(R.id.txtv_acc);
                        TextView tv_ifsc =  layout.findViewById(R.id.txtv_ifsc);
                        TextView tv_fund =  layout.findViewById(R.id.txtv_fund);


                        TextView tv_cancel =  layout.findViewById(R.id.tv_cancel);
                        TextView tv_share =  layout.findViewById(R.id.tv_share);
                        tv_msg.setText("Beneficiary Name : "+name);

                        tv_acc.setText(s1);

                        builder.setView(layout);
                        final AlertDialog alertDialog = builder.create();




                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                alertDialog.dismiss();
                            }
                        });
                        tv_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,/*"Beneficiary Name : "+*/tv_msg.getText().toString() +"\n"+tv_acc.getText().toString());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);


                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                    else{
                        Toast.makeText(this, "Please select account to share.", Toast.LENGTH_SHORT).show();

                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}



//package com.creativethoughts.iscore;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.creativethoughts.iscore.Helper.Common;
//import com.creativethoughts.iscore.Retrofit.APIInterface;
//import com.creativethoughts.iscore.adapters.ShareListInfoAdapter;
//import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
//import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
//import com.creativethoughts.iscore.db.dao.model.Share;
//import com.creativethoughts.iscore.db.dao.model.UserCredential;
//import com.creativethoughts.iscore.db.dao.model.UserDetails;
//import com.creativethoughts.iscore.utility.DialogUtil;
//import com.creativethoughts.iscore.utility.NetworkUtil;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
//import javax.net.ssl.X509TrustManager;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;
//
//public class ShareActivity extends AppCompatActivity implements View.OnClickListener{
//    private ProgressDialog progressDialog;
//    RecyclerView rv_banklist;
//    String token,cusid,type,loantype,name;
//    Button btnShare;
//    JSONArray Jarray;
//    JSONObject jsonObject = null;
//    String s,text1,text2;
//    public List<Share> shareArrayList ;
//    ShareListInfoAdapter adapter;
//
//
//    @Override
//    protected void onCreate( Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_share);
//
//        setRegViews();
//
//
//        showActiveList();
//    }
//
//    private void setRegViews() {
//
//        rv_banklist = (RecyclerView) findViewById(R.id.rv_accountSummaryDetails);
//        btnShare = (Button) findViewById(R.id.btnShare);
//
//        btnShare.setOnClickListener(this);
//    }
//
//    private void showActiveList() {
//        if (NetworkUtil.isOnline()) {
//            progressDialog = new ProgressDialog(ShareActivity.this, R.style.Progress);
//            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
//            progressDialog.setCancelable(false);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setIndeterminateDrawable(this.getResources()
//                    .getDrawable(R.drawable.progress));
//            progressDialog.show();
//            try {
//
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .sslSocketFactory(getSSLSocketFactory())
//                        .hostnameVerifier(getHostnameVerifier())
//                        .build();
//                Gson gson = new GsonBuilder()
//                        .setLenient()
//                        .create();
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl(Common.getBaseUrl())
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .client(client)
//                        .build();
//                APIInterface apiService = retrofit.create(APIInterface.class);
//                String reqmode = IScoreApplication.encryptStart("14");
//                final JSONObject requestObject1 = new JSONObject();
//                try {
//                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
//                    token = loginCredential.token;
//                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//                    cusid = userDetails.customerId;
//                    name = userDetails.userCustomerName;
//                    requestObject1.put("ReqMode",reqmode);
//                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
//                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
//                    requestObject1.put("SubMode", IScoreApplication.encryptStart("1"));
//                    requestObject1.put("LoanType", IScoreApplication.encryptStart("1"));
//                    requestObject1.put("BankKey", IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
//                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
//                Call<String> call = apiService.getCustomerLoanandDeposit(body);
//                call.enqueue(new Callback<String>() {
//
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        progressDialog.dismiss();
//                        try{
//                            Log.i("DepositDetails",response.body());
//                            JSONObject jsonObj = new JSONObject(response.body());
//                            if(jsonObj.getString("StatusCode").equals("0")) {
//                                JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
//                                Log.i("First ",String.valueOf(jsonObj1));
//                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
//                                Log.i("First1 ",String.valueOf(object));
//                                 Jarray  = object.getJSONArray("CustomerLoanAndDepositDetailsList");
//                                int length = object.length()+1;
//                                if(Jarray.length()!=0) {
//                                    rv_banklist.setVisibility(View.VISIBLE);
//                                    GridLayoutManager lLayout = new GridLayoutManager(ShareActivity.this, 1);
//                                    rv_banklist.setLayoutManager(lLayout);
//                                    rv_banklist.setHasFixedSize(true);
//                                    adapter = new ShareListInfoAdapter(ShareActivity.this, getListData(),Jarray);
//                                    rv_banklist.setAdapter(adapter);
//                                }
//                            }
//                            else {
//                                rv_banklist.setVisibility(View.GONE);
//                                try{
//                                    JSONObject jobj = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
//                                    String ResponseMessage = jobj.getString("ResponseMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShareActivity.this);
//                                    builder.setMessage(ResponseMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }catch (JSONException e){
//                                    String EXMessage = jsonObj.getString("EXMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShareActivity.this);
//                                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }
//                            }
//
//                        }
//                        catch (JSONException e) {
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//
//                    }
//                });
//
//            }
//            catch (Exception e)
//            {
//
//            }
//        }
//        else {
//            DialogUtil.showAlert(ShareActivity.this,
//                    "Network is currently unavailable. Please try again later.");
//        }
//    }
//
//    public   List<Share> getListData() {
//        shareArrayList = new ArrayList<>();
//        for (int k = 0; k < Jarray.length(); k++) {
//
//            try {
//
//                jsonObject = Jarray.getJSONObject(k);
//                shareArrayList.add(new Share(jsonObject.getString("AccountNumber"), jsonObject.getString("IFSCCode"),
//                        jsonObject.getString("FundTransferAccount"), jsonObject.getString("BranchName"), jsonObject.getString("LoanType"),name));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return  shareArrayList;
//    }
//
//    private SSLSocketFactory getSSLSocketFactory()
//            throws CertificateException, KeyStoreException, IOException,
//            NoSuchAlgorithmException,
//            KeyManagementException {
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
//        // File path: app\src\main\res\raw\your_cert.cer
//        InputStream caInput =  IScoreApplication.getAppContext().
//                getAssets().open(Common.getCertificateAssetName());
//        Certificate ca = cf.generateCertificate(caInput);
//        caInput.close();
//        KeyStore keyStore = KeyStore.getInstance("BKS");
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, wrappedTrustManagers, null);
//        return sslContext.getSocketFactory();
//    }
//    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
//        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
//        return new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return originalTrustManager.getAcceptedIssuers();
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            if (certs != null && certs.length > 0) {
//                                certs[0].checkValidity();
//                            } else {
//                                originalTrustManager.checkClientTrusted(certs, authType);
//                            }
//                        } catch (CertificateException e) {
//                            Log.w("checkClientTrusted", e.toString());
//                        }
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            if (certs != null && certs.length > 0) {
//                                certs[0].checkValidity();
//                            } else {
//                                originalTrustManager.checkServerTrusted(certs, authType);
//                            }
//                        } catch (CertificateException e) {
//                            Log.w("checkServerTrusted", e.toString());
//                        }
//                    }
//                }
//        };
//    }
//    private HostnameVerifier getHostnameVerifier() {
//        return new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.btnShare:
//
//                    try {
//                    s="";
//                    text1="";
//                    for (Share model : shareArrayList) {
//                        if (model.isSelected()) {
//
//                                text1+=model.getLoantype();
//                                s += "Account Type : "+model.getLoantype()+"\n"+"Beneficiary Account : "+model.getFundTransferAccount()+"\n"+"IFSC Code: " +model.getIFSCCode()+"\n"+"\n";
//                        }
//                    }
//
//                    if (s.length() != 0){
//                        Log.i("TAG","Output : " + s);
//
//                        String s1 = s.replaceAll("null", "");
//                        String s2 = text1.replaceAll("null", "");
//                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                        LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
//                        View layout = inflater1.inflate(R.layout.share_popup4, null);
//                        TextView tv_msg =  layout.findViewById(R.id.txtv_name);
//                        TextView tv_acc =  layout.findViewById(R.id.txtv_acc);
//                        TextView tv_ifsc =  layout.findViewById(R.id.txtv_ifsc);
//                        TextView tv_fund =  layout.findViewById(R.id.txtv_fund);
//
//
//                        TextView tv_cancel =  layout.findViewById(R.id.tv_cancel);
//                        TextView tv_share =  layout.findViewById(R.id.tv_share);
//                        tv_msg.setText("Beneficiary Name : "+name);
//
//                        tv_acc.setText(s1);
//
//                        builder.setView(layout);
//                        final AlertDialog alertDialog = builder.create();
//
//
//
//
//                        tv_cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                alertDialog.dismiss();
//                            }
//                        });
//                        tv_share.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//
//                                Intent sendIntent = new Intent();
//                                sendIntent.setAction(Intent.ACTION_SEND);
//                                sendIntent.putExtra(Intent.EXTRA_TEXT,"Beneficiary Name : "+tv_msg.getText().toString() +"\n"+tv_acc.getText().toString());
//                                sendIntent.setType("text/plain");
//                                startActivity(sendIntent);
//
//
//                                alertDialog.dismiss();
//                            }
//                        });
//                        alertDialog.show();
//                    }
//                    else{
//                        Toast.makeText(this, "Please select account to share.", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
//    }
//}
