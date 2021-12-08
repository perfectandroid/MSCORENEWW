package com.creativethoughts.iscore.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Brijesh on 26-03-2015.
 */

public final class CommonUtilities {
    private static Context context;

    private CommonUtilities(){
        throw new IllegalStateException("Illegal");
    }

    //private static final String BASE_URL_KEY = "iscore_base_url_key";
    //private static final String BASE_URL = Common.getBaseUrl();
    //private static final String URI = Common.getApiName();
    private static final String BANK_KEY = "BankKey";
    public static String result = "";
    public static String bal="";
    private static final String BANKHEADER = "BankHeader";
    public static ArrayList<ToAccountDetails> AccountDetails;
    static ArrayAdapter<ToAccountDetails> AccountAdapter = null;
 /*   public static String getBaseUrl() {
        String tempBaserUrl = PreferenceUtil.getInstance().getStringValue(BASE_URL_KEY, "");

        if(TextUtils.isEmpty(tempBaserUrl.trim())) {
            return BASE_URL;
        }

        return tempBaserUrl;
    }*/

    /*public static String getUrl() {
            return BASE_URL + URI;
    }*/
    private static Date convertStingToDate(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.getDefault());
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            //Do nothing
        }
        return date;
    }

    public static String getFormatedMsgDate(String inputDate) {
        Date date = convertMsgStingToDate(inputDate);

        if (date == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH );

        return simpleDateFormat.format(date);
    }

    private static Date convertMsgStingToDate(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyy hh:mm:ss a", Locale.getDefault());
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            //Do nothing
        }
        return date;
    }

    public static long getTimeDifferenceFromNow(String inputDate) {

        Date date = convertStingToDate(inputDate);

        if(date == null) {
            return System.currentTimeMillis();
        }

        Calendar now = Calendar.getInstance();
        Calendar theDay = Calendar.getInstance();
        theDay.setTime(date);

        return now.getTimeInMillis() - theDay.getTimeInMillis();
    }



    public static String getFormatedDate(String inputDate) {
        Date date = convertStingToDate(inputDate);

        if (date == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH );

        return simpleDateFormat.format(date);
    }

    public static String getDateString(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH );
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            //Do nothing
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat convetDateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        return convetDateFormat.format(date);
    }




    public static void setAccountNumber(String accountNo, Spinner spinner, Activity activity) {
        setAccountNumber(accountNo, spinner, activity, R.layout.simple_spinner_item_dark);
    }
    public static void setAccountNumberPassbook(String accountNo, Spinner spinner, Activity activity) {
        setAccountNumber(accountNo, spinner, activity, R.layout.simple_spinner_item );
    }

    private static void setAccountNumber(String accountNo, Spinner spinner, Activity activity, int layout) {
        List<String> accountNos = PBAccountInfoDAO.getInstance().getAccountNos();

        if (accountNos.isEmpty()) {
            return;
        }

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(activity, layout, accountNos);

        spinner.setAdapter(spinnerAdapter);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (accountNo == null) {
            return;
        }

        for (int i = 0; i < accountNos.size(); i++) {
            String account = accountNos.get(i);

            if (TextUtils.isEmpty(account)) {
                continue;
            }

            if (account.equalsIgnoreCase(accountNo)) {
                spinner.setSelection(i);

                break;
            }
        }
    }
    public static void transactionActivitySetAccountNumber(String accNo, Spinner spinner, Activity activity){
//        if (accNo.isEmpty())
//            return;
//        List<String> accountSpinnerItems  ;
//        accountSpinnerItems = PBAccountInfoDAO.getInstance().getAccountNos();
//        ArrayList<String> itemTemp =  new ArrayList<>();
//
//        Log.e(accNo,"accNo   182 "+accNo+"   "+accountSpinnerItems);
//        if (accountSpinnerItems.isEmpty())
//            return;

        showOwnAccToList(spinner,activity);

//        for (int i = 0; i< accountSpinnerItems.size(); i++){
//
//            if (!accountSpinnerItems.get(i).contains("SB") && !accountSpinnerItems.get(i).contains("CA") && !accountSpinnerItems.get(i).contains("OD"))
//                itemTemp.add(accountSpinnerItems.get(i));
//
//        }
//        for ( String item: itemTemp ) {
//            accountSpinnerItems.remove(item);
//        }
//
//        Log.e("accountSpinnerItems","accountSpinnerItems    195   "+accountSpinnerItems);
//
//        ArrayAdapter<String> spinnerAdapter =
//                new ArrayAdapter< >(activity, R.layout.simple_spinner_item_dark, accountSpinnerItems);
//        spinner.setAdapter(spinnerAdapter);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        for (int i = 0; i < accountSpinnerItems.size(); i++) {
//            String account = accountSpinnerItems.get(i);
//
//            if (TextUtils.isEmpty(account)) {
//                continue;
//            }
//            SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//            if (account.equalsIgnoreCase(settingsModel.customerId)) {
//                spinner.setSelection(i);
//
//                break;
//            }
//        }

    }

    private static void showOwnAccToList(Spinner spinner, Activity activity) {


        SharedPreferences pref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;

                    requestObject1.put("ReqMode", IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
//                    requestObject1.put("BankKey",IScoreApplication.encryptStart(Resources.getSystem().getString(R.string.BankKey)));
//                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(Resources.getSystem().getString(R.string.BankHeader)));
                    SharedPreferences bankkeypref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1 ","requestObject1  302   "+requestObject1);

                } catch (Exception e) {
                    e.printStackTrace();

                    Log.e("Exception ","Exception  302   "+e.toString());

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try{

                            Log.e("response ","response  302   "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
                                if(Jarray.length()!=0) {
                                    List<String> accNoList = new ArrayList< >();
                                    List<String> accountSpinnerItems = new ArrayList< >();
                                    AccountDetails = new ArrayList<>();
                                    for (int k = 0; k < Jarray.length(); k++) {
                                        JSONObject kjsonObject = Jarray.getJSONObject(k);
                                        Log.e("jsonobject","jsonobject   318    "+kjsonObject.getString("AccountNumber"));
                                        accNoList.add(kjsonObject.getString("AccountNumber"));
                                        accNoList.add(kjsonObject.getString("BranchName"));
                                        accountSpinnerItems.add(kjsonObject.getString("AccountNumber"));
                                        AccountDetails.add(new ToAccountDetails( kjsonObject.getString("FK_Account"), kjsonObject.getString("AccountNumber"), kjsonObject.getString("SubModule"), kjsonObject.getString("Balance"), kjsonObject.getString("typeShort"), kjsonObject.getString("BranchName")));
                                       // accountSpinnerItems.add(kjsonObject.getString("BranchName"));

                                    }


                                   // accountSpinnerItems = accNoList;
                                    Log.e("accountSpinnerItems","accountSpinnerItems    195   "+accountSpinnerItems);
                                    Log.e("accNoList","accNoList    195   "+accNoList);

                                    if (accountSpinnerItems.size()>0){
                                       /* ArrayAdapter<String> spinnerAdapter =
                                                new ArrayAdapter< >(activity, R.layout.simple_spinner_item_dark, accountSpinnerItems);
                                        spinner.setAdapter(spinnerAdapter);
                                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
*/
                                      //  AccountAdapter = new ArrayAdapter<>(OwnAccountFundTransferActivity.this,  R.layout.simple_spinner_item_dark,R.id.text1, AccountDetails);

                                        AccountAdapter = new ArrayAdapter<>(activity,  R.layout.simple_spinner_item_dark,R.id.textview, AccountDetails);
                                        //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                        spinner.setAdapter(AccountAdapter);



                                        for (int i = 0; i < AccountDetails.size(); i++) {
                                     //   for (int i = 0; i < accountSpinnerItems.size(); i++) {
                                            String account = accountSpinnerItems.get(i);



                                            if (TextUtils.isEmpty(account)) {
                                                continue;
                                            }
                                            SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
                                            if (account.equalsIgnoreCase(settingsModel.customerId)) {
                                                spinner.setSelection(i);


                                                break;
                                            }

                                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                    // TextView textView = (TextView)mAccountTypeSpinner.getSelectedView();
                                                    result = AccountAdapter.getItem(position).getBranchName();
                                                    bal=AccountAdapter.getItem(position).getBalance();


                                                                  //   Toast.makeText(activity,AccountAdapter.getItem(position).getBranchName(),Toast.LENGTH_LONG).show();


                                                }




                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {
                                                    //Do nothing
                                                }
                                            });
                                        }
                                    }





                                }
                                else {

                                }
                            }
                            else {

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
            catch (Exception e)
            {

            }
        }
        else {

        }

    }

    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
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

    private static SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
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

    public static String getDecimelFormate(double amount) {
        DecimalFormat fmt = new DecimalFormat("#,##,##,##,###.00");
        String amt = fmt.format(amount);
        if (amt.substring(0,1).equals(".")){
            amt = "0"+amt;
        }
        return amt;
    }
    public static String getDecimelFormateForEditText(double amount) {
        DecimalFormat fmt = new DecimalFormat("#,##,##,##,###");
        String amt = fmt.format(amount);
        if (amt.substring(0,1).equals(".")){
            amt = "0"+amt;
        }
        return amt;
    }




}
