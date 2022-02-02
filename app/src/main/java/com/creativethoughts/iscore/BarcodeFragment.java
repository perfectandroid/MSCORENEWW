package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
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
import java.text.SimpleDateFormat;
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

public class BarcodeFragment extends Fragment {
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;
    private RadioGroup radioGroup;
    String customeridwithcode,customeridwithcode1;
    private ImageView imgv_qrcode,imgv_barcode,imgv_customerimg;
    Bitmap bitmap ;
    Thread thread ;
    public final static int QRcodeWidth = 500 ;
    String cusid, token, vritualcardCombination;
    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_barcode, container, false);

//        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
//        cusid = userDetails.customerId;
//        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
//        token = loginCredential.token;

        SharedPreferences customerIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF26, 0);
        cusid = customerIdSP.getString("customerId","");
        SharedPreferences tokenIdSP = getActivity().getSharedPreferences(Config.SHARED_PREF35, 0);
        token = tokenIdSP.getString("Token","");

        getVritualcardCombination();
        SharedPreferences customerNameSP = getActivity().getSharedPreferences(Config.SHARED_PREF28, 0);
        String username = customerNameSP.getString("customerName","");
        SharedPreferences customerAddress1SP = getActivity().getSharedPreferences(Config.SHARED_PREF29, 0);
        SharedPreferences customerAddress2SP = getActivity().getSharedPreferences(Config.SHARED_PREF30, 0);
        String useraddress=customerAddress1SP.getString("customerAddress1","")+","+customerAddress2SP.getString("customerAddress2","");
        SharedPreferences customerNoSP = getActivity().getSharedPreferences(Config.SHARED_PREF27, 0);
        String customerid = customerNoSP.getString("customerNo","");
        SharedPreferences mobileNoSP = getActivity().getSharedPreferences(Config.SHARED_PREF31, 0);
        String phone =mobileNoSP.getString("mobileNo","");

        customeridwithcode = vritualcardCombination;
       // customeridwithcode = customerid+"15";
        customeridwithcode1 = customerid;
      //  Toast.makeText(getActivity(),cusid,Toast.LENGTH_LONG).show();
        Log.i("Userdetails",customerNameSP.getString("customerName","")+"\n"+ customerAddress1SP.getString("customerAddress1","") + ", " + customerAddress2SP.getString("customerAddress2",""));


        TextView txtv_userdetails = (TextView) rootView.findViewById(R.id.txt_userdetails);
        TextView txtv_custgenid = (TextView) rootView.findViewById(R.id.txtv_custgenid);
        TextView txt_userid =  rootView.findViewById(R.id.txt_userid);

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Log.i("Date",date);
        String[] items1 = date.split("/");
        String date1=items1[0];
        String month=items1[1];
        Log.i("date",date1);
        Log.i("month",month);
        String cusgenid =date1+customeridwithcode1+month;
        Log.i("cusgenid",cusgenid);
        txtv_custgenid.setText(cusgenid);
        txt_userid.setText(customerid);


        txtv_userdetails.setText(username);
        txtv_userdetails.setAllCaps(false);


        imgv_customerimg = (ImageView) rootView.findViewById(R.id.imgv_customerimg);


        SharedPreferences sperf = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
        if(sperf.getString("custimage", null) == null ) {
            getCustomerImage();
        }else {
            try{
            if(!sperf.getString("custimage", null).isEmpty()) {
            byte[] decodedString = Base64.decode(sperf.getString("custimage", null), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(getActivity())
                    .load(stream.toByteArray())
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .into(imgv_customerimg);
            }
            }catch (Exception e){e.printStackTrace();}
        }



        return rootView;
    }

    private void getCustomerImage() {

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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

                    requestObject1.put("FK_Customer",/*cusid*/IScoreApplication.encryptStart(cusid));

                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
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
                            Log.i("Imagedetails",response.body());
                           // Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobjt = jObject.getJSONObject("CustomerImageDets");
                            String statuscode = jObject.getString("StatusCode");
                            if(statuscode.equals("0"))
                            {
                                SharedPreferences custimageSP = getContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                                SharedPreferences.Editor custimageSPEditer = custimageSP.edit();
                                custimageSPEditer.putString("custimage", jobjt.getString("CusImage"));
                                custimageSPEditer.commit();
                                try{
                                    byte[] decodedString = Base64.decode(jobjt.getString("CusImage"), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    Glide.with(getActivity())
                                            .load(stream.toByteArray())
                                            .placeholder(R.drawable.person)
                                            .error(R.drawable.person)
                                            .into(imgv_customerimg);
                                }catch (Exception e){e.printStackTrace();}
                                //Toast.makeText(getActivity(),"Image found",Toast.LENGTH_LONG).show();
                            }
                            else  if(statuscode.equals("-1"))
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
        } else {
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
        }

    }



    public void getVritualcardCombination(){
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("9") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );

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
                Call<String> call = apiService.getBardCodeData(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {
                                JSONObject jobj = jObject.getJSONObject("BarcodeFormatDet");
                                vritualcardCombination = jobj.getString("BarcodeFormat");
                            }
                            else{
                                try{
                                    JSONObject jsonObj1 = jObject.getJSONObject("BarcodeFormatDet");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                                } catch (JSONException e){
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
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {}
                });



            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            DialogUtil.showAlert(getContext(),
//                    "Network is currently unavailable. Please try again later.");
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



}

