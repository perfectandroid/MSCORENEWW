package com.creativethoughts.iscore;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.DuedateAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.creativethoughts.iscore.Helper.Common;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DuedatesFragment extends Fragment implements View.OnClickListener{

    RecyclerView rv_standinginst;
    String cusid, token,acctype;
    ProgressDialog progressDialog;
    LinearLayout ll_standnginstr,llreminder;
    TextView tvDeposit,tvLoan, tvTitle;
    FloatingActionButton fab;
    String strHeader="Deposit";
    public DuedatesFragment() {
    }

    public static DuedatesFragment newInstance() {
        DuedatesFragment fragment = new DuedatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_duedate, container, false);
        ll_standnginstr = rootView.findViewById(R.id.ll_standnginstr);
        rv_standinginst = rootView.findViewById(R.id.rv_standinginst);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;

        llreminder = rootView.findViewById(R.id.llreminder);
        tvDeposit = rootView.findViewById(R.id.tvDeposit);
        tvDeposit.setOnClickListener(this);
        tvLoan = rootView.findViewById(R.id.tvLoan);
        tvLoan.setOnClickListener(this);
        fab =rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        acctype ="1";
        getDuedate();

        try {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(currentDate));
            c.add(Calendar.DATE, 14);
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date resultdate = new Date(c.getTimeInMillis());
            String lastDate = sdf.format(resultdate);
           // tvTitle.setText("Due Date List [ "+currentDate+" to "+lastDate+" ]");
            tvTitle.setText("Due Date list for upcoming two weeks");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void getDuedate(){

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(getContext(), R.style.Progress);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("8") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );
                    requestObject1.put("AccountType",IScoreApplication.encryptStart(acctype));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDuedate(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            Log.i("Duedatelist",response.body());
                            if(jObject.getString("StatusCode").equals("0")) {
                                ll_standnginstr.setVisibility(View.VISIBLE);
                                llreminder.setVisibility(View.VISIBLE);
                                JSONObject jobj = jObject.getJSONObject("AccountDueDateDetailsIfo");
                                JSONArray jarray = jobj.getJSONArray("AccountDueDateDetails");
                                if(jarray.length()!=0){
                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
                                    rv_standinginst.setLayoutManager(lLayout);
                                    rv_standinginst.setHasFixedSize(true);
                                    DuedateAdapter adapter = new DuedateAdapter(getContext(), jarray, strHeader);
                                    rv_standinginst.setAdapter(adapter);
                                }else {
                                    ll_standnginstr.setVisibility(View.GONE);
                                    llreminder.setVisibility(View.GONE);
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                    builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }
                            else{
                                ll_standnginstr.setVisibility(View.GONE);
                                llreminder.setVisibility(View.GONE);
                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountDueDateDetailsIfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                    builder.setMessage(ResponseMessage)
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
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
                            ll_standnginstr.setVisibility(View.GONE);
                            llreminder.setVisibility(View.GONE);
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
            DialogUtil.showAlert(getContext(),
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvDeposit:
                tvTitle.setText("Due Date list for upcoming two weeks");
                tvLoan.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle3));
                tvDeposit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle1));
                tvLoan.setTextColor(Color.parseColor("#000000"));
                tvDeposit.setTextColor(Color.parseColor("#ffffff"));
                acctype ="1";
                getDuedate();
                strHeader="Deposit";
                break;
            case R.id.tvLoan:
                tvTitle.setText("Demand list for upcoming two weeks");
                tvLoan.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle));
                tvDeposit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle4));
                tvLoan.setTextColor(Color.parseColor("#ffffff"));
                tvDeposit.setTextColor(Color.parseColor("#000000"));
                acctype ="2";
                getDuedate();
                strHeader="Loan";
                break;
            case R.id.fab:
                 addEvent();
                break;
        }
    }

    public void addEvent() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }
        ContentResolver cr = getContext().getContentResolver();
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2019, 11-1, 28, 9, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2019, 11-1, 29, 11, 40);
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, "Due Notification");
        values.put(CalendarContract.Events.DESCRIPTION, "[Your Loan will be due soon, please do the needful actions.]");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        TimeZone tz = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        values.put(CalendarContract.Events.EVENT_LOCATION, "India");

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, uri.getLastPathSegment());
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 30);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent i = new Intent(getActivity(),HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
