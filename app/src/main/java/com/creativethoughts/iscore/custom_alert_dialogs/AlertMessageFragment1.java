package com.creativethoughts.iscore.custom_alert_dialogs;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.kseb.KsebRechargeStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertMessageFragment1 extends Fragment {

    private RecyclerView mRecyclerView;
    private static final String KEY_VALUE = "keyvalue";
    private static final String TITLE = "title";
    private static final String HAPPY = "happy";
    private static final String FROM = "from";
    private static final String ISBACK = "false";
    String from1;
    private static final String MESSAGE = "message";
    private static final String BUNDLE_KSEB_MODEL = "ksebRechargeStatus";
    private KsebRechargeStatus ksebRechargeStatus;
    String TAG = "FundTransferFragment";
    public AlertMessageFragment1() {
        // Required empty public constructor
    }

    public static AlertMessageFragment1 getInstance(ArrayList<KeyValuePair> keyValuePairs, String title, String message, KsebRechargeStatus ksebRechargeStatus, boolean isHappy, boolean isBackButtonEnable, String from){
  //  public static AlertMessageFragment getInstance(ArrayList<KeyValuePair> keyValuePairs, String title, String message, PaymentModel mPaymentModel, boolean isHappy, boolean isBackButtonEnable){
        AlertMessageFragment1 alertMessageFragment = new AlertMessageFragment1();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList( KEY_VALUE, keyValuePairs );
        bundle.putString( TITLE, title );
        bundle.putParcelable( BUNDLE_KSEB_MODEL, ksebRechargeStatus );
        bundle.putBoolean( HAPPY, isHappy );
        bundle.putString( MESSAGE, message );
        bundle.putString( FROM, from );
        bundle.putString( ISBACK, String.valueOf(isBackButtonEnable));
        alertMessageFragment.setArguments(bundle);

        return alertMessageFragment;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach( context );

        Bundle bundle = getArguments();
        if ( bundle != null ){
            ksebRechargeStatus = bundle.getParcelable(BUNDLE_KSEB_MODEL);
            from1 =bundle.getParcelable(FROM);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        View view = inflater.inflate(R.layout.fragment_message_alert_new1,container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        view.findViewById( R.id.rltv_footer ).setOnClickListener(view1 -> {
            try{
//                getFragmentManager().beginTransaction().replace( R.id.container, FragmentMenuCard.newInstance("EMPTY","EMPTY") )
//                        .commit();
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();

            }catch ( NullPointerException e ){
                //Do nothing
            }
        } );
        RelativeLayout rltv_share = view.findViewById( R.id.rltv_share );
        RelativeLayout lay_share = view.findViewById( R.id.lay_share );
        mRecyclerView = view.findViewById( R.id.recycler_message );
        ImageView imgIcon      = view.findViewById( R.id.img_success );
        ImageView img_share      = view.findViewById( R.id.img_share );
        TextView txtTitle       = view.findViewById( R.id.txt_success );
        TextView txtMessage = view.findViewById( R.id.txt_message );
        TextView txtpfrom = view.findViewById( R.id.txtpfrom );
        TextView txtTo = view.findViewById( R.id.txtTo );



        TextView tvdate = view.findViewById( R.id.tvdate );
        TextView tvtime = view.findViewById( R.id.tvtime );
        TextView tv_amount_words = view.findViewById( R.id.tv_amount_words );

        TextView tv_amount = view.findViewById(R.id.tv_amount);
        TextView txtvAcntno = view.findViewById(R.id.txtvAcntno);
        TextView txtvbranch = view.findViewById(R.id.txtvbranch);
        TextView txtvmob = view.findViewById(R.id.txtvmob);



        TextView txtvAcntnoto = view.findViewById(R.id.txtvconsumersectn);
        TextView txtvbranchto = view.findViewById(R.id.txtvconsumerno);
        TextView txtvbill = view.findViewById(R.id.txtvbill);

        txtTo.setText("Consumer Details");

    /*   if(from1!=null)
       {
           if(from1.equals("KSEB")) {

           }
           else
           {
               txtpfrom.setText("Paying To");
           }
       }
*/

        //current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvtime.setText("Time : "+currentTime);

        //current date

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

       // txtvbranch.setText(mPaymentModel.getBranch());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        tvdate.setText("Date : "+formattedDate);

   /*    if(ksebRechargeStatus.getAmount()!=null)
        {
            String amnt = ksebRechargeStatus.getAmount().replaceAll(",", "");
            String[] netAmountArr = amnt.split("\\.");
            String amountInWordPop = "";
            if ( netAmountArr.length > 0 ){
                int integerValue = Integer.parseInt( netAmountArr[0] );
                amountInWordPop = "Rupees " + NumberToWord.convertNumberToWords( integerValue );
                if ( netAmountArr.length > 1 ){
                    int decimalValue = Integer.parseInt( netAmountArr[1] );
                    if ( decimalValue != 0 ){
                        amountInWordPop += " and " + NumberToWord.convertNumberToWords( decimalValue ) + " paise" ;
                    }
                }
                amountInWordPop += " only";
            }
            tv_amount_words.setText(""+amountInWordPop);
            double num = Double.parseDouble(""+amnt);
            Log.e(TAG,"CommonUtilities  945   "+ CommonUtilities.getDecimelFormate(num));
            String stramnt = CommonUtilities.getDecimelFormate(num);


            tv_amount.setText("₹ " + stramnt );

            double num1 = Double.parseDouble(ksebRechargeStatus.getAmount())-Double.parseDouble(stramnt);
            DecimalFormat fmt = new DecimalFormat("#,##,###.00");

            txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));

        }
        else {

           double num1 = Double.parseDouble(ksebRechargeStatus.getAmount());
           DecimalFormat fmt = new DecimalFormat("#,##,###.00");

           txtvbalnce.setText("Available Bal: "+"\u20B9 "+ CommonUtilities.getDecimelFormate(num1));
        }*/
        txtvAcntno.setText(""+ksebRechargeStatus.getAccNo());
        txtvbranch.setText(""+ksebRechargeStatus.getBranch());
        txtvAcntnoto.setText("Consumer Section : "+ksebRechargeStatus.getSectionname());
        txtvmob.setText(ksebRechargeStatus.getMobile());
        txtvbranchto.setText("Consumer No : "+ksebRechargeStatus.getConsumerno());
        txtvbill.setText("Bill No : "+ksebRechargeStatus.getBillno());

     //   txtvAcntnoto.setText("A/C : "+mPaymentModel.getBeneficiaryAccNo());
       // txtvbranchto.setText("Branch :"+BranchName);



      //  String values = mPaymentModel.getAccNo()+"\n"+mPaymentModel.getBeneficiaryAccNo()+ "\n"+mPaymentModel.getAmount();
      //  Toast.makeText(getContext(),values,Toast.LENGTH_LONG).show();
        try{
            Bundle bundle = getArguments();
            boolean isHappy = bundle.getBoolean( HAPPY );
            String title = bundle.getString( TITLE );
            String message = bundle.getString( MESSAGE );
            txtMessage.setText( message );
            txtTitle.setText( title );
            if ( !isHappy ){
                imgIcon.setImageResource( R.mipmap.ic_failed );
            }
         /*   ArrayList<KeyValuePair> keyValuePairs = bundle.getParcelableArrayList( KEY_VALUE );
            SuccessAdapter successAdapter = new SuccessAdapter( keyValuePairs );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
            mRecyclerView.setLayoutManager( layoutManager );
            mRecyclerView.setAdapter( successAdapter );*/

            lay_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bitmap bitmap = Bitmap.createBitmap(rltv_share.getWidth(),
                            rltv_share.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    rltv_share.draw(canvas);

                    try {


                        Uri bmpUri = getLocalBitmapUri(bitmap);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Exception","Exception   117   "+e.toString());
                    }

                }
            });




        }catch ( Exception e){
            //Do nothing
        }



        return view;
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
      //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        Log.e("File  ","File   142   "+file);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
 /*   public void onBackPressed() {

        try{
            getActivity().finish();
        }catch ( NullPointerException e ){
            //Do nothing
        }
    }*/

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* Enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            // Handle the back button event

            Log.e(TAG,"HLLOOOO     910");

            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }


    };



}
