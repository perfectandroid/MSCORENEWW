package com.creativethoughts.iscore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.model.CustomerModulesModel;

import java.util.ArrayList;

public class CustomerModulesAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private ArrayList<CustomerModulesModel> arraylist;

	public CustomerModulesAdapter(Context context, ArrayList<CustomerModulesModel> arraylist) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = arraylist;
	}

	public class ViewHolder {
		TextView tvPrdName;
	}

	@Override
	public int getCount() {
		return arraylist.size();
	}

	@Override
	public CustomerModulesModel getItem(int position) {
		return arraylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.modulelist, null);
			holder.tvPrdName = (TextView) view.findViewById(R.id.tvPrdName);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.tvPrdName .setText(arraylist.get(position).getModuleName());
		return view;
	}

}