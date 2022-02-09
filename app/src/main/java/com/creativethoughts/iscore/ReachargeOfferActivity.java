package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Recharge.OfferListAdapter;
import com.creativethoughts.iscore.Recharge.OnItemClickListener;
import com.creativethoughts.iscore.Recharge.RechargeHeaderAdapter;
import com.creativethoughts.iscore.Retrofit.APIInterface;
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
import java.util.ArrayList;
import java.util.Iterator;

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
//implements OnItemClickListener
public class ReachargeOfferActivity extends AppCompatActivity implements OnItemClickListener{

    String TAG = "ReachargeOfferActivity";
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<Fragment> fragments;
    ProgressDialog progressDialog = null;
    RecyclerView rv_rechrgehead;
    RecyclerView rv_offerlist;
    ArrayList<String> arrayHeader;
    JSONObject jObject4 = null;
    TextView tv_header;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacharge_offers);

        rv_rechrgehead = (RecyclerView)findViewById(R.id.rv_rechrgehead) ;
        rv_offerlist = (RecyclerView)findViewById(R.id.rv_offerlist) ;
        tv_header = findViewById(R.id.tv_header) ;



//        Intent intent = new Intent();
//        intent.putExtra("editTextValue","250.1" );
//        setResult(RESULT_OK, intent);
//        finish();

