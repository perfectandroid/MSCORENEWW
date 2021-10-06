package com.creativethoughts.iscore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    RatingBar ratingBar;
    RadioGroup rg;
    RadioButton radio0, radio1, radio2;
    String rating, StrFeedback;
    EditText etMSG;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        intViews();
        setRegViews();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio0:
                        StrFeedback=radio0.getText().toString();
                        break;
                    case R.id.radio1:
                        StrFeedback=radio1.getText().toString();
                        break;
                    case R.id.radio2:
                        StrFeedback=radio2.getText().toString();
                        break;
                }
            }
        });
    }

    private void intViews() {
        ratingBar=findViewById(R.id.ratingBar);
        rg =findViewById(R.id.radioGroup1);
        radio0=findViewById(R.id.radio0);
        radio1=findViewById(R.id.radio1);
        radio2=findViewById(R.id.radio2);
        etMSG=findViewById(R.id.etMSG);
        btnOk=findViewById(R.id.btnOk);
        rg.clearCheck();
    }

    private void setRegViews() {
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                rating=String.valueOf(ratingBar.getRating());
                sendEmail(rating,StrFeedback,etMSG.getText().toString());
                break;
        }
    }

    private void sendEmail(String strrating, String strfeedback, String msg) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "pssappfeedback@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MSCORE: Feedback & Rating");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Bank : "+getResources().getString(R.string.app_name)+"\n\n Customer Rating is : "+strrating+"\nFEEDBACK: ("+strfeedback+") \n\t"+msg);
        startActivity(Intent.createChooser(emailIntent, "Select One"));
    }

}
