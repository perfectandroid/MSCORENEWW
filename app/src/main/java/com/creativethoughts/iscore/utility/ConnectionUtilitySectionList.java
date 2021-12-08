package com.creativethoughts.iscore.utility;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SplashScreen;
import com.creativethoughts.iscore.UserRegistrationActivity;
import com.creativethoughts.iscore.db.dao.BankVerifier;

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

import com.creativethoughts.iscore.Helper.Common;

/**
 * Created by vishnu on 3/17/2017 - 9:39 AM
 */

public class ConnectionUtilitySectionList {
    private static Context context;

    private ConnectionUtilitySectionList(){
        //Do nothing
    }
    public static String getResponse(String url) {

      /*  String bankKey      = IScoreApplication.getAppContext().getResources().getString(R.string.BankKey);
        String bankHeader   = IScoreApplication.getAppContext().getResources().getString( R.string.BankHeader );
*/

        String bankKey      = UserRegistrationActivity.getBankkey();
        String bankHeader   = UserRegistrationActivity.getBankheader();

        String bankVerified = BankVerifier.getInstance().getVerifyStatus();

        if (ContextCompat.checkSelfPermission(IScoreApplication.getAppContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            return IScoreApplication.EXCEPTION_NOPHONE_PERMISSION;
        }

        String iemi=IScoreApplication.getIEMI();
        if (iemi.equals(IScoreApplication.EXCEPTION_NOIEMI)){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
        url=url+"&imei="+iemi;


        if ( ! bankHeader.trim().isEmpty() && !bankKey.trim().isEmpty() ){
            try {
                url = url+"&BankKey="+ IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankKey))+"&BankHeader="+
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankHeader))+
                        "&BankVerified="+IScoreApplication.encodedUrl(IScoreApplication.encryptStart(bankVerified));

            } catch (Exception e) {
                return IScoreApplication.EXCEPTION_ENCRIPTION_IEMI;
            }
        }


        if (IScoreApplication.DEBUG)  Log.e("url",url);
        String result = "";

        URL updateURL;


        HttpsURLConnection connection = null;

        try {

            /*HostnameVerifier hostnameVerifier = (hostname, session) -> {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(Common.getHostnameSubject()+"", session) *//*true*//*;
            };*/
            HostnameVerifier hostnameVerifier = ( hostname, session ) ->{
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();

                if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 ){
                    return true;
                }else {
                    return hv.verify(UserRegistrationActivity.getHostnameSubject()+"", session )  ;
                }
            };

            updateURL = new URL(url);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput =  IScoreApplication.getAppContext().
                    getAssets().open(UserRegistrationActivity.getCertificateAssetName());


            Certificate ca;

            ca = cf.generateCertificate(caInput);
            if(IScoreApplication.DEBUG)   Log.e("ca","ca=" + ((X509Certificate) ca).getSubjectDN()+"");

            caInput.close();

            if(IScoreApplication.DEBUG)   Log.e("created url",url+"");

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            SSLContext context = SSLContext.getInstance("TLSv1.2");

            context.init(null, tmf.getTrustManagers(), null);

            connection = (HttpsURLConnection) updateURL.openConnection();


            if(Common.isHostnameverficationManual()) {
                connection.setHostnameVerifier(hostnameVerifier);
            }
            connection.setConnectTimeout(60000);
            connection.setSSLSocketFactory(context.getSocketFactory());



            InputStream is = new BufferedInputStream(connection.getInputStream());

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder("");
            String line;
            String nl = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {
                sb.append(line).append(nl);
            }
            int status = connection.getResponseCode();

            if (status == 200) {
                result = sb.toString();

            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | CertificateException | KeyStoreException e) {
            if (IScoreApplication.DEBUG)Log.e("Exception",e.toString()+"");
            result="";
            if ( connection != null )
                connection.disconnect();
        }

        if (IScoreApplication.DEBUG)Log.e("result",result+"");

        return result;

    }

}
