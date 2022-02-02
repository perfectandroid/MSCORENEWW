package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.creativethoughts.iscore.Helper.Common;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ContactusActivity extends AppCompatActivity {

    String TAG ="ContactusActivity";
    private ProgressDialog progressDialog;
    TextView txtv_bankname,txtv_branchname,txtv_address,txtv_hour,txtv_land,txtv_mob;
    String cusid,tokenn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        txtv_bankname = findViewById(R.id.txtv_bankname);
        txtv_branchname = findViewById(R.id.txtv_branchname);
        txtv_bankname = findViewById(R.id.txtv_bankname);
        txtv_address = findViewById(R.id.txtv_address);
        txtv_hour = findViewById(R.id.txtv_hour);
        txtv_land = findViewById(R.id.txtv_land);
        txtv_mob = findViewById(R.id.txtv_mob);



        SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
        cusid = customerIdSP.getString("customerId","");
        SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
        tokenn = tokenIdSP.getString("Token","");

        showBankDetails();
      //  showBankDetails1();

     //   getMaintenanceMessage();
    }

    private void showBankDetails1() {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        Log.e("TAG","BASE_URL   235 "+BASE_URL);
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


                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("1"));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(tokenn));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1  128  "+requestObject1);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getBankbranchList(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("response  341   ",response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0")){

                            }
                            else {

                                try{


                                }
                                catch (Exception e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactusActivity.this);
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

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Imagedetails","Something went wrong");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(ContactusActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private void showBankDetails() {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        Log.e("TAG","BASE_URL   235 "+BASE_URL);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(ContactusActivity.this, R.style.Progress);
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

                Log.e(TAG,"BASE_URL   "+BASE_URL);

                String reqmode = IScoreApplication.encryptStart("1");
                String submode = IScoreApplication.encryptStart("1");
                String token = IScoreApplication.encryptStart(tokenn);
                String fkcus = IScoreApplication.encryptStart(cusid);
                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("SubMode",submode);
                    requestObject1.put("Token",token);
                    requestObject1.put("FK_Customer",fkcus);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1  128  "+requestObject1);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getBankbranchList(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try {
                            Log.e("Locationdetails", response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")){
                                JSONObject jsonObj1 = jsonObj.getJSONObject("BankBranchDetailsListInfo");
                                Log.i("First ", String.valueOf(jsonObj1));
                                String addrss = jsonObj1.getString("Address") + "," + jsonObj1.getString("Place") + "," + "\n" +
                                        jsonObj1.getString("Post") + "," + "\n" + jsonObj1.getString("District");
                                txtv_address.setText(addrss);
                                String moble = jsonObj1.getString("ContactPersonMobile")+", "+jsonObj1.getString("BranchMobileNumber");
                                txtv_mob.setText(moble);
                                String landline = jsonObj1.getString("LandPhoneNumber");
                                txtv_land.setText(landline);
                                String bnk = jsonObj1.getString("BankName");
                                txtv_bankname.setText(bnk);
                                String brnch = jsonObj1.getString("BranchName");
                                txtv_branchname.setText(brnch);
                                String hrs = jsonObj1.getString("OpeningTime")+" - "+jsonObj1.getString("ClosingTime");
                                txtv_hour.setText(hrs);
                            }
                            else {
                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("BankBranchDetailsListInfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactusActivity.this);
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
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactusActivity.this);
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

                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        }catch (Exception e)
                        {

                            Log.e(TAG,"Exception  200   "+e.toString());

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
            DialogUtil.showAlert(ContactusActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }

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

    private void getMaintenanceMessage() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        Log.e("TAG","BASE_URL   235 "+BASE_URL);
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
                    requestObject1.put("ReqMode",/*cusid*/IScoreApplication.encryptStart("15"));

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
                Call<String> call = apiService.getMaintenanceMessage(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("response  341   ",response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0")){
                                JSONObject jobjt = jObject.getJSONObject("MaintenanceMessage");
                                JSONArray maintenancejobjt = jobjt.getJSONArray("MaintenanceMessageList");
                                JSONObject MaintenanceMessageList =  maintenancejobjt.getJSONObject(0);
                                String type = MaintenanceMessageList.getString("Type");

                                String message = "";

                                if (maintenancejobjt.length() != 0 ){
                                    for (int i = 0; i < maintenancejobjt.length(); i++) {
                                        JSONObject MaintenanceMessageL =  maintenancejobjt.getJSONObject(i);
                                        if (type.equals("1")){
                                            message +=   ""+ MaintenanceMessageL.getString("Description");
                                        }
                                        else {

                                            if (i== 0){
                                                message +=   ""+ (i+1) +" - " + MaintenanceMessageL.getString("Description");
                                            }
                                            else{
                                                message +=   "\n"+ (i+1) +" - " + MaintenanceMessageL.getString("Description");
                                            }
                                        }

                                    }

                                }
                            }
                            else {

                                try{


                                }
                                catch (Exception e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactusActivity.this);
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

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Imagedetails","Something went wrong");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(ContactusActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

}
