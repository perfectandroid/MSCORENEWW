package com.creativethoughts.iscore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.db.dao.DbSync;
import com.creativethoughts.iscore.db.dao.UserCredentialDAO;
import com.creativethoughts.iscore.db.dao.UserDetailsDAO;
import com.creativethoughts.iscore.db.dao.model.UserCredential;
import com.creativethoughts.iscore.db.dao.model.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PinLoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnShwPassFirst;
    private Button btnShwPassSecond;
    private Button btnShwPassThird;
    private Button btnShwPassFourth;
    private Button btnShwPassFifth;
    private Button btnShwPassSixth;
    private int counter = 0;
    private Button[] btnArray ;
    private Button[] btnOthersArray;
    private String  firstLetter;
    private String  secondLetter;
    private String  thirdLetter;
    private String  fourthLetter;
    private String  fifthLetter;
    private String  sixthLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_pin_login );

        ImageView imgLogout = findViewById(R.id.img_logout);
        ImageView img_quit = findViewById(R.id.img_quit);
        ImageView imgShare  = findViewById(R.id.img_share);
        TextView txt_user  = findViewById(R.id.txt_user);
        TextView txt_logout  = findViewById(R.id.txt_logout);
        TextView txt_quit  = findViewById(R.id.txt_quit);
        //  ImageView imgClose  = findViewById(R.id.img_Close);

        imgLogout.setOnClickListener(this);
        txt_logout.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        img_quit.setOnClickListener(this);
        txt_quit.setOnClickListener(this);
        //imgClose.setOnClickListener(this);

        Button btnKeyPadOne;
        Button btnKeyPadTwo;
        Button btnKeyPadThree;
        Button btnKeyPadFour;
        Button btnKeyPadFive;
        Button btnKeyPadSix;
        Button btnKeyPadSeven;
        Button btnKeyPadEight;
        Button btnKeyPadNine;
        Button btnKeyPadZero;
        Button btnKeyPadBack;
        Button btnKeyPadClearAll;

        btnShwPassFirst     = findViewById(R.id.btn_pswrd_one );
        btnShwPassSecond    = findViewById(R.id.btn_pswrd_two );
        btnShwPassThird     = findViewById(R.id.btn_pswrd_three );
        btnShwPassFourth    = findViewById(R.id.btn_pswrd_four );
        btnShwPassFifth    = findViewById(R.id.btn_pswrd_five );
        btnShwPassSixth    = findViewById(R.id.btn_pswrd_six );

        btnKeyPadOne    = findViewById(R.id.btn_keypad_one );
        btnKeyPadTwo    = findViewById(R.id.btn_keypad_two );
        btnKeyPadThree  = findViewById(R.id.btn_keypad_three );
        btnKeyPadFour   = findViewById(R.id.btn_keypad_four );
        btnKeyPadFive   = findViewById(R.id.btn_keypad_five );
        btnKeyPadSix    = findViewById(R.id.btn_keypad_six );
        btnKeyPadSeven  = findViewById(R.id.btn_keypad_seven );
        btnKeyPadEight  = findViewById(R.id.btn_keypad_eight );
        btnKeyPadNine   = findViewById(R.id.btn_keypad_nine );
        btnKeyPadZero   = findViewById(R.id.btn_keypad_zero );

        btnKeyPadClearAll   = findViewById(R.id.btn_keypad_clear_all);
        btnKeyPadBack       = findViewById(R.id.btn_keypad_back);

        UserDetails userDetails = UserDetailsDAO.getInstance().getUserDetail();

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            txt_user.setText("Good Morning "+userDetails.userCustomerName);

        }else if(timeOfDay >= 12 && timeOfDay < 16){
            txt_user.setText("Good Afternoon "+userDetails.userCustomerName);

        }else if(timeOfDay >= 16 && timeOfDay < 21){
            txt_user.setText("Good Evening "+userDetails.userCustomerName);

        }else if(timeOfDay >= 21 && timeOfDay < 24){
            txt_user.setText("Good Night "+userDetails.userCustomerName);
        }
