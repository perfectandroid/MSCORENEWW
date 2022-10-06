package com.creativethoughts.iscore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.receiver.ConnectivityReceiver;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.PreferenceUtil;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


import com.creativethoughts.iscore.Helper.Common;

/**
 * Created by muthukrishnan on 22/09/15
 */
public class IScoreApplication extends Application {
    private static Context context;

    public static final boolean DEBUG = false;
    public static final String SERVICE_NOT_AVAILABLE = "Service is not available";
    public static final String EXCEPTION_NOIEMI = "EXCEPTION_NOIEMI";
    public static final String EXCEPTION_ENCRIPTION_IEMI = "EXCEPTION_NO_ENCRIPTION_IEMI";
    public static final int FLAG_NETWORK_EXCEPTION = 24252;
    private static final int timeout = Common.timeInMillisecond;
    public static final String EXCEPTION_NOPHONE_PERMISSION = "EXCEPTION_NOPHONE_PERMISSION";
    public static final String MSG_EXCEPTION_NETWORK = "Network error occured. Please try again later";
    public static final String versionName = BuildConfig.VERSION_NAME;
    private static final String MSG_NOPHONE_PERMISSION = "Please enable phone permission in settings.";
    private static final String FATAL_EXCEPTION = "Fatal Exception";
    private static final String MSG_NO_IMEI = "Please use valid mobile phone to continue";

    private static final int FLAG_NOIEMI = 54412;
    private static final int FLAG_NO_PHONEPERMISSION = 655;
    private static final int FLAG_EXCEPTION_IEMI_ENCRYPTION = 825;


    public static final String OTHER_FUND_TRANSFER_MODE_NEFT = "NEFT";
    public static final String OTHER_FUND_TRANSFER_MODE_RTGS = "RTGS";
    public static final String OTHER_FUND_TRANSFER_MODE_IMPS = "IMPS";
    public static final String OTHER_FUND_TRANSFER_MODE_QKPY = "QKPY";

    private static final String MSG_EXCEPTION_ENCRYPTION_IMEI = "Please use valid mobile phone to continue.ERROR CODE:" + FLAG_EXCEPTION_IEMI_ENCRYPTION;
    public static final String OOPS = "Oops...";
    private static IScoreApplication mInstance;
    private CountDownTimer countDownTimer;

    public static void toastMessage(String message, int length) {
        Toast.makeText(mInstance, message, length).show();
    }


    public static Application getAppContext() {
        return mInstance;
    }

    public static String encodedUrl(String parameterPass) throws UnsupportedEncodingException {
        return URLEncoder.encode(parameterPass, "UTF-8");
    }

    public static String encryptStart(String encypt) throws Exception {
        String te = encypt;

        String encrypted = encrypt(te);
        return encrypted;
    }

    private static String encrypt(String inputText) throws Exception {
        String s = "Agentscr";
        byte[] keyValue = s.getBytes("US-ASCII");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            KeySpec keySpec = new DESKeySpec(keyValue);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(keyValue);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            bout.write(cipher.doFinal(inputText.getBytes("ASCII")));
        } catch (Exception e) {
            System.out.println("Exception .. " + e.getMessage());
        }

        return new String(Base64.encode(bout.toByteArray(), Base64.DEFAULT), "ASCII");

    }

    public static String getIEMI() {
        try {

            return "f34d64b61f8baf6f";
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }


    public static String getBankkey() {
        try {
            String bank_Key, bankKey;
            SharedPreferences bankkeypref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0);
            bank_Key=bankkeypref.getString("bankkey", null);
            bankKey      = SplashScreen.BankKey;

            if (bank_Key.isEmpty()){
                return bankKey;
            }else {
                return bank_Key;
            }
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }
    public static String getBankheader() {
        try {
            String bankHeader,bank_Header;

            SharedPreferences bankheaderpref =context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0);
            bankHeader=bankheaderpref.getString("bankheader", null);
            bank_Header  = SplashScreen.BankHeader;

            if (bankHeader.isEmpty()){
                return bank_Header;
            }else {
                return bankHeader;
            }
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static boolean checkPermissionIemi(int result,Context context){
        if (result==IScoreApplication.FLAG_NOIEMI){
            DialogUtil.showAlert(context,
                    IScoreApplication.MSG_NO_IMEI);
            return false;
        }else if (result==IScoreApplication.FLAG_NO_PHONEPERMISSION){
            DialogUtil.showAlert(context,
                    IScoreApplication.MSG_NOPHONE_PERMISSION);
            return false;
        }else if (result==IScoreApplication.FLAG_EXCEPTION_IEMI_ENCRYPTION){
            DialogUtil.showAlert(context,
                    IScoreApplication.MSG_EXCEPTION_ENCRYPTION_IMEI);
            return false;
        }else {
            return true;
        }
    }

 public static boolean containAnyKnownException(String response){
     return response.equals(IScoreApplication.EXCEPTION_NOIEMI) ||
             response.equals(IScoreApplication.EXCEPTION_ENCRIPTION_IEMI) ||
             response.equals(IScoreApplication.EXCEPTION_NOPHONE_PERMISSION);
 }
 public static void simpleAlertDialog( Context context, AlertProcess alertProcess, String message ){

     AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
     alertDialogBuilder.setMessage(message);
     alertDialogBuilder.setCancelable(false);
     alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
         dialog.dismiss(); alertProcess.ok( );
     } );
     alertDialogBuilder.setNegativeButton("Cancel", (dialog,which)->{
         dialog.dismiss();
         alertProcess.cancel( );
     });
     alertDialogBuilder.show();
 }
 public interface AlertProcess{
        void ok( );
        void cancel( );
 }
  public static int getFlagException(String response){
      switch (response) {
          case IScoreApplication.EXCEPTION_NOIEMI:
              return FLAG_NOIEMI;
          case IScoreApplication.EXCEPTION_ENCRIPTION_IEMI:
              return FLAG_EXCEPTION_IEMI_ENCRYPTION;
          case IScoreApplication.EXCEPTION_NOPHONE_PERMISSION:
              return FLAG_NO_PHONEPERMISSION;
          default:
              return 0;
      }
  }

    public static String decryptStart(String decryp)throws Exception{
        return decrypt(decryp);
    }

    private static String decrypt(String inputText) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String s = "Agentscr";
        byte[] keyValue = s.getBytes("US-ASCII");
