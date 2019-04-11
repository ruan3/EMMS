package com.esquel.epass.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.item.ItemLeaveGenerationDetails;
import com.esquel.epass.leave.AnnualLeaveGenerationActivity;
import com.esquel.epass.ui.AnimatedExpandableListView.AnimatedExpandableListAdapter;

/**
 * 
 * Holds the list of details of leave corresponding to the user.
 * 
 * 
 */
public class LeaveGenerateAdapter extends AnimatedExpandableListAdapter {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	Activity activity;
	ArrayList<ItemLeaveGenerationDetails> list;
	LayoutInflater inflater;
	private static final int POSITION_LEAVE_TYPE = 2;
	private static final int POSITION_START_DATE = 4;
	private static final int POSITION_END_DATE = 5;

	public LeaveGenerateAdapter(Activity activity,
			ArrayList<ItemLeaveGenerationDetails> list) {

		this.activity = activity;
		this.list = list;
		inflater = LayoutInflater.from(activity);

	}

	/**
	 * 
	 * Holds the cell of the Group/Parent of list.
	 * 
	 */
	private class GroupHolder {
		private TextView key;
		private TextView value;
		private TextView arrow;
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
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

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		GroupHolder holder;
		ItemLeaveGenerationDetails itemDetails = list.get(groupPosition);
		if (convertView == null || !(convertView.getTag() instanceof GroupHolder)) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_leave_generation, parent, false);
			holder = new GroupHolder();
			holder.key = (TextView) convertView.findViewById(R.id.detailKey);
			holder.value = (TextView) convertView
					.findViewById(R.id.detailValue);
			holder.arrow = (TextView) convertView.findViewById(R.id.arrow);
			convertView.setTag(holder);

		} else {
			holder = (GroupHolder) convertView.getTag();
		}

		String defaultValue = itemDetails.getLeaveDetailValue();
		holder.key.setText(itemDetails.getLeaveDetailKey());
		holder.value.setText(defaultValue);

	
		switch (groupPosition) {
		case POSITION_LEAVE_TYPE:
		case POSITION_START_DATE:
		case POSITION_END_DATE:
			holder.value.setTextColor(activity.getResources().getColor(R.color.black_content));
			holder.arrow.setVisibility(View.VISIBLE);
			break;

		default:
			holder.value.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
			holder.arrow.setVisibility(View.INVISIBLE);
			break;
		}

			
		
		
		return convertView;

	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return false;
	}

	@Override
	public View getRealChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, final ViewGroup parent) {
		
		View view = convertView;
		if (view == null) {
			view = inflater
					.inflate(R.layout.date_time_layout, parent, false);
		}
		
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		datePicker.setTag(groupPosition);
		final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
		timePicker.setTag(groupPosition);

		// ChildItem item = getChild(groupPosition, childPosition);
		String defaultText = list.get(groupPosition).getLeaveDetailValue();

		Date defaultDate = new Date();
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
		if (defaultText != null) {
			try {
				defaultDate = formatter.parse(defaultText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
		calendar.setTime(defaultDate);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		TextView textView = null;
		if (groupPosition < parent.getChildCount()) {
			textView = (TextView) parent.getChildAt(groupPosition).findViewById(R.id.detailValue);
		}
		
		final TextView t = textView;
		//When date has change, need to capture time
		datePicker.init(year, monthOfYear, dayOfMonth,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						int currentPosition = (Integer) view.getTag();
						if (currentPosition != groupPosition) {
							return;
						}
						String defaultText = list.get(currentPosition).getLeaveDetailValue();

						Date defaultDate = new Date();
						final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
						formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
						if (defaultText != null) {
							try {
								defaultDate = formatter.parse(defaultText);
							} catch (Exception e) {
							}
						}
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity
								.getApplication()).getDefaultTimeZone()));
						calendar.setTime(defaultDate);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						String dateString = "";

						dateString = formatter.format(calendar.getTime());
						list.get(currentPosition).setLeaveDetailValue(dateString);
						if (t != null) {
							t.setText(dateString);
						}

					}
				});
		
		
		boolean isSupportAPI11 = android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
		Calendar minCalendar = Calendar.getInstance(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
		minCalendar.set(Calendar.DAY_OF_MONTH, 1);
		minCalendar.set(Calendar.HOUR_OF_DAY, 0);
		minCalendar.set(Calendar.SECOND, 0);
		if (isSupportAPI11) {
			datePicker.setMinDate(minCalendar.getTimeInMillis());
		}
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		final int position = groupPosition;

		//When time has change, need to capture date
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				int currentPosition = (Integer) view.getTag();
				if (currentPosition != groupPosition) {
					return;
				}
				String defaultText = list.get(currentPosition).getLeaveDetailValue();

				Date defaultDate = new Date();
				final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
				formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
				if (defaultText != null) {
					try {
						defaultDate = formatter.parse(defaultText);
					} catch (Exception e) {
					}
				}
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeZone(TimeZone.getTimeZone(((AppApplication) activity.getApplication()).getDefaultTimeZone()));
				calendar.setTime(defaultDate);
				calendar.set(Calendar.HOUR_OF_DAY,
						hourOfDay);
				calendar.set(Calendar.MINUTE, view.getCurrentMinute());
				String dateString = "";
				dateString = formatter.format(calendar.getTime());
				list.get(position).setLeaveDetailValue(dateString);
				if (t != null) {
					t.setText(dateString);
				}			
			}
		});
		return view;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		if (groupPosition == POSITION_START_DATE
				|| groupPosition == POSITION_END_DATE) {
			((AnnualLeaveGenerationActivity) activity).getRemainingLeave();
			
			checkSeletedDate();
			notifyDataSetChanged();

		}
	}

	/**
	 * Check whether end date & start date selected appropriate or not.
	 * <br/>
	 * If end date is early in compare to start date than it will set start date to end date 
	 * 
	 */
	private void checkSeletedDate() {
		String endDateString = list.get(POSITION_END_DATE).getLeaveDetailValue();
		String startDateString = list.get(POSITION_START_DATE).getLeaveDetailValue();
	    DateFormat df = new SimpleDateFormat(DATE_FORMAT); 
	    Date endDate, startDate;
	    try {
	        endDate = df.parse(endDateString);
	        startDate = df.parse(startDateString);
	        double diff = endDate.getTime() - startDate.getTime();
	        if (Math.floor(diff) <= 0) {
	        	list.get(POSITION_END_DATE).setLeaveDetailValue(startDateString);
	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
		
	}

	@Override
	public int getRealChildrenCount(int groupPosition) {
		return (groupPosition == POSITION_START_DATE || groupPosition == POSITION_END_DATE) ? 1
				: 0;
	}

}
