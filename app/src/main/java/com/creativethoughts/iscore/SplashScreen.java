package com.creativethoughts.iscore;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
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

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class SplashScreen extends AppCompatActivity {


     public static final String BASE_URL="https://202.164.150.65:14264/MscoreQA";
   // public static final String BASE_URL="https://202.164.150.65:14264/Mscore";
    public static final String IMAGE_URL="https://202.164.150.65:14264/";
  //  public static final String API_NAME= "api/MV3";
    public static final String BankKey= "d.22333";
    public static final String BankHeader= "PERFECT SCORE BANK HEAD OFFICE";
    public static final String HOSTNAME_SUBJECT="STATIC-VM";
    public static final String CERTIFICATE_ASSET_NAME="staticvm.pem";




    // Perfect test
//    public static final String BASE_URL="https://112.133.227.123:1400/TESTMSCORE";
//    public static final String IMAGE_URL="https://112.133.227.123:1400/TESTMSCORE";
//    public static final String API_NAME= "api/MV3";
//    public static final String BankKey= "2504";
//    public static final String BankHeader= "PERFECT SCORE BANK QUALITY OFFICE";
//    public static final String HOSTNAME_SUBJECT="TEST16";
//    public static final String CERTIFICATE_ASSET_NAME="mscoredemotest.pem";

    // NADUVIL live
//    public static final String BASE_URL="https://13.71.91.134:14009/Mscore/";
//    public static final String IMAGE_URL="https://13.71.91.134:14009/";
//    public static final String API_NAME= "api/MV3";
//    public static final String BankKey= "LL.136";
//    public static final String BankHeader= "Naduvil Service Co-operative Bank Ltd.No.LL.136 HEAD OFFICE";
//    public static final String HOSTNAME_SUBJECT="MSCORESERVER";
//    public static final String CERTIFICATE_ASSET_NAME="naduviltest.pem";

    // NADUVIL test
//    public static final String BASE_URL="https://13.71.91.134:14008/MscoreQALive";
//    public static final String IMAGE_URL="https://13.71.91.134:14008/MscoreQALive";
//    public static final String API_NAME= "api/MV3";
//    public static final String BankKey= "D.2721";
//    public static final String BankHeader= "PERFECT SCORE BANK HEAD OFFICE";
//    public static final String HOSTNAME_SUBJECT="MSCORESERVER";
//    public static final String CERTIFICATE_ASSET_NAME="naduviltesting.pem";


//    LIVE
    /*public static final String BASE_URL="https://13.71.91.134:14006/Mscore";
    public static final String IMAGE_URL="https://13.71.91.134:14006/";
    public static final String API_NAME= "api/MV3";
    public static final String BankKey= "D.2721";
    public static final String BankHeader= "Atholi Service Co-operative Bank Ltd., No.D.2721 Head Office Cum Main Branch";
    public static final String HOSTNAME_SUBJECT="MSCORESERVER";
    public static final String CERTIFICATE_ASSET_NAME="mscorelive.pem";*/


    // kuruvattoor

 /*   public static final String BASE_URL="https://103.78.221.136:14001/MSCORE/";
    public static final String IMAGE_URL="https://103.78.221.136:14001/";
    public static final String API_NAME= "api/MV3";
    public static final String BankKey= "";
    public static final String BankHeader= "";
    public static final String HOSTNAME_SUBJECT="backoffice28";
    public static final String CERTIFICATE_ASSET_NAME="mscorelive.pem";*/


    static String bank_Key, bank_Header;
    TextView tv_error_message;
    Button btn_proceed;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        tv_error_message = findViewById(R.id.tv_error_message);
        btn_proceed = findViewById(R.id.btn_proceed);
        imageView = findViewById(R.id.imageView);

        btn_proceed.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Handler().postDelayed(SplashScreen.this::startUserregistrationActivity, 1000);

            }
        });

        SharedPreferences hostnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
        SharedPreferences.Editor hostnameEditer = hostnameSP.edit();
        hostnameEditer.putString("hostname", HOSTNAME_SUBJECT);
        hostnameEditer.commit();

        SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
        SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
        assetnameEditer.putString("certificateassetname", CERTIFICATE_ASSET_NAME);
        assetnameEditer.commit();

        SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
        bank_Key=bankkeypref.getString("bankkey", null);
        SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
        bank_Header=bankheaderpref.getString("bankheader", null);

        new Handler().postDelayed(SplashScreen.this::getResellerData, 1000);



    }

    private void startUserregistrationActivity() {
        SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF33, 0);
        if (loginSP.getString("login","").equals("0")){
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

                 //   requestObject1.put("BankKey" +"" +"",IScoreApplication.encryptStart(BankKey));
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
        Log.e("TAG","BASE_URL   2351 "+BASE_URL);
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
                        .baseUrl(BASE_URL+"/")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                final JSONObject requestObject1 = new JSONObject();
                try {
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("20"));
                    String s= BankKey;
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

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
                            Log.e("getResellerData","   344   "+response.body());
                            // Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                            JSONObject jObject = new JSONObject(response.body());
                            String statuscode = jObject.getString("StatusCode");

                            SharedPreferences versnSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0);
                            SharedPreferences.Editor versnEditer = versnSP.edit();

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
                                SharedPreferences EwireCardServiceSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF12, 0);
                                SharedPreferences.Editor EwireCardServiceEditer = EwireCardServiceSP.edit();
                                EwireCardServiceEditer.putString("EwireCardService",jobjt.getString("EwireCardService"));
                                EwireCardServiceEditer.commit();

                                SharedPreferences TestMobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF15, 0);
                                SharedPreferences.Editor TestMobileNoEditer = TestMobileNoSP.edit();
                                TestMobileNoEditer.putString("TestingMobileNo",jobjt.getString("TestingMobileNo"));
                                TestMobileNoEditer.commit();

                                SharedPreferences baseurlSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF16, 0);
                                SharedPreferences.Editor baseurlEditer1 = baseurlSP1.edit();
                                baseurlEditer1.putString("testbaseurl", jobjt.getString("TestingURL") + "/");
                                baseurlEditer1.commit();

