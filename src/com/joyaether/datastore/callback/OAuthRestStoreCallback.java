package com.joyaether.datastore.callback;

import android.content.Context;
import android.content.Intent;

import com.esquel.epass.activity.BaseGestureActivity;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.exception.NotAuthorizedException;

public abstract class OAuthRestStoreCallback implements StoreCallback {
	
	private Context mContext;
	public OAuthRestStoreCallback(Context context) {
		setContext(context);
	}

	@Override
	public void failure(DatastoreException ex, String resource) {
		if (ex.getCause() instanceof NotAuthorizedException) {
			tokenExpired();
		} 
		else {
			fail(ex, resource);
		}
	}
	
	private void tokenExpired() {
		Intent intent = new Intent(BaseGestureActivity.ACTION_TOKEN_EXPIRED);
		getContext().sendBroadcast(intent);
	}
	
	public abstract void fail(DatastoreException ex, String resource);

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
}
