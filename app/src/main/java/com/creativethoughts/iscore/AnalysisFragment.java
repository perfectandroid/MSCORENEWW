package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
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
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AnalysisFragment extends Fragment {
    String TAG = "AnalysisFragment";
    ColumnChartView columnChartView;
    ProgressDialog progressDialog;
    String cusid, token;
    ColumnChartData data;
    ImageView ivAsset1,ivAsset2,ivAsset3,ivAsset4,ivAsset5;
    TextView tvAsset1, tvAsset2, tvAsset3,tvAsset4, tvAsset5, txtvDate;
    LinearLayout lay_chart,lay_nodata;
    TextView tv_nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analysys, container, false);

        columnChartView = v.findViewById(R.id.chart);
        ivAsset1 = v.findViewById(R.id.ivAsset1);
        ivAsset2 = v.findViewById(R.id.ivAsset2);
        tvAsset1 = v.findViewById(R.id.tvAsset1);
        tvAsset2 = v.findViewById(R.id.tvAsset2);
        txtvDate = v.findViewById(R.id.txtvDate);
        lay_chart = v.findViewById(R.id.lay_chart);
        lay_nodata = v.findViewById(R.id.lay_nodata);
        tv_nodata = v.findViewById(R.id.tv_nodata);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;
        getcolumnChartData();

        ivAsset1.setBackgroundColor(getResources().getColor(R.color.graph2));
        tvAsset1.setText("Payment");
        ivAsset2.setBackgroundColor(getResources().getColor(R.color.graph1));
        tvAsset2.setText("Receipt");

        return v;
    }


    public void getcolumnChartData() {
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
                    .baseUrl(Common.getBaseUrl())
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
                requestObject1.put("ChartType",IScoreApplication.encryptStart("3") );
                requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getDashboardpaymentrecept(body);
            call.enqueue(new Callback<String>() {
                @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    try {
                        progressDialog.dismiss();
                        Log.e(TAG,"response   48   "+response.body());
                        JSONObject jObject = new JSONObject(response.body());

                        if(jObject.getString("StatusCode").equals("0")){
                            lay_chart.setVisibility(View.VISIBLE);
                            lay_nodata.setVisibility(View.GONE);
                            JSONObject jobj = jObject.getJSONObject("DashBoardDataPaymentAndReceiptDetailsIfo");
                            JSONArray jcolumnDataArray = jobj.getJSONArray("DashBoardDataPaymentDetails");
                            String startDate = jobj.getString("StartDate");
                            String endDate = jobj.getString("EndDate");
                            txtvDate.setText("Data From :"+startDate +" to " +endDate);
                            columnChartView.setColumnChartData(generateColumnChartData(jcolumnDataArray));
                            columnChartView.setZoomType(ZoomType.HORIZONTAL);
                        }
                        else {
                            lay_chart.setVisibility(View.VISIBLE);
                            lay_nodata.setVisibility(View.GONE);
                            try{
                                JSONObject jsonObj1 = jObject.getJSONObject("DashBoardDataPaymentAndReceiptDetailsIfo");
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
                        lay_chart.setVisibility(View.VISIBLE);
                        lay_nodata.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                    lay_chart.setVisibility(View.VISIBLE);
                    lay_nodata.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            lay_chart.setVisibility(View.VISIBLE);
            lay_nodata.setVisibility(View.GONE);
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


    private ColumnChartData generateColumnChartData(JSONArray jvalues) {
        try {
            if(jvalues.length()!=0) {
                List<Column> columns = new ArrayList<Column>();
                List<SubcolumnValue> values;
                values = new ArrayList<SubcolumnValue>();
                for (int i = 0; i < jvalues.length(); i++)
                {
                    JSONObject qstnArray = null;
                    qstnArray = jvalues.getJSONObject(i);

               /*     if(qstnArray.getLong("Amount")<0) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph2)));
                       // values.setLabel("some_label".toCharArray());
                    }else{
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph1)));
                    }
*/
                    if(qstnArray.getString("TransType").equals("P")) {
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph2)));
                        // values.setLabel("some_label".toCharArray());
                    }else{
                        values.add(new SubcolumnValue(qstnArray.getLong("Amount"), getResources().getColor(R.color.graph1)));
                    }

                }
                columns.add(new Column(values));
                data = new ColumnChartData(columns);
                data.setAxisYLeft(new Axis().setName("     ").setHasLines(true).setTextColor(Color.BLACK));
              //  data.setAxisXBottom(new Axis().setName("").setHasLines(true).setTextColor(Color.BLACK));
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

}