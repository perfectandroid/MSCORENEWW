package com.creativethoughts.iscore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Recharge.AccountAdapter;
import com.creativethoughts.iscore.Recharge.OnItemClickListener;
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

public class ChangePinActivity extends AppCompatActivity implements View.OnClickListener {

    public String TAG = "ChangePinActivity";
    private ProgressDialog progressDialog;
    Button cmdSubmit;
    EditText edtOldPin, edtNewPin, edtCnfrmPin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_pin);
        Log.e(TAG,"START  77    ");
        setInitialise();
        setRegister();


    }

    private void setRegister() {
        cmdSubmit.setOnClickListener(this);
    }

    private void setInitialise() {

        cmdSubmit              =  findViewById(R.id.cmdSubmit);
        edtOldPin              =  findViewById(R.id.edtOldPin);
        edtNewPin              =  findViewById(R.id.edtNewPin);
        edtCnfrmPin              =  findViewById(R.id.edtCnfrmPin);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cmdSubmit:
                Log.e(TAG,"cmdSubmit  77    ");
                if (edtOldPin.getText().toString().isEmpty()) {
                    edtOldPin.setError("Please enter old Pin number");
                } else if (edtOldPin.getText().toString().length()!=6) {
                    edtOldPin.setError("Please enter valid Pin number");
                } else if (edtNewPin.getText().toString().isEmpty()) {
                    edtNewPin.setError("Please enter valid Pin number");
                } else if (edtNewPin.getText().toString().length()!=6) {
                    edtNewPin.setError("Please enter valid Pin number");
                }else if (edtCnfrmPin.getText().toString().isEmpty()) {
                    edtCnfrmPin.setError("Please re enter valid Pin number ");
                } else if (edtCnfrmPin.getText().toString().length()!=6) {
                    edtCnfrmPin.setError("Please re enter valid Pin number");
                }   else {
                    if(edtNewPin.getText().toString().equals(edtCnfrmPin.getText().toString())) {
                        doChangePin();
                    }else{
                        edtCnfrmPin.setError("New Pin No and Confirm Pin No does not match.");
                    }
                }

                break;
        }

    }


    private void doChangePin() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(ChangePinActivity.this, R.style.Progress);
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
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    String iemi =   IScoreApplication.getIEMI();

                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("oldPin",IScoreApplication.encryptStart(edtOldPin.getText().toString()));
                    requestObject1.put("newPin",IScoreApplication.encryptStart(edtCnfrmPin.getText().toString()));

                    Log.e(TAG,"requestObject1  1731    "+requestObject1);

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.doChangeMpin(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG,"response  1732    "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                SharedPreferences pinIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                                SharedPreferences.Editor pinIdSPEditer = pinIdSP.edit();
                                pinIdSPEditer.putString("pinlog", edtCnfrmPin.getText().toString());
                                pinIdSPEditer.commit();
                                String EXMessage = jsonObj.getString("ResponseMessage");
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
                                builder.setMessage(EXMessage)
//                                builder.setMessage("No data found.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
//                                                finish();
                                                Intent i = new Intent(ChangePinActivity.this,PinLoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                            else {

                                try{
                                    String EXMessage = jsonObj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
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
                                catch (JSONException e){
                                    String EXMessage = jsonObj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePinActivity.this);
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

}
