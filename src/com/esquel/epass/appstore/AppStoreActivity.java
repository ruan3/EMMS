package com.esquel.epass.appstore;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.restlet.data.Reference;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.esquel.epass.DownloadTask;
import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.delegate.DownloadCallback;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.oauth.OAuthToken;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationDownloadState;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.PackageUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.joyaether.datastore.Build;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;

public class AppStoreActivity extends BaseGestureActivity {
	
	private static final int PROGRESS_FINISH = 100;
	private static final int NOTIFICATION_ID = 1001;
	public static final String ACTION_APP_INSTALL = "com.esquel.epass.app.install";
	public static final int REQUEST_CODE_INSTALL_APP = 1003;
	public static final String EXTRA_OAUTH_TOKEN = "com.esquel.intent.extra.OAUTH_TOKEN";
	public static final String KEY_APP_CATEGORY = "app-category";
	private static final String EXTRA_RESULT_ITEM = "result-item";
	public static final String EXTRA_REMOVE_ITEM = "remove-item";
	private HashMap<Long, ObjectElement> subscribedItems = new HashMap<Long, ObjectElement>();
	private List<String> removeItems = new ArrayList<String>();
	
	protected boolean isNeedUpdate(ObjectElement element) {
		int versionCode = 0;
		String packageName = "";
		DataElement versionCodeElement = element.get(ApplicationVersion.BUILD_NUMBER_FIELD_NAME);
		if (versionCodeElement != null && versionCodeElement.isPrimitive()) {
			versionCode = versionCodeElement.asPrimitiveElement().valueAsInt();
		}
	
		DataElement e = element.get(ApplicationVersion.APPLICATION_FIELD_NAME).asObjectElement().get("identifier");
		if (e != null && e.isPrimitive()) {
			packageName = e.asPrimitiveElement().valueAsString();
		}
		
		//check the app is it installed 
		if (!PackageUtils.isPackageExist(this, packageName)) {
			return false;
		}
		int currentVersion = PackageUtils.getPackageVersionCode(this, packageName);
		return currentVersion < versionCode;
	}

