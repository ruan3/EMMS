package com.esquel.epass.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.esquel.epass.oauth.EPassRestStoreClient;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.oauth.OAuthToken;
import com.joyaether.datastore.rest.oauth.Token;
import com.joyaether.datastore.schema.Query;

/**
 * 
 * @author joyaether
 * 
 */
public class MainActivity extends BaseGestureActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String tokenString = pref.getString(EPassRestStoreClient.KEY_TOKEN, null);

		if (tokenString == null) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			return;
		}

		Token token = OAuthToken.deserialize(tokenString);
		if (token == null) {
			startActivity(new Intent(this, HomeActivity.class));
		} else {
			startActivity(new Intent(this, ChannelListFlipActivity.class));
		}
		finish();
	}
	
	private void checkTokenIsExpired() {
		showLoadingDialog();
		((AppApplication) getApplication()).getRestStore().count(new Query(), "tasks", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						startActivity(new Intent(MainActivity.this, ChannelListFlipActivity.class));
						finish();
					}
					
				});
				
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						startActivity(new Intent(MainActivity.this, HomeActivity.class));
						finish();
					}
					
				});
				
			}
			
		});
	}

}
