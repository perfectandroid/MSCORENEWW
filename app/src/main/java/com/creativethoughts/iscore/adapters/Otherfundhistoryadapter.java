package com.creativethoughts.iscore.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativethoughts.iscore.DepositAccountSummaryDetailsActivity;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.StatusDetailsActivity;
import com.creativethoughts.iscore.adapters.DepositListInfoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Otherfundhistoryadapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String date, name, benefcry,status,remark;
    LinearLayout l2;

    public Otherfundhistoryadapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        // this.strdate = strdate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_history, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                date = jsonObject.getString("Date");
                name = jsonObject.getString("Name");
                benefcry = jsonObject.getString("Beneficiary");
                status = jsonObject.getString("Status");
                remark = jsonObject.getString("Remark");





                //  LoanNumber = jsonObject.getString("LoanNumber");
                //    mobilenum = jsonObject.getString("CustomerMobileNumber");
                ((MainViewHolder) holder).txtv_date.setText(""+date);
//                ((MainViewHolder) holder).txtv_name.setText("Name : "+name);
                ((MainViewHolder) holder).txtv_beneficiary.setText(""+benefcry);
                if(status.equals("WAITING"))
                {
                    ((MainViewHolder) holder).iv_status.setImageResource(R.drawable.ic_waiting_b);
                    ((MainViewHolder) holder).txtv_status.setTextColor(Color.parseColor("#195BA7"));

                }
                else if(status.equals("SUCCESS"))
                {
                    ((MainViewHolder) holder).txtv_status.setTextColor(Color.parseColor("#2F875F"));
                    ((MainViewHolder) holder).iv_status.setImageResource(R.drawable.ic_sucess_g);

                }
                else if(status.equals("RETURNED"))
                {
                    ((MainViewHolder) holder).txtv_status.setTextColor(Color.parseColor("#FFB92C"));
                    ((MainViewHolder) holder).iv_status.setImageResource(R.drawable.ic_returned_y);
                }
                else if(status.equals("FAILED"))
                {
                    ((MainViewHolder) holder).txtv_status.setTextColor(Color.parseColor("#D30905"));
                    ((MainViewHolder) holder).iv_status.setImageResource(R.drawable.ic_fail_r);
                }
                else
                {
                    ((MainViewHolder) holder).txtv_status.setTextColor(R.color.main_disabled_color);
                }

                ((MainViewHolder) holder).txtv_status.setText(""+status);



                ((MainViewHolder) holder).txtv_remark.setText(""+remark);





                ((MainViewHolder) holder).lnLayout.setTag(position);
                ((MainViewHolder) holder).lnLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            Intent i = new Intent(context, StatusDetailsActivity.class);
                            i.putExtra("Date", jsonObject.getString("Date"));
                            i.putExtra("AccountNo", jsonObject.getString("AccountNo"));
                            i.putExtra("UTRNO", jsonObject.getString("UTRNO"));
                            i.putExtra("Name", jsonObject.getString("Name"));
                            i.putExtra("Beneficiary", jsonObject.getString("Beneficiary"));
                            i.putExtra("Amount", jsonObject.getString("Amount"));
                            i.putExtra("Status", jsonObject.getString("Status"));
                            i.putExtra("Remark", jsonObject.getString("Remark"));
                            i.putExtra("BeneficiaryNumber", jsonObject.getString("BeneficiaryNumber"));
                            i.putExtra("Branch", jsonObject.getString("Branch"));
                            i.putExtra("BankRefNo", jsonObject.getString("BankRefNo"));
                            i.putExtra("BeneficiaryBank", jsonObject.getString("BeneficiaryBank"));
                            i.putExtra("BeneficiaryBankBranch", jsonObject.getString("BeneficiaryBankBranch"));
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
        public TextView txtv_date,txtv_name,txtv_beneficiary, txtv_remark,txtv_status;
        ImageView iv_status;
        LinearLayout lnLayout;

        public MainViewHolder(View v) {
            super(v);
            //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
            txtv_date = v.findViewById(R.id.txtv_Date);
            iv_status = v.findViewById(R.id.iv_status);
//            txtv_name = v.findViewById(R.id.txtv_name);
            txtv_beneficiary = v.findViewById(R.id.txtv_beneficiary);
            txtv_status = v.findViewById(R.id.txtv_status);
            txtv_remark = v.findViewById(R.id.txtv_remark);
            lnLayout = v.findViewById(R.id.l2);

        }
    }
}