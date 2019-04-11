package com.esquel.epass.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class PackageUtils {
	
	public static String getPackageVersionName(Context context, String packageName) {
		try {
		  PackageManager manager = context.getPackageManager();
		  PackageInfo info = manager.getPackageInfo(
		  context.getPackageName(), 0);
		  return info.versionName;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getApplicationName(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			return manager.getApplicationLabel(manager.getApplicationInfo(packageName, ApplicationInfo.FLAG_INSTALLED)).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Drawable getApplicationIcon(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			return manager.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getPackageVersionCode(Context context, String packageName) {
		try {
		  PackageManager manager = context.getPackageManager();
		  PackageInfo info = manager.getPackageInfo(
				  packageName, 0);
		  return info.versionCode;
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info;
	}
	 
	public static boolean isPackageExist(Context context, String packageName) {
		return getPackageIntent(context, packageName) != null;
	}
	
	public static Intent getPackageIntent(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = getPackageInfo(context, packageName);
		if (info != null) {
			Intent intent = manager.getLaunchIntentForPackage(info.packageName);
			return intent;
		}
		return null;	
	}

}
