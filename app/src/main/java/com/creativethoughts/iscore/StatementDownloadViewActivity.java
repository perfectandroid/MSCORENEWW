package com.creativethoughts.iscore;
 
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coolerfall.download.DownloadListener;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AccountListAdapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.model.AccountToTransfer;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.core.content.FileProvider;
import lecho.lib.hellocharts.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class StatementDownloadViewActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etxtFrom, etxtTo;
    RadioButton rb1, rb2;
    Spinner s_month;
    LinearLayout l1,l0;
    ArrayAdapter aa;
    int indexofmonth;
    String accno;
    String from,to,acc;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ProgressDialog progressDialog;
    String[] month = { "January", "February", "March", "April", "May","June","July","August", "September","October","November","December"};
    TextView tv_download,tv_acc_selection,tv_view,tv_acc_selection1,tv_acc_selection2, tv_accselection;
    String no;
    AccountInfo accountInfo;
    private DownloadManager mDownloadManager;
    JSONObject jsonObject;
    ListView list_view;
    TextView tv_popuptitle;
    String submodule,branchcode,submod;
    File destination = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement_download_view);

        mDownloadManager = new DownloadManager();

        /*accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accNewChange);
        branchcode = accountInfo.accountBranchCode;
*/
        l1= findViewById(R.id.l1);
        l0= findViewById(R.id.l0);
        etxtFrom =  findViewById(R.id.etxtFrom);
        etxtTo =  findViewById(R.id.etxtTo);
        tv_view = findViewById(R.id.tv_view);
        tv_acc_selection = findViewById(R.id.tv_acc_selection);
        tv_acc_selection1 = findViewById(R.id.tv_acc_selection1);
        tv_acc_selection2 = findViewById(R.id.tv_acc_selection2);
        tv_accselection = findViewById(R.id.tv_accselection);
        tv_download = findViewById(R.id.tv_download);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        s_month = findViewById(R.id.s_month);
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        etxtFrom.setOnClickListener(this);
        etxtTo.setOnClickListener(this);
        etxtFrom.setKeyListener(null);
        etxtTo.setKeyListener(null);
        tv_download.setOnClickListener(this);
        tv_view.setOnClickListener(this);
        tv_acc_selection.setOnClickListener(this);
        tv_accselection.setOnClickListener(this);

        s_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                mMonth = s_month.getSelectedItemPosition();
                String month = s_month.getSelectedItem().toString();
                if(rb1.isChecked()) {
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);
                    StringTokenizer tokens = new StringTokenizer(formattedDate, "-");
                    String first = tokens.nextToken();// this will contain "Fruit"
                    String second = tokens.nextToken();
                    String third = tokens.nextToken();
                    String firstday;
                    if(mMonth==11||mMonth==10) {
                        try {
                            int m =mMonth+1;
                            firstday = "01"+"-"+m+"-"+third;
                            String date = firstday;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date convertedDate = null;
                            convertedDate = dateFormat.parse(date);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(convertedDate);
                            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                            int res = cal.getActualMaximum(Calendar.DATE);
                            String lastday = res+"-"+m+"-"+third;
                            etxtTo.setText(lastday);
                            Log.i("Res",lastday);
                            etxtFrom.setText(firstday);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            int m =mMonth+1;
                            String mon = "0"+m;
                            firstday = "01"+"-"+mon+"-"+third;
                            String date = firstday;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date convertedDate = null;
                            convertedDate = dateFormat.parse(date);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(convertedDate);
                            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                            int res = cal.getActualMaximum(Calendar.DATE);
                            String lastday = res+"-"+mon+"-"+third;
                            etxtTo.setText(lastday);
                            etxtFrom.setText(firstday);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        no = "001001001999";
        rb1.setChecked(true);
        if(rb1.isChecked()) {
            s_month.setVisibility(View.VISIBLE);
            rb2.setChecked(false);
            l0.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
        }
        else if(rb2.isChecked()) {
            s_month.setVisibility(View.GONE);
            rb1.setChecked(false);
            l0.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
        }

        aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,month);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_month.setAdapter(aa);


        indexofmonth = Calendar.getInstance().get(Calendar.MONTH);
        s_month.setSelection(indexofmonth);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void AccList(AlertDialog alertDialog) {
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

                    SharedPreferences tokenIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF35, 0);
                    String token = tokenIdSP.getString("Token","");
                    SharedPreferences customerIdSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF26, 0);
                    String cusid = customerIdSP.getString("customerId","");


                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("26"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Customer",   IScoreApplication.encryptStart(cusid));
                    requestObject1.put("SubMode",       IScoreApplication.encryptStart("0"));
                    requestObject1.put("LoanType",      IScoreApplication.encryptStart("0"));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1  ",""+requestObject1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getCustomerLoanandDeposit(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {

                            Log.e("response  286  ",""+response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("CustomerLoanAndDepositDetails");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray Jarray  = object.getJSONArray("CustomerLoanAndDepositDetailsList");
                                if(Jarray.length()!=0) {
                                    JSONArray jarray = Jarray;
                                    ArrayList<AccountToTransfer>  searchNamesArrayList = new ArrayList<>();
                                    ArrayList<AccountToTransfer> array_sort = new ArrayList<>();
                                    for (int k = 0; k < jarray.length(); k++) {
                                        jsonObject = jarray.getJSONObject(k);
                                        searchNamesArrayList.add(new AccountToTransfer(jsonObject.getString("FK_Account"),jsonObject.getString("AccountNumber"),jsonObject.getString("SubModule"),jsonObject.getString("LoanType"),jsonObject.getString("BranchName")));
                                        array_sort.add(new AccountToTransfer(jsonObject.getString("FK_Account"),jsonObject.getString("AccountNumber"),jsonObject.getString("SubModule"),jsonObject.getString("BranchName"),jsonObject.getString("LoanType")));
                                    }
                                    AccountListAdapter sadapter = new AccountListAdapter(StatementDownloadViewActivity.this, array_sort);
                                    list_view.setAdapter(sadapter);
                                    list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            tv_acc_selection.setVisibility(View.VISIBLE);
                                            tv_acc_selection.setText(array_sort.get(position).getAccountNumber());

                                            submodule = array_sort.get(position).getSubModule();
                                            tv_acc_selection1.setText(submodule);
                                            accno =array_sort.get(position).getAccountNumber();
                                            tv_acc_selection2.setText(accno);
                                        //    Toast.makeText(getApplicationContext(),submodule,Toast.LENGTH_LONG).show();
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                                else { }
                            }
                            else { }
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
            alertMessage1("", "Network is currently unavailable. Please try again later.");
            //DialogUtil.showAlert(StatementDownloadViewActivity.this,"Network is currently unavailable. Please try again later.");
        }

    }

    private void getAccList() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(StatementDownloadViewActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) StatementDownloadViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.acc_list_popup, null);
            list_view =  layout.findViewById(R.id.list_view);
            tv_popuptitle =  layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Account");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            AccList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private static Date getDate(String value) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            Log.e("Parse e", e.toString());
        }

        return date;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.rb1:
                rb2.setChecked(false);
                etxtFrom.setText("");
                etxtTo.setText("");
                s_month.setVisibility(View.VISIBLE);
                l0.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);

            break;
            case R.id.rb2:
                rb1.setChecked(false);
                l0.setVisibility(View.VISIBLE);
                l1.setVisibility(View.VISIBLE);

                s_month.setVisibility(View.GONE);
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate1 = df1.format(c.getTime());
                etxtTo.setText(formattedDate1);

                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, -14);
                Date dat = cal.getTime();
                SimpleDateFormat df2= new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate2 = df2.format(dat);
                etxtFrom.setText(formattedDate2);
            break;
            case R.id.etxtFrom:
                if(rb2.isChecked()) {
                    l0.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    Calendar cetxtFrom = Calendar.getInstance();
                    mYear = cetxtFrom.get(Calendar.YEAR);

                    mMonth = cetxtFrom.get(Calendar.MONTH);
                    //   mMonth = im_month.getSelectedItemPosition();
                    mDay = cetxtFrom.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(StatementDownloadViewActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {


                                    etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
                break;
            case R.id.etxtTo:
                if(rb2.isChecked()) {
                    l0.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    Calendar cetxtTo = Calendar.getInstance();
                    mYear = cetxtTo.get(Calendar.YEAR);

                    mMonth = cetxtTo.get(Calendar.MONTH);
                    //   mMonth = im_month.getSelectedItemPosition();
                    mDay = cetxtTo.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(StatementDownloadViewActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {


                                    etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            break;
            case R.id.tv_acc_selection:
                getAccList();
                break;
            case R.id.tv_accselection:
                getAccList();
                break;

            case R.id.tv_download:
                if (!tv_acc_selection.getText().equals("Select Account")){

                    from = etxtFrom.getText().toString();
                    to = etxtTo.getText().toString();
                    submod =tv_acc_selection1.getText().toString();

                    acc=tv_acc_selection2.getText().toString();
                    acc = acc.replace(acc.substring(acc.indexOf(" (")+1, acc.indexOf(")")+1), "");
                    acc = acc.replace(" ","");

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        if (Environment.isExternalStorageManager()){
//                            getAccountStatement(from,to,acc,submod);
//                        }else {
//                            final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                            final Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }
//                    }else{
//                        getAccountStatement(from,to,acc,submod);
//                    }
                    getAccountStatement(from,to,acc,submod);



                }
                else {
                    Toast.makeText(StatementDownloadViewActivity.this, "Please Select Any Account For Download Statement.", Toast.LENGTH_LONG).show();
                }
            break;
            case R.id.tv_view:
                if (!tv_acc_selection.getText().equals("Select Account")){
                    from = etxtFrom.getText().toString();
                    to = etxtTo.getText().toString();
                    submod =tv_acc_selection1.getText().toString();
                    acc=tv_acc_selection2.getText().toString();

                    acc = acc.replace(acc.substring(acc.indexOf(" (")+1, acc.indexOf(")")+1), "");
                    acc = acc.replace(" ","");
                //    getAccountStatement1(from,to,acc,submod);
                }
                else {
                    Toast.makeText(StatementDownloadViewActivity.this, "Please Select Any Account For View Statement.", Toast.LENGTH_LONG).show();
                }

            break;
        }
    }
    private void getAccountStatement1(String from, String to, String acc, String submod) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(StatementDownloadViewActivity.this, R.style.Progress);
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
              //  String reqmode = IScoreApplication.encryptStart("1");
                final JSONObject requestObject1 = new JSONObject();
             //   try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = dateFormat.parse(from);
                    Date d1 = dateFormat.parse(to);
                    //  Date cDate = new Date();
                    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
                    String tDate = new SimpleDateFormat("yyyy-MM-dd").format(d1);

                    accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(acc);
                    branchcode = accountInfo.accountBranchCode;

                    requestObject1.put("SubModule", IScoreApplication.encryptStart(submod));
                    requestObject1.put("FromNo", IScoreApplication.encryptStart(acc));
                    requestObject1.put("FromDate", IScoreApplication.encryptStart(fDate));
                    requestObject1.put("ToDate", IScoreApplication.encryptStart(tDate));
                    requestObject1.put("BranchCode", IScoreApplication.encryptStart(branchcode));
                SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                String BankKey=bankkeypref.getString("bankkey", null);
                SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                String BankHeader=bankheaderpref.getString("bankheader", null);
                requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));


                Log.e("requestObject1  122 ",""+requestObject1);

              /*  }
                catch (Exception e) {
                    e.printStackTrace();

                }*/
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountstatement(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try {

                            Log.e("requestObject1  622 ",""+response.body());
                            String res = response.toString();
                            StringTokenizer tokenss = new StringTokenizer(res, ",");
                            String s = tokenss.nextToken();
                            String s1 = tokenss.nextToken();
                            String s2 =tokenss.nextToken();
                            s2 = s2.replace("\"", "");
                          //  StringTokenizer tokens1 = new StringTokenizer(s2, "=");
                            StringTokenizer tokens1 = new StringTokenizer(s1, "=");
                            //  Log.i("Documentdownload", response.message());
                            String s3 = tokens1.nextToken();
                            String s4 = tokens1.nextToken();
                           // if(!s4.contains("Error"))
                                if(s4.contains("200"))
                            {
                                JSONObject jsonObj = new JSONObject(response.body());


                                if (jsonObj.getString("StatusCode").equals("0")) {
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("StatementOfAccountDet");
                                    Log.i("First ",String.valueOf(jsonObj1));
                                    JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                    Log.i("First1 ",String.valueOf(object));
                                    String filename =object.getString("FilePath");
                                    String filename1 =object.getString("FileName");

                                //    int index = filename.indexOf("Mscore");
                                    int index = filename.indexOf("Statement");
                                    String strNew = filename.substring(index);



                                   /* StringTokenizer tokens = new StringTokenizer(filename, "\\");
                                    String first = tokens.nextToken();// this will contain "Fruit"
                                    String second = tokens.nextToken();
                                    String third = tokens.nextToken();
                                    String four = tokens.nextToken();
                                    String five = tokens.nextToken();*/
                                  //  String six = tokens.nextToken();

                                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    String BASE_URL=pref.getString("baseurl", null);
                                    String filename2 = BASE_URL +"\\"+strNew+"\\"+filename1;
                                   // String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+filename1;
                                    Log.e("Path",filename2+"\n"+filename1);
                                 //   String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+six+"/"+filename1;
//
//                                    Log.e("Path1  ",filename1);
//                                    Log.e("Path2   ",filename2);
//                                    Log.e("Path4   ",four);
//                                    Log.e("Path5   ",five);



//                                    Intent i = new Intent(StatementDownloadViewActivity.this, Viewstatement.class);
//                                    i.putExtra("docx", filename2);
//                                    startActivity(i);

                                }
                                else {

                                    try{
                                        JSONObject jobj = jsonObj.getJSONObject("StatementOfAccountDet");
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StatementDownloadViewActivity.this);
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StatementDownloadViewActivity.this);
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
                            else
                            {
                                alertMessage1("", s4);
                              //  DialogUtil.showAlert(StatementDownloadViewActivity.this,
                                      //  s4);
                            }
                            //      Log.i("viewdocument", response.body());

                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e) {

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
            catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
           /* DialogUtil.showAlert(StatementDownloadViewActivity.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    private void getAccountStatement(String from, String to, String acc, String submod) {


        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(StatementDownloadViewActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try {

               /* OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(getSSLSocketFactory())
                        .hostnameVerifier(getHostnameVerifier())
                        .build();
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Common.getBaseUrl())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                APIInterface apiService = retrofit.create(APIInterface.class);
              //  String reqmode = IScoreApplication.encryptStart("1");
                final JSONObject requestObject1 = new JSONObject();*/


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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = dateFormat.parse(from);
                    Date d1 = dateFormat.parse(to);
                    //  Date cDate = new Date();
                    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
                    String tDate = new SimpleDateFormat("yyyy-MM-dd").format(d1);

                    accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(acc);
                    branchcode = accountInfo.accountBranchCode;

                    requestObject1.put("SubModule", IScoreApplication.encryptStart(submod));
                    requestObject1.put("FromNo", IScoreApplication.encryptStart(acc));
                    requestObject1.put("FromDate", IScoreApplication.encryptStart(fDate));
                    requestObject1.put("ToDate", IScoreApplication.encryptStart(tDate));
                    requestObject1.put("BranchCode", IScoreApplication.encryptStart(branchcode));
                    SharedPreferences bankkeypref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject1","requestObject1   840   "+requestObject1);

                }

                catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountstatement(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        Log.e("response","response   840   "+response.body());
                        try {
                            String res = response.toString();
                            StringTokenizer tokenss = new StringTokenizer(res, ",");
                            String s = tokenss.nextToken();
                            String s1 = tokenss.nextToken();
                            String s2 =tokenss.nextToken();
                            s2 = s2.replace("\"", "");
                          //  StringTokenizer tokens1 = new StringTokenizer(s2, "=");
                            StringTokenizer tokens1 = new StringTokenizer(s1, "=");
                            //  Log.i("Documentdownload", response.message());
                            String s3 = tokens1.nextToken();
                            String s4 = tokens1.nextToken();
                          //  if(!s4.contains("Error"))
                                if(s4.contains("200"))
                            {
                                JSONObject jsonObj = new JSONObject(response.body());


                                if (jsonObj.getString("StatusCode").equals("0")) {
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("StatementOfAccountDet");
                                    Log.i("First ",String.valueOf(jsonObj1));
                                    JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                    Log.i("First1 ",String.valueOf(object));
                                    String filename =object.getString("FilePath");
                                   /* String one = filename.substring(0, filename.length() / 2);  // gives "How ar"
                                    String two = filename.substring(filename.length() / 2);

                                    String strNew = two.replaceFirst("t", "");*/
                                  //  int index = filename.indexOf("Mscore");
                                    int index = filename.indexOf("Statement");
                                    String strNew = filename.substring(index);

                                    String filename1 =object.getString("FileName");
                                   /* StringTokenizer tokens = new StringTokenizer(filename, "\\");
                                    String first = tokens.nextToken();// this will contain "Fruit"
                                    String second = tokens.nextToken();
                                    String third = tokens.nextToken();

                                    String four = tokens.nextToken();
                                    String five = tokens.nextToken();
*/


                                   //  String six = tokens.nextToken();

                                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    String BASE_URL=pref.getString("baseurl", null);
                                    String filename2 = BASE_URL +"\\"+strNew+"\\"+filename1;
                                    //String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+filename1;

                                  //  String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+six+"/"+filename1;
                                    Log.i("Path",filename2+"\n"+filename1);

                                    downloadFile(filename2, filename1);

                                    // JSONArray Jarray  = object.getJSONArray("BranchLocationDetails");
                                    //  int length = object.length()+1;

                                }
                                else {

                                    try{
                                        JSONObject jobj = jsonObj.getJSONObject("StatementOfAccountDet");
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StatementDownloadViewActivity.this);
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StatementDownloadViewActivity.this);
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
                            else{
                                alertMessage1("", s4);
                               /* DialogUtil.showAlert(StatementDownloadViewActivity.this,
                                        s4);*/
                            }
//                            Log.i("Documentdownload", response.body());

                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
            /*DialogUtil.showAlert(StatementDownloadViewActivity.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    //  private void downloadFile(String value, String filename1) {
    private void downloadFile(String filename2, String filename1) {


        try {
            long filename3 = System.currentTimeMillis();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download"+ "/");
            boolean isPresent = true;
            Log.e("photoURI","StatementDownloadViewActivity   5682   ");
            if (!docsFolder.exists()) {
               // isPresent = docsFolder.mkdir();
                docsFolder.mkdir();
                Log.e("photoURI","StatementDownloadViewActivity   5683   ");
            }

            Log.e("Exception","StatementDownloadViewActivity  1012   ");
            DownloadRequest request = new DownloadRequest().setDownloadId(39)
//                .setSimpleDownloadListener(new SimpleListener())
//                    .setRetryTime(2).setDestDirectory(
//                            Environment
//                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                                    .getAbsolutePath() ).setFileName(filename1)
//                    .setRetryTime(2).setDestDirectory(docsFolder.toString())
                    .setRetryTime(2).setDestDirectory(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .getAbsolutePath() ).setFileName(filename3+"-"+filename1)
                    .setAllowedNetworkTypes(this, DownloadRequest.NETWORK_WIFI)
                    .setDownloadListener(new Listener())
                    .setProgressInterval(1000).setUrl(filename2);
            mDownloadManager.add(request);
        }catch (Exception e){
            Log.e("Exception","StatementDownloadViewActivity  10122   "+e.toString());
        }



     /*    .setRetryTime(2).setDestDirectory(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath() + "/" + "mScore/").setFileName(filename1)
                .setAllowedNetworkTypes(this, DownloadRequest.NETWORK_WIFI)
                .setDownloadListener(new Listener())
                .setProgressInterval(1000).setUrl(filename2);
        mDownloadManager.add(request);*/

    }


    private class Listener implements DownloadListener {
       /* private long mStartTimestamp = 0;
        private final long mStartSize = 0;*/

        @Override
        public void onStart(int downloadId, long totalBytes) {

            /* mStartTimestamp = System.currentTimeMillis();*/
        }

        @Override
        public void onRetry(int downloadId) {
//            Log.d(TAG, "downloadId: " + downloadId);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
            int progress = (int) (bytesWritten * 100f / totalBytes);
            progress = progress == 100 ? 0 : progress;
//            long currentTimestamp = System.currentTimeMillis();

            progressDialog.setProgress(progress);
//            Log.d(TAG, "progress: " + progress);
//
//            mTextStatus.setText(
//                    "In progress - Total bytes : " + totalBytes + " downloaded bytes : " + bytesWritten);

//            int speed;
//            int deltaTime = (int) (currentTimestamp - mStartTimestamp + 1);
//            speed = (int) ((bytesWritten - mStartSize) * 1000 / deltaTime) / 1024;

//            switch (downloadId) {
//                case DOWNLOAD_ID0:
//                    mProgressBar.setProgress(progress);
//                    mTextSpeed.setText(speed + "kb/s");
//                    break;
//
//                //			case DOWNLOAD_ID1:
//                //				mProgressBar1.setProgress(progress);
//                //				mTextSpeed1.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID2:
//                //				mProgressBar2.setProgress(progress);
//                //				mTextSpeed2.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID3:
//                //				mProgressBar3.setProgress(progress);
//                //				mTextSpeed3.setText(speed + "kb/s");
//                //				break;
//                //
//                //			case DOWNLOAD_ID4:
//                //				mProgressBar4.setProgress(progress);
//                //				mTextSpeed4.setText(speed + "kb/s");
//                //				break;
//
//                default:
//                    break;
//            }
        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
            progressDialog.dismiss();
         //   Toast.makeText(StatementDownloadViewActivity.this, filePath, Toast.LENGTH_SHORT).show();
           /* DialogUtil.showAlert(StatementDownloadViewActivity.this,
                    "Statement Downloaded Successfuly"+"\n"+filePath);*/

            alertMessage1("Statement Downloaded Successfully" +"\n"+filePath, "");

         /*   if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                try{
                    final Uri data = FileProvider.getUriForFile(
                            StatementDownloadViewActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( filePath )
                    );
                    StatementDownloadViewActivity.this.grantUriPermission( StatementDownloadViewActivity.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(data, "application/pdf")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }catch ( Exception e ){
                    if ( IScoreApplication.DEBUG ){
                        Log.e("error", e.toString() );
                    }
                }
            }else {
                File file = new File(filePath);
                Intent target = new Intent( Intent.ACTION_VIEW );
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    DialogUtil.showAlert(StatementDownloadViewActivity.this,
                            "Please install App to open PDF file");
                }
            }
*/
//            mTextStatus.setText("Success : " + filePath);
//            Log.d(TAG, "success: " + downloadId + " size: " + new File(filePath).length());
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
            progressDialog.dismiss();
            alertMessage1("", "Not able to download PDF file, Please try again later");
           /* DialogUtil.showAlert(StatementDownloadViewActivity.this,
                    "Not able to download PDF file, Please try again later");*/

//            mTextStatus.setText("onFailure : " +  statusCode + " errMsg " + errMsg);
//            Log.d(TAG, "fail: " + downloadId + " " + statusCode + " " + errMsg);
        }
    }
    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StatementDownloadViewActivity.this);

        LayoutInflater inflater =this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        //  TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);
        if(msg1.equals(""))
        {
            tv_msg.setText(msg2);
        }
        else
        {
            tv_msg.setText(msg1);
        }
  //      tv_msg.setText(msg1);
        //  tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // displaypdf(msg2);
          /*     Intent viewDownloadsIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(viewDownloadsIntent);*/

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                    try{
                        final Uri data = FileProvider.getUriForFile(
                                StatementDownloadViewActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( msg2 )
                        );
                        StatementDownloadViewActivity.this.grantUriPermission( StatementDownloadViewActivity.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(data, "application/pdf")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }catch ( Exception e ){
                        if ( IScoreApplication.DEBUG ){
                            Log.e("error", e.toString() );
                        }
                    }
                }else {
                    File file = new File(msg2);
                    Intent target = new Intent( Intent.ACTION_VIEW );
                    target.setDataAndType(Uri.fromFile(file), "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        alertMessage1("", "Please install App to open PDF file");
                      /*  DialogUtil.showAlert(StatementDownloadViewActivity.this,
                                "Please install App to open PDF file");*/
                    }
                }

                alertDialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
