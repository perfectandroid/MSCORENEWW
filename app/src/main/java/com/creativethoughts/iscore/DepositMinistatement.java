package com.creativethoughts.iscore;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coolerfall.download.DownloadListener;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.MiniStatementTranscationListAdapter;
import com.creativethoughts.iscore.adapters.MinistatementAdapter;
import com.creativethoughts.iscore.db.dao.NewTransactionDAO;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.Transaction;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DepositMinistatement extends AppCompatActivity implements View.OnClickListener {

    String TAG = "DepositMinistatement";
    RecyclerView rv_passbook;
    LinearLayout ll_download,ll_view;
    MinistatementAdapter adapter;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ColumnChartView columnChartView;
    ColumnChartData data;
    int indexofmonth;
    private DownloadManager mDownloadManager;
    TextView tv_accno, tv_bal,tvtxn,tvGraph, empty_list;
    CardView cv_transaction,cv_graph;
    LinearLayout llgraph,lltxn;
    public String acChange, amt, submodule;
    EditText etxtFrom, etxtTo;
    String branchcode, accNewChange, EnableDownloadStatement, fkaccount;
    Spinner im_month;
    String[] month = { "January", "February", "March", "April", "May","June","July","August",
    "September","October","November","December"};
    private ProgressDialog progressDialog;
    Date newDate, datecurrent;
    AccountInfo accountInfo;
    private ProgressDialog pd;
    JSONArray Jarray = new JSONArray();
    RadioButton rb1,rb2;
    ArrayAdapter aa;
    LinearLayout llstatement;
    int noofdays;
    public static final int RequestPermissionCode = 7;
    public static final int RequestManagePermissionCode = 5;

    private  String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ministatement);
        mDownloadManager = new DownloadManager();

        Log.e(TAG,"START   156  ");
        fkaccount = getIntent().getStringExtra("account");
        acChange = getIntent().getStringExtra("accno");
        accNewChange = acChange;
        accNewChange = accNewChange.replace(accNewChange.substring(accNewChange.indexOf(" (")+1, accNewChange.indexOf(")")+1), "");
        accNewChange = accNewChange.replace(" ","");

        amt = getIntent().getStringExtra("amt");
        submodule = getIntent().getStringExtra("submodule");

        accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accNewChange);
        EnableDownloadStatement = getIntent().getStringExtra("EnableDownloadStatement");

        branchcode = accountInfo.accountBranchCode;

        progressDialog = new ProgressDialog(DepositMinistatement.this);
        setRegViews();
        tv_accno.setText(acChange);
        tv_bal.setText(amt);

        Log.e(TAG,"START    180");

      //  prepareListData();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if(EnableDownloadStatement.equals("1")){
            llstatement.setVisibility(View.VISIBLE);
        }else{
            llstatement.setVisibility(View.GONE);
        }

        SharedPreferences SelectedDaysSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF37, 0);
        String SelectedDays = SelectedDaysSP.getString("SelectedDays",null);
        //SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
        if (SelectedDays == null || Integer.parseInt(SelectedDays) <= 0) {
            noofdays = 30;
        } else {
            noofdays = Integer.parseInt(SelectedDays);
        }

        getPassBookAccountStatement(fkaccount,submodule, noofdays);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setRegViews() {

        llstatement =  findViewById(R.id.llstatement);
        rv_passbook =  findViewById(R.id.rv_passbook);

        columnChartView = (ColumnChartView) findViewById(R.id.chart);

        tv_accno = (TextView) findViewById(R.id.tv_accno);
        tv_bal = (TextView) findViewById(R.id.tv_bal);
        empty_list = (TextView) findViewById(R.id.empty_list);



        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        ll_download.setOnClickListener(this);
        ll_view = (LinearLayout) findViewById(R.id.ll_view);
        ll_view.setOnClickListener(this);


        tvGraph         = findViewById(R.id.tvGraph);
        tvtxn           = findViewById(R.id.tvtxn);
        cv_transaction  = findViewById(R.id.cv_transaction);
        cv_graph        = findViewById(R.id.cv_graph);
        llgraph         = findViewById(R.id.llgraph);
        lltxn           = findViewById(R.id.lltxn);
        tvGraph.setOnClickListener(this);
        tvtxn.setOnClickListener(this);



    }

