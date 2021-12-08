package com.creativethoughts.iscore;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AccountSummaryAdapter;
import com.creativethoughts.iscore.adapters.CustomerModulesAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.CustomerModulesModel;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountInfoFragment extends Fragment implements View.OnClickListener {

    ListView list_view;
    RecyclerView rv_accountSummary;
    Fragment fragment = null;
    TextView tv_popuptitle, txt_modules, tvDeposit, tvLoan, tvTitle;
    LinearLayout llchooseModule, ll_accSummary ,ll_standingins,ll_notice;
    String cusid, subModule, token, strType="1";
    CustomerModulesAdapter sadapter;
    ArrayList<CustomerModulesModel> cusModuleArrayList = new ArrayList<>();
    ProgressDialog progressDialog;

    // --Commented out by Inspection (9/8/2017 10:25 AM):private static final String TAG = null;


    public AccountInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountInfoFragment.
     */
    public static AccountInfoFragment newInstance() {
        AccountInfoFragment fragment = new AccountInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        int tempId = rootView.getId();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        ActionBar actionBar = activity.getSupportActionBar();
        final Fragment[] fragment = {null};

        tvDeposit = rootView.findViewById(R.id.tvDeposit);
        tvLoan = rootView.findViewById(R.id.tvLoan);
        tvDeposit.setOnClickListener(this);
        tvLoan.setOnClickListener(this);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        txt_modules = rootView.findViewById(R.id.txt_modules);
        ll_accSummary = rootView.findViewById(R.id.ll_accSummary);
        ll_notice = rootView.findViewById(R.id.ll_notice);
        ll_notice.setOnClickListener(this);
        ll_standingins = rootView.findViewById(R.id.ll_standingins);
        ll_standingins.setOnClickListener(this);
        rv_accountSummary = rootView.findViewById(R.id.rv_accountSummary);
        llchooseModule = rootView.findViewById(R.id.llchooseModule);
        llchooseModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cusModuleArrayList.clear();
                getCustomerModules();
            }
        });
        ll_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
              //  Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.container,new NotificationPostingFragment());
                fr.commit();

            }
        });
        ll_standingins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.container,new StandingInstructionFragment());
                fr.commit();

            }
        });

       /* ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ImageButton im_back = rootView.findViewById(R.id.im_back);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                im_back.setBackgroundColor(getResources().getColor(R.color.grey));
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
            }
        });*/

        if (fragment[0] != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment[0]).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }




        TextView mCustomerId = rootView.findViewById(R.id.customerId);
        //mCustomerId.setTextColor(Color.parseColor("#F2F2F2"));

        TextView mCustomerName = rootView.findViewById(R.id.customer_name);
        //mCustomerName.setTextColor(Color.parseColor("#F2F2F2"));

        TextView mAddress1 = rootView.findViewById(R.id.address1);
        //mAddress1.setTextColor(Color.parseColor("#F2F2F2"));

        TextView mAddress2 = rootView.findViewById(R.id.address2);
        //mAddress2.setTextColor(Color.parseColor("#F2F2F2"));

        TextView mMobileNumber = rootView.findViewById(R.id.mobile_number);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
       // token = userDetails.userToken;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;

        mCustomerId.setText(userDetails.userCustomerNo);
        mCustomerName.setText(userDetails.userCustomerName);
        String tempAddress1 = userDetails.userCustomerAddress1 + ", " + userDetails.userCustomerAddress2;
        mAddress1.setText( tempAddress1 );
        mAddress2.setText(userDetails.userCustomerAddress3);
        mMobileNumber.setText(userDetails.userMobileNo);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        tvTitle.setText("Account Summary as on "+df.format(c));
        return rootView;

    }

    private void getCustomerModules() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.cusmodule_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Account");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getModuleList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }


    public void getModuleList(final AlertDialog alertDialog){
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("2") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );
                    requestObject1.put("AccountType",IScoreApplication.encryptStart(strType) );

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
                Call<String> call = apiService.getCustomerModules(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {
                                JSONObject jobj = jObject.getJSONObject("AccountSummaryDetailsListInfo");
                                JSONArray jarray = jobj.getJSONArray("AccountSummaryDetailsList");
                                if(jarray.length()!=0){
                                    for (int k = 0; k < jarray.length(); k++) {
                                        JSONObject jsonObject = jarray.getJSONObject(k);
                                        cusModuleArrayList.add(new CustomerModulesModel(jsonObject.getString("SubModule"), jsonObject.getString("ModuleName")));
                                    }
                                    sadapter = new CustomerModulesAdapter(getContext(), cusModuleArrayList);
                                    list_view.setAdapter(sadapter);
                                    list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            txt_modules.setText(cusModuleArrayList.get(position).getModuleName());
                                            subModule = cusModuleArrayList.get(position).getSubModule();
                                            getAccountSummary(cusModuleArrayList.get(position).getSubModule());
                                            alertDialog.dismiss();
                                        }
                                    });
                                }else {
                                }
                            }
                            else {

                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountSummaryDetailsListInfo");
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

                                }
                                catch (JSONException e){
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

    public void getAccountSummary(String strSubModule){
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("3") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(strSubModule) );

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
                Call<String> call = apiService.getAccountSummary(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                ll_accSummary.setVisibility(View.VISIBLE);
                                JSONObject jobj = jObject.getJSONObject("AccountModuleDetailsListInfo");
                                JSONArray jarray = jobj.getJSONArray("Data");
                                if(jarray.length()!=0) {
                                    GridLayoutManager lLayout = new GridLayoutManager(getContext(), 1);
                                    rv_accountSummary.setLayoutManager(lLayout);
                                    rv_accountSummary.setHasFixedSize(true);
                                    AccountSummaryAdapter adapter = new AccountSummaryAdapter(getContext(), jarray);
                                    rv_accountSummary.setAdapter(adapter);
                                }else {
                                    ll_accSummary.setVisibility(View.GONE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("No data found.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }

                            }
                            else{
                                ll_accSummary.setVisibility(View.GONE);

                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountSummaryDetailsListInfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                                }
                                catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
        }


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

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvLoan:
                tvLoan.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle));
                tvDeposit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle4));
                tvLoan.setTextColor(Color.parseColor("#ffffff"));
                tvDeposit.setTextColor(Color.parseColor("#000000"));
                strType="0";
                ll_accSummary.setVisibility(View.GONE);
                txt_modules.setText("Choose Module");

                break;
            case R.id.tvDeposit:
                tvLoan.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle3));
                tvDeposit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle1));
                tvLoan.setTextColor(Color.parseColor("#000000"));
                tvDeposit.setTextColor(Color.parseColor("#ffffff"));
                strType="1";
                ll_accSummary.setVisibility(View.GONE);
                txt_modules.setText("Choose Module");

                break;
        }
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
                    // handle back button's click listener
                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(),HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }
}


