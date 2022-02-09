package com.creativethoughts.iscore.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.UserRegistrationActivity;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


/**
 * Created by muthukrishnan on 11/10/15
 */ 

public class ConnectionUtil {
    public String TAG = "ConnectionUtil";
    private static Context context;
    private static String bankKey =""    ;
    private static String bankHeader=""   ;
    private static String AssetName =""  ;
    private static String Hostname  =""  ;

    private ConnectionUtil(){
        throw new IllegalStateException( "exception ");
    }
    public static String getResponse(String url) {

//        Log.e("ConnectionUtil 571   ","bankKey   "+UserRegistrationActivity.getBankkey()+"  "+UserRegistrationActivity.getBankheader());
//         bankKey      = UserRegistrationActivity.getBankkey();
//         bankHeader   = UserRegistrationActivity.getBankheader();
//         AssetName   = UserRegistrationActivity.getCertificateAssetName();
//         Hostname   = UserRegistrationActivity.getHostnameSubject();
//
//       if (bankKey == null || bankHeader == null || AssetName == null || Hostname == null ){
//            bankKey      = HomeActivity.getBankkey();
//            bankHeader   = HomeActivity.getBankheader();
//            AssetName   = HomeActivity.getCertificateAssetName();
//            Hostname   = HomeActivity.getHostnameSubject();
//        }

        bankKey      = "d.22333";
        bankHeader   = "PERFECT SCORE BANK HEAD OFFICE";
        AssetName   = "staticvm.pem";
        Hostname   = "STATIC-VM";

//        bankKey      = SplashScreen.BankKey;
//        bankHeader   = SplashScreen.BankHeader;


        Log.e("ConnectionUtil 571   ","bankKey   "+bankKey+"  "+bankHeader);

        String bankVerified = "1";
//        SharedPreferences BankVerifierSP = context.getSharedPreferences(Config.SHARED_PREF32, 0);
//        String BankVerifier = BankVerifierSP.getString("BankVerifier","");
//        if (BankVerifier.equals("")){
//            bankVerified = "0";
//        }else {
//            bankVerified = "1";
//        }
        Log.e("ConnectionUtil 5712   ","bankKey   "+bankKey+"  "+bankHeader+"  "+AssetName+"  "+Hostname+"  "+bankVerified);

        if ( ContextCompat.checkSelfPermission(IScoreApplication.getAppContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return IScoreApplication.EXCEPTION_NOPHONE_PERMISSION;
        }

        String iemi =   IScoreApplication.getIEMI();
        Log.e("imei","         myy     "+iemi);

        if (iemi.equals(IScoreApplication.EXCEPTION_NOIEMI)){
            Log.e("TEST","         14     "+iemi);
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
        Log.e("TEST","         15     "+iemi);
        url = url+"&imei="+iemi;
//        if ( UserCredentialDAO.getInstance().isUserAlreadyLogin() ) {
//            String token = UserCredentialDAO.getInstance().getLoginCredential().token;
//            Log.e("TEST","         1     "+iemi);
//            try {
//                url = url+"&token="+token;
//                Log.e("TEST","         12     "+iemi);
//            } catch (Exception e) {
//                Log.e("exception",e.toString()+"");
//                if (IScoreApplication.DEBUG)
//                url = url +"&token=exceptiontoken";
//            }
//        }
      //  SharedPreferences loginSP = context.getSharedPreferences(Config.SHARED_PREF33, 0);
        String login = "0";
        if ( login.equals("0")) {
           // String token = UserCredentialDAO.getInstance().getLoginCredential().token;
//            SharedPreferences tokenIdSP = .getSharedPreferences(Config.SHARED_PREF35, 0);
//            String token = tokenIdSP.getString("Token","");
            String token = "";
            Log.e("TEST","         1     "+iemi);
            try {
                url = url+"&token="+token;
                Log.e("TEST","         12     "+iemi);
            } catch (Exception e) {
                Log.e("exception",e.toString()+"");
                if (IScoreApplication.DEBUG)
                    url = url +"&token=exceptiontoken";
            }
        }


        Log.e("TEST","         16     "+iemi);
        Log.e("ConnectionUtil 57122   ","bankKey   "+bankKey+"  "+bankHeader+"  "+AssetName+"  "+Hostname+"  "+bankVerified);
        if (!bankHeader.trim().isEmpty() && !bankKey.trim().isEmpty() ){
            try {
                Log.e("TEST","         13     "+iemi);
                url=url+"&BankKey="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankKey))+"&BankHeader="+
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankHeader))+
                        "&BankVerified="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankVerified));
                Log.e("TAG  ","url971    "+url+"");
            } catch (Exception e) {
                Log.e("Exception 97   ",""+e.toString());
                return IScoreApplication.EXCEPTION_ENCRIPTION_IEMI;

            }
        }


        Log.e("url97   ",url+"");
        if ( IScoreApplication.DEBUG )
            Log.e("url",url+"");
        String result ;
        URL updateURL  ;
       HttpsURLConnection  connection = null;
        try {

            HostnameVerifier hostnameVerifier = ( hostname, session ) ->{
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();

                if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 ){
                    return true;
                }else {
                   // return hv.verify(UserRegistrationActivity.getHostnameSubject()+"", session )  ;
                    return hv.verify(Hostname+"", session )  ;

                }
            };
            updateURL = new URL(url);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

//            InputStream caInput =  IScoreApplication.getAppContext().
//                    getAssets().open( UserRegistrationActivity.getCertificateAssetName());
            InputStream caInput =  IScoreApplication.getAppContext().
                    getAssets().open( AssetName);


            Certificate ca;

            ca = cf.generateCertificate(caInput);

            if(IScoreApplication.DEBUG)   Log.e("ca","ca=" + ((X509Certificate) ca).getSubjectDN()+"");

            caInput.close();

            if(IScoreApplication.DEBUG)   Log.e("created url",url+"");

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLSv1.2");

            context.init(null, tmf.getTrustManagers(), null);

// Tell the URLConnection to use a SocketFactory from our SSLContext
            connection = (HttpsURLConnection) updateURL.openConnection();


            if( Common.isHostnameverficationManual() ) {
                connection.setHostnameVerifier(hostnameVerifier);

            }
            connection.setConnectTimeout(60000);
            connection.setSSLSocketFactory(context.getSocketFactory());
            int status = connection.getResponseCode();
            if ( status != 200 ){
                return IScoreApplication.SERVICE_NOT_AVAILABLE;
            }

            InputStream is = new BufferedInputStream( connection.getInputStream());

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder("");
            String line;
            String nl = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {
                sb.append(line).append(nl);
            }

                result = sb.toString();
        }
        catch ( IOException | NoSuchAlgorithmException | KeyManagementException | CertificateException | KeyStoreException e ) {
            e.printStackTrace();
            if (IScoreApplication.DEBUG)Log.e("Exception",e.toString()+"");
            result="";
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (IScoreApplication.DEBUG)Log.e("result",result+"");
        return result;

    }
}

