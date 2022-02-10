package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginOTPActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressDialog progressDialog;
    Button btnVerify;
    private EditText mEtVerificationCode;
    public String TAG = "LoginOTPActivity";


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        Log.e(TAG,"START  68  ");
        btnVerify = findViewById(R.id.btnVerify);
        mEtVerificationCode =   findViewById(R.id.verificationCode);
        btnVerify.setOnClickListener(this);




    }

    public void OTPVerification(){

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(LoginOTPActivity.this, R.style.Progress);
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



                    SharedPreferences countryCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF34, 0);
                    String countryCode = countryCodeSP.getString("countryCode","");
                    SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                    String mobileNumber = mobileNoSP.getString("mobileNo","");

                    String otp = mEtVerificationCode.getText().toString();

                    requestObject1.put("MobileNo",IScoreApplication.encryptStart(countryCode +
                            mobileNumber) );
                    requestObject1.put("OTPCode",IScoreApplication.encryptStart(otp) );
                    requestObject1.put("NoOfDays",IScoreApplication.encryptStart("30") );
                    requestObject1.put("Token",IScoreApplication.encryptStart("token") );
                    requestObject1.put("IMEI",IScoreApplication.encryptStart("123456789"));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getVerifyOTP(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());

                            if(jObject.getString("StatusCode").equals("0")) {

                               // JSONArray jarray = jObject.getJSONArray("acInfo");
                               // JSONObject jobj=jarray.getJSONObject(0);

                               /* String customerId=   jobj.getString("customerId");
                                String customerNo=   jobj.getString("customerNo");
                                String customerName=   jobj.getString("customerName");

                                String customerAddress1=   jobj.getString("customerAddress1");
                                String customerAddress2=   jobj.getString("customerAddress2");
                                String customerAddress3=   jobj.getString("customerAddress3");

                                String mobileNo=   jobj.getString("mobileNo");
                                String login=   jobj.getString("login");
                                String TokenNo=   jobj.getString("TokenNo");


                                Date date = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                                String formattedDate = df.format(date);


                                SharedPreferences TestingMobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
                                SharedPreferences.Editor TestingMobileNoEditer = TestingMobileNoSP.edit();
                                TestingMobileNoEditer.putString("LoginMobileNo", mobileNo);
                                TestingMobileNoEditer.commit();

                                SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                SharedPreferences.Editor loginEditer = loginSP.edit();
                                loginEditer.putString("logintime", formattedDate);
                                loginEditer.commit();

                                SharedPreferences ImageSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                SharedPreferences.Editor imageEditer = ImageSP.edit();
                                imageEditer.putString("custimage", "");
                                imageEditer.commit();
                                Intent passBookAccount = new Intent(LoginOTPActivity.this, HomeActivity.class);
                                startActivity(passBookAccount);
                                finish();*/

                                JSONArray jArray3 = jObject.getJSONArray("acInfo");

                                for(int i = 0; i < jArray3 .length(); i++)
                                {
                                    JSONObject object3 = jArray3.getJSONObject(i);


                                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                                    SharedPreferences.Editor customerIdEditer = customerIdSP.edit();
                                    customerIdEditer.putString("customerId",  object3.getString("customerId"));
                                    customerIdEditer.commit();

                                    SharedPreferences customerNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF27, 0);
                                    SharedPreferences.Editor customerNoEditer = customerNoSP.edit();
                                    customerNoEditer.putString("customerNo",  object3.getString("customerNo"));
                                    customerNoEditer.commit();

                                    SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
                                    SharedPreferences.Editor customerNameEditer = customerNameSP.edit();
                                    customerNameEditer.putString("customerName", object3.getString("customerName"));
                                    customerNameEditer.commit();

                                    SharedPreferences customerAddress1SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF29, 0);
                                    SharedPreferences.Editor customerAddress1Editer = customerAddress1SP.edit();
                                    customerAddress1Editer.putString("customerAddress1", object3.getString("customerAddress1"));
                                    customerAddress1Editer.commit();

                                    SharedPreferences customerAddress2SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF30, 0);
                                    SharedPreferences.Editor customerAddress2Editer = customerAddress2SP.edit();
                                    customerAddress2Editer.putString("customerAddress2", object3.getString("customerAddress2"));
                                    customerAddress2Editer.commit();

                                    SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                                    SharedPreferences.Editor mobileNoEditer = mobileNoSP.edit();
                                    mobileNoEditer.putString("mobileNo",  object3.getString("mobileNo"));
                                    mobileNoEditer.commit();

                                    SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                                    SharedPreferences.Editor BankVerifierEditer = BankVerifierSP.edit();
                                    BankVerifierEditer.putString("BankVerifier",  "1");
                                    BankVerifierEditer.commit();

                                    SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF33, 0);
                                    SharedPreferences.Editor loginEditer = loginSP.edit();
                                    loginEditer.putString("login", "0");
                                    loginEditer.commit();

                                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                                    SharedPreferences.Editor tokenIdSPEditer = tokenIdSP.edit();
                                    tokenIdSPEditer.putString("Token", object3.getString("TokenNo"));
                                    tokenIdSPEditer.commit();



                                    SharedPreferences pinIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                                    SharedPreferences.Editor pinIdSPEditer = pinIdSP.edit();
                                    pinIdSPEditer.putString("pinlog", object3.getString("pin"));
                                    pinIdSPEditer.commit();

                                    SharedPreferences customerAddress3SP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF42, 0);
                                    SharedPreferences.Editor customerAddress3Editer = customerAddress3SP.edit();
                                    customerAddress3Editer.putString("customerAddress3", object3.getString("customerAddress3"));
                                    customerAddress3Editer.commit();


                                    JSONObject jOBJ = jArray3.getJSONObject(i);
                                    JSONArray jArray4 = jOBJ.getJSONArray("accounts");

                                    SharedPreferences accntIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF43, 0);
                                    SharedPreferences.Editor accntSPEditer = accntIdSP.edit();
                                    accntSPEditer.putString("accountNoarray", String.valueOf(jArray4));
                                    accntSPEditer.commit();