	protected void download(final ObjectElement object) {
		if (object == null) {
			return;
		}
		String url = null;
		DataElement installUrl = object.get("installer_url");
		if (installUrl != null && installUrl.isPrimitive()) {
			url = installUrl.asPrimitiveElement().valueAsString();
		}
	
		int id = 0;
		DataElement idField = object.get("application/application_id");
		if (idField != null && idField.isPrimitive()) {
			id = idField.asPrimitiveElement().valueAsInt();
		}
		try {
			if (url != null && id != 0L) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showLoadingDialog();
					}
					
				});
				downloadNewVervion(id, url, isNeedUpdate(object), object);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		SharedPreferenceManager.setSubscribedApplicationDetail(this, id, object.toJson());
		
		
	}
	
	protected void downloadNewVervion(int id, String urlString, final boolean isUpdate, final ObjectElement application) throws MalformedURLException {
		JsonObjectElement downloadState = new JsonObjectElement();
		downloadState.set(ApplicationDownloadState.ID_FIELD_NAME, id);
		downloadState.set(ApplicationDownloadState.STATE_FIELD_NAME, ApplicationDownloadState.DonwloadState.Downloading.ordinal());
		((AppApplication) getApplication()).getSqliteStore().createElement(downloadState, EPassSqliteStoreOpenHelper.SCHEMA_APPLICATION_DOWNLOAD_STATE, null);
		Reference source = new Reference(urlString);
		final File file = new File(getExternalFilesDir(null), source.getLastSegment());
		final String fileName = file.getAbsolutePath();
		Reference destination = new Reference(fileName);
		DownloadTask downloadTask = new DownloadTask(this, source, destination);
		
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);	
	    
		NotificationCompat.Builder builder = getNotificationBuilder(isUpdate);
		builder.setProgress(PROGRESS_FINISH, PROGRESS_FINISH, true);
		builder.setContentText(getString(R.string.downloading))
			.setProgress(0, 0, true)
			.setContentText(isUpdate ? getString(R.string.app_downloading) : getString(R.string.app_downloading));
		
		
		Toast.makeText(this, R.string.downloading, Toast.LENGTH_SHORT).show();
		manager.notify(NOTIFICATION_ID, builder.build());
		downloadTask.setCallback(new DownloadCallback() {

			@Override
			public void success(boolean hasUpdate) {
				ListenableFuture<Boolean> future = addToUserChannel(application);
				Futures.addCallback(future, new FutureCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if (!result) {
							onFailure(null);
							return;
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissLoadingDialog();
								NotificationCompat.Builder builder = getNotificationBuilder(isUpdate);
								builder.setProgress(0, 0, false);
								builder.setContentText(getString(R.string.download_complete));
								builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
								TaskStackBuilder stackBuilder = TaskStackBuilder.create(AppStoreActivity.this);
								Intent intent = getInstallAppIntent(fileName);
							    stackBuilder.addParentStack(AppStoreActivity.this);
							    stackBuilder.addNextIntent(intent);
								PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
								builder.setContentIntent(pendingIntent);
								
								manager.notify(NOTIFICATION_ID, builder.build());
								if (AppStoreActivity.this != null) {
									Toast.makeText(AppStoreActivity.this, R.string.download_complete, Toast.LENGTH_SHORT).show();
								}
								startActivityForResult(intent, REQUEST_CODE_INSTALL_APP);
							}
					    	
					    });
					}

					@Override
					public void onFailure(Throwable arg0) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissLoadingDialog();
								NotificationCompat.Builder builder = getNotificationBuilder(isUpdate);
								builder.setProgress(0, 0, false);
								builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
								builder.setContentText(getString(R.string.app_download_failed));
								manager.notify(NOTIFICATION_ID, builder.build());
								if (AppStoreActivity.this != null) {
									Toast.makeText(AppStoreActivity.this, R.string.app_download_failed, Toast.LENGTH_SHORT).show();
								}
							}
					 });
					}
				});
			    
			}

			@Override
			public void fail(Exception e) {
				 runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dismissLoadingDialog();
							NotificationCompat.Builder builder = getNotificationBuilder(isUpdate);
							builder.setProgress(0, 0, false);
							builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
							builder.setContentText(getString(R.string.app_download_failed));
							manager.notify(NOTIFICATION_ID, builder.build());
							if (AppStoreActivity.this != null) {
								Toast.makeText(AppStoreActivity.this, R.string.app_download_failed, Toast.LENGTH_SHORT).show();
							}
						}
				 });
				
			}
			
		});
		downloadTask.download();
	}
	
	private Intent getInstallAppIntent(String fileName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
	    return intent;
	}
	
	protected Builder getNotificationBuilder(boolean isUpdate) {
		NotificationCompat.Builder builder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(android.R.drawable.stat_sys_download)
		        .setContentTitle(getString(R.string.app_download_title))
		        .setContentText(isUpdate ? getString(R.string.app_updating) : getString(R.string.app_downloading));
		return builder;
		
	}

	
    protected void openApp(String packageName) {
    	Intent intent = PackageUtils.getPackageIntent(this, packageName);
		if (intent != null) {			
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			String token = pref.getString(EPassRestStoreClient.KEY_TOKEN, null);
			String idToken = pref.getString(EPassRestStoreClient.KEY_ID_TOKEN, null);
			
			if (token != null && idToken != null) {
				OAuthToken t = OAuthToken.deserialize(token);
				t.setIdToken(idToken);
				intent.putExtra(LeaderBoardActivity.EXTRA_OAUTH_TOKEN, t.toJson());
			}
			startActivity(intent);
		} else {
			if (Build.DEBUG) {
				Log.e(getClass().getSimpleName(), "The package: " + packageName + " is not found");
			}
			Toast.makeText(this, R.string.can_not_launch_the_app, Toast.LENGTH_SHORT).show();
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == REQUEST_CODE_INSTALL_APP) {
    		onApplicationInstalled();
    	} else {
    		String result = data == null ? null : data.getStringExtra(EXTRA_RESULT_ITEM);
    		Intent intent = new Intent();
    		if (result  != null) {
    			intent.putExtra(EXTRA_RESULT_ITEM, result);
    		}
    		String[] resultArray = data == null ? null : data.getStringArrayExtra(EXTRA_REMOVE_ITEM);
    		if (resultArray  != null) {
    			intent.putExtra(EXTRA_REMOVE_ITEM, resultArray);
    		}
			setResult(Activity.RESULT_OK, intent);
    	}
    }
    
    protected void onApplicationInstalled() {
    	//Ignore
    }
    
    private ListenableFuture<Boolean> addToUserChannel(final ObjectElement application) {
    	final SettableFuture<Boolean> future = SettableFuture.create();
    	if (application == null) {
    		future.set(false);
    		return future;
    	}
    	getRestStore().count(new Query(), "user_channels", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isPrimitive()) {
					final JsonObjectElement object = new JsonObjectElement();
			    	object.set(UserChannel.ID_FIELD_NAME, UUID.randomUUID().toString());
			    	final DataElement app = application.get(ApplicationVersion.APPLICATION_FIELD_NAME);
			    	if (app != null && app.isObject()) {		    		
				    	DataElement e = app.asObjectElement().get(Application.ID_FIELD_NAME);
				    	if (e != null && e.isPrimitive()) {
					    	object.set(UserChannel.IDENTIFIER_FIELD_NAME, e.asPrimitiveElement().valueAsLong());
				    	}
				    	e = app.asObjectElement().get(Application.IDENTIFIER_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							object.set(UserChannel.BUNDLE_IDENTIFIER_FIELD_NAME, e.asPrimitiveElement().valueAsString());
						}
			    	} 
			    	object.set(UserChannel.TYPE_FIELD_NAME, UserChannel.TYPE_APPLICATION);
			    	object.set(UserChannel.SEQUENCE_FIELD_NAME, element.asPrimitiveElement().valueAsInt() + 1);
			    	int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);
			    	if (requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION) {
						DataElement e = app.asObjectElement()
								.get(Application.NAME_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							object.set(UserChannel.NAME_FIELD_NAME, e
									.asPrimitiveElement().valueAsString());
						}

						e = app.asObjectElement().get(
								Application.ICON_URL_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							object.set(UserChannel.IMAGE_URL_FIELD_NAME, e
									.asPrimitiveElement().valueAsString());
						}

						e = app.asObjectElement().get(Application.ID_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							subscribedItems.put(e.asPrimitiveElement()
									.valueAsLong(), object);
						}
						future.set(true);
						return;
			    	}
			    	
			    	getRestStore().createElement(object, resource, new StoreCallback() {

						@Override
						public void success(DataElement element, String resource) {
//							Intent intent = new Intent();
							if (element != null && element.isObject()) {
								ObjectElement resultObject = element.asObjectElement();					
								DataElement e = app.asObjectElement().get(Application.NAME_FIELD_NAME);
								if (e != null && e.isPrimitive()) {
									resultObject.set(UserChannel.NAME_FIELD_NAME, e.asPrimitiveElement().valueAsString());
								}
								e = app.asObjectElement().get(Application.ICON_URL_FIELD_NAME);
								if (e != null && e.isPrimitive()) {
									resultObject.set(UserChannel.IMAGE_URL_FIELD_NAME , e.asPrimitiveElement().valueAsString());
								}
								e = app.asObjectElement().get(Application.ID_FIELD_NAME);
								
								long id = 0l;
								if (e != null && e.isPrimitive() && e.asPrimitiveElement().isNumber()) {
									id = e.asPrimitiveElement().valueAsLong();								
								}
								subscribedItems.put(id, resultObject);
								future.set(true);
							}
						}

						@Override
						public void failure(DatastoreException ex,
								String resource) {
							Log.e(AppStoreActivity.this.getClass().getSimpleName(), ex.getMessage());
							DataElement e = app.asObjectElement()
								.get(Application.NAME_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								object.set(UserChannel.NAME_FIELD_NAME, e
									.asPrimitiveElement().valueAsString());
							}

							e = app.asObjectElement().get(
									Application.ICON_URL_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								object.set(UserChannel.IMAGE_URL_FIELD_NAME, e
									.asPrimitiveElement().valueAsString());
							}

							e = app.asObjectElement().get(Application.ID_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								subscribedItems.put(e.asPrimitiveElement()
									.valueAsLong(), object);
							}
							future.set(true);
						}
			    		
			    	});

				}
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				Log.e(AppStoreActivity.this.getClass().getSimpleName(), ex.getMessage());
				future.setException(ex);
			}
    		
    	});
    	return future;
    }
    
    protected HashMap<Long, ObjectElement> getSubscibredItems() {
    	return subscribedItems;
    }
    
    protected List<String> getRemoveItems() {
    	return removeItems;
    }
    
    @Override
    public void onBackPressed() {
    	Intent intent = new Intent();
    	boolean hasChanges = false;
    	if (subscribedItems.size() > 0) {
			JsonArrayElement array = new JsonArrayElement(new JsonArray());
			for (Long applicationId : subscribedItems.keySet()) {
				ObjectElement object = subscribedItems.get(applicationId);
				if (object != null) {
					array.add(object);
				}
			}
			hasChanges = true;
			intent.putExtra(EXTRA_RESULT_ITEM, array.toJson());
			
		}
    	if (removeItems.size() > 0) {
    		String[] removeItemArray = new String[removeItems.size()];
    		try {
    			for (int i = 0; i < removeItems.size(); i++) {
        			removeItemArray[i] = removeItems.get(i);
        		}
        		intent.putExtra(EXTRA_REMOVE_ITEM, removeItemArray);
        		hasChanges = true;
    		} catch (Exception e) {
    			
    		}
    		
    	}
    	if (hasChanges) {
    		setResult(Activity.RESULT_OK, intent);
    	}
		super.onBackPressed();
    }
    
}
