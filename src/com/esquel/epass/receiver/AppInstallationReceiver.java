package com.esquel.epass.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class AppInstallationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent result = intent;
		LocalBroadcastManager.getInstance(context).sendBroadcast(result);
		
//		context.sendBroadcast(result);
	}

}
