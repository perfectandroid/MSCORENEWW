package com.creativethoughts.iscore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.CustomListAdapter;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.model.BarcodeAgainstCustomerAccountList;
import com.creativethoughts.iscore.model.FundTransferResult1;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OtherAccountFundTransferActivity extends AppCompatActivity implements View.OnClickListener, EditText.OnEditorActionListener,
        View.OnFocusChangeListener, TextWatcher, AdapterView.OnItemSelectedListener{
    String TAG = "FundTransferFragment";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Button button,btn_clear;
    private Spinner mAccountSpinner;
    private Spinner mAccountTypeSpinner;
    private AppCompatEditText mAccountNumberEt;
    private AppCompatEditText mConfirmAccountNumberEt;
    private AppCompatEditText mAmountEt;
    private AppCompatEditText mMessageEt;
    private ProgressDialog mProgressDialog;
    private String mScannedValue;
    ProgressDialog progressDialog;
    String result;
    String statusmessage;
    private EditText edtTxtAccountNoFirstBlock;
    private EditText edtTxtAccountNoSecondBlock;
    private EditText edtTxtAccountNoThirdBlock;
    private ArrayList<FundTransferResult1> fundtransfrlist = new ArrayList<FundTransferResult1>();
    private EditText edtTxtConfirmAccountNoFirstBlock;
    private EditText edtTxtConfirmAccountNoSecondBlock;
    private EditText edtTxtConfirmAccountNoThirdBlock;

    private EditText et_othr_acc_details;
    String MaximumAmount = "0";
    private EditText edtTxtAmount;
    private EditText edt_txt_remark;
    private String token,cusid,dataItem;
    ListView list_view;
    TextView tv_popuptitle,tv_branch_name,tv_maxamount,tv_balance,tv_account_no,txt_amtinword;
    private ArrayList<BarcodeAgainstCustomerAccountList> CustomerList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private static final String KEY_VALUE = "keyvalue";
    private static final String TITLE = "title";
    private static final String HAPPY = "happy";
    private static final String MESSAGE = "message";
    String SourceAccountNumber,Balance,typeShort,SubModule,FK_Account,BranchName;
    String reference;
    String receiveraccno;

    JSONArray array;
    JSONObject obj1;
    String accno,typeshrt;
    List<String> accountSpinnerItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_acc_fund_transferfragment);


        SourceAccountNumber = getIntent().getStringExtra("AccountNumber");
        BranchName = getIntent().getStringExtra("BranchName");
        Balance = getIntent().getStringExtra("Balance");
        typeShort = getIntent().getStringExtra("typeShort");
        SubModule = getIntent().getStringExtra("SubModule");
        FK_Account = getIntent().getStringExtra("FK_Account");

        tv_account_no = findViewById(R.id.tv_account_no);
        tv_branch_name = findViewById(R.id.tv_branch_name);
        tv_balance = findViewById(R.id.tv_balance);
        tv_account_no.setText("" + SourceAccountNumber );
        tv_branch_name.setText("" + BranchName );


        double num =Double.parseDouble(Balance);
        DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//        tv_balance.setText("\u20B9 "+ fmt.format(num));
        tv_balance.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));




        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mAccountSpinner = findViewById(R.id.spn_account_num);
        mAccountTypeSpinner = findViewById(R.id.spn_account_type);
        tv_maxamount = findViewById(R.id.tv_maxamount);

        mAccountNumberEt = findViewById(R.id.account_number);
        mConfirmAccountNumberEt = findViewById(R.id.confirm_account_number);

        edtTxtAccountNoFirstBlock = findViewById(R.id.acc_no_block_one);
        edtTxtAccountNoSecondBlock = findViewById(R.id.acc_no_block_two);
        edtTxtAccountNoThirdBlock = findViewById(R.id.acc_no_block_three);

        edtTxtAccountNoFirstBlock.setOnEditorActionListener(this);
        edtTxtAccountNoSecondBlock.setOnEditorActionListener(this);
        edtTxtAccountNoThirdBlock.setOnEditorActionListener(this);

        edtTxtAccountNoFirstBlock.setOnFocusChangeListener(this);
        edtTxtAccountNoSecondBlock.setOnFocusChangeListener(this);
        edtTxtAccountNoThirdBlock.setOnFocusChangeListener(this);

        edtTxtAccountNoFirstBlock.addTextChangedListener(this);
        edtTxtAccountNoSecondBlock.addTextChangedListener(this);
        edtTxtAccountNoThirdBlock.addTextChangedListener(this);

        edtTxtConfirmAccountNoFirstBlock = findViewById(R.id.confirm_acc_no_block_one);
        edtTxtConfirmAccountNoSecondBlock =findViewById(R.id.confirm_acc_no_block_two);
        edtTxtConfirmAccountNoThirdBlock = findViewById(R.id.confirm_acc_no_block_three);

        edtTxtConfirmAccountNoFirstBlock.setOnEditorActionListener(this);
        edtTxtConfirmAccountNoSecondBlock.setOnEditorActionListener(this);
        edtTxtConfirmAccountNoThirdBlock.setOnEditorActionListener(this);

        edtTxtConfirmAccountNoFirstBlock.setOnFocusChangeListener(this);
        edtTxtConfirmAccountNoSecondBlock.setOnFocusChangeListener(this);
        edtTxtConfirmAccountNoThirdBlock.setOnFocusChangeListener(this);

        edtTxtConfirmAccountNoFirstBlock.addTextChangedListener(this);
        edtTxtConfirmAccountNoSecondBlock.addTextChangedListener(this);
        edtTxtConfirmAccountNoThirdBlock.addTextChangedListener(this);

        edtTxtAmount = findViewById(R.id.edt_txt_amount);
        edt_txt_remark = findViewById(R.id.edt_txt_remark);

        mAccountNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() < 14 ) {

                    mScannedValue = null;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });

        mAmountEt = findViewById(R.id.amount);
        mMessageEt = findViewById(R.id.message);

        button = findViewById(R.id.btn_submit);
        btn_clear = findViewById(R.id.btn_clear);
        txt_amtinword= findViewById(R.id.txt_amtinword);
        button.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        Button btnScanAccounttNo = findViewById(R.id.btn_scan_acnt_no);
        btnScanAccounttNo.setOnClickListener(this);

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(this);


       /* UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token =  loginCredential.token;
        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        try {
            Log.e(TAG,"token   251   "+IScoreApplication.encryptStart(token));
            Log.e(TAG,"token   252   "+IScoreApplication.encryptStart(cusid));
            Log.e(TAG,"token   253   "+IScoreApplication.encryptStart("13"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        SharedPreferences toknpref = OtherAccountFundTransferActivity.this.getSharedPreferences(Config.SHARED_PREF35, 0);
         token=toknpref.getString("Token", null);

        SharedPreferences cusidpref = OtherAccountFundTransferActivity.this.getSharedPreferences(Config.SHARED_PREF26, 0);
         cusid=cusidpref.getString("customerId", null);

        setAccountNumber();
        setAccountType();

        getminTransAmount();


        edtTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                edtTxtAmount.removeTextChangedListener(this);

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
                    edtTxtAmount.setText(formattedString);
                    edtTxtAmount.setSelection(edtTxtAmount.getText().length());


                    String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
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
                    txt_amtinword.setText(""+amountInWordPop);


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edtTxtAmount.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() != 0) {
                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        button.setText( "PAY  "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        button.setText( "PAY");
                    }
                }
                catch (NumberFormatException e)
                {

                }

            }
        });
    }



    private void getminTransAmount() {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(OtherAccountFundTransferActivity.this, R.style.Progress);
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


                //String sourceAccount = mAccountSpinner.getSelectedItem().toString();
                try {
                    requestObject1.put("ReqMode", IScoreApplication.encryptStart("18") );
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1    761  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getFundTransferLimit(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("FundTransferLimit");
                                MaximumAmount = jobj.getString("MaximumAmount");
//                                tv_maxamount.setText("Transfer upto ₹ " + MaximumAmount + " instantly.");
                                double num =Double.parseDouble(MaximumAmount);
                                if (num > 0 ){
                                    tv_maxamount.setVisibility(View.VISIBLE);
                                    tv_maxamount.setText("Transfer upto ₹ " + CommonUtilities.getDecimelFormate(num) + " instantly.");
                                }
                                else {
                                    tv_maxamount.setVisibility(View.GONE);
                                }
                            }
                            else {
                                tv_maxamount.setVisibility(View.GONE);

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
            alertMessage1("", "Network is currently unavailable. Please try again later.");
            /*DialogUtil.showAlert(this,
                    "Network is currently unavailable. Please try again later.");*/
        }



    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            int id;
            //noinspection ConstantConditions
            id = getCurrentFocus().getId();
            changeFocusOnTextFill(id);
        }catch (NullPointerException e){
            if (IScoreApplication.DEBUG)
                Log.d("Error", e.toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Do nothing

    }

    private void changeFocusOnTextFill(int id){
        switch (id){
            case R.id.acc_no_block_one:
                if (edtTxtAccountNoFirstBlock.getText().toString().length() == 3)
                    edtTxtAccountNoSecondBlock.requestFocus();
                break;
            case R.id.acc_no_block_two:
                if (edtTxtAccountNoSecondBlock.getText().toString().length() == 3)
                    edtTxtAccountNoThirdBlock.requestFocus();
                break;
            case R.id.acc_no_block_three:
                if (edtTxtAccountNoThirdBlock.getText().toString().length() == 6)
                    edtTxtConfirmAccountNoFirstBlock.requestFocus();
                break;
            case R.id.confirm_acc_no_block_one:
                if (edtTxtConfirmAccountNoFirstBlock.getText().toString().length() == 3)
                    edtTxtConfirmAccountNoSecondBlock.requestFocus();
                break;
            case R.id.confirm_acc_no_block_two:
                if (edtTxtConfirmAccountNoSecondBlock.getText().toString().length() == 3)
                    edtTxtConfirmAccountNoThirdBlock.requestFocus();
                break;

            default:break;
        }

    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent i){
        String editorAction = "editor_action";
        assert v != null;
        int id = v.getId();
        if (actionId == EditorInfo.IME_ACTION_DONE  || actionId == EditorInfo.IME_ACTION_NEXT  || actionId == EditorInfo.IME_ACTION_NONE) {
            switch (id){
                case R.id.acc_no_block_one:
                    findValueForEditText(edtTxtAccountNoFirstBlock.getText().toString(), id, editorAction );
                    break;
                case R.id.acc_no_block_two:
                    findValueForEditText(edtTxtAccountNoSecondBlock.getText().toString(), id, editorAction);
                    break;
                case R.id.acc_no_block_three:
                    findValueForEditText(edtTxtAccountNoThirdBlock.getText().toString(), id, editorAction);
                    break;
                case R.id.confirm_acc_no_block_one:
                    findValueForEditText(edtTxtConfirmAccountNoFirstBlock.getText().toString(), id, editorAction);
                    break;
                case R.id.confirm_acc_no_block_two:
                    findValueForEditText(edtTxtConfirmAccountNoSecondBlock.getText().toString(), id, editorAction);
                    break;
                case R.id.confirm_acc_no_block_three:
                    findValueForEditText(edtTxtConfirmAccountNoThirdBlock.getText().toString(), id, editorAction);
                    break;

                default:break;
            }
        }
        return false;
    }
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        String focusChange = "focus_change";
        if (!hasFocus) {
            int id = view.getId();
            switch (id){
                case R.id.acc_no_block_one:
                    findValueForEditText(edtTxtAccountNoFirstBlock.getText().toString(), R.id.acc_no_block_one, focusChange);
                    break;
                case R.id.acc_no_block_two:
                    findValueForEditText(edtTxtAccountNoFirstBlock.getText().toString(), R.id.acc_no_block_one, focusChange);
                    findValueForEditText(edtTxtAccountNoSecondBlock.getText().toString(), R.id.acc_no_block_two, focusChange);
                    break;
                case R.id.acc_no_block_three:
                    findValueForEditText(edtTxtAccountNoFirstBlock.getText().toString(), R.id.acc_no_block_one, focusChange);
                    findValueForEditText(edtTxtAccountNoSecondBlock.getText().toString(), R.id.acc_no_block_two, focusChange);
                    findValueForEditText(edtTxtAccountNoThirdBlock.getText().toString(), R.id.acc_no_block_three, focusChange);
                    break;

                case R.id.confirm_acc_no_block_one:
                    findValueForEditText(edtTxtConfirmAccountNoFirstBlock.getText().toString(), R.id.confirm_acc_no_block_one, focusChange);
                    break;
                case R.id.confirm_acc_no_block_two:
                    findValueForEditText(edtTxtConfirmAccountNoFirstBlock.getText().toString(), R.id.confirm_acc_no_block_one, focusChange);
                    findValueForEditText(edtTxtConfirmAccountNoSecondBlock.getText().toString(), R.id.confirm_acc_no_block_two, focusChange);
                    break;
                case R.id.confirm_acc_no_block_three:
                    findValueForEditText(edtTxtConfirmAccountNoFirstBlock.getText().toString(), R.id.confirm_acc_no_block_one, focusChange);
                    findValueForEditText(edtTxtConfirmAccountNoSecondBlock.getText().toString(), R.id.confirm_acc_no_block_two, focusChange);
                    findValueForEditText(edtTxtConfirmAccountNoThirdBlock.getText().toString(), R.id.confirm_acc_no_block_three, focusChange);
                    break;

                default:break;
            }
        }
    }
    private void findValueForEditText(String value, int id, String from){
        int stringLength = value.length();
        if (( id == R.id.acc_no_block_one || id == R.id.acc_no_block_two || id == R.id.confirm_acc_no_block_one || id == R.id.confirm_acc_no_block_two)
                && (stringLength > 0 && stringLength < 4)){
            switch (stringLength){
                case 1:
                    value = "00"+value;
                    break;
                case 2:
                    value = "0"+value;
                    break;

                default:break;
            }
        }
        else if ((id == R.id.acc_no_block_three || id == R.id.confirm_acc_no_block_three) && ( stringLength > 0 && stringLength < 7 )){
            switch (stringLength){
                case 1:
                    value = "00000"+value;
                    break;
                case 2:
                    value = "0000"+value;
                    break;
                case 3:
                    value = "000"+value;
                    break;
                case 4:
                    value = "00"+value;
                    break;
                case  5:
                    value = "0"+value;
                    break;
                default:break;
            }
        }
        if (from.equals("editor_action"))
            assignTextToEdtTextOnKeyBoardImeAction(value, id);
        else
            assignTextToEdtTextOnFocusChange(value, id);
    }
    private void assignTextToEdtTextOnFocusChange(String value, int id){
        switch (id){
            case R.id.acc_no_block_one:
                edtTxtAccountNoFirstBlock.setText(value);
                break;
            case R.id.acc_no_block_two:
                edtTxtAccountNoSecondBlock.setText(value);
                break;
            case R.id.acc_no_block_three:
                edtTxtAccountNoThirdBlock.setText(value);
                break;
            case R.id.confirm_acc_no_block_one:
                edtTxtConfirmAccountNoFirstBlock.setText(value);
                break;
            case R.id.confirm_acc_no_block_two:
                edtTxtConfirmAccountNoSecondBlock.setText(value);
                break;
            case R.id.confirm_acc_no_block_three:
                edtTxtConfirmAccountNoThirdBlock.setText(value);
                break;
            default:break;
        }
    }
    private void assignTextToEdtTextOnKeyBoardImeAction(String value, int id){
        switch (id){
            case R.id.acc_no_block_one:
                edtTxtAccountNoFirstBlock.setText(value);
                edtTxtAccountNoSecondBlock.requestFocus();
                break;
            case R.id.acc_no_block_two:
                edtTxtAccountNoSecondBlock.setText(value);
                edtTxtAccountNoThirdBlock.requestFocus();
                break;
            case R.id.acc_no_block_three:
                edtTxtAccountNoThirdBlock.setText(value);
                break;
            case R.id.confirm_acc_no_block_one:
                edtTxtConfirmAccountNoFirstBlock.setText(value);
                edtTxtConfirmAccountNoSecondBlock.requestFocus();
                break;
            case R.id.confirm_acc_no_block_two:
                edtTxtConfirmAccountNoSecondBlock.setText(value);
                edtTxtConfirmAccountNoSecondBlock.requestFocus();
                break;
            case R.id.confirm_acc_no_block_three:
                edtTxtConfirmAccountNoThirdBlock.setText(value);
                break;
            default:break;
        }
    }

    private void setAccountNumber() {
        settingAccountNumber(cusid);

        /*SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        if (settingsModel == null) {
            settingAccountNumber(null);
        } else {
            settingAccountNumber(settingsModel.customerId);
        }*/
    }
    private void settingAccountNumber(String cusid){
        //  CommonUtilities.transactionActivitySetAccountNumber(customerId, mAccountSpinner, getActivity());

        if (cusid.isEmpty())
            return;

        SharedPreferences acntpref = OtherAccountFundTransferActivity.this.getSharedPreferences(Config.SHARED_PREF43, 0);
        String acntnos=acntpref.getString("accountNoarray", null);

        try {
            array = new JSONArray(acntnos);

            for (int i=0; i<array.length(); i++) {
                obj1 = array.getJSONObject(i);
                accno = obj1.getString("acno");
                typeshrt = obj1.getString("typeShort");
                accountSpinnerItems.add(accno+" ("+typeshrt+")");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

      //  List<String> accountSpinnerItems  ;
        //accountSpinnerItems = PBAccountInfoDAO.getInstance().getAccountNos();
        ArrayList<String> itemTemp =  new ArrayList<>();

        if (accountSpinnerItems.isEmpty())
            return;

        for (int i = 0; i< accountSpinnerItems.size(); i++){

            if (!accountSpinnerItems.get(i).contains("SB") && !accountSpinnerItems.get(i).contains("CA") && !accountSpinnerItems.get(i).contains("OD"))
                itemTemp.add(accountSpinnerItems.get(i));

        }
        for ( String item: itemTemp ) {
            accountSpinnerItems.remove(item);
        }

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter< >(this, R.layout.simple_spinner_item_dark, accountSpinnerItems);
        mAccountSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < accountSpinnerItems.size(); i++) {
            String account = accountSpinnerItems.get(i);

            if (TextUtils.isEmpty(account)) {
                continue;
            }
        //    SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
            if (account.equalsIgnoreCase(cusid)) {
                mAccountSpinner.setSelection(i);

                break;
            }
        }
    }

    private void setAccountType() {

        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.savings_bank));
        items.add(getString(R.string.current_account));
        items.add(getString(R.string.cash_credit));

        if ( this == null )
            return;

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, R.layout.simple_spinner_item_dark, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAccountTypeSpinner.setAdapter(spinnerAdapter);
        mAccountTypeSpinner.setOnItemSelectedListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        int id = v.getId();

      /*  if ( getActivity() == null || getContext() == null )
            return;*/
        switch (id) {
            case R.id.btn_submit:
                submit();

                break;
            case R.id.btn_clear:

                edtTxtAccountNoFirstBlock.setText("");
                edtTxtAccountNoSecondBlock.setText("");
                edtTxtAccountNoThirdBlock.setText("");
                edtTxtConfirmAccountNoFirstBlock.setText("");
                edtTxtConfirmAccountNoSecondBlock.setText("");
                edtTxtConfirmAccountNoThirdBlock.setText("");
                edtTxtAmount.setText("");
                edt_txt_remark.setText("");
                tv_maxamount.setText("");
                txt_amtinword.setText("");
                setAccountNumber();
                setAccountType();

                getminTransAmount();

                break;
            case R.id.scan:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivityForResult(intent, 100);
                }

                break;
            case R.id.btn_scan_acnt_no:
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            ZXING_CAMERA_PERMISSION);
                }else {
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            default:break;
        }
    }
    private void submit(){
        if ( this == null )
            return;
        String recieverAccountNo = confirmAndSetRecieversAccountNo();

        if (isValid() && recieverAccountNo.length() == 12) {
            if (NetworkUtil.isOnline()) {
                final String accountNumber = SourceAccountNumber;

                final String amount = edtTxtAmount.getText().toString();
                String remark = edt_txt_remark.getText().toString();
                String fromAccountNo  = accountNumber.substring(0,12);
                final String accNumber = recieverAccountNo;

                final String[] type = {""};


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.confirmation_msg_popup, null);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setView(dialogView);

                TextView txtvAcntno = dialogView.findViewById(R.id.txtvAcntno);
                TextView txtvbranch = dialogView.findViewById(R.id.txtvbranch);
                TextView txtvbalnce = dialogView.findViewById(R.id.txtvbalnce);

                TextView txtvAcntnoto = dialogView.findViewById(R.id.txtvAcntnoto);
                TextView txtvbranchto = dialogView.findViewById(R.id.txtvbranchto);
                TextView txtvbalnceto = dialogView.findViewById(R.id.txtvbalnceto);

                txtvAcntno.setText("A/C No : "+SourceAccountNumber);
                txtvbranch.setText("Branch :"+BranchName);
                double num1 =Double.parseDouble(Balance);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");

                txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));

                String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
                TextView text_confirmationmsg = dialogView.findViewById(R.id.text_confirmationmsg);

                txtvAcntnoto.setText("A/C No: "+edtTxtConfirmAccountNoFirstBlock.getText().toString()+edtTxtConfirmAccountNoSecondBlock.getText().toString()+edtTxtConfirmAccountNoThirdBlock.getText().toString());
                txtvbranchto.setText("A/C Type :"+mAccountTypeSpinner.getSelectedItem().toString());



                TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
                TextView tv_amount_words = dialogView.findViewById(R.id.tv_amount_words);
                Button butOk = dialogView.findViewById(R.id.btnOK);
                Button butCan = dialogView.findViewById(R.id.btnCncl);

                String stramnt = CommonUtilities.getDecimelFormate(Double.parseDouble(amnt));
                text_confirmationmsg.setText("Proceed Transaction with above receipt amount"+ "..?");
             //   text_confirmationmsg.setText("Proceed Transaction with above receipt amount to A/C no " + accNumber + " ..?");
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

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                butOk.setOnClickListener(v -> {

                    alertDialog.dismiss();

                    String  accountType = mAccountTypeSpinner.getSelectedItem().toString();

                    if (accountType.equalsIgnoreCase(getString(R.string.savings_bank))) {
                        type[0] = "SB";
                    } else if (accountType.equalsIgnoreCase( getString(R.string.current_account) )) {
                        type[0] = "CA";
                    }
                    else if (accountType.equalsIgnoreCase( getString(R.string.cash_credit) )) {
                        type[0] = "OD";
                    }
                    else if (accountType.equalsIgnoreCase( getString(R.string.member_loan) )) {
                        type[0] = "ML";
                    }
                    else if (accountType.equalsIgnoreCase( getString(R.string.recurring_deposit) )) {
                        type[0] = "RD";
                    }
                    else if (accountType.equalsIgnoreCase( getString(R.string.jewell_loan) )) {
                        type[0] = "JL";
                    }
                    else {
                        type[0] = "GD";
                    }
                    String Finalamount = amount.replace(",","");

                    startTransfer1( accountNumber, type[0], accNumber, Finalamount,remark);
                });

                butCan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            } else {


                alertMessage1("", "Network is currently unavailable. Please try again later.");

              /*  DialogUtil.showAlert(this,
                        "Network is currently unavailable. Please try again later.");*/
            }
        }
    }

    private void startTransfer1(String accountNumber, String type, String recvaccNumber, String finalamount, String remark) {

        if (TextUtils.isEmpty(mScannedValue)) {
            mScannedValue = "novalue";
        }
        mScannedValue = mScannedValue.replaceAll(" ", "%20");

        /*Extract account number*/
        accountNumber = accountNumber.replace(accountNumber.substring(accountNumber.indexOf(" (") + 1, accountNumber.indexOf(")") + 1), "");
        accountNumber = accountNumber.replace(" ", "");

      //  AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accountNumber);

     //   String accountType = accountInfo.accountTypeShort;
        final String tempFromAccNo = accountNumber +"("+ type +")";
        final String tempToAccNo = recvaccNumber +"("+ type +")";

        SharedPreferences pref =getApplicationContext().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try {


                SharedPreferences bankkeypref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                String BankKey = bankkeypref.getString("bankkey", null);
                SharedPreferences bankheaderpref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                String BankHeader = bankheaderpref.getString("bankheader", null);

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
                    requestObject1.put("AccountNo", IScoreApplication.encryptStart(accountNumber));
                    requestObject1.put("Module", IScoreApplication.encryptStart(type) );
                    requestObject1.put("ReceiverModule", IScoreApplication.encryptStart(type));
                    requestObject1.put("ReceiverAccountNo", IScoreApplication.encryptStart(recvaccNumber.trim()));

                    String amot = finalamount.replace(",","");
                    requestObject1.put("amount", IScoreApplication.encryptStart(amot));

                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");

                    requestObject1.put("Pin", IScoreApplication.encryptStart(pin));
                    requestObject1.put("QRCode", IScoreApplication.encryptStart(mScannedValue));
                    requestObject1.put("Remark", IScoreApplication.encryptStart(remark));

                    // requestObject1.put("IMEI",IScoreApplication.encryptStart(ToFK_Account));

                    SharedPreferences preftoken =getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String tokn =preftoken.getString("Token", "");

                    requestObject1.put("token", IScoreApplication.encryptStart(tokn));


                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1 0",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getfundtransfrintrabnk(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e(TAG,"Response otheraccount   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject j1 = jObject.getJSONObject("FundTransferIntraBankList");
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



                                alertMessage("", fundTransferResult, statusmsg, true, false);
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
                                 JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OtherAccountFundTransferActivity.this);
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OtherAccountFundTransferActivity.this);
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
            } catch (Exception e) {
                //Do nothing
            }
        }
        else {
            alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){


        if ( requestCode == ZXING_CAMERA_PERMISSION ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, 100);
            }else {
                Toast.makeText(this, "App need permission for use camera to scan account number", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValid() {

        String amount = edtTxtAmount.getText().toString();
        if (MaximumAmount != ""){
            final double MaximumAmountD = Double.parseDouble(MaximumAmount);
            amount = amount.replace(",", "");

            if (amount.length() < 1 && MaximumAmountD > 0){
                edtTxtAmount.setError("Please enter amount between 1 and " + MaximumAmount + ".");
                return false;
            }
            else if (amount.length() < 1 && MaximumAmountD == 0){
                edtTxtAmount.setError("Please enter valid amount.");
                return false;
            }
            final double amt = Double.parseDouble(amount);

            if (MaximumAmountD > 0){

                if(amt < 1 || amt > MaximumAmountD) {

                    edtTxtAmount.setError("Please enter amount between 1 and " + MaximumAmountD + ".");
                    return false;
                }
            }
        }


        edtTxtAmount.setError(null);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 100 && resultCode == 200) {
            String value = data.getStringExtra("Value");

            if(TextUtils.isEmpty(value)) {
                return;
            }

            if(value.trim().length() >= 14) {
                value = value.substring(0, 14);
            }
            String customerNumber = value.substring(0,12);
            String Submodule = value.substring(12,14);
            CustomerList.clear();
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater1.inflate(R.layout.cusmodule_popup, null);
                list_view = (ListView) layout.findViewById(R.id.list_view);
                tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
                tv_popuptitle.setText("Select Account");
                builder.setView(layout);
                final AlertDialog alertDialog = builder.create();
                getCustomerAccount(alertDialog,customerNumber,Submodule);
                alertDialog.show();
            }catch (Exception e){e.printStackTrace();}


        }
    }

    private void getCustomerAccount(AlertDialog alertDialog, String value, String submodule) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(this, R.style.Progress);
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
                String  accountType = mAccountTypeSpinner.getSelectedItem().toString();
                final String type;

                if (accountType.equalsIgnoreCase(getString(R.string.savings_bank))) {
                    type = "SB";
                } else if (accountType.equalsIgnoreCase( getString(R.string.current_account) )) {
                    type = "CA";
                }
                else if (accountType.equalsIgnoreCase( getString(R.string.cash_credit) )) {
                    type = "OD";
                }
                else if (accountType.equalsIgnoreCase( getString(R.string.member_loan) )) {
                    type = "ML";
                }
                else if (accountType.equalsIgnoreCase( getString(R.string.recurring_deposit) )) {
                    type = "RD";
                }
                else if (accountType.equalsIgnoreCase( getString(R.string.jewell_loan) )) {
                    type = "JL";
                }
                else {
                    type = "GD";
                }

                //String sourceAccount = mAccountSpinner.getSelectedItem().toString();
                try {
                    requestObject1.put("ReqMode", IScoreApplication.encryptStart("10") );
                    requestObject1.put("CustomerNoumber", IScoreApplication.encryptStart(value) );
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("SubModule", IScoreApplication.encryptStart(type) );
                    requestObject1.put("ModuleCode", IScoreApplication.encryptStart(submodule) );
                    requestObject1.put("FK_Customer", IScoreApplication.encryptStart(cusid));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1    761  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("BarcodeAgainstCustomerAccountDets");
                                JSONArray jarray = jobj.getJSONArray( "BarcodeAgainstCustomerAccountList");
                                if(jarray.length()!=0){
                                    for (int k = 0; k < jarray.length(); k++) {
                                        JSONObject jsonObject = jarray.getJSONObject(k);
                                        CustomerList.add(new BarcodeAgainstCustomerAccountList(jsonObject.getString("FK_Customer"),jsonObject.getString("CustomerName"),jsonObject.getString("AccountName"),jsonObject.getString("AccountNumber")));
                                    }
                                    if(jarray.length()==1){
                                        dataItem = CustomerList.get(0).getAccountNumber();
                                        mScannedValue = dataItem;
                                        displayAccountNumber(dataItem,alertDialog);

                                    }else {


                                        HashSet<BarcodeAgainstCustomerAccountList> hashSet = new HashSet<>(CustomerList);
                                        CustomerList.clear();
                                        CustomerList.addAll(hashSet);
                                        CustomListAdapter adapter = new CustomListAdapter(OtherAccountFundTransferActivity.this,CustomerList);
                                        list_view.setAdapter(adapter);
                                        list_view.setOnItemClickListener((parent, view, position, id) -> {
                                            dataItem = CustomerList.get(position).getAccountNumber();
                                            mScannedValue = dataItem;
                                            displayAccountNumber(dataItem,alertDialog);
                                        });

                                    }


                                }

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(OtherAccountFundTransferActivity.this);
                                builder.setMessage(jObject.getString("EXMessage"))
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

            alertMessage1("", "Network is currently unavailable. Please try again later.");


          /*  DialogUtil.showAlert(this,
                    "Network is currently unavailable. Please try again later.");*/
        }



    }

    private void displayAccountNumber(String data,AlertDialog alertDialog){

        if(data.trim().length() >= 14) {
            data = data.substring(0, 14);
        }


        if(data.startsWith("01")) {
            mAccountTypeSpinner.setSelection(0);
        } else if(data.startsWith("02")) {
            mAccountTypeSpinner.setSelection(1);
        }
        String firstValueQrScanned = data.substring(0,3);
        String secondValueQrScanned = data.substring(3,6);
        String thirdValueQrScanned = data.substring(6,12);

        edtTxtAccountNoFirstBlock.setText(firstValueQrScanned);
        edtTxtAccountNoSecondBlock.setText(secondValueQrScanned);
        edtTxtAccountNoThirdBlock.setText(thirdValueQrScanned);

        edtTxtConfirmAccountNoFirstBlock.setText(firstValueQrScanned);
        edtTxtConfirmAccountNoSecondBlock.setText(secondValueQrScanned);
        edtTxtConfirmAccountNoThirdBlock.setText(thirdValueQrScanned);

        mAccountNumberEt.setText(data);
        mConfirmAccountNumberEt.setText(data);

        mAmountEt.requestFocus();
        mAmountEt.setSelection(mAmountEt.getText().length());
        alertDialog.dismiss();
    }


    private String confirmAndSetRecieversAccountNo(){
        String recieverAccountNo = edtTxtAccountNoFirstBlock.getText().toString()+
                edtTxtAccountNoSecondBlock.getText().toString()+
                edtTxtAccountNoThirdBlock.getText().toString();
        String confirmRecieverAccountNo = edtTxtConfirmAccountNoFirstBlock.getText().toString()+
                edtTxtConfirmAccountNoSecondBlock.getText().toString()+
                edtTxtConfirmAccountNoThirdBlock.getText().toString();
        if ( recieverAccountNo.equals( confirmRecieverAccountNo ) &&
                recieverAccountNo.length() == 12 && confirmRecieverAccountNo.length() == 12){
            return recieverAccountNo;
        }
        else if ( !recieverAccountNo.equals( confirmRecieverAccountNo ) &&  recieverAccountNo.length() == 12 && confirmRecieverAccountNo.length() == 12){
            showAlert();
        }
        else {
            if (edtTxtAccountNoFirstBlock.getText().toString().length() < 3)
                edtTxtAccountNoFirstBlock.setError("Atleast 3 digit are required");

            if (edtTxtAccountNoSecondBlock.getText().toString().length() <3)
                edtTxtAccountNoSecondBlock.setError("Atleast 3 digit are required");

            if (edtTxtAccountNoThirdBlock.getText().toString().length() < 6)
                edtTxtAccountNoThirdBlock.setError("Atleast 6 digits are required");

            if (edtTxtConfirmAccountNoFirstBlock.getText().toString().length() < 3)
                edtTxtConfirmAccountNoFirstBlock.setError("Atleast 3 digits are required");

            if (edtTxtConfirmAccountNoSecondBlock.getText().toString().length() < 3)
                edtTxtConfirmAccountNoSecondBlock.setError("Atleast 3 digits are required");

            if (edtTxtConfirmAccountNoThirdBlock.getText().toString().length() < 6)
                edtTxtConfirmAccountNoThirdBlock.setError("Atleast 6 digits are required");
        }
        return "";
    }

    private void showAlert(){
        if ( this == null )
            return;
        alertMessage1("Oops...", "Both account number and confirm account number are not matching");
       /* new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Both account number and confirm account number are not matching")
                .show();*/
    }



    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OtherAccountFundTransferActivity.this);

        LayoutInflater inflater = OtherAccountFundTransferActivity.this.getLayoutInflater();
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
        PicassoTrustAll.getInstance(OtherAccountFundTransferActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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

    private void alertMessage(String title, FundTransferResult1 fundTransferResult1, String message , boolean isHappy, boolean isBackButtonEnabled ){
        Log.e(TAG,"alertMessage   954   "+title+"   "+fundTransferResult1+"   "+message+"   "+isHappy+"    "+isBackButtonEnabled);

        alertPopup(title,fundTransferResult1,message,isHappy,isBackButtonEnabled);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.spn_account_num){

            Log.e(TAG,"SPINNN   spn_account_num  1042");
        }

        else if (adapterView.getId() == R.id.spn_account_type){
            Log.e(TAG,"SPINNN   spn_account_type  1046");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

    private void alertPopup(String title, FundTransferResult1 fundTransferResult1, String message, boolean isHappy, boolean isBackButtonEnabled) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_message_alert, null);
        dialogBuilder.setView(dialogView);

        RelativeLayout rltv_share = dialogView.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = dialogView.findViewById( R.id.lay_share );
        mRecyclerView = dialogView.findViewById( R.id.recycler_message );
        ImageView imgIcon      = dialogView.findViewById( R.id.img_success );
        ImageView img_share      = dialogView.findViewById( R.id.img_share );
        TextView txtTitle       = dialogView.findViewById( R.id.txt_success );
        TextView txtMessage = dialogView.findViewById( R.id.txt_message );
        TextView tvrefe = dialogView.findViewById( R.id.tvrefe );
        TextView tvdate = dialogView.findViewById( R.id.tvdate );
        TextView tvtime = dialogView.findViewById( R.id.tvtime );
        TextView tv_amount_words = dialogView.findViewById( R.id.tv_amount_words );

        TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
        TextView txtvAcntno = dialogView.findViewById(R.id.txtvAcntno);
        TextView txtvbranch = dialogView.findViewById(R.id.txtvbranch);
        TextView txtvbalnce = dialogView.findViewById(R.id.txtvbalnce);

        TextView txtvAcntnoto = dialogView.findViewById(R.id.txtvAcntnoto);
        TextView txtvbranchto = dialogView.findViewById(R.id.txtvbranchto);
        TextView txtvbalnceto = dialogView.findViewById(R.id.txtvbalnceto);

        tvrefe.setText("Ref.No "+fundtransfrlist.get(0).getrefId());

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

        String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
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
        Log.e(TAG,"CommonUtilities  945   "+ CommonUtilities.getDecimelFormate(num));
        String stramnt = CommonUtilities.getDecimelFormate(num);


        tv_amount.setText("₹ " + stramnt );



        txtvAcntno.setText("A/C :"+SourceAccountNumber);
        txtvbranch.setText("Branch :"+BranchName);
        double num1 = Double.parseDouble(Balance) - Double.parseDouble(stramnt.replace(",",""));
        DecimalFormat fmt = new DecimalFormat("#,##,###.00");

        txtvbalnce.setVisibility(View.GONE);

        String rtype =mAccountTypeSpinner.getSelectedItem().toString();
        String type ="";

        if(rtype.equals("Savings bank"))
        {
            type = "SB";
        }
        else if(rtype.equals("Current account"))
        {
            type = "CA";
        }
        else if(rtype.equals("Cash credit"))
        {
            type = "OD";
        }

        receiveraccno =edtTxtConfirmAccountNoFirstBlock.getText().toString()+
                edtTxtConfirmAccountNoSecondBlock.getText().toString()+
                edtTxtConfirmAccountNoThirdBlock.getText().toString()+"("+type+")";


        txtvAcntnoto.setText("A/C : "+fundtransfrlist.get(0).getAccNo());
        txtvbranchto.setText("Branch :"+result);
        txtvbranchto.setVisibility(View.GONE);




        dialogView.findViewById( R.id.rltv_footer ).setOnClickListener(view1 -> {
            try{

                Intent i=new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );

        try{

            txtMessage.setText( message );
            txtTitle.setText( title );
            if ( !isHappy ){
                imgIcon.setImageResource( R.mipmap.ic_failed );
            }

            lay_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("img_share","img_share   1170   ");
                    Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                            rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    rltv_share.draw(canvas);

                    try {

                          File file = saveBitmap(bitmap, SourceAccountNumber+".png");
                          Log.e("chase  2044   ", "filepath: "+file.getAbsolutePath());

                        Uri bmpUri = Uri.fromFile(file);
                        //  Uri bmpUri = getLocalBitmapUri(bitmap);

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


        }catch ( Exception e){
            //Do nothing
        }

        AlertDialog alertDialog = dialogBuilder.create();
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

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
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
