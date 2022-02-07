package com.creativethoughts.iscore;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.receiver.ConnectivityReceiver;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HomeActivity extends AppCompatActivity implements NavigationDrawerCallbacks, ConnectivityReceiver.ConnectivityReceiverListener {

    static String bank_Key, bank_Header,version;
    static String host_nameCommon, asset_namecommon;
    String customerId ="";


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String mParamsHideRecharge ;
    private String mParamsHideMoneyTransfer;

    private  int getCurrentVersionNumber(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
        }

        return 1;
    }


    @Override
    public void onPause() {

        super.onPause();  // Always call the superclass method first
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
        customerId = customerIdSP.getString("customerId","");
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,  findViewById(R.id.drawer_layout));

//        bank_Key = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0).getString("bankkey", null);
//        bank_Header =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0).getString("bankheader", null);
//        host_nameCommon = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0).getString("hostname", null);
//        asset_namecommon =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0).getString("certificateassetname", null);
        onSectionAttached(0);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onBackPressed();
       versionCheck();



    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        mParamsHideRecharge = "EMPTY";
        mParamsHideMoneyTransfer  = "EMPTY";
        Fragment fragment = null;
        Intent i;
        switch (position) {
            case 0:
                fragment = FragmentMenuCard.newInstance(mParamsHideRecharge, mParamsHideMoneyTransfer);
                Log.i("position",String.valueOf(position));
                break;
            case 1:
                fragment = ProfileFragment.newInstance();
              /*i=new Intent(this,ProfileActivity.class);
              startActivity(i);*/

                break;
            case 2:
//                if(mNavigationDrawerFragment != null) {
//                  //  UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//                  //  PBMessagesDAO.getInstance().markMessageAsRead(userDetails.customerId);
//                    PBMessagesDAO.getInstance().markMessageAsRead(customerId);
//                    mNavigationDrawerFragment.refreshMenu();
//                }
//                fragment = MessagesFragment.newInstance();

                Intent im = new Intent(HomeActivity.this,MessageActivity.class);
                startActivity(im);
                break;
            case 3:
                if(mNavigationDrawerFragment != null) {
                   // UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();

                  //  PBMessagesDAO.getInstance().markOffersAsRead(userDetails.customerId);
                    PBMessagesDAO.getInstance().markOffersAsRead(customerId);
                    mNavigationDrawerFragment.refreshMenu();
                }
                fragment = OffersFragment.newInstance();
                break;
            case 4:
                fragment = KsebBillStatusFragment.newInstance();
                break;
            case 5:

              /*  i=new Intent(this,MoreActivity.class);
                startActivity(i);*/

                fragment = MoreFragment.newInstance();
                break;
            case 6:
               /* i=new Intent(this, DuedateActivity.class);
                startActivity(i);*/
                fragment = DuedatesFragment.newInstance();
                break;
            case 7:
              /*  i=new Intent(this,MoreActivity.class);
                startActivity(i);*/

                fragment = MoreFragment.newInstance();
                break;
            case 8:
                fragment = ChangePinFragment.newInstance();
                break;
            case 9:
//                fragment = SettingsFragment.newInstance();

                Intent setIntent=new Intent(this,SettingsActivity.class);
                startActivity(setIntent);
                break;
            case 10:
                getPermissionAndQuit();
                break;
            case 11:
                getPermissionAndQuit();
                break;
            case 12:
                break;
            default:
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
            onSectionAttached(position);
        }
    }


    private void onSectionAttached(int number) {

        String mTitle = "";
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section9);
                break;
            case 1:
                mTitle = "Profile";
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.kseb_sub_name_bill_status_on_drawer);
                break;
            case 5:
                mTitle = "More";
                break;
            case 6:
                mTitle = "Due Date Reminder";
                break;
            case 7:
                mTitle = "More";
                break;
            case 8:
                mTitle = getString(R.string.title_section5);
                break;
            case 9:
                mTitle = getString(R.string.title_section6);
                break;
            case 10:
                mTitle = getString(R.string.title_section8);
                break;
            case 11:
                mTitle = getString(R.string.title_section8);
                break;

            default:
                break;
        }

        try{
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(mTitle);
        }catch (Exception e){
            Log.e("REcharge", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.container );
            if ( currentFragment instanceof FragmentMenuCard ){
                getPermissionAndQuit();
            }else {
                super.onBackPressed();
            }
        }
    }

    private void getPermissionAndQuit() {

//        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
////                .setTitleText("Are you sure?")
//                .setContentText("Do you want to quit?")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCustomImage(R.drawable.aappicon)
//                 .setConfirmClickListener( sweetAlertDialog -> finishAffinity())
//                .show();
        Quit();
    }

    private void Quit(){
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.quit_popup, null);
            TextView tv_share =  layout.findViewById(R.id.tv_share);
            TextView tv_cancel =  layout.findViewById(R.id.tv_cancel);

            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();




            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            tv_share.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View view) {

                    finishAffinity();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        versionCheck();

    }
    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        try{
            IScoreApplication.setConnectivityListener( this );
        }catch ( Exception e ){
            //Do nothing
        }
    }

    private void versionCheck(){
        SharedPreferences pref1 =getApplicationContext().getSharedPreferences(Config.SHARED_PREF14, 0);
        String strloginmobile=pref1.getString("LoginMobileNo", null);

        SharedPreferences TestMobileNoSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF15, 0);
        String strTestmobile=TestMobileNoSP.getString("TestingMobileNo", null);

        if(strloginmobile == null || strloginmobile.isEmpty()) {
            SharedPreferences versnSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0);
            SharedPreferences.Editor versnEditer = versnSP.edit();
            versnEditer.putString("version", "false");
            versnEditer.commit();
        }
        else {
            if (strTestmobile.equals(strloginmobile)) {
                SharedPreferences versnSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0);
                SharedPreferences.Editor versnEditer = versnSP.edit();
                versnEditer.putString("version", "true");
                versnEditer.commit();
            }
            else if (!strTestmobile.equals(strloginmobile)) {
                SharedPreferences versnSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0);
                SharedPreferences.Editor versnEditer = versnSP.edit();
                versnEditer.putString("version", "false");
                versnEditer.commit();
            }
        }

        version = getApplicationContext().getSharedPreferences(Config.SHARED_PREF25, 0).getString("version", null);

        Log.i("Boolean", version);
        if(version.equals("true"))
        {

        }
        else  if(version.equals("false")) {
            //   getVersioncode();

           /* if (NetworkUtil.isOnline()) {
                int versionNumber = getCurrentVersionNumber(HomeActivity.this);
//            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);
             *//*   SharedPreferences pref = getApplicationContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL = pref.getString("baseurl", null);*//*

                SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL=pref.getString("baseurl", null)+"api/MV3";

                String url;
                try {
                    url = BASE_URL +
                            "/Checkstatus?versionNo=" +
                            IScoreApplication.encodedUrl(IScoreApplication.encryptStart(versionNumber + ""));
                } catch (Exception e) {
                    url = "";
                }
                NetworkManager.getInstance().connector(url, new ResponseManager() {
                    @Override
                    public void onSuccess(String result) {
                        result = result.trim();
                        try {
                            int res = Integer.parseInt(result);
                            Log.e("TAG", "res   332   " + res);
                            if (res == 10)
                                goToPlayStore();
                        } catch (Exception e) {
                            //Do nothing
                        }

                    }

                    @Override
                    public void onError(String error) {
                        //Do nothing
                    }
                }, null, null);
            }*/
        }
    }

    private void getVersioncode() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        int versionNumber = getCurrentVersionNumber(HomeActivity.this);
        if (NetworkUtil.isOnline()) {
            try{
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

                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("versionNo", IScoreApplication.encryptStart(String.valueOf(versionNumber)));


                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1 vrsn",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getVersioncode(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response versn   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                          //  JSONObject j1 = jObject.getJSONObject("CheckVersionCode");
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0"))
                            {
                                String result = jObject.getJSONObject("CheckVersionCode").getString("ChkStatusOutput");
                                result = result.trim();
                                try {
                                    int res = Integer.parseInt(result);
                                    Log.e("TAG", "res   332   " + res);
                                    if (res == 10)
                                        goToPlayStore();
                                } catch (Exception e) {
                                    //Do nothing
                                }

                            }
                            else{


                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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

                                }catch (Exception e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private void goToPlayStore( ){
        try{
//            String url = getResources().getString(R.string.app_link );
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
            String url = pref.getString("PlayStoreLink", null);

            new URL( url );
        }catch ( MalformedURLException e ){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setMessage("The app is under maintenance. Sorry for the inconvenience.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) ->  finish() );
            alertDialogBuilder.show();
            return;
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog( HomeActivity.this , SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialog
                .setCustomImage(R.drawable.aappicon)
                .setConfirmText("OK!")
                .showCancelButton(true)
                .setTitleText("New Version Available")
                .setContentText("New version of this application is available.\nClick OK to upgrade now")
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog1.dismissWithAnimation();
                    finish();
//                    String url = getResources().getString(R.string.app_link );

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);

                    String url = pref.getString("PlayStoreLink", null);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                })
                .setCancelable(false);
        sweetAlertDialog.show();

    }


    public static String getBankkey() {
        try {
            return bank_Key; }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getBankheader() {
       try {
            return bank_Header;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getHostnameSubject() {
        try {
            return host_nameCommon;
       }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI; }
   }

    public static String getCertificateAssetName() {
       try {
            return asset_namecommon;
       }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
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

    private void alertMessage1(String s, String s1) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(s);
        tv_msg2.setText(s1);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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