//    private void prepareListData() {
//
//        ArrayList<Transaction> transactions =
//                NewTransactionDAO.getInstance().getTransactions2(accNewChange);
//
//        if (transactions.size() == 0) {
//
//            SettingsModel settingsModel = SettingsDAO.getInstance().getDetails();
//
//            final int days;
//
//            if (settingsModel == null || settingsModel.days <= 0) {
//                days = 30;
//            } else {
//                days = settingsModel.days;
//            }
//
//
//            rv_passbook.setVisibility(View.GONE);
//            cv_transaction.setVisibility(View.GONE);
//
//            return;
//        }
//        rv_passbook.setVisibility(View.VISIBLE);
//        cv_transaction.setVisibility(View.VISIBLE);
//
//
//        sortAccordingToTime(transactions);
//        JSONArray jsonArray = new JSONArray();
//        for (int i = 0; i < transactions.size(); i++) {
//            jsonArray.put(transactions.get(i).getJSONObject());
//        }
//        System.out.println(jsonArray);
//        columnChartView.setColumnChartData(generateColumnChartData(jsonArray));
//        columnChartView.setZoomType(ZoomType.VERTICAL);
//
//
//       /* adapter = new MinistatementAdapter(DepositMinistatement.this, R.layout.ministatement_list_row, transactions);
//        lv_passbook.setAdapter(adapter);*/
//
//
//
//    }

    private ColumnChartData generateColumnChartData(JSONArray jsonArray) {
        try {
            if (jsonArray.length() != 0) {
                List<Column> columns = new ArrayList<Column>();
                List<SubcolumnValue> values;
                values = new ArrayList<SubcolumnValue>();
                int inlenght;
                if(jsonArray.length()<=10){
                    inlenght=jsonArray.length();
                }else{
                    inlenght=11;
                }
                for (int i = 0; i < inlenght; i++) {
                    JSONObject qstnArray = null;
                    qstnArray = jsonArray.getJSONObject(i);

                    if (qstnArray.getString("TransType").equals("D")) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.FireBrick)));
                        // values.setLabel("some_label".toCharArray());
                    } else {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.Green)));
                    }
                    // values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph2)));

                }
                columns.add(new Column(values));
                data = new ColumnChartData(columns);
                data.setAxisYLeft(new Axis().setName("     ").setHasLines(true).setTextColor(Color.BLACK));
                //  data.setAxisXBottom(new Axis().setName("").setHasLines(true).setTextColor(Color.BLACK));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("No data found.")
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
        }
        return data;

    }



//    public static void sortAccordingToTime(ArrayList<Transaction> sortlist) {
//        CountryComparator comparator = new CountryComparator();
//
//        Collections.sort(sortlist, comparator);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download:
                popupDownloaddoc();
                // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.ll_view:
              //  popupDownloaddoc1();
            //    Intent i = new Intent(DepositMinistatement.this, Viewstatement.class);
            //    startActivity(i);
                break;

            case R.id.tvtxn:
                lltxn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.under_line));
                llgraph.setBackgroundDrawable(null);
                tvtxn.setTextColor(getResources().getColor(R.color.blue_variant1));
                tvGraph.setTextColor(getResources().getColor(R.color.black));
                ArrayList<Transaction> transactions = NewTransactionDAO.getInstance().getTransactions2(accNewChange);
//                cv_transaction.setVisibility(View.VISIBLE);
//                cv_graph.setVisibility(View.GONE);
                try{
                    if (!Jarray.toString().equals("null")){
                        if(Jarray.length()!=0) {
                            cv_transaction.setVisibility(View.VISIBLE);
                            cv_graph.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No Data To Display.",Toast.LENGTH_LONG).show();
                            cv_transaction.setVisibility(View.GONE);
                        }
                    }

                }
                catch (Exception e) {
                    cv_transaction.setVisibility(View.GONE);

                }
               /* if (transactions.size() == 0) {
                    cv_transaction.setVisibility(View.GONE);
                     Toast.makeText(getApplicationContext(),"No Data To Display.",Toast.LENGTH_LONG).show();
                }
                else {
                    cv_transaction.setVisibility(View.VISIBLE);
                }
                cv_graph.setVisibility(View.GONE);*/
                break;
            case  R.id.tvGraph:
                llgraph.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.under_line));
                lltxn.setBackgroundDrawable(null);
                tvtxn.setTextColor(getResources().getColor(R.color.black));
                tvGraph.setTextColor(getResources().getColor(R.color.blue_variant1));
                try{
                    if (!Jarray.toString().equals("null")){
                        if(Jarray.length()!=0) {
                            cv_graph.setVisibility(View.VISIBLE);
                            cv_transaction.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No Data To Display.",Toast.LENGTH_LONG).show();
                          //  cv_graph.setVisibility(View.GONE);
                        }
                    }

                }
                catch (Exception e) {
                    cv_graph.setVisibility(View.GONE);
                }
