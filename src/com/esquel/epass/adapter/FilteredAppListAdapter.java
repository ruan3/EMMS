package com.esquel.epass.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.esquel.epass.appstore.AppListAdapter;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;

public class FilteredAppListAdapter extends AppListAdapter {

	private ArrayElement elements;
	
	public FilteredAppListAdapter(Context context) {
		super(context, null, null, null);
	}
	
	@Override
	public int getCount() {
		if (elements != null) {
			return elements.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public Object getItem(int position) {
		if (elements != null) {
			return elements.get(position);
		} else {
			return null;
		}
	}
	
	public void setListItem(ArrayElement array) {
		elements = array;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getInflatedView(position, convertView, parent);
		onDataAvailable((DataElement) getItem(position), view);
		return view;
	}

}
