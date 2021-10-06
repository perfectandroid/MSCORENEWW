package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.DepositAccountSummaryDetailsActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class DepositListInfoAdapter extends RecyclerView.Adapter {
    String TAG = "DepositListInfoAdapter";
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String loantype, accno, branch,amt,status,submodule,account, loantypemode,IsDue;
    LinearLayout l2;

    public DepositListInfoAdapter(Context context, JSONArray jsonArray, String loantypemode) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.loantypemode = loantypemode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_deposit, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                loantype = jsonObject.getString("LoanType");
                IsDue = jsonObject.getString("IsDue");
                accno = jsonObject.getString("AccountNumber");
                branch = jsonObject.getString("BranchName");
                amt = jsonObject.getString("Balance");
                status = jsonObject.getString("Status");
                submodule = jsonObject.getString("SubModule");
                account = jsonObject.getString("FK_Account");

                if (IsDue.equals("1")){
                    ((MainViewHolder) holder).lnLayout.setBackgroundColor(Color.parseColor("#FFEFD5"));
                }
                else{
                    ((MainViewHolder) holder).lnLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                //  LoanNumber = jsonObject.getString("LoanNumber");
                //    mobilenum = jsonObject.getString("CustomerMobileNumber");
                ((MainViewHolder) holder).txtv_loantype.setText(loantype);
//                ((MainViewHolder) holder).txtv_accno.setText("Acc Num : " + accno);
                ((MainViewHolder) holder).txtv_accno.setText("" + accno);
                ((MainViewHolder) holder).txtv_branchname.setText(branch);

                double num =Double.parseDouble(amt);
              //  DecimalFormat fmt = new DecimalFormat("#,##,###.00");
                ((MainViewHolder) holder).txtv_amt.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));
                ((MainViewHolder) holder).txtv_status.setText(status);
//                if(status.equals("Open"))
//                {
//                    ((MainViewHolder) holder).txtv_status.setVisibility(View.GONE);
//                    ((MainViewHolder) holder). txtv_status.setTextColor(ContextCompat.getColor(context, R.color.red_error_snack_bar));
//                }
//                else if(status.equals("Closed"))
//                {
//                    ((MainViewHolder) holder).txtv_status.setVisibility(View.VISIBLE);
//                    ((MainViewHolder) holder). txtv_status.setTextColor(ContextCompat.getColor(context, R.color.google_green1));
//                }
                //((MainViewHolder) holder).txtv_ph.setText(phone+"("+inchrge+")");

                Log.e(TAG,"submodule  83   "+submodule);
                if (submodule.equals("DDSB")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddsb), null, null, null);
                }
                else if (submodule.equals("DDCA")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddca), null, null, null);
                }
                else if (submodule.equals("DDTD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddtd), null, null, null);
                }
                else if (submodule.equals("TDFD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdfd), null, null, null);
                }
                else if (submodule.equals("TDCC")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdcc), null, null, null);
                }
                else if (submodule.equals("ODMD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_odmd), null, null, null);
                }
                else if (submodule.equals("TDCH")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdch), null, null, null);
                }
                else if (submodule.equals("TDSD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdsd), null, null, null);
                }
                else if (submodule.equals("TDCD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdcd), null, null, null);
                }
                else if (submodule.equals("TDED")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tded), null, null, null);
                }
                else if (submodule.equals("TDEM")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdem), null, null, null);
                }
                else if (submodule.equals("PDDD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pddd), null, null, null);
                }
                else if (submodule.equals("PDRD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdrd), null, null, null);
                }
                else if (submodule.equals("PDGD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdgd), null, null, null);
                }
                else if (submodule.equals("PDHD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdhd), null, null, null);
                }
                else if (submodule.equals("ODGD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_odgd), null, null, null);
                }
                else if (submodule.equals("SHAS")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shas), null, null, null);
                }
                else if (submodule.equals("SHNS")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shns), null, null, null);
                }
                else if (submodule.equals("SHSG")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shsg), null, null, null);
                }
                else {
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_others), null, null, null);
                }


                ((MainViewHolder) holder).lnLayout.setTag(position);
                ((MainViewHolder) holder).lnLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            submodule = jsonObject.getString("SubModule");
                            account= jsonObject.getString("FK_Account");
                            status = jsonObject.getString("Status");
                            String acno = jsonObject.getString("AccountNumber");
                            String amount = jsonObject.getString("Balance");
                            String type = jsonObject.getString("LoanType");
                            String fund =jsonObject.getString("FundTransferAccount");
                            String ifsc =jsonObject.getString("IFSCCode");
                            String IsShareAc =jsonObject.getString("IsShareAc");
                            String EnableDownloadStatement =jsonObject.getString("EnableDownloadStatement");
                            Intent i = new Intent(context, DepositAccountSummaryDetailsActivity.class);
                            i.putExtra("Submodule", submodule);
                            i.putExtra("FK_Account", account);
                            i.putExtra("Accno", acno);
                            i.putExtra("Amt", amount);
                            i.putExtra("Status", status);
                            i.putExtra("type", type);
                            i.putExtra("Type", "Depo");
                            i.putExtra("Fund", fund);
                            i.putExtra("Ifsc", ifsc);
                            i.putExtra("loantypemode", loantypemode);
                            i.putExtra("IsShareAc", IsShareAc);
                            i.putExtra("EnableDownloadStatement", EnableDownloadStatement);

                            context.startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView txtv_loantype,txtv_accno,txtv_branchname, txtv_amt,txtv_status;
        ImageView imgv_details;
        LinearLayout lnLayout;

        public MainViewHolder(View v) {
            super(v);
            //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
            txtv_loantype = v.findViewById(R.id.txtv_loantype);
            txtv_accno = v.findViewById(R.id.txtv_accno);
            txtv_branchname = v.findViewById(R.id.txtv_branchname);
            txtv_amt = v.findViewById(R.id.txtv_amt);
            txtv_status = v.findViewById(R.id.txtv_status);
            imgv_details = v.findViewById(R.id.imgv_details);
            lnLayout = v.findViewById(R.id.l2);
        }
    }
}




