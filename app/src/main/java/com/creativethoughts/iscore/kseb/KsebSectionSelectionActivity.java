package com.creativethoughts.iscore.kseb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class KsebSectionSelectionActivity extends Activity {
    private RecyclerSectionListAdapter mRecyclerSectionListAdapter;
    public String TAG = "KsebSectionSelectionActivity";
    RecyclerView recyclerSectionList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kseb_section_selection);

        recyclerSectionList = findViewById(R.id.recycler_select_kseb_section);
        recyclerSectionList.setHasFixedSize( true );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerSectionList.setLayoutManager( layoutManager );

        mRecyclerSectionListAdapter = new RecyclerSectionListAdapter(new ArrayList< >(), sectionDetails -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable( getString(R.string.kseb_section_list  ), sectionDetails );
            intent.putExtras( bundle );
            setResult( RESULT_OK, intent );
            finish();
        });
        recyclerSectionList.setAdapter(mRecyclerSectionListAdapter);

        EditText edtTxtSectionName = findViewById( R.id.edt_txt_section_name );
        edtTxtSectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence keyWord, int i, int i1, int i2) {
//                 getObservable( keyWord.toString() )
//                         .subscribeOn( Schedulers.io() )
//                         .observeOn( AndroidSchedulers.mainThread() )
//                         .subscribe(  getObserver() );
              //  String sss = edtTxtSectionName.getText().toString();
            //    Log.e(TAG,"sss   97    "+sss);
                getSection(keyWord.toString());
               // getSection(sss);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do nothing
            }
        });
    }

    private void getSection(String keyword) {
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

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);


                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("SectioName",IScoreApplication.encryptStart(keyword));

                    Log.e(TAG,"requestObject1   150   "+requestObject1);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getKSEBSectionDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e(TAG,"response   150   "+response.body());

                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("KSEBSectionDetails");
                                Log.e(TAG,"jsonObj1   1502   "+jsonObj1);
//                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                            //    JSONArray newJArray = jsonObj1.getJSONArray("OwnAccountdetailsList");
                             //   String ss = " {\"KSEBSectionList\":[{\"SectionName\":\"Atholy\",\"SectionCode\":6615}]}";
                              //  processResult(ss);
                                processResult(jsonObj1.toString());

                            }
                            else {

                                recyclerSectionList.setAdapter(null);

//                                try{
//
//                                    String EXMessage = jsonObj.getString("EXMessage");
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(KsebSectionSelectionActivity.this);
//                                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//
//                                                }
//                                            });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }catch (JSONException e){
//                                    String EXMessage = jsonObj.getString("EXMessage");
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(KsebSectionSelectionActivity.this);
//                                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }
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
            catch (Exception e) {

            }
        }
        else {


            AlertDialog.Builder builder = new AlertDialog.Builder(KsebSectionSelectionActivity.this);
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

    private Observable< String > getObservable( String keyword ){
        return Observable.fromCallable( ()-> listenText( keyword ));
    }
//    private Observer< String > getObserver(  ){
//        return  new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                //Do nothing
//            }
//
//            @Override
//            public void onNext(String result ) {
//                processResult( result );
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                //Do nothing
//            }
//
//            @Override
//            public void onComplete() {
//                //Do nothing
//            }
//        };
//    }
    private String listenText( String keyWord ){
//        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//        String BASE_URL=pref.getString("oldbaseurl", null);

//        SharedPreferences pref =getApplicationContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
//        String BASE_URL=pref.getString("baseurl", null);
//
//        String url = BASE_URL+"/KSEBSectionList?Sectionname="+ keyWord;
      //  return ConnectionUtilitySectionList.getResponse( url );

        return "";
    }
    private void processResult( String result ){
        Gson gson = new Gson();
        try{
            Log.e(TAG,"result    "+result);

                SectionSearchResult sectionSearchResult  = gson.fromJson( result, SectionSearchResult.class );
                Log.e("TAG","sectionSearchResult  1101  "+sectionSearchResult);
                Log.e("TAG","result  1102  "+result);
                recyclerSectionList.setAdapter(mRecyclerSectionListAdapter);
                if ( mRecyclerSectionListAdapter != null ){
                    mRecyclerSectionListAdapter.addSections( sectionSearchResult.getSectionDetailsList() );
                    mRecyclerSectionListAdapter.notifyDataSetChanged();
                }



        }catch ( Exception e ){
            if ( IScoreApplication.DEBUG ){
                Log.e("ksebsectionexc  1102", e.toString() );
            }
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
