package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AccountSummaryListAdapter;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.SyncAll;
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
import java.text.DecimalFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

public class LoanAccountSummaryDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public  String TAG = "LoanAccountSummaryDetailsActivity";
    private ProgressDialog progressDialog;
    RecyclerView rv_accountSummary;
    String cusid, subModule, token,account,accno,amount,status,type,type1,fund,ifsc, loantypemode,IsShareAc, EnableDownloadStatement;
    TextView tv_loanslab,tv_share,tv_bal,tv_accno,acc_type,tv_ifsc,tv_fund,tv_ministatmnt;
    JSONArray jsonArrayKey;
    String shareData = "";
    CardView cv_accSummaryLone;
    LinearLayout ll_mini_slab,ll_fund_ifsc,ll_share;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accsummarydetails1);

        Log.e(TAG,"START  84  ");
        subModule = getIntent().getStringExtra("Submodule");
        account = getIntent().getStringExtra("FK_Account");
        accno = getIntent().getStringExtra("Accno");
        amount = getIntent().getStringExtra("Amt");
        status = getIntent().getStringExtra("Status");
        type1 = getIntent().getStringExtra("type");

        type = getIntent().getStringExtra("Type");
        fund = getIntent().getStringExtra("Fund");
        ifsc = getIntent().getStringExtra("Ifsc");
        loantypemode = getIntent().getStringExtra("loantypemode");
        IsShareAc = getIntent().getStringExtra("IsShareAc");
        EnableDownloadStatement = getIntent().getStringExtra("EnableDownloadStatement");

        setRegViews();

        if (IsShareAc.equals("1")){
            tv_fund.setText(fund);
            tv_ifsc.setText(ifsc);
            ll_fund_ifsc.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.VISIBLE);
        }
        else{

            ll_fund_ifsc.setVisibility(View.GONE);
            ll_share.setVisibility(View.INVISIBLE);
        }



        getAccountSummaryDetails();

        if (status.equals("Closed")){
            ll_mini_slab.setVisibility(View.GONE);
        }
        else{
            ll_mini_slab.setVisibility(View.VISIBLE);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setRegViews() {

        rv_accountSummary = (RecyclerView)findViewById(R.id.rv_accountSummary);
        cv_accSummaryLone = findViewById(R.id.cv_accSummaryLone);
        ll_mini_slab = findViewById(R.id.ll_mini_slab);

        tv_ministatmnt =  findViewById(R.id.tv_ministatmnt);
        ll_fund_ifsc =  findViewById(R.id.ll_fund_ifsc);
        ll_share =  findViewById(R.id.ll_share);
        tv_share =  findViewById(R.id.tv_share);
        tv_loanslab = (TextView) findViewById(R.id.tv_loanslab);
        acc_type = (TextView) findViewById(R.id.tv_acc_type);
        tv_bal = (TextView)findViewById(R.id.tv_bal);
        tv_accno = (TextView)findViewById(R.id.tv_accno);
        tv_fund = (TextView)findViewById(R.id.tv_fund);
        tv_ifsc = (TextView)findViewById(R.id.tv_ifsc);
        tv_ministatmnt.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_loanslab.setOnClickListener(this);

        if(accno!=null)
        {
            tv_accno.setText(accno);
        }
        if(amount!=null)
        {

            double num =Double.parseDouble(amount);
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");
            tv_bal.setText("\u20B9 "+ fmt.format(num));
        }
        if(type1!=null)
        {
            acc_type.setText(type1);
        }
        else
        {
            tv_accno.setText("");
            tv_bal.setText("");
            acc_type.setText("");
        }


    }

    private void getAccountSummaryDetails() {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(LoanAccountSummaryDetailsActivity.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("3");

                SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                cusid = customerIdSP.getString("customerId","");
                SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                token = tokenIdSP.getString("Token","");


                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("SubModule", IScoreApplication.encryptStart(subModule) );
                    requestObject1.put("FK_Account", IScoreApplication.encryptStart(account) );
                    requestObject1.put("Token", IScoreApplication.encryptStart(token) );
                    requestObject1.put("LoanType", IScoreApplication.encryptStart(loantypemode) );

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
                Call<String> call = apiService.getAccountSummary(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.i("Accountsummary Details",response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("AccountModuleDetailsListInfo");
                                JSONArray jarray = jobj.getJSONArray("Data");
                                jsonArrayKey= jarray.getJSONObject(0).getJSONArray("Details");
                                if(jarray.length()!=0) {
                                    cv_accSummaryLone.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(LoanAccountSummaryDetailsActivity.this, 1);
                                    rv_accountSummary.setLayoutManager(lLayout);
                                    rv_accountSummary.setHasFixedSize(true);
                                    AccountSummaryListAdapter adapter = new AccountSummaryListAdapter(LoanAccountSummaryDetailsActivity.this, jsonArrayKey);
                                    rv_accountSummary.setAdapter(adapter);
                                }
                                else{
                                    cv_accSummaryLone.setVisibility(View.GONE);
                                    try{
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoanAccountSummaryDetailsActivity.this);
                                        builder.setMessage(ResponseMessage)
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
                                        String EXMessage = jObject.getString("EXMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoanAccountSummaryDetailsActivity.this);
                                        builder.setMessage(EXMessage)
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
                            else{
                                cv_accSummaryLone.setVisibility(View.GONE);

                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountSummaryDetailsListInfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanAccountSummaryDetailsActivity.this);
                                    builder.setMessage(ResponseMessage)
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
                                    String EXMessage = jObject.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanAccountSummaryDetailsActivity.this);
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


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { progressDialog.dismiss();}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(LoanAccountSummaryDetailsActivity.this,
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

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.tv_ministatmnt:
                if (NetworkUtil.isOnline()) {

//                    final ProgressDialog pDialog = ProgressDialog.show(LoanAccountSummaryDetailsActivity.this, "", "Loading Details...");
//                    SyncAll.syncAllAccounts(new SyncAll.OnSyncStateListener() {
//                        @Override
//                        public void onCompleted() {
//
//                            Intent i = new Intent(LoanAccountSummaryDetailsActivity.this, LoanMinistatement.class);
//                            i.putExtra("accno", tv_accno.getText().toString());
//                            i.putExtra("amt", tv_bal.getText().toString());
//                            i.putExtra("submodule", subModule);
//                            i.putExtra("account", account);
//                            i.putExtra("EnableDownloadStatement", EnableDownloadStatement);
//                            startActivity(i);
//                            pDialog.dismiss();
//                            return;
//                        }
//                        @Override
//                        public void onFailed() {
//                            //                        updateTopView();
//                            pDialog.dismiss();
//                        }
//                    }, true,LoanAccountSummaryDetailsActivity.this);

                    Intent i = new Intent(LoanAccountSummaryDetailsActivity.this, LoanMinistatement.class);
                    i.putExtra("accno", tv_accno.getText().toString());
                    i.putExtra("amt", tv_bal.getText().toString());
                    i.putExtra("submodule", subModule);
                    i.putExtra("account", account);
                    i.putExtra("EnableDownloadStatement", EnableDownloadStatement);
                    startActivity(i);


                }
                else {
                    Intent i = new Intent(LoanAccountSummaryDetailsActivity.this, LoanMinistatement.class);
                    i.putExtra("accno", tv_accno.getText().toString());
                    i.putExtra("amt", tv_bal.getText().toString());
                    i.putExtra("submodule", subModule);
                    i.putExtra("account", account);
                    i.putExtra("EnableDownloadStatement", EnableDownloadStatement);
                    startActivity(i);
                }
                break;

//            case R.id.tv_loanslab:
//                Intent i3 = new Intent(LoanAccountSummaryDetailsActivity.this,LoanSlabDetails.class);
//                startActivity(i3);
//                break;

            case R.id.tv_loanslab:
                Intent i3 = new Intent(LoanAccountSummaryDetailsActivity.this,LoanSlabDetails.class);
                i3.putExtra("submodule", subModule);
                i3.putExtra("accno", accno);
                startActivity(i3);
                break;

            case R.id.tv_share:
                shareData= "";
                share();
                break;
        }
    }

    private void share(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.share_popup, null);
            TextView tv_share =  layout.findViewById(R.id.tv_share);
            TextView tv_cancel =  layout.findViewById(R.id.tv_cancel);
            TextView tvp_name =  layout.findViewById(R.id.tvp_name);
            TextView tvp_accNum =  layout.findViewById(R.id.tvp_accNum);
            TextView tvp_ifsc =  layout.findViewById(R.id.tvp_ifsc);
            TextView tvp_accType =  layout.findViewById(R.id.tvp_accType);


            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();



            SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
            tvp_name.setText("Beneficiary Name : "+customerNameSP.getString("customerName","")+"");

            tvp_accNum.setText("Account Type : "+type1 +"\nBeneficiary Account : "+fund +"\nIFSC Code : "+ifsc);



            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareData = ""+tvp_name.getText()+"\nAccount Type : "+type1+"\n"+"Beneficiary Account : "+fund+"\n"+"IFSC Code : "+ifsc;


                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,shareData);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);


                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}