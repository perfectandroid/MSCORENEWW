package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.adapters.DepositListInfoAdapter;
import com.creativethoughts.iscore.Retrofit.APIInterface;
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
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

public class DepositFragment extends Fragment implements View.OnClickListener{

    String TAG = "DepositFragment";
    private ProgressDialog progressDialog;
    RecyclerView rv_banklist;
    RadioButton r1,r2;
    String token,cusid,type,formattedDate;
    RadioGroup radio ;
    String loantype = "1" ;
    TextView tvActive,tvClosed,tv_as_on_date;
    TextView tv_nodata;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_deposit, null, false);
        rv_banklist = view.findViewById(R.id.rv_accountSummaryDetails);


        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(date);
        tv_as_on_date = view.findViewById(R.id.tv_as_on_date);
        tv_as_on_date.setText("**List As On "+formattedDate+".");

        tvActive = (TextView) view.findViewById(R.id.tvActive);
        tvClosed = (TextView) view.findViewById(R.id.tvClosed);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        tvActive.setOnClickListener(this);
        tvClosed.setOnClickListener(this);
        radio = (RadioGroup) view.findViewById(R.id.rgp1);
        r1 = view.findViewById(R.id.rb_active);
        r2 = view.findViewById(R.id.rb_closed);
        showDepositList(loantype);


        radio = (RadioGroup) view.findViewById(R.id.rgp1);

        r1 = view.findViewById(R.id.rb_active);
        r2 = view.findViewById(R.id.rb_closed);
        if(r1.isChecked())
        {
            type =r1.getText().toString();
        }
        else if(r2.isChecked())
        {
            type =r2.getText().toString();
        }

        if (type.equals("Active"))
        {
            loantype ="1";
        }
        else if (type.equals("Closed"))
        {
            loantype ="2";
        }

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button
                        if(index==0)
                        {
                            loantype ="1";
                        }

                        break;
                    case 1: // secondbutton

                        if(index==1)
                        {
                            loantype ="2";
                        }
                        break;
                }
                showDepositList(loantype);

            }
        });



        return view;
    }


    private void showDepositList(String loantype) {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("14");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF35, 0);
                    token = tokenIdSP.getString("Token","");
                    SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid = customerIdSP.getString("customerId","");

                    String types = loantype;




                  /*  if (type.equals("Active"))
                    {
                         loantype ="1";
                    }
                    else if (type.equals("Closed"))
                    {
                        loantype ="2";
                    }*/

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    requestObject1.put("LoanType",IScoreApplication.encryptStart(types));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));


                    Log.e(TAG,"requestObject1   2221     "+requestObject1);
                    Log.e(TAG,"token  2222   "+token+"   "+cusid);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception  2223   "+e.toString());
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCustomerLoanandDeposit(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.e(TAG,"DepositDetails 2222     "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                tv_nodata.setVisibility(View.GONE);
                                rv_banklist.setVisibility(View.VISIBLE);
                                JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
                                Log.i("First ",String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ",String.valueOf(object));
                                JSONArray Jarray  = object.getJSONArray("CustomerLoanAndDepositDetailsList");
                                int length = object.length()+1;
                                if(Jarray.length()!=0) {
                                    GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 1);
                                    rv_banklist.setLayoutManager(lLayout);
                                    rv_banklist.setHasFixedSize(true);
                                    DepositListInfoAdapter adapter = new DepositListInfoAdapter(getActivity(), Jarray, loantype);
                                    rv_banklist.setAdapter(adapter);
                                }
                               /* else {
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }*/
                            }
                            else {
                                rv_banklist.setVisibility(View.GONE);
                                tv_nodata.setVisibility(View.VISIBLE);
                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
                                    tv_nodata.setText(""+jobj.getString("ResponseMessage"));

                                }catch (JSONException e){
                                    tv_nodata.setVisibility(View.VISIBLE);
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    tv_nodata.setText(""+EXMessage);

                                }
                            }


                        }catch (JSONException e)
                        {

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
//            DialogUtil.showAlert(getActivity(),
//                    "Network is currently unavailable. Please try again later.");
            rv_banklist.setVisibility(View.GONE);
            tv_nodata.setVisibility(View.VISIBLE);
            tv_nodata.setText("Network is currently unavailable. Please try again later.");
        }

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
    public void onStart() {
        super.onStart();
        //showDepositList();
    }


    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tvActive:
                tvActive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle1));
                tvClosed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle3));
                tvActive.setTextColor(Color.parseColor("#ffffff"));
                tvClosed.setTextColor(Color.parseColor("#000000"));
                loantype ="1";

                tv_as_on_date.setText("**List As On "+formattedDate+".");
                showDepositList(loantype);
                break;
            case  R.id.tvClosed:

                tvActive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle4));
                tvClosed.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle));
                tvActive.setTextColor(Color.parseColor("#000000"));
                tvClosed.setTextColor(Color.parseColor("#ffffff"));
                loantype ="2";

                tv_as_on_date.setText("**List Of Last Three Months.");
                showDepositList(loantype);
                break;
        }
    }
}

