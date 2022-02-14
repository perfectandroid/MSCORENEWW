package com.creativethoughts.iscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.BeneficiaryListAdapter;
import com.creativethoughts.iscore.neftrtgs.BeneficiaryDetailsModel;
import com.creativethoughts.iscore.neftrtgs.BeneficiaryRecyclerAdapter;
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

public class ListSavedBeneficiaryActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mRecyclerBeneficiary;
    private BeneficiaryRecyclerAdapter mBeneficiaryRecyclerAdapter;
    private ProgressBar mProgressBarLoading;
    private LinearLayout mLnrAnimationContainer;
    private LinearLayout llyt_adbnfcry;
    private LinearLayout mLnrOops;
    private TextView mTxtOops;
    private ImageView img_refrsh;
    private static final String MODE = "MODE";
    private String mMode;
    ArrayList<BeneficiaryDetailsModel> beneficiaryDetailsModels = new ArrayList<>();
    Bundle extras;
    String statusmsg,exmsg;
    String cusid;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_saved_beneficiary);

         mMode = getIntent().getStringExtra("mode");
         Log.i("Mode",mMode);
        setRegViews();

    }

    private void setRegViews() {

        mRecyclerBeneficiary = findViewById( R.id.recycler_view_beneficiary );
        img_refrsh=findViewById( R.id.img_refresh );
        mProgressBarLoading = findViewById( R.id.progressBar );
        mLnrAnimationContainer = findViewById( R.id.lnr_anim_container );
        mLnrOops    = findViewById( R.id.lnr_oops );
        mTxtOops    = findViewById( R.id.txt_oops_message );
        llyt_adbnfcry=findViewById( R.id.lnr_add_new_beneficiary);

        img_refrsh.setOnClickListener(this);
        llyt_adbnfcry.setOnClickListener(this);
        hideAnim( true );
        fetchBeneficiary();
    }

    private void fetchBeneficiary() {


        SharedPreferences pref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        SharedPreferences cusidpref = ListSavedBeneficiaryActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
        cusid=cusidpref.getString("customerId", null);

        SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
        String tokn =preftoken.getString("Token", "");

        if (NetworkUtil.isOnline()) {
            try{
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
                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));
             //       requestObject1.put("token", IScoreApplication.encryptStart(tokn));
                    requestObject1.put("imei", IScoreApplication.encryptStart(""));


                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject benf ",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getNeftReeceiverList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response benefcry   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject j1 = jObject.getJSONObject("NEFTRTGSGetReceiverList");
                            statusmsg = jObject.getString("EXMessage");
                           // exmsg = j1.getString("EXMessage");
                            JSONObject object = new JSONObject(String.valueOf(j1));
                            Log.i("First1 ", String.valueOf(object));
                            exmsg = object.getString("ResponseMessage");
                            JSONArray Jarray = object.getJSONArray("NEFTRTGSGetReceiverListDetails");


                          //  String responsemsg = j2.getString("ResponseMessage");

                            int statusCode=jObject.getInt("StatusCode");
                            if(statusCode==0){

                                hideAnim( true );
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject jObj = Jarray.getJSONObject(i);
                                    String Benefname =jObj.getString("BeneName");
                                    String BeneIFSC =jObj.getString("BeneIFSC");
                                    String BeneAccNo =jObj.getString("BeneAccNo");

                                    beneficiaryDetailsModels = new ArrayList<>();
                                    beneficiaryDetailsModels.add(new BeneficiaryDetailsModel(Benefname,BeneIFSC,BeneAccNo));

                                }
                                String noBen = "No beneficiary found in your account";

                                for (int i = 0; i < beneficiaryDetailsModels.size(); i++) {
                                    Log.e("Name", beneficiaryDetailsModels.get(i).getBeneficiaryName());
                                    Log.e("Ifsc", beneficiaryDetailsModels.get(i).getBeneficiaryIfsc());
                                    Log.e("Accno", beneficiaryDetailsModels.get(i).getBeneficiaryAccNo());
                                }
                                if(beneficiaryDetailsModels.isEmpty())
                                {
                                    mRecyclerBeneficiary.setVisibility( View.GONE );
                                    mLnrOops.setVisibility( View.VISIBLE );
                                    mTxtOops.setText( noBen );
                                }
                                else
                                {
                                    mRecyclerBeneficiary.setVisibility( View.VISIBLE );
                                    mLnrOops.setVisibility( View.GONE );

                                    if(Jarray.length()!=0) {
                                        GridLayoutManager lLayout = new GridLayoutManager(ListSavedBeneficiaryActivity.this, 1);
                                        mRecyclerBeneficiary.setLayoutManager(lLayout);
                                        mRecyclerBeneficiary.setHasFixedSize(true);
                                        BeneficiaryListAdapter adapter = new BeneficiaryListAdapter(ListSavedBeneficiaryActivity.this, Jarray,mMode);
                                        adapter.notifyDataSetChanged();
                                        mRecyclerBeneficiary.setAdapter(adapter);
                                        //fetchBeneficiary();
                                    }
                                }

                               /* for(int n = 0; n < j2.length(); n++)
                                {

                                    JSONObject obj = j2.getJSONObject(String.valueOf(n));
                                    obj = j2.getJSONObject(String.valueOf(n));


                                }*/
                              //  String refid;
                               // JSONArray jArray3 = j1.getJSONArray("FundTransferIntraBankList");
                              /*  for(int i = 0; i < jArray3 .length(); i++) {
                                    JSONObject object3 = jArray3.getJSONObject(i);

                                    FundTransferResult1 fundTransferResult1 = new FundTransferResult1();


                                    fundTransferResult1.refId =object3.getString("RefID");
                                    fundTransferResult1.mobileNumber = object3.getString("MobileNumber");
                                    fundTransferResult1.amount=object3.getString("Amount");
                                    fundTransferResult1.accNo=object3.getString("AccNumber");
                                    fundtransfrlist.add(fundTransferResult1);

                                }
*/

                                //  JSONArray jarray = jobj.getJSONArray( "Data");

                            }
                          /*  else if ( statusCode == 2 ){
                                alertMessage1("" ,statusmsg );
                            }
                            else if ( statusCode == 3 ){
                                alertMessage1("", statusmsg);
                            }
                            else if ( statusCode == 4 ){
                                alertMessage1("", statusmsg);
                            }
                            else  if ( statusCode == 5 ){
                                alertMessage1("", statusmsg);
                            }*/

                        /*    else{


                                try{
                                  *//*  JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");*//*
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
                                    builder.setMessage(responsemsg)
//                                builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }catch (Exception e){
                                    String EXMessage = j1.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
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
                            }*/


                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
         //   alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void hideAnim(boolean hide) {
        if ( Build.VERSION.SDK_INT > 18 ){
            TransitionManager.beginDelayedTransition( mLnrAnimationContainer );
        }
        if ( hide )
            mProgressBarLoading.setVisibility( View.GONE );
        else mProgressBarLoading.setVisibility( View.VISIBLE );
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

        switch(v.getId())
        {
            case R.id.img_refresh:
                fetchBeneficiary();
                break;
            case R.id.lnr_add_new_beneficiary:
                Intent i = new Intent(ListSavedBeneficiaryActivity.this, NeftRtgsActivity.class);
                i.putExtra("mode",mMode);
                i.putExtra("name","");
                i.putExtra("accno","");
                i.putExtra("ifsc","");
                startActivity(i);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(ListSavedBeneficiaryActivity.this,HomeActivity.class);
        startActivity(i);
    }
}
