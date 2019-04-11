package com.esquel.epass.utils;

import android.content.Context;

/**
 * Define the enum of region with the app need.
 * 
 * @author joyaether
 * 
 */
public class EsquelPassRegion {
	public static final String CHINA = "china";
	public static final String HONG_KONG = "hongkong";

	private final String regionCode;

	EsquelPassRegion(String code) {
		regionCode = code;
	}

	public String toString() {
		return regionCode;
	}

	public static EsquelPassRegion getRegion(String code) {
		if (code == null) {
			return null;
		}
		return new EsquelPassRegion(code);
	}

	public static EsquelPassRegion getDefault(Context context) {
		String regionCode = SharedPreferenceManager.getRegion(context);
		if (regionCode == null) {
			return new EsquelPassRegion(CHINA);
		}
		return getRegion(regionCode);
	}

	public static void setDefault(Context context, String code) {
		SharedPreferenceManager.setRegion(context, code);
	}

}
