package com.creativethoughts.iscore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Retrofit.APIInterface;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class BarcodeTest extends Fragment {
    private RadioGroup radioGroup;
    Bitmap bitmap,bitmap1,bitmap2 ;
    Thread thread ;
    private ImageView imgv_qrcode,imgv_barcode,imgv_customerimg;
    public final static int QRcodeWidth = 500 ;
    public String  customerid,customeridwithcode;
    UserDetails userDetails;
    String customeridwithcode1;
    ProgressDialog progressDialog;
    String useraddress,cusid, token,  vritualcardCombination;


    public BarcodeTest() throws WriterException {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode_test, container, false);

        userDetails = UserDetailsDAO.getInstance().getUserDetail();
        cusid = userDetails.customerId;
        UserCredential loginCredential = UserCredentialDAO.getInstance( ).getLoginCredential( );
        token = loginCredential.token;


        getVirtualCard();
        //customeridwithcode = vritualcardCombination;
        String phone =userDetails.userMobileNo;
        Log.i("Userdetails",userDetails.userCustomerName+"\n"+ userDetails.userCustomerAddress1 + ", " + userDetails.userCustomerAddress2);

        TextView txt_customerid = (TextView) view.findViewById(R.id.txt_customerid);
        TextView txtv_address = (TextView) view.findViewById(R.id.txtv_addrs);
        TextView txtv_phone = (TextView) view.findViewById(R.id.txtv_phone);

        txt_customerid.setText(customerid);
        txt_customerid.setAllCaps(false);
        txtv_address.setText(useraddress);
        txtv_phone.setText(phone);

        imgv_barcode = (ImageView) view.findViewById(R.id.imgv_barcode);
        imgv_qrcode = (ImageView) view.findViewById(R.id.imgv_qrcode);


        imgv_barcode.setOnClickListener(v -> barpopup());
        imgv_qrcode.setOnClickListener(v -> qrpopup());



        return view;
    }

    private void generatebarcode() {
        try {

          /*  Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType,ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();

            BitMatrix byteMatrix = codeWriter.encode(vritualcardCombination, BarcodeFormat.CODE_128,400,200,hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            for(int i=0; i<width ;i++)
            {
                for(int j=0; j<height ;j++)
                {
                    bitmap.setPixel(i,j,byteMatrix.get(i,j)? Color.BLACK : Color.WHITE);
                }
            }
            imgv_barcode.setImageBitmap(bitmap);*/

          MultiFormatWriter  multiFormatWriter = new MultiFormatWriter();
          BitMatrix bitMatrix = multiFormatWriter.encode(vritualcardCombination,BarcodeFormat.CODE_128,400,170);
          BarcodeEncoder  barcodeEncoder = new BarcodeEncoder();
          Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
         imgv_barcode.setImageBitmap(bitmap);

        } catch (Exception e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private void generateqrcode() {

        try {
            bitmap = TextToImageEncode(vritualcardCombination);

            imgv_qrcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    private void barpopup() {

        progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(this.getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(5900);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            final View popupView = factory.inflate(R.layout.layout_barpopup, null);

                            final AlertDialog popup = new AlertDialog.Builder(getActivity()).create();

                            popup.setView(popupView);



                            ImageView imgv_popupcode =  popupView.findViewById(R.id.imgv_popupcard);

                            try {

                                Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType,ErrorCorrectionLevel>();
                                hintMap.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
                                Writer codeWriter;
                                codeWriter = new Code128Writer();

                                BitMatrix byteMatrix = codeWriter.encode(vritualcardCombination, BarcodeFormat.CODE_128,400,200,hintMap);
                                int width = byteMatrix.getWidth();
                                int height = byteMatrix.getHeight();
                                bitmap2 = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
                                for(int i=0; i<width ;i++)
                                {
                                    for(int j=0; j<height ;j++)
                                    {
                                        bitmap2.setPixel(i,j,byteMatrix.get(i,j)? Color.BLACK : Color.WHITE);
                                    }
                                }
                                imgv_popupcode.setImageBitmap(bitmap2);

                            } catch (Exception e) {
                                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }


                            TextView tv_popuptitle = popupView.findViewById(R.id.tv_popuptitle);
                            ImageButton imgbtn_close = popupView.findViewById(R.id.btnCancel);
                            imgbtn_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                }
                            });

                            tv_popuptitle.setText(R.string.barcode);

                            popup.show();

                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }

            }
        }.start();


    }
    private void qrpopup() {

        progressDialog = new ProgressDialog(getActivity(), R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(this.getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(5900);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            final View popupView = factory.inflate(R.layout.layout_popup, null);
                            final AlertDialog popup = new AlertDialog.Builder(getActivity()).create();
                            popup.setView(popupView);
                            ImageView imgv_popupcode =  popupView.findViewById(R.id.imgv_popupcard);
                            TextView tv_popuptitle = popupView.findViewById(R.id.tv_popuptitle);
                            ImageButton imgbtn_close = popupView.findViewById(R.id.btnCancel);
                            imgbtn_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                }
                            });
                            try {
                                bitmap1 = TextToImageEncode(vritualcardCombination);

                                imgv_popupcode.setImageBitmap(bitmap1);

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            tv_popuptitle.setText(R.string.qrcode);
                            popup.show();

                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }

            }
        }.start();




    }
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        bitmap1 = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap1.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap1;
    }

    private void getVirtualCard() {
        SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
        if(NetworkUtil.isOnline()){

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
                    requestObject1.put("ReqMode",IScoreApplication.encryptStart("9") );
                    requestObject1.put("FK_Customer",IScoreApplication.encryptStart(cusid) );
                    requestObject1.put("Token",IScoreApplication.encryptStart(token) );
                    SharedPreferences bankkeypref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
                    String BankKey=bankkeypref.getString("bankkey", null);
                    SharedPreferences bankheaderpref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
                    String BankHeader=bankheaderpref.getString("bankheader", null);
                    requestObject1.put("BankKey",IScoreApplication.encryptStart(BankKey));
                    requestObject1.put("BankHeader",IScoreApplication.encryptStart(BankHeader));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String >call = apiService.getBardCodeData(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {

                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")) {
                                JSONObject jobj = jObject.getJSONObject("BarcodeFormatDet");
                               vritualcardCombination = jobj.getString("BarcodeFormat");
                                generatebarcode();
                                generateqrcode();
                            }
                            else{
                                try{
                                    JSONObject jsonObj1 = jObject.getJSONObject("BarcodeFormatDet");
                                    String ResponseMessage = jsonObj1.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                                } catch (JSONException e){
                                    String EXMessage = jObject.getString("EXMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });


            }catch (Exception  e){
                e.printStackTrace();
            }

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
        SharedPreferences sslnamepref =getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0);
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
}