//                        String strDMenu =object3.getString("DMenu");
//                        JSONObject jobjDMenu = object3.getString("DMenu"));

                                    try{
                                        JSONObject jobjDMenu = new JSONObject(object3.getString("DMenu"));

                                        SharedPreferences RechargeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF44, 0);
                                        SharedPreferences.Editor RechargeEditer = RechargeSP.edit();
                                        RechargeEditer.putString("Recharge",jobjDMenu.getString("Recharge") );
                                        RechargeEditer.commit();

                                        SharedPreferences ImpsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF45, 0);
                                        SharedPreferences.Editor ImpsEditer = ImpsSP.edit();
                                        ImpsEditer.putString("Imps",jobjDMenu.getString("Imps") );
                                        ImpsEditer.commit();

                                        SharedPreferences RtgsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF46, 0);
                                        SharedPreferences.Editor RtgsEditer = RtgsSP.edit();
                                        RtgsEditer.putString("Rtgs",jobjDMenu.getString("Rtgs") );
                                        RtgsEditer.commit();

                                        SharedPreferences KsebSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF47, 0);
                                        SharedPreferences.Editor KsebEditer = KsebSP.edit();
                                        KsebEditer.putString("Kseb",jobjDMenu.getString("Kseb") );
                                        KsebEditer.commit();

                                        SharedPreferences NeftSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF48, 0);
                                        SharedPreferences.Editor NeftEditer = NeftSP.edit();
                                        NeftEditer.putString("Neft",jobjDMenu.getString("Neft") );
                                        NeftEditer.commit();

                                        SharedPreferences OwnImpsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF49, 0);
                                        SharedPreferences.Editor OwnImpsEditer = OwnImpsSP.edit();
                                        OwnImpsEditer.putString("OwnImps",jobjDMenu.getString("OwnImps") );
                                        OwnImpsEditer.commit();

                                    }
                                    catch (Exception e){

                                    }

                                    Date date = Calendar.getInstance().getTime();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                                    String formattedDate = df.format(date);


                                    SharedPreferences TestingMobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
                                    SharedPreferences.Editor TestingMobileNoEditer = TestingMobileNoSP.edit();
                                    TestingMobileNoEditer.putString("LoginMobileNo", object3.getString("mobileNo"));
                                    TestingMobileNoEditer.commit();

                                    SharedPreferences logintimeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                    SharedPreferences.Editor logintimeEditer = logintimeSP.edit();
                                    logintimeEditer.putString("logintime", formattedDate);
                                    logintimeEditer.commit();

                                    SharedPreferences ImageSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                    SharedPreferences.Editor imageEditer = ImageSP.edit();
                                    imageEditer.putString("custimage", "");
                                    imageEditer.commit();
                                    Intent passBookAccount = new Intent(LoginOTPActivity.this, HomeActivity.class);
                                    startActivity(passBookAccount);
                                    finish();


                      /*  for (int j = 0; j < jArray4.length(); j++)
                        {
                            JSONObject obj2 = jArray4.getJSONObject(j);


                            SharedPreferences accntIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF43, 0);
                            SharedPreferences.Editor accntSPEditer = accntIdSP.edit();
                            accntSPEditer.putString("accountNo",  obj2 .getString("acno"));
                            accntSPEditer.commit();

                            SharedPreferences typeshrtSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF44, 0);
                            SharedPreferences.Editor typeshrtSPEditer = typeshrtSP.edit();
                            typeshrtSPEditer.putString("typeShort",  obj2 .getString("typeShort"));
                            typeshrtSPEditer.commit();

                        }*/
                                }
                            }
                            else{
                                try{
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginOTPActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginOTPActivity.this);
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
            DialogUtil.showAlert(LoginOTPActivity.this,
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
            case R.id.btnVerify:

                if (mEtVerificationCode.getText().toString().isEmpty()) {
                    mEtVerificationCode.setError("Please enter OTP");
                } else if (mEtVerificationCode.getText().toString().length()!=6) {
                    mEtVerificationCode.setError("Please enter 6 digit OTP");
                }   else {
                    OTPVerification();
                }
                break;
        }
    }

}
