package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.creativethoughts.iscore.LoanAccountSummaryDetailsActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;

public class LoanListInfoAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String loantype, accno, branch,amt,status,submodule,account, loantypemode,IsDue;
    LinearLayout l2;

    public LoanListInfoAdapter(Context context, JSONArray jsonArray, String loantypemode) {
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
                ((MainViewHolder) holder).txtv_loantype.setText(loantype);
                ((MainViewHolder) holder).txtv_accno.setText(accno);
                ((MainViewHolder) holder).txtv_branchname.setText(branch);
                double num =Double.parseDouble(amt);
                DecimalFormat fmt = new DecimalFormat("#,##,###.00");
//                ((MainViewHolder) holder).txtv_amt.setText("\u20B9 "+fmt.format(num));
                ((MainViewHolder) holder).txtv_amt.setText("\u20B9 "+ CommonUtilities.getDecimelFormate(num));
                ((MainViewHolder) holder).txtv_status.setText(status);

                if (submodule.equals("TLML")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tlml), null, null, null);
                }
                else if (submodule.equals("TLSL")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tlsl), null, null, null);
                }
                else if (submodule.equals("TLOD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tlod), null, null, null);
                }
                else if (submodule.equals("TLSD")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_tlsd), null, null, null);
                }
                else if (submodule.equals("SLJL")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_sljl), null, null, null);
                }
                else if (submodule.equals("SLDL")){
                    (((MainViewHolder) holder).txtv_loantype).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_sldl), null, null, null);
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
                            Intent i = new Intent(context, LoanAccountSummaryDetailsActivity.class);
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
                            i.putExtra("BranchCode", jsonObject.getString("BranchCode"));
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

