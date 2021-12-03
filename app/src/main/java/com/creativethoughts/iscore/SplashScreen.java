package com.creativethoughts.iscore;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class SplashScreen extends AppCompatActivity {
    public static final String BASE_URL="https://202.164.150.65:14264/Mscore/";

    TextView tv_error_message;
    Button btn_proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        tv_error_message = findViewById(R.id.tv_error_message);
        btn_proceed = findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Handler().postDelayed(SplashScreen.this::startUserregistrationActivity, 1000);

            }
        });
        new Handler().postDelayed(SplashScreen.this::getResellerData, 1000);
    }

    private void startUserregistrationActivity() {
        if (UserCredentialDAO.getInstance().isUserAlreadyLogin()) {
            Intent pinLoginActivity =
                    new Intent(SplashScreen.this, PinLoginActivity.class);
            startActivity(pinLoginActivity);
            finish();
            return;
        }
        else {

            Intent intent = new Intent( SplashScreen.this, UserRegistrationActivity.class);
            startActivity(  intent );
            finish();
        }
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput =  IScoreApplication.getAppContext().
                getAssets().open(Common.getCertificateAssetName());
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
                        .baseUrl(Common.getBaseUrl())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {
                    requestObject1.put("ReqMode",/*cusid*/IScoreApplication.encryptStart("15"));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getMaintenanceMessage(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.i("Imagedetails",response.body());
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

                                    if (type.equals("1")) {
                                        tv_error_message.setVisibility(View.VISIBLE);
                                        tv_error_message.setText(message);
                                    }
                                    else if (type.equals("0")) {
                                        tv_error_message.setVisibility(View.VISIBLE);
                                        btn_proceed.setVisibility(View.VISIBLE);
                                        tv_error_message.setText(message);
                                    }
                                    else if (type.equals("-1")) {
                                        String splashScreen = getString(R.string.splash_screen);
                                        if (splashScreen.equals("ON")) {
                                            new Handler().postDelayed(SplashScreen.this::startUserregistrationActivity, 3000);
                                        } else {
                                            startUserregistrationActivity();
                                        }
                                    }
                                }
                            }
                            else {

                                try{
                                    JSONObject jobjt = jObject.getJSONObject("MaintenanceMessage");
                                    String ResponseMessage = jobjt.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreen.this);
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

                                }
                                catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreen.this);
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
            DialogUtil.showAlert(SplashScreen.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private void getResellerData() {
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
                        .baseUrl(Common.getBaseUrl())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("20"));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));

                    Log.e("requestObject1   344   ",""+requestObject1);


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getResellerDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("Imagedetails   344   ",response.body());
                            // Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                            JSONObject jObject = new JSONObject(response.body());
                            String statuscode = jObject.getString("StatusCode");

                            if(statuscode.equals("0")){
                                JSONObject jobjt = jObject.getJSONObject("ResellerDetails");
                                SharedPreferences ResellerNameeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
                                SharedPreferences.Editor ResellerNameeSPEditer = ResellerNameeSP.edit();
                                ResellerNameeSPEditer.putString("ResellerName", jobjt.getString("ResellerName"));
                                ResellerNameeSPEditer.commit();
                                SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                                SharedPreferences.Editor AppIconImageCodeSPEditer = AppIconImageCodeSP.edit();
                                AppIconImageCodeSPEditer.putString("AppIconImageCode", jobjt.getString("AppIconImageCode"));
                                AppIconImageCodeSPEditer.commit();
                                SharedPreferences CompanyLogoImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                                SharedPreferences.Editor CompanyLogoImageCodeSPEditer = CompanyLogoImageCodeSP.edit();
                                CompanyLogoImageCodeSPEditer.putString("CompanyLogoImageCode", jobjt.getString("CompanyLogoImageCode"));
                                CompanyLogoImageCodeSPEditer.commit();
                                SharedPreferences ProductNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                                SharedPreferences.Editor ProductNameSPEditer = ProductNameSP.edit();
                                ProductNameSPEditer.putString("ProductName", jobjt.getString("ProductName"));
                                ProductNameSPEditer.commit();
                                SharedPreferences PlayStoreLinkSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                                SharedPreferences.Editor PlayStoreLinkSPEditer = PlayStoreLinkSP.edit();
                                PlayStoreLinkSPEditer.putString("PlayStoreLink",jobjt.getString("PlayStoreLink"));
                                PlayStoreLinkSPEditer.commit();
                                SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                                baseurlEditer.putString("baseurl",BASE_URL);
                                baseurlEditer.commit();

                                getMaintenanceMessage();

                            }

                            else {
                                try{
                                    JSONObject jobjt = jObject.getJSONObject("ResellerDetails");
                                    String ResponseMessage = jobjt.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreen.this);
                                    builder.setMessage(ResponseMessage)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }
                                catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreen.this);
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
            }
            catch (Exception e) {
                Log.i("Imagedetails",e.toString());

                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(SplashScreen.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

}
