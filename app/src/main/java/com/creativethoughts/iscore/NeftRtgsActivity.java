package com.creativethoughts.iscore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.google.android.material.snackbar.Snackbar;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

public class NeftRtgsActivity extends Activity  implements View.OnClickListener{
    private ProgressDialog progressDialog;
    private EditText mEdtTxtBeneficiaryName;
    private EditText  mEdtTxtBeneficiaryAccNo;
    private EditText  mEdtTxtBeneficiaryConfirmAccNo;
    private EditText mEdtTxtIfscNo;
    private TextView txtTrans,txt_header,txt_amtinword;
    private EditText  mEdtTxtAmount ;
    private Spinner mSpinnerAccountNo;
    private Button mBtnClear;
    private CheckBox mCheckSaveBeneficiary;
    private RelativeLayout mLinearParent;
    private ScrollView mScrollView;
    public static String result ="";
    public static String balnce ="";
    String amot;
    String from="NEFT";
    SharedPreferences customerIdSP;
    String reslts;
    String amt;
    private int mModeNeftRtgs;
    private PaymentModel mPaymentModel;
    private static final String BENEFICIARY_DETAILS = "beneficiary details";
    private static final String MODE = "MODE";
    public static ArrayList<ToAccountDetails> AccountDetails;
    static ArrayAdapter<ToAccountDetails> AccountAdapter = null;
    String mode,benef,name,ifsc,accno;
    public static String bal="";
    String type = "";
    PaymentModel paymentModel = new PaymentModel( );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_neft_rtgs);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mode = getIntent().getStringExtra("mode");

    //    benef = getIntent().getStringExtra("benefmodel");

        setRegViews();
        setAccountNumber();


        name = getIntent().getStringExtra("name");
        ifsc = getIntent().getStringExtra("ifsc");
        accno = getIntent().getStringExtra("accno");

        if(!name.equals("null") && !ifsc.equals("null") && !accno.equals("null"))
        {
            mEdtTxtBeneficiaryName.setText(name);
            mEdtTxtIfscNo.setText(ifsc);
            mEdtTxtBeneficiaryAccNo.setText(accno);
            mEdtTxtBeneficiaryConfirmAccNo.setText(accno);
        }
        if(name.equals("") && !ifsc.equals("") && !accno.equals(""))
        {
            mEdtTxtBeneficiaryName.setText("");
            mEdtTxtIfscNo.setText("");
            mEdtTxtBeneficiaryAccNo.setText("");
            mEdtTxtBeneficiaryConfirmAccNo.setText("");
        }
    }



    private void setRegViews() {

        mEdtTxtBeneficiaryName              =   findViewById( R.id.edt_txt_neft_rtgs_benificiary_name );
        mEdtTxtBeneficiaryAccNo             =   findViewById( R.id.edt_txt_neft_rtgs_benificiary_acc_no );
        mEdtTxtBeneficiaryConfirmAccNo      =   findViewById( R.id.edt_txt_neft_rtgs_confirm_benificiary_acc_no );
        mEdtTxtAmount                       =   findViewById( R.id.edt_txt_neft_rtgs_amount );
        mEdtTxtIfscNo                       =   findViewById( R.id.edt_txt_neft_rtgs_ifsc_code );
        mSpinnerAccountNo                   =   findViewById( R.id.spinner_neft_rtgs_acc_no );
        mLinearParent                       =   findViewById( R.id.lnear_impes_rtgs_parent );
        mScrollView                         =   findViewById( R.id.scroll_view_rtgs_neft );
        mBtnClear                           =   findViewById( R.id.btn_neft_rtgs_clear );
        txtTrans                           =   findViewById( R.id.txtTrans );
        txt_header                           =   findViewById( R.id.txt_header );
        txt_amtinword                         = findViewById(R.id.txt_amtinword);

        Button btnSubmit                    =   findViewById( R.id.btn_neft_rtgs_submit );
        TextView txtViewChooseBeneficiary   =  findViewById( R.id.txt_view__neft_rtgs_choose_benefeciary );
        TextView mTxtHeader = findViewById(R.id.txt_header);
        mCheckSaveBeneficiary               =   findViewById( R.id.chk_save_ben );
        mLinearParent.setOnClickListener( this );
        btnSubmit.setOnClickListener(this );
        mBtnClear.setOnClickListener(this );
        txtTrans.setOnClickListener(this );

        txtViewChooseBeneficiary.setOnClickListener(this );

        switch (mode) {
            case IScoreApplication.OTHER_FUND_TRANSFER_MODE_NEFT:
                mModeNeftRtgs = 2;
                break;
            case IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS:
                mModeNeftRtgs = 1;
                break;
            case IScoreApplication.OTHER_FUND_TRANSFER_MODE_IMPS:
                mModeNeftRtgs = 3;
                break;
            default:
                mModeNeftRtgs = 0;
                break;
        }
        mTxtHeader.setText( mode );

        mEdtTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mEdtTxtAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();


                    Double longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Double.parseDouble(originalString);
                    String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    mEdtTxtAmount.setText(formattedString);
                    mEdtTxtAmount.setSelection(mEdtTxtAmount.getText().length());


                    String amnt = mEdtTxtAmount.getText().toString().replaceAll(",", "");
                    String[] netAmountArr = amnt.split("\\.");
                    String amountInWordPop = "";
                    if ( netAmountArr.length > 0 ){
                        try {
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
                        catch (Exception e)
                        {

                        }

                    }
                    txt_amtinword.setText(""+amountInWordPop);


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                mEdtTxtAmount.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length()!= 0) {

                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        btnSubmit.setText( "PAY  "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num));
                        //  txt_amtinword.setText(CommonUtilities.getDecimelFormate(num));




                    }
                    else{
                        btnSubmit.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }

            }
        });

    }
    private void setAccountNumber() {

        if (NetworkUtil.isOnline()) {
            try {

                SharedPreferences pref = this.getSharedPreferences(Config.SHARED_PREF7, 0);
                String BASE_URL=pref.getString("baseurl", null);

                Log.e("TAG","BASE_URL   283   "+BASE_URL);

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

                    customerIdSP = this.getSharedPreferences(Config.SHARED_PREF26, 0);
                    SharedPreferences tokenIdSP = this.getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token = tokenIdSP.getString("Token","");
                    String cusid = customerIdSP.getString("customerId","");
                    SharedPreferences bankkeypref =this.getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =this.getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("ReqMode", IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode", IScoreApplication.encryptStart("1"));
//                    requestObject1.put("BankKey",IScoreApplication.encryptStart(Resources.getSystem().getString(R.string.BankKey)));
//                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(Resources.getSystem().getString(R.string.BankHeader)));
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

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

                                        AccountAdapter = new ArrayAdapter<>(NeftRtgsActivity.this,  R.layout.simple_spinner_item_dark, R.id.textview, AccountDetails);
                                        //                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                        mSpinnerAccountNo.setAdapter(AccountAdapter);



                                        for (int i = 0; i < AccountDetails.size(); i++) {
                                            //   for (int i = 0; i < accountSpinnerItems.size(); i++) {
                                            String account = accountSpinnerItems.get(i);



                                            if (TextUtils.isEmpty(account)) {
                                                continue;
                                            }



                                            String customerId = customerIdSP.getString("customerId","");
                                            // if (account.equalsIgnoreCase(settingsModel.customerId)) {
                                            if (account.equalsIgnoreCase(customerId)) {
                                                mSpinnerAccountNo.setSelection(i);


                                                break;
                                            }

                                            mSpinnerAccountNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                e.printStackTrace();

                Log.e("Exception ","Exception  302   "+e.toString());

            }
        }
        else {

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_neft_rtgs_clear:
                clearAll();
                break;
            case R.id.txtTrans:
                String module = txt_header.getText().toString();
                if(module.equals("IMPS"))
                {
                    Intent im = new Intent(this, OtherfundTransferHistoryIMPS.class);
                    startActivity(im);
                }
                else if(module.equals("NEFT"))
                {
                    Intent im = new Intent(this, OtherfundTransferHistoryNEFT.class);
                    startActivity(im);
                }
                if(module.equals("RTGS"))
                {
                    Intent im = new Intent(this, OtherfundTransferHistoryRTGS.class);
                    //   im.putExtra("submode","3");
                    startActivity(im);
                }
                break;
            case R.id.btn_neft_rtgs_submit:

                if (isValid() ) {
                    confirmationPopup();
                }


                break;


            case R.id.txt_view__neft_rtgs_choose_benefeciary:
                break;
        }
    }

    private boolean isValid() {
        changeBackground(mEdtTxtBeneficiaryName, false );
        changeBackground(mEdtTxtBeneficiaryAccNo, false );
        changeBackground(mEdtTxtBeneficiaryConfirmAccNo, false );
        changeBackground(mEdtTxtIfscNo,false );

        if (mEdtTxtBeneficiaryName.getText( ).toString( ).isEmpty( ) ){
            showSnackBar("Please enter Beneficiary name" );
            changeBackground(mEdtTxtBeneficiaryName, true );
            focusScrollView(mEdtTxtBeneficiaryName );
            return false;
        }
        String tempBeneficiaryAccNo = mEdtTxtBeneficiaryAccNo.getText( ).toString( );
        String tempBeneficiaryConfirmAccNo = mEdtTxtBeneficiaryConfirmAccNo.getText( ).toString( );

        if (tempBeneficiaryAccNo.isEmpty( )    ){
            showSnackBar("Beneficiary account number is required" );
            changeBackground(mEdtTxtBeneficiaryAccNo, true );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryAccNo );
            return false;
        }
        if ( tempBeneficiaryConfirmAccNo.isEmpty( )   ){
            showSnackBar("Confirm Beneficiary account number is required" );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryConfirmAccNo );
            return false;
        }

        if (! tempBeneficiaryAccNo.matches("^\\d+$" ) ){
            showSnackBar("Invalid beneficiary account numbers " );
            changeBackground(mEdtTxtBeneficiaryAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryAccNo );
            return false;
        }
        if ( ( !tempBeneficiaryAccNo.equals(tempBeneficiaryConfirmAccNo )  )  ){
            showSnackBar("Beneficiary account numbers don't match" );
            changeBackground(mEdtTxtBeneficiaryConfirmAccNo, true );
            focusScrollView(mEdtTxtBeneficiaryConfirmAccNo );
            return false;
        }
        try {
            String ifscNumber = mEdtTxtIfscNo.getText( ).toString( );
            if (ifscNumber.isEmpty( ) || ifscNumber.length() != 11){
                showSnackBar("Please enter valid IFSC" );
                changeBackground(mEdtTxtIfscNo, true );
                focusScrollView(mEdtTxtIfscNo );
                return false;
            }


        }catch (Exception e ){

            if (IScoreApplication.DEBUG ) Log.d("ifscParseExc", e.toString( ) );
            focusScrollView(mEdtTxtIfscNo );
            return false;

        }
        try {
            String amount = mEdtTxtAmount.getText( ).toString( );
            amount = amount.replace(",","");
            int tempAmount = Integer.parseInt(amount );
            if (tempAmount < 0  ){
                showSnackBar("please enter amount" );
//                showSnackBar("Invalid amount " );
                changeBackground(mEdtTxtAmount, true );
                focusScrollView(mEdtTxtAmount );
                return false;
            }
        }catch (Exception e ){
            showSnackBar("Invalid amount " );
            changeBackground(mEdtTxtAmount, true );
            focusScrollView(mEdtTxtAmount );
            if (IScoreApplication.DEBUG ) Log.d("AmountExc", e.toString( ) );
            return false;

        }

        return true;

    }

    private void clearAll() {

        mEdtTxtBeneficiaryName.setText(null );
        mEdtTxtBeneficiaryAccNo.setText(null );
        mEdtTxtBeneficiaryConfirmAccNo.setText(null );
        mEdtTxtAmount.setText(null );
        mEdtTxtIfscNo.setText(null );
        mBtnClear.setVisibility( View.GONE );
        if ( mCheckSaveBeneficiary.isChecked() )
            mCheckSaveBeneficiary.toggle();

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
    private void showSnackBar(String message ){
        Snackbar snackbar = Snackbar.make(mLinearParent, message, Snackbar.LENGTH_SHORT );
        View snackBarView = snackbar.getView( );
        snackBarView.setBackgroundResource( R.color.red_error_snack_bar );
        snackbar.show( );
    }
    private  SSLSocketFactory getSSLSocketFactory()

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
    private void focusScrollView(final View view ){
        mScrollView.post(( ) -> mScrollView.scrollTo(0, view.getBottom( ) ) );
    }
    private void confirmationPopup() {


        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.confirmation_msg_popup, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);


            String amnt = mEdtTxtAmount.getText().toString().replaceAll(",", "");
            TextView text_confirmationmsg = dialogView.findViewById(R.id.text_confirmationmsg);
            TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
            TextView txtvAcntno = dialogView.findViewById(R.id.txtvAcntno);
            TextView txtvbranch = dialogView.findViewById(R.id.txtvbranch);
            TextView txtvbalnce = dialogView.findViewById(R.id.txtvbalnce);
            ImageView img_applogo = dialogView.findViewById(R.id.img_aapicon);

            TextView txtvAcntnoto = dialogView.findViewById(R.id.txtvAcntnoto);
            TextView txtvbranchto = dialogView.findViewById(R.id.txtvbranchto);
            TextView txtvbalnceto = dialogView.findViewById(R.id.txtvbalnceto);

            SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
            String IMAGEURL = imageurlSP.getString("imageurl","");
            SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
            String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
            PicassoTrustAll.getInstance(NeftRtgsActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);



            txtvAcntno.setText("A/C No : "+mSpinnerAccountNo.getSelectedItem().toString());
            String result1 = result;
            balnce = bal;
            txtvbranch.setText("Branch :"+result1);
            // txtvbranch.setVisibility(View.GONE);
            double num1 =Double.parseDouble(balnce);
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");

            txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));

            TextView tv_amount_words = dialogView.findViewById(R.id.tv_amount_words);
            Button butOk = dialogView.findViewById(R.id.btnOK);
            Button butCan = dialogView.findViewById(R.id.btnCncl);

            txtvAcntnoto.setText("A/C No: "+ mEdtTxtBeneficiaryConfirmAccNo.getText().toString());
            txtvbranchto.setText("Branch :"+"");
            txtvbranchto.setVisibility(View.GONE);
            // txtvbalnceto.setText("Transfer Amount: ");



            if(amnt!=null)
            {
                double num =Double.parseDouble(""+amnt);
                String stramnt = CommonUtilities.getDecimelFormate(num);

                text_confirmationmsg.setText("Proceed Transaction with above receipt amount"+ "..?");

                // text_confirmationmsg.setText("Proceed Transaction with above receipt amount to A/C no " + accNumber + " ..?");
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


                tv_amount.setText("₹ " + stramnt );
            }



            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            butOk.setOnClickListener(v -> {

                //   btn_submit.setEnabled(false);
                alertDialog.dismiss();
                // submit(alertDialog);
                submit();
            });

            butCan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }catch (Exception e){
            Log.e("Exception  446    ",""+e.toString());
        }



    }
    private void  submit( ){
        if (!NetworkUtil.isOnline( ) ){
            alertMessage1("", " Network is currently unavailable. Please try again later.");

           /* DialogUtil.showAlert(getActivity( ),
                    "Network is currently unavailable. Please try again later." );*/
            return;
        }

        if (isValid( ) ){
            String tempAccNo = mSpinnerAccountNo.getSelectedItem( ).toString( );
            tempAccNo = tempAccNo.replace(tempAccNo.substring(tempAccNo.indexOf(" (" )+1, tempAccNo.indexOf( ')' )+1 ), "" );
            tempAccNo = tempAccNo.replace(" ","" );

            String accno = mSpinnerAccountNo.getSelectedItem( ).toString();
            if (accno.contains("SB")) {
                type = "SB";
            } else if (accno.contains("CA")) {
                type= "CA";
            }
            else if (accno.contains("OD")) {
                type= "OD";
            }
            else if (accno.contains("ML")) {
                type= "ML";
            }
            else if (accno.contains("RD")) {
                type= "RD";
            }
            else if (accno.contains("JL")) {
                type= "JL";
            }
            else if (accno.contains("GD")) {
                type= "GD";
            }





            final String accNo = tempAccNo;

            final String beneficiaryName = mEdtTxtBeneficiaryName.getText( ).toString( );
            final String ifscNumber = mEdtTxtIfscNo.getText( ).toString( );
            final String amount = mEdtTxtAmount.getText( ).toString( );
            final String beneficiaryAccNo = mEdtTxtBeneficiaryAccNo.getText( ).toString( );

            String result1 =result;

            SharedPreferences cusidpref = NeftRtgsActivity.this.getSharedPreferences(Config.SHARED_PREF36, 0);
            String pin=cusidpref.getString("pinlog", null);


            paymentModel.setAccNo( accNo  );
            paymentModel.setBeneficiaryAccNo( beneficiaryAccNo  );
            paymentModel.setBeneficiaryName( beneficiaryName  );
            paymentModel.setModule( type);
            paymentModel.setIfsc(ifscNumber );
            paymentModel.setAmount( amount  );
            paymentModel.setBranch( result1  );
            paymentModel.setBalance( balnce  );
            paymentModel.setMode(Integer.toString(mModeNeftRtgs ) );
            paymentModel.setPin(pin);
            if ( mCheckSaveBeneficiary.isChecked() ){
                paymentModel.setBeneficiaryAdd("1" );
            }else
                paymentModel.setBeneficiaryAdd("0" );

            startPayment( paymentModel );
        }
    }

    private void startPayment(final PaymentModel paymentModel) {

        SharedPreferences pref =this.getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);


        if (NetworkUtil.isOnline()) {
            try{

                progressDialog = new ProgressDialog(NeftRtgsActivity.this, R.style.Progress);
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
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart(paymentModel.getAccNo()));
                    requestObject1.put("Module", IScoreApplication.encryptStart(paymentModel.getModule()) );
                    requestObject1.put("BeneName", IScoreApplication.encryptStart(paymentModel.getBeneficiaryName( )));
                    requestObject1.put("BeneIFSC", IScoreApplication.encryptStart(paymentModel.getIfsc( )));
                    requestObject1.put("BeneAccountNumber", IScoreApplication.encryptStart(paymentModel.getBeneficiaryAccNo( )));

                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");
                      amt=paymentModel.getAmount();
                     amot = amt.replace(",","");
                    requestObject1.put("amount", IScoreApplication.encryptStart(amot));
                    requestObject1.put("EftType", IScoreApplication.encryptStart(Integer.toString( mModeNeftRtgs)));
                    String benefadd =paymentModel.getBeneficiaryAdd();
                    requestObject1.put("BeneAdd", IScoreApplication.encryptStart(paymentModel.getBeneficiaryAdd( ) ));
                    requestObject1.put("Pin", IScoreApplication.encryptStart( paymentModel.getPin( )  ));
                    requestObject1.put("OTPRef", IScoreApplication.encryptStart(""));
                    requestObject1.put("OTPCode", IScoreApplication.encryptStart(""));

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
                    requestObject1.put("BankVerified",IScoreApplication.encryptStart(""));

                    Log.e("requestObject1   344   ",""+requestObject1);

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
                            Log.e("TAG","Response neft   "+response.body());

                            JSONObject jObject = new JSONObject(response.body());
                            int statusCode=jObject.getInt("HttpStatusCode");
                            String statsmsg=jObject.getString("Message");
                            String otpref=jObject.getString("StatusCode");

                            if(statusCode==1 && !otpref.isEmpty())
                            {
                                Log.i("Values",paymentModel.getAccNo( )+"\n"+paymentModel.getModule( )
                                +type+"\n"+paymentModel.getIfsc()+"\n"+paymentModel.getAccNo()+"\n"+paymentModel.getPin()+amot+"\n"+mModeNeftRtgs);
                                Intent i = new Intent(NeftRtgsActivity.this,OTPActivity.class);
                                i.putExtra("AccountNo",paymentModel.getAccNo( ));
                                i.putExtra("Branch",paymentModel.getBranch( ));
                                i.putExtra("Balance",paymentModel.getBal( ));

                                i.putExtra("Module",paymentModel.getModule( ));
                                i.putExtra("BeneName",paymentModel.getBeneficiaryName());
                                i.putExtra("BeneIFSC",paymentModel.getIfsc());
                                i.putExtra("BeneAccountNumber",paymentModel.getBeneficiaryAccNo());
                                i.putExtra("Pin",paymentModel.getPin());
                                i.putExtra("Amount",amot);
                                i.putExtra("Benadd",paymentModel.getBeneficiaryAdd());
                                i.putExtra("EftType",mModeNeftRtgs);
                                i.putExtra("otpref",otpref);


                                startActivity(i);
                            }
                            else if ( statusCode< 0  ){

                                alertMessage1( "",  statsmsg);


                            }else if (statusCode== 3  ){
                                alertMessage1( "",  statsmsg);
                            }else{


                                alertMessage1( "", statsmsg);
                            }
               /*             JSONObject j1 = jObject.getJSONObject("FundTransferIntraBankList");
                            String responsemsg = j1.getString("ResponseMessage");
                            String statusmsg = j1.getString("StatusMessage");
                            int statusCode=j1.getInt("StatusCode");
                            if(statusCode==1){
                                String refid;
                                JSONArray jArray3 = j1.getJSONArray("FundTransferIntraBankList");
                                for(int i = 0; i < jArray3 .length(); i++) {
                                    JSONObject object3 = jArray3.getJSONObject(i);

                                    FundTransferResult1 fundTransferResult1 = new FundTransferResult1();


                                    fundTransferResult1.refId =object3.getString("RefID");
                                    fundTransferResult1.mobileNumber = object3.getString("MobileNumber");
                                    fundTransferResult1.amount=object3.getString("Amount");
                                    fundTransferResult1.accNo=object3.getString("AccNumber");
                                    fundtransfrlist.add(fundTransferResult1);

                                }

                                FundTransferResult1 fundTransferResult= new FundTransferResult1();


                                ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();

                                KeyValuePair keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Ref. No");
                                keyValuePair.setValue( fundTransferResult.getrefId() );
                                keyValuePairs.add( keyValuePair );

                                keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("Amount");
                                keyValuePair.setValue(fundTransferResult.getAmount());
                                keyValuePairs.add( keyValuePair );

                                keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("From Acc.No");
                                keyValuePair.setValue( tempFromAccNo );
                                keyValuePairs.add( keyValuePair );

                                keyValuePair = new KeyValuePair();
                                keyValuePair.setKey("To Acc.No");
                                keyValuePair.setValue( tempToAccNo );
                                keyValuePairs.add( keyValuePair );

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
                            }*/


                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
//                        progressDialog.dismiss();
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
    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = NeftRtgsActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);


        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(NeftRtgsActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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
