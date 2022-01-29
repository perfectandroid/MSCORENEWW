package com.creativethoughts.iscore;


import android.Manifest;
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
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.CustomListAdapter;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.custom_alert_dialogs.SuccessAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.model.BarcodeAgainstCustomerAccountList;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherAccFundTransferFragment extends Fragment implements View.OnClickListener, EditText.OnEditorActionListener,
        View.OnFocusChangeListener, TextWatcher,AdapterView.OnItemSelectedListener{

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

    private EditText edtTxtAccountNoFirstBlock;
    private EditText edtTxtAccountNoSecondBlock;
    private EditText edtTxtAccountNoThirdBlock;

    private EditText edtTxtConfirmAccountNoFirstBlock;
    private EditText edtTxtConfirmAccountNoSecondBlock;
    private EditText edtTxtConfirmAccountNoThirdBlock;

    private EditText et_othr_acc_details;
    String MaximumAmount = "";
    private EditText edtTxtAmount;
    private EditText edt_txt_remark;
    private String token,cusid,dataItem;
    ListView list_view;
    TextView tv_popuptitle,tv_maxamount;
    private ArrayList<BarcodeAgainstCustomerAccountList>CustomerList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private static final String KEY_VALUE = "keyvalue";
    private static final String TITLE = "title";
    private static final String HAPPY = "happy";
    private static final String MESSAGE = "message";

    public OtherAccFundTransferFragment() {
        // Required empty public constructor
    }

    public static OtherAccFundTransferFragment newInstance() {

        return new OtherAccFundTransferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.other_acc_fund_transferfragment, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mAccountSpinner = view.findViewById(R.id.spn_account_num);
        mAccountTypeSpinner = view.findViewById(R.id.spn_account_type);
        tv_maxamount = view.findViewById(R.id.tv_maxamount);

        mAccountNumberEt = view.findViewById(R.id.account_number);
        mConfirmAccountNumberEt = view.findViewById(R.id.confirm_account_number);

        edtTxtAccountNoFirstBlock = view.findViewById(R.id.acc_no_block_one);
        edtTxtAccountNoSecondBlock = view.findViewById(R.id.acc_no_block_two);
        edtTxtAccountNoThirdBlock = view.findViewById(R.id.acc_no_block_three);

        edtTxtAccountNoFirstBlock.setOnEditorActionListener(this);
        edtTxtAccountNoSecondBlock.setOnEditorActionListener(this);
        edtTxtAccountNoThirdBlock.setOnEditorActionListener(this);

        edtTxtAccountNoFirstBlock.setOnFocusChangeListener(this);
        edtTxtAccountNoSecondBlock.setOnFocusChangeListener(this);
        edtTxtAccountNoThirdBlock.setOnFocusChangeListener(this);

        edtTxtAccountNoFirstBlock.addTextChangedListener(this);
        edtTxtAccountNoSecondBlock.addTextChangedListener(this);
        edtTxtAccountNoThirdBlock.addTextChangedListener(this);

        edtTxtConfirmAccountNoFirstBlock = view.findViewById(R.id.confirm_acc_no_block_one);
        edtTxtConfirmAccountNoSecondBlock = view.findViewById(R.id.confirm_acc_no_block_two);
        edtTxtConfirmAccountNoThirdBlock = view.findViewById(R.id.confirm_acc_no_block_three);

        edtTxtConfirmAccountNoFirstBlock.setOnEditorActionListener(this);
        edtTxtConfirmAccountNoSecondBlock.setOnEditorActionListener(this);
        edtTxtConfirmAccountNoThirdBlock.setOnEditorActionListener(this);

        edtTxtConfirmAccountNoFirstBlock.setOnFocusChangeListener(this);
        edtTxtConfirmAccountNoSecondBlock.setOnFocusChangeListener(this);
        edtTxtConfirmAccountNoThirdBlock.setOnFocusChangeListener(this);

        edtTxtConfirmAccountNoFirstBlock.addTextChangedListener(this);
        edtTxtConfirmAccountNoSecondBlock.addTextChangedListener(this);
        edtTxtConfirmAccountNoThirdBlock.addTextChangedListener(this);

        edtTxtAmount = view.findViewById(R.id.edt_txt_amount);
        edt_txt_remark = view.findViewById(R.id.edt_txt_remark);

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

        mAmountEt = view.findViewById(R.id.amount);
        mMessageEt = view.findViewById(R.id.message);

        button = view.findViewById(R.id.btn_submit);
        btn_clear = view.findViewById(R.id.btn_clear);
        button.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        Button btnScanAccounttNo = view.findViewById(R.id.btn_scan_acnt_no);
        btnScanAccounttNo.setOnClickListener(this);

        Button scan = view.findViewById(R.id.scan);
        scan.setOnClickListener(this);


        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token =  loginCredential.token;
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
        setAccountType();

        getminTransAmount();
        return view;
    }

    private void getminTransAmount() {

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("18") );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1    761  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getFundTransferLimit(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("FundTransferLimit");
                                MaximumAmount = jobj.getString("MaximumAmount");
                                tv_maxamount.setText("Transfer upto ₹ " + MaximumAmount + " instantly.");
                            }
                            else {
                                tv_maxamount.setVisibility(View.GONE);
//                                try{
//                                    JSONObject jobj = jObject.getJSONObject("FundTransferLimit");
//                                    String ResponseMessage = jobj.getString("ResponseMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                                    builder.setMessage(ResponseMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }
//                                catch (JSONException e){
//                                    String EXMessage = jObject.getString("EXMessage");
//                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    android.app.AlertDialog alert = builder.create();
//                                    alert.show();
//
//                                }
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
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
        }



    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            int id;
            //noinspection ConstantConditions
            id = getActivity().getCurrentFocus().getId();
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
                new ArrayAdapter< >(getActivity(), R.layout.simple_spinner_item_dark, accountSpinnerItems);
        mAccountSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

    private void setAccountType() {
        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();

        String customerId = null;
        if (settingsModel == null) {
            customerId = null;
        } else {
            customerId = settingsModel.customerId;
        }
        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.savings_bank));
        items.add(getString(R.string.current_account));
        items.add(getString(R.string.cash_credit));
        items.add(getString(R.string.member_loan));
        items.add(getString(R.string.recurring_deposit));
        items.add(getString(R.string.jewell_loan));
        items.add(getString(R.string.gds));

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

