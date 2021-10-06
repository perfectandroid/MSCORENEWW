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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.Helper.Common;
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

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AssetsFragment extends Fragment {

    String TAG = "AssetsFragment";
    PieChartView pieChartView;
    ProgressDialog progressDialog;
    String cusid, token;
    ImageView ivAsset1,ivAsset2,ivAsset3,ivAsset4,ivAsset5;
    TextView tvAsset1, tvAsset2, tvAsset3,tvAsset4, tvAsset5, txtvDate;
    RecyclerView rv_pie;
    LinearLayout lay_chart,lay_nodata;
    TextView tv_nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_assets, container,
                false);
        pieChartView = v.findViewById(R.id.chart);
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
        getAssetData();

        return v;
    }
    public void getAssetData(){
        if (NetworkUtil.isOnline()) {
            pieChartView.setVisibility(View.VISIBLE);
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
                    requestObject1.put("ChartType",IScoreApplication.encryptStart("1") );
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(getResources().getString(R.string.BankKey)));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(getResources().getString(R.string.BankHeader)));

                    Log.e(TAG,"requestObject1   139   "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDashboard(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG,"response   149   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());


                            if(jObject.getString("StatusCode").equals("0")){
                                lay_chart.setVisibility(View.VISIBLE);
                                lay_nodata.setVisibility(View.GONE);
                                JSONObject jobj = jObject.getJSONObject("DashBoardDataAssetsDetailsIfo");

                                JSONArray jarray = jobj.getJSONArray("DashBoardAssestDetails");
                                String startDate = jobj.getString("StartDate");
                                String endDate = jobj.getString("EndDate");
                                txtvDate.setText("Data From :"+startDate +" to " +endDate);
                                Log.e(TAG,"response   158   "+jarray.length());
                                if(jarray.length()!=0) {
                                    //int a[]=new int[jarray.length()];

                                    try {
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


                                        List pieData = new ArrayList<>();
                                        for (int i = 0; i < jarray.length(); i++)
                                        {
                                            JSONObject qstnArray = jarray.getJSONObject(i);
                                            int intColor=ChartUtils.pickColor();
                                            // a[i]=intColor;//initialization

                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"), a[i]).setLabel(qstnArray.getString("Balance")));
                                            GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 1);
                                            rv_pie.setLayoutManager(lLayout);
                                            rv_pie.setHasFixedSize(true);
                                            AssetAdapter adapter = new AssetAdapter(getActivity(), jarray,a);
                                            rv_pie.setAdapter(adapter);
                                            //pieData.add(new SliceValue(qstnArray.getLong("Balance"), ChartUtils.pickColor()).setLabel(qstnArray.getString("Account")));
                                     /*   if(i==0) {
                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"),getResources().getColor(R.color.graph1)).setLabel(qstnArray.getString("Balance")));
                                            ivAsset1.setBackgroundColor(getResources().getColor(R.color.graph1));
                                            tvAsset1.setText(qstnArray.getString("Account"));
                                        }
                                        if(i==1) {
                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph2)).setLabel(qstnArray.getString("Balance")));
                                            ivAsset2.setBackgroundColor(getResources().getColor(R.color.graph2));
                                            tvAsset2.setText(qstnArray.getString("Account"));
                                        }
                                        if(i==2) {
                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph3)).setLabel(qstnArray.getString("Balance")));
                                            ivAsset3.setBackgroundColor(getResources().getColor(R.color.graph3));
                                            tvAsset3.setText(qstnArray.getString("Account"));
                                        }
                                        if(i==3) {
                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"), getResources().getColor(R.color.graph4)).setLabel(qstnArray.getString("Balance")));
                                            ivAsset4.setBackgroundColor(getResources().getColor(R.color.graph4));
                                            tvAsset4.setText(qstnArray.getString("Account"));
                                        }
                                        if(i==4) {
                                            pieData.add(new SliceValue(qstnArray.getLong("Balance"),getResources().getColor(R.color.graph5)).setLabel(qstnArray.getString("Balance")));
                                            ivAsset5.setBackgroundColor(getResources().getColor(R.color.graph5));
                                            tvAsset5.setText(qstnArray.getString("Account"));
                                        }*/
                                        }
                                        PieChartData pieChartData = new PieChartData(pieData);
                                        pieChartData.setHasLabels(true).setValueLabelTextSize(12);
                                        pieChartData.setHasCenterCircle(true).setCenterText1("Assets").setCenterText1FontSize(22).setCenterText1Color(Color.parseColor("#0097A7"));
                                        pieChartView.setPieChartData(pieChartData);
                                    }

                                    catch (Exception e){
                                        Log.e(TAG,"Exception   149   "+e.toString());
                                    }

                                }
                                else {
                                    lay_chart.setVisibility(View.GONE);
                                    lay_nodata.setVisibility(View.VISIBLE);
                                    tv_nodata.setText("No data found in Assets.");
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                    builder.setMessage("No data found in Assets.")
//                                            .setCancelable(false)
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int id) {
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();
                                }
                            }
                            else {
                                try{
                                    JSONObject jobj = jObject.getJSONObject("DashBoardDataAssetsDetailsIfo");
                                    String ResponseMessage = jobj.getString("ResponseMessage");
                                    lay_chart.setVisibility(View.GONE);
                                    lay_nodata.setVisibility(View.VISIBLE);
                                    tv_nodata.setText(""+ResponseMessage);

                                }catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    lay_chart.setVisibility(View.GONE);
                                    lay_nodata.setVisibility(View.VISIBLE);
                                    tv_nodata.setText(""+EXMessage);

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            lay_chart.setVisibility(View.GONE);
                            lay_nodata.setVisibility(View.VISIBLE);
                            tv_nodata.setText("No data found");
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) { progressDialog.dismiss();
                        lay_chart.setVisibility(View.GONE);
                        lay_nodata.setVisibility(View.VISIBLE);
                        tv_nodata.setText("No data found in Assets.");}
                });
            } catch (Exception e) {
                e.printStackTrace();
                lay_chart.setVisibility(View.GONE);
                lay_nodata.setVisibility(View.VISIBLE);
                tv_nodata.setText("No data found in Assets.");
            }
        } else {
            pieChartView.setVisibility(View.GONE);
            lay_chart.setVisibility(View.GONE);
            lay_nodata.setVisibility(View.VISIBLE);
            tv_nodata.setText("Network is currently unavailable. Please try again later.");
//            DialogUtil.showAlert(getContext(),
//
//                    "Network is currently unavailable. Please try again later.");
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

}