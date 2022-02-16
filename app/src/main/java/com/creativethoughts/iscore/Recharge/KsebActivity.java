package com.creativethoughts.iscore.Recharge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.kseb.KsebSectionSelectionActivity;
import com.creativethoughts.iscore.kseb.SectionDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.NumberToWord;
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
import java.util.ArrayList;

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

public class KsebActivity extends AppCompatActivity implements View.OnClickListener,OnItemClickListener{

    public String TAG = "KsebActivity";
    private ProgressDialog progressDialog;
    AlertDialog dialog =  null;
    Spinner accountNumberSelector;
    LinearLayout lnr_lyt_select_section;

    private AutoCompleteTextView autoCompleConsumerName;
    private AutoCompleteTextView autoCompleMobileNumber;
    private AutoCompleteTextView autoCompleConsumerNo;
    private TextView txtSectionName;
    private TextView txt_amtinword;
    private AutoCompleteTextView edtTxtBillNo;
    private AutoCompleteTextView edtTxtAmount;
    private Button proceedToPayButton;
    private Button btnClearAll;
    TextView tv_accountno;


    private String tempStringMobileNumber;
    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist = new ArrayList<String>();
    private String tempStringAccountNo,BranchName ;
    private String tempStringConsumerName;
    private String tempStringConsumerNo;
    private String tempStringSectionList;
    private String tempStringBillNo;
    private String tempStringAmount;
    String typeShort="";
    String SubModule="";
    String tempDisplaySection;
    private String sectionCode = "";

