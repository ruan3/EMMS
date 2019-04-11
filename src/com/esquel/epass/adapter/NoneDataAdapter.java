package com.esquel.epass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class NoneDataAdapter extends BaseAdapter {

	private final LayoutInflater inflater;

	private Context mContext;

	public NoneDataAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {

		return 1;

	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return inflater.inflate(R.layout.view_no_data, null);
	}

}
