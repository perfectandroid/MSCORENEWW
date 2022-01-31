package com.creativethoughts.iscore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Recharge.AccountAdapter;
import com.creativethoughts.iscore.Recharge.OnItemClickListener;
import com.creativethoughts.iscore.Recharge.RechargeActivity;
import com.creativethoughts.iscore.Retrofit.APIInterface;
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
import java.util.ArrayList;

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

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {

    String TAG = "SettingsActivity";
    private ProgressDialog progressDialog;
    AlertDialog dialog =  null;

    private Button mApplyBtn;
    private Spinner mDaySpinner;
  //  private Spinner mDefaultAccountSpinner;
    private Spinner mHourSpinner;
    private Spinner mMinSpinner;

    private AutoCompleteTextView act_DefAcc;



    private int mSelectedHours = 0;
    private int mSelectedMinute = 0;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setInitialise();
        setRegister();

        SharedPreferences SelectedDaysSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF37, 0);

//        SharedPreferences.Editor SelectedDaysEditer = SelectedDaysSP.edit();
//        SelectedDaysEditer.putString("SelectedDays",  object3.getString("pin"));
//        SelectedDaysEditer.commit();

        if (SelectedDaysSP.getString("SelectedDays","").equals("")){
            setDaysForDropDown(null);
            updateIntervalHours(0);
            updateIntervalMinutes(1);
        }else {

            SharedPreferences SelectedHoursSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF38, 0);
            SharedPreferences SelectedMinuteSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF39, 0);
            SharedPreferences SelectedAccountSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF40, 0);

            setDaysForDropDown(SelectedDaysSP.getString("SelectedDays",""));
            updateIntervalHours(Integer.parseInt(SelectedHoursSP.getString("SelectedHours","")));
            updateIntervalMinutes(Integer.parseInt(SelectedMinuteSP.getString("SelectedMinute","")));

            act_DefAcc.setText(""+SelectedAccountSP.getString("SelectedAccount",""));
        }

        getAccList();
    }

    private void setRegister() {
        mApplyBtn.setOnClickListener(this);
        act_DefAcc.setOnClickListener(this);
    }

    private void setInitialise() {

        mDaySpinner              =  findViewById(R.id.spnUpdateDays);
        mHourSpinner             =  findViewById(R.id.hoursSpinner);
        mMinSpinner              =  findViewById(R.id.minutesSpinner);

      //  mDefaultAccountSpinner   =  findViewById(R.id.spnDefAcc);
        mApplyBtn                =  findViewById(R.id.btnApply);
        act_DefAcc                =  findViewById(R.id.act_DefAcc);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnApply:
                applayChanges();
                break;

            case R.id.act_DefAcc:
                accountPop();
                break;
        }

    }

    private void applayChanges() {

        mSelectedHours = Integer.parseInt(mHourSpinner.getSelectedItem().toString());
        mSelectedMinute = Integer.parseInt(mMinSpinner.getSelectedItem().toString());

        if (mSelectedHours == 0 && mSelectedMinute == 0) {
            DialogUtil.showAlert(this,
                    "Please give minimum 15 minutes  interval for data update");

        }
        else {
            if (mSelectedHours == 0 && mSelectedMinute < 15) {
                DialogUtil.showAlert(this,
                        "Please give minimum 15 minutes  interval for data update");
            }
            else {

                String days = mDaySpinner.getSelectedItem().toString();
                String accountNumber = act_DefAcc.getText().toString();
                if (accountNumber.equals("")){
                    DialogUtil.showAlert(this,
                            "Select Account Number");
                }else {


                    try {
                        long intervalTimeInMillis = ((mSelectedHours * 60 * 60) + (mSelectedMinute * 60)) * 1000;

                        SharedPreferences SelectedDaysSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF37, 0);
                        SharedPreferences.Editor SelectedDaysEditer = SelectedDaysSP.edit();
                        SelectedDaysEditer.putString("SelectedDays",  days);
                        SelectedDaysEditer.commit();

                        SharedPreferences SelectedHoursSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF38, 0);
                        SharedPreferences.Editor SelectedHoursEditer = SelectedHoursSP.edit();
                        SelectedHoursEditer.putString("SelectedHours", String.valueOf(mSelectedHours));
                        SelectedHoursEditer.commit();

                        SharedPreferences SelectedMinuteSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF39, 0);
                        SharedPreferences.Editor SelectedMinuteEditer = SelectedMinuteSP.edit();
                        SelectedMinuteEditer.putString("SelectedMinute", String.valueOf(mSelectedMinute));
                        SelectedMinuteEditer.commit();

                        SharedPreferences SelectedAccountSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF40, 0);
                        SharedPreferences.Editor SelectedAccountEditer = SelectedAccountSP.edit();
                        SelectedAccountEditer.putString("SelectedAccount",  accountNumber);
                        SelectedAccountEditer.commit();

                        SharedPreferences syncintervaltimemsSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF41, 0);
                        SharedPreferences.Editor syncintervaltimemsEditer = syncintervaltimemsSP.edit();
                        syncintervaltimemsEditer.putString("syncintervaltimems", String.valueOf(intervalTimeInMillis));
                        syncintervaltimemsEditer.commit();

                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Update Successfully")
