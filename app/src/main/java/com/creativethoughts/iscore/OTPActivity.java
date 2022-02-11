package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.neftrtgs.NeftRtgsOtpResponseModel;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private static final String BUNDLE_PAYMENT_MODEL = "payment_model";
    private static final String BUNDLE_REQUEST_NEFT_RTGS_OTP_RESPONSE_MODEL = "neft_rtgs_request_otp_request_response_model";
    String from="OTP";
    private RecyclerView mRecyclerView;
    private static final int STATUS_RESEND_OTP = 100;
    private static final int STATUS_VERIFY_OTP = 200;
    int mode;
    private NeftRtgsOtpResponseModel mNeftRtgsOtpResponseModel;
    private EditText mEdtotp;
    private PaymentModel mPaymentModel;
    private SweetAlertDialog mSweetAlertDialog;
    String paymntmodel;
    private int maxFailedAttempt = 3;
    private TextView txtFailedAttempt;
    Button btnResend,btnSubmt;
    PaymentModel paymentModel = new PaymentModel();
    String accntno,module,bennme,benifsc,benacc,pin,amt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_otp);

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
        benifsc = getIntent().getStringExtra("BeneIFSC");
        benacc = getIntent().getStringExtra("BeneAccountNumber");
        pin = getIntent().getStringExtra("Pin");
        amt = getIntent().getStringExtra("Amount");
        mode = getIntent().getIntExtra("EftType",0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_resend:
                 getResendOTP();
                break;
            case R.id.btn_submit:
                String otp = mEdtotp.getText().toString();
                if ( otp.length() < 6 ){
                    Toast.makeText( getApplicationContext(), "Please enter atleast 6 digits", Toast.LENGTH_LONG ).show();

                }
                if ( otp.isEmpty() ){
                    Toast.makeText( getApplicationContext(), "Please enter OTP that you have received", Toast.LENGTH_LONG ).show();

                }
                getVerifyOTP();
                break;
        }
    }

    private void getVerifyOTP() {


        SharedPreferences pref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
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
                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart(accntno));
                    requestObject1.put("Module", IScoreApplication.encryptStart(module) );
                    requestObject1.put("BeneName", IScoreApplication.encryptStart(bennme));
                    requestObject1.put("BeneIFSC", IScoreApplication.encryptStart(benifsc));
                    requestObject1.put("BeneAccountNumber", IScoreApplication.encryptStart(benacc));

                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");

                    requestObject1.put("amount", IScoreApplication.encryptStart(amt));
                    requestObject1.put("EftType", IScoreApplication.encryptStart(Integer.toString(mode)));
                    requestObject1.put("BeneAdd", IScoreApplication.encryptStart(paymentModel.getBeneficiaryAdd( ) ));
                    requestObject1.put("Pin", IScoreApplication.encryptStart( paymentModel.getPin( )  ));
                    requestObject1.put("OTPRef", IScoreApplication.encryptStart(""));
                    requestObject1.put("OTPCode", IScoreApplication.encryptStart(""));

                    // requestObject1.put("IMEI",IScoreApplication.encryptStart(ToFK_Account));

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1   344   ",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getNeftPaymnt(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response neftotp   "+response.body());

                            JSONObject jObject = new JSONObject(response.body());
                            int statusCode=jObject.getInt("StatusCode");
                            String statsmsg=jObject.getString("Message");

                           if(statusCode==200 && statsmsg.equals("200"))
                            {



                                ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                                KeyValuePair keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Amount");
                                String rupee = getString( R.string.rupee );
                                keyValuePair.setValue( rupee + "."+amt );
                                keyValuePairs.add( keyValuePair );
                                keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Ref.Id");
                                //keyValuePair.setValue( "Refid"  );
                                keyValuePairs.add( keyValuePair );
                                alertMessage("", keyValuePairs, statsmsg, true, false);
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

    private void alertMessage(String title, ArrayList<KeyValuePair> keyValuePairs, String statsmsg, boolean b, boolean b1) {
        alertPopup(title,keyValuePairs,statsmsg,b,b1);

    }

    private void alertPopup(String title, ArrayList<KeyValuePair> keyValuePairs, String statsmsg, boolean b, boolean b1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_message_alert_new, null);
        dialogBuilder.setView(dialogView);


        RelativeLayout rltv_share = findViewById( R.id.rltv_share );
        RelativeLayout lay_share = findViewById( R.id.lay_share );
        mRecyclerView = findViewById( R.id.recycler_message );
        ImageView imgIcon      = findViewById( R.id.img_success );
        ImageView img_share      = findViewById( R.id.img_share );
        TextView txtTitle       = findViewById( R.id.txt_success );
        TextView txtMessage = findViewById( R.id.txt_message );
        TextView txtpfrom = findViewById( R.id.txtpfrom );


        TextView tvdate = findViewById( R.id.tvdate );
        TextView tvtime = findViewById( R.id.tvtime );
        TextView tv_amount_words = findViewById( R.id.tv_amount_words );

        TextView tv_amount =findViewById(R.id.tv_amount);
        TextView txtvAcntno = findViewById(R.id.txtvAcntno);
        TextView txtvbranch = findViewById(R.id.txtvbranch);
        TextView txtvbalnce =findViewById(R.id.txtvbalnce);



        TextView txtvAcntnoto = findViewById(R.id.txtvAcntnoto);
        TextView txtvbranchto =findViewById(R.id.txtvbranchto);
        TextView txtvbalnceto =findViewById(R.id.txtvbalnceto);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        // txtvbranch.setText(mPaymentModel.getBranch());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        if(amt!=null) {

            String amnt = amt.replaceAll(",", "");
            String[] netAmountArr = amnt.split("\\.");
            String amountInWordPop = "";
            if (netAmountArr.length > 0) {
                int integerValue = Integer.parseInt(netAmountArr[0]);
                amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords(integerValue);
                if (netAmountArr.length > 1) {
                    int decimalValue = Integer.parseInt(netAmountArr[1]);
                    if (decimalValue != 0) {
                        amountInWordPop += " and " + NumberToWord.convertNumberToWords(decimalValue) + " paise";
                    }
                }
                amountInWordPop += " only";
            }
            tv_amount_words.setText("" + amountInWordPop);

            double num = Double.parseDouble("" + amnt);
            Log.e("TAG", "CommonUtilities  945   " + CommonUtilities.getDecimelFormate(num));
            String stramnt = CommonUtilities.getDecimelFormate(num);


            tv_amount.setText("₹ " + stramnt);

            double num1 = Double.parseDouble(mPaymentModel.getBal()) - Double.parseDouble(stramnt.replaceAll(",", ""));
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");

            // txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));
            txtvbalnce.setVisibility(View.GONE);
        }
        else {

            double num1 = Double.parseDouble(mPaymentModel.getBal());
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");

            txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));
            txtvbalnce.setVisibility(View.GONE);

        }
        txtvAcntno.setText("A/C No : "+mPaymentModel.getAccNo());
        txtvbranch.setText("Branch : "+mPaymentModel.getBranch());
        txtvAcntnoto.setText("A/C No : "+mPaymentModel.getBeneficiaryAccNo());

    }

    private void getResendOTP() {
        getVerifyOTP();
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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = OTPActivity.this.getLayoutInflater();
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
}
