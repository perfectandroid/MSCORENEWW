package com.creativethoughts.iscore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AccountSplitBalanceDetailsAdapter;
import com.creativethoughts.iscore.adapters.CustomListAdapter;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.custom_alert_dialogs.SuccessAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.BarcodeAgainstCustomerAccountList;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
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
import java.text.NumberFormat;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OwnAccountFundTransferActivity extends AppCompatActivity implements View.OnClickListener, EditText.OnEditorActionListener,
        View.OnFocusChangeListener, TextWatcher, AdapterView.OnItemSelectedListener{

    String TAG = "FundTransferFragment";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Button button,btn_clear;
    private Spinner mAccountSpinner;
    private Spinner mAccountPayingToSpinner;
    private AppCompatEditText mAccountNumberEt;
    private AppCompatEditText mConfirmAccountNumberEt;
    private AppCompatEditText mAmountEt;
    private AppCompatEditText mMessageEt;
    private String mScannedValue;
    private EditText edtTxtAccountNoFirstBlock;
    private EditText edtTxtAccountNoSecondBlock;
    private EditText edtTxtAccountNoThirdBlock;
    private EditText edtTxtConfirmAccountNoFirstBlock;
    private EditText edtTxtConfirmAccountNoSecondBlock;
    private EditText edtTxtConfirmAccountNoThirdBlock;
    private EditText edtTxtAmount;
    private EditText edt_txt_remark;
    private String token,cusid,dataItem;
    ListView list_view;
    TextView tv_popuptitle,txt_amtinword;
    private ArrayList<BarcodeAgainstCustomerAccountList> CustomerList = new ArrayList<>();
    TextView tv_dueamount,tv_availbal,tv_balance,tv_account_no;
    private RecyclerView mRecyclerView;
    private static final String KEY_VALUE = "keyvalue";
    private static final String TITLE = "title";
    private static final String HAPPY = "happy";
    private static final String MESSAGE = "message";
    String SourceAccountNumber,Balance,typeShort,SubModule,FK_Account, BranchName;
    String statusmessage;
    ArrayList<ToAccountDetails> AccountDetails = new ArrayList<>();
    ArrayAdapter<ToAccountDetails> AccountAdapter = null;
    String result;
    RecyclerView rv_split_details;
    Spinner status_spinner;
    String[] pendinginsa;
    LinearLayout ll_remittance,ll_needTochange, ll_needToPayAdvance;
    TextView tvAdvance,tvInstallment,tv_branch_name,tv_as_on_date;
    String reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fund_transferfragment);

        SourceAccountNumber = getIntent().getStringExtra("AccountNumber");
        BranchName = getIntent().getStringExtra("BranchName");
        Balance = getIntent().getStringExtra("Balance");
        typeShort = getIntent().getStringExtra("typeShort");
        SubModule = getIntent().getStringExtra("SubModule");
        FK_Account = getIntent().getStringExtra("FK_Account");
        txt_amtinword= findViewById(R.id.txt_amtinword);

        tv_account_no = findViewById(R.id.tv_account_no);
