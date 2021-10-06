package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativethoughts.iscore.R;

import java.util.ArrayList;
import java.util.List;

public class OperatorSpinnerAdapter extends BaseAdapter {
    LayoutInflater mInflater = null;
    private List<String> mItems = new ArrayList< >();
    String OperatorNames = "";
    int types =0;

    public OperatorSpinnerAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clear() {
        mItems.clear();

        notifyDataSetChanged();
    }

    public void addItem(String spinnerModel) {
        if (spinnerModel == null) {
            return;
        }

        mItems.add(spinnerModel);

        notifyDataSetChanged();
    }

    public void addItems(List<String> spinnerModels,String OperatorName,int type) {
        if (spinnerModels == null) {
            return;
        }

        mItems.addAll(spinnerModels);
        OperatorNames = OperatorName;
        types = type;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {

            view = mInflater.inflate(R.layout.spinner_item_dropdown_operator_adaper_row, parent, false);
            view.setTag("DROPDOWN");
        }

        types = 0;
        String name = getTitle(position);
        Log.e("strings","strings 12     "+name);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(name);

        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        int operatorImageId = getOperatorIcon(name);

        if(operatorImageId == -1) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(operatorImageId);
        }

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mInflater.inflate(R.layout.
                    spinner_item_dropdown_operator_adaper_row, parent, false);
            view.setTag("NON_DROPDOWN");
        }

        String name = getTitle(position);
        Log.e("strings","strings 0     "+name+"   "+OperatorNames+"  "+types);


        if (types == 0) {

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));

            ImageView imageView = (ImageView) view.findViewById(R.id.image);

            int operatorImageId = getOperatorIcon(name);

            if (operatorImageId == -1) {
                imageView.setVisibility(View.INVISIBLE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(operatorImageId);
            }
        }else {
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(OperatorNames);

            ImageView imageView = (ImageView) view.findViewById(R.id.image);

            int operatorImageId = getOperatorIcon(OperatorNames);

            if (operatorImageId == -1) {
                imageView.setVisibility(View.INVISIBLE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(operatorImageId);
            }
        }

        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position) : "";
    }

    private int getOperatorIcon(String operator) {

        if(operator.startsWith("Airtel")) {
            return R.drawable.airtel;
        } else if(operator.startsWith("Vodafone")) {
            return R.drawable.vodafone;
        } else if(operator.startsWith("BSNL")) {
            if(operator.startsWith("BSNL 3G")) {
                return R.drawable.bsnal_3g;
            }
            return R.drawable.bsnl;
        } else if(operator.startsWith("Reliance")) {
            return R.drawable.reliance;
        } /*else if(operator.startsWith("Aircel")) {
            return R.drawable.aircel;
        } else if(operator.startsWith("MTNL")) {
            return R.drawable.mtnl;
        } */ else if(operator.startsWith("Idea")) {
            return R.drawable.idea;
        } else if(operator.startsWith("Tata Indicom")) {
            return R.drawable.tata_indicom;
        } /*else if(operator.startsWith("Loop")) {
            return R.drawable.loop;
        } else if(operator.startsWith("LOOP")) {
            return R.drawable.loop;
        }*/ else if(operator.startsWith("Tata Docomo")) {
            return R.drawable.docomo;
        }/* else if(operator.startsWith("Virgin")) {
            return R.drawable.virgin;
        } */else if(operator.startsWith("MTS")) {
            return R.drawable.mts;
        } /*else if(operator.startsWith("S Tel")) {
            return R.drawable.stel;
        } *//*else if(operator.startsWith("Uninor")) {
            return R.drawable.uninor;
        }*/
//         else if(operator.startsWith("Videocon")) {
//            return R.mipmap.v;
//        }
        else if(operator.startsWith("Dish TV")) {
            return R.drawable.dishtv;
        } else if(operator.startsWith("Big TV")) {
            return R.drawable.bigtv;
        } else if(operator.startsWith("Tata Sky")) {
            return R.drawable.tata_sky;
        } else if(operator.startsWith("Sun DTH")) {
            return R.drawable.sun_direct;
        }
        else if(operator.startsWith("NetConnect +"))
            return R.drawable.reliance;
        else if(operator.startsWith("NetConnect 3G"))
            return R.drawable.reliance;
        else if(operator.startsWith("Tata Photon +"))
            return R.drawable.docomo;
        else if(operator.startsWith("Mbrowse"))
            return R.drawable.mts;
        else if(operator.startsWith("Jio"))
            return R.mipmap.jio;
        else if(operator.startsWith("V!"))
            return R.mipmap.vi;


        return -1;
    }
}
