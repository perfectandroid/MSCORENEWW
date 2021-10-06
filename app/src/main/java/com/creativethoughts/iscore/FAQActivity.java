package com.creativethoughts.iscore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FAQActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout FAQ1,FAQ2,FAQ3,FAQ4,FAQ5,FAQ6,FAQ7;
    TextView faq1,faq2,faq3,faq4,faq5,faq6,faq7;
    ImageView img1,img2,img3,img4,img5,img6,img7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        intViews();
        setRegViews();
    }

    private void intViews() {
        FAQ1=findViewById(R.id.FAQ1);
        FAQ2=findViewById(R.id.FAQ2);
        FAQ3=findViewById(R.id.FAQ3);
        FAQ4=findViewById(R.id.FAQ4);
        FAQ5=findViewById(R.id.FAQ5);
        FAQ6=findViewById(R.id.FAQ6);
        FAQ7=findViewById(R.id.FAQ7);
        faq1=findViewById(R.id.faq1);
        faq2=findViewById(R.id.faq2);
        faq3=findViewById(R.id.faq3);
        faq4=findViewById(R.id.faq4);
        faq5=findViewById(R.id.faq5);
        faq6=findViewById(R.id.faq6);
        faq7=findViewById(R.id.faq7);
        img1=findViewById(R.id.img1);
        img2=findViewById(R.id.img2);
        img3=findViewById(R.id.img3);
        img4=findViewById(R.id.img4);
        img5=findViewById(R.id.img5);
        img6=findViewById(R.id.img6);
        img7=findViewById(R.id.img7);
    }

    private void setRegViews() {
        FAQ1.setOnClickListener(this);
        FAQ2.setOnClickListener(this);
        FAQ3.setOnClickListener(this);
        FAQ4.setOnClickListener(this);
        FAQ5.setOnClickListener(this);
        FAQ6.setOnClickListener(this);
        FAQ7.setOnClickListener(this);


        img1.setImageResource(R.drawable.ic_arrow_right_white);
        img2.setImageResource(R.drawable.ic_arrow_right_white);
        img3.setImageResource(R.drawable.ic_arrow_right_white);
        img4.setImageResource(R.drawable.ic_arrow_right_white);
        img5.setImageResource(R.drawable.ic_arrow_right_white);
        img6.setImageResource(R.drawable.ic_arrow_right_white);
        img7.setImageResource(R.drawable.ic_arrow_right_white);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.FAQ1:
                faq1.setVisibility(View.VISIBLE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.GONE);


                img1.setImageResource(R.drawable.ic_arrow_down_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ2:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.VISIBLE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_down_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ3:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.VISIBLE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.GONE);



                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_down_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ4:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.VISIBLE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.GONE);


                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_down_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ5:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.VISIBLE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_down_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ6:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.VISIBLE);
                faq7.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_down_white);
                img7.setImageResource(R.drawable.ic_arrow_right_white);
                break;
            case R.id.FAQ7:
                faq1.setVisibility(View.GONE);
                faq2.setVisibility(View.GONE);
                faq3.setVisibility(View.GONE);
                faq4.setVisibility(View.GONE);
                faq5.setVisibility(View.GONE);
                faq6.setVisibility(View.GONE);
                faq7.setVisibility(View.VISIBLE);

                img1.setImageResource(R.drawable.ic_arrow_right_white);
                img2.setImageResource(R.drawable.ic_arrow_right_white);
                img3.setImageResource(R.drawable.ic_arrow_right_white);
                img4.setImageResource(R.drawable.ic_arrow_right_white);
                img5.setImageResource(R.drawable.ic_arrow_right_white);
                img6.setImageResource(R.drawable.ic_arrow_right_white);
                img7.setImageResource(R.drawable.ic_arrow_down_white);
                break;
        }
    }
}
