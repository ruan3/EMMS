package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.esquel.epass.item.ItemEsquelTube;
import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class EsquelTubeAdapter extends BaseAdapter {

	Activity activity;
	List<ItemEsquelTube> list;

	public EsquelTubeAdapter(Activity activity, List<ItemEsquelTube> list) {
		this.activity = activity;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View convertView = view;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.adapter_esqueltube, parent, false);
		}

		return convertView;
	}

}
