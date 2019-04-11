package com.esquel.epass.leave;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.schema.LeaveQuota;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.Language;
import com.esquel.epass.utils.NetworkUtils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.widget.DataAdapter;
import com.joyaether.datastore.widget.DataCache;

/**
 * List of leave type with the user's remaining day or hour of the leave
 * 
 * 
 */
public class AnnualLeaveTypeActivity extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	ListView listViewLeaveType;
	public static final int REQUEST_CODE_LEAVE_FORM = 101;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_leave_type);

		setUIData();		
	}
	
	/**
	 * check whether the field value is null & string type or not
	 * @param fieldValue DataElement variable 
	 * @return proper string
	 */
	String getFieldValue(DataElement fieldValue){
		if(fieldValue != null && fieldValue.isPrimitive()){
			
			return fieldValue.asPrimitiveElement().valueAsString(); 
		
		}
		return "";
	}
	
	
	/**
	 * set data to the UI when Activity is initialized
	 * 
	 */
	private void setUIData() {

		// Setting Visibility of Header Icons
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_first).setVisibility(View.INVISIBLE);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_activity_leave_type);

		listViewLeaveType = (ListView) findViewById(R.id.listview_leaveType);
		listViewLeaveType.setDividerHeight(0);
		findViewById(R.id.left_first).setOnClickListener(this);

		
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		final String username = pref.getString(Constants.USER_NAME, null);
		final String password = pref.getString(Constants.PASSWORD, null);
		
		if (!NetworkUtils.hasNetworkConnection(this)) {
	        Toast.makeText(AnnualLeaveTypeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
	        onBackPressed();
	        return;
	    }
		showLoadingDialog();
		final LeaveTypeAdapter adapter = new LeaveTypeAdapter(((AppApplication) getApplication()).getRestStore(),
				LeaveQuota.RESOURCE_NAME, new Query()
		.fieldIsEqualTo(
						LeaveQuota.PARAMETER_USERNAME_FIELD_NAME, username)
						.fieldIsEqualTo(
								LeaveQuota.PARAMETER_PASSWORD_FIELD_NAME,
								password)
						.fieldIsEqualTo(
								LeaveQuota.PARAMETER_LANGUAGE,
								Language.getLang())
								);
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				ListenableFuture<Long> future = adapter.getCache().getCount();
				Futures.addCallback(future, new FutureCallback<Long>() {
					@Override
					public void onSuccess(final Long result) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {	
								dismissLoadingDialog();
								listViewLeaveType.setAdapter(adapter);
								if (adapter.getViewTypeCount() == 0 ) {
									Toast.makeText(AnnualLeaveTypeActivity.this, R.string.fail_to_load_leave_type, Toast.LENGTH_SHORT).show();
						            onBackPressed();
						        	return;
								}
							}
												
						});
					}

					@Override
					public void onFailure(Throwable t) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {										
								dismissLoadingDialog();
								Toast.makeText(AnnualLeaveTypeActivity.this, R.string.fail_to_load_leave_type, Toast.LENGTH_SHORT).show();
					            onBackPressed();
							}
												
						});
					}
				});
			}
			
		}).start();
		
		
		listViewLeaveType.setOnItemClickListener(this);

	}

	
	/**
	 * 
	 * Holds the list of leave type corresponding to the user
	 *
	 */
	class LeaveTypeAdapter extends DataAdapter {
		
		private HashMap<String, String> map;
		private boolean isDataAvailable = false;

		public LeaveTypeAdapter(Store store, String schema, Query query) {
			super(new LeaveTypeDataCache(store, schema, query));
			map = new HashMap<String, String>();
			String[] mappingArray = getResources().getStringArray(R.array.leave_type_map);
			for (int i = 0; i < mappingArray.length; i++) {
				String mappingString = mappingArray[i];
				String[] mapping = mappingString.split(":");
				map.put(mapping[0], mapping[1]);
			}
		}
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected void onDataAvailable(DataElement data, View view) {
			dismissLoadingDialog();
			if (!isDataAvailable) {
				listViewLeaveType.setDividerHeight(1);
				isDataAvailable = true;
			}
			TextView type = (TextView) view.findViewById(R.id.leaveType);
			TextView remainingTime = (TextView) view.findViewById(R.id.timeRemaining);
			
			View arrow = view.findViewById(R.id.arrow);
			arrow.setVisibility(View.VISIBLE);
					
			type.setText(getFieldValue(data.asObjectElement().get(LeaveQuota.NAME_FIELD_NAME)));
			String remainTime = getFieldValue(data.asObjectElement().get(LeaveQuota.REMAINING_FIELD_NAME));
			String timeUnit = getFieldValue(data.asObjectElement().get(LeaveQuota.UNIT_FIELD_NAME));
			if (timeUnit == null || timeUnit.equals("") || timeUnit.equals("null")) {
				timeUnit = getString(R.string.hour);
			} else {
				timeUnit = timeUnit.equals(LeaveQuota.UNIT_FIELD_HOUR) ? getResources()
						.getString(R.string.hour) : getResources()
						.getString(R.string.day);
			}
			
			remainingTime.setText(remainTime + timeUnit);
			DataElement id = data.asObjectElement().get(LeaveQuota.LEAVE_TYPE_ID_FIELD_NAME);
			
			ImageView icon = (ImageView) view.findViewById(R.id.leaveIcon);
			int resId = 0;
			if (id != null && id.isPrimitive() && map.containsKey(id.asPrimitiveElement().valueAsString())) {
				resId = getResources().getIdentifier(map.get(id.asPrimitiveElement().valueAsString()), "drawable", getPackageName());
			} else {
				resId = R.drawable.icon_other;
			}
			icon.setBackgroundResource(resId);
			
		}

		@Override
		public View getInflatedView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(AnnualLeaveTypeActivity.this).inflate(R.layout.item_leave_type, parent, false);			
			}
			
			ImageView icon = (ImageView) view.findViewById(R.id.leaveIcon);
			icon.setBackgroundResource(0);
			
			View arrow = view.findViewById(R.id.arrow);
			arrow.setVisibility(View.INVISIBLE);

			return view;
			
		}
		protected void onError(Throwable t) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dismissLoadingDialog();
					Toast.makeText(AnnualLeaveTypeActivity.this, R.string.fail_to_load_leave_type, Toast.LENGTH_SHORT).show();
		            onBackPressed();					
				}
				
			});
			
		}
		
	}
	
	private class LeaveTypeDataCache extends DataCache {
		
		/** The cached size of this cache. */
		private Long cachedCount;
		
		/** The schema to retrieve data from in the {@link Store}. */
		private String schema;

		public LeaveTypeDataCache(Store store, String schema, Query query) {
			super(store, schema, query);
		}
		
		public ListenableFuture<Long> getCount() {
			if (cachedCount != null) {
				return Futures.immediateFuture(cachedCount);
			} else {			
				if (getStore() != null && getSchema() != null) {
					final SettableFuture<Long> future = SettableFuture.create();
					getStore().performQuery(getQuery(), getSchema(), new OAuthRestStoreCallback(AnnualLeaveTypeActivity.this) {
						
						@Override
						public void success(DataElement element, String resource) {
							if (element != null && element.isArray()) {
								try {
									cachedCount = (long) element.asArrayElement().size();
								} catch (Exception e) {
									future.setException(new DatastoreException("Failed to determine the size of cache."));
									return;
								}

								future.set(cachedCount);
							} else {
								future.setException(new DatastoreException("Failed to determine the size of cache."));
							}
						}
						
						@Override
						public void fail(DatastoreException ex, String resource) {
							future.setException(ex);
						}
						
					});
					return future;
				} else {
					return Futures.immediateFailedFuture(new DatastoreException("Failed to determine the size of cache. No store or schema specified"));
				}
			}
		}
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DataElement element = (DataElement) parent.getItemAtPosition(position);
		
		if (getCallingActivity() == null) {
			
			Intent intent = new Intent(AnnualLeaveTypeActivity.this,
					AnnualLeaveGenerationActivity.class);
			intent.putExtra(Constants.LEAVE_TYPE, element.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(intent, REQUEST_CODE_LEAVE_FORM);
			
		} else if (getCallingActivity() != null) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(Constants.LEAVE_TYPE, element.toString());
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_LEAVE_FORM && resultCode == Activity.RESULT_OK) {
			finish();
		}
	}  

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.left_first:
			onBackPressed();
			break;
		default:
			break;
		}
	}

}