//                                SharedPreferences oldbaseurlSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF17, 0);
//                                SharedPreferences.Editor oldbaseurlEditer1 = oldbaseurlSP1.edit();
//                                oldbaseurlEditer1.putString("testoldbaseurl", jobjt.getString("TestingURL") + "/" + API_NAME);
//                                oldbaseurlEditer1.commit();

                                SharedPreferences imageurlSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF18, 0);
                                SharedPreferences.Editor imageurlEditer1 = imageurlSP1.edit();
                                imageurlEditer1.putString("testimageurl", jobjt.getString("TestingImageURL"));
                                imageurlEditer1.commit();
                                SharedPreferences bankkeySP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF19, 0);
                                SharedPreferences.Editor bankkeyEditer1 = bankkeySP1.edit();
                                bankkeyEditer1.putString("testbankkey", jobjt.getString("BankKey"));
                                bankkeyEditer1.commit();

                                SharedPreferences bankheaderSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF20, 0);
                                SharedPreferences.Editor bankheaderEditer1 = bankheaderSP1.edit();
                                bankheaderEditer1.putString("testbankheader", jobjt.getString("BankHeader"));
                                bankheaderEditer1.commit();

                                SharedPreferences hostnameSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF21, 0);
                                SharedPreferences.Editor hostnameEditer1 = hostnameSP1.edit();
                                hostnameEditer1.putString("testhostname", jobjt.getString("HostName"));
                                hostnameEditer1.commit();

                                SharedPreferences assetnameSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF22, 0);
                                SharedPreferences.Editor assetnameEditer1 = assetnameSP1.edit();
                                assetnameEditer1.putString("testcertificateassetname", jobjt.getString("AssetName"));
                                assetnameEditer1.commit();


                                SharedPreferences versionSP1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0);
                                SharedPreferences.Editor versionSP1Editer1 = versionSP1.edit();
                                versionSP1Editer1.putString("version", jobjt.getString("VersionCode"));
                                versionSP1Editer1.commit();


                                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
                                String strloginmobile=pref.getString("LoginMobileNo", null);

                                if(strloginmobile == null || strloginmobile.isEmpty()) {

                                    versnEditer.putString("version", "false");
                                    versnEditer.commit();
                                    Log.i("empty","FALSE");

                                    SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                                    baseurlEditer.putString("baseurl", BASE_URL + "/");
                                    baseurlEditer.commit();

//                                    SharedPreferences oldbaseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                                    SharedPreferences.Editor oldbaseurlEditer = oldbaseurlSP.edit();
//                                    oldbaseurlEditer.putString("oldbaseurl", BASE_URL + "/" + API_NAME);
//                                    oldbaseurlEditer.commit();

                                    SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                                    SharedPreferences.Editor imageurlEditer = imageurlSP.edit();
                                    imageurlEditer.putString("imageurl", IMAGE_URL);
                                    imageurlEditer.commit();

                                    SharedPreferences bankkeySP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                                    SharedPreferences.Editor bankkeyEditer = bankkeySP.edit();
                                    bankkeyEditer.putString("bankkey", BankKey);
                                    bankkeyEditer.commit();

                                    SharedPreferences bankheaderSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                                    SharedPreferences.Editor bankheaderEditer = bankheaderSP.edit();
                                    bankheaderEditer.putString("bankheader", BankHeader);
                                    bankheaderEditer.commit();

                                    SharedPreferences hostnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                                    SharedPreferences.Editor hostnameEditer = hostnameSP.edit();
                                    hostnameEditer.putString("hostname", HOSTNAME_SUBJECT/*jobjt.getString("BankKey")*/);
                                    hostnameEditer.commit();

                                    SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                                    SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
                                    assetnameEditer.putString("certificateassetname", CERTIFICATE_ASSET_NAME/*jobjt.getString("BankHeader")*/);
                                    assetnameEditer.commit();
                                }
                                else {
                                    if (jobjt.getString("TestingMobileNo").equals(strloginmobile)) {
                                        if (jobjt.getString("TestingURL").isEmpty() && jobjt.getString("TestingImageURL").isEmpty()
                                                && jobjt.getString("BankKey").isEmpty() && jobjt.getString("BankHeader").isEmpty()) {

                                            versnEditer.putString("version", "false");
                                            versnEditer.commit();
                                            Log.i("empty1","FALSE");

                                            SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                            SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                                            baseurlEditer.putString("baseurl", BASE_URL + "/");
                                            baseurlEditer.commit();

//                                            SharedPreferences oldbaseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                                            SharedPreferences.Editor oldbaseurlEditer = oldbaseurlSP.edit();
//                                            oldbaseurlEditer.putString("oldbaseurl", BASE_URL + "/" + API_NAME);
//                                            oldbaseurlEditer.commit();

                                            SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                                            SharedPreferences.Editor imageurlEditer = imageurlSP.edit();
                                            imageurlEditer.putString("imageurl", IMAGE_URL);
                                            imageurlEditer.commit();

                                            SharedPreferences bankkeySP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                                            SharedPreferences.Editor bankkeyEditer = bankkeySP.edit();
                                            bankkeyEditer.putString("bankkey", BankKey);
                                            bankkeyEditer.commit();

                                            SharedPreferences bankheaderSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                                            SharedPreferences.Editor bankheaderEditer = bankheaderSP.edit();
                                            bankheaderEditer.putString("bankheader", BankHeader);
                                            bankheaderEditer.commit();

                                            SharedPreferences hostnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                                            SharedPreferences.Editor hostnameEditer = hostnameSP.edit();
                                            hostnameEditer.putString("hostname", HOSTNAME_SUBJECT);
                                            hostnameEditer.commit();

                                            SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                                            SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
                                            assetnameEditer.putString("certificateassetname", CERTIFICATE_ASSET_NAME);
                                            assetnameEditer.commit();

                                        }
                                        else {

                                            Log.i("empty2","TRUE");
                                            versnEditer.putString("version", "true");
                                            versnEditer.commit();

                                            SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                            SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                                            baseurlEditer.putString("baseurl", jobjt.getString("TestingURL") + "/");
                                            baseurlEditer.commit();

//                                            SharedPreferences oldbaseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                                            SharedPreferences.Editor oldbaseurlEditer = oldbaseurlSP.edit();
//                                            oldbaseurlEditer.putString("oldbaseurl", jobjt.getString("TestingURL") + "/" + API_NAME);
//                                            oldbaseurlEditer.commit();

                                            SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                                            SharedPreferences.Editor imageurlEditer = imageurlSP.edit();
                                            imageurlEditer.putString("imageurl", jobjt.getString("TestingImageURL"));
                                            imageurlEditer.commit();

                                            SharedPreferences bankkeySP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                                            SharedPreferences.Editor bankkeyEditer = bankkeySP.edit();
                                            bankkeyEditer.putString("bankkey", jobjt.getString("BankKey"));
                                            bankkeyEditer.commit();

                                            SharedPreferences bankheaderSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                                            SharedPreferences.Editor bankheaderEditer = bankheaderSP.edit();
                                            bankheaderEditer.putString("bankheader", jobjt.getString("BankHeader"));
                                            bankheaderEditer.commit();

                                            SharedPreferences hostnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                                            SharedPreferences.Editor hostnameEditer = hostnameSP.edit();
                                            hostnameEditer.putString("hostname", jobjt.getString("HostName"));
                                            hostnameEditer.commit();

                                            SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                                            SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
                                            assetnameEditer.putString("certificateassetname", jobjt.getString("AssetName"));
                                            assetnameEditer.commit();
                                        }
                                    }
                                    else {

                                        Log.i("empty","FALSE");

                                        versnEditer.putString("version", "false");
                                        versnEditer.commit();


                                        SharedPreferences baseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                        SharedPreferences.Editor baseurlEditer = baseurlSP.edit();
                                        baseurlEditer.putString("baseurl", BASE_URL + "/");
                                        baseurlEditer.commit();

//                                        SharedPreferences oldbaseurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//                                        SharedPreferences.Editor oldbaseurlEditer = oldbaseurlSP.edit();
//                                        oldbaseurlEditer.putString("oldbaseurl", BASE_URL + "/" + API_NAME);
//                                        oldbaseurlEditer.commit();

                                        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                                        SharedPreferences.Editor imageurlEditer = imageurlSP.edit();
                                        imageurlEditer.putString("imageurl", IMAGE_URL);
                                        imageurlEditer.commit();

                                        SharedPreferences bankkeySP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                                        SharedPreferences.Editor bankkeyEditer = bankkeySP.edit();
                                        bankkeyEditer.putString("bankkey", BankKey);
                                        bankkeyEditer.commit();

                                        SharedPreferences bankheaderSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                                        SharedPreferences.Editor bankheaderEditer = bankheaderSP.edit();
                                        bankheaderEditer.putString("bankheader", BankHeader);
                                        bankheaderEditer.commit();

                                        SharedPreferences hostnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0);
                                        SharedPreferences.Editor hostnameEditer = hostnameSP.edit();
                                        hostnameEditer.putString("hostname", HOSTNAME_SUBJECT);
                                        hostnameEditer.commit();

                                        SharedPreferences assetnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
                                        SharedPreferences.Editor assetnameEditer = assetnameSP.edit();
                                        assetnameEditer.putString("certificateassetname", CERTIFICATE_ASSET_NAME);
                                        assetnameEditer.commit();
                                    }
                                }

                              /* */

                                SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
                                String IMAGEURL = imageurlSP.getString("imageurl","");
                                String AppIconImageCodePath =IMAGEURL+jobjt.getString("AppIconImageCode");
                                PicassoTrustAll.getInstance(SplashScreen.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(imageView);
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
                            Log.e("TAG","Exception   651   "+e.toString());

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Imagedetails","Something went wrong");
                    }
                });
            }
            catch (Exception e) {
                Log.e("Imagedetails",e.toString());

                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(SplashScreen.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

//    public static String getBankkey() {
//        try {
//
//            if (bank_Key.isEmpty()){
//                return BankKey;
//            }else {
//                return bank_Key;
//            }
//        }catch (Exception e){
//            return IScoreApplication.EXCEPTION_NOIEMI;
//        }
//    }
//
//    public static String getBankheader() {
//        try {
//
//            if (bank_Header.isEmpty()){
//                return BankHeader;
//            }else {
//                return bank_Header;
//            }
//        }catch (Exception e){
//            return IScoreApplication.EXCEPTION_NOIEMI;
//        }
//    }

}
