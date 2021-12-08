package com.creativethoughts.iscore.money_transfer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.adapters.SourceAccListAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
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


public class OwnBankFundTransferServiceChooserFragment extends Fragment implements View.OnClickListener {

    RelativeLayout rltv_ownaccount, rltv_otheraccount;
    RecyclerView rv_source_acc_list_own,rv_source_acc_list_other;
    LinearLayout ll_source_acc_list_own,ll_source_acc_list_other;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_own_bank_fund_transfer_service_chooser, container, false);

        rltv_ownaccount    = view.findViewById( R.id.rltv_ownaccount );
        rltv_otheraccount    = view.findViewById( R.id.rltv_otheraccount );
        rv_source_acc_list_own    = view.findViewById( R.id.rv_source_acc_list_own );
        rv_source_acc_list_other    = view.findViewById( R.id.rv_source_acc_list_other );
        ll_source_acc_list_other    = view.findViewById( R.id.ll_source_acc_list_other );
        ll_source_acc_list_own    = view.findViewById( R.id.ll_source_acc_list_own );


        rltv_ownaccount.setOnClickListener(this);
        rltv_otheraccount.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //  Fragment fragment = null;

        switch ( id ){
            case R.id.rltv_ownaccount:
                if (ll_source_acc_list_own.getVisibility() == View.VISIBLE) {
                    ll_source_acc_list_other.setVisibility(View.GONE);
                    ll_source_acc_list_own.setVisibility(View.GONE);
                }
                else{
                    showOwnAccFromList("1");
                    ll_source_acc_list_other.setVisibility(View.GONE);
                }

//                Intent i_own=new Intent(getContext(), OwnAccountFundTransferActivity.class);
//                startActivity(i_own);
                break;
            case R.id.rltv_otheraccount:

                if (ll_source_acc_list_other.getVisibility() == View.VISIBLE) {
                    ll_source_acc_list_other.setVisibility(View.GONE);
                    ll_source_acc_list_own.setVisibility(View.GONE);
                }
                else{
                    showOtherAccFromList("1");
                    ll_source_acc_list_own.setVisibility(View.GONE);
                }

//                Intent i_other=new Intent(getContext(), OtherAccountFundTransferActivity.class);
//                startActivity(i_other);
                break;



            default:break;
        }

    }

    private void showOwnAccFromList(String loantype) {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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
                String reqmode = IScoreApplication.encryptStart("14");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    String types = loantype;

                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
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
                                    ll_source_acc_list_own.setVisibility(View.VISIBLE);
                                    GridLayoutManager Layout = new GridLayoutManager(getContext(), 1);
                                    SourceAccListAdapter adapterown = new SourceAccListAdapter(getContext(), Jarray,0);
                                    rv_source_acc_list_own.setLayoutManager(Layout);
                                    rv_source_acc_list_own.setHasFixedSize(true);
                                    rv_source_acc_list_own.setAdapter(adapterown);
                                }
                                else {
                                    ll_source_acc_list_other.setVisibility(View.GONE);
                                    ll_source_acc_list_own.setVisibility(View.GONE);
                                }
                            }
                            else {
                                ll_source_acc_list_other.setVisibility(View.GONE);
                                ll_source_acc_list_own.setVisibility(View.GONE);
                            }
                        }
                        catch (JSONException e) {
                            ll_source_acc_list_other.setVisibility(View.GONE);
                            ll_source_acc_list_own.setVisibility(View.GONE);
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
            DialogUtil.showAlert(getActivity(),
                    "Network is currently unavailable. Please try again later.");
        }

    }

    private void showOtherAccFromList(String loantype) {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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
                String reqmode = IScoreApplication.encryptStart("14");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    String types = loantype;


                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
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

                                    ll_source_acc_list_other.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
                                    SourceAccListAdapter adapter = new SourceAccListAdapter(getContext(), Jarray,1);

                                    rv_source_acc_list_other.setLayoutManager(lLayout);
                                    rv_source_acc_list_other.setHasFixedSize(true);
                                    rv_source_acc_list_other.setAdapter(adapter);
                                }
                                else {
                                    ll_source_acc_list_other.setVisibility(View.GONE);
                                    ll_source_acc_list_own.setVisibility(View.GONE);
                                }
                            }
                            else {
                                ll_source_acc_list_other.setVisibility(View.GONE);
                                ll_source_acc_list_own.setVisibility(View.GONE);
                            }
                        }
                        catch (JSONException e) {
                            ll_source_acc_list_other.setVisibility(View.GONE);
                            ll_source_acc_list_own.setVisibility(View.GONE);
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
            DialogUtil.showAlert(getActivity(),
                    "Network is currently unavailable. Please try again later.");
        }

    }

    private HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> true;
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
        SharedPreferences sslnamepref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
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
}









