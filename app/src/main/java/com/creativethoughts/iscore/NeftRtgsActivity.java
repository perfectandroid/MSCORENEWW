package com.creativethoughts.iscore;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.creativethoughts.iscore.neftrtgs.PaymentModel;

public class NeftRtgsActivity extends Activity  implements View.OnClickListener{
    private EditText mEdtTxtBeneficiaryName;
    private EditText  mEdtTxtBeneficiaryAccNo;
    private EditText  mEdtTxtBeneficiaryConfirmAccNo;
    private EditText mEdtTxtIfscNo;
    private TextView txtTrans,txt_header,txt_amtinword;
    private EditText  mEdtTxtAmount ;
    private Spinner mSpinnerAccountNo;
    private Button mBtnClear;
    private CheckBox mCheckSaveBeneficiary;
    private RelativeLayout mLinearParent;
    private ScrollView mScrollView;
    public static final String result ="";
    public static String balnce ="";
    String from="NEFT";
    String reslts;
    private int mModeNeftRtgs;
    private PaymentModel mPaymentModel;
    private static final String BENEFICIARY_DETAILS = "beneficiary details";
    private static final String MODE = "MODE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_neft_rtgs);
    }

    @Override
    public void onClick(View view) {

    }
}