//                cv_transaction.setVisibility(View.GONE);
//                cv_graph.setVisibility(View.VISIBLE);
              /*  ArrayList<Transaction> transactionsf = NewTransactionDAO.getInstance().getTransactions2(accNewChange);
                if (transactionsf.size() == 0) {
                    Toast.makeText(getApplicationContext(),"No Data To Display.",Toast.LENGTH_LONG).show();
                    cv_graph.setVisibility(View.GONE);
                }
                else {
                    cv_graph.setVisibility(View.VISIBLE);
                }*/
                break;
        }
    }


//    private static class CountryComparator implements Comparator<Transaction> {
//        public int compare(Transaction left, Transaction right) {
//
//            String leftEffectiveDate = left.effectDate;
//            String rightEffectiveDate = right.effectDate;
//
//            if (TextUtils.isEmpty(leftEffectiveDate) || TextUtils.isEmpty(rightEffectiveDate)) {
//                return 0;
//            }
//
//            Date leftDate = getDate(leftEffectiveDate);
//            Date rightDate = getDate(rightEffectiveDate);
//
//            if (leftDate == null || rightDate == null) {
//                return 0;
//            }
//
//            return (int) ((rightDate.getTime() / 100000) - (leftDate.getTime() / 100000));
//
//        }
//    }

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

          progressDialog.setProgress(progress);

        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
          progressDialog.dismiss();
        //   Toast.makeText(DepositMinistatement.this, filePath, Toast.LENGTH_SHORT).show();
         /* DialogUtil.showAlert(DepositMinistatement.this,
                  "Statement Downloaded Successfuly"+"\n"+filePath);*/

               alertMessage1("Statement Downloaded Successfully"+"\n"+filePath , "");

        /*  if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
              try{
                  final Uri data = FileProvider.getUriForFile(
                          DepositMinistatement.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( filePath )
                  );
                  DepositMinistatement.this.grantUriPermission( DepositMinistatement.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                  DialogUtil.showAlert(DepositMinistatement.this,
                          "Please install App to open PDF file");
              }
          }*/

        //            mTextStatus.setText("Success : " + filePath);
        //            Log.d(TAG, "success: " + downloadId + " size: " + new File(filePath).length());
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
          progressDialog.dismiss();
          Log.e("TAG","errMsg  590     "+errMsg);
          alertMessage1("", "Not able to download PDF file, Please try again later");
        /*  DialogUtil.showAlert(DepositMinistatement.this,
                  "Not able to download PDF file, Please try again later");*/

        //            mTextStatus.setText("onFailure : " +  statusCode + " errMsg " + errMsg);
        //            Log.d(TAG, "fail: " + downloadId + " " + statusCode + " " + errMsg);
        }
    }

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }


    private void popupDownloaddoc() {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.download_popup, null);
            EditText tv_msg = layout.findViewById(R.id.etxt_acc);
            LinearLayout l1= layout.findViewById(R.id.l1);
            LinearLayout l0= layout.findViewById(R.id.l0);
            etxtFrom = (EditText) layout.findViewById(R.id.etxtFrom);
            etxtTo = (EditText) layout.findViewById(R.id.etxtTo);
            TextView tv_cancel = layout.findViewById(R.id.tv_cancel);
            TextView tv_share = layout.findViewById(R.id.tv_share);
            rb1 = layout.findViewById(R.id.rb1);
            rb2 = layout.findViewById(R.id.rb2);
            im_month = layout.findViewById(R.id.imgv_month);




            String no = tv_accno.getText().toString();
            tv_msg.setText(no);

            rb1.setChecked(true);

            if(rb1.isChecked())
            {
                im_month.setVisibility(View.VISIBLE);
                rb2.setChecked(false);
                l0.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);

            }
            else if(rb2.isChecked())
            {
                im_month.setVisibility(View.GONE);
                rb1.setChecked(false);
                l0.setVisibility(View.VISIBLE);
                l1.setVisibility(View.VISIBLE);


            }
            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rb2.setChecked(false);
                    etxtFrom.setText("");
                    etxtTo.setText("");
                    im_month.setVisibility(View.VISIBLE);
                    l0.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                }
            });
            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rb1.setChecked(false);
                    l0.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);

                    im_month.setVisibility(View.GONE);
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
                }
            });

            //  im_month.setOnItemSelectedListener(this);
            aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,month);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            im_month.setAdapter(aa);

               indexofmonth = Calendar.getInstance().get(Calendar.MONTH);
              im_month.setSelection(indexofmonth);


            im_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                    mMonth = im_month.getSelectedItemPosition();
                    String month = im_month.getSelectedItem().toString();


                    if(rb1.isChecked())
                    {
                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);

                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String formattedDate = df.format(c);
                        Log.i("Current Date",formattedDate);

                        StringTokenizer tokens = new StringTokenizer(formattedDate, "-");
                        String first = tokens.nextToken();// this will contain "Fruit"
                        String second = tokens.nextToken();
                        String third = tokens.nextToken();



                        String firstday;
                        if(mMonth==11||mMonth==10)
                        {



                            try {
                                int m =mMonth+1;
                                firstday = "01"+"-"+m+"-"+third;
                                Log.i("Firstday",firstday);
                                String date = firstday;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date convertedDate = null;
                                convertedDate = dateFormat.parse(date);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(convertedDate);
                                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                                int res = cal.getActualMaximum(Calendar.DATE);
                            /*  etxtTo.setText(String.valueOf(res)+"-"+m+"-"+third);
                              Log.i("Res",String.valueOf(res)+"-"+m+"-"+third);
                              etxtFrom.setText(firstday);*/

                                String lastday = res+"-"+m+"-"+third;
                                etxtTo.setText(lastday);
                                Log.i("Res",lastday);
                                etxtFrom.setText(firstday);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {



                            try {
                                int m =mMonth+1;
                                String mon = "0"+m;
                                firstday = "01"+"-"+mon+"-"+third;
                                Log.i("Firstday",firstday);
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
                                Log.i("Res",lastday);
                                etxtFrom.setText(firstday);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }


                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView)
                {

                }
            });

            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();

            etxtFrom.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {

                        if(rb2.isChecked())
                        {
                            l0.setVisibility(View.VISIBLE);
                            l1.setVisibility(View.VISIBLE);
                            Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);

                            mMonth = c.get(Calendar.MONTH);
                            //   mMonth = im_month.getSelectedItemPosition();
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();

                        }
                    }


                    return true; // return is important...
                }
            });
            etxtTo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        if(rb2.isChecked())
                        {
                            l0.setVisibility(View.VISIBLE);
                            l1.setVisibility(View.VISIBLE);
                            Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);

                            mMonth = c.get(Calendar.MONTH);
                            //   mMonth = im_month.getSelectedItemPosition();
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();

                        }
                    }



                    return true; // return is important...
                }
            });

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                }
            });
            tv_share.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                    String from = etxtFrom.getText().toString();
                    String to = etxtTo.getText().toString();
                    String acc = tv_msg.getText().toString();

                    acc = acc.replace(acc.substring(acc.indexOf(" (")+1, acc.indexOf(")")+1), "");
                    acc = acc.replace(" ","");

                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                    String BASE_URL=pref.getString("baseurl", null);

                    String value = BASE_URL + "/Mscore/Statement/ASD7.pdf";
                    String fileUrl = value;
                    String fileName = "ASD7.pdf";
                    //   downloadFile(value, fileName);
