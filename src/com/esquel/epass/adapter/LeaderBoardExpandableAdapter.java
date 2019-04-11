package com.esquel.epass.adapter;

import java.util.List;

import u.aly.V;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.appstore.LeaderBoardActivity;
import com.esquel.epass.ui.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.joyaether.datastore.DataElement;

public class LeaderBoardExpandableAdapter extends AnimatedExpandableListAdapter
		implements OnClickListener {

	Activity activity;
	List<String> parentList;
	List<DataElement> childList;

	public LeaderBoardExpandableAdapter(Activity activity,
			List<String> parentDataElementList,
			List<DataElement> childDataElementList) {

		this.activity = activity;
		this.parentList = parentDataElementList;
		this.childList = childDataElementList;
	}

	@Override
	public int getGroupCount() {
		return parentList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).asArrayElement().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	class ViewHolder {
		TextView categoryName;
		ImageView tickIcon;
		ImageView downIcon;
		View listDivider;

	}

	class ChildViewHolder {
		TextView categoryName;
		ImageView tickIcon;
		ImageView downIcon;

	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View view,
			ViewGroup parent) {
		ViewHolder holder = null;

		String parentListData = parentList.get(groupPosition);

		View convertView = view;
		if (convertView == null) {

			convertView = LayoutInflater.from(activity).inflate(
					R.layout.list_leaderboard_category, parent, false);

			holder = new ViewHolder();

			holder.categoryName = (TextView) convertView
					.findViewById(R.id.category_name);
			holder.tickIcon = (ImageView) convertView.findViewById(R.id.check);
			holder.downIcon = (ImageView) convertView
					.findViewById(R.id.down_icon);
			holder.listDivider = (View) convertView
					.findViewById(R.id.list_divider_view);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.listDivider.setVisibility(View.INVISIBLE);

		holder.categoryName.setText(parentListData);

		if (childList.get(groupPosition).asArrayElement().size() == 0) {
			convertView.setOnClickListener(this);
			holder.downIcon.setVisibility(View.INVISIBLE);
		}

		Log.i("", "childList size"
				+ childList.get(groupPosition).asArrayElement().size());

		holder.tickIcon.setVisibility(View.INVISIBLE);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public View getRealChildView(int groupPosition, int childPosition,
			boolean isLastChild, View view, ViewGroup parent) {

		ChildViewHolder childHolder = null;

		View convertView = view;
		if (convertView == null) {

			convertView = LayoutInflater.from(activity).inflate(
					R.layout.list_leaderboard_category, parent, false);

			childHolder = new ChildViewHolder();

			childHolder.categoryName = (TextView) convertView
					.findViewById(R.id.category_name);
			childHolder.tickIcon = (ImageView) convertView
					.findViewById(R.id.check);
			childHolder.downIcon = (ImageView) convertView
					.findViewById(R.id.down_icon);

			convertView.setTag(childHolder);
		} else {
			childHolder = (ChildViewHolder) convertView.getTag();
		}
		convertView.setOnClickListener(this);

		childHolder.downIcon.setVisibility(View.INVISIBLE);
		childHolder.tickIcon.setVisibility(View.INVISIBLE);

		DataElement childAppCategory = childList.get(groupPosition)
				.asArrayElement();

		for (int i = 0; i < childAppCategory.asArrayElement().size(); i++) {

			DataElement childParentCategory = childAppCategory.asArrayElement()
					.get(i).asObjectElement().get("parent_app_category");

			if (childParentCategory.asObjectElement() != null) {

				childHolder.categoryName.setText(childParentCategory
						.asObjectElement().get("name").asPrimitiveElement()
						.valueAsString());

			}

		}

		return convertView;
	}

	@Override
	public int getRealChildrenCount(int groupPosition) {
		return childList.get(groupPosition).asArrayElement().size();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity, LeaderBoardActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(intent);
		activity.finish();

	}

}
