package com.esquel.epass.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.gson.JsonArray;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.rest.JsonArrayElement;

public class AnnualleaveLeaderAdapter extends BaseAdapter {
	
	private Context mContext;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private boolean inSearch;
	private Predicate<DataElement> predicate;
	private String searchKey;
	private ArrayElement dataSet;
	private ArrayElement originalDataSet;

	public AnnualleaveLeaderAdapter(Context context, ArrayElement array) {
		this.mContext = context;
		this.originalDataSet = array;
		this.dataSet = array;
		predicate = new Predicate<DataElement>() {
			 @Override
		        public boolean apply(DataElement input) {
				 	DataElement e = input.asObjectElement().get("employee_name");
				 	if (e != null && e.isPrimitive()) {
				 		String name = e.asPrimitiveElement().valueAsString();
				 		//to support case sensitive
				 		name = name.toLowerCase();
				 		return name.contains(getSearchKey());
				 	}
		            return true;
		        }
		};
	}
	


	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isInSearch() {
		return inSearch;
	}


	public void setInSearch(boolean inSearch) {
		this.inSearch = inSearch;
	}



	public String getSearchKey() {
		return searchKey;
	}



	public void setSearchKey(String key) {
		this.searchKey = key.toLowerCase();
		if (key == null || key.isEmpty()) {
			this.dataSet = originalDataSet;
			return;
		}
		//to support case sensitive

		Iterator<DataElement> iterators = Iterators.filter(originalDataSet.iterator(), predicate);
		ArrayElement arrayElement = new JsonArrayElement(new JsonArray());
		while(iterators.hasNext()) {
			arrayElement.add(iterators.next());
		}
		this.dataSet = arrayElement;
	}



	@Override
	public int getCount() {
		return dataSet.size();
	}



	@Override
	public Object getItem(int position) {
		return dataSet.get(position);
	}



	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(mContext, R.layout.item_anual_leave_leader, null);
		}
		
		TextView firstTextView = (TextView) view.findViewById(R.id.text1);
		firstTextView.setText("");
		TextView secondTextView = (TextView) view.findViewById(R.id.text2);
		secondTextView.setText("");	
		
		DataElement data = (DataElement) getItem(position);
		DataElement e = data.asObjectElement().get("employee_name");
		String text1 = "";
		if (e != null && e.isPrimitive()) {
			text1 = e.asPrimitiveElement().valueAsString();
		}
		
		e = data.asObjectElement().get("leave_name");
		if (e != null && e.isPrimitive()) {
			text1 = text1 + " - " + e.asPrimitiveElement().valueAsString();
		}
		String text2 = "";
		e = data.asObjectElement().get("leave_start_date");
		if (e != null && e.isPrimitive()) {
			Date date = new Date(e.asPrimitiveElement().valueAsLong());
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			dateFormat.setTimeZone(TimeZone.getTimeZone(((AppApplication) ((Activity) mContext).getApplication()).getDefaultTimeZone()));
			text2 = dateFormat.format(date);
		}
		firstTextView.setText(text1);
		secondTextView.setText(text2);
		return view;
	}
}
