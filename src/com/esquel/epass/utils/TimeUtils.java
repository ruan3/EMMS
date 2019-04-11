package com.esquel.epass.utils;

import android.content.Context;

import com.esquel.epass.R;

public class TimeUtils {
	
	private static final long MILLSECOND = 1000;
	private static final long HOUR = 60 * 60 * MILLSECOND;
	private static final long DAY = 24 * HOUR;
	
	public static String getFormattedTime(Context context, long time) {
		if (time >= DAY) {
			return time / DAY + context.getString(R.string.day_left);
		} else {
			return time / HOUR + context.getString(R.string.hour_left);
		}
	}

}