//package com.creativethoughts.iscore.adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.creativethoughts.iscore.DepositAccountSummaryDetailsActivity;
//import com.creativethoughts.iscore.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.NumberFormat;
//import java.util.Locale;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//public class DepositListInfoAdapter extends RecyclerView.Adapter {
//    String TAG = "DepositListInfoAdapter";
//    JSONArray jsonArray;
//    JSONObject jsonObject = null;
//    Context context;
//    String loantype, accno, branch,amt,status,submodule,account;
//    LinearLayout l2;
//
//    public DepositListInfoAdapter(Context context, JSONArray jsonArray) {
//        this.context = context;
//        this.jsonArray = jsonArray;
//        // this.strdate = strdate;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        final RecyclerView.ViewHolder vh;
//        View v = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.recyclerview_deposit, parent, false);
//        vh = new MainViewHolder(v);
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        try {
//            jsonObject = jsonArray.getJSONObject(position);
//            if (holder instanceof MainViewHolder) {
//                loantype = jsonObject.getString("LoanType");
//                accno = jsonObject.getString("AccountNumber");
//                branch = jsonObject.getString("BranchName");
//                amt = jsonObject.getString("Balance");
//                status = jsonObject.getString("Status");
//                submodule = jsonObject.getString("SubModule");
//                account = jsonObject.getString("FK_Account");
//
//
//
//                //  LoanNumber = jsonObject.getString("LoanNumber");
//                //    mobilenum = jsonObject.getString("CustomerMobileNumber");
//                ((MainViewHolder) holder).txtv_loantype.setText(loantype);
////                ((MainViewHolder) holder).txtv_accno.setText("Acc Num : " + accno);
//                ((MainViewHolder) holder).txtv_accno.setText("" + accno);
//                ((MainViewHolder) holder).txtv_branchname.setText(branch);
//                double num =Double.parseDouble(amt);
//                ((MainViewHolder) holder).txtv_amt.setText("\u20B9 "+ NumberFormat.getNumberInstance(Locale.US).format(num));
//                ((MainViewHolder) holder).txtv_status.setText(status);
////                if(status.equals("Open"))
////                {
////                    ((MainViewHolder) holder).txtv_status.setVisibility(View.GONE);
////                    ((MainViewHolder) holder). txtv_status.setTextColor(ContextCompat.getColor(context, R.color.red_error_snack_bar));
////                }
////                else if(status.equals("Closed"))
////                {
////                    ((MainViewHolder) holder).txtv_status.setVisibility(View.VISIBLE);
////                    ((MainViewHolder) holder). txtv_status.setTextColor(ContextCompat.getColor(context, R.color.google_green1));
////                }
//                //((MainViewHolder) holder).txtv_ph.setText(phone+"("+inchrge+")");
//
//                Log.e(TAG,"submodule  83   "+submodule);
//                if (submodule.equals("DDSB")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddsb), null, null, null);
//                }
//                else if (submodule.equals("DDCA")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddca), null, null, null);
//                }
//                else if (submodule.equals("DDTD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_ddtd), null, null, null);
//                }
//                else if (submodule.equals("TDFD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdfd), null, null, null);
//                }
//                else if (submodule.equals("TDCC")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdcc), null, null, null);
//                }
//                else if (submodule.equals("ODMD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_odmd), null, null, null);
//                }
//                else if (submodule.equals("TDCH")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdch), null, null, null);
//                }
//                else if (submodule.equals("TDSD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdsd), null, null, null);
//                }
//                else if (submodule.equals("TDCD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdcd), null, null, null);
//                }
//                else if (submodule.equals("TDED")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tded), null, null, null);
//                }
//                else if (submodule.equals("TDEM")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tdem), null, null, null);
//                }
//                else if (submodule.equals("PDDD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pddd), null, null, null);
//                }
//                else if (submodule.equals("PDRD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdrd), null, null, null);
//                }
//                else if (submodule.equals("PDGD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdgd), null, null, null);
//                }
//                else if (submodule.equals("PDHD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_pdhd), null, null, null);
//                }
//                else if (submodule.equals("ODGD")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_odgd), null, null, null);
//                }
//                else if (submodule.equals("SHAS")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shas), null, null, null);
//                }
//                else if (submodule.equals("SHNS")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shns), null, null, null);
//                }
//                else if (submodule.equals("SHSG")){
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_shsg), null, null, null);
//                }
//                else {
//                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_others), null, null, null);
//                }
//
//
//                ((MainViewHolder) holder).lnLayout.setTag(position);
//                ((MainViewHolder) holder).lnLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            jsonObject = jsonArray.getJSONObject(position);
//                            submodule = jsonObject.getString("SubModule");
//                            account= jsonObject.getString("FK_Account");
//                            status = jsonObject.getString("Status");
//                            String acno = jsonObject.getString("AccountNumber");
//                            String amount = jsonObject.getString("Balance");
//                            String type = jsonObject.getString("LoanType");
//                            String fund =jsonObject.getString("FundTransferAccount");
//                            String ifsc =jsonObject.getString("IFSCCode");
//                            Intent i = new Intent(context, DepositAccountSummaryDetailsActivity.class);
//                            i.putExtra("Submodule", submodule);
//                            i.putExtra("FK_Account", account);
//                            i.putExtra("Accno", acno);
//                            i.putExtra("Amt", amount);
//                            i.putExtra("Status", status);
//                            i.putExtra("type", type);
//                            i.putExtra("Type", "Depo");
//                            i.putExtra("Fund", fund);
//                            i.putExtra("Ifsc", ifsc);
//                            context.startActivity(i);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        //   Toast.makeText(context,"Test",Toast.LENGTH_LONG).show();
//
//                          /*  jsonObject = jsonArray.getJSONObject(position);
//                            Intent i = new Intent(context, SingleBranchActivity.class);
//                            i.putExtra("ID_Br", jsonObject.getString("ID_Branch"));
//                            i.putExtra("Lat", jsonObject.getString("LocationLatitude"));
//                            i.putExtra("Long", jsonObject.getString("LocationLongitude"));
//                            i.putExtra("BNK", jsonObject.getString("BankName"));
//                            context.startActivity(i);*/
//
//                    }
//                });
//
////
////                ((MainViewHolder) holder).imgv_details.setTag(position);
////                ((MainViewHolder) holder).imgv_details.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        try {
////                            jsonObject = jsonArray.getJSONObject(position);
////                            submodule = jsonObject.getString("SubModule");
////                            account= jsonObject.getString("FK_Account");
////                            status = jsonObject.getString("Status");
////                            String acno = jsonObject.getString("AccountNumber");
////                            String amount = jsonObject.getString("Balance");
////                            Intent i = new Intent(context, AccountSummaryDetailsActivity.class);
////                            i.putExtra("Submodule", submodule);
////                            i.putExtra("FK_Account", account);
////                            i.putExtra("Accno", acno);
////                            i.putExtra("Amt", amount);
////                            i.putExtra("Status", status);
////                            context.startActivity(i);
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                        //   Toast.makeText(context,"Test",Toast.LENGTH_LONG).show();
////
////                          /*  jsonObject = jsonArray.getJSONObject(position);
////                            Intent i = new Intent(context, SingleBranchActivity.class);
////                            i.putExtra("ID_Br", jsonObject.getString("ID_Branch"));
////                            i.putExtra("Lat", jsonObject.getString("LocationLatitude"));
////                            i.putExtra("Long", jsonObject.getString("LocationLongitude"));
////                            i.putExtra("BNK", jsonObject.getString("BankName"));
////                            context.startActivity(i);*/
////
////                    }
////                });
//
//
//
//
//
//                //  ((MainViewHolder) holder).imnext.setTag(position);
//               /* ((MainViewHolder) holder).imnext.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            jsonObject = jsonArray.getJSONObject(position);
//                            Intent i = new Intent(context, SingleLoanActivity.class);
//                            i.putExtra("ID_BcLoanAccount", jsonObject.getString("ID_BcLoanAccount"));
//                            i.putExtra("CustomerMobileNumber", jsonObject.getString("CustomerMobileNumber"));
//                            i.putExtra("strdate", strdate);
//                            context.startActivity(i);
//                            ((Activity) context).finish();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });*/
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return jsonArray.length();
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position % 2;
//    }
//
//    private class MainViewHolder extends RecyclerView.ViewHolder {
//        public TextView txtv_loantype,txtv_accno,txtv_branchname, txtv_amt,txtv_status;
//        ImageView imgv_details;
//        LinearLayout lnLayout;
//
//        public MainViewHolder(View v) {
//            super(v);
//            //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
//            txtv_loantype = v.findViewById(R.id.txtv_loantype);
//            txtv_accno = v.findViewById(R.id.txtv_accno);
//            txtv_branchname = v.findViewById(R.id.txtv_branchname);
//            txtv_amt = v.findViewById(R.id.txtv_amt);
//            txtv_status = v.findViewById(R.id.txtv_status);
//            imgv_details = v.findViewById(R.id.imgv_details);
//            lnLayout = v.findViewById(R.id.l2);
//        }
//    }
//}


