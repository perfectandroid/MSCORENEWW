package com.creativethoughts.iscore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.NeftRtgsActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.model.FundTransferResult1;
import com.creativethoughts.iscore.neftrtgs.BeneficiaryDetailsModel;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BeneficiaryListAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    private ArrayList<BeneficiaryDetailsModel> beneficiaryDetailsModels = new ArrayList<BeneficiaryDetailsModel>();
    Context context;
    String name, accno, ifsc;
    LinearLayout l2;

    public BeneficiaryListAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_list_beneficiary_details, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                name = jsonObject.getString("BeneName");
                accno = jsonObject.getString("BeneIFSC");
                ifsc = jsonObject.getString("BeneAccNo");

                ((MainViewHolder) holder).txt_view_beneficiary_name.setText(name);
                ((MainViewHolder) holder).txt_view_beneficiary_acc_no.setText(accno);
                ((MainViewHolder) holder).txt_view_beneficiary_ifsc.setText(ifsc);


                ((MainViewHolder) holder).mImgDelete.setTag(position);
                ((MainViewHolder) holder).mImgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            jsonObject = jsonArray.getJSONObject(position);
                            String Benefname =jsonObject.getString("BeneName");
                            String BeneIFSC =jsonObject.getString("BeneIFSC");
                            String BeneAccNo =jsonObject.getString("BeneAccNo");

                            alertMessage1("Alert", "Do you want to delete the beneficiary details?",Benefname,BeneIFSC,BeneAccNo);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });



                ((MainViewHolder) holder).mParentLinearLayout.setTag(position);
                ((MainViewHolder) holder).mParentLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            String Benefname =jsonObject.getString("BeneName");
                            String BeneIFSC =jsonObject.getString("BeneIFSC");
                            String BeneAccNo =jsonObject.getString("BeneAccNo");

                            BeneficiaryDetailsModel beneficiaryDetailsModel = new BeneficiaryDetailsModel();
                            beneficiaryDetailsModel.beneficiaryName =Benefname;
                            beneficiaryDetailsModel.beneficiaryIfsc = BeneIFSC;
                            beneficiaryDetailsModel.beneficiaryAccNo=BeneAccNo;

                            beneficiaryDetailsModels.add(beneficiaryDetailsModel);


                            Intent i = new Intent(context, NeftRtgsActivity.class);
                            i.putExtra("benefmodel",beneficiaryDetailsModels);
                            context.startActivity(i);
                        /*    submodule = jsonObject.getString("SubModule");
                            account= jsonObject.getString("FK_Account");
                            status = jsonObject.getString("Status");

                            Intent i = new Intent(context, LoanAccountSummaryDetailsActivity.class);
                            i.putExtra("Submodule", submodule);
                            i.putExtra("FK_Account", account);
                            i.putExtra("Accno", acno);
                            i.putExtra("Amt", amount);
                            i.putExtra("Status", status);
                            i.putExtra("type", type);
                            i.putExtra("Type", "Depo");
                            i.putExtra("Fund", fund);
                            i.putExtra("Ifsc", ifsc);
                            i.putExtra("loantypemode", loantypemode);
                            i.putExtra("IsShareAc", IsShareAc);
                            i.putExtra("EnableDownloadStatement", EnableDownloadStatement);
                            context.startActivity(i);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertMessage1(String alert1, String s1, String benefname, String ifsc, String accno) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       // LayoutInflater inflater = context.getApplicationContext().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_delet_layout, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
        TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);

        tv_msg.setText(alert1);
        tv_msg2.setText(s1);
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

             deletebenefcry(benefname,ifsc,accno);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void deletebenefcry(String benefname, String ifsc, String accno) {


        SharedPreferences pref =context.getSharedPreferences(Config.SHARED_PREF7, 0);
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
                    //   requestObject1.put("ReqMode",IScoreApplication.encryptStart("24") );
                    requestObject1.put("BeneName", IScoreApplication.encryptStart(benefname));
                    requestObject1.put("BeneIFSC", IScoreApplication.encryptStart(ifsc));
                    requestObject1.put("BeneAccNo", IScoreApplication.encryptStart(accno));

                    SharedPreferences bankkeypref =context.getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);

                    SharedPreferences bankheaderpref =context.getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);

                    requestObject1.put("BankKey", IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader", IScoreApplication.encryptStart(BankHeader));

                    Log.e("requestObject dltbenf ",""+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getNeftdlterecvr(body);
                call.enqueue(new Callback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            Log.e("TAG","Response benefcry   "+response.body());

                            int resultInt = Integer.parseInt(String.valueOf(response));
                            if (resultInt > 0) {

                                if ( benefname.isEmpty() ){

                                }
                                Toast.makeText(context, "Beneficiary deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, " Can't delete the beneficiary ", Toast.LENGTH_SHORT).show();
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
//                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
//                        progressDialog.dismiss();
                    }
                });
            }
            catch (Exception e) {
//                progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            //   alertMessage1("", " Network is currently unavailable. Please try again later.");

            // DialogUtil.showAlert(this,
            //"Network is currently unavailable. Please try again later.");
        }

    }

    private SSLSocketFactory getSSLSocketFactory()   throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException,
            KeyManagementException {

        SharedPreferences sslnamepref =context.getSharedPreferences(Config.SHARED_PREF24, 0);
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
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_view_beneficiary_name,txt_view_beneficiary_acc_no,txt_view_beneficiary_ifsc;
        ImageView mImgDelete;
        CardView mParentLinearLayout;

        public MainViewHolder(View v) {
            super(v);
            //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
            txt_view_beneficiary_name = v.findViewById(R.id.txt_view_beneficiary_name);
            txt_view_beneficiary_acc_no = v.findViewById(R.id.txt_view_beneficiary_acc_no);
            txt_view_beneficiary_ifsc = v.findViewById(R.id.txt_view_beneficiary_ifsc);

            mImgDelete = v.findViewById(R.id.img_delete);
            mParentLinearLayout = v.findViewById(R.id.linear_beneficiary_parent);



        }


    }

}