//        byte[] keyValue = new byte[] { 'm', 'y', 'k', 'e', 'y', 'n', 'u', 'l'};
        try {
            KeySpec keySpec = new DESKeySpec(keyValue);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
            IvParameterSpec iv = new IvParameterSpec(keyValue);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,key,iv);
            byte[] decoded=   android.util.Base64.decode(inputText, android.util.Base64.DEFAULT);
//           byte[] decoded = Base64.decodeBase64(inputText); //Works with apache.commons.codec-1.8
//            byte[] decoded = Base64.decodeBase64(inputText.getBytes("ASCII"));// works with apache.commons.codec-1.3
            bout.write(cipher.doFinal(decoded));
        } catch(Exception e) {
            System.out.println("Exception ... "+e);
        }
        return new String(bout.toByteArray(),"ASCII");
    }

    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
   /* public static boolean isRooted() {

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }*/

    /*private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }*/

    public static void logoutNow(){


//        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {

        Toast.makeText(mInstance, "Logging out", Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ActivityManager)mInstance.getSystemService(ACTIVITY_SERVICE))
                    .clearApplicationUserData(); // note: it has a return value!
        }else{
             // use old hacky way, which can be removed
            // once minSdkVersion goes above 19 in a few years.
            clearApplicationData(mInstance);
        }
    }

    public static void clearApplicationData(Context context) {

        File cacheDirectory = context.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {

            String[] fileNames = applicationDirectory.list();

            for (String fileName : fileNames) {

//                if (!fileName.equals("lib")) {

                 deleteFile(new File(applicationDirectory, fileName));

//                }

            }

        }
        Toast.makeText(context, "Logging out.", Toast.LENGTH_LONG).show();

    }

    public static boolean deleteFile(File file) {

        boolean deletedAll = true;

        if (file != null) {

            if (file.isDirectory()) {

                String[] children = file.list();

                for (String aChildren : children) {

                    deletedAll = deleteFile(new File(file, aChildren)) && deletedAll;

                }

            } else {

                deletedAll = file.delete();

            }

        }

        return deletedAll;

    }

    @Override
    public void onCreate() {

//        FireCrasher.install(this, new CrashListener() {
//
//            @Override
//            public void onCrash(Throwable throwable, final Activity activity) {
//
//                StringWriter sw = new StringWriter();
//                PrintWriter pw = new PrintWriter(sw);
//                throwable.printStackTrace(pw);
//
//                sendMail(new String[] {"rmkrishna.sacoe@gmail.com"}, "Mscore crash", sw.toString(), activity);
////                Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show();
//
//                // start the recovering process
//                recover(activity);
////                activity.finish();
//
//
//                //you need to add your crash reporting tool here
//                //Ex: Crashlytics.logException(throwable);
//            }
//        });

        super.onCreate();

//        CrashWoodpecker.init(this);

        mInstance = this;

      //  IScoreDatabase.initDataBase(this);

        NotificationMgr.init(this);

        PreferenceUtil.init(this);

        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if (ContextCompat.checkSelfPermission(IScoreApplication.getAppContext(),
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    IScoreApplication.toastMessage("Network connection may fail if phone permission denied." +
                            "Please allow phone permission from settings-apps-permission",
                            Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //Do nothing
            }

            @Override
            public void onActivityResumed(Activity activity) {

                try {
                    if (countDownTimer != null) countDownTimer.cancel();
                }catch (Exception ignored){

                }
            }

            @Override
            public void onActivityPaused(final Activity activity) {


                countDownTimer = new CountDownTimer(timeout, 1000) {

                    public void onTick(long millisUntilFinished) {

                        if (IScoreApplication.DEBUG) Log.e("ontick",(millisUntilFinished/1000)+"");
                    }

                    public void onFinish() {

                        Intent intent=new Intent(activity,TimeOutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

//                        System.exit(0);
                    }
                }.start();

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //Do nothing
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                //Do nothing
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //Do nothing
            }
        });

    }
    public static void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
