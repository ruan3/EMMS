package com.esquel.epass.ui;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.esquel.epass.R;

/**
 * Date & Time Picker  
 * 
 *
 */
public class DateTimePicker {

	/**
	 * 
	 * Shows date & time dialog
	 * @param activity instance of activity on which dialog is initialized
	 * @param textView instance of text view where required to set time & date
	 */
	public static void pickDateTime(Activity activity,final TextView textView) {
//		final String date = "";
		final StringBuilder builder=new StringBuilder();
		final Dialog dialog = new Dialog(activity);

		dialog.setContentView(R.layout.date_time_layout);
		
		final DatePicker datePicker=(DatePicker)dialog.findViewById(R.id.datePicker);
		final TimePicker timePicker=(TimePicker)dialog.findViewById(R.id.timePicker);
		Button done  = (Button) dialog.findViewById(R.id.done);
		Button cancel  = (Button) dialog.findViewById(R.id.cancel);
		
		
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.append(setData(datePicker.getYear())+"-");
				builder.append((setData(datePicker.getMonth() + 1))+"-");//month is 0 based
				builder.append(setData(datePicker.getDayOfMonth()));
				builder.append(" "+ setData(timePicker.getCurrentHour())+":");
				builder.append(setData(timePicker.getCurrentMinute()));
				textView.setText(builder.toString());
				dialog.dismiss();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
		
		
	}
	
	/**
	 *  
	 * 
	 * @return properly formated date
	 */
	private static String setData(int data){
		return (String) (data<10? "0" + data:data+"");
		
	}
	
}
