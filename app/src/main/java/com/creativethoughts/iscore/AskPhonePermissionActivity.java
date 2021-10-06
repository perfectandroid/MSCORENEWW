package com.creativethoughts.iscore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AskPhonePermissionActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_permission_layout);
        Button mGoToSettingsBtn = findViewById(R.id.buttonOkayPhonePermission);
        assert mGoToSettingsBtn != null;
        mGoToSettingsBtn.setOnClickListener(v -> {
            Log.e("clicked","Yes");
            goToSettings();
        });
        if (ContextCompat.checkSelfPermission(AskPhonePermissionActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


// Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(AskPhonePermissionActivity.this,
                Manifest.permission.READ_PHONE_STATE)) {

            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(AskPhonePermissionActivity.this);
            alertDialogBuilder.setMessage("We are asking phone permission only for security purpose.Please allow this permission");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                ActivityCompat.requestPermissions(AskPhonePermissionActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                dialog.dismiss();

            });
            alertDialogBuilder.show();

        } else {

            ActivityCompat.requestPermissions(AskPhonePermissionActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }


        }else {
            Intent intent=new Intent(AskPhonePermissionActivity.this,UserRegistrationActivity.class);
            startActivity(intent);finish();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(getIntent());

                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(AskPhonePermissionActivity.this,
                            Manifest.permission.READ_PHONE_STATE)) {

                        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(AskPhonePermissionActivity.this);
                        alertDialogBuilder.setMessage("We are asking this permission only for security purpose.Please allow this permission");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                            ActivityCompat.requestPermissions(AskPhonePermissionActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                            dialog.dismiss();

                        });
                        alertDialogBuilder.show();

                    } else {

                        ActivityCompat.requestPermissions(AskPhonePermissionActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void goToSettings(){

        Log.e("reached","goToSettings");
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            Log.e("reached","okay");


        }catch (Exception e){
            if (IScoreApplication.DEBUG)Log.e("exception",e.toString()+"");
        }

    }
}
