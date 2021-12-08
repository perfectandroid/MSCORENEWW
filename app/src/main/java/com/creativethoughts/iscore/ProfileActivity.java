package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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

public class ProfileActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    TextView txtv_name,txtv_address,txtv_contact,txtv_email,txtv_gender,txtv_dob,txtv_Cusid;
    String cusid,tokenn,cusno;
    CircleImageView profileImg;
    LinearLayout ll_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtv_name = findViewById(R.id.txtv_name);
        txtv_address = findViewById(R.id.txtv_address);
        txtv_contact = findViewById(R.id.txtv_contact);
        txtv_email = findViewById(R.id.txtv_email);
        txtv_gender = findViewById(R.id.txtv_gender);
        txtv_dob = findViewById(R.id.txtv_dob);
        txtv_Cusid = findViewById(R.id.txtv_Cusid);
        profileImg = findViewById(R.id.profileImg);
        ll_email = findViewById(R.id.ll_email);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        cusno=userDetails.userCustomerNo;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        tokenn = loginCredential.token;

        showProfile();
        SharedPreferences sperf = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
        if(sperf.getString("custimage", null) == null ) {
            getCustomerImage();
        }else{
            try{
            byte[] decodedString = Base64.decode(sperf.getString("custimage", null), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(this)
                    .load(stream.toByteArray())
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .into(profileImg);
            }catch (Exception e){e.printStackTrace();}
        }

    }

    private void getCustomerImage() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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

                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));

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
                Call<String> call = apiService.getImage(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobjt = jObject.getJSONObject("CustomerImageDets");
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0"))
                            {
                                SharedPreferences custimageSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                SharedPreferences.Editor custimageSPEditer = custimageSP.edit();
                                custimageSPEditer.putString("custimage", jobjt.getString("CusImage"));
                                custimageSPEditer.commit();
                                try{
                                byte[] decodedString = Base64.decode(jobjt.getString("CusImage"), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                Glide.with(ProfileActivity.this)
                                        .load(stream.toByteArray())
                                        .placeholder(R.drawable.person)
                                        .error(R.drawable.person)
                                        .into(profileImg);
                                }catch (Exception e){e.printStackTrace();}
                                //Toast.makeText(getActivity(),"Image found",Toast.LENGTH_LONG).show();
                            }
                            else if(statuscode.equals("-1"))
                            {
                                //  Toast.makeText(getActivity(),"Statuscode -1 so no image found",Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Imagedetails","Something went wrong");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            DialogUtil.showAlert(ProfileActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

    private void showProfile() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(ProfileActivity.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("7");
                String token = IScoreApplication.encryptStart(tokenn);
                String fkcus = IScoreApplication.encryptStart(cusid);
                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token",token);
                    requestObject1.put("FK_Customer",fkcus);
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
                Call<String> call = apiService.getProfile(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.i("Custdetails",response.body());
                            JSONObject jsonObj = new JSONObject(response.body());

                            if(jsonObj.getString("StatusCode").equals("0")){

                                    JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerProfileDetailsInfo");
                                    Log.i("First ", String.valueOf(jsonObj1));
                                    String addrss = jsonObj1.getString("Address");
                                    String lives = getColoredSpanned("Address ", "#472E72");
                                    txtv_address.setText(Html.fromHtml(lives + ": " + addrss));

                                    String name = jsonObj1.getString("CustomerName");
                                    String nme = getColoredSpanned("Name ", "#472E72");
                                    txtv_name.setText(Html.fromHtml(/*nme+": "+*/name));
                                    txtv_Cusid.setText("( Customer Id : " + cusno + " )");

                                    String contact = jsonObj1.getString("PhoneNumber");
                                    String cntct = getColoredSpanned("Contact No ", "#472E72");
                                    txtv_contact.setText(Html.fromHtml(cntct + ": " + contact));


                                    String mail = jsonObj1.getString("Email");
                                    String email = getColoredSpanned("Email ", "#472E72");
                                    if (mail.length() == 0 ){
                                        ll_email.setVisibility(View.GONE);
                                    }
                                    else{
                                        ll_email.setVisibility(View.VISIBLE);
                                        txtv_email.setText(Html.fromHtml(email + ": " + mail));
                                    }


                                    String gender = jsonObj1.getString("Gender");
                                    String gnd = getColoredSpanned("Gender ", "#472E72");
                                    txtv_gender.setText(Html.fromHtml(gnd + ": " + gender));


                                    String dob = jsonObj1.getString("DateOfBirth");
                                    String db = getColoredSpanned("Date Of Birth ", "#472E72");
                                    txtv_dob.setText(Html.fromHtml(db + ": " + dob));
                            }
                            else {
                                try{
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerProfileDetailsInfo");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileActivity.this);
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
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileActivity.this);
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
            DialogUtil.showAlert(ProfileActivity.this,
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

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}
