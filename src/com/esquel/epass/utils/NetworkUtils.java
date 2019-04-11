package com.esquel.epass.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	private NetworkUtils() {

	}

	public static boolean hasNetworkConnection(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			return netInfo != null && netInfo.isConnected();
		} catch (Exception e) {
			return false;
		}
	}

}
