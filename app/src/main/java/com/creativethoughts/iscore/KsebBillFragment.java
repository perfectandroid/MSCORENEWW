package com.creativethoughts.iscore;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment;
import com.creativethoughts.iscore.custom_alert_dialogs.AlertMessageFragment1;
import com.creativethoughts.iscore.custom_alert_dialogs.KeyValuePair;
import com.creativethoughts.iscore.db.dao.KsebBillDAO;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.SettingsDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.KsebBillModel;
import com.creativethoughts.iscore.db.dao.model.SettingsModel;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.kseb.KsebRechargeStatus;
import com.creativethoughts.iscore.kseb.KsebSectionSelectionActivity;
import com.creativethoughts.iscore.kseb.SectionDetails;
import com.creativethoughts.iscore.neftrtgs.PaymentModel;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.ConnectionUtil;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
import com.creativethoughts.iscore.utility.Validation;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KsebBillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class KsebBillFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_SELECT_SECTION = 100;
    private String sectionCode = "";
    private Spinner accountNumberSelector;
    private TextView txtMobileNoErrorMessage;
    private TextView txtConsumerNameErrorMessage;
    private TextView txtConsumerNoErrorMessage;
    private TextView txtBillNoErrorMessage;
    private TextView txtAmountErrorMessage;
    private TextView txtViewName;
    private TextView txtViewMobileNo;
    private TextView txtViewConsumerNo;
    private TextView txtViewSection;
    private TextView txtViewBillNo;
    private TextView txtViewAmount,txt_amtinword;
    private TextView txtViewAccountNo;
    private TextView txtSectionName;
    String tempDisplaySection;
    private EditText edtTxtBillNo;
    private EditText edtTxtAmount;
    private AutoCompleteTextView autoCompleConsumerName;
    private AutoCompleteTextView autoCompleMobileNumber;
    private AutoCompleteTextView autoCompleConsumerNo;
    private LinearLayout mLinearForm;
    private String tempStringMobileNumber;
    private String tempStringConsumerName;
    private String tempStringConsumerNo;
    private String tempStringSectionList;
    private String tempStringBillNo;
    private String tempStringAmount;
    public KsebRechargeStatus ksebRechargeStatus;
    private SweetAlertDialog mSweetAlertDialog;
    String from ="KSEB";
    private static final String BUNDLE_PAYMENT_MODEL = "payment_model";
    String statusmessage;
    private String tempStringAccountNo,BranchName ;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();

    public KsebBillFragment() {
        // Required empty public constructor
    }


    public static KsebBillFragment newInstance() {
        KsebBillFragment fragment = new KsebBillFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach( context );

        Bundle bundle = getArguments();
        if ( bundle != null ){
           // mPaymentModel = bundle.getParcelable(BUNDLE_PAYMENT_MODEL);

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //String s =R.string.kseb_bill_expiry_alert
            alertMessage1("", "Please check the expiry date of the bill");
            //showAlertDialogue(getString(R.string.kseb_bill_expiry_alert));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView               = inflater.inflate(R.layout.fragment_kseb_bill, container, false);
        addViewForm(rootView);
        addViewToBillData(rootView);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


        AppCompatActivity activity  = (AppCompatActivity) getActivity();
        if ( activity != null && activity.getSupportActionBar() != null ){
            ActionBar actionBar = activity.getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(getString(R.string.kseb_title_action_bar));

            getAccountNumber();
        }
        rootView.findViewById( R.id.lnr_lyt_select_section).setOnClickListener( this );
        return rootView;
    }


    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* Enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            // Handle the back button event


            Intent i = new Intent(getActivity(),HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }


    };
    private void addViewForm(View rootView){

        mLinearForm                 =   rootView.findViewById(R.id.form_scroll_view);
        txtMobileNoErrorMessage     =  rootView.findViewById(R.id.mobile_no_error);
        txtConsumerNameErrorMessage =   rootView.findViewById(R.id.name_error);
        txtConsumerNoErrorMessage   =   rootView.findViewById(R.id.consumer_no_error);
        txtBillNoErrorMessage       =   rootView.findViewById(R.id.bill_no_error);
        txtAmountErrorMessage       =  rootView.findViewById(R.id.amount_error);
        txt_amtinword               =  rootView.findViewById(R.id.txt_amtinword);


        accountNumberSelector       =     rootView.findViewById(R.id.account_number_selector_spinner);
        txtSectionName              = rootView.findViewById( R.id.txt_section_name );
        Button proceedToPayButton   =   rootView.findViewById(R.id.proceedToPay);
        proceedToPayButton.setOnClickListener(this);
        Button edit                 =   rootView.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        Button pay                  =  rootView.findViewById(R.id.pay);
        pay.setOnClickListener(this);
        Button btnClearAll          =   rootView.findViewById(R.id.clear_all);
        btnClearAll.setOnClickListener(this);

        autoCompleConsumerName      =  rootView.findViewById(R.id.consumer_name);
        autoCompleConsumerName.setSingleLine(true);
        edtTxtBillNo                =   rootView.findViewById(R.id.bill_no);
        edtTxtBillNo.setSingleLine(true);
        edtTxtAmount                =   rootView.findViewById(R.id.amount);

        autoCompleMobileNumber      =   rootView.findViewById(R.id.mobile_no);
        autoCompleMobileNumber.setSingleLine(true);
        autoCompleConsumerNo        =  rootView.findViewById(R.id.consumer_no);
        autoCompleConsumerNo.setSingleLine(true);


        /*Add textchangelistener to autocomplete textview*/
        addTextChange(autoCompleMobileNumber, rootView, R.id.mobile_no, "mobile_no");
        addTextChange(autoCompleConsumerNo, rootView, R.id.consumer_no, "consumer_no");
        addTextChange(autoCompleConsumerName, rootView, R.id.consumer_name, "consumer_name");

        autoCompleConsumerName.setOnItemClickListener((p, v, pos, id) -> autoSelect(autoCompleConsumerName.getText().toString()));

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
                        proceedToPayButton.setText( "PAY  "+"\u20B9 "+CommonUtilities.getDecimelFormate(num));
                    }
                    else{
                        proceedToPayButton.setText( "PAY");
                    }
                }
                catch(NumberFormatException e)
                {

                }
            }
        });
    }
    private void addViewToBillData(View rootView){
        txtViewName = rootView.findViewById(R.id.view_name);
        txtViewMobileNo = rootView.findViewById(R.id.view_phone);
        txtViewConsumerNo = rootView.findViewById(R.id.view_consumer_no);
        txtViewSection = rootView.findViewById(R.id.view_section);
        txtViewBillNo = rootView.findViewById(R.id.view_bill_);
        txtViewAmount = rootView.findViewById(R.id.view_amount);
        txtViewAccountNo = rootView.findViewById(R.id.view_ac_number);
    }
    private void showAlertDialogue(String message){
        if ( getActivity() != null ){
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) ->
                //Action
                dialog.dismiss()

           );
            alertDialogBuilder.show();
        }
    }
    private void showAlertDialogue(String title, String message, final int status){
        if ( getContext() == null )
            return;
        if(status ==1){
            alertMessage1(title ,message);
           /* new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(message)
                    .setConfirmClickListener(sDialog -> {
                        Fragment fragment = new HomeFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        if ( fragmentManager != null ){
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, fragment);
                            fragmentTransaction.addToBackStack("Kseb");
                            fragmentTransaction.commit();
                        }
                        sDialog.dismiss();
                    })
                    .show();*/
        }
        else{

            alertMessage2(title ,message);
         /*   new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(title)
                    .setContentText(message)
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();*/
        }
    }

    private void getAccountNumber(){
        SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
        if ( settingsModel != null )
            getAccList();
//            settingAccountNumber(settingsModel.customerId);
    }
