package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.Otherfundhistoryadapter;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


public class OtherBankFundTransferPreviousHistoryFragmentNEFT extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private int mYear, mMonth, mDay;
    private ProgressDialog progressDialog;
    RecyclerView rv_otherfund;
    TextView tv_status;
    String token,cusid,loantype;
    Spinner status_spinner,week_spinner;
    String[] status,weeks;
    String reqSubMode="0",SelectedWeek= "2";
    EditText etFromdate;
    int check = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_otherfund_previous, container, false);


        rv_otherfund = view.findViewById(R.id.rv_otherfund);
        status_spinner = view.findViewById(R.id.status_spinner);
        tv_status = view.findViewById(R.id.tv_status);

        etFromdate=view.findViewById(R.id.etFromdate);
        etFromdate.setKeyListener(null);

        etFromdate.setOnClickListener(this);

        status_spinner.setOnItemSelectedListener(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        etFromdate.setText(currentDateandTime);
        status = new String[]{"All", "SUCCESS","WAITING", "RETURNED", "FAILED"};


        week_spinner = view.findViewById(R.id.week_spinner);
        week_spinner.setOnItemSelectedListener(this);
        weeks = new String[]{"2", "4","6", "8", "10"};
        getWeek();
        getStatus();
        // showOtherfundhistory(reqSubMode);


        return view;
    }


    private void getWeek() {
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,weeks);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        week_spinner.setAdapter(aa);
        week_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if (check > 0){
                    Object item = parent.getItemAtPosition(pos);
                    String item_position = String.valueOf(pos);
                    int positonInt = Integer.valueOf(item_position);
                    if(positonInt==0) {
                        SelectedWeek = "2";
                    }
                    else if(positonInt==1) {
                        SelectedWeek = "4";
                    }
                    else if(positonInt==2) {
                        SelectedWeek = "6";
                    }
                    else if(positonInt==3) {
                        SelectedWeek = "8";
                    }
                    else if(positonInt==4) {
                        SelectedWeek = "10";
                    }
                    showOtherfundhistory(reqSubMode);
                }
                check = check + 1;

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void getStatus() {
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,status);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(aa);

    }

    private void showOtherfundhistory(String reqSubMode) {

        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(getContext(), R.style.Progress);
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
                        .baseUrl(Common.getBaseUrl())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
                String reqmode = IScoreApplication.encryptStart("22");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    cusid = userDetails.customerId;
                    String types = loantype;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());


                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("BranchCode",IScoreApplication.encryptStart("1"));
                    requestObject1.put("TransType",IScoreApplication.encryptStart("2"));
                    requestObject1.put("TrnsDate",IScoreApplication.encryptStart(SelectedWeek));
//                    requestObject1.put("TrnsDate",IScoreApplication.encryptStart(etFromdate.getText().toString()));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("2"));
                    requestObject1.put("Status",IScoreApplication.encryptStart(reqSubMode));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getFundTransferHistory(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.i("Otherfund",response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("OtherFundTransferHistory");
                                //Log.i("First ",String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ",String.valueOf(object));
                                JSONArray Jarray  = object.getJSONArray("OtherFundTransferHistoryList");
                                int length = object.length()+1;
                                if(Jarray.length()!=0) {
                                    rv_otherfund.setVisibility(View.VISIBLE);
                                    tv_status.setVisibility(View.GONE);
                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
                                    rv_otherfund.setLayoutManager(lLayout);
                                    rv_otherfund.setHasFixedSize(true);
                                    Otherfundhistoryadapter adapter = new Otherfundhistoryadapter(getContext(), Jarray);
                                    rv_otherfund.setAdapter(adapter);
                                }
                                else {
                                    rv_otherfund.setVisibility(View.GONE);
                                    tv_status.setVisibility(View.VISIBLE);
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    tv_status.setText(""+ResponseMessage);
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                    builder.setMessage(ResponseMessage)
////                                builder.setMessage("No data found.")
//                                            .setCancelable(false)
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int id) {
//
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();
                                }
                            }
                            else {
                                rv_otherfund.setVisibility(View.GONE);
                                tv_status.setVisibility(View.VISIBLE);

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");

                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    tv_status.setText(""+ResponseMessage);
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                    builder.setMessage(ResponseMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();

                                }catch (JSONException e){
                                    rv_otherfund.setVisibility(View.GONE);
                                    tv_status.setVisibility(View.VISIBLE);
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    tv_status.setText(""+EXMessage);
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput =  IScoreApplication.getAppContext().
                getAssets().open(Common.getCertificateAssetName());
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
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Toast.makeText(getApplicationContext(),status[position] , Toast.LENGTH_LONG).show();
        //  String mySpinner = (String) parent.getItemAtPosition(position);
        //  Toast.makeText(getApplicationContext(),mySpinner , Toast.LENGTH_LONG).show();
        String item_position = String.valueOf(position);
        int positonInt = Integer.valueOf(item_position);
        //  Toast.makeText(OrdersActivity.this, "value is "+ positonInt, Toast.LENGTH_SHORT).show();
        if(positonInt==0)
        {
            reqSubMode = "0";
        }
        else if(positonInt==1)
        {
            reqSubMode = "1";
        }
        else if(positonInt==2)
        {
            reqSubMode = "2";
        }
        else if(positonInt==3)
        {
            reqSubMode = "3";
        }
        else if(positonInt==4)
        {
            reqSubMode = "4";
        }
        showOtherfundhistory(reqSubMode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.etFromdate:
                dateSelectorfrom();
                break;
        }
    }

    public void dateSelectorfrom(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        etFromdate.setText(dayOfMonth + "-" + (monthOfYear + 1)+ "-" + year);
                        showOtherfundhistory(reqSubMode);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.DAY_OF_MONTH, mDay-(365));
        minDate.set(Calendar.MONTH, mMonth);
        minDate.set(Calendar.YEAR, mYear);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

}
