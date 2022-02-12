package com.creativethoughts.iscore;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.MessageAdapter1;
import com.creativethoughts.iscore.utility.ConnectionUtil;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KsebBillStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KsebBillStatusFragment extends Fragment implements View.OnClickListener{
    String TAG = "KsebBillStatusFragment";
    private ProgressDialog progressDialog;
    private EditText editTransactionId;
    private Button btnCheckStatus;
    private TextView textViewTransactionIdErrorMsg;
    public KsebBillStatusFragment() {
        // Required empty public constructor
    }

    public static KsebBillStatusFragment newInstance() {

        return new KsebBillStatusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_kseb_bill_status, container, false);
        addView(rootView);
        return rootView;
    }
    private void addView(View rootView){
        editTransactionId = rootView.findViewById(R.id.edit_transaction_id);
        textViewTransactionIdErrorMsg = rootView.findViewById(R.id.transaction_id_error_message);
        btnCheckStatus = rootView.findViewById(R.id.check_status);
        btnCheckStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        if ( id == R.id.check_status )
            checkTransactionId();
    }
    private void checkTransactionId(){
        String transactionId = editTransactionId.getText().toString();
        //noinspection ObjectEqualsNull
        if(transactionId.equals("") ){
            setErrorHint(editTransactionId);
            return;
        }

       // new CheckStatusAsync(transactionId).execute();

        getTransactionDetails(transactionId);
    }

    private void setErrorHint(EditText editText){
        if ( getContext() == null )
            return;
        editText.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(),
                R.color.error), PorterDuff.Mode.SRC_ATOP);
        String text = "Please enter valid transaction id";
        textViewTransactionIdErrorMsg.setText( text );
    }



    private class CheckStatusAsync extends AsyncTask<String, android.R.integer, String>{
        final String id;
        ProgressDialog progressDialog;
        private CheckStatusAsync(String transId){
            id = transId;
        }
        private String downloadStatus(String id){
            String url;
            String response;
            String pin;
            SharedPreferences pinIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF36, 0);
            pin = pinIdSP.getString("pinlog","");

//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            url = BASE_URL+"/KSEBTransactionResponse?TransactioID="+id+
                    "&Pin="+pin;
            response = ConnectionUtil.getResponse(url);
            return response;

        }
        @Override
        public void onPreExecute(){
            btnCheckStatus.setEnabled(false);
            progressDialog = ProgressDialog.show(
                    getContext(), "",""
            );
        }
        @Override
        public String doInBackground(String... params){
            String result;
            result = downloadStatus(id);
            return result;
        }
        @Override
        public void onPostExecute(String result){
            btnCheckStatus.setEnabled(true);
            progressDialog.dismiss();
            processResponse(result);

        }
        private void processResponse(String response){
            try{
                int tempResponse = Integer.parseInt(response.trim());
                switch (tempResponse){
                    case 1:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment was successfull",1);
                        break;
                    case  2:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment was failed",2);
                        break;
                    case 3:
                        showAlertDialogue(getString(R.string.app_name), "Your bill payment is on pending",2);
                        break;
                    case 4:
                        showAlertDialogue(getString(R.string.app_name), "Wrong transaction Id", 2);
                        break;
                    case 5:
                        showAlertDialogue(getString(R.string.app_name), "Due to some technical issues, your transaction was reversed.\nThe amount will" +
                                " be reversed with in few hours",2);
                        break;
                    default:
                        break;
                }
            }catch (Exception e){
                if(IScoreApplication.DEBUG) Log.e("Response parse error", e.toString());
            }
        }
        private void showAlertDialogue(String title, String message, final int status){ // status for decide whether going to home fragment or not
            if ( getActivity() == null )
                return;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message).setTitle(title)
                    .setIcon(R.drawable.aappicon)
                    .setPositiveButton("Ok", (dialog, idTemp) -> {
                        if(status == 1){
                            Fragment fragment =   new KsebBillStatusFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            assert fragmentManager != null;
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, fragment);
                            fragmentTransaction.addToBackStack("Kseb");
                            fragmentTransaction.commit();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void getTransactionDetails(String transId) {

        SharedPreferences pref =getActivity().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
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


                    SharedPreferences tokenIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token = tokenIdSP.getString("Token","");
                    SharedPreferences prefpin =getActivity().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                    SharedPreferences bankkeypref =getActivity().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    String iemi =   IScoreApplication.getIEMI();
                    SharedPreferences BankVerifierSP = getActivity().getSharedPreferences(Config.SHARED_PREF32, 0);
                    String BankVerifier =BankVerifierSP.getString("BankVerifier","");

                    requestObject1.put("TransactioID",IScoreApplication.encryptStart(transId));
                    requestObject1.put("Pin",IScoreApplication.encryptStart(pin));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified",IScoreApplication.encryptStart(BankVerifier));

                    Log.e(TAG,"requestObject1   2111     "+requestObject1);
                    Log.e(TAG,"RECHARGE KSEBSTATUS   18888"
                            +"\n"+"TransactioID      "+transId
                            +"\n"+"Pin         "+pin
                            +"\n"+"imei           "+iemi
                            +"\n"+"Token           "+token
                            +"\n"+"BankKey        "+BankKey
                            +"\n"+"BankHeader           "+BankHeader
                            +"\n"+"BankVerifier     "+BankVerifier);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception  2112   "+e.toString());
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.KSEBTransactionResponse(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.e(TAG,"response 2113     "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {


                            }
                            else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                builder.setMessage(""+jsonObj.getString("EXMessage"))
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


                        }catch (JSONException e)
                        {

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setMessage(""+t.toString())
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
                });

            }
            catch (Exception e)
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage(""+e.toString())
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
        else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage("Network is currently unavailable. Please try again later.")
//                                builder.setMessage("No data found.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
//            tv_nodata.setText("Network is currently unavailable. Please try again later.");
        }
    }

    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref =getActivity().getSharedPreferences(Config.SHARED_PREF24, 0);
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