//    private void settingAccountNumber(String customerId){
//        //noinspection ObjectEqualsNull
//        CommonUtilities.transactionActivitySetAccountNumber(customerId, accountNumberSelector, getActivity());
//
//    }
    private void proceedPay() {
        if ( getContext() == null )
            return;
        tempStringAccountNo = accountNumberSelector.getSelectedItem().toString();

        tempStringConsumerName = autoCompleConsumerName.getText().toString();
        tempStringMobileNumber = autoCompleMobileNumber.getText().toString();
        tempStringConsumerNo = autoCompleConsumerNo.getText().toString();
        String txt_section_name = txtSectionName.getText().toString();
        tempStringBillNo = edtTxtBillNo.getText().toString();
        tempStringAmount = edtTxtAmount.getText().toString();

        if (tempStringConsumerName.length() == 0) {
                autoCompleConsumerName.setError("Please Enter Valid Name.");
        }
        else if(tempStringMobileNumber == null || tempStringMobileNumber.length() != 10){

            autoCompleMobileNumber.setError("Please Enter Valid 10 Digit Mobile Number.");
        }
        else if (tempStringConsumerNo.length() < 8 || tempStringConsumerNo.length()>16) {
            autoCompleConsumerNo.setError("Please Enter Correct Consumer Number.");
        }
        else if(sectionCode.equals("")) {
            txtSectionName.setTextColor( ContextCompat.getColor( getContext(), R.color.FireBrick ) );
            Toast.makeText(getActivity(), "Please Select Section Name", Toast.LENGTH_SHORT).show();

        }
        else if(tempStringBillNo.length() == 0) {
            edtTxtBillNo.setError("Please Enter Valid Bill Number.");
        }
        else if(tempStringAmount.length() == 0) {
            boolean isValidInteger = false;
            try{
                Float.parseFloat(tempStringAmount);
                isValidInteger = true;
            }catch (NumberFormatException e){
            }
            if(tempStringAmount.isEmpty() || isValidInteger == false)
                edtTxtAmount.setError("Please Enter Amount.");
        }
        else {
            ksebConfirmation(tempStringAccountNo,tempStringConsumerName,
                    tempStringMobileNumber,tempStringConsumerNo,txt_section_name,tempStringBillNo,tempStringAmount);
        }
    }
    private void ksebConfirmation(String tempStringAccountNo,String tempStringConsumerName,
                                  String tempStringMobileNumber,String tempStringConsumerNo, String txt_section_name,
                                  String tempStringBillNo, String tempStringAmount ) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getApplicationContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.kseb_confirmation_popup, null);
            TextView tvAcntno =  layout.findViewById(R.id.tvAcntno);
            TextView tvbranch =  layout.findViewById(R.id.tvbranch);
            TextView tv_con_name =  layout.findViewById(R.id.tv_con_name);
            TextView tv_con_mob_numb =  layout.findViewById(R.id.tv_con_mob_numb);
            TextView tv_con_num =  layout.findViewById(R.id.tv_con_num);
            TextView tv_con_section =  layout.findViewById(R.id.tv_con_section);
            TextView tv_bill_num =  layout.findViewById(R.id.tv_bill_num);

            TextView tv_amount =  layout.findViewById(R.id.tv_amount);
            TextView tv_amount_words =  layout.findViewById(R.id.tv_amount_words);
            TextView text_confirmationmsg =  layout.findViewById(R.id.text_confirmationmsg);
            TextView bt_ok =  layout.findViewById(R.id.bt_ok);
            TextView bt_cancel =  layout.findViewById(R.id.bt_cancel);
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();

            tvAcntno.setText(""+tempStringAccountNo);
            tvbranch.setText(BranchName);
            tv_con_name.setText(""+tempStringConsumerName);
            tv_con_mob_numb.setText(""+tempStringMobileNumber);
            tv_con_num.setText("Consumer No : "+tempStringConsumerNo);
            tv_con_section.setText("Consumer Section : "+txt_section_name);
            tv_bill_num.setText("Bill NO : "+tempStringBillNo);


            double num =Double.parseDouble(""+tempStringAmount);
            String stramnt = CommonUtilities.getDecimelFormate(num);
            text_confirmationmsg.setText("Proceed Recharge With Above Amount"+ "..?");
            String[] netAmountArr = tempStringAmount.split("\\.");
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
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pay();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedToPay() {
        if ( getContext() == null )
            return;
        String tempMobileNoErrorMessage;
        String tempConsumerNameErrorMessage;
        String tempAmountErrorMessage;
        String tempBillNoErrorMessage;
        String tempConsumerNoErrroMessage;

        int validationCount = 0;
        tempStringMobileNumber = autoCompleMobileNumber.getText().toString();
        tempStringConsumerName = autoCompleConsumerName.getText().toString();
        tempStringConsumerNo = autoCompleConsumerNo.getText().toString();
        tempStringBillNo = edtTxtBillNo.getText().toString();
        tempStringAmount = edtTxtAmount.getText().toString();
        tempStringAccountNo = accountNumberSelector.getSelectedItem().toString();

        tempMobileNoErrorMessage = Validation.mobileNumberValidator(tempStringMobileNumber);
        tempConsumerNameErrorMessage = Validation.nameValidation(tempStringConsumerName);
        tempAmountErrorMessage = Validation.amount(tempStringAmount);
        tempConsumerNoErrroMessage = Validation.consumerNoValidation(tempStringConsumerNo);
        tempBillNoErrorMessage = Validation.billNoValidator(tempStringBillNo);

        this.txtMobileNoErrorMessage.setText(tempMobileNoErrorMessage);
        this.txtConsumerNameErrorMessage.setText(tempConsumerNameErrorMessage);
        this.txtAmountErrorMessage.setText(tempAmountErrorMessage);
        this.txtBillNoErrorMessage.setText(tempBillNoErrorMessage);
        txtConsumerNoErrorMessage.setText(tempConsumerNoErrroMessage);
        autoCompletesetLineColor(autoCompleConsumerName, R.color.text_tertary);
        autoCompletesetLineColor(autoCompleConsumerNo, R.color.text_tertary);
        autoCompletesetLineColor(autoCompleMobileNumber, R.color.text_tertary);
        editTextSetLineColor(edtTxtAmount, R.color.text_tertary);
        editTextSetLineColor(edtTxtBillNo, R.color.text_tertary);

        if(!tempConsumerNameErrorMessage.equals("")){
            validationCount++;

            autoCompletesetLineColor(autoCompleConsumerName, R.color.error);
        }
        if(!tempMobileNoErrorMessage.equals("")){
            validationCount++;
            autoCompletesetLineColor(autoCompleMobileNumber, R.color.error);
        }
        if(!tempConsumerNoErrroMessage.equals("")){
            validationCount++;
            autoCompletesetLineColor(autoCompleConsumerNo, R.color.error);
        }
        if(!tempAmountErrorMessage.equals("")){
            validationCount++;
            editTextSetLineColor(edtTxtAmount, R.color.error);
        }
        if(!tempBillNoErrorMessage.equals("")){
            validationCount++;
            editTextSetLineColor(edtTxtBillNo, R.color.error);
        }
        if(!Validation.sectionCodeValidator(sectionCode).equals("")){
            validationCount++;
            txtSectionName.setTextColor( ContextCompat.getColor( getContext(), R.color.error ) );
        }

        if(validationCount == 0){
            hideFormShowDetails();
        }

        txtViewMobileNo.setText(tempStringMobileNumber);
        txtViewName.setText(tempStringConsumerName);
        txtViewConsumerNo.setText(tempStringConsumerNo);
        txtViewSection.setText(tempStringSectionList);
        txtViewBillNo.setText(tempStringBillNo);
        txtViewAmount.setText(tempStringAmount);
        txtViewAccountNo.setText(tempStringAccountNo);

    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.proceedToPay:
                proceedPay();
                break;
            case R.id.edit:
                hideDetailsShowForm();
                break;
//            case R.id.pay:
//                pay();
//                break;
            case R.id.clear_all:
                clearAll();
                break;
            case R.id.lnr_lyt_select_section:
                requestForSearchList();
                break;
            default:
                break;

        }
    }
    private void requestForSearchList(){
        Intent intent = new Intent( getContext(), KsebSectionSelectionActivity.class );
        startActivityForResult( intent, REQUEST_SELECT_SECTION );
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == REQUEST_SELECT_SECTION && resultCode == RESULT_OK ){
            Bundle bundle = data.getExtras();
            try{
                if ( bundle != null ){
                    SectionDetails sectionDetails = bundle.getParcelable( getString( R.string.kseb_section_list ) );
                    if ( sectionDetails != null ){
                        tempDisplaySection = sectionDetails.getSectionName() + '(' + sectionDetails.getSectionCode() + ')';
                        txtSectionName.setText( tempDisplaySection );
                        sectionCode = sectionDetails.getSectionCode();
                        tempStringSectionList = tempDisplaySection;
                    }
                }
                else {
                    Toast.makeText( getContext(), "Error occured", Toast.LENGTH_SHORT ).show();
                }
            }catch ( Exception e ){
                if ( IScoreApplication.DEBUG )
                    Log.e("exc section ", e.toString() );
            }
        }
    }

    private void hideFormShowDetails(){
        mLinearForm.setVisibility(View.GONE);

    }
    private void hideDetailsShowForm(){
        mLinearForm.setVisibility(View.VISIBLE);
    }


    private void clearAll(){
        Context context = getContext();
        if ( context == null )
            return;
        autoCompleConsumerName.setText("");
        edtTxtBillNo.setText("");
        edtTxtAmount.setText("");
        autoCompleMobileNumber.setText("");
        autoCompleConsumerNo.setText("");
        sectionCode = "";
        txtSectionName.setText(getString( R.string.select_section_name ));
        txtSectionName.setTextColor(ContextCompat.getColor( context, R.color.black_75_per));
        txtMobileNoErrorMessage.setText("");
        txtConsumerNameErrorMessage.setText("");
        txtConsumerNoErrorMessage.setText("");
        txtBillNoErrorMessage.setText("");
        txtAmountErrorMessage.setText("");
        txt_amtinword.setText("");
    }
    private void addAdapterToAutoComplteView(View rootView, int id, String column){
        if ( getContext() == null )
            return;
        ArrayAdapter<String> adapter;
        AutoCompleteTextView actv;
        if ( getContext() != null ){

                adapter = new ArrayAdapter<>
                        (getContext(), android.R.layout.select_dialog_item, KsebBillDAO.getInstance().getListFromDb(column));
                actv= rootView.findViewById(id);
                actv.setThreshold(1);
                actv.setAdapter(adapter);
            }
            adapter = new ArrayAdapter<>
                    (getContext(), android.R.layout.select_dialog_item, KsebBillDAO.getInstance().getListFromDb(column));
            actv= rootView.findViewById(id);
            actv.setThreshold(1);
            actv.setAdapter(adapter);

    }
    private void addTextChange(final AutoCompleteTextView autoCompleteTextView, final View view, final int id, final String colomn){
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addAdapterToAutoComplteView(view, id, colomn);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing

            }
        });
    }



    private void autoCompletesetLineColor(AutoCompleteTextView layout, int idColor){
        if ( getContext() != null ){
            layout.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(),
                    idColor), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void editTextSetLineColor(EditText editText, int idColor){
        if ( getContext() != null ){
            editText.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(),
                    idColor), PorterDuff.Mode.SRC_ATOP);
        }
    }





    private void autoSelect(String value){
        KsebBillModel ksebBillModel = KsebBillDAO.getInstance().getRow("consumer_name",value);
        autoCompleConsumerName.setText(ksebBillModel.consumerName); autoCompleMobileNumber.setText(ksebBillModel.mobileNo); autoCompleConsumerNo.setText(ksebBillModel.consumerNo);
    }
    private void pay(){
        if ( getContext() == null )
            return;

    //    alertMessage2("Loading" ,"");
       /* mSweetAlertDialog = new SweetAlertDialog( getContext(), SweetAlertDialog.PROGRESS_TYPE );
        mSweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mSweetAlertDialog.setTitleText( "Loading" );
        mSweetAlertDialog.setCancelable( false );
        mSweetAlertDialog.show();
*/
        getObservable(  )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe(  getObserver() );
    }
    private Observable< String > getObservable( ){
        return Observable.fromCallable(this::payKsebBill);
    }

    private Observer< String > getObserver(  ){
        return  new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Do nothing
            }

            @Override
            public void onNext(String result ) {
                processResponse( result );

            }

            @Override
            public void onError(Throwable e) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                //Do nothing

            }
        };
    }
    private void processResponse(String response){
        try{
            KsebRechargeStatus ksebRechargeStatus = new Gson().fromJson( response, KsebRechargeStatus.class );
            ksebRechargeStatus.setAccNo( tempStringAccountNo  );
            ksebRechargeStatus.setBranch(  BranchName );
            ksebRechargeStatus.setConsumername(tempStringConsumerName);
            ksebRechargeStatus.setMobile(tempStringMobileNumber);
            ksebRechargeStatus.setConsumerno(tempStringConsumerNo);
            ksebRechargeStatus.setBillno(tempStringBillNo);
            ksebRechargeStatus.setAmount(tempStringAmount);
            ksebRechargeStatus.setSectionname(tempDisplaySection);


            if ( ksebRechargeStatus.getStatusCode() > 0 ){
                statusmessage=ksebRechargeStatus.getStatusMessage();
            //    String message = "Kseb bill payment is on pending.\n";
                String message =statusmessage;
                ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                KeyValuePair keyValuePair = new KeyValuePair();
                keyValuePair.setKey("A/c.");
                keyValuePair.setValue( ksebRechargeStatus.getAccNo() );
                keyValuePairs.add( keyValuePair );
                keyValuePair = new KeyValuePair();
                keyValuePair.setKey("Amount");
                keyValuePair.setValue( ksebRechargeStatus.getAmount() );
                keyValuePairs.add( keyValuePair );
                keyValuePair = new KeyValuePair();
                keyValuePair.setKey("Ref.No");
                keyValuePair.setValue( ksebRechargeStatus.getRefId() );
                keyValuePairs.add( keyValuePair );







            //    ksebRechargeStatus = ksebRechargeStatus;

             //   getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValuePairs  ,"Pending", message,
                      //  ksebRechargeStatus, true, false ) ).commit();
                getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValuePairs  ,"Pending", message
                        , ksebRechargeStatus, true, false,from ) ).commit();

             //   alertMessage1("Pending" ,message);
           //     alertPopup(title,keyValueList,message,isHappy,isBackButtonEnabled,from);

            }
            else if ( ksebRechargeStatus.getStatusCode()< 0 ){
                if ( ksebRechargeStatus.getStatusCode() == -72 ){
                  //  String message =  "You have no sufficient balance on your account\n";
                    String message = statusmessage;
                    ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                    KeyValuePair keyValuePair = new KeyValuePair();
                    keyValuePairs.add( keyValuePair );
                  /*  getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValuePairs  ,"Failed", message,
                            mPaymentModel,   false, false ) ).commit();*/
                    alertMessage1("Failed" ,message);

               //     getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValuePairs  ,"Failed", message
                           // , ksebRechargeStatus, false, false, from) ).commit();

                }
                else if ( ksebRechargeStatus.getStatusCode() == -55 ){
                    String message = statusmessage;
                  //  String message =   "Your transaction is already processed.";
                    ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                    KeyValuePair keyValuePair = new KeyValuePair();
                    keyValuePairs.add( keyValuePair );

                    alertMessage1("Failed" ,message);
                   /* getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValuePairs  ,"Failed", message,
                            mPaymentModel,    false, false ) ).commit();*/
                //    getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValuePairs  ,"Failed", message
                          //  , ksebRechargeStatus, false, false, from) ).commit();
                }
                else {
                    String message = statusmessage;
                  //  String message =   "Transaction Failed";
                    ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
                    KeyValuePair keyValuePair = new KeyValuePair();
                    keyValuePairs.add( keyValuePair );

                    alertMessage1("Failed" ,message);
                 //   getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValuePairs  ,"Pending", message
                        //    , ksebRechargeStatus, true, false,from ) ).commit();
                }
//                String message = "Kseb bill payment is  failed.\n";
//                ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
//
//                getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValuePairs  ,"Failed", message,
//                        false, false ) ).commit();
            }
        }catch ( Exception e ){
            Toast.makeText( getContext(), "Error occured", Toast.LENGTH_LONG ).show();
        }
        /*try{
            int resultResponse = Integer.parseInt(response.trim());
            if(resultResponse > 0){
                showAlertDialogue(getString(R.string.app_name), "Kseb bill payment is on pending.\n"+"Your reference no:"+response, 1);
                KsebBillDAO.getInstance().insertValues(tempStringConsumerName, tempStringMobileNumber, tempStringConsumerNo, tempStringSectionList,
                        "", "");
            }
            else if(resultResponse < 0){
                if(resultResponse == -72){
                    showAlertDialogue(getString(R.string.app_name), "You have no sufficient balance on your account", 2);
                }
                else if(resultResponse == -55){
                    showAlertDialogue(getString(R.string.app_name), "Your transaction is already processed.\n Please try after some time", 2);
                }
                else
                    showAlertDialogue(getString(R.string.app_name), "Transaction Failed", 2);
            }
        }catch (Exception e){
            if(IScoreApplication.DEBUG) Log.e("Response parse error" , response);
            showAlertDialogue(getString(R.string.app_name), IScoreApplication.SERVICE_NOT_AVAILABLE, 2);
        }*/
    }
    private String payKsebBill(){
        String module;
        String pin;
        String  response = "";
        String  url;
        AccountInfo accountInfo;
        pin = UserCredentialDAO.getInstance().getLoginCredential().pin;

        //extracting account number
        try{

            String extractedAccNo = tempStringAccountNo;
            extractedAccNo = extractedAccNo.
                        /*replace(extractedAccNo.substring(extractedAccNo.indexOf(" (")+1, extractedAccNo.indexOf(") ")+1), "")*/
                                replace(extractedAccNo.substring(extractedAccNo.indexOf(" (")+1, extractedAccNo.indexOf(')')+1), "");
            extractedAccNo = extractedAccNo.replace(" ","");
            accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(extractedAccNo);
            module = accountInfo.accountTypeShort;

            SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
            String BASE_URL=pref.getString("oldbaseurl", null);
            url = BASE_URL+"/KSEBPaymentRequest?ConsumerName="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(tempStringConsumerName)) +"&MobileNo="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(tempStringMobileNumber))+"&ConsumerNo="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(tempStringConsumerNo))+ "&SectionList="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(sectionCode)) +"&BillNo="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(tempStringBillNo))+ "&Amount="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(tempStringAmount)) + "&AccountNo="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(extractedAccNo)) + "&Module="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(module)) + "&Pin="+
                    IScoreApplication.encodedUrl(IScoreApplication.encryptStart(pin));
            response = ConnectionUtil.getResponse(url);
            return response;
        }catch (Exception e){
            if(IScoreApplication.DEBUG) Log.e("ksebexcetpion", e.toString());
        }
        return response;
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


    private void getAccList() {
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
                    UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                    String token = loginCredential.token;
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    String cusid = userDetails.customerId;
                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
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
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("OwnAccountdetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray newJArray = object.getJSONArray("OwnAccountdetailsList");

                                for(int i=0;i<newJArray.length();i++){
                                    try {
                                        JSONObject json = newJArray.getJSONObject(i);
//                                        if (json.getString("IsShowBalance").equals("1")){
                                        jresult.put(json);
                                        accountlist.add(json.getString("AccountNumber"));
//                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                accountNumberSelector.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));

                                accountNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            BranchName = jresult.getJSONObject(position).getString("BranchName");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                })
                                ;
                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
                                accountNumberSelector.setSelection(getIndex(accountNumberSelector, settingsModel.customerId));


                            }
                            else {

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
                        }
                        catch (JSONException e) { }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) { }
        }
        else {
           // DialogUtil.showAlert(getContext(),"Network is currently unavailable. Please try again later.");
        }

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }


    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //  InputStream caInput = getResources().openRawResource(Common.getCertificateAssetName());
        // File path: app\src\main\res\raw\your_cert.cer
        InputStream caInput = IScoreApplication.getAppContext().
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

    private void alertMessage( String title, ArrayList<KeyValuePair> keyValueList, String message , boolean isHappy, boolean isBackButtonEnabled ){
        getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment1.getInstance( keyValueList  ,title, message,
                ksebRechargeStatus, isHappy, isBackButtonEnabled, from) ).commit();
       /* getFragmentManager().beginTransaction().replace( R.id.container, AlertMessageFragment.getInstance( keyValueList  ,title, message,
                 isHappy,isBackButtonEnabled ) ).commit();*/
    }

    private void alertMessage1(String msg1, String msg2) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
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
               /* Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                if ( fragmentManager != null ){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack("Kseb");
                    fragmentTransaction.commit();
                }*/
                //  finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void alertMessage2(String msg1, String msg2) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
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




//