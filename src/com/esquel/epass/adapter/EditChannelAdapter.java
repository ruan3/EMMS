package com.esquel.epass.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.utils.ParserUtils;
import com.joyaether.datastore.DataElement;

public class EditChannelAdapter extends BaseAdapter {

	DataElement adapter;
	int count;
	Activity activity;

	public EditChannelAdapter(Activity activity, DataElement dataElement) {
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
					R.layout.item_subscribed_channel_reorder_list, parent,
					false);

			holder = new ViewHolder();
			holder.mainLayout = (RelativeLayout) convertView
					.findViewById(R.id.rlMainlayout);

			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.subscibe_channel_icon);
			holder.rearrangeIcon = (ImageView) convertView
					.findViewById(R.id.rearrange_icon);
			holder.channelName = (TextView) convertView
					.findViewById(R.id.subscibe_channel_name);
//			holder.cellDivider = (View) convertView
//					.findViewById(R.id.cellDivider);
			
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.channelName.setText(getFieldValue(itemLeaderBoard
				.asObjectElement().get("cahnnelname")));
		if (ParserUtils.getBoolean(itemLeaderBoard.asObjectElement(),
				"isHeader")) {
			
			holder.mainLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.sub_header));
			holder.appIcon.setVisibility(View.GONE);
//			holder.cellDivider.setVisibility(View.GONE);
		} else {
			
			holder.mainLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.transparent));
			holder.appIcon.setVisibility(View.VISIBLE);
//			holder.cellDivider.setVisibility(View.VISIBLE);
			
		}
		return convertView;
	}

	class ViewHolder {
		ImageView appIcon;
		ImageView rearrangeIcon;
		TextView channelName;
		RelativeLayout mainLayout;
		View cellDivider;
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
