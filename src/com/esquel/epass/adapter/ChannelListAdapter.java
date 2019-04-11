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

public class ChannelListAdapter extends BaseAdapter {

	DataElement adapter;
	Activity activity;
	int count;
	private static final int EDITABLE_POSITION = 2;

	public ChannelListAdapter(Activity activity, DataElement dataElement) {
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

		DataElement itemChannelList = adapter.asArrayElement().get(position);

		View convertView = view;
		if (convertView == null) {

			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_fix_drag_subscribe_channel, parent, false);
			holder = new ViewHolder();

			holder.channelName = (TextView) convertView
					.findViewById(R.id.channel_name);
			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.subscibe_channel_icon);
			holder.dragHandle = (ImageView) convertView
					.findViewById(R.id.drag_handle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (itemChannelList.asObjectElement().get("esquelnews")
				.asPrimitiveElement().valueAsBoolean() == false) {
			holder.channelName.setText(getFieldValue(itemChannelList
					.asObjectElement().get("cahnnelname")));
		}
		
//		if (position > EDITABLE_POSITION) {
//			holder.dragHandle.setVisibility(View.VISIBLE);
//		} else {
			holder.dragHandle.setVisibility(View.GONE);
//		}
		
		return convertView;

	}

	class ViewHolder {
		TextView channelName;
		ImageView appIcon;
		ImageView dragIcon, dragHandle;

	}

	String getFieldValue(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsString();

		}
		return "";
	}
}
