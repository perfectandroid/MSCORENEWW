package com.creativethoughts.iscore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.creativethoughts.iscore.Helper.Config;
import com.creativethoughts.iscore.Helper.PicassoTrustAll;


public class MoreFragment extends Fragment implements View.OnClickListener{


    TextView tv_version, tv_abt_us, tv_contact_us, tv_features, tv_rate_us, tv_feed_back, tv_faq;
    ImageView imCompanylogo,img_applogo;
    public MoreFragment() {
    }

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_more, container, false);
        intViews(rootView);
        setRegViews();
        tv_version.setText("Version : "+ IScoreApplication.versionName);
        SharedPreferences imageurlSP = getActivity().getSharedPreferences(Config.SHARED_PREF13, 0);
        String IMAGEURL = imageurlSP.getString("imageurl","");

        SharedPreferences AppIconImageCodeSP = getActivity().getSharedPreferences(Config.SHARED_PREF3, 0);
        String AppIconImageCodePath =IMAGEURL+AppIconImageCodeSP.getString("AppIconImageCode","");
        PicassoTrustAll.getInstance(getActivity()).load(AppIconImageCodePath).error(R.drawable.errorlogo).into(img_applogo);

        SharedPreferences CompanyLogoImageCodeSP = getActivity().getSharedPreferences(Config.SHARED_PREF4, 0);
        String companylogoPath =IMAGEURL+CompanyLogoImageCodeSP.getString("CompanyLogoImageCode","");
        PicassoTrustAll.getInstance(getActivity()).load(companylogoPath).error(R.drawable.errorlogo).into(imCompanylogo);
        return rootView;
    }

    private void intViews(View v) {
        tv_version=v.findViewById(R.id.tv_version);
        tv_abt_us=v.findViewById(R.id.tv_abt_us);
        tv_contact_us=v.findViewById(R.id.tv_contact_us);
        tv_rate_us=v.findViewById(R.id.tv_rate_us);
        tv_feed_back=v.findViewById(R.id.tv_feed_back);
        tv_faq=v.findViewById(R.id.tv_faq);
        tv_features=v.findViewById(R.id.tv_feat);
        imCompanylogo = v.findViewById(R.id.imCompanylogo);
        img_applogo = v.findViewById(R.id.img_applogo);
    }

    private void setRegViews() {
        tv_abt_us.setOnClickListener(this);
        tv_contact_us.setOnClickListener(this);
        tv_rate_us.setOnClickListener(this);
        tv_feed_back.setOnClickListener(this);
        tv_faq.setOnClickListener(this);
        tv_features.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.tv_abt_us:
                intent=new Intent(getContext(), AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_contact_us:
                intent=new Intent(getContext(), ContactusActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_feat:
                intent=new Intent(getContext(), FeaturesActvity.class);
                startActivity(intent);
                break;
            case R.id.tv_rate_us:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Do you love this app? Please rate us.");
                alertDialogBuilder.setPositiveButton("Rate Now",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                /*        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(goToMarket);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                        }*/
                                final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.tv_feed_back:
                intent=new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_faq:
                intent=new Intent(getContext(), FAQActivity.class);
                startActivity(intent);
                break;
        }
    }
}
