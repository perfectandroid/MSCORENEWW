package com.creativethoughts.iscore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.adapters.AssetAdapter;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
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
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.creativethoughts.iscore.Helper.Common;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LiabilityFragment extends Fragment {

    String TAG = "LiabilityFragment";
    ColumnChartView columnChartView;
    ProgressDialog progressDialog;
    String cusid, token;
    ColumnChartData data;
    ImageView ivAsset1,ivAsset2,ivAsset3,ivAsset4,ivAsset5;
    TextView tvAsset1, tvAsset2, tvAsset3,tvAsset4, tvAsset5, txtvDate;
    RecyclerView rv_pie;
    LinearLayout lay_chart,lay_nodata;
    TextView tv_nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_liability, container,
                false);

        columnChartView = v.findViewById(R.id.chart);
        ivAsset1 = v.findViewById(R.id.ivAsset1);
        ivAsset2 = v.findViewById(R.id.ivAsset2);
        ivAsset3 = v.findViewById(R.id.ivAsset3);
        ivAsset4 = v.findViewById(R.id.ivAsset4);
        ivAsset5 = v.findViewById(R.id.ivAsset5);
        tvAsset1 = v.findViewById(R.id.tvAsset1);
        tvAsset2 = v.findViewById(R.id.tvAsset2);
        tvAsset3 = v.findViewById(R.id.tvAsset3);
        tvAsset4 = v.findViewById(R.id.tvAsset4);
        tvAsset5 = v.findViewById(R.id.tvAsset5);
        txtvDate = v.findViewById(R.id.txtvDate);
        rv_pie = v.findViewById(R.id.rv_pie);
        lay_chart = v.findViewById(R.id.lay_chart);
        lay_nodata = v.findViewById(R.id.lay_nodata);
        tv_nodata = v.findViewById(R.id.tv_nodata);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;
        getcolumnChartData();

        return v;
    }

    public void getcolumnChartData() {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if (NetworkUtil.isOnline()) {
            columnChartView.setVisibility(View.VISIBLE);
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
                try {
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("6") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );
                    requestObject1.put("ChartType",IScoreApplication.encryptStart("2") );
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDashboardLaibility(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            Log.e(TAG,"response   148   "+response.body());
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){
                                lay_chart.setVisibility(View.VISIBLE);
                                lay_nodata.setVisibility(View.GONE);
                                JSONObject jobj = jObject.getJSONObject("DashBoardDataLaibilityDetailsIfo");
                                JSONArray jcolumnDataArray = jobj.getJSONArray("DashBoardLabilityDetails");
                                String startDate = jobj.getString("StartDate");
                                String endDate = jobj.getString("EndDate");
                                txtvDate.setText("Data From :"+startDate +" to " +endDate);
                                columnChartView.setColumnChartData(generateColumnChartData(jcolumnDataArray));
                                columnChartView.setZoomType(ZoomType.HORIZONTAL);
                            }
                            else {
                                lay_chart.setVisibility(View.GONE);
                                lay_nodata.setVisibility(View.VISIBLE);
                                try{
                                    JSONObject jsonObj1 = jObject.getJSONObject("DashBoardDataLaibilityDetailsIfo");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    tv_nodata.setText(""+ResponseMessage);

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    tv_nodata.setText(""+EXMessage);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            lay_chart.setVisibility(View.GONE);
                            lay_nodata.setVisibility(View.VISIBLE);

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                        lay_chart.setVisibility(View.GONE);
                        lay_nodata.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                lay_chart.setVisibility(View.GONE);
                lay_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            columnChartView.setVisibility(View.GONE);
            lay_chart.setVisibility(View.GONE);
            lay_nodata.setVisibility(View.VISIBLE);
            tv_nodata.setText("Network is currently unavailable. Please try again later.");
//            DialogUtil.showAlert(getContext(),
//                    "Network is currently unavailable. Please try again later.");
        }


    }

    @SuppressLint("NewApi")
    private ColumnChartData generateColumnChartData(JSONArray jvalues) {
        try {
            if(jvalues.length()!=0) {
               // int a[]=new int[jvalues.length()];

                int[] a = {
                        getResources().getColor(R.color.graph1),
                        getResources().getColor(R.color.graph2),
                        getResources().getColor(R.color.graph3),
                        getResources().getColor(R.color.graph4),
                        getResources().getColor(R.color.graph5),
                        getResources().getColor(R.color.graph1),
                        getResources().getColor(R.color.graph2),
                        getResources().getColor(R.color.graph3),
                        getResources().getColor(R.color.graph4),
                        getResources().getColor(R.color.graph5),
                        getResources().getColor(R.color.graph1),
                        getResources().getColor(R.color.graph2),
                        getResources().getColor(R.color.graph3),
                        getResources().getColor(R.color.graph4),
                        getResources().getColor(R.color.graph5),
                        getResources().getColor(R.color.graph1),
                        getResources().getColor(R.color.graph2),
                        getResources().getColor(R.color.graph3),
                        getResources().getColor(R.color.graph4),
                        getResources().getColor(R.color.graph5)
                };

                List<Column> columns = new ArrayList<Column>();
                List<SubcolumnValue> values;


                values = new ArrayList<SubcolumnValue>();
                for (int i = 0; i < jvalues.length(); i++)
                {
                    JSONObject qstnArray = null;

                    qstnArray = jvalues.getJSONObject(i);


                   // int intColor= ChartUtils.pickColor();
                   // a[i]=intColor;//initialization

                   // values.add(new SubcolumnValue(qstnArray.getLong("Balance"), a[i]));
                    values.add(new SubcolumnValue(qstnArray.getLong("Balance"), a[i]));
                    GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 1);
                    rv_pie.setLayoutManager(lLayout);
                    rv_pie.setHasFixedSize(true);
                    AssetAdapter adapter = new AssetAdapter(getActivity(), jvalues,a);
                    rv_pie.setAdapter(adapter);



                 /*   if(i==0) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph1)));
                        ivAsset1.setBackgroundColor(getResources().getColor(R.color.graph1));
                        tvAsset1.setText(qstnArray.getString("Account"));
                    }
                    if(i==1) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph2)));
                        ivAsset2.setBackgroundColor(getResources().getColor(R.color.graph2));
                        tvAsset2.setText(qstnArray.getString("Account"));
                    }
                    if(i==2) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph3)));
                        ivAsset3.setBackgroundColor(getResources().getColor(R.color.graph3));
                        tvAsset3.setText(qstnArray.getString("Account"));
                    }
                    if(i==3) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph4)));
                        ivAsset4.setBackgroundColor(getResources().getColor(R.color.graph4));
                        tvAsset4.setText(qstnArray.getString("Account"));
                    }*/
                }
                columns.add(new Column(values));
                data = new ColumnChartData(columns);
                data.setAxisYLeft(new Axis().setName("      ").setHasLines(true).setTextColor(Color.BLACK));


            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> true;
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
;

}