//package com.creativethoughts.iscore.money_transfer;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import com.creativethoughts.iscore.Retrofit.APIInterface;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.creativethoughts.iscore.Helper.Common;
//import com.creativethoughts.iscore.IScoreApplication;
//import com.creativethoughts.iscore.R;
//import com.creativethoughts.iscore.adapters.SourceAccListAdapter;
//import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
//import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
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
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
//import javax.net.ssl.X509TrustManager;
//
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;
//
//
//public class OwnBankFundTransferServiceChooserFragment extends Fragment implements View.OnClickListener {
//
//    RelativeLayout rltv_ownaccount, rltv_otheraccount;
//    RecyclerView rv_source_acc_list_own,rv_source_acc_list_other;
//    LinearLayout ll_source_acc_list_own,ll_source_acc_list_other;
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_own_bank_fund_transfer_service_chooser, container, false);
//
//        rltv_ownaccount    = view.findViewById( R.id.rltv_ownaccount );
//        rltv_otheraccount    = view.findViewById( R.id.rltv_otheraccount );
////        rv_source_acc_list_own    = view.findViewById( R.id.rv_source_acc_list_own );
////        rv_source_acc_list_other    = view.findViewById( R.id.rv_source_acc_list_other );
////        ll_source_acc_list_other    = view.findViewById( R.id.ll_source_acc_list_other );
////        ll_source_acc_list_own    = view.findViewById( R.id.ll_source_acc_list_own );
//
//
//        rltv_ownaccount.setOnClickListener(this);
//        rltv_otheraccount.setOnClickListener(this);
//
//
//        return view;
//    }
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//      //  Fragment fragment = null;
//
//        switch ( id ){
//            case R.id.rltv_ownaccount:
//                if (ll_source_acc_list_own.getVisibility() == View.VISIBLE) {
//                    ll_source_acc_list_other.setVisibility(View.GONE);
//                    ll_source_acc_list_own.setVisibility(View.GONE);
//                }
//                else{
//                    showOwnAccFromList("1");
//                    ll_source_acc_list_other.setVisibility(View.GONE);
//                }
//
////                Intent i_own=new Intent(getContext(), OwnAccountFundTransferActivity.class);
////                startActivity(i_own);
//                break;
//            case R.id.rltv_otheraccount:
//
//                if (ll_source_acc_list_other.getVisibility() == View.VISIBLE) {
//                    ll_source_acc_list_other.setVisibility(View.GONE);
//                    ll_source_acc_list_own.setVisibility(View.GONE);
//                }
//                else{
//                    showOtherAccFromList("1");
//                    ll_source_acc_list_own.setVisibility(View.GONE);
//                }
//
////                Intent i_other=new Intent(getContext(), OtherAccountFundTransferActivity.class);
////                startActivity(i_other);
//                break;
//
//
//
//            default:break;
//        }
//
//    }
//
//    private void showOwnAccFromList(String loantype) {
//        if (NetworkUtil.isOnline()) {
//            try {
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
//                    String token = loginCredential.token;
//                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//                    String cusid = userDetails.customerId;
//                    String types = loantype;
//
//                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
//                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
//                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
//                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
//                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
//                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
//                Call<String> call = apiService.getOwnAccounDetails(body);
//                call.enqueue(new Callback<String>() {
//
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        try{
//                            JSONObject jsonObj = new JSONObject(response.body());
//                            if(jsonObj.getString("StatusCode").equals("0")) {
//                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
//                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
//                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
//                                if(Jarray.length()!=0) {
//                                    ll_source_acc_list_own.setVisibility(View.VISIBLE);
//                                    GridLayoutManager Layout = new GridLayoutManager(getContext(), 1);
//                                    SourceAccListAdapter adapterown = new SourceAccListAdapter(getContext(), Jarray,0);
//                                    rv_source_acc_list_own.setLayoutManager(Layout);
//                                    rv_source_acc_list_own.setHasFixedSize(true);
//                                    rv_source_acc_list_own.setAdapter(adapterown);
//                                }
//                                else {
//                                    ll_source_acc_list_other.setVisibility(View.GONE);
//                                    ll_source_acc_list_own.setVisibility(View.GONE);
//                                }
//                            }
//                            else {
//                                ll_source_acc_list_other.setVisibility(View.GONE);
//                                ll_source_acc_list_own.setVisibility(View.GONE);
//                            }
//                        }
//                        catch (JSONException e) {
//                            ll_source_acc_list_other.setVisibility(View.GONE);
//                            ll_source_acc_list_own.setVisibility(View.GONE);
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
//            DialogUtil.showAlert(getActivity(),
//                    "Network is currently unavailable. Please try again later.");
//        }
//
//    }
//
//    private void showOtherAccFromList(String loantype) {
//        if (NetworkUtil.isOnline()) {
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
//                    String token = loginCredential.token;
//                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//                    String cusid = userDetails.customerId;
//                    String types = loantype;
//
//
//                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
//                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
//                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
//                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
//                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
//                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
//                Call<String> call = apiService.getOwnAccounDetails(body);
//                call.enqueue(new Callback<String>() {
//
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        try{
//                            JSONObject jsonObj = new JSONObject(response.body());
//                            if(jsonObj.getString("StatusCode").equals("0")) {
//                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
//                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
//                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
//                                if(Jarray.length()!=0) {
//
//                                    ll_source_acc_list_other.setVisibility(View.VISIBLE);
//                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
//                                    SourceAccListAdapter adapter = new SourceAccListAdapter(getContext(), Jarray,1);
//
//                                    rv_source_acc_list_other.setLayoutManager(lLayout);
//                                    rv_source_acc_list_other.setHasFixedSize(true);
//                                    rv_source_acc_list_other.setAdapter(adapter);
//                                }
//                                else {
//                                    ll_source_acc_list_other.setVisibility(View.GONE);
//                                    ll_source_acc_list_own.setVisibility(View.GONE);
//                                }
//                            }
//                            else {
//                                ll_source_acc_list_other.setVisibility(View.GONE);
//                                ll_source_acc_list_own.setVisibility(View.GONE);
//                            }
//                        }
//                        catch (JSONException e) {
//                            ll_source_acc_list_other.setVisibility(View.GONE);
//                            ll_source_acc_list_own.setVisibility(View.GONE);
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
//            DialogUtil.showAlert(getActivity(),
//                    "Network is currently unavailable. Please try again later.");
//        }
//
//    }
//
//    private HostnameVerifier getHostnameVerifier() {
//        return (hostname, session) -> true;
//    }
//
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
//}
