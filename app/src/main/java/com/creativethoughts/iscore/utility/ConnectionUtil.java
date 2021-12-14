package com.creativethoughts.iscore.utility;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.creativethoughts.iscore.Helper.Common;
import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SplashScreen;
import com.creativethoughts.iscore.UserRegistrationActivity;
import com.creativethoughts.iscore.db.dao.BankVerifier;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;

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
    private static Context context;

    private ConnectionUtil(){
        throw new IllegalStateException( "exception ");
    }
    public static String getResponse(String url) {

        String bankKey      = UserRegistrationActivity.getBankkey();
        String bankHeader   = UserRegistrationActivity.getBankheader();
//        String bankKey      = SplashScreen.BankKey;
//        String bankHeader   = SplashScreen.BankHeader;

        String bankVerified = BankVerifier.getInstance().getVerifyStatus();

        if ( ContextCompat.checkSelfPermission(IScoreApplication.getAppContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return IScoreApplication.EXCEPTION_NOPHONE_PERMISSION;
        }

        String iemi =   IScoreApplication.getIEMI();
        Log.e("imei","         myy     "+iemi);

        if (iemi.equals(IScoreApplication.EXCEPTION_NOIEMI)){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
        url = url+"&imei="+iemi;
        if ( UserCredentialDAO.getInstance().isUserAlreadyLogin() ) {
            String token = UserCredentialDAO.getInstance().getLoginCredential().token;

            try {
                url = url+"&token="+token;
            } catch (Exception e) {
                if (IScoreApplication.DEBUG)Log.e("exception",e.toString()+"");
                url = url +"&token=exceptiontoken";
            }
        }

        if ( ! bankHeader.trim().isEmpty() && !bankKey.trim().isEmpty() ){
            try {

                url=url+"&BankKey="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankKey))+"&BankHeader="+
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankHeader))+
                        "&BankVerified="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankVerified));
            } catch (Exception e) {
                return IScoreApplication.EXCEPTION_ENCRIPTION_IEMI;
            }
        }



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
                    return hv.verify(UserRegistrationActivity.getHostnameSubject()+"", session )  ;
//                    return hv.verify(SplashScreen.HOSTNAME_SUBJECT+"", session )  ;
                }
            };
            updateURL = new URL(url);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput =  IScoreApplication.getAppContext().
                    getAssets().open( UserRegistrationActivity.getCertificateAssetName());
//            InputStream caInput =  IScoreApplication.getAppContext().
//                    getAssets().open( SplashScreen.CERTIFICATE_ASSET_NAME);


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

