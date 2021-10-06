package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.SingleBranchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BankListInfoAdapter extends RecyclerView.Adapter {
    JSONArray jsonArray;
    JSONObject jsonObject = null;
    Context context;
    String bnkName, address, phone,place,post,district,inchrge,brnch,id,latitude,longitude;

    public BankListInfoAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
       // this.strdate = strdate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_banklist, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject = jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                bnkName = jsonObject.getString("BankName");
                brnch = jsonObject.getString("BranchName");
                address = jsonObject.getString("Address");
                phone = jsonObject.getString("ContactPersonMobile");
                place = jsonObject.getString("Place");
                post = jsonObject.getString("Post");
                district = jsonObject.getString("District");
                inchrge = jsonObject.getString("InchargeContactPerson");
                id = jsonObject.getString("ID_Branch");
                latitude = jsonObject.getString("LocationLatitude");
                longitude = jsonObject.getString("LocationLongitude");

                //  LoanNumber = jsonObject.getString("LoanNumber");
            //    mobilenum = jsonObject.getString("CustomerMobileNumber");
                ((MainViewHolder) holder).txtv_bnkName.setText(bnkName);
                ((MainViewHolder) holder).txtv_hrs.setText("Working Hours : "+jsonObject.getString("OpeningTime")+" - "+jsonObject.getString("ClosingTime"));
                ((MainViewHolder) holder).txtv_address.setText(inchrge+"\n"+brnch+","+"\n"+address+","+"\n"+place+","+"\n"+post+","+"\n"+district+"\nPhone : "+
                        jsonObject.getString("LandPhoneNumber")+", "+jsonObject.getString("BranchMobileNumber"));
              //  ((MainViewHolder) holder).txtv_ph.setText("Contact Person: \n"+inchrge+"\nPhone no: "+phone);
                ((MainViewHolder) holder).txtv_ph.setText("Phone no: "+phone);
                //((MainViewHolder) holder).txtv_ph.setText(phone+"("+inchrge+")");
                if (!latitude.equals("")&& !longitude.equals("")
                       /* ||!(latitude.equals("null"))&&!(longitude.equals("null"))*/)
                {
                    ((MainViewHolder) holder).imgv_dirctn.setVisibility(View.VISIBLE);
                    ((MainViewHolder) holder).imgv_dirctn.setImageResource(R.drawable.ic_directions);
                }else {
                    ((MainViewHolder) holder).imgv_dirctn.setVisibility(View.INVISIBLE);}
                ((MainViewHolder) holder).imgv_dirctn.setTag(position);
                ((MainViewHolder) holder).imgv_dirctn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            Intent i = new Intent(context, SingleBranchActivity.class);
                            i.putExtra("ID_Br", jsonObject.getString("ID_Branch"));
                            i.putExtra("Lat", jsonObject.getString("LocationLatitude"));
                            i.putExtra("Long", jsonObject.getString("LocationLongitude"));
                            i.putExtra("BNK", jsonObject.getString("BankName"));
                            context.startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
              //  ((MainViewHolder) holder).imnext.setTag(position);
               /* ((MainViewHolder) holder).imnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            Intent i = new Intent(context, SingleLoanActivity.class);
                            i.putExtra("ID_BcLoanAccount", jsonObject.getString("ID_BcLoanAccount"));
                            i.putExtra("CustomerMobileNumber", jsonObject.getString("CustomerMobileNumber"));
                            i.putExtra("strdate", strdate);
                            context.startActivity(i);
                            ((Activity) context).finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });*/
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
        LinearLayout lnLayout;
        TextView txtv_bnkName,txtv_address,txtv_ph, txtv_hrs;
        ImageView imgv_dirctn;

        public MainViewHolder(View v) {
            super(v);
          //  lnLayout = v.findViewById(R.id.ll_loanApplicationListInfoApp);
            txtv_bnkName = v.findViewById(R.id.txtv_bankname);
            txtv_address = v.findViewById(R.id.txtv_address);
            txtv_ph = v.findViewById(R.id.txtv_ph);
            imgv_dirctn = v.findViewById(R.id.imgv_dirctn);
            txtv_hrs = v.findViewById(R.id.txtv_hrs);
        }
    }
}
