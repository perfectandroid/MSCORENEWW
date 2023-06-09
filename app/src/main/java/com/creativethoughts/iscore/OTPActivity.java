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
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.model.FundTransferResult1;
import com.creativethoughts.iscore.neftrtgs.NeftRtgsOtpResponseModel;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressDialog progressDialog;
    private static final String BUNDLE_PAYMENT_MODEL = "payment_model";
    private ArrayList<NeftRtgsOtpResponseModel> neftRtgsOtpResponseModels = new ArrayList<NeftRtgsOtpResponseModel>();
    private static final String BUNDLE_REQUEST_NEFT_RTGS_OTP_RESPONSE_MODEL = "neft_rtgs_request_otp_request_response_model";
    String from="OTP";
    private RecyclerView mRecyclerView;
    private static final int STATUS_RESEND_OTP = 100;
    private static final int STATUS_VERIFY_OTP = 200;
    int mode;
    String otp;
    private NeftRtgsOtpResponseModel mNeftRtgsOtpResponseModel;
    private EditText mEdtotp;
    private PaymentModel mPaymentModel;
    private SweetAlertDialog mSweetAlertDialog;
    String paymntmodel,branch,bal,time,transdate;
    private int maxFailedAttempt = 3;
    private TextView txtFailedAttempt;
    Button btnResend,btnSubmt;
    int statusCode;
    PaymentModel paymentModel = new PaymentModel();
    String accntno,module,bennme,benifsc,benacc,pin,amt,otpref,otpref1,benadd,statsmsg1,refid1,amt1,exmsg1,accno,benaccno,brnch;
    NeftRtgsOtpResponseModel neftRtgsOtpResponseModel1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_otp);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setRegViews();
    }

    private void setRegViews() {

        mEdtotp = findViewById( R.id.edt_txt_otp );

        btnResend = findViewById( R.id.btn_resend );
        btnResend.setOnClickListener( this );

        btnSubmt=findViewById( R.id.btn_submit);
        btnSubmt.setOnClickListener( this);

        txtFailedAttempt = findViewById( R.id.txt_failed_attempt );

        accntno = getIntent().getStringExtra("AccountNo");
        module = getIntent().getStringExtra("Module");
        bennme = getIntent().getStringExtra("BeneName");
        benadd = getIntent().getStringExtra("Benadd");
        benifsc = getIntent().getStringExtra("BeneIFSC");
        benacc = getIntent().getStringExtra("BeneAccountNumber");
        pin = getIntent().getStringExtra("Pin");
        amt = getIntent().getStringExtra("Amount");
        mode = getIntent().getIntExtra("EftType",0);
        otpref = getIntent().getStringExtra("otpref");
        branch = getIntent().getStringExtra("Branch");
        bal = getIntent().getStringExtra("Balance");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.btn_resend:
                otp = mEdtotp.getText().toString();
                 getResendOTP(otp);
                break;
            case R.id.btn_submit:
                 otp = mEdtotp.getText().toString();
                if ( otp.length() < 6 && !(otp.length()==0)){
                    Toast.makeText( getApplicationContext(), "Please enter atleast 6 digits", Toast.LENGTH_LONG ).show();

                }
               else if ( otp.length()==0){
                    Toast.makeText( getApplicationContext(), "Please enter OTP that you have received", Toast.LENGTH_LONG ).show();

                }
              /*  else if ( !otp.equals(otpref) ){
                    Toast.makeText( getApplicationContext(), "Please enter OTP that you have received", Toast.LENGTH_LONG ).show();

                }*/
                else
                {
                    getVerifyOTP(otp);
                }

                break;
        }
    }

    private void getVerifyOTP(String otp) {


        SharedPreferences pref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);


        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(OTPActivity.this, R.style.Progress);
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
                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart(accntno));
                    requestObject1.put("Module", IScoreApplication.encryptStart(module) );
                    requestObject1.put("BeneName", IScoreApplication.encryptStart(bennme));
                    requestObject1.put("BeneIFSC", IScoreApplication.encryptStart(benifsc));
                    requestObject1.put("BeneAccountNumber", IScoreApplication.encryptStart(benacc));

                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                    String benefadd =paymentModel.getBeneficiaryAdd( );
                    requestObject1.put("amount", IScoreApplication.encryptStart(amt));
                    requestObject1.put("EftType", IScoreApplication.encryptStart(Integer.toString(mode)));
                    requestObject1.put("BeneAdd", IScoreApplication.encryptStart(benadd));
                    requestObject1.put("Pin", IScoreApplication.encryptStart( paymentModel.getPin( )  ));
                    requestObject1.put("OTPRef", IScoreApplication.encryptStart(otpref));
                    requestObject1.put("OTPCode", IScoreApplication.encryptStart(otp));

                     requestObject1.put("imei",IScoreApplication.encryptStart(""));

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(""));

                    Log.e("requestObject1otpverify",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getNeftPaymnt(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e("TAG","Response neftotp   "+response.body());

                            JSONObject jObject = new JSONObject(response.body());
                             statusCode=jObject.getInt("HttpStatusCode");
                            otpref1=jObject.getString("StatusCode");
                             statsmsg1=jObject.getString("Message");
                             refid1=jObject.getString("RefID");
                             amt1=jObject.getString("Amount");
                             exmsg1=jObject.getString("ExMessge");
                            accno=jObject.getString("AccNo");
                            benaccno=jObject.getString("BenAccNo");
                            brnch=jObject.getString("Branch");
                            time=jObject.getString("Time");
                            transdate=jObject.getString("TransDate");

                           if(statusCode==200 && otpref1.equals("200"))
                            {


                                NeftRtgsOtpResponseModel neftRtgsOtpResponseModel = new NeftRtgsOtpResponseModel();


                                neftRtgsOtpResponseModel.statusCode =statusCode;
                                neftRtgsOtpResponseModel.otpRefNo =otpref1;
                                neftRtgsOtpResponseModel.message =statsmsg1;
                                neftRtgsOtpResponseModel.refId =refid1;
                                neftRtgsOtpResponseModel.amount =amt1;
                                neftRtgsOtpResponseModel.exMessage =exmsg1;
                                neftRtgsOtpResponseModel.accno =accno;
                                neftRtgsOtpResponseModel.benaccno =benaccno;
                                neftRtgsOtpResponseModel.branch =brnch;
                                neftRtgsOtpResponseModel.transdate =transdate;
                                neftRtgsOtpResponseModel.time =time;
                                neftRtgsOtpResponseModels.add(neftRtgsOtpResponseModel);
                               /* ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                                KeyValuePair keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Amount");
                                String rupee = getString( R.string.rupee );
                                keyValuePair.setValue( rupee + "."+amt );
                                keyValuePairs.add( keyValuePair );
                                keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Ref.Id");
                                //keyValuePair.setValue( "Refid"  );
                                keyValuePairs.add( keyValuePair );*/
                                 neftRtgsOtpResponseModel1= new NeftRtgsOtpResponseModel();
                                alertMessage("", neftRtgsOtpResponseModels, statsmsg1, true, false);
                            }

                           else if(statusCode<0)
                           {
                               String msg="";
                               alertMessage1(msg,  statsmsg1);
                           }
                           else if(statusCode==500)
                           {
                               String msg="";
                               alertMessage1(msg,  statsmsg1);
                           }


                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            alertMessage1("",  statsmsg1);
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                        progressDialog.dismiss();
                        alertMessage1("",  statsmsg1);
                    }
                });
            }
            catch (Exception e) {
                progressDialog.dismiss();
//                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }


    }

    private void alertMessage(String title, ArrayList<NeftRtgsOtpResponseModel> neftRtgsOtpResponseModell, String statsmsg, boolean b, boolean b1) {
        alertPopup(title,neftRtgsOtpResponseModell,statsmsg,b,b1);

    }

    private void alertPopup(String title, ArrayList<NeftRtgsOtpResponseModel> keyValueList, String message, boolean isHappy, boolean isBackButtonEnabled) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_message_alert_new, null);
        dialogBuilder.setView(dialogView);

        RelativeLayout rltv_share = dialogView.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = dialogView.findViewById( R.id.lay_share );
        mRecyclerView = dialogView.findViewById( R.id.recycler_message );
        ImageView imgIcon      = dialogView.findViewById( R.id.img_success );
        ImageView img_share      = dialogView.findViewById( R.id.img_share );
        TextView txtTitle       = dialogView.findViewById( R.id.txt_success );
        TextView txtMessage = dialogView.findViewById( R.id.txt_message );


        TextView tvdate = dialogView.findViewById( R.id.tvdate );
        TextView tvtime = dialogView.findViewById( R.id.tvtime );
        TextView tv_amount_words = dialogView.findViewById( R.id.tv_amount_words );

        TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
        TextView txtvAcntno = dialogView.findViewById(R.id.txtvAcntno);
        TextView txtvbranch = dialogView.findViewById(R.id.txtvbranch);
        TextView txtvbalnce = dialogView.findViewById(R.id.txtvbalnce);

        TextView txtvAcntnoto = dialogView.findViewById(R.id.txtvAcntnoto);
        /*TextView txtvbranchto = dialogView.findViewById(R.id.txtvbranchto);
        TextView txtvbalnceto = dialogView.findViewById(R.id.txtvbalnceto);*/
        ImageView img_hdAccount = dialogView.findViewById( R.id.img_hdAccount);
        ImageView img_applogo = dialogView.findViewById(R.id.img_aapicon);
        ImageView img_hdAccount1 = dialogView.findViewById( R.id.img_hdAccountto);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(OTPActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

        String refid=neftRtgsOtpResponseModels.get(0).getRefId();
        String msg=neftRtgsOtpResponseModels.get(0).getMessage();
        String amm=neftRtgsOtpResponseModels.get(0).getAmount();



        txtTitle.setText("Ref.No "+refid);
        txtMessage.setText(msg);

        //current time
       // String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String currentTime =neftRtgsOtpResponseModels.get(0).getTime();
        tvtime.setText("Time : "+currentTime);

        //current date

       /* Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);*/

        String formattedDate = neftRtgsOtpResponseModels.get(0).getTransdate();
        tvdate.setText("Date : "+formattedDate);

        String amnt = amm.replaceAll(",", "");
        String[] netAmountArr = amnt.split("\\.");
        String amountInWordPop = "";
        if ( netAmountArr.length > 0 ){
            int integerValue = Integer.parseInt( netAmountArr[0] );
            amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
            if ( netAmountArr.length > 1 ){
                int decimalValue = Integer.parseInt( netAmountArr[1] );
                if ( decimalValue != 0 ){
                    amountInWordPop += " and " + NumberToWord.convertNumberToWords( decimalValue ) + " paise" ;
                }
            }
            amountInWordPop += " only";
        }
        tv_amount_words.setText(""+amountInWordPop);

        double num =Double.parseDouble(""+amnt);
        Log.e("TAG","CommonUtilities  945   "+ CommonUtilities.getDecimelFormate(num));
        String stramnt = CommonUtilities.getDecimelFormate(num);


        tv_amount.setText("₹ " + stramnt );



    //    txtvAcntno.setText("A/C :"+accntno);
      //  txtvAcntno.setText("A/C :"+neftRtgsOtpResponseModels.get(0).getAccno());

        String tempStringAccountNo =neftRtgsOtpResponseModels.get(0).getAccno();
        String maskAccountNumber =tempStringAccountNo.replaceAll("\\w(?=\\w{4})", "*");
        txtvAcntno.setText(maskAccountNumber);

     //  txtvbranch.setText("Branch :"+branch);
        txtvbranch.setText("Branch :"+neftRtgsOtpResponseModels.get(0).getBranch());
        double num1 = Double.parseDouble(bal) - Double.parseDouble(stramnt.replace(",",""));
        DecimalFormat fmt = new DecimalFormat("#,##,###.00");




    //    txtvAcntnoto.setText("A/C : "+ benacc);
       // txtvAcntnoto.setText("A/C : "+ neftRtgsOtpResponseModels.get(0).getBenaccno());
        String tempStringAccountNo1 =neftRtgsOtpResponseModels.get(0).getBenaccno();
        String maskAccountNumber1 =tempStringAccountNo1.replaceAll("\\w(?=\\w{4})", "*");
        txtvAcntnoto.setText(maskAccountNumber1);

        dialogView.findViewById( R.id.rltv_footer ).setOnClickListener(view1 -> {
            try{

                Intent i=new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );
        img_hdAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtvAcntno.getText().toString() == tempStringAccountNo){
                    txtvAcntno.setText(maskAccountNumber);
                    img_hdAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.visibility_eye));
                }
                else{
                    txtvAcntno.setText(tempStringAccountNo);
                    img_hdAccount.setBackgroundDrawable(getResources().getDrawable(R.drawable.visibility_off_eye));

                }
            }
        });
        img_hdAccount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtvAcntnoto.getText().toString() == tempStringAccountNo1){
                    txtvAcntnoto.setText(maskAccountNumber1);
                    img_hdAccount1.setBackgroundDrawable(getResources().getDrawable(R.drawable.visibility_eye));
                }
                else{
                    txtvAcntnoto.setText(tempStringAccountNo1);
                    img_hdAccount1.setBackgroundDrawable(getResources().getDrawable(R.drawable.visibility_off_eye));

                }
            }
        });
        try{

            txtMessage.setText( message );
            txtTitle.setText( title );
            if ( !isHappy ){
                imgIcon.setImageResource( R.mipmap.ic_failed );
            }


            lay_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    img_hdAccount.setVisibility(View.INVISIBLE);
                    img_hdAccount1.setVisibility(View.INVISIBLE);

                    Log.e("img_share","img_share   1170   ");
                    Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                            rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    rltv_share.draw(canvas);

                    try {

                        File file = saveBitmap(bitmap, accntno+".png");
                        Log.e("chase  2044   ", "filepath: "+file.getAbsolutePath());
                        Uri bmpUri = Uri.fromFile(file);

                        // Uri bmpUri = getLocalBitmapUri(bitmap);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //   shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share"));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Exception","Exception   117   "+e.toString());
                    }

                    img_hdAccount.setVisibility(View.VISIBLE);
                    img_hdAccount1.setVisibility(View.VISIBLE);
                }
            });



        }catch ( Exception e){
            //Do nothing
        }

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void getResendOTP(String otp) {

        getVerifyOTP(otp);
    }

    private SSLSocketFactory getSSLSocketFactory()

            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {
        SharedPreferences sslnamepref = this.getSharedPreferences(Config.SHARED_PREF24, 0);
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
    private void changeBackground(EditText editText, boolean errorStatus ){
        if (errorStatus )
            editText.setBackgroundResource( R.drawable.custom_edt_txt_error_back_ground );
        else
            editText.setBackgroundResource( R.drawable.custom_edt_txt_account_border );
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


    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OTPActivity.this);

        LayoutInflater inflater = OTPActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(msg1);
        tv_msg2.setText(msg2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);


        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(OTPActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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

    private File saveBitmap(Bitmap bm, String fileName){

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download"+ "/");
        boolean isPresent = true;
        Log.e("photoURI","StatementDownloadViewActivity   5682   ");
        if (!docsFolder.exists()) {
            // isPresent = docsFolder.mkdir();
            docsFolder.mkdir();
            Log.e("photoURI","StatementDownloadViewActivity   5683   ");
        }

        File file = new File(docsFolder, fileName);
        if(file.exists()){

            file.delete();
        }
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
