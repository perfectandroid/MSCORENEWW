package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.money_transfer.AddSenderReceiverResponseModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TraansactionOTPActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String BUNDLE_DATA_SENDER_ID = "sender_id";
    private static final String BUNDLE_DATA_RECEIVER_ID = "receiver_id";
    private static final String BUNDLE_DATA_TRANSACTION_ID = "transaction_id";
    private static final String BUNDLE_DATA_IS_TRANSACTION = "is_transaction";
    private static final String BUNDLE_OTP_REFERENCE = "otpReference";
    private static final String BUNDLE_MOBILE = "mobile";
    private static final String BUNDLE_RESEND_LINK = "resend_link";
    private static final String BUNDLE_IS_SENDER = "is_sender";
    private static final String BUNDLE_SENDER_RECEIVER_OBJ = "sender_reciever_obj";
    protected Button button;
    private String mSenderId;
    private String mReceiverId;
    private String mTransactionId;
    private boolean mIsForTransaction;
    private boolean mIsSender;
    public TextView txt_amtinword;
    private AppCompatEditText mOTPEt;
    private String mOtpReferenceNo;
    private String mMobileNo;
    private ProgressDialog mProgressDialog;
    private String mResendLink;
    private AddSenderReceiverResponseModel mAddSenderReceiverResponseModel;
    String cusid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_transaction_ot);

        setRegViews();
    }

    private void setRegViews() {

        mOTPEt =   findViewById(R.id.otp);

        button =   findViewById(R.id.btn_submit);
        //txt_amtinword =   view.findViewById(R.id.txt_amtinword);

        button.setOnClickListener( this );
        Button btnResendOtp  =  findViewById( R.id.btn_resend_otp );
        btnResendOtp.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_submit:
                if (isValid()) {
                    if (NetworkUtil.isOnline()) {
                        button.setEnabled(false);
                        String otp = mOTPEt.getText().toString();
                        getonfirmOTP(otp);
                    } else {

                        alertMessage1("",  "Network is currently unavailable. Please try again later.");

                       /* DialogUtil.showAlert(getActivity(),
                                "Network is currently unavailable. Please try again later.");*/
                    }
                }
                break;
            case R.id.btn_resend_otp:
                break;
        }
    }

    private void getonfirmOTP(String otp) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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


                    SharedPreferences cusidpref = TraansactionOTPActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    cusid=cusidpref.getString("customerId", null);



                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("senderid", IScoreApplication.encryptStart("firstName"));
                    requestObject1.put("receiverid", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("transcationID", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("OTP", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("otpRefNo", IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("mobile", IScoreApplication.encryptStart(cusid) );



                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1 addsndr",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAddsender(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response ownaccount   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            String statusCode=jObject.getString("StatusCode");
                            String statusmsg = jObject.getString("StatusMessage");

                            if ( statusCode!= null && statusCode.equals("200") ){

                                Intent i = new Intent(TraansactionOTPActivity.this,TraansactionOTPActivity.class);
                                startActivity(i);
                                QuickSuccess();
                                //   !moneyTransferResponseModel.getOtpRefNo().equals("0")){
                               /* TransactionOTPFragment.openTransactionOTP(getActivity(), mSender, mReceiver,
                                        moneyTransferResponseModel.getTransactionId(), new AddSenderReceiverResponseModel(),
                                        moneyTransferResponseModel.getOtpRefNo(), mOtpResendLink);*/
                                //     Log.e(TAG,"1091   "+moneyTransferResponseModel.getMessage());
                            }
                            else if ( statusCode!= null && statusCode.equals("200")){
                                //   moneyTransferResponseModel.getOtpRefNo().equals("0")){

                               /* QuickSuccess(mAccNo,moneyTransferResponseModel.getStatus(),moneyTransferResponseModel.getMessage(),"",
                                        moneyTransferResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch,mAmount);*/

                                //  Log.e(TAG,"10912   "+moneyTransferResponseModel.getMessage());

                            }
                            else if (  statusCode.equals("500")){
                                Intent i = new Intent(TraansactionOTPActivity.this,TraansactionOTPActivity.class);
                                startActivity(i);
                 /*   TransactionOTPFragment.openTransactionOTP(getActivity(), mSender, mReceiver,
                            moneyTransferResponseModel.getTransactionId(), new AddSenderReceiverResponseModel(),
                            moneyTransferResponseModel.getOtpRefNo(), mOtpResendLink);*/

                                alertMessage1(statusmsg, statusmsg);
                            }
                            else {
//                    QuickSuccess(mAccNo,"Oops....","Something went wrong",moneyTransferResponseModel.getStatusCode(),
//                            moneyTransfer
//                            ResponseModel.getOtpRefNo(),msenderName,msenderMobile,mreceiverAccountno,mrecievererName,mrecieverMobile,mbranch, mAmount);
                                //    Log.e(TAG,"10914   "+moneyTransferResponseModel.getMessage());
                                alertMessage1("Oops....!", "Something went wrong");
                            }

                    /*        JSONObject j1 = jObject.getJSONObject("FundTransferIntraBankList");
                            String responsemsg = j1.getString("ResponseMessage");
                            String statusmsg = j1.getString("StatusMessage");
                            int statusCode=j1.getInt("StatusCode");*/
                        /*    if(statusCode==1){
                                String refid;
                                JSONArray jArray3 = j1.getJSONArray("FundTransferIntraBankList");


                                alertMessage("", keyValuePairs, statusmsg, true, false);
                                //  JSONArray jarray = jobj.getJSONArray( "Data");

                            }
                            else if ( statusCode == 2 ){
                                alertMessage1("" ,statusmsg );
                            }
                            else if ( statusCode == 3 ){
                                alertMessage1("", statusmsg);
                            }
                            else if ( statusCode == 4 ){
                                alertMessage1("", statusmsg);
                            }
                            else  if ( statusCode == 5 ){
                                alertMessage1("", statusmsg);
                            }

                            else{


                                try{
                                  *//*  JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");*//*
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
                                    builder.setMessage(responsemsg)
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
                                    String EXMessage = j1.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
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
*/

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

    private void QuickSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater1 = this.getLayoutInflater();

        View layout = inflater1.inflate(R.layout.quick_pay_success_popup, null);
        TextView tvbranch =  layout.findViewById(R.id.tvbranch);
        RelativeLayout rltv_share = layout.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = layout.findViewById( R.id.lay_share );
        TextView tv_sender_name =  layout.findViewById(R.id.tv_sender_name);
        TextView tv_sender_acc_no =  layout.findViewById(R.id.tv_sender_acc_no);
        TextView tv_sender_mob_no =  layout.findViewById(R.id.tv_sender_mob_no);

        TextView tv_reciever_name =  layout.findViewById(R.id.tv_reciever_name);
        TextView tv_reciever_acc_no =  layout.findViewById(R.id.tv_reciever_acc_no);
        TextView tv_reciever_mob_no =  layout.findViewById(R.id.tv_reciever_mob_no);
        ImageView img_aapicon1=  layout.findViewById(R.id.img_aapicon);
        TextView tv_msg =  layout.findViewById(R.id.txtv_msg);
        // TextView tv_msg1 =  layout.findViewById(R.id.txtv_msg1);
        // TextView tv_status =  layout.findViewById(R.id.txtv_status);
        // CardView crdSuccess =  layout.findViewById(R.id.crdSuccess);

        TextView tv_amount =  layout.findViewById(R.id.tv_amount);
        TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
        //  TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
        TextView tvdate =  layout.findViewById(R.id.tvdate);
        TextView tvtime =  layout.findViewById(R.id.tvtime);
        //TextView bt_ok1 =  layout.findViewById(R.id.bt_ok1);
        TextView txtv_typeamt =  layout.findViewById(R.id.txtv_typeamt);
        TextView txtTo =  layout.findViewById(R.id.txtTo);
        TextView txtpfrom =  layout.findViewById(R.id.txtpfrom);


        //  crdSuccess.setVisibility(View.VISIBLE);

        tv_msg.setText("status");
        tv_sender_name.setText("msenderName");
        tv_sender_mob_no.setText("msenderMobile");
        tv_sender_acc_no.setText("mAccNo");
        // tvbranch.setText(mbranch);
        txtv_typeamt.setText("Paid Amount");
        txtpfrom.setText("Sender Details");
        txtTo.setText("Reciever Details");

        tv_reciever_name.setText("mrecievererName");
        tv_reciever_acc_no.setText("mreceiverAccountno");
        tv_reciever_mob_no.setText("mrecieverMobile");

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        double num =Double.parseDouble(""+"mAmount");
        String stramnt = CommonUtilities.getDecimelFormate(num);
        //  text_confirmationmsg.setText(message);
        // text_confirmationmsg.setVisibility(View.VISIBLE);
        String[] netAmountArr = "mAmount".split("\\.");
        String amountInWordPop = "";
        if ( netAmountArr.length > 0 ){
            int integerValue = Integer.parseInt( netAmountArr[0] );
            amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
            if ( netAmountArr.length > 1 ){
                int decimalValue = Integer.parseInt( netAmountArr[1] );
                if ( decimalValue != 0 ){
                    amountInWordPop += " And " + NumberToWord.convertNumberToWords( decimalValue ) + " Paise" ;
                }
            }
            amountInWordPop += " Only";
        }
        tv_amount_words.setText(""+amountInWordPop);
        tv_amount.setText("₹ " + stramnt );



        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();


        layout.findViewById( R.id.rltv_footer ).setOnClickListener( view1 -> {
            try{
//                getFragmentManager().beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY") )
//                        .commit();
                Intent i=new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );
        lay_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("img_share","img_share   1170   ");
                Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                        rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                rltv_share.draw(canvas);

                try {


                    Uri bmpUri = getLocalBitmapUri(bitmap);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception","Exception   117   "+e.toString());
                }

            }
        });


    /*    bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });*/
        alertDialog.show();
    }

    private boolean isValid() {
        String otp = mOTPEt.getText().toString();

        if (TextUtils.isEmpty(otp)) {
            mOTPEt.setError("Please enter the OTP");

            return false;
        }

        mOTPEt.setError(null);

        return true;
    }

    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TraansactionOTPActivity.this);

        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
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

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        Log.e("File  ","File   142   "+file);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