//        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF10, 0);
//        String operatorIds = pref.getString("operatorIds", null);
//        if (operatorIds.equals("1")){
//            tv_header.setText("Airtel");
//        }
//        else if (operatorIds.equals("2")){
//            tv_header.setText("V!");
//        }
//        else if (operatorIds.equals("3")){
//            tv_header.setText("BSNL");
//        }
//        else if (operatorIds.equals("400")){
//            tv_header.setText("Jio");
//        }
//        else if (operatorIds.equals("18")){
//            tv_header.setText("Dish TV DTH");
//        }
//        else if (operatorIds.equals("19")){
//            tv_header.setText("Tata Sky DTH");
//        }
//        else if (operatorIds.equals("20")) {
//            tv_header.setText("Big TV DTH");
//        }
//        else if (operatorIds.equals("21")){
//            tv_header.setText("Videocon DTH");
//        }
//        else if (operatorIds.equals("22")){
//            tv_header.setText("Sun DTH");
//        }
//        else if (operatorIds.equals("23")){
//            tv_header.setText("Airtel DTH");
//        }

        String operatorIds = getIntent().getStringExtra("operatorIds");
        tv_header.setText(""+getIntent().getStringExtra("operatorName"));
        Log.e(TAG,"operatorIds   164    "+getIntent().getStringExtra("operatorIds"));
     //   Log.e(TAG,"operatorIds   164    "+pref.getString("operatorIds", null));

        getDataUsingOperator(operatorIds);



    }

    private void getDataUsingOperator(String operatorIds) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        try{
            progressDialog = new ProgressDialog(this, R.style.Progress);
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

                requestObject1.put("Operator",operatorIds);

                SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                String BankKey=bankkeypref.getString("bankkey", null);
                SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                String BankHeader=bankheaderpref.getString("bankheader", null);
                requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                Log.e(TAG,"requestObject1   165    "+requestObject1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"requestObject1   168    "+e.toString());
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getRechargeOffer(body);
            call.enqueue(new Callback<String>() {
                @Override public void onResponse(Call<String> call, Response<String> response) {
                    progressDialog.dismiss();

//                    Log.e(TAG,"onResponse  1731   ");
//                    String strBody  = response.body().substring(1,response.body().length()-1);
//                    Log.e(TAG,"onResponse  1732   ");
                    try {
                        Log.e(TAG,"onResponse  180   "+response.body());
                        JSONObject jsonObj = new JSONObject(response.body());
                        Log.e(TAG,"onResponse  177   "+jsonObj.getString("StatusCode"));
                        if(jsonObj.getString("StatusCode").equals("0")) {
                            JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeOffersDets");
                            Log.e(TAG,"onResponse  180   "+jsonObj1.getString("ResponseMessage"));
                            String strBody  = jsonObj1.getString("OffersDetails");
                            String strStart = "{"+strBody.replaceAll("\\r\\n","")+"}";
                            Log.e(TAG,"onResponse  183   "+strStart);
//                            JSONObject jObject2 = new JSONObject(strStart.replaceAll("\\\\",""));
                            JSONObject jObject3 = new JSONObject(strStart.replaceAll("\\\\",""));
                            Log.e(TAG,"onResponse  185   "+jObject3);

                            Iterator iterator = jObject3.keys();
                            while(iterator.hasNext()){
                                String ope = (String)iterator.next();
                                Log.e(TAG,"iterator   190   "+ope);

                                arrayHeader = new ArrayList<String>();

                                jObject4 = jObject3.getJSONObject(""+ope);
                                Log.e(TAG,"Oper    193   "+jObject4);

                                Iterator iterator1 = jObject4.keys();
                                while(iterator1.hasNext()){
                                    String key = (String)iterator1.next();
                                    Log.e(TAG,"iterator   201   "+key);
                                    if (!key.equals("key")){
                                        arrayHeader.add(""+key);
                                    }
                                }


                                //  Log.e(TAG,"iterator   190   "+(jObject.getJSONObject((String)iterator.next())));

                            }


                            Log.e(TAG,"jObject   213    "+arrayHeader);

                            rv_rechrgehead.setHasFixedSize(false);
                            RechargeHeaderAdapter rechargeHeaderAdapter = new RechargeHeaderAdapter(ReachargeOfferActivity.this, arrayHeader);
                            rv_rechrgehead.setLayoutManager(new LinearLayoutManager(ReachargeOfferActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rv_rechrgehead.setAdapter(rechargeHeaderAdapter);
                            rechargeHeaderAdapter.setOnItemClickListener(ReachargeOfferActivity.this);

                            if (jObject4 != null){
                                setOfferList(jObject4,arrayHeader.get(0));
                            }






                        }else {

                            //  JSONObject jsonObj1 = jsonObj.getJSONObject("RechargeOffersDets");
                            // Log.e(TAG,"onResponse  233   "+jsonObj1.getString("ResponseMessage"));
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReachargeOfferActivity.this);
                            builder.setMessage(""+jsonObj.getString("EXMessage"))
//                                builder.setMessage("No data found.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                            android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }

//                        Log.e(TAG,"onResponse  1733   ");
//                        String strStart = strBody.replaceAll("\\r\\n","");
//                        Log.e(TAG,"onResponse  17331   "+strStart);
//                        JSONObject jObject = new JSONObject(strStart.replaceAll("\\\\",""));
//                        Log.e(TAG,"onResponse  17332   ");
//                        if (jObject.getString("status_code").equals("200")){
//                            Log.e(TAG,"onResponse  1734   ");
//                            String data = jObject.getString("data").replace("rn","");
//                            Log.e(TAG,"jObject   2556    "+data);
//                            jObject1 = new JSONObject(data);
//                           // Log.e(TAG,"jObject   2557    "+jObject1.getString("ToprnUp"));
//                            JSONArray  jsonArray = jObject1.getJSONArray("TopUp");
//                            Log.e(TAG,"jObject   2558    "+jsonArray);
//                            arrayHeader = new ArrayList<String>();
//                            Iterator iterator = jObject1.keys();
//                            while(iterator.hasNext()){
//                                String key = (String)iterator.next();
//                                // JSONObject issue = jObject1.getJSONObject(key);
//                                Log.e(TAG,"jObject   2559    "+key);
//                                //  get id from  issue
////                            String _pubKey = issue.optString("id");
//                                //tabLayout.addTab(tabLayout.newTab().setText("" + key));
//                                arrayHeader.add(""+key);
//
//                            }
//                            Log.e(TAG,"jObject   2560    "+arrayHeader);
//
//                            rv_rechrgehead.setHasFixedSize(false);
//                            RechargeHeaderAdapter rechargeHeaderAdapter = new RechargeHeaderAdapter(ReachargeOfferActivity.this, arrayHeader);
//                            rv_rechrgehead.setLayoutManager(new LinearLayoutManager(ReachargeOfferActivity.this, LinearLayoutManager.HORIZONTAL, false));
//                            rv_rechrgehead.setAdapter(rechargeHeaderAdapter);
//                            rechargeHeaderAdapter.setOnItemClickListener(ReachargeOfferActivity.this);
//
//                            setOfferList(jObject1,arrayHeader.get(0));
//                        }
//                        else {
//                            Log.e(TAG,"onResponse  1735   ");
//                            Log.e(TAG,"NO DATA");
//                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReachargeOfferActivity.this);
//                            builder.setMessage("No data Found.")
////                                builder.setMessage("No data found.")
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                            finish();
//                                        }
//                                    });
//                            android.app.AlertDialog alert = builder.create();
//                            alert.show();
//                        }


                    }
                    catch (Exception e) {
                        Log.e(TAG,"JSONException  173   "+e.toString());
                        e.printStackTrace();
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReachargeOfferActivity.this);
                        builder.setMessage("No data Found.")
//                                builder.setMessage("No data found.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                        Log.e(TAG,"jObject   2551    "+e.toString());
                    }


                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();}
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOfferList(JSONObject jObject4, String header) {

        try {

            Log.e(TAG,"jsonArray   319    "+header);
            Log.e(TAG,"jsonArray   232029    "+jObject4);

//            JSONArray  jsonArray = jObject4.getJSONArray(""+header);
//            Log.e(TAG,"jsonArray   323    "+jsonArray);

//            rv_offerlist.setHasFixedSize(false);
//            OfferListAdapter offerListAdapter = new OfferListAdapter(ReachargeOfferActivity.this, jsonArray);
//            rv_offerlist.setLayoutManager(new LinearLayoutManager(ReachargeOfferActivity.this, LinearLayoutManager.VERTICAL, false));
//            rv_offerlist.setAdapter(offerListAdapter);
//            offerListAdapter.setOnItemClickListener(ReachargeOfferActivity.this);

            String jObject5 = jObject4.getString(header);

            //     JSONArray  jsonArray = jObject4.getJSONArray(""+jObject4.getJSONObject(""+header));
            Log.e(TAG,"jObject5   338    "+jObject5);
            JSONArray jsonArray = new JSONArray(jObject5);
            Log.e(TAG,"jsonArray   340    "+jsonArray);

            rv_offerlist.setHasFixedSize(false);
            OfferListAdapter offerListAdapter = new OfferListAdapter(ReachargeOfferActivity.this, jsonArray);
            rv_offerlist.setLayoutManager(new LinearLayoutManager(ReachargeOfferActivity.this, LinearLayoutManager.VERTICAL, false));
            rv_offerlist.setAdapter(offerListAdapter);
            offerListAdapter.setOnItemClickListener(ReachargeOfferActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Exception   346    "+e.toString());

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        Log.e(TAG,"onActivityResult  24   "+requestCode+"  "+resultCode+"   "+data);

        super.onActivityResult(requestCode, resultCode, data);
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
    public void onItemClick(View v, int position,String data,String mode) {
        Log.e(TAG,"position  554   "+position+"   "+data+"   "+mode);

        if (mode.equals("0")){
            setOfferList(jObject4,data);
        }
        else if (mode.equals("1")){
            Intent intent = new Intent();
            intent.putExtra("editTextValue",data );
            setResult(RESULT_OK, intent);
            finish();
        }

    }





}