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

public class LeaderBoardCategoryAdapter extends BaseAdapter {

	DataElement adapter;
	Activity activity;
	int count;

	public LeaderBoardCategoryAdapter(Activity activity, DataElement dataElement) {
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
					R.layout.list_leaderboard_category, parent, false);

			holder = new ViewHolder();

			holder.categoryName = (TextView) convertView
					.findViewById(R.id.category_name);
			holder.tickIcon = (ImageView) convertView.findViewById(R.id.check);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// holder.categoryName.setText(getFieldValue(itemLeaderBoard
		// .asObjectElement().get("name")));

		// //////////////
		holder.categoryName.setText(getFieldValue(itemLeaderBoard
				.asObjectElement().get("appcategory")));
		// ////////////
		holder.tickIcon.setVisibility(View.INVISIBLE);

		return convertView;
	}

	class ViewHolder {
		TextView categoryName;
		ImageView tickIcon;

	}

	String getFieldValue(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsString();

		}
		return "";
	}

}
