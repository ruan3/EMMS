package com.esquel.epass.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.schema.LeavesApplicationStatus;
import com.joyaether.datastore.DataElement;

/**
 * 
 * Holds the list of applied leave corresponding to the user
 * 
 *
 */
public class AppliedLeaveAdapter extends BaseAdapter {

	Activity activity;
	DataElement list;
	int count;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	
	public AppliedLeaveAdapter(Activity activity, DataElement all) {
		
		this.activity = activity;
		this.list = all;
		//seem can not get count directly call get size() at getCount()
		if (all != null && all.isArray()) {
			count = all.asArrayElement().size();
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
		return list.asArrayElement().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		DataElement itemLeave = list.asArrayElement().get(position);
	
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_applied_leave, parent, false);
			holder = new ViewHolder();
			holder.type = (TextView) convertView.findViewById(R.id.leaveType);
			holder.date = (TextView) convertView.findViewById(R.id.leaveDate);
			holder.status = (TextView) convertView.findViewById(R.id.leaveStatus);
			holder.notBadge = (TextView) convertView.findViewById(R.id.notBadge);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.type.setText(getFieldValue(itemLeave.asObjectElement().get(LeavesApplicationStatus.RESPONSE_LEAVE_TYPE))); 
		DataElement e = itemLeave.asObjectElement().get(LeavesApplicationStatus.RESPONSE_START_DATE);
		if (e != null && e.isPrimitive()) {
			Date date = new Date(e.asPrimitiveElement().valueAsLong());
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			dateFormat.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
			holder.date.setText(dateFormat.format(date));
		}
		e = itemLeave.asObjectElement().get(LeavesApplicationStatus.RESPONSE_STATUS);
		String status = "";
		if (e != null && e.isPrimitive()) {
			switch (e.asPrimitiveElement().valueAsInt()) {
				case LeavesApplicationStatus.RESPONSE_OTHER_TO_BE_APPROVED:
				case LeavesApplicationStatus.RESPONSE_TO_BE_APPROVED:
					status = activity.getString(R.string.leave_tobeapprove);
					holder.status.setTextColor(Color.BLUE);
					holder.notBadge.setVisibility(View.INVISIBLE);
					break;
				case LeavesApplicationStatus.RESPONSE_APPROVED:
					status = activity.getString(R.string.leave_approved);
					holder.status.setTextColor(Color.GREEN);
					holder.notBadge.setVisibility(View.INVISIBLE);
					break;
				default :
					//the status number of rejected is 6/9 so put on default to handle.
					status = activity.getString(R.string.leave_rejected);
					holder.status.setTextColor(Color.RED);
					holder.notBadge.setVisibility(View.VISIBLE);
					break;
			}
		}
		holder.status.setText(status);
		
		return convertView;
	}

	/**
	 * 
	 * Holds the current cell of the list
	 * 
	 */
	private class ViewHolder {
		private TextView type;
		private TextView date;
		private TextView status;		
		private TextView notBadge;
	}

	/**
	 * check whether the field value is null & string type or not
	 * @param fieldValue DataElement variable 
	 * @return proper string
	 */
	String getFieldValue(DataElement fieldValue){
		if (fieldValue != null && fieldValue.isPrimitive()) {
			
			return fieldValue.asPrimitiveElement().valueAsString(); 
		
		}
		return "";
	}
}