//        ArrayAdapter<String> spinnerAdapter =
//                new ArrayAdapter< >(getActivity(), R.layout.simple_spinner_item_dark, accountSpinnerItems);
//        mAccountSpinner.setAdapter(spinnerAdapter);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if ( getActivity() == null )
            return;

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_dark, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAccountTypeSpinner.setAdapter(spinnerAdapter);
        mAccountTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if ( getActivity() == null || getContext() == null )
            return;
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
                setAccountNumber();
                setAccountType();

                getminTransAmount();

                break;
            case R.id.scan:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    startActivityForResult(intent, 100);
                }

                break;
            case R.id.btn_scan_acnt_no:
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            ZXING_CAMERA_PERMISSION);
                }else {
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            default:break;
        }
    }
    private void submit(){
        if ( getContext() == null )
            return;
        String recieverAccountNo = confirmAndSetRecieversAccountNo();

////        String  accountType = mAccountTypeSpinner.getSelectedItem().toString();
//        String recieverAccountNo  = accountType.substring(0,12);
//        Log.e(TAG,"recieverAccountNo   619   "+recieverAccountNo);

        if (isValid() && recieverAccountNo.length() == 12) {
            if (NetworkUtil.isOnline()) {
                final String accountNumber = mAccountSpinner.getSelectedItem().toString();

                final String amount = edtTxtAmount.getText().toString();
                String remark = edt_txt_remark.getText().toString();
                String fromAccountNo  = accountNumber.substring(0,12);
                final String accNumber = recieverAccountNo;
//                String type = accountType.substring(accountType.indexOf("(")+1, accountType.indexOf(")"));

                final String[] type = {""};

//                if (!accountNumber.equals(accountType)){

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.confirmation_msg_popup, null);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setView(dialogView);


                String amnt = edtTxtAmount.getText().toString().replaceAll(",", "");
                TextView text_confirmationmsg = dialogView.findViewById(R.id.text_confirmationmsg);
                TextView tv_amount = dialogView.findViewById(R.id.tv_amount);
                TextView tv_amount_words = dialogView.findViewById(R.id.tv_amount_words);
                Button butOk = dialogView.findViewById(R.id.btnOK);
                Button butCan = dialogView.findViewById(R.id.btnCncl);

                String stramnt = CommonUtilities.getDecimelFormate(Double.parseDouble(amnt));

                text_confirmationmsg.setText("Proceed Transaction with above receipt amount to A/C no " + accNumber + " ..?");
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

//        if (amnt.contains(".")){
//            int domain = amnt.lastIndexOf(".");
//            if (amnt.length() == 0){
//                tv_amount.setText("₹ " + amnt + "00");
//            }
//            else if (amnt.length() == 1){
//                tv_amount.setText("₹ " + amnt + "0");
//            }
//            else{
//                tv_amount.setText("₹ " + amnt);
//            }
//        }
//        else{
//            tv_amount.setText("₹ " + amnt + ".00");
//        }

                tv_amount.setText("₹ " + stramnt );

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                butOk.setOnClickListener(v -> {

                    alertDialog.dismiss();

                    String  accountType = mAccountTypeSpinner.getSelectedItem().toString();
//
//                                Log.e(TAG,"result   635    "+type);
                    //    final String type;

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
                    startTransfer( accountNumber, type[0], accNumber, amount,remark);
                });

                butCan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                // HIDE 09.07.2021
//                    new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
//                            .setTitleText("Are you sure?")
//                            .setContentText("Do you want to transfer INR " + amount + " to A/C no " + accNumber + " ..?")
//                            .setCancelText("No,cancel pls!")
//                            .setConfirmText("Ok!")
//                            .showCancelButton(true)
//                            .setCustomImage(R.mipmap.fund_sure)
//                            .setCancelClickListener(SweetAlertDialog::cancel)
//                            .setConfirmClickListener(sDialog -> {
//                                sDialog.dismissWithAnimation();
//                            String  accountType = mAccountTypeSpinner.getSelectedItem().toString();
////
////                                Log.e(TAG,"result   635    "+type);
//                                //    final String type;
//
//                            if (accountType.equalsIgnoreCase(getString(R.string.savings_bank))) {
//                                type[0] = "SB";
//                            } else if (accountType.equalsIgnoreCase( getString(R.string.current_account) )) {
//                                type[0] = "CA";
//                            }
//                            else if (accountType.equalsIgnoreCase( getString(R.string.cash_credit) )) {
//                                type[0] = "OD";
//                            }
//                            else if (accountType.equalsIgnoreCase( getString(R.string.member_loan) )) {
//                                type[0] = "ML";
//                            }
//                            else if (accountType.equalsIgnoreCase( getString(R.string.recurring_deposit) )) {
//                                type[0] = "RD";
//                            }
//                            else if (accountType.equalsIgnoreCase( getString(R.string.jewell_loan) )) {
//                                type[0] = "JL";
//                            }
//                            else {
//                                type[0] = "GD";
//                            }
//
//                                Log.e(TAG,"startTransfer  664   "+accountNumber+"   "+ type[0] +"   "+accNumber+"   "+amount);
//
//                                //new FundTransferAsyncTask(accountNumber, type, accNumber, amount).execute();
//                                startTransfer( accountNumber, type[0], accNumber, amount,remark);
//                            })
//                            .show();

                // HIDE 09.07.2021



//                }
//            else {
////                    DialogUtil.showAlert(getActivity(),
////                            "Account Number are Same");
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setMessage("Account Number are Same")
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//
//                                }
//                            });
//
//                    //Creating dialog box
//                    AlertDialog alert = builder.create();
//
//                    alert.show();
//                }



            } else {
                DialogUtil.showAlert(getActivity(),
                        "Network is currently unavailable. Please try again later.");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){


        if ( requestCode == ZXING_CAMERA_PERMISSION ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivityForResult(intent, 100);
            }else {
                Toast.makeText(getContext(), "App need permission for use camera to scan account number", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValid() {

        String amount = edtTxtAmount.getText().toString();

        if (MaximumAmount != ""){

            if (amount.length() <1)
                return false;
            final double amt = Double.parseDouble(amount);
            final double MaximumAmountD = Double.parseDouble(MaximumAmount);

            if(amt < 1 || amt > MaximumAmountD) {

                edtTxtAmount.setError("Please enter amount between 0 and " + MaximumAmountD + ".");
                return false;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("10") );
                    requestObject1.put("CustomerNoumber",IScoreApplication.encryptStart(value) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token));
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(type) );
                    requestObject1.put("ModuleCode",IScoreApplication.encryptStart(submodule) );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid));
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
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
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
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
                                        CustomListAdapter adapter = new CustomListAdapter(getContext(),CustomerList);
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

                                }catch (JSONException e){
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
            DialogUtil.showAlert(getContext(),
                    "Network is currently unavailable. Please try again later.");
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
        if ( getContext() == null )
            return;
        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Both account number and confirm account number are not matching")
                .show();
    }


    private void startTransfer( String accountNo, String type, String receiverAccNo, String amount, String remark) {


        UserCredential loginCredential = UserCredentialDAO.getInstance().getLoginCredential();


        if (TextUtils.isEmpty(mScannedValue)) {
            mScannedValue = "novalue";
        }

        mScannedValue = mScannedValue.replaceAll(" ", "%20");

        /*Extract account number*/
        accountNo = accountNo.replace(accountNo.substring(accountNo.indexOf(" (") + 1, accountNo.indexOf(")") + 1), "");
        accountNo = accountNo.replace(" ", "");

        AccountInfo accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accountNo);
        String accountType = accountInfo.accountTypeShort;
        final String tempFromAccNo = accountNo +"("+ accountType +")";
        final String tempToAccNo = receiverAccNo +"("+ type +")";
        /*End of Extract account number*/



        try{
//            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
//            String BASE_URL=pref.getString("oldbaseurl", null);

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
            String BASE_URL=pref.getString("baseurl", null);

            String url =
                    BASE_URL + "/FundTransferIntraBank?AccountNo="
                            + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountNo))
                            + "&Module=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(accountType))
                            + "&ReceiverModule=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(type))
                            + "&ReceiverAccountNo=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(receiverAccNo.trim()))
                            + "&amount=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(amount.trim()))
                            + "&Pin=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(loginCredential.pin))
                            + "&QRCode=" + IScoreApplication.encodedUrl(IScoreApplication.encryptStart(mScannedValue));
            Log.e(TAG,"startTransfer   954     "+url);
            NetworkManager.getInstance().connector(url, new ResponseManager() {
                @Override
                public void onSuccess(String result) {
                    try{
                        Gson gson = new Gson();
                        FundTransferResult fundTransferResult = gson.fromJson( result, FundTransferResult.class );
                        int statusCode = fundTransferResult.getStatusCode();
                        if ( statusCode == 1 ){
                            ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();

                            KeyValuePair keyValuePair = new KeyValuePair();
                            keyValuePair.setKey("Ref. No");
                            keyValuePair.setValue( fundTransferResult.getRefId() );
                            keyValuePairs.add( keyValuePair );

                            keyValuePair = new KeyValuePair();
                            keyValuePair.setKey("Amount");
                            keyValuePair.setValue( fundTransferResult.getAmount() );
                            keyValuePairs.add( keyValuePair );

                            keyValuePair = new KeyValuePair();
                            keyValuePair.setKey("From Acc.No");
                            keyValuePair.setValue( tempFromAccNo );
                            keyValuePairs.add( keyValuePair );

                            keyValuePair = new KeyValuePair();
                            keyValuePair.setKey("To Acc.No");
                            keyValuePair.setValue( tempToAccNo );
                            keyValuePairs.add( keyValuePair );

                            alertMessage("Success...!", keyValuePairs, "Successfully transferred the amount", true, false);
                        }
                        else if ( statusCode == 2 ){
                            alertMessage("Oops....!", new ArrayList<>(), "Transaction Failed, Please try again later", false, false);
                        }
                        else if ( statusCode == 3 ){
                            alertMessage("Transaction failed...!", new ArrayList<>(), "Invalid QR code", false, false);
                        }
                        else if ( statusCode == 4 ){
                            alertMessage("Transaction failed...!", new ArrayList<>(), "Invalid Account number", false, false);
                        }
                        else if ( statusCode == 5 ){
                            alertMessage("Transaction failed...!", new ArrayList<>(), "Does Not have sufficient balance to Transfer", false, false);
                        }
                        else {
                            alertMessage("Transaction failed...!", new ArrayList<>(), "Please try again later", false, false);
                        }
                    }catch ( Exception e ){

                        alertMessage("Transaction failed...!", new ArrayList<>(), "Please try again later", false, false);

                    }
                }

                @Override
                public void onError(String error) {
                    alertMessage("Transaction failed...!", new ArrayList<>(), "Please try again later", false, false);
                }
            }, getActivity(), "Talking to server. Please wait....");
        }catch ( Exception e ){
            //Do nothing
        }

    }
    private void alertMessage(String title, ArrayList<KeyValuePair> keyValueList, String message , boolean isHappy, boolean isBackButtonEnabled ){
        Log.e(TAG,"alertMessage   954   "+title+"   "+keyValueList+"   "+message+"   "+isHappy+"    "+isBackButtonEnabled);

//                getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
//                isHappy, isBackButtonEnabled ) ).commit();


        alertPopup(title,keyValueList,message,isHappy,isBackButtonEnabled);


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


    public class FundTransferResult{
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

        @SerializedName("StatusMessage")
        private String statusMessage;

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



        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(),HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

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

    private void alertPopup(String title, ArrayList<KeyValuePair> keyValueList, String message, boolean isHappy, boolean isBackButtonEnabled) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList( KEY_VALUE, keyValueList );
        bundle.putString( TITLE, title );
        bundle.putBoolean( HAPPY, isHappy );
        bundle.putString( MESSAGE, message );

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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

        dialogView.findViewById( R.id.rltv_footer ).setOnClickListener( view1 -> {
            try{
//                getFragmentManager().beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY") )
//                        .commit();
                Intent i=new Intent(getContext(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();
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
            ArrayList<KeyValuePair> keyValuePairs = keyValueList;
            SuccessAdapter successAdapter = new SuccessAdapter( keyValuePairs );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
            mRecyclerView.setLayoutManager( layoutManager );
            mRecyclerView.setAdapter( successAdapter );
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
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
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
