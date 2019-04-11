package com.esquel.epass.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.esquel.epass.R;
import com.joyaether.datastore.DataElement;

public class LeaderBoardAdapter extends BaseAdapter {

	DataElement adapter;
	int count;
	int counter = 0;
	Activity activity;

	public LeaderBoardAdapter(Activity activity, DataElement dataElement) {

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
					R.layout.item_leaderboard_cell, parent, false);

			holder = new ViewHolder();

			holder.count = (TextView) convertView.findViewById(R.id.list_count);

			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.app_icon);

			holder.appName = (TextView) convertView.findViewById(R.id.app_name);
			holder.appDecs = (TextView) convertView.findViewById(R.id.app_decs);
			holder.appCategory = (TextView) convertView
					.findViewById(R.id.app_category);

			holder.appRating = (RatingBar) convertView
					.findViewById(R.id.app_ratingbar);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		counter++;

		holder.count.setText("" + counter);
		float rating = Float.parseFloat(getFieldValue(itemLeaderBoard
				.asObjectElement().get("average_rating")));

		holder.appName.setText(getFieldValue(itemLeaderBoard.asObjectElement()
				.get("name")));
		holder.appCategory.setText(getFieldValue(itemLeaderBoard
				.asObjectElement().get("name")));
		holder.appDecs.setText(getFieldValue(itemLeaderBoard.asObjectElement()
				.get("description")));

		LayerDrawable stars = (LayerDrawable) holder.appRating
				.getProgressDrawable();
		stars.getDrawable(2).setColorFilter(Color.YELLOW,
				PorterDuff.Mode.SRC_ATOP);

		holder.appRating.setRating(rating);

		return convertView;
	}

	class ViewHolder {
		TextView count;
		ImageView appIcon;
		TextView appName;
		TextView appDecs;
		RatingBar appRating;
		TextView appCategory;
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
