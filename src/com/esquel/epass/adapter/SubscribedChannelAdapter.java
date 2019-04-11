package com.esquel.epass.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.joyaether.datastore.DataElement;

public class SubscribedChannelAdapter extends BaseAdapter {

	DataElement adapter;
	int count;
	Activity activity;

	public SubscribedChannelAdapter(Activity activity, DataElement dataElement) {

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

		ViewHolder holder = null;

		DataElement itemLeaderBoard = adapter.asArrayElement().get(position);

		View convertView = view;
		if (convertView == null) {

			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_subscribed_channel_list, parent, false);

			holder = new ViewHolder();

			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.subscibe_channel_icon);
			holder.channelName = (TextView) convertView
					.findViewById(R.id.subscibe_channel_name);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.channelName.setText(getFieldValue(itemLeaderBoard.asObjectElement()
				.get("cahnnelname")));

		return convertView;
	}

	class ViewHolder {
		ImageView appIcon;
		TextView channelName;

	}
	
	/**
	 * check whether the field value is null & string type or not
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
