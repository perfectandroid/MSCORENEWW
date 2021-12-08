package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.LoanMiniadapter;
import com.creativethoughts.iscore.db.dao.PBAccountInfoDAO;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.model.AccountInfo;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.utility.DialogUtil;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

public class LoanMinistatement extends AppCompatActivity implements View.OnClickListener {
    TextView tv_accno,tv_bal,tvtxn,tvGraph;
    CardView cv_transaction,cv_graph;
    LinearLayout llgraph,lltxn,llstatement;
    private String acChange,amt,account,submodule,token,EnableDownloadStatement;
    RecyclerView rv_ministatmnt;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ColumnChartView columnChartView;
    ColumnChartData data;
    String accNewChange,branchcode;
    int indexofmonth;
    LinearLayout ll_download,ll_view;
    EditText etxtFrom, etxtTo;
    private DownloadManager mDownloadManager;
    JSONArray Jarray = new JSONArray();
    private ProgressDialog progressDialog;
    Spinner im_month;
    String[] month = { "January", "February", "March", "April", "May","June","July","August",
            "September","October","November","December"};
    RadioButton rb1,rb2;
    ArrayAdapter aa;
    AccountInfo accountInfo;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_ministatement);
        acChange = getIntent().getStringExtra("accno");

        mDownloadManager = new DownloadManager();

       /* accNewChange = acChange;
        accNewChange = accNewChange.replace(accNewChange.substring(accNewChange.indexOf(" (")+1, accNewChange.indexOf(")")+1), "");
        accNewChange = accNewChange.replace(" ","");*/

        amt = getIntent().getStringExtra("amt");
        account = getIntent().getStringExtra("account");
        submodule = getIntent().getStringExtra("submodule");
        EnableDownloadStatement = getIntent().getStringExtra("EnableDownloadStatement");


        accountInfo = PBAccountInfoDAO.getInstance().getAccountInfo(accNewChange);
        branchcode = accountInfo.accountBranchCode;



        setRegViews();
        tv_accno.setText(acChange);
        tv_bal.setText(amt);
        if(EnableDownloadStatement.equals("1")){
            llstatement.setVisibility(View.VISIBLE);
        }else{
            llstatement.setVisibility(View.GONE);
        }
        showMinistatmnt();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void setRegViews() {

        rv_ministatmnt  = (RecyclerView) findViewById(R.id.rv_ministatmnt);
        columnChartView = (ColumnChartView) findViewById(R.id.chart);
        tv_accno        = (TextView) findViewById(R.id.tv_accno);
        tv_bal          = (TextView) findViewById(R.id.tv_bal);
        tvGraph         = findViewById(R.id.tvGraph);
        tvtxn           = findViewById(R.id.tvtxn);
        cv_transaction  = findViewById(R.id.cv_transaction);
        cv_graph        = findViewById(R.id.cv_graph);
        llgraph         = findViewById(R.id.llgraph);
        lltxn           = findViewById(R.id.lltxn);
        llstatement           = findViewById(R.id.llstatement);
        tvGraph.setOnClickListener(this);
        tvtxn.setOnClickListener(this);

        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        ll_download.setOnClickListener(this);
        ll_view = (LinearLayout) findViewById(R.id.ll_view);
        ll_view.setOnClickListener(this);

    }

    private void showMinistatmnt() {
        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(LoanMinistatement.this, R.style.Progress);
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
                String reqmode = IScoreApplication.encryptStart("16");

                UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
                token = loginCredential.token;

                final JSONObject requestObject1 = new JSONObject();
                try {

                    requestObject1.put("ReqMode",reqmode);
                    requestObject1.put("Token", IScoreApplication.encryptStart(token));
                    requestObject1.put("FK_Account", IScoreApplication.encryptStart(account));
                    requestObject1.put("SubModule", IScoreApplication.encryptStart(submodule));
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
                Call<String> call = apiService.getLoanMinistatement(body);
                call.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        try{
                            Log.i("LoanMinistatement",response.body());
                            JSONObject jsonObj = new JSONObject(response.body());
                            if(jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("LoanMiniStatement");
                                Log.i("First ",String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ",String.valueOf(object));
                                Jarray  = object.getJSONArray("LoanMiniStatementList");
                                int length = object.length()+1;
                                columnChartView.setColumnChartData(generateColumnChartData(Jarray));
                                columnChartView.setZoomType(ZoomType.VERTICAL);

                                if(Jarray.length()!=0) {
                                    cv_transaction.setVisibility(View.VISIBLE);
                                    GridLayoutManager lLayout = new GridLayoutManager(LoanMinistatement.this, 1);
                                    rv_ministatmnt.setLayoutManager(lLayout);
                                    rv_ministatmnt.setHasFixedSize(true);
                                    LoanMiniadapter adapter = new LoanMiniadapter(LoanMinistatement.this, Jarray);
                                    rv_ministatmnt.setAdapter(adapter);
                                }
                                else {
                                    cv_graph.setVisibility(View.GONE);
                                    cv_transaction.setVisibility(View.GONE);
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
                                    builder.setMessage(ResponseMessage)
//                                builder.setMessage("No data found.")
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
                            }
                            else{
                                cv_graph.setVisibility(View.GONE);
                                cv_transaction.setVisibility(View.GONE);
                                try{
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("BranchLocationDetailsListInfo");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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
                            //Toast.makeText(getActivity(),response.body(),Toast.LENGTH_LONG).show();
                        }catch (JSONException e)
                        {

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
          /*  DialogUtil.showAlert(LoanMinistatement.this,
                    "Network is currently unavailable. Please try again later.");*/
        }

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

    private ColumnChartData generateColumnChartData(JSONArray jsonArray) {
        try {
            if(jsonArray.length()!=0) {
                List<Column> columns = new ArrayList<Column>();
                List<SubcolumnValue> values;
                values = new ArrayList<SubcolumnValue>();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject qstnArray = null;
                    qstnArray = jsonArray.getJSONObject(i);

               /*     if(qstnArray.getLong("Amount")<0) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph2)));
                       // values.setLabel("some_label".toCharArray());
                    }else{
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph1)));
                    }
*/
                    if(qstnArray.getString("TransType").equals("D")) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.FireBrick)));
                        // values.setLabel("some_label".toCharArray());
                    }else {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.Green)));
                    }
                    //  values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph2)));

                }
                columns.add(new Column(values));
                data = new ColumnChartData(columns);
                data.setAxisYLeft(new Axis().setName("     ").setHasLines(true).setTextColor(Color.BLACK));
                //  data.setAxisXBottom(new Axis().setName("").setHasLines(true).setTextColor(Color.BLACK));
            }
            else {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download:
                popupDownloaddoc();
                // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
                break;

            case R.id.ll_view:
                popupDownloaddoc1();
                //    Intent i = new Intent(DepositMinistatement.this, Viewstatement.class);
                //    startActivity(i);
                break;


            case R.id.tvtxn:
                lltxn.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.under_line));
                llgraph.setBackgroundDrawable(null);
                tvtxn.setTextColor(getResources().getColor(R.color.RoyalBlue));
                tvGraph.setTextColor(getResources().getColor(R.color.black));

                try{
                    if (!Jarray.toString().equals("null")){
                        if(Jarray.length()!=0) {
                            cv_transaction.setVisibility(View.VISIBLE);
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
                cv_graph.setVisibility(View.GONE);
                break;
            case  R.id.tvGraph:
                llgraph.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.under_line));
                lltxn.setBackgroundDrawable(null);
                tvtxn.setTextColor(getResources().getColor(R.color.black));
                tvGraph.setTextColor(getResources().getColor(R.color.RoyalBlue));
                try{
                    if (!Jarray.toString().equals("null")){
                        if(Jarray.length()!=0) {
                            cv_graph.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No Data To Display.",Toast.LENGTH_LONG).show();
                            cv_graph.setVisibility(View.GONE);
                        }
                    }

                }
                catch (Exception e) {
                    cv_graph.setVisibility(View.GONE);
                }

                cv_transaction.setVisibility(View.GONE);
                break;
        }
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

          /*  Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -14);
            Date newDate = calendar.getTime();
*/
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

            /*Calendar someDate = GregorianCalendar.getInstance();
            someDate.add(Calendar.DAY_OF_YEAR, -14);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(someDate);

*/
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

                    // selecteditem =  adapter.getItemAtPosition(i)./();
                    //or this can be also right: selecteditem = level[i];
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView)
                {

                }
            });

            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();

         /*   im_month.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
                    *//*
                      final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);


                        DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();*//*
                    }

                    return true;
                }
            });
*/
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

                            DatePickerDialog datePickerDialog = new DatePickerDialog(LoanMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                            //   currentmonth = c.get(Calendar.MONTH);
                            //  c.set(Calendar.DAY_OF_MONTH, 1);

                            //  Calendar c1 = Calendar.getInstance();
                            //   c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

                            //   int indexofmonth = Calendar.getInstance().get(Calendar.MONTH);
//                        if (mMonth == currentmonth) {
//                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
//                                    new DatePickerDialog.OnDateSetListener() {
//
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year,
//                                                              int monthOfYear, int dayOfMonth) {
//
//
//                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                                        }
//                                    }, mYear, mMonth, mDay);
//
//                          //  Calendar c0 = Calendar.getInstance();
//                          /*  c0.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//                            c0.clear(Calendar.MINUTE);
//                            c0.clear(Calendar.SECOND);
//                            c0.clear(Calendar.MILLISECOND);*/
//                         //   c0.set(Calendar.DAY_OF_MONTH, 1);
//
//                         /*   Calendar c1 = Calendar.getInstance();
//                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));*/
//
//                            c.set(Calendar.DAY_OF_MONTH, 1);
//                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
//
//
//
//
//
//                            datePickerDialog.show();
//                        }
//
//                       else {
//                            mMonth = im_month.getSelectedItemPosition();
//                            int m = mMonth;
//
//                            c.set(m, 1);
//                          //  Calendar c2= Calendar.getInstance();
//                            c.set(m, c.getActualMaximum(m));
//                            //  mMonth = im_month.getSelectedItemPosition();
//                            // Toast.makeText(DepositMinistatement.this,("Not equal"),Toast.LENGTH_LONG).show();
//                            DatePickerDialog datePickerDialog1 = new DatePickerDialog(DepositMinistatement.this,
//                                    new DatePickerDialog.OnDateSetListener() {
//
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year,
//                                                              int monthOfYear, int dayOfMonth) {
//
//
//                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                                        }
//
//
//                                    },
//
//                                    mYear, m, 1);
//
//                            /*datePickerDialog1.getDatePicker().setMinDate(c.getTimeInMillis());
//                            datePickerDialog1.getDatePicker().setMaxDate(c.getTimeInMillis());*/
//                            datePickerDialog1.show();
//                        }
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

                            DatePickerDialog datePickerDialog = new DatePickerDialog(LoanMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();


                      /*  final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                     //   mMonth = c.get(Calendar.MONTH);
                        mMonth = im_month.getSelectedItemPosition();
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        int currentmonth = c.get(Calendar.MONTH);

                        if (mMonth != currentmonth) {
                            // Toast.makeText(DepositMinistatement.this,("Not equal"),Toast.LENGTH_LONG).show();
                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, 1);

                            c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
                            c.clear(Calendar.MINUTE);
                            c.clear(Calendar.SECOND);
                            c.clear(Calendar.MILLISECOND);
                            c.set(Calendar.DAY_OF_MONTH, 1);

                            Calendar c1 = Calendar.getInstance();
                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));


                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
                            datePickerDialog.show();
                        }
                        else
                        {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {

                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
                            c.clear(Calendar.MINUTE);
                            c.clear(Calendar.SECOND);
                            c.clear(Calendar.MILLISECOND);
                            c.set(Calendar.DAY_OF_MONTH, 1);

                            Calendar c1 = Calendar.getInstance();
                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));


                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
                            datePickerDialog.show();
                        }
*/
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

          /*  Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -14);
            Date newDate = calendar.getTime();
*/
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

                    // selecteditem =  adapter.getItemAtPosition(i)./();
                    //or this can be also right: selecteditem = level[i];
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

                            DatePickerDialog datePickerDialog = new DatePickerDialog(LoanMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                            //   currentmonth = c.get(Calendar.MONTH);
                            //  c.set(Calendar.DAY_OF_MONTH, 1);

                            //  Calendar c1 = Calendar.getInstance();
                            //   c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

                            //   int indexofmonth = Calendar.getInstance().get(Calendar.MONTH);
//                        if (mMonth == currentmonth) {
//                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
//                                    new DatePickerDialog.OnDateSetListener() {
//
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year,
//                                                              int monthOfYear, int dayOfMonth) {
//
//
//                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                                        }
//                                    }, mYear, mMonth, mDay);
//
//                          //  Calendar c0 = Calendar.getInstance();
//                          /*  c0.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//                            c0.clear(Calendar.MINUTE);
//                            c0.clear(Calendar.SECOND);
//                            c0.clear(Calendar.MILLISECOND);*/
//                         //   c0.set(Calendar.DAY_OF_MONTH, 1);
//
//                         /*   Calendar c1 = Calendar.getInstance();
//                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));*/
//
//                            c.set(Calendar.DAY_OF_MONTH, 1);
//                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
//
//
//
//
//
//                            datePickerDialog.show();
//                        }
//
//                       else {
//                            mMonth = im_month.getSelectedItemPosition();
//                            int m = mMonth;
//
//                            c.set(m, 1);
//                          //  Calendar c2= Calendar.getInstance();
//                            c.set(m, c.getActualMaximum(m));
//                            //  mMonth = im_month.getSelectedItemPosition();
//                            // Toast.makeText(DepositMinistatement.this,("Not equal"),Toast.LENGTH_LONG).show();
//                            DatePickerDialog datePickerDialog1 = new DatePickerDialog(DepositMinistatement.this,
//                                    new DatePickerDialog.OnDateSetListener() {
//
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year,
//                                                              int monthOfYear, int dayOfMonth) {
//
//
//                                            etxtFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//
//                                        }
//
//
//                                    },
//
//                                    mYear, m, 1);
//
//                            /*datePickerDialog1.getDatePicker().setMinDate(c.getTimeInMillis());
//                            datePickerDialog1.getDatePicker().setMaxDate(c.getTimeInMillis());*/
//                            datePickerDialog1.show();
//                        }
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

                            DatePickerDialog datePickerDialog = new DatePickerDialog(LoanMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();


                      /*  final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                     //   mMonth = c.get(Calendar.MONTH);
                        mMonth = im_month.getSelectedItemPosition();
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        int currentmonth = c.get(Calendar.MONTH);

                        if (mMonth != currentmonth) {
                            // Toast.makeText(DepositMinistatement.this,("Not equal"),Toast.LENGTH_LONG).show();
                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {


                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, 1);

                            c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
                            c.clear(Calendar.MINUTE);
                            c.clear(Calendar.SECOND);
                            c.clear(Calendar.MILLISECOND);
                            c.set(Calendar.DAY_OF_MONTH, 1);

                            Calendar c1 = Calendar.getInstance();
                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));


                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
                            datePickerDialog.show();
                        }
                        else
                        {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(DepositMinistatement.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {

                                            etxtTo.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                        }
                                    }, mYear, mMonth, mDay);
                            c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
                            c.clear(Calendar.MINUTE);
                            c.clear(Calendar.SECOND);
                            c.clear(Calendar.MILLISECOND);
                            c.set(Calendar.DAY_OF_MONTH, 1);

                            Calendar c1 = Calendar.getInstance();
                            c1.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));


                            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                            datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
                            datePickerDialog.show();
                        }
*/
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
                    String value = BASE_URL+ "/Mscore/Statement/ASD7.pdf";
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
            progressDialog = new ProgressDialog(LoanMinistatement.this, R.style.Progress);
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
                            //  StringTokenizer tokens1 = new StringTokenizer(s2, "=");
                            StringTokenizer tokens1 = new StringTokenizer(s1, "=");
                            //  Log.i("Documentdownload", response.message());
                            String s3 = tokens1.nextToken();
                            String s4 = tokens1.nextToken();
                            // if(!s4.contains("Error"))
                            if(s4.contains("200"))
                            {
                                //      Log.i("viewdocument", response.body());
                                JSONObject jsonObj = new JSONObject(response.body());


                                if (jsonObj.getString("StatusCode").equals("0")) {
                                    JSONObject jsonObj1 = jsonObj.getJSONObject("StatementOfAccountDet");
                                    Log.i("First ", String.valueOf(jsonObj1));
                                    JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                    Log.i("First1 ", String.valueOf(object));
                                    String filename = object.getString("FilePath");
                                    String filename1 = object.getString("FileName");

                                    int index = filename.indexOf("Mscore");
                                    String strNew = filename.substring(index);


                                   /* StringTokenizer tokens = new StringTokenizer(filename, "\\");
                                    String first = tokens.nextToken();// this will contain "Fruit"
                                    String second = tokens.nextToken();
                                    String third = tokens.nextToken();
                                    String four = tokens.nextToken();
                                    String five = tokens.nextToken();*/
                                    // String six = tokens.nextToken();

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                    String BASE_URL=pref.getString("baseurl", null);
                                    String filename2 = BASE_URL +"\\"+strNew+"\\"+filename1;

                                  //  String filename2 = Common.getBaseUrl() + "/" + four + "/" + five + "/" + filename1;
                                    Log.i("Path", filename2 + "\n" + filename1);

                                    Intent i = new Intent(LoanMinistatement.this, Viewstatement.class);
                                    i.putExtra("docx", filename2);
                                    startActivity(i);

                                } else {

                                    try {
                                        JSONObject jobj = jsonObj.getJSONObject("StatementOfAccountDet");
                                        String ResponseMessage = jobj.getString("ResponseMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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

                                    } catch (JSONException e) {
                                        String EXMessage = jsonObj.getString("EXMessage");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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
          /*  DialogUtil.showAlert(LoanMinistatement.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    private void getAccountStatement(String from, String to, String acc) {

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);

        if (NetworkUtil.isOnline()) {
            progressDialog = new ProgressDialog(LoanMinistatement.this, R.style.Progress);
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
                            //  StringTokenizer tokens1 = new StringTokenizer(s2, "=");
                            StringTokenizer tokens1 = new StringTokenizer(s1, "=");
                            //  Log.i("Documentdownload", response.message());
                            String s3 = tokens1.nextToken();
                            String s4 = tokens1.nextToken();
                            //  if(!s4.contains("Error"))
                            if(s4.contains("200"))
                            {
//                            Log.i("Documentdownload", response.body());
                            JSONObject jsonObj = new JSONObject(response.body());


                            if (jsonObj.getString("StatusCode").equals("0")) {
                                JSONObject jsonObj1 = jsonObj.getJSONObject("StatementOfAccountDet");
                                Log.i("First ",String.valueOf(jsonObj1));
                                JSONObject object = new JSONObject(String.valueOf(jsonObj1));
                                Log.i("First1 ",String.valueOf(object));
                                String filename =object.getString("FilePath");
                                String filename1 =object.getString("FileName");

                                int index = filename.indexOf("Mscore");
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoanMinistatement.this);
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
           /* DialogUtil.showAlert(LoanMinistatement.this,
                    "Network is currently unavailable. Please try again later.");*/
        }


    }
    //  private void downloadFile(String value, String filename1) {
    private void downloadFile(String filename2, String filename1) {


        DownloadRequest request = new DownloadRequest().setDownloadId(39)
//                .setSimpleDownloadListener(new SimpleListener())
                .setRetryTime(2).setDestDirectory(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .getAbsolutePath() ).setFileName(filename1)
                .setAllowedNetworkTypes(this, DownloadRequest.NETWORK_WIFI)
                .setDownloadListener(new Listener())
                .setProgressInterval(1000).setUrl(filename2);
        mDownloadManager.add(request);

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
         //   Toast.makeText(LoanMinistatement.this, filePath, Toast.LENGTH_SHORT).show();


                alertMessage1("Statement Downloaded Successfully" + "\n" + filePath, "");


   /*         if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
                try{
                    final Uri data = FileProvider.getUriForFile(
                            LoanMinistatement.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( filePath )
                    );
                    LoanMinistatement.this.grantUriPermission( LoanMinistatement.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                    DialogUtil.showAlert(LoanMinistatement.this,
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
           /* DialogUtil.showAlert(LoanMinistatement.this,
                    "Not able to download PDF file, Please try again later");*/

//            mTextStatus.setText("onFailure : " +  statusCode + " errMsg " + errMsg);
//            Log.d(TAG, "fail: " + downloadId + " " + statusCode + " " + errMsg);
        }
    }
    private void alertMessage1(String msg1, String msg2) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoanMinistatement.this);

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

         // tv_share.setText(msg2);
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
                                LoanMinistatement.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File( msg2 )
                        );
                        LoanMinistatement.this.grantUriPermission( LoanMinistatement.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                        /*DialogUtil.showAlert(LoanMinistatement.this,
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