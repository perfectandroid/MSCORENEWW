package com.creativethoughts.iscore.neftrtgs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.creativethoughts.iscore.HomeActivity;
import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.OtherfundTransferHistory;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.db.dao.DynamicMenuDao;
import com.creativethoughts.iscore.db.dao.model.DynamicMenuDetails;
import com.creativethoughts.iscore.money_transfer.QuickPayMoneyTransferFragment;
import com.creativethoughts.iscore.money_transfer.OtherbankFundTransferActivity;
import com.creativethoughts.iscore.money_transfer.OtherfundTransferType;


public class OtherBankFundTransferServiceChooserFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mMode = "NO_MODE";

    private ImageView imghist,imghist1,imghist2;
    private Button mBtnImps;
    private Button mBtnNeft;
    private Button mBtnRtgs;
    private Button mBtnQckPay;
    private Button mBtnProceed;
//    private TextView btn_hist;
    private boolean isVisible = false;

    RelativeLayout rltv_quickpay;
    RelativeLayout rltv_rtgs;
    RelativeLayout rltv_neft;
    RelativeLayout rltv_imps;
    RelativeLayout rltv_transaction_history;


    public OtherBankFundTransferServiceChooserFragment() {
        // Required empty public constructor
    }


    public static OtherBankFundTransferServiceChooserFragment newInstance(String param1, String param2) {
        OtherBankFundTransferServiceChooserFragment fragment = new OtherBankFundTransferServiceChooserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_other_bank_fund_transfer_service_chooser, container, false);

        mBtnImps    = view.findViewById( R.id.btn_imps );
        mBtnNeft    = view.findViewById( R.id.btn_neft );
        mBtnRtgs    = view.findViewById( R.id.btn_rtgs );
        mBtnQckPay  = view.findViewById( R.id.btn_qck_pay );
        mBtnProceed = view.findViewById( R.id.btn_proceed );