    private static final int REQUEST_SELECT_SECTION = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kseb);

        if ( KsebActivity.this != null && KsebActivity.this.getSupportActionBar() != null ){
            ActionBar actionBar = KsebActivity.this.getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(getString(R.string.kseb_title_action_bar));

          //  getAccountNumber();
        }

        setInitialise();
        setRegister();

        alertMessage1("", "Please check the expiry date of the bill");

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



    private void setInitialise() {

        accountNumberSelector       =    (Spinner)findViewById(R.id.account_number_selector_spinner);
        lnr_lyt_select_section      =    (LinearLayout) findViewById(R.id.lnr_lyt_select_section);
        proceedToPayButton                =    (Button) findViewById(R.id.proceedToPay);
        btnClearAll                 =   (Button) findViewById(R.id.clear_all);

        autoCompleConsumerName      =  (AutoCompleteTextView)findViewById(R.id.consumer_name);
        autoCompleMobileNumber      =   (AutoCompleteTextView)findViewById(R.id.mobile_no);
        autoCompleConsumerNo        =  (AutoCompleteTextView)findViewById(R.id.consumer_no);
        txtSectionName              =  (TextView) findViewById(R.id.txt_section_name);
        txt_amtinword               =  (TextView) findViewById(R.id.txt_amtinword);
        edtTxtBillNo                =   (AutoCompleteTextView) findViewById(R.id.bill_no);
        edtTxtAmount                =   (AutoCompleteTextView) findViewById(R.id.amount);

        tv_accountno = (TextView)findViewById(R.id.tv_accountno);






    }

    private void setRegister() {

        lnr_lyt_select_section.setOnClickListener(this);
        proceedToPayButton.setOnClickListener(this);
        btnClearAll.setOnClickListener(this);
        tv_accountno.setOnClickListener(this);

    }



    private SSLSocketFactory getSSLSocketFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
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

    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(KsebActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
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
        PicassoTrustAll.getInstance(KsebActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

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

                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }
    private void alertMessage2(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(KsebActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
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
        PicassoTrustAll.getInstance(KsebActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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

                alertDialog.dismiss();
                Intent intent = new Intent(KsebActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lnr_lyt_select_section:
                Intent intent = new Intent(KsebActivity.this, KsebSectionSelectionActivity.class );
                startActivityForResult( intent, REQUEST_SELECT_SECTION );
//                Intent intent = new Intent(KsebActivity.this, KsebSectionActivity.class );
//                startActivityForResult( intent, REQUEST_SELECT_SECTION );
                break;

            case R.id.tv_accountno:
                getAccList();
                break;

            case R.id.proceedToPay:
                proceedPay();
                break;

            case R.id.clear_all:
                clearAll();
                break;

        }
    }

    private void clearAll() {

        BranchName = "";
        SubModule = "";
        typeShort = "";
        tv_accountno.setText("");

        autoCompleConsumerName.setText("");
        autoCompleMobileNumber.setText("");
        autoCompleConsumerNo.setText("");
        txtSectionName.setText("");
        sectionCode = "";
        txt_amtinword.setText("");
        edtTxtBillNo.setText("");
        edtTxtAmount.setText("");

    }

    private void getAccList() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(KsebActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
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
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e(TAG,"requestObject1   501   "+requestObject1);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getOwnAccounDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            progressDialog.dismiss();
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
                                Log.e(TAG,"accountlist   501   "+accountlist);
                                Log.e(TAG,"jresult   501   "+jresult);
//                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, accountlist));
//
//                                mAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                        try {
//                                            BranchName = jresult.getJSONObject(position).getString("BranchName");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> parent) {
//
//                                    }
//                                })
//                                ;
//                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));

                                if (jresult.length()>0){
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(KsebActivity.this);
                                    final View customLayout2 = getLayoutInflater().inflate(R.layout.popup_accountno, null);
                                    RecyclerView rvAccountno = customLayout2.findViewById(R.id.rvAccountno);
                                    builder2.setView(customLayout2);


                                    GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(), 1);
                                    rvAccountno.setLayoutManager(lLayout);
                                    rvAccountno.setHasFixedSize(true);
                                    AccountAdapter adapter = new AccountAdapter(getApplicationContext(), jresult);
                                    rvAccountno.setAdapter(adapter);
                                    adapter.setOnItemClickListener(KsebActivity.this);

                                    // dialog = builder2.create();
                                    dialog = builder2.create();
                                    dialog.setCancelable(true);
                                    dialog.show();
                                }



                            }
                            else {

                                try{
//                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
//                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
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

                                }catch (JSONException e){
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
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
                        }
                        catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
                progressDialog.dismiss();
            }
        }
        else {

            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REQUEST_SELECT_SECTION && resultCode == RESULT_OK ){
            Bundle bundle = data.getExtras();
            try{
                if ( bundle != null ){
                    SectionDetails sectionDetails = bundle.getParcelable( getString( R.string.kseb_section_list ) );
                //    {"SectionName":"Adimali","SectionCode":"5617"}
                    if ( sectionDetails != null ){

                        tempDisplaySection = sectionDetails.getSectionName() + '(' + sectionDetails.getSectionCode() + ')';
                        txtSectionName.setText( tempDisplaySection );
                        sectionCode = sectionDetails.getSectionCode();
                        tempStringSectionList = tempDisplaySection;
                    }
                }
                else {
                   // Toast.makeText( getContext(), "Error occured", Toast.LENGTH_SHORT ).show();

//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KsebActivity.this);
//                    builder.setMessage(EXMessage)
////                                builder.setMessage("No data found.")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    android.app.AlertDialog alert = builder.create();
//                    alert.show();
                }
            }catch ( Exception e ){
                if ( IScoreApplication.DEBUG )
                    Log.e("exc section ", e.toString() );
            }
        }
    }

    private void proceedPay() {

      //  tempStringAccountNo = accountNumberSelector.getSelectedItem().toString();
        tempStringAccountNo = tv_accountno.getText().toString();
        tempStringConsumerName = autoCompleConsumerName.getText().toString();
        tempStringMobileNumber = autoCompleMobileNumber.getText().toString();
        tempStringConsumerNo = autoCompleConsumerNo.getText().toString();
        String txt_section_name = txtSectionName.getText().toString();
        tempStringBillNo = edtTxtBillNo.getText().toString();
        tempStringAmount = edtTxtAmount.getText().toString();

        if (tempStringAccountNo.equals("")){
          //  autoCompleConsumerName.setError("Select Account");
            showToast("Select Account");
        }
        else if (tempStringConsumerName.length() == 0) {
            autoCompleConsumerName.setError("Please Enter Valid Name.");
            showToast("Please Enter Valid Name.");
        }
        else if(tempStringMobileNumber == null || tempStringMobileNumber.length() != 10){

            autoCompleMobileNumber.setError("Please Enter Valid 10 Digit Mobile Number.");
            showToast("Please Enter Valid 10 Digit Mobile Number.");
        }
        else if (tempStringConsumerNo.length() < 8 || tempStringConsumerNo.length()>16) {
            autoCompleConsumerNo.setError("Please Enter Correct Consumer Number.");
            showToast("Please Enter Correct Consumer Number.");
        }
        else if(sectionCode.equals("")) {
            txtSectionName.setTextColor( ContextCompat.getColor( getApplicationContext(), R.color.FireBrick ) );
          //  Toast.makeText(getActivity(), "Please Select Section Name", Toast.LENGTH_SHORT).show();
            showToast("Please Select Section Name");

        }
        else if(tempStringBillNo.length() == 0) {
            edtTxtBillNo.setError("Please Enter Valid Bill Number.");
            showToast("Please Enter Valid Bill Number.");
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
            showToast("Please Enter Amount.");
        }
        else {

            Log.e(TAG,"proceedPay  567 "
            +"\n"+"tempStringAccountNo   "+tempStringAccountNo
                    +"\n"+"tempStringConsumerName   "+tempStringConsumerName
                    +"\n"+"tempStringMobileNumber   "+tempStringMobileNumber
                    +"\n"+"tempStringConsumerNo   "+tempStringConsumerNo
                    +"\n"+"txt_section_name   "+txt_section_name
                    +"\n"+"tempStringBillNo   "+tempStringBillNo
                    +"\n"+"tempStringAmount   "+tempStringAmount);
            ksebConfirmation(tempStringAccountNo,tempStringConsumerName,
                    tempStringMobileNumber,tempStringConsumerNo,txt_section_name,tempStringBillNo,tempStringAmount);
        }
    }


    private void ksebConfirmation(String tempStringAccountNo,String tempStringConsumerName,
                                  String tempStringMobileNumber,String tempStringConsumerNo, String txt_section_name,
                                  String tempStringBillNo, String tempStringAmount ) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KsebActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) KsebActivity.this.getSystemService(KsebActivity.this.LAYOUT_INFLATER_SERVICE);
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
            ImageView img_applogo = layout.findViewById(R.id.img_aapicon);

            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();

            SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
            String IMAGEURL = imageurlSP.getString("imageurl","");
            SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
            String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
            PicassoTrustAll.getInstance(KsebActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

            tvAcntno.setText(""+tempStringAccountNo);
            tvbranch.setText(BranchName);
            tv_con_name.setText(""+tempStringConsumerName);
            tv_con_mob_numb.setText(""+tempStringMobileNumber);
            tv_con_num.setText("Consumer No : "+tempStringConsumerNo);
            tv_con_section.setText("Consumer Section : "+txt_section_name);
            tv_bill_num.setText("Bill NO : "+tempStringBillNo);



            String stramnt = tempStringAmount.replace(",","");
            text_confirmationmsg.setText("Proceed Recharge With Above Amount"+ "..?");
            String[] netAmountArr = stramnt.split("\\.");
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

            tv_amount.setText("₹ " + CommonUtilities.getDecimelFormate(Double.parseDouble(stramnt)) );



            //  double num =Double.parseDouble(""+tempStringAmount);
            //  String stramnt = CommonUtilities.getDecimelFormate(num);

            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  pay();
                    String mAmount = tempStringAmount.replace(",","");
                    payKsebBill(tempStringAccountNo,tempStringConsumerName,
                            tempStringMobileNumber,tempStringConsumerNo, txt_section_name,
                            tempStringBillNo, mAmount);
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void payKsebBill(String tempStringAccountNo, String tempStringConsumerName, String tempStringMobileNumber,
                             String tempStringConsumerNo, String txt_section_name, String tempStringBillNo, String mAmount) {

        String extractedAccNo = tempStringAccountNo;
        extractedAccNo = extractedAccNo.
                /*replace(extractedAccNo.substring(extractedAccNo.indexOf(" (")+1, extractedAccNo.indexOf(") ")+1), "")*/
                        replace(extractedAccNo.substring(extractedAccNo.indexOf(" (")+1, extractedAccNo.indexOf(')')+1), "");
        extractedAccNo = extractedAccNo.replace(" ","");
       // accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(extractedAccNo);

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(KsebActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
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
                String reqmode = IScoreApplication.encryptStart("21");
                final JSONObject requestObject1 = new JSONObject();
                try {

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token=tokenIdSP.getString("Token", null);
                    SharedPreferences customerIdSP =getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid=customerIdSP.getString("customerId", null);
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    SharedPreferences BankVerifierSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF32, 0);
                    String BankVerifier =BankVerifierSP.getString("BankVerifier","");
                    String iemi =   IScoreApplication.getIEMI();
                    Log.e("imei","         15381     "+iemi);
                    SharedPreferences prefpin =getApplicationContext().getSharedPreferences(Config.SHARED_PREF36, 0);
                    String pin =prefpin.getString("pinlog", "");


                    requestObject1.put("ConsumerName",IScoreApplication.encryptStart(tempStringConsumerName));
                    requestObject1.put("MobileNo",IScoreApplication.encryptStart(tempStringMobileNumber));
                    requestObject1.put("ConsumerNo",IScoreApplication.encryptStart(tempStringConsumerNo));
                    requestObject1.put("SectionList",IScoreApplication.encryptStart(sectionCode));
                    requestObject1.put("BillNo",IScoreApplication.encryptStart(tempStringBillNo));
                    requestObject1.put("amount",IScoreApplication.encryptStart(mAmount));
                    requestObject1.put("AccountNo",IScoreApplication.encryptStart(extractedAccNo));
                    requestObject1.put("Module",IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("Pin",IScoreApplication.encryptStart(pin));
                    requestObject1.put("imei",IScoreApplication.encryptStart(iemi));
                    requestObject1.put("token", IScoreApplication.encryptStart(token));
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));
                    requestObject1.put("BankVerified", IScoreApplication.encryptStart(BankVerifier));

                    Log.e(TAG,"requestObject1     790   "+requestObject1);

                    Log.e(TAG,"payKsebBill  790 "
                            +"\n"+"ConsumerName   "+tempStringConsumerName
                            +"\n"+"MobileNo   "+tempStringMobileNumber
                            +"\n"+"ConsumerNo   "+tempStringConsumerNo
                            +"\n"+"SectionList   "+sectionCode
                            +"\n"+"BillNo   "+tempStringBillNo
                            +"\n"+"amount   "+mAmount
                            +"\n"+"AccountNo   "+extractedAccNo
                            +"\n"+"Module   "+SubModule
                            +"\n"+"Pin   "+pin
                            +"\n"+"imei   "+iemi
                            +"\n"+"token   "+token
                            +"\n"+"BankKey   "+BankKey
                            +"\n"+"BankHeader   "+BankHeader
                            +"\n"+"BankVerified   "+BankVerifier);


                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Log.e(TAG,"Exception     790   "+e.toString());

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getKSEBPaymentRequest(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG,"KSEBPaymentRequest  7901   "+response.body());
                        try{
                            progressDialog.dismiss();

                            Log.e(TAG," KSEBPaymentRequest    7902       "+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                alertMessage2("",jsonObj.getString("EXMessage"));

                            }
                            if(jsonObj.getString("StatusCode").equals("1")) {

                                alertMessageSucces("1",jsonObj.getString("EXMessage"));

                            }
                            if(jsonObj.getString("StatusCode").equals("3")) {

                                alertMessageSucces("3",jsonObj.getString("EXMessage"));

                            }
                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
                                builder.setMessage(jsonObj.getString("EXMessage"))
//                                builder.setMessage("No data found.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(KsebActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,"Exception     7901   "+e.toString());
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
                            builder.setMessage(e.toString())
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

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e(TAG,"onFailure     7902   "+t.toString());
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
                        builder.setMessage(t.toString())
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
                });

            }
            catch (Exception e)
            {
                Log.e(TAG,"Exception     7903   "+e.toString());

                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
                builder.setMessage(e.toString())
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
        else {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
            builder.setMessage("Network error occured. Please try again later")
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

    private void alertMessageSucces(String mode, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(KsebActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText("msg1");
        tv_msg2.setText(msg2);
        ImageView img_applogo = dialogView.findViewById(R.id.img_applogo);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");
        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(KsebActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);
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

                alertDialog.dismiss();
                Intent intent = new Intent(KsebActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemClick(View v, int position, String data, String mode) {
        Log.e(TAG,"onItemClick   730   "+position+"   "+data);
        if (data.equals("account")){
            dialog.dismiss();

            BranchName = "";
            SubModule = "";
            typeShort = "";
            tv_accountno.setText("");
            try {
                JSONObject jsonObject = jresult.getJSONObject(position);
                BranchName = jsonObject.getString("BranchName");
                typeShort = jsonObject.getString("typeShort");
                //SubModule = jsonObject.getString("SubModule");
                SubModule = jsonObject.getString("typeShort");
                tv_accountno.setText(""+jsonObject.getString("AccountNumber"));
            }catch (Exception e){

            }
        }

    }

    private void showToast(String value) {
        if ( getApplicationContext()  == null )
            return;
//        Toast toast = Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT);
//        LayoutInflater layoutInflater = getLayoutInflater();
//        View toastView = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.container));
//        TextView textView = toastView.findViewById(R.id.text);
//        textView.setText(value);
//        toast.setView(toastView);
//        toast.show();


        AlertDialog.Builder builder = new AlertDialog.Builder(KsebActivity.this);
        builder.setMessage(value)
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
