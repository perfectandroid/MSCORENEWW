package com.creativethoughts.iscore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.creativethoughts.iscore.utility.CommonUtilities;
import com.creativethoughts.iscore.utility.NumberToWord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StatusDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    String Date,AccountNo,UTRNO,Name,Beneficiary,Amount,Status,Remark,BeneficiaryNumber,Branch,BankRefNo,BeneficiaryBank,BeneficiaryBankBranch;
    ImageView img_share;
    LinearLayout lnr_lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        lnr_lay = (LinearLayout)findViewById(R.id.lnr_lay);
        img_share = (ImageView)findViewById(R.id.img_share);
        img_share.setOnClickListener(this);

        Date = getIntent().getStringExtra("Date");
        AccountNo = getIntent().getStringExtra("AccountNo");
        UTRNO = getIntent().getStringExtra("UTRNO");
        Name = getIntent().getStringExtra("Name");
        Beneficiary = getIntent().getStringExtra("Beneficiary");
        Amount = getIntent().getStringExtra("Amount");
        Status = getIntent().getStringExtra("Status");
        Remark = getIntent().getStringExtra("Remark");
        BeneficiaryNumber = getIntent().getStringExtra("BeneficiaryNumber");
        Branch = getIntent().getStringExtra("Branch");
        BankRefNo = getIntent().getStringExtra("BankRefNo");
        BeneficiaryBank = getIntent().getStringExtra("BeneficiaryBank");
        BeneficiaryBankBranch = getIntent().getStringExtra("BeneficiaryBankBranch");

        TextView tvdate = findViewById(R.id.tvdate);
        TextView tvAccountNo = findViewById(R.id.tvAccountNo);
        TextView tvUTRNO = findViewById(R.id.tvUTRNO);
        TextView tvBeneficiary = findViewById(R.id.tvBeneficiary);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView narration_desc = findViewById(R.id.narration_desc);
        TextView tvBeneficiaryacc = findViewById(R.id.tvBeneficiaryacc);
        TextView tvBranch = findViewById(R.id.tvBranch);
        TextView tvBankRefNo = findViewById(R.id.tvBankRefNo);
        TextView tvBeneficiaryBank= findViewById(R.id.tvBeneficiaryBank);
        TextView tvBeneficiaryBankBranch = findViewById(R.id.tvBeneficiaryBankBranch);
        TextView tv_Amount_words = findViewById(R.id.tv_Amount_words);
        tvdate.setText(Date);
        tvAccountNo.setText(AccountNo);
        tvUTRNO.setText(UTRNO);
        tvBeneficiary.setText(Beneficiary);
        double num =Double.parseDouble(""+Amount);
        tvAmount.setText("\u20B9 "+CommonUtilities.getDecimelFormate(num));
//        tvAmount.setText(Amount);
        tvStatus.setText(Status);
        narration_desc.setText(Remark);
        tvBeneficiaryacc.setText(BeneficiaryNumber);
        tvBranch.setText(Branch);
        tvBankRefNo.setText(BankRefNo);
        tvBeneficiaryBank.setText(BeneficiaryBank);
        tvBeneficiaryBankBranch.setText(BeneficiaryBankBranch);

        String[] netAmountArr = Amount.split("\\.");
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
        tv_Amount_words.setText(amountInWordPop);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.img_share:
                Log.e("onClick","onClick   61");
                Bitmap bitmap = Bitmap.createBitmap(lnr_lay.getWidth(),
                        lnr_lay.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                lnr_lay.draw(canvas);

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
                break;
        }
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        //  final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
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
}