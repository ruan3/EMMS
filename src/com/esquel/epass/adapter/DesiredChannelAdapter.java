package com.esquel.epass.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.joyaether.datastore.DataElement;

public class DesiredChannelAdapter extends BaseAdapter {

	DataElement adapter;
	int count;
	Activity activity;

	public DesiredChannelAdapter(Activity activity, DataElement dataElement) {
		this.adapter = dataElement;
		this.activity = activity;

		if (adapter != null && adapter.isArray()) {
			count = adapter.asArrayElement().size();
		} else {
			count = 0;
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return adapter.asArrayElement().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		DataElement itemLeaderBoard = adapter.asArrayElement().get(position);
		ViewHolder holder = null;
		View convertView = view;
		if (convertView == null) {

			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_desired_channel_list, parent,
					false);

			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.desiredChannelName = (TextView) convertView.findViewById(R.id.desired_channel_name);
		holder.desiredChannelTitle = (TextView) convertView.findViewById(R.id.deisre_channel_title);
		holder.desireChannelLinear = (LinearLayout) convertView.findViewById(R.id.desire_channel_ll);
		
		holder.desiredChannelName.setText(getFieldValue(itemLeaderBoard
				.asObjectElement().get("cahnnelname")));

		
		
		return convertView;
	}

	class ViewHolder {
		
		TextView desiredChannelName;
		TextView desiredChannelTitle;
		LinearLayout desireChannelLinear;

	}
	
	/**
	 * check whether the field value is null & string type or not.
	 * 
	 * @param fieldValue
	 *            DataElement variable
	 * @return proper string
	 */

	String getFieldValue(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsString();

		}
		return "";
	}

}