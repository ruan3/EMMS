package com.esquel.epass.activity;

import com.esquel.epass.R;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.ui.LoadingDialog;
import com.esquel.epass.utils.SharedPreferenceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
    public static final String ACTION_TOKEN_EXPIRED = "token-expired"; 
    private LoadingDialog loadingDialog;
    private TokenExpiredReceiver receiver;
    private IntentFilter filter;
    private boolean registerReceiver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        loadingDialog = new LoadingDialog(this);
        receiver = new TokenExpiredReceiver();
        filter = new IntentFilter();
        filter.addAction(ACTION_TOKEN_EXPIRED);
        registerReceiver(receiver, filter);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	this.unregisterReceiver(receiver);
    }


	private class TokenExpiredReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_TOKEN_EXPIRED)) {
				handleTokenExpired();
			}
		}
    	
    }
    
    
    private void handleTokenExpired() {
		dismissLoadingDialog();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		pref.edit().remove(EPassRestStoreClient.KEY_TOKEN)
		.remove(EPassRestStoreClient.KEY_ID_TOKEN).commit();
		SharedPreferenceManager.setUserName(this, null);

		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		Toast.makeText(this, R.string.error_token_expired, Toast.LENGTH_SHORT).show();
		finish();
    }

	public boolean isRegisterReceiver() {
		return registerReceiver;
	}

	public void setRegisterReceiver(boolean registerReceiver) {
		this.registerReceiver = registerReceiver;
	}
	
	 protected void showLoadingDialog() {
	    	if (loadingDialog != null && !loadingDialog.isShowing()) {
	    		loadingDialog.show();
	    	}
	    }
	    
	    protected void dismissLoadingDialog() {
	    	if (loadingDialog != null && loadingDialog.isShowing()) {
	    		loadingDialog.dismiss();
	    	}
	    }
}