//        btn_hist= view.findViewById( R.id.btn_hist );

        imghist= view.findViewById( R.id.imghist );
        imghist1= view.findViewById( R.id.imghist1 );
        imghist2= view.findViewById( R.id.imghist2 );


        imghist.setOnClickListener(this);
        imghist1.setOnClickListener(this);
        imghist2.setOnClickListener(this);


        rltv_imps    = view.findViewById( R.id.rltv_imps );
        rltv_transaction_history    = view.findViewById( R.id.rltv_transaction_history );
        rltv_neft    = view.findViewById( R.id.rltv_neft );
        rltv_rtgs    = view.findViewById( R.id.rltv_rtgs );
        rltv_quickpay    = view.findViewById( R.id.rltv_quickpay );

        mBtnImps.setVisibility( View.GONE );
        mBtnNeft.setVisibility( View.GONE );
        mBtnRtgs.setVisibility( View.GONE );
        mBtnQckPay.setVisibility( View.GONE );

        rltv_imps.setVisibility( View.GONE );
        rltv_neft.setVisibility( View.GONE );
        rltv_rtgs.setVisibility( View.GONE );
        rltv_quickpay.setVisibility( View.GONE );

        RelativeLayout rltvProceed = view.findViewById( R.id.rltv_proceed );
        DynamicMenuDetails dynamicMenuDetails = DynamicMenuDao.getInstance().getMenuDetails();
        try{
            String tempRtgsImpsNeft = IScoreApplication.decryptStart(dynamicMenuDetails.getRtgs() );
            String[] dmenu = tempRtgsImpsNeft.split("(?!^)");
            if ( dmenu.length == 3 ){
                if ( dmenu[0].equals("1") ){
                    mBtnNeft.setVisibility( View.VISIBLE );
                    rltv_neft.setVisibility( View.VISIBLE );
                }
                if ( dmenu[1].equals("1") ){
                    mBtnRtgs.setVisibility( View.VISIBLE );
                    rltv_rtgs.setVisibility( View.VISIBLE );
                }
                if ( dmenu[2].equals("1") ){
                    mBtnImps.setVisibility( View.VISIBLE );
                    rltv_imps.setVisibility( View.VISIBLE );
                }
            }
            String quickPay = IScoreApplication.decryptStart( dynamicMenuDetails.getImps() );
            if ( quickPay.equals("1") ){
                mBtnQckPay.setVisibility( View.VISIBLE);
                rltv_quickpay.setVisibility( View.VISIBLE );
            }


            mBtnImps.setOnClickListener( this );
            mBtnNeft.setOnClickListener( this );
            mBtnRtgs.setOnClickListener( this );
            mBtnQckPay.setOnClickListener( this );
            mBtnRtgs.setOnClickListener( this );
            mBtnProceed.setOnClickListener( this );
//            btn_hist.setOnClickListener( this );

            rltv_transaction_history.setOnClickListener( this );
            rltv_imps.setOnClickListener( this );
            rltv_neft.setOnClickListener( this );
            rltv_rtgs.setOnClickListener( this );
            rltv_quickpay.setOnClickListener( this );

        }catch ( Exception e ){
            //Do nothing
        }

        if ( !isVisible )
            mBtnProceed.setVisibility( View.GONE );
        if ( Build.VERSION.SDK_INT > 18 ){
            TransitionManager.beginDelayedTransition( rltvProceed );
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch ( id ){
            case R.id.btn_imps:
                buttonColorChange( mBtnImps, new Button[]{mBtnQckPay, mBtnRtgs, mBtnNeft } );
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_IMPS;
                isVisible = true;
                break;
            case R.id.btn_neft:
                buttonColorChange( mBtnNeft, new Button[]{mBtnQckPay, mBtnRtgs, mBtnImps } );
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_NEFT;
                isVisible = true;
                break;
            case R.id.btn_rtgs:
                buttonColorChange( mBtnRtgs, new Button[]{mBtnQckPay, mBtnImps, mBtnNeft } );
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS;
                isVisible = true;
                break;
            case R.id.btn_qck_pay:
                buttonColorChange( mBtnQckPay, new Button[]{mBtnRtgs, mBtnImps, mBtnNeft } );
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_QKPY;
                isVisible = true;
                break;
            case R.id.btn_proceed:
                proceed();
                break;

            case R.id.rltv_imps:
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_IMPS;
                Intent ii = new Intent(getActivity(), OtherbankFundTransferActivity.class);
                ii.putExtra("mMode",mMode);
                getActivity().startActivity(ii);
                isVisible = true;
                break;

            case R.id.rltv_transaction_history:
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS;
                Intent inn = new Intent(getActivity(), OtherfundTransferHistory.class);
                inn.putExtra("mMode",mMode);
                getActivity().startActivity(inn);
                isVisible = true;
                break;

            case R.id.rltv_neft:
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_NEFT;
                Intent in = new Intent(getActivity(), OtherbankFundTransferActivity.class);
                in.putExtra("mMode",mMode);
                getActivity().startActivity(in);
                isVisible = true;
                break;

            case R.id.rltv_rtgs:
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS;
                Intent ir = new Intent(getActivity(), OtherbankFundTransferActivity.class);
                ir.putExtra("mMode",mMode);
                getActivity().startActivity(ir);

                isVisible = true;
                break;
//            case R.id.btn_hist:
//                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_RTGS;
//                Intent inn = new Intent(getActivity(), OtherfundTransferHistory.class);
//                inn.putExtra("mMode",mMode);
//                getActivity().startActivity(inn);
//
//                isVisible = true;
//                break;

            case R.id.rltv_quickpay:
                mMode = IScoreApplication.OTHER_FUND_TRANSFER_MODE_QKPY;
                Intent iq = new Intent(getActivity(), OtherbankFundTransferActivity.class);
                iq.putExtra("mMode",mMode);
                getActivity().startActivity(iq);
                isVisible = true;
                break;
            case R.id.imghist:
                Intent im = new Intent(getActivity(), OtherfundTransferType.class);
                im.putExtra("submode","1");
                getActivity().startActivity(im);
                break;
            case R.id.imghist1:
                Intent imm = new Intent(getActivity(), OtherfundTransferType.class);
                imm.putExtra("submode","2");
                getActivity().startActivity(imm);
                break;

            case R.id.imghist2:
                Intent immm = new Intent(getActivity(), OtherfundTransferType.class);
                immm.putExtra("submode","3");
                getActivity().startActivity(immm);
                break;


            default:break;
        }
        if ( isVisible ){
           // mBtnProceed.setVisibility( View.VISIBLE );
        }
    }
    private void proceed(){

        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (mMode) {
            case "NO_MODE":
                Toast.makeText(getContext(), "Please select mode", Toast.LENGTH_SHORT).show();
                return;
            case IScoreApplication.OTHER_FUND_TRANSFER_MODE_QKPY:
                assert getFragmentManager() != null;
                fragmentTransaction.add(R.id.container, QuickPayMoneyTransferFragment.newInstance());
                fragmentTransaction.addToBackStack("sample");
                fragmentTransaction.commit();
                break;

            default:
                assert getFragmentManager() != null;
                fragmentTransaction.add(R.id.container, ListSavedBeneficiaryFragment.newInstance(mMode));
                fragmentTransaction.addToBackStack("sample");
                fragmentTransaction.commit();
                break;

        }
    }
    private void buttonColorChange(Button buttonSelected, Button[] btnArray){
        buttonSelected.setBackgroundResource( R.drawable.service_chooser_button_after );
        buttonSelected.setTextColor(getResources().getColor( R.color.white ) );
        for (Button btn: btnArray   ) {
            btn.setBackgroundResource( R.drawable.service_chooser_button_before );
            btn.setTextColor(getResources().getColor( R.color.google_blue  ) );
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    //  Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), HomeActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }
}
