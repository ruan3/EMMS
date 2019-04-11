package com.esquel.epass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A class to manager the value store in {@link SharedPreferences}.
 * 
 * @author joyaether
 * 
 */
public final class SharedPreferenceManager {

	public static final String KEY_REGION = "region";
	public static final String KEY_LAST_SYNC_DATA_DATE = "last-sync-data-date";
	public static final String KEY_REPEAT_COUNT = "repeat_count";
	public static final String KEY_USER_NAME = "user-name";
	public static final String KEY_APP_STORE_REGION = "appstore-region";
	public static final String KEY_USER_CHANNEL = "user-channels";
	public static final String KEY_SUBSCRIBED_APPLICATION = "appId-";
	public static final String KEY_USER_CHANNEL_COUNT = "user-channel-count";
	public static final String KEY_USER_ID = "code";


	private SharedPreferenceManager() {

	}
	
	public static String getAppStoreRegion(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_APP_STORE_REGION, null);
	}

	public static void setAppStoreRegion(Context context, String region) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_APP_STORE_REGION, region).commit();
	}

	public static String getRegion(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_REGION, null);
	}

	public static void setRegion(Context context, String region) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_REGION, region).commit();
	}

	public static long getLastSyncDataDate(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getLong(KEY_LAST_SYNC_DATA_DATE, 0L);
	}

	public static void setLastSyncDataDate(Context context,
			long lastSyncDataDate) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putLong(KEY_LAST_SYNC_DATA_DATE, lastSyncDataDate).commit();
	}
	
	public static long getRepeatCount(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getLong(KEY_REPEAT_COUNT, -100L);
	}

	public static void setRepeatCount(Context context,
			long count) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putLong(KEY_REPEAT_COUNT, count).commit();
	}
	
	public static void setUserName(Context context, String username) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_USER_NAME, username).commit();
	}
	
	public static String getUserName(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_USER_NAME, null);
	}
	
	public static String getUserChannel(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(getUserId(context) + KEY_USER_CHANNEL, null);
	}
	
	public static void setUserChannel(Context context, String userChannel) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(getUserId(context) + KEY_USER_CHANNEL, userChannel).commit();
	}
	
	public static String getSubscribedApplicationDetail(Context context, int appId) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_SUBSCRIBED_APPLICATION + appId, null);
	}
	
	public static void setSubscribedApplicationDetail(Context context,int appId, String detail) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_SUBSCRIBED_APPLICATION + appId, detail).commit();
	}
	
	public static int getUserChannelCount(Context context, int defaultCount) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getInt(getUserId(context) + "-" + KEY_USER_CHANNEL_COUNT, defaultCount);
	}
	
	public static void setUserChannelCount(Context context, int count) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putInt(getUserId(context) + "-" + KEY_USER_CHANNEL_COUNT, count).commit();
	}
	
	public static String getUserId(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString("code", null);
	}

}
 