//
//                    final int REQUEST_EXTERNAL_STORAGE = 1;
//                    String[] PERMISSIONS_STORAGE = {
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE };
//                    if (ContextCompat.checkSelfPermission(DepositMinistatement.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(DepositMinistatement.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
//                    } else {
//                        getAccountStatement(from,to,acc);
//                    }

//                    if (checkDiskPermission()){
//                        getAccountStatement(from,to,acc);
//                    }
//
//                    if (checkPermission()){
//                        getAccountStatement(from,to,acc);
//                    }

//                    if(CheckingPermissionIsEnabledOrNot())
//                    {
//                        Toast.makeText(DepositMinistatement.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
//                    }
//
//                    // If, If permission is not enabled then else condition will execute.
//                    else {
//
//                        //Calling method to enable permission.
//                        RequestMultiplePermission();
//                        //Toast.makeText(DepositMinistatement.this, "No Permissions Granted ", Toast.LENGTH_LONG).show();
//
//                    }

                //    getAccountStatement(from,to,acc);

//                    if (!hasPermissions(DepositMinistatement.this,PERMISSIONS)){
//                        ActivityCompat.requestPermissions(DepositMinistatement.this,PERMISSIONS,1);
//                    }

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                         if (Environment.isExternalStorageManager()){
//                             getAccountStatement(from,to,acc);
//                        }else {
//                            final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                            final Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }
//                    }
//                    else {
//                        getAccountStatement(from,to,acc);
//                    }
                    getAccountStatement(from,to,acc);


                }
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popupDownloaddoc1() {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.view_popup, null);
            EditText tv_msg = layout.findViewById(R.id.etxt_acc);
            LinearLayout l0= layout.findViewById(R.id.l0);
            LinearLayout l1= layout.findViewById(R.id.l1);
            etxtFrom = (EditText) layout.findViewById(R.id.etxtFrom);
            etxtTo = (EditText) layout.findViewById(R.id.etxtTo);
            TextView tv_cancel = layout.findViewById(R.id.tv_cancel);
            TextView tv_share = layout.findViewById(R.id.tv_share);
            im_month = layout.findViewById(R.id.imgv_month);
            String no = tv_accno.getText().toString();
            tv_msg.setText(no);
            rb1 = layout.findViewById(R.id.rb1);
            rb2 = layout.findViewById(R.id.rb2);
            im_month = layout.findViewById(R.id.imgv_month);
            //  im_month.setOnItemSelectedListener(this);

            rb1.setChecked(true);

            if(rb1.isChecked())
            {
                im_month.setVisibility(View.VISIBLE);
                rb2.setChecked(false);
                l0.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
            }
            else if(rb2.isChecked())
            {
                im_month.setVisibility(View.GONE);
                rb1.setChecked(false);
                l0.setVisibility(View.VISIBLE);
                l1.setVisibility(View.VISIBLE);
            }
            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rb2.setChecked(false);
                    etxtFrom.setText("");
                    etxtTo.setText("");
                    im_month.setVisibility(View.VISIBLE);
                    l0.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                }
            });
            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rb1.setChecked(false);
                    l0.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    im_month.setVisibility(View.GONE);
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
                }
            });


            ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,month);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            im_month.setAdapter(aa);

            int indexofmonth = Calendar.getInstance().get(Calendar.MONTH);
            im_month.setSelection(indexofmonth);

            im_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                    mMonth = im_month.getSelectedItemPosition();

                    if(rb1.isChecked())
                    {
                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);

                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String formattedDate = df.format(c);
                        Log.i("Current Date",formattedDate);

                        StringTokenizer tokens = new StringTokenizer(formattedDate, "-");
                        String first = tokens.nextToken();// this will contain "Fruit"
                        String second = tokens.nextToken();
                        String third = tokens.nextToken();



                        String firstday;
                        if(mMonth==11||mMonth==10)
                        {



                            try {
                                int m =mMonth+1;
                                firstday = "01"+"-"+m+"-"+third;
                                Log.i("Firstday",firstday);
                                String date = firstday;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Date convertedDate = null;
                                convertedDate = dateFormat.parse(date);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(convertedDate);
                                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                                int res = cal.getActualMaximum(Calendar.DATE);
                            /*  etxtTo.setText(String.valueOf(res)+"-"+m+"-"+third);
                              Log.i("Res",String.valueOf(res)+"-"+m+"-"+third);
                              etxtFrom.setText(firstday);*/

                                String lastday = res+"-"+m+"-"+third;
                                etxtTo.setText(lastday);
                                Log.i("Res",lastday);
                                etxtFrom.setText(firstday);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {



                            try {
                                int m =mMonth+1;
                                String mon = "0"+m;
                                firstday = "01"+"-"+mon+"-"+third;
                                Log.i("Firstday",firstday);
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
                                Log.i("Res",lastday);
                                etxtFrom.setText(firstday);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView)
                {

                }
            });


            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();


            etxtFrom.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {

                        if(rb2.isChecked())
                        {
                            l0.setVisibility(View.VISIBLE);
                            l1.setVisibility(View.VISIBLE);
                            Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);

                            mMonth = c.get(Calendar.MONTH);
                            //   mMonth = im_month.getSelectedItemPosition();
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();

                        }
                    }

                    return true; // return is important...
                }
            });
            etxtTo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        if(rb2.isChecked())
                        {
                            l0.setVisibility(View.VISIBLE);
                            l1.setVisibility(View.VISIBLE);
                            Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);

                            mMonth = c.get(Calendar.MONTH);
                            //   mMonth = im_month.getSelectedItemPosition();
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();

                        }
                    }



                    return true; // return is important...
                }
            });

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
                    String from = etxtFrom.getText().toString();
                    String to = etxtTo.getText().toString();
                    String acc = tv_msg.getText().toString();

                    acc = acc.replace(acc.substring(acc.indexOf(" (")+1, acc.indexOf(")")+1), "");
                    acc = acc.replace(" ","");

                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                    String BASE_URL=pref.getString("baseurl", null);

                    String value = BASE_URL + "/Mscore/Statement/ASD7.pdf";
                    String fileUrl = value;
                    String fileName = "ASD7.pdf";
                    //   downloadFile(value, fileName);

                    getAccountStatement1(from,to,acc);


                }
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getAccountStatement1(String from, String to, String acc) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(DepositMinistatement.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("1");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = dateFormat.parse(from);
                    Date d1 = dateFormat.parse(to);
                    //  Date cDate = new Date();
                    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
                    String tDate = new SimpleDateFormat("yyyy-MM-dd").format(d1);


                    requestObject1.put("SubModule", IScoreApplication.encryptStart(submodule));
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

                    Log.e("requestObject1","requestObject1    2459   "+requestObject1);


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountstatement(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try {
                            //      Log.i("viewdocument", response.body());
                            Log.e("onResponse","onResponse    2459   "+response.body());
                            String res = response.toString();
                            StringTokenizer tokenss = new StringTokenizer(res, ",");
                            String s = tokenss.nextToken();
                            String s1 = tokenss.nextToken();
                            String s2 =tokenss.nextToken();
                            s2 = s2.replace("\"", "");
                           // StringTokenizer tokens1 = new StringTokenizer(s2, "=");
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
                                    Log.e("filename","filename    2459   "+filename);
                                    String filename1 =object.getString("FileName");

                                  //  int index = filename.indexOf("Mscore");
                                    int index = filename.indexOf("Statement");
                                    String strNew = filename.substring(index);

                                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    String BASE_URL=pref.getString("baseurl", null);
                                    String filename2 = BASE_URL +"\\"+strNew+"\\"+filename1;


                                /*    StringTokenizer tokens = new StringTokenizer(filename, "\\");
                                    String first = tokens.nextToken();// this will contain "Fruit"
                                    String second = tokens.nextToken();
                                    String third = tokens.nextToken();
                                    String four = tokens.nextToken();
                                    String five = tokens.nextToken();*/
                                    // String six = tokens.nextToken();

                                  //  String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+filename1;
                                    Log.e("Path  2459  ",filename2+"\n"+filename1);

//                                    Intent i = new Intent(DepositMinistatement.this, Viewstatement.class);
//                                    i.putExtra("docx", filename2);
//                                    startActivity(i);

                                }
                                else {

                                    try{
                                        JSONObject jobj = jsonObj.getJSONObject("StatementOfAccountDet");
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DepositMinistatement.this);
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DepositMinistatement.this);
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
                              /*  DialogUtil.showAlert(DepositMinistatement.this,
                                        s4);*/
                            }


                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } catch (Exception e) {

            }
        } else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
           /* DialogUtil.showAlert(DepositMinistatement.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    private void getAccountStatement(String from, String to, String acc) {


        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(DepositMinistatement.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("1");
                final JSONObject requestObject1 = new JSONObject();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = dateFormat.parse(from);
                    Date d1 = dateFormat.parse(to);
                    //  Date cDate = new Date();
                    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
                    String tDate = new SimpleDateFormat("yyyy-MM-dd").format(d1);


                    requestObject1.put("SubModule", IScoreApplication.encryptStart(submodule));
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


                } catch (Exception e) {
                    e.printStackTrace();

                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAccountstatement(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try {
                            String res = response.toString();
                            StringTokenizer tokenss = new StringTokenizer(res, ",");
                            String s = tokenss.nextToken();
                            String s1 = tokenss.nextToken();
                            String s2 =tokenss.nextToken();
                            s2 = s2.replace("\"", "");
                         //   StringTokenizer tokens1 = new StringTokenizer(s2, "=");
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
                                    String filename1 =object.getString("FileName");

                                    //int index = filename.indexOf("Mscore");
                                    int index = filename.indexOf("Statement");
                                    String strNew = filename.substring(index);
                                   /* StringTokenizer tokens = new StringTokenizer(filename, "\\");
                                    String first = tokens.nextToken();// this will contain "Fruit"
                                    String second = tokens.nextToken();
                                    String third = tokens.nextToken();
                                    String four = tokens.nextToken();
                                    String five = tokens.nextToken();*/
                                    // String six = tokens.nextToken();

                                    SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    String BASE_URL=pref.getString("baseurl", null);
                                    String filename2 = BASE_URL +"\\"+strNew+"\\"+filename1;

                                  //  String filename2 = Common.getBaseUrl() + "/"+four+"/"+five+"/"+filename1;
                                    Log.i("Path",filename2+"\n"+filename1);

                                    downloadFile(filename2, filename1);

                                    // JSONArray Jarray  = object.getJSONArray("BranchLocationDetails");
                                    //  int length = object.length()+1;

                                }
                                else {

                                    try{
                                        JSONObject jobj = jsonObj.getJSONObject("StatementOfAccountDet");
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DepositMinistatement.this);
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DepositMinistatement.this);
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
                              /*  DialogUtil.showAlert(DepositMinistatement.this,
                                       s4);*/
                            }

                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } catch (Exception e) {

            }
        } else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
           /* DialogUtil.showAlert(DepositMinistatement.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    //  private void downloadFile(String value, String filename1) {

    private void downloadFile(String filename2, String filename1) {


        Log.e("rrrrrrr","rrrrrrrrrr");
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

            DownloadRequest request = new DownloadRequest().setDownloadId(39)
//                .setSimpleDownloadListener(new SimpleListener())
                    .setRetryTime(2).setDestDirectory(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .getAbsolutePath() ).setFileName(filename3+"-"+filename1)
                    .setAllowedNetworkTypes(this, DownloadRequest.NETWORK_MOBILE | DownloadRequest.NETWORK_WIFI)
                    .setDownloadListener(new Listener())
                    .setProgressInterval(1000).setUrl(filename2);
            mDownloadManager.add(request);
        }catch (Exception e){
            Log.e("Exception","Exception   1664   "+e.toString());
        }




    }





    private void getPassBookAccountStatement(String fkaccount, String SubModule, int NoOfDays) {
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

                    requestObject1.put("ReqMode",       IScoreApplication.encryptStart("28"));
                    requestObject1.put("Token",         IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Account",   IScoreApplication.encryptStart(fkaccount));
                    requestObject1.put("SubModule",   IScoreApplication.encryptStart(SubModule));
                    requestObject1.put("NoOfDays",   IScoreApplication.encryptStart(""+NoOfDays));
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
                Call<String> call = apiService.getPassBookAccountStatement(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {

                                JSONObject jsonObj1 = jsonObj.getJSONObject("PassBookAccountStatement");
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                JSONArray jstatement  = object.getJSONArray("PassBookAccountStatementList");
                                Jarray = jstatement;

                                columnChartView.setColumnChartData(generateColumnChartData(jstatement));
                                columnChartView.setZoomType(ZoomType.VERTICAL);
                              //  if(jstatement.length()!=0) {
                                rv_passbook.setVisibility(View.VISIBLE);
                                cv_transaction.setVisibility(View.VISIBLE);

                                empty_list.setVisibility(View.GONE);
                                    GridLayoutManager lLayout = new GridLayoutManager(DepositMinistatement.this, 1);
                                    rv_passbook.setLayoutManager(lLayout);
                                    rv_passbook.setHasFixedSize(true);
                                    MiniStatementTranscationListAdapter adapter = new MiniStatementTranscationListAdapter(DepositMinistatement.this, jstatement, SubModule);
                                    rv_passbook.setAdapter(adapter);
                             /*   }
                                else{
                                    rv_passbook.setVisibility(View.GONE);
                                    empty_list.setVisibility(View.VISIBLE);
                                    empty_list.setText("There are no transactions to display for the last " + noofdays + " days");
                                }*/
                            }
                            else {
                                rv_passbook.setVisibility(View.GONE);
                                cv_transaction.setVisibility(View.VISIBLE);
                                empty_list.setVisibility(View.VISIBLE);
                                try{
                                    JSONObject jobj = jsonObj.getJSONObject("PassBookAccountDetails");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    empty_list.setText(ResponseMessage);
                                }
                                catch (Exception e) {
                                    empty_list.setText(jsonObj.getString("EXMessage"));

                                }
                            }
                        }
                        catch (JSONException e) {
                            rv_passbook.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { }
                });
            }
            catch (Exception e) { }
        }
        else {
            alertMessage1("", "Network is currently unavailable. Please try again later.");
          //  DialogUtil.showAlert(DepositMinistatement.this,"Network is currently unavailable. Please try again later.");
        }

    }


    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DepositMinistatement.this);

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
    //    tv_msg.setText(msg1);
        //  tv_msg2.setText(msg2);
        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // displaypdf(msg2);
          /*     Intent viewDownloadsIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(viewDownloadsIntent);*/

                if ( SDK_INT >= Build.VERSION_CODES.N ){
                    try{
                        final Uri data = FileProvider.getUriForFile(
                                DepositMinistatement.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( msg2 )
                        );
                        DepositMinistatement.this.grantUriPermission( DepositMinistatement.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                       /* DialogUtil.showAlert(DepositMinistatement.this,
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




//    private boolean checkDiskPermission ()
//
//    {
//
//        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//         || ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//         || ActivityCompat.checkSelfPermission(this, MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(this, "No Permissions" , Toast.LENGTH_LONG).show();
//
//            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 0);
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 0);
//            ActivityCompat.requestPermissions(this, new String[]{MANAGE_EXTERNAL_STORAGE}, 0);
//
//        }
//        else
//        {
//            Toast.makeText(this, "Has Permissions" , Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        return false;
//    }




//    public boolean CheckingPermissionIsEnabledOrNot() {
//
//        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
//        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), MANAGE_EXTERNAL_STORAGE);
//
//
//        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
//                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
//                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED ;
//    }
//
//
//    private void RequestMultiplePermission() {
//
//        // Creating String Array with Permissions.
//        ActivityCompat.requestPermissions(DepositMinistatement.this, new String[]
//                {
//                        READ_EXTERNAL_STORAGE,
//                        WRITE_EXTERNAL_STORAGE,
//                        MANAGE_EXTERNAL_STORAGE
//                }, RequestPermissionCode);
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case RequestPermissionCode:
//                if (grantResults.length > 0) {
//
//                    boolean ReadPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean WritePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
////                    boolean ManagePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
//
//
//                    if (ReadPermission && WritePermission) {
//
//                        Toast.makeText(DepositMinistatement.this, "Permission Granted", Toast.LENGTH_LONG).show();
//                    }
//                    else {
//                        Toast.makeText(DepositMinistatement.this,"Permission Denied",Toast.LENGTH_LONG).show();
//
//                    }
//                }
//
//                break;
//        }
//    }

//    private boolean hasPermissions(Context context,String... PERMISSIONS){
//
//        if (context != null && PERMISSIONS != null){
//            for (String permissions: PERMISSIONS){
//                if (ActivityCompat.checkSelfPermission(context,permissions) == PackageManager.PERMISSION_GRANTED){
//                    return false;
//                }
//            }
//
//        }
//        return true;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.R)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 1){
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(DepositMinistatement.this, "Permission Granted0", Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(DepositMinistatement.this,"Permission Denied0",Toast.LENGTH_LONG).show();
//            }
//
//            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(DepositMinistatement.this, "Permission Granted1", Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(DepositMinistatement.this,"Permission Denied1",Toast.LENGTH_LONG).show();
//            }
//            if (Environment.isExternalStorageManager()){
//
//            }else {
//                final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                final Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);
//            }
////            if (grantResults[2] == PackageManager.PERMISSION_GRANTED){
////                Toast.makeText(DepositMinistatement.this, "Permission Granted2", Toast.LENGTH_LONG).show();
////            }
////            else {
////                Toast.makeText(DepositMinistatement.this,"Permission Denied2",Toast.LENGTH_LONG).show();
//////                if (ActivityCompat.shouldShowRequestPermissionRationale(DepositMinistatement.this,
//////                        MANAGE_EXTERNAL_STORAGE)) {
//////                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder=new androidx.appcompat.app.AlertDialog.Builder(DepositMinistatement.this);
//////                    alertDialogBuilder.setMessage("We are asking phone permission only for security purpose.Please allow this permission");
//////                    alertDialogBuilder.setCancelable(false);
//////                    alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
//////                        ActivityCompat.requestPermissions(DepositMinistatement.this,
//////                                new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
//////                                RequestManagePermissionCode);
//////                        dialog.dismiss();
//////
//////                    });
//////                    alertDialogBuilder.show();
//////
//////                }
////
//////                final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//////                final Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
//////                intent.setData(uri);
//////                startActivity(intent);
////            }
//        }
//    }
}