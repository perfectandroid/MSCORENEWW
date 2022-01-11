package com.creativethoughts.iscore;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.db.dao.PBMessagesDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserDetails;
import com.creativethoughts.iscore.receiver.ConnectivityReceiver;
import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.DialogUtil;
import com.creativethoughts.iscore.utility.NetworkUtil;
import com.creativethoughts.iscore.utility.SyncAll;
import com.creativethoughts.iscore.utility.network.NetworkManager;
import com.creativethoughts.iscore.utility.network.ResponseManager;


import java.net.MalformedURLException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity implements NavigationDrawerCallbacks, ConnectivityReceiver.ConnectivityReceiverListener {

    static String bank_Key, bank_Header;
    static String host_nameCommon, asset_namecommon;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String mParamsHideRecharge ;
    private String mParamsHideMoneyTransfer;

    private  int getCurrentVersionNumber(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
        }

        return 1;
    }


    @Override
    public void onPause() {

        super.onPause();  // Always call the superclass method first
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,  findViewById(R.id.drawer_layout));

        bank_Key = getApplicationContext().getSharedPreferences(Config.SHARED_PREF9, 0).getString("bankkey", null);
        bank_Header =getApplicationContext().getSharedPreferences(Config.SHARED_PREF11, 0).getString("bankheader", null);
        host_nameCommon = getApplicationContext().getSharedPreferences(Config.SHARED_PREF23, 0).getString("hostname", null);
        asset_namecommon =getApplicationContext().getSharedPreferences(Config.SHARED_PREF24, 0).getString("certificateassetname", null);
        onSectionAttached(0);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onBackPressed();
       versionCheck();


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        mParamsHideRecharge = "EMPTY";
        mParamsHideMoneyTransfer  = "EMPTY";
        Fragment fragment = null;
        Intent i;
        switch (position) {
            case 0:
                fragment = FragmentMenuCard.newInstance(mParamsHideRecharge, mParamsHideMoneyTransfer);
                Log.i("position",String.valueOf(position));
                break;
            case 1:
                fragment = ProfileFragment.newInstance();
              /*i=new Intent(this,ProfileActivity.class);
              startActivity(i);*/

                break;
            case 2:
                if(mNavigationDrawerFragment != null) {
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    PBMessagesDAO.getInstance().markMessageAsRead(userDetails.customerId);
                    mNavigationDrawerFragment.refreshMenu();
                }
                fragment = MessagesFragment.newInstance();
                break;
            case 3:
                if(mNavigationDrawerFragment != null) {
                    UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();
                    PBMessagesDAO.getInstance().markOffersAsRead(userDetails.customerId);
                    mNavigationDrawerFragment.refreshMenu();
                }
                fragment = OffersFragment.newInstance();
                break;
            case 4:
                fragment = KsebBillStatusFragment.newInstance();
                break;
            case 5:

              /*  i=new Intent(this,MoreActivity.class);
                startActivity(i);*/

                fragment = MoreFragment.newInstance();
                break;
            case 6:
               /* i=new Intent(this, DuedateActivity.class);
                startActivity(i);*/
                fragment = DuedatesFragment.newInstance();
                break;
            case 7:
              /*  i=new Intent(this,MoreActivity.class);
                startActivity(i);*/

                fragment = MoreFragment.newInstance();
                break;
            case 8:
                fragment = ChangePinFragment.newInstance();
                break;
            case 9:
                fragment = SettingsFragment.newInstance();
                break;
            case 10:
                getPermissionAndQuit();
                break;
            case 11:
                getPermissionAndQuit();
                break;
            case 12:
                break;
            default:
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
            onSectionAttached(position);
        }
    }


    private void onSectionAttached(int number) {

        String mTitle = "";
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section9);
                break;
            case 1:
                mTitle = "Profile";
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.kseb_sub_name_bill_status_on_drawer);
                break;
            case 5:
                mTitle = "More";
                break;
            case 6:
                mTitle = "Due Date Reminder";
                break;
            case 7:
                mTitle = "More";
                break;
            case 8:
                mTitle = getString(R.string.title_section5);
                break;
            case 9:
                mTitle = getString(R.string.title_section6);
                break;
            case 10:
                mTitle = getString(R.string.title_section8);
                break;
            case 11:
                mTitle = getString(R.string.title_section8);
                break;

            default:
                break;
        }

        try{
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(mTitle);
        }catch (Exception e){
            Log.e("REcharge", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.container );
            if ( currentFragment instanceof FragmentMenuCard ){
                getPermissionAndQuit();
            }else {
                super.onBackPressed();
            }
        }
    }

    private void getPermissionAndQuit() {

//        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
////                .setTitleText("Are you sure?")
//                .setContentText("Do you want to quit?")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCustomImage(R.drawable.aappicon)
//                 .setConfirmClickListener( sweetAlertDialog -> finishAffinity())
//                .show();
        Quit();
    }

    private void Quit(){
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.quit_popup, null);
            TextView tv_share =  layout.findViewById(R.id.tv_share);
            TextView tv_cancel =  layout.findViewById(R.id.tv_cancel);

            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();




            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finishAffinity();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        versionCheck();

    }
    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        try{
            IScoreApplication.setConnectivityListener( this );
        }catch ( Exception e ){
            //Do nothing
        }
    }

    private void versionCheck(){
        if (NetworkUtil.isOnline()) {
            int versionNumber = getCurrentVersionNumber(HomeActivity.this);
            SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF8, 0);
            String BASE_URL=pref.getString("oldbaseurl", null);
            String url;
            try{
                url = BASE_URL +
                        "/Checkstatus?versionNo="+
                        IScoreApplication.encodedUrl(IScoreApplication.encryptStart(versionNumber +""));
            }catch ( Exception e ){
                url = "";
            }
            NetworkManager.getInstance().connector(url, new ResponseManager() {
                @Override
                public void onSuccess(String result) {
                    result = result.trim();
                    try{
                        int res = Integer.parseInt( result );
                        if ( res == 10 )
                            goToPlayStore();
                    }catch ( Exception e ){
                        //Do nothing
                    }

                }

                @Override
                public void onError(String error) {
                    //Do nothing
                }
            }, null,   null);
        }
    }

    private void goToPlayStore( ){
        try{
//            String url = getResources().getString(R.string.app_link );
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
            String url = pref.getString("PlayStoreLink", null);

            new URL( url );
        }catch ( MalformedURLException e ){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setMessage("The app is under maintenance. Sorry for the inconvenience.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) ->  finish() );
            alertDialogBuilder.show();
            return;
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog( HomeActivity.this , SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialog
                .setCustomImage(R.drawable.aappicon)
                .setConfirmText("OK!")
                .showCancelButton(true)
                .setTitleText("New Version Available")
                .setContentText("New version of this application is available.\nClick OK to upgrade now")
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog1.dismissWithAnimation();
                    finish();
//                    String url = getResources().getString(R.string.app_link );

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);

                    String url = pref.getString("PlayStoreLink", null);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                })
                .setCancelable(false);
        sweetAlertDialog.show();

    }


    public static String getBankkey() {
        try {
            return bank_Key;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getBankheader() {
        try {
            return bank_Header;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getHostnameSubject() {
        try {
            return host_nameCommon;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }

    public static String getCertificateAssetName() {
        try {
            return asset_namecommon;
        }catch (Exception e){
            return IScoreApplication.EXCEPTION_NOIEMI;
        }
    }


}
