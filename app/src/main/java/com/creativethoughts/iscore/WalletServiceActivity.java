package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.WalletMintransAdapter;
import com.creativethoughts.iscore.model.ToAccountDetails;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class WalletServiceActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressDialog progressDialog;
    TextView txt_userdetails,  txt_userid, txtv_totalbal, txt_amtinword;
    TextView tvTransaction,tvLoadmoney, tvwalletbal,txt_app_name;
    LinearLayout ll_loadmoney, llministatement;
    RecyclerView rv_ministatmnt;
    private Spinner spn_account_type;
    ArrayList<ToAccountDetails> AccountDetails = new ArrayList<>();
    ArrayAdapter<ToAccountDetails> AccountAdapter = null;
    String strAccNo, strFKacc, strSubModule;
    EditText edt_txt_amount;
    Button btn_submit;
    Spinner mAccountSpinner;
    ImageView img_applogo;

    private JSONArray jresult = new JSONArray();
    private ArrayList<String> accountlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walletservice);
        //accountlist = new ArrayList<String>();

        mAccountSpinner = findViewById(R.id.spnAccountNum);

        tvwalletbal = findViewById(R.id.tvwalletbal);
        tvTransaction = findViewById(R.id.tvTransaction);
        txt_amtinword = findViewById(R.id.txt_amtinword);
        tvTransaction.setOnClickListener(this);
        tvLoadmoney = findViewById(R.id.tvLoadmoney);
        tvLoadmoney.setOnClickListener(this);
        txt_userid =  findViewById(R.id.txt_userid);
        txt_userdetails =  findViewById(R.id.txt_userdetails);
        txtv_totalbal =  findViewById(R.id.txtv_totalbal);
        ll_loadmoney =  findViewById(R.id.ll_loadmoney);
        llministatement =  findViewById(R.id.llministatement);
        rv_ministatmnt =  findViewById(R.id.rv_ministatmnt);
        spn_account_type = findViewById(R.id.spn_account_type);
        edt_txt_amount = findViewById(R.id.edt_txt_amount);
        btn_submit = findViewById(R.id.btn_submit);
        img_applogo = findViewById(R.id.img_applogo);
        txt_app_name = findViewById(R.id.txt_app_name);

        btn_submit.setOnClickListener(this);

        SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
        txt_userid.setText( "Customer Id : "+customerIdSP.getString("customerId",""));
        SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
        txt_userdetails.setText( customerNameSP.getString("customerName",""));
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        tvwalletbal.setText( "WALLET BALANCE ON "+currentDate);

        SharedPreferences imageurlSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");


        SharedPreferences AppIconImageCodeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(WalletServiceActivity.this).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

        SharedPreferences ResellerNameeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
        String aapName = ResellerNameeSP.getString("ResellerName","");
        txt_app_name.setText(aapName);

        //  showOwnAccToList();
        getWalletAmount();

        getAccList();

        ll_loadmoney.setVisibility(View.VISIBLE);
        llministatement.setVisibility(View.GONE);


        /*strAccNo= "001001999315 (SB)";
        strFKacc="25567";
        strSubModule="DDSB";*/

        getTransactiondetails(strAccNo,strFKacc,strSubModule);



        edt_txt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                edt_txt_amount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);

                    Double longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Double.parseDouble(originalString);
                    String formattedString = CommonUtilities.getDecimelFormateForEditText(longval);
                    //setting text after format to EditText
                    edt_txt_amount.setText(formattedString);
                    edt_txt_amount.setSelection(edt_txt_amount.getText().length());

                    String amnt = edt_txt_amount.getText().toString().replaceAll(",", "");
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

                edt_txt_amount.addTextChangedListener(this);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {

                }
                catch(NumberFormatException e)
                {

                }

            }
        });

    }

    private void getAccList() {
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


                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid = customerIdSP.getString("customerId","");
                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token = tokenIdSP.getString("Token","");
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("13"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",IScoreApplication.encryptStart("1"));
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
                                accountlist = new ArrayList<String>();

                                for(int i=0;i<newJArray.length();i++){
                                    try {
                                        JSONObject json = newJArray.getJSONObject(i);
                                        jresult.put(json);

                                        accountlist.add(json.getString("AccountNumber"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mAccountSpinner.setAdapter(new ArrayAdapter<String>(WalletServiceActivity.this, android.R.layout.simple_spinner_dropdown_item, accountlist));

                                mAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            strAccNo = jresult.getJSONObject(position).getString("AccountNumber");
                                            strFKacc = jresult.getJSONObject(position).getString("FK_Account");
                                            strSubModule = jresult.getJSONObject(position).getString("SubModule");
                                            getTransactiondetails(strAccNo,strFKacc,strSubModule);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

//                                SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, settingsModel.customerId));

                                SharedPreferences SelectedAccountSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF40, 0);
                                String SelectedAccount = SelectedAccountSP.getString("SelectedAccount","");
                                mAccountSpinner.setSelection(getIndex(mAccountSpinner, SelectedAccount));


                            }
                            else {

                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("OwnAccountdetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WalletServiceActivity.this);
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
                                    String EXMessage = jsonObj.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WalletServiceActivity.this);
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
                        catch (JSONException e) { }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) { }
        }
        else {
             DialogUtil.showAlert(WalletServiceActivity.this,"Network is currently unavailable. Please try again later.");
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

    public void getTransactiondetails(String straccno,String stracccode, String strsubmodule){

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


                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));


                    requestObject1.put("AccNo",IScoreApplication.encryptStart(straccno) );
                    requestObject1.put("Fk_AccountCode",IScoreApplication.encryptStart(stracccode) );
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(strsubmodule) );


                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String customerId = customerIdSP.getString("customerId","");
                    SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                    String mobileNo = mobileNoSP.getString("mobileNo","");
                    SharedPreferences customerNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF27, 0);
                    String customerNo = customerNoSP.getString("customerNo","");

                    requestObject1.put("ID_Customer",IScoreApplication.encryptStart(customerId));
                    requestObject1.put("MobNo",IScoreApplication.encryptStart(mobileNo));
                    requestObject1.put("CustId",IScoreApplication.encryptStart(customerNo));
                    requestObject1.put("CorpCode",IScoreApplication.encryptStart(BankKey) );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCardMiniStatement(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {
                                JSONObject jobj = jObject.getJSONObject("CardMiniStatementDetails");
                                JSONArray jarray = jobj.getJSONArray("Data");
                                if(jarray.length()!=0){
                                    GridLayoutManager lLayout = new GridLayoutManager(WalletServiceActivity.this, 1);
                                    rv_ministatmnt.setLayoutManager(lLayout);
                                    rv_ministatmnt.setHasFixedSize(true);
                                    WalletMintransAdapter adapter = new WalletMintransAdapter(WalletServiceActivity.this, jarray);
                                    rv_ministatmnt.setAdapter(adapter);
                                }else {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                    builder.setMessage("No data found.")
                                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }
                            else{
                                try{
                                    JSONObject jobj = jObject.getJSONObject("CardMiniStatementDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }

    public void doUploadMoney(String straccno,String stracccode, String strsubmodule, String stramount){

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

                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String customerId = customerIdSP.getString("customerId","");
                    SharedPreferences mobileNoSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF31, 0);
                    String mobileNo = mobileNoSP.getString("mobileNo","");
                    SharedPreferences customerNameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF28, 0);
                    String customerName = customerNameSP.getString("customerName","");


                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    requestObject1.put("AccNo",IScoreApplication.encryptStart(straccno) );
                    requestObject1.put("Fk_AccountCode",IScoreApplication.encryptStart(stracccode) );
                    requestObject1.put("SubModule",IScoreApplication.encryptStart(strsubmodule) );
                    requestObject1.put("SubTranType",IScoreApplication.encryptStart("P") );
                    requestObject1.put("Amount",IScoreApplication.encryptStart(stramount) );


                    requestObject1.put("ID_Customer",IScoreApplication.encryptStart(customerId));
                    requestObject1.put("MobNo",IScoreApplication.encryptStart(mobileNo));
                    requestObject1.put("CustId",IScoreApplication.encryptStart(customerName));
                    requestObject1.put("CorpCode",IScoreApplication.encryptStart(BankKey) );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCardTopUpandReverse(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {

                                JSONObject jobj = jObject.getJSONObject("CardTopUpDetails");

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
                                builder.setMessage("Congrats on successful top up, your Transaction Reference number is "+jobj.getString("TransactonrefNo"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        });
                                android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }
                            else{
                                try{
                                    JSONObject jobj = jObject.getJSONObject("CardTopUpDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
        }
    }


    public void getWalletAmount(){

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            try{
                progressDialog = new ProgressDialog(WalletServiceActivity.this, R.style.Progress);
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
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String customerId = customerIdSP.getString("customerId","");

                    requestObject1.put("ID_Customer",IScoreApplication.encryptStart(customerId));
                    requestObject1.put("CorpCode",IScoreApplication.encryptStart(BankKey));
                   /* requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCardBalance(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {

                                JSONObject jobj = jObject.getJSONObject("BalanceDetails");
                                txtv_totalbal.setText("\u20B9 "+  CommonUtilities.getDecimelFormate(jobj.getDouble("Balance")));

                            }
                            else{
//                                ll_standnginstr.setVisibility(View.GONE);
//                                llreminder.setVisibility(View.GONE);
                                try{
                                    JSONObject jobj = jObject.getJSONObject("BalanceDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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
                                    String EXMessage = jObject.getString("EXMessage");
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
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

                        } catch (JSONException e) {
//                            ll_standnginstr.setVisibility(View.GONE);
//                            llreminder.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { progressDialog.dismiss();}
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.showAlert(WalletServiceActivity.this,
                    "Network is currently unavailable. Please try again later.");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvLoadmoney:
                tvTransaction.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle3));
                tvLoadmoney.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle1));
                tvTransaction.setTextColor(Color.parseColor("#000000"));
                tvLoadmoney.setTextColor(Color.parseColor("#ffffff"));
                ll_loadmoney.setVisibility(View.VISIBLE);
                llministatement.setVisibility(View.GONE);

//                acctype ="1";
//                strHeader="Load Money";
                break;
            case R.id.tvTransaction:
                tvTransaction.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle));
                tvLoadmoney.setBackground(ContextCompat.getDrawable(WalletServiceActivity.this, R.drawable.toggle4));
                tvTransaction.setTextColor(Color.parseColor("#ffffff"));
                tvLoadmoney.setTextColor(Color.parseColor("#000000"));
                llministatement.setVisibility(View.VISIBLE);
                ll_loadmoney.setVisibility(View.GONE);

//                acctype ="2";
//                strHeader="Transactions";
                break;
            case R.id.btn_submit:
                validationforloadmoney();
                break;
        }
    }

    private void validationforloadmoney() {

        if(strAccNo.isEmpty()){

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
            builder.setMessage("Please select account")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if(edt_txt_amount.getText().toString().isEmpty()){

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WalletServiceActivity.this);
            builder.setMessage("Please enter amount")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else {
            doUploadMoney(strAccNo, strFKacc, strSubModule, edt_txt_amount.getText().toString());
        }
    }


}