//        tv_as_on_date = findViewById(R.id.tv_as_on_date);
        tv_branch_name = findViewById(R.id.tv_branch_name);
        ll_remittance = findViewById(R.id.ll_remittance);
        ll_needTochange = findViewById(R.id.ll_needTochange);
        ll_needToPayAdvance = findViewById(R.id.ll_needToPayAdvance);
        tv_balance = findViewById(R.id.tv_balance);
        tvInstallment = findViewById(R.id.tvInstallment);
        tvAdvance = findViewById(R.id.tvAdvance);
        tv_account_no.setText(SourceAccountNumber);
        tv_branch_name.setText(BranchName);

        tvAdvance.setPaintFlags(tvAdvance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvInstallment.setPaintFlags(tvInstallment.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        double num =Double.parseDouble(Balance);
        DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//        tv_balance.setText("\u20B9 "+ fmt.format(num));
        tv_balance.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        rv_split_details = findViewById(R.id.rv_split_details);
        mAccountSpinner = findViewById(R.id.spn_account_num);
        mAccountPayingToSpinner = findViewById(R.id.spn_account_type);
        tv_dueamount = findViewById(R.id.tv_dueamount);
        tv_availbal = findViewById(R.id.tv_availbal);
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
        edtTxtConfirmAccountNoSecondBlock = findViewById(R.id.confirm_acc_no_block_two);
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
        btn_clear= findViewById(R.id.btn_clear);
        ll_needTochange.setOnClickListener(this);
        ll_needToPayAdvance.setOnClickListener(this);
        button.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        Button btnScanAccounttNo = findViewById(R.id.btn_scan_acnt_no);
        btnScanAccounttNo.setOnClickListener(this);

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(this);


        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;
        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        try {
            Log.e(TAG,"token   251   "+IScoreApplication.encryptStart(token));
            Log.e(TAG,"token   252   "+IScoreApplication.encryptStart(cusid));
            Log.e(TAG,"token   253   "+IScoreApplication.encryptStart("13"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAccountNumber();
        showOwnAccToList();

        status_spinner = findViewById(R.id.status_spinner);
        status_spinner.setOnItemSelectedListener(this);



        edtTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                edtTxtAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    if (!originalString.equals("")){
                        Double longval;
                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }
                        longval = Double.parseDouble(originalString);

//                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                        formatter.applyPattern("#,###,###,###");
//                        String formattedString = formatter.format(longval);
                        String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
//
                        //setting text after format to EditText
                        edtTxtAmount.setText(formattedString);
                        edtTxtAmount.setSelection(edtTxtAmount.getText().length());


                        String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
                        String[] netAmountArr = amnt.split("\\.");
                        String amountInWordPop = "";
                        if (Integer.parseInt( netAmountArr[0]) != 0){
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

                        }
                    }
                    else{
                        txt_amtinword.setText("");

                    }


                }
                catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edtTxtAmount.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.length() != 0) {
                        String originalString = s.toString();

                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }

                        double num =Double.parseDouble(""+originalString);
                        button.setText( "PAY  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        button.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }

            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        int id = v.getId();

        /*if ( getActivity() == null || getContext() == null )
            return;*/
        switch (id) {
            case R.id.ll_needTochange:
            case R.id.ll_needToPayAdvance:
                if (ll_remittance.getVisibility() == View.GONE) {
                    ll_remittance.setVisibility(View.VISIBLE);
                }
                else {
                    ll_remittance.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.btn_clear:
                setAccountNumber();
                showOwnAccToList();
//                setAccountType();
                edtTxtAmount.setText("");
                edt_txt_remark.setText("");
                txt_amtinword.setText("");
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
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            default:break;
        }
    }

    private void getpendinginsa(String ToSubModule,String ToFK_Account,String IsAdvance) {
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,pendinginsa);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(aa);
        status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                edtTxtAmount.setText(""+position);
                int remittanance = position +1;
                Log.e(TAG,"onResponse   355   ");
                remittanceDetails(""+remittanance,ToSubModule,ToFK_Account,IsAdvance);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void remittanceDetails(String remittanance, String ToSubModule, String ToFK_Account, String IsAdvance) {
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("25") );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(ToSubModule) );
                    requestObject1.put("IsAdvance",IScoreApplication.encryptStart(IsAdvance) );
                    requestObject1.put("FK_Account",IScoreApplication.encryptStart(ToFK_Account));
                    requestObject1.put("InstalmentCount",IScoreApplication.encryptStart(remittanance));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getGetInstalmmentRemittanceAmount(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("GetInstalmmentRemittanceAmount");
                                String RemittanceAmount = jobj.getString( "RemittanceAmount");
                                edtTxtAmount.setText(RemittanceAmount);
                            }
                            else{
                                edtTxtAmount.setText("");
                                try{
                                    JSONObject jobj = jObject.getJSONObject("GetInstalmmentRemittanceAmount");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
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

                                }
                                catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
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
        }
        else {

            alertMessage1("", "Network is currently unavailable. Please try again later.");
           // DialogUtil.showAlert(this,
                    //"Network is currently unavailable. Please try again later.");
        }
    }

    private void showOwnAccToList() {
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;

                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("2"));
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
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try{
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray Jarray  = object.getJSONArray("OwnAccountdetailsList");
                                if(Jarray.length()!=0) {

//                                    Date date = Calendar.getInstance().getTime();
//                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//                                    String formattedDate = df.format(date);
//                                    tv_as_on_date.setText("As On "+ formattedDate+"");
                                    JSONObject jsonobject= (JSONObject) Jarray.get(0);
                                    AccountDetails = new ArrayList<>();
                                    AccountDetails.add(new ToAccountDetails("", "Select Account","","", "", ""));

                                    for (int k = 0; k < Jarray.length(); k++) {
                                        JSONObject kjsonObject = Jarray.getJSONObject(k);

                                        AccountDetails.add(new ToAccountDetails( kjsonObject.getString("FK_Account"), kjsonObject.getString("AccountNumber"), kjsonObject.getString("SubModule"), kjsonObject.getString("Balance"), kjsonObject.getString("typeShort"), kjsonObject.getString("BranchName")));
                                    }

                                    AccountAdapter = new ArrayAdapter<>(OwnAccountFundTransferActivity.this,  R.layout.list_content_spin,R.id.textview, AccountDetails);
//                                    AccountAdapter.setDropDownViewResource( android.R.layout.activity_list_item);
                                    mAccountPayingToSpinner.setAdapter(AccountAdapter);




                                    mAccountPayingToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            if (position!=0) {

                                                // TextView textView = (TextView)mAccountTypeSpinner.getSelectedView();
                                                result = AccountAdapter.getItem(position).getBranchName();
                                                //  Toast.makeText(getApplicationContext(),AccountAdapter.getItem(position).getBranchName(),Toast.LENGTH_LONG).show();
//                                                tv_as_on_date.setVisibility(View.VISIBLE);

                                                ll_needTochange.setVisibility(View.GONE);
                                                ll_needToPayAdvance.setVisibility(View.GONE);
                                                ll_remittance.setVisibility(View.GONE);
                                                Object item = parent.getItemAtPosition(position);

                                                BalanceSplitUpDetails(AccountAdapter.getItem(position).getSubModule(),AccountAdapter.getItem(position).getFK_Account());
                                            }
                                            else {

                                                ll_needTochange.setVisibility(View.GONE);
                                                ll_needToPayAdvance.setVisibility(View.GONE);
                                                ll_remittance.setVisibility(View.GONE);
                                                rv_split_details.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            //Do nothing
                                        }
                                    });
                                }
                                else {

//                                    tv_as_on_date.setVisibility(View.GONE);
                                }
                            }
                            else {

//                                tv_as_on_date.setVisibility(View.GONE);
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
            alertMessage1("", "Network is currently unavailable. Please try again later.");
           /* DialogUtil.showAlert(OwnAccountFundTransferActivity.this,
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
        }
        catch (NullPointerException e){
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

        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        if (settingsModel == null) {
            settingAccountNumber(null);
        } else {
            settingAccountNumber(settingsModel.customerId);
        }
    }

    private void settingAccountNumber(String customerId){
        //  CommonUtilities.transactionActivitySetAccountNumber(customerId, mAccountSpinner, getActivity());

        if (customerId.isEmpty())
            return;
        List<String> accountSpinnerItems  ;
        accountSpinnerItems = PBAccountInfoDAO.getInstance().getAccountNos();
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
        mAccountSpinner.setOnItemSelectedListener(this);

        for (int i = 0; i < accountSpinnerItems.size(); i++) {
            String account = accountSpinnerItems.get(i);

            if (TextUtils.isEmpty(account)) {
                continue;
            }
            SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
            if (account.equalsIgnoreCase(settingsModel.customerId)) {
                mAccountSpinner.setSelection(i);

                break;
            }
        }


    }

    private void submit(){
        if ( this == null )
            return;
        // String recieverAccountNo = confirmAndSetRecieversAccountNo();
        String  accountPayingTo = mAccountPayingToSpinner.getSelectedItem().toString();
        try{
            if (accountPayingTo != "Select Account"){
                String recieverAccountNo  = accountPayingTo.substring(0,12);
                Log.e(TAG,"recieverAccountNo   619   "+recieverAccountNo);

                if (isValid() && recieverAccountNo.length() == 12) {
                    if (NetworkUtil.isOnline()) {
//                final String accountNumber = mAccountSpinner.getSelectedItem().toString();
                        final String accountNumber = SourceAccountNumber;

                        String amount = edtTxtAmount.getText().toString();
                        String remark = edt_txt_remark.getText().toString();
                        String fromAccountNo  = accountNumber.substring(0,12);
                        final String accNumber = recieverAccountNo;

                        String type = accountPayingTo.substring(accountPayingTo.indexOf("(")+1, accountPayingTo.indexOf(")"));
                        Log.e(TAG,"type   763  "+type);
                        amount = amount.replace(",", "");
                        if (amount.length() != 0 && Float.parseFloat(amount) > 0){

                            if (!accountNumber.equals(accountPayingTo)){


                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                                LayoutInflater inflater = this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.confirmation_msg_popup, null);
                                dialogBuilder.setCancelable(false);
                                dialogBuilder.setView(dialogView);


                                String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
                                TextView text_confirmationmsg = dialogView.findViewById(R.id.text_confirmationmsg);
                                TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
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

                                TextView tv_amount_words = dialogView.findViewById(R.id.tv_amount_words);
                                Button butOk = dialogView.findViewById(R.id.btnOK);
                                Button butCan = dialogView.findViewById(R.id.btnCncl);

                                txtvAcntnoto.setText("A/C No: "+ mAccountPayingToSpinner.getSelectedItem().toString());
                                txtvbranchto.setText("Branch :"+result);

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

                                AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();
                                String finalAmount = amount;
                                butOk.setOnClickListener(v -> {
                                    alertDialog.dismiss();
                                    startTransfer( accountNumber, type, accNumber, finalAmount,remark);
                                });

                                butCan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Account Numbers Are Same, Please Select Other Account For Transaction.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });

                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }

                        else {
                            Toast.makeText(this, "Please Enter Valid Amount.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        alertMessage1("", "Network is currently unavailable. Please try again later.");

                        // DialogUtil.showAlert(this,
                        //  "Network is currently unavailable. Please try again later.");
                    }
                }
            }
            else {
                Toast.makeText(this, "Please Select Paying To Account Number.", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){
            e.printStackTrace();
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


        if (amount.length() <1)
            return false;
//        final double amt = Double.parseDouble(amount);

//        if(amt < 1 || amt > 50000) {
//
//            edtTxtAmount.setError("Please enter amount between 0 and 50000");
//            return false;
//        }


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

    private void BalanceSplitUpDetails(String ToSubModule, String ToFK_Account) {
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(ToSubModule) );
                    requestObject1.put("FK_Account",IScoreApplication.encryptStart(ToFK_Account));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getBalanceSplitUpDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e(TAG,"onResponse   1121   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("BalanceSplitUpDetails");
                                JSONArray jarray = jobj.getJSONArray( "Data");
                                if(jarray.length()!=0){
                                    JSONObject jsonObject = jarray.getJSONObject(0);
                                    JSONArray jsonObjectjarray = jsonObject.getJSONArray( "Details");
                                    if(jsonObjectjarray.length()!=0) {
                                        String Slimit = "0";
                                        String Advancelimit = "0";
                                        rv_split_details.setVisibility(View.VISIBLE);
                                        GridLayoutManager lLayout = new GridLayoutManager(OwnAccountFundTransferActivity.this, 1);
                                        rv_split_details.setLayoutManager(lLayout);
                                        rv_split_details.setHasFixedSize(true);
                                        AccountSplitBalanceDetailsAdapter adapter = new AccountSplitBalanceDetailsAdapter(OwnAccountFundTransferActivity.this, jsonObjectjarray);
                                        rv_split_details.setAdapter(adapter);
                                        for (int k = 0; k < jsonObjectjarray.length(); k++) {
                                            JSONObject DetailsjsonObject=jsonObjectjarray.getJSONObject(k);
                                            String NetAmount = DetailsjsonObject.getString("Key");
                                            if (NetAmount.equals("NetAmount")){
                                                Log.e(TAG,"onResponse   1142   ");
                                                edtTxtAmount.setText(DetailsjsonObject.getString("Value").replace("-",""));
                                            }
                                            else{
                                                edtTxtAmount.setText("");
                                            }

                                            String BalanceInstallment = DetailsjsonObject.getString("Key");
                                            if (BalanceInstallment.equals("BalanceInstallment")){
                                                Advancelimit = DetailsjsonObject.getString("Value");
                                            }
                                        }

                                        String IsAdvance = "0";

                                        if (ToSubModule.equals("PDRD") || ToSubModule.equals("ODGD")) {

                                            JSONObject DetailsjsonObject= jsonObjectjarray.getJSONObject(0);
                                            Slimit =  DetailsjsonObject.getString("Value");
                                            int limit=Integer.parseInt(Slimit);
                                            if (limit > 0 ) {
                                                ll_needTochange.setVisibility(View.VISIBLE);
                                                edtTxtAmount.setKeyListener(null);
                                                edtTxtAmount.setEnabled(false);
                                                edtTxtAmount.setInputType(InputType.TYPE_NULL);
                                                edtTxtAmount.setFocusable(false);
                                                pendinginsa = new String[limit];
                                                IsAdvance = "0";
                                                int i;
                                                for ( i = 0; i < limit; i++ )
                                                {
                                                    pendinginsa[i] =String.valueOf( i+1);
                                                }
                                                getpendinginsa(ToSubModule,ToFK_Account,IsAdvance);

                                            }
                                            else if (limit == 0 && !Advancelimit.equals("0")){
                                                ll_needToPayAdvance.setVisibility(View.VISIBLE);
                                                edtTxtAmount.setKeyListener(null);
                                                edtTxtAmount.setEnabled(false);
                                                edtTxtAmount.setInputType(InputType.TYPE_NULL);
                                                edtTxtAmount.setFocusable(false);
                                                int Advancelimitlimit=Integer.parseInt(Advancelimit);
                                                pendinginsa = new String[Advancelimitlimit];
                                                IsAdvance = "1";
                                                int i;
                                                for ( i = 0; i < Advancelimitlimit; i++ )
                                                {
                                                    pendinginsa[i] =String.valueOf( i+1);
                                                }
                                                getpendinginsa(ToSubModule,ToFK_Account,IsAdvance);
                                            }
                                            else {

                                                edtTxtAmount.setKeyListener(null);
                                                edtTxtAmount.setEnabled(false);
                                                edtTxtAmount.setInputType(InputType.TYPE_NULL);
                                                edtTxtAmount.setFocusable(false);
                                            }
                                        }
//                                        else if (ToSubModule.equals("ODGD")) {
//                                            JSONObject DetailsjsonObject=jsonObjectjarray.getJSONObject(0);
//                                            Slimit =  DetailsjsonObject.getString("Value");
//                                            int limit=Integer.parseInt(Slimit);
//                                            if (limit > 0 ) {
//                                                ll_needTochange.setVisibility(View.VISIBLE);
//                                                edtTxtAmount.setKeyListener(null);
//                                                edtTxtAmount.setEnabled(false);
//                                                edtTxtAmount.setInputType(InputType.TYPE_NULL);
//                                                edtTxtAmount.setFocusable(false);
//                                                pendinginsa = new String[limit];
//
//
//                                                IsAdvance = "0";
//                                                int i;
//                                                for ( i = 0; i < limit; i++ )
//                                                {
//                                                    pendinginsa[i] =String.valueOf( i+1);
//                                                }
//                                                getpendinginsa(ToSubModule,ToFK_Account,IsAdvance);
//                                            }
//                                            else {
//                                                ll_needTochange.setVisibility(View.GONE);
//
//                                                edtTxtAmount.setKeyListener(null);
//                                                edtTxtAmount.setEnabled(false);
//                                                edtTxtAmount.setInputType(InputType.TYPE_NULL);
//                                                edtTxtAmount.setFocusable(false);
//                                            }
//                                        }
                                        else {
                                            ll_needTochange.setVisibility(View.GONE);
                                            ll_needToPayAdvance.setVisibility(View.GONE);
                                            ll_remittance.setVisibility(View.GONE);
                                            edtTxtAmount.setEnabled(true);
                                            edtTxtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            edtTxtAmount.setFocusable(true);
                                            edtTxtAmount.setFocusableInTouchMode(true);
                                        }
                                    }
                                    else{
                                        tv_availbal.setVisibility(View.GONE);
                                    }
                                }
                                else{
                                    tv_availbal.setVisibility(View.GONE);
                                }
                            }
                            else{
                                tv_availbal.setVisibility(View.GONE);

                                try{
                                    JSONObject jobj = jObject.getJSONObject("AccountDueDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
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

    private void getCustomerAccount(AlertDialog alertDialog, String value, String submodule) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
//                progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
//                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
//                progressDialog.setCancelable(false);
//                progressDialog.setIndeterminate(true);
//                progressDialog.setIndeterminateDrawable(this.getResources()
//                        .getDrawable(R.drawable.progress));
//                progressDialog.show();
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
                String  accountType = mAccountPayingToSpinner.getSelectedItem().toString();
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("10") );
                    requestObject1.put("CustomerNoumber",IScoreApplication.encryptStart(value) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(type) );
                    requestObject1.put("ModuleCode",IScoreApplication.encryptStart(submodule) );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1    761  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
//                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("BarcodeAgainstCustomerAccountDets");
                                JSONArray jarray = jobj.getJSONArray( "BarcodeAgainstCustomerAccountList");
                                if(jarray.length()!=0){
                                    for (int k = 0; k < jarray.length(); k++) {
                                        JSONObject jsonObject = jarray.getJSONObject(k);
                                        CustomerList.add(new BarcodeAgainstCustomerAccountList (jsonObject.getString("FK_Customer"),jsonObject.getString("CustomerName"),jsonObject.getString("AccountName"),jsonObject.getString("AccountNumber")));
                                    }
                                    if(jarray.length()==1){
                                        dataItem = CustomerList.get(0).getAccountNumber();
                                        mScannedValue = dataItem;
                                        displayAccountNumber(dataItem,alertDialog);

                                    }else {


                                        HashSet<BarcodeAgainstCustomerAccountList> hashSet = new HashSet<>(CustomerList);
                                        CustomerList.clear();
                                        CustomerList.addAll(hashSet);
                                        CustomListAdapter adapter = new CustomListAdapter(OwnAccountFundTransferActivity.this,CustomerList);
                                        list_view.setAdapter(adapter);
                                        list_view.setOnItemClickListener((parent, view, position, id) -> {
                                            dataItem = CustomerList.get(position).getAccountNumber();
                                            mScannedValue = dataItem;
                                            displayAccountNumber(dataItem,alertDialog);
                                        });

                                    }


                                }

                            }
                            else{
                                try{
                                    JSONObject jobj = jObject.getJSONObject("BarcodeAgainstCustomerAccountDets");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
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
                e.printStackTrace();
            }
        } else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");

            /*DialogUtil.showAlert(this,
                    "Network is currently unavailable. Please try again later.");*/
        }



    }

    private void displayAccountNumber(String data,AlertDialog alertDialog){

        if(data.trim().length() >= 14) {
            data = data.substring(0, 14);
        }


        if(data.startsWith("01")) {
            mAccountPayingToSpinner.setSelection(0);
        } else if(data.startsWith("02")) {
            mAccountPayingToSpinner.setSelection(1);
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

    private void showAlert(){
        if ( this == null )
            return;
        alertMessage1("Oops...", "Both account number and confirm account number are not matching");
       /* new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Both account number and confirm account number are not matching")
                .show();*/
    }

    private void startTransfer( String accountNo, String type, String receiverAccNo, String amount, String remark) {


        UserCredential loginCredential = UserCredentialDAO.getInstance().getLoginCredential();


        if (TextUtils.isEmpty(mScannedValue)) {
            mScannedValue = "novalue";
        }

        mScannedValue = mScannedValue.replaceAll(" ", "%20");

        Log.e(TAG,"accountNo  1455  "+accountNo);
        String accountType = accountNo.substring(accountNo.indexOf("(")+1, accountNo.indexOf(")"));

        /*Extract account number*/
        accountNo = accountNo.replace(accountNo.substring(accountNo.indexOf(" (") + 1, accountNo.indexOf(")") + 1), "");
        accountNo = accountNo.replace(" ", "");

//        AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accountNo);
//        String accountType = accountInfo.accountTypeShort;
        final String tempFromAccNo = accountNo +"("+ accountType +")";
        final String tempToAccNo = receiverAccNo +"("+ type +")";

        /*End of Extract account number*/


        Log.e(TAG,"accountNo  1465  "+accountType+"  "+tempFromAccNo+"  "+tempToAccNo);

        Log.e(TAG,"receiverAccNo  1273   "+receiverAccNo+"   "+accountNo+"   "+type+"    "+accountType);

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
        String BASE_URL=pref.getString("oldbaseurl", null);
        try{
            String url =
                    BASE_URL + "/FundTransferIntraBank?AccountNo="
                            + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountNo))
                            + "&Module=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType))
                            + "&ReceiverModule=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(type))
                            + "&ReceiverAccountNo=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(receiverAccNo.trim()))
                            + "&amount=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(amount.trim()))
                            + "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin))
                            + "&QRCode=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mScannedValue))
                            + "&Remark=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(remark));
            Log.e(TAG,"startTransfer   954     "+url);
            NetworkManager.getInstance().connector(url, new ResponseManager() {
                @Override
                public void onSuccess(String result) {
                    try{
                        Gson gson = new Gson();
                        OwnAccFundTransferFragment.FundTransferResult fundTransferResult = gson.fromJson( result, OwnAccFundTransferFragment.FundTransferResult.class );
                        int statusCode = fundTransferResult.getStatusCode();
                        reference = fundTransferResult.getRefId();
                        statusmessage= fundTransferResult.getStatusMessage();
                        if ( statusCode == 1 ){
                            ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();

                            KeyValuePair keyValuePair = new KeyValuePair();
                            keyValuePair.setKey("Ref. No");
                            keyValuePair.setValue( fundTransferResult.getRefId() );
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

                            alertMessage("", keyValuePairs, statusmessage, true, false);
                        }
                        else if ( statusCode == 2 ){
                            alertMessage1("" ,statusmessage );
                        }
                        else if ( statusCode == 3 ){
                            alertMessage1("", statusmessage);
                        }
                        else if ( statusCode == 4 ){
                                alertMessage1("", statusmessage);
                        }
                        else if ( statusCode == 5 ){
                              alertMessage1("", statusmessage);
                        }
                        else {
                             alertMessage1("",  statusmessage);
                        }
                    }
                    catch ( Exception e ){
                        alertMessage1("", "Something Went Wrong. Please Try Again Later....");
                    }
                }
                @Override
                public void onError(String error) {
                    alertMessage1("", "Something Went Wrong. Please Try Again Later....");
                }
            }, this, "Talking to server. Please wait....");
        }catch ( Exception e ){
            //Do nothing
        }

    }
    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OwnAccountFundTransferActivity.this);

        LayoutInflater inflater = OwnAccountFundTransferActivity.this.getLayoutInflater();
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
    private void alertMessage(String title, ArrayList<KeyValuePair> keyValueList, String message , boolean isHappy, boolean isBackButtonEnabled ){
        Log.e(TAG,"alertMessage   954   "+title+"   "+keyValueList+"   "+message+"   "+isHappy+"    "+isBackButtonEnabled);


        alertPopup(title,keyValueList,message,isHappy,isBackButtonEnabled);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.spn_account_num){

            Log.e(TAG,"SPINNN   spn_account_num  1042");
        }

        else if (adapterView.getId() == R.id.spn_account_type) {
            Log.e(TAG,"SPINNN   spn_account_type  1046");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class FundTransferResult{
        @SerializedName("StatusCode")
        private int statusCode;
        @SerializedName("RefID")
        private String refId;
        @SerializedName("MobileNumber")
        private String mobileNumber;
        @SerializedName("Amount")
        private String amount;
        @SerializedName("AccNumber")
        private String accNo;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAccNo() {
            return accNo;
        }

        public void setAccNo(String accNo) {
            this.accNo = accNo;
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
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput =  IScoreApplication.getAppContext().
                getAssets().open(Common.getCertificateAssetName());
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

   private void alertPopup(String title, ArrayList<KeyValuePair> keyValueList, String message, boolean isHappy, boolean isBackButtonEnabled) {
       Bundle bundle = new Bundle();
       bundle.putParcelableArrayList( KEY_VALUE, keyValueList );
       bundle.putString( TITLE, title );
       bundle.putBoolean( HAPPY, isHappy );
       bundle.putString( MESSAGE, message );

       AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
       LayoutInflater inflater = this.getLayoutInflater();
       View dialogView = inflater.inflate(R.layout.fragment_message_alert, null);
       dialogBuilder.setView(dialogView);

       // EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
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
       tvrefe.setText("Ref.No "+reference);

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
       Log.e(TAG,"CommonUtilities  945   "+CommonUtilities.getDecimelFormate(num));
       String stramnt = CommonUtilities.getDecimelFormate(num);


       tv_amount.setText("₹ " + stramnt );



       txtvAcntno.setText("A/C :"+SourceAccountNumber);
       txtvbranch.setText("Branch :"+BranchName);
       double num1 = Double.parseDouble(Balance) - Double.parseDouble(stramnt.replace(",",""));
      // double num1 = Double.parseDouble(Balance) - Double.parseDouble(stramnt);
       DecimalFormat fmt = new DecimalFormat("#,##,###.00");

       txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));

       txtvAcntnoto.setText("A/C : "+ mAccountPayingToSpinner.getSelectedItem().toString());
       txtvbranchto.setText("Branch :"+result);

       dialogView.findViewById( R.id.rltv_footer ).setOnClickListener( view1 -> {
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

       try{
//            Bundle bundle = getArguments();
//            boolean isHappy = bundle.getBoolean( HAPPY );
//            String title = bundle.getString( TITLE );
//            String message = bundle.getString( MESSAGE );
           txtMessage.setText( message );
           txtTitle.setText( title );
           if ( !isHappy ){
               imgIcon.setImageResource( R.mipmap.ic_failed );
           }
           //  ArrayList<KeyValuePair> keyValuePairs = bundle.getParcelableArrayList( KEY_VALUE );
     /*      ArrayList<KeyValuePair> keyValuePairs = keyValueList;
           SuccessAdapter successAdapter = new SuccessAdapter( keyValuePairs );
           RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
           mRecyclerView.setLayoutManager( layoutManager );
           mRecyclerView.setAdapter( successAdapter );*/


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



//            img_share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Log.e("img_share","img_share   1170   ");
//                    Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
//                            rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    rltv_share.draw(canvas);
//
//                    try {
//
//
//                        Uri bmpUri = getLocalBitmapUri(bitmap);
//
//                        Intent shareIntent = new Intent();
//                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                        shareIntent.setType("image/*");
//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(shareIntent, "Share"));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("Exception","Exception   117   "+e.toString());
//                    }
//
//                }
//            });




       }catch ( Exception e){
           //Do nothing
       }

       AlertDialog alertDialog = dialogBuilder.create();
       alertDialog.show();
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