//                                builder.setMessage("No data found.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent i = new Intent(SettingsActivity.this,HomeActivity.class);
                                        startActivity(i);
                                        finish();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                    }catch (Exception e){

//                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//                        builder.setMessage(""+e.toString())
////                                builder.setMessage("No data found.")
//                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();


                    }



                }

            }
        }
    }

    private void accountPop() {

        if (jresult.length()>0){
            AlertDialog.Builder builder2 = new AlertDialog.Builder(SettingsActivity.this);
            final View customLayout2 = getLayoutInflater().inflate(R.layout.popup_accountno, null);
            RecyclerView rvAccountno = customLayout2.findViewById(R.id.rvAccountno);
            builder2.setView(customLayout2);


            GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
            rvAccountno.setLayoutManager(lLayout);
            rvAccountno.setHasFixedSize(true);
            AccountAdapter adapter = new AccountAdapter(getApplicationContext(), jresult);
            rvAccountno.setAdapter(adapter);
            adapter.setOnItemClickListener(SettingsActivity.this);

            // dialog = builder2.create();
            dialog = builder2.create();
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private void getAccList() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(SettingsActivity.this, R.style.Progress);
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
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);


                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));

                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1   169   "+requestObject1);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG,"response   1692   "+response.body());

                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray newJArray = object.getJSONArray("OwnAccountdetailsList");

                                for(int i=0;i<newJArray.length();i++){
                                    try {
                                        JSONObject json = newJArray.getJSONObject(i);
                                        jresult.put(json);
                                        //accountlist.add(json.getString("AccountNumber"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                Log.e(TAG,"jresult   1693   "+jresult);
                                SharedPreferences SelectedAccountSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF40, 0);
                                if (SelectedAccountSP.getString("SelectedAccount","").equals("")){
                                    act_DefAcc.setText(""+jresult.getJSONObject(0).getString("AccountNumber"));
                                }

                            }
                            else {

                                try{
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
                        }
                        catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
                progressDialog.dismiss();
            }
        }
        else {

            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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

    private void updateIntervalHours(int hours) {
        ArrayList<String> hoursItems = new ArrayList<>();

        for(int i = 0; i < 24; i++) {
            hoursItems.add(String.format("%02d", i));
        }

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(this, R.layout.simple_spinner_item_dark, hoursItems);

        mHourSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mHourSpinner.setSelection(hours);
    }

    private void updateIntervalMinutes(int minutes) {
        ArrayList<String> minutesItems = new ArrayList<>();
        minutesItems.add("00");
        minutesItems.add("15");
        minutesItems.add("30");
        minutesItems.add("45");

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(this, R.layout.simple_spinner_item_dark, minutesItems);

        mMinSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        switch (minutes) {
            case 0:
                mMinSpinner.setSelection(0);
                break;
            case 15:
                mMinSpinner.setSelection(1);
                break;
            case 30:
                mMinSpinner.setSelection(2);
                break;
            case 45:
                mMinSpinner.setSelection(3);
                break;
            default:
                break;
        }
    }

    private void setDaysForDropDown(String days) {
        Activity activity = this;

        if ( activity == null )
            return;

        ArrayList<String> items = new ArrayList<>();
        items.add("7");
        items.add("10");
        items.add("14");
        items.add("30");
        items.add("60");
        items.add("120");
        items.add("150");
        items.add("180");

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(activity, R.layout.simple_spinner_item_dark, items);

        mDaySpinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (days == null) {
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            String day = items.get(i);

            if (TextUtils.isEmpty(day)) {
                continue;
            }

            if (day.equalsIgnoreCase(days)) {
                mDaySpinner.setSelection(i);

                break;
            }
        }
    }


    @Override
    public void onItemClick(View v, int position, String data, String mode) {

        if (data.equals("account")){
            dialog.dismiss();

            act_DefAcc.setText("");
            try {
                JSONObject jsonObject = jresult.getJSONObject(position);
                act_DefAcc.setText(""+jsonObject.getString("AccountNumber"));
            }catch (Exception e){

            }
        }

    }
}