//        txt_user.setText("Welcome "+userDetails.userCustomerName);
        // getCustomerImage(userDetails.customerId);
        btnArray    =  new Button[]{btnKeyPadOne, btnKeyPadTwo, btnKeyPadThree,
                btnKeyPadFour, btnKeyPadFive, btnKeyPadSix,
                btnKeyPadSeven, btnKeyPadEight, btnKeyPadNine, btnKeyPadZero};

        btnOthersArray = new Button[]{btnKeyPadClearAll, btnKeyPadBack};

        for (Button btn: btnArray      ) {
            btn.setOnClickListener(this);
        }
        for (Button btn: btnOthersArray      ) {
            btn.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view){

        if (view.getId() == R.id.img_logout){
            logout();
            return;
        }
        if (view.getId() == R.id.txt_logout){
            logout();
            return;
        } if (view.getId() == R.id.img_quit){
            getPermissionAndQuit();
            return;
        }
        if (view.getId() == R.id.txt_quit){
            getPermissionAndQuit();
            return;
        }
        else if (view.getId() == R.id.img_share){
            shareApp();
            return;
        }/*else if(view.getId() == R.id.img_Close){
             finish();
            return;
        }*/

        Button btn = (Button) view;
        if (Arrays.asList(btnArray).contains(btn)){
            //if (counter > 3)
            if (counter > 5)
                return;
            counter++;
            switch (counter){
                case 1:
                    btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                    firstLetter = btn.getText().toString();
                    break;
                case 2:
                    btnShwPassSecond.setBackgroundResource(R.drawable.show_pwd_btn);
                    secondLetter = btn.getText().toString();
                    break;
                case 3:
                    btnShwPassThird.setBackgroundResource(R.drawable.show_pwd_btn);
                    thirdLetter = btn.getText().toString();
                    break;
                case 4:
                    btnShwPassFourth.setBackgroundResource(R.drawable.show_pwd_btn);
                    fourthLetter = btn.getText().toString();

                    /*  if(firstLetter.isEmpty() || secondLetter.isEmpty() || thirdLetter.isEmpty() || fourthLetter.isEmpty()*//*||fifthLetter.isEmpty()||sixthLetter.isEmpty()*//*)
                        return;
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    String formattedDate = df.format(date);
                    SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    SharedPreferences.Editor loginEditer = loginSP.edit();
                    loginEditer.putString("logintime", formattedDate);
                    loginEditer.commit();

                    getObservable( firstLetter + secondLetter + thirdLetter + fourthLetter *//*+ fifthLetter + sixthLetter*//* )
                            .subscribeOn( Schedulers.io() )
                            .observeOn( AndroidSchedulers.mainThread() )
                            .subscribe( getObserver() );*/

                    break;
                case 5:
                    btnShwPassFifth.setBackgroundResource(R.drawable.show_pwd_btn);
                    fifthLetter = btn.getText().toString();
                    break;
                case  6:
                    btnShwPassSixth.setBackgroundResource(R.drawable.show_pwd_btn);
                    sixthLetter = btn.getText().toString();
                    if(firstLetter.isEmpty() || secondLetter.isEmpty() || thirdLetter.isEmpty() || fourthLetter.isEmpty()||fifthLetter.isEmpty()||sixthLetter.isEmpty())
                        return;

                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    String formattedDate = df.format(date);
                    SharedPreferences loginSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    SharedPreferences.Editor loginEditer = loginSP.edit();
                    loginEditer.putString("logintime", formattedDate);
                    loginEditer.commit();

                    getObservable( firstLetter + secondLetter + thirdLetter + fourthLetter + fifthLetter + sixthLetter )
                            .subscribeOn( Schedulers.io() )
                            .observeOn( AndroidSchedulers.mainThread() )
                            .subscribe( getObserver() );
                    break;
                default:
                    break;
            }
        }
        else if (Arrays.asList(btnOthersArray).contains(btn)){
            int id = view.getId();
            switch (id){
                case R.id.btn_keypad_clear_all:
                    counter = 0;
                    btnShwPassFirst.setBackgroundResource(R.drawable.empty_password);
                    btnShwPassSecond.setBackgroundResource(R.drawable.empty_password);
                    btnShwPassThird.setBackgroundResource(R.drawable.empty_password);
                    btnShwPassFourth.setBackgroundResource(R.drawable.empty_password);
                    btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                    btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                    break;
                case R.id.btn_keypad_back:
                    if ( counter > 0 )
                        counter--;
                    switch (counter){
                        case 5:
                            btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSecond.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassThird.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassFourth.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassFifth.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        case 4:
                            btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSecond.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassThird.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassFourth.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        case 3:
                            btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSecond.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassThird.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassFourth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        case 2:
                            btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSecond.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassThird.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFourth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        case 1:
                            btnShwPassFirst.setBackgroundResource(R.drawable.show_pwd_btn);
                            btnShwPassSecond.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassThird.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFourth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        case 0:
                            btnShwPassFirst.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSecond.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassThird.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFourth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassFifth.setBackgroundResource(R.drawable.empty_password);
                            btnShwPassSixth.setBackgroundResource(R.drawable.empty_password);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private io.reactivex.Observable<Integer> getObservable(String pin) {
        return io.reactivex.Observable.fromCallable(()-> getAuthenticate( pin ));
    }
    private Observer< Integer > getObserver(){
        return new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Do nothing
            }

            @Override
            public void onNext(Integer result) {
                if ( result == 1 )
                    openHomeSceen();
                else {
                    Toast toast =
                            Toast.makeText(PinLoginActivity.this, "Invalid Pin No.", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onError(Throwable e) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                //Do nothing
            }
        };
    }
    private int getAuthenticate( String pin ){
        UserCredential loginCredential = UserCredentialDAO.getInstance().getLoginCredential();

        if (loginCredential != null && loginCredential.flag == 1 && pin
                .equalsIgnoreCase(loginCredential.pin)) {
            return 1;
        }

        return -1;
    }
    private void openHomeSceen() {
        Intent passBookAccount = new Intent(PinLoginActivity.this, HomeActivity.class);
        startActivity(passBookAccount);

        finish();
    }

    private void logout(){
        /*UserCredentialDAO.getInstance().deleteAllUserData();
        UserDetailsDAO.getInstance().deleteAllRows();
        PBAccountInfoDAO.getInstance().deleteAllRows();
        PBMessagesDAO.getInstance().deleteAllRows();
        RechargeDAO.getInstance().deleteAllRows();
        NewTransactionDAO.getInstance().deleteAllRow();
        SettingsDAO.getInstance().deleteAllRows();
        BankVerifier.getInstance().deleteAllRows();
        DynamicMenuDao.getInstance().deleteAll();
        KsebBillDAO.getInstance().deleteAll();

        Intent intent = new Intent(PinLoginActivity.this, UserRegistrationActivity.class);
        intent.putExtra("from","true");
        startActivity(intent);
        finish();*/
//        new SweetAlertDialog(this,SweetAlertDialog.CUSTOM_IMAGE_TYPE)
//                // .setTitleText("Are you sure?")
//                .setContentText("Do you want to delete this account and register with another account?")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCustomImage(R.drawable.aappicon)
//                .setConfirmClickListener(sweetAlertDialog ->DbSync.getInstance().logout( this ) )
//                .show();
        delete();
    }

    private void delete(){
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.logout_popup, null);
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

                    DbSync.getInstance().logout( PinLoginActivity.this );
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareApp(){

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
        String url = pref.getString("PlayStoreLink", null);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,url);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_link));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void getPermissionAndQuit() {

//        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
//                // .setTitleText("Are you sure?")
//                .setContentText("Do you want to Quit?")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCustomImage(R.drawable.aappicon)
//                .setConfirmClickListener( sweetAlertDialog -> finish())
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
}




