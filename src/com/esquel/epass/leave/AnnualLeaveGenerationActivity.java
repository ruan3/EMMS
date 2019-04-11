package com.esquel.epass.leave;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.LeaveGenerateAdapter;
import com.esquel.epass.item.ItemLeaveGenerationDetails;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.oauth.TokenUtils;
import com.esquel.epass.schema.Leave;
import com.esquel.epass.schema.LeaveQuota;
import com.esquel.epass.schema.LeaveRemaining;
import com.esquel.epass.ui.AnimatedExpandableListView;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.Language;
import com.esquel.epass.utils.NetworkUtils;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;

/**
 * A list to show the data for apply new leave.
 * 
 */
public class AnnualLeaveGenerationActivity extends BaseGestureActivity
		implements OnClickListener {

	/**
	 * Position of leave type node in a list <br>
	 * Value is 2
	 */
	private static final int POSITION_LEAVE_TYPE_ACTIVITY = 2;
	
	/**
	 * Position of leave remaining node in a list <br>
	 * Value is 3
	 */
	private static final int POSITION_LEAVE_REMAINING = 3;

	/**
	 * Position of apply date node in a list <br>
	 * Value is 1
	 */
	private static final int POSITION_APPLY_DATE = 1;

	/**
	 * Position of start date node in a list <br>
	 * Value is 4
	 */
	private static final int POSITION_START_DATE = 4;

	/**
	 * Position of end date node in a list <br>
	 * Value is 5
	 */
	private static final int POSITION_END_DATE = 5;

	/**
	 * Position of leave count node in a list <br>
	 * Value is 6
	 */
	private static final int POSITION_LEAVE_COUNT = 6;
	
	/**
	 * Minimum date for date picker <br>
	 * 
	 */
	public static Date MIN_DATE;
	
	/**
	 * First time initializing this activity
	 */
	private static boolean CHECK_FIRST_TIME = true;
	
	/**
	 * Count of leave in the list <br>
	 * Value is 0
	 */
	private static double LEAVE_COUNT = 0;
	
	/**
	 * First character of the unit <br>
	 * Value is 0
	 */
	private static final int FIRST_CHARACTER_UNIT = 0;
	/**
	 * Holds the list of detail required to apply new leave
	 */
	AnimatedExpandableListView listViewLeaveDetail;
	Activity activity;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	
	/**
	 * 
	 * Integer code work as identifier in response of onActivityResult. <br>
	 * value is 1.
	 */
	public static final int REQUEST_CODE = 1;
	ArrayList<ItemLeaveGenerationDetails> listDetail;
	LeaveGenerateAdapter adapter;

	DataElement elementLeaveType;

	private static final int[] QUARTER = {0, 15, 30, 45};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_leave_generation);
		setUIData();
		
	}

	/**
	 * set data to the UI when Activity is initialized
	 * 
	 */
	private void setUIData() {

		// Setting Visibility of Header Icons
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		// findViewById(R.id.left_first).setVisibility(View.INVISIBLE);
		// findViewById(R.id.right_first).setVisibility(View.INVISIBLE);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_activity_leave_type);

		TextView submitLeaveDetail = (TextView) findViewById(R.id.right_first);
		submitLeaveDetail.setOnClickListener(this);
		
		findViewById(R.id.left_first).setOnClickListener(this);

		listViewLeaveDetail = (AnimatedExpandableListView) findViewById(R.id.listview_leaveDetail);
		listViewLeaveDetail.setGroupIndicator(null);
		activity = AnnualLeaveGenerationActivity.this;

		listDetail = new ArrayList<ItemLeaveGenerationDetails>();
		String[] arrayKey = getResources().getStringArray(
				R.array.leave_details_key);
		String[] arrayKeyName = getResources().getStringArray(
				R.array.leave_details_key_name);

		for (int i = 0; i < arrayKey.length; i++) {
			ItemLeaveGenerationDetails item = new ItemLeaveGenerationDetails();
			item.setLeaveDetailKey(arrayKey[i]);
			item.setLeaveDetailKeyName(arrayKeyName[i]);
			switch (i) {
				case POSITION_APPLY_DATE:					
				case POSITION_START_DATE:
				case POSITION_END_DATE:
					Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
					Date defaultDate = calendar.getTime();					
					SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
				    formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
					String defaultValue = "";

				    defaultValue = formatter.format(calendar.getTime());
					item.setLeaveDetailValue(defaultValue);
					if( CHECK_FIRST_TIME ){
						CHECK_FIRST_TIME = false;
						MIN_DATE  = defaultDate;
					}
					if (i == POSITION_APPLY_DATE) {
						break;
					}
					
					calendar.setTime(defaultDate);
					int currentMinutes = calendar.get(Calendar.MINUTE);
					for (int m : QUARTER) {
						if (currentMinutes <= m) {
							calendar.set(Calendar.MINUTE, m);
							break;
						}
						if (currentMinutes > 45) {
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
							break;
						}
					}
				    defaultValue = formatter.format(calendar.getTime());
					item.setLeaveDetailValue(defaultValue);
					
					break;
				case POSITION_LEAVE_COUNT:
					
					item.setLeaveDetailValue(LEAVE_COUNT + "");
					break;
				case 0:    	
					EPassRestStoreClient client = (EPassRestStoreClient) ((AppApplication) getApplication()).getRestStore().getClient();
					String empNumber = TokenUtils.getEmployeeNumber(client.getToken());
					if (empNumber != null && empNumber.equals("")) {
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(this);
						String username = pref.getString("code", null);
						if (username != null) {
							empNumber = username;
						}
					}
					
					if (empNumber == null) {
						empNumber = "";
					}
					item.setLeaveDetailValue(empNumber);
					break;
				default:
					item.setLeaveDetailValue("");
					break;
			}
			
			listDetail.add(item);
		}

		if (listDetail.size() != 0) {
			adapter = new LeaveGenerateAdapter(this, listDetail);
			listViewLeaveDetail.setAdapter(adapter);
		}

//		adapter = new LeaveGenerateAdapter(this, listDetail);
//		listViewLeaveDetail.setAdapter(adapter);
		listViewLeaveDetail
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						if (groupPosition == POSITION_LEAVE_TYPE_ACTIVITY) {
							Intent intent = new Intent(
									AnnualLeaveGenerationActivity.this,
									AnnualLeaveTypeActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivityForResult(intent, REQUEST_CODE);
							listViewLeaveDetail.collapseGroup(groupPosition);
							return;
						}
						if (groupPosition == POSITION_START_DATE || 
								groupPosition == POSITION_END_DATE) {
							findViewById(R.id.right_first).setEnabled(false);
							for (int i = 0; i < adapter.getGroupCount(); i++) {
								if (listViewLeaveDetail.isGroupExpanded(i)
										&& i != groupPosition) {
									listViewLeaveDetail.collapseGroup(i);
								}
							}		
						}
									
					}

				});
		listViewLeaveDetail.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				if (groupPosition == POSITION_START_DATE || 
						groupPosition == POSITION_END_DATE) {
					findViewById(R.id.right_first).setEnabled(true);
				}
			}
			
		});
		
		try{
				Bundle bundle = getIntent().getExtras();
				String leaveType = bundle.getString(Constants.LEAVE_TYPE);
				Log.e("Detail",""+leaveType);
				setLeaveType(leaveType);
		} catch(Exception e) {	
			e.printStackTrace();
		}
	}
	
	/**
	 * set leave type received from {@link AnnualLeaveTypeActivity} in the list 
	 * @param leaveType contains detail of leave
	 */
	void setLeaveType(String leaveType){
		
		elementLeaveType = new JsonObjectElement(leaveType);
		listDetail.get(POSITION_LEAVE_TYPE_ACTIVITY)
				.setLeaveDetailValue(getFieldValueString(
						elementLeaveType.asObjectElement()
								.get(LeaveQuota.NAME_FIELD_NAME))
								);
		String timeUnit = getFieldValueString(elementLeaveType.asObjectElement().get(LeaveQuota.UNIT_FIELD_NAME));
		if (timeUnit == null || timeUnit.equals("") || timeUnit.equals("null")) {
			timeUnit = getString(R.string.hour);
		} else {
			timeUnit = timeUnit.equals(LeaveQuota.UNIT_FIELD_HOUR) ? getResources()
					.getString(R.string.hour) : getResources()
					.getString(R.string.day);
		}
		listDetail.get(POSITION_LEAVE_REMAINING). 	
		setLeaveDetailValue(getFieldValueDouble(elementLeaveType.asObjectElement()
				.get(LeaveQuota.REMAINING_FIELD_NAME))
				+ timeUnit);

		
		adapter.notifyDataSetChanged();
		getRemainingLeave();
	}
	
	/*
	 * returns data on finishing child activity
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String leaveType = data.getStringExtra(Constants.LEAVE_TYPE);
				setLeaveType(leaveType);
			}
		}
	}

	/**
	 * Used to get date of particular cell in a list(Ex: Start Date )
	 * 
	 * @param pos
	 *            of the item in the adapter/list
	 * @return date at the current position
	 */
	long getDateTime(int pos) {
		try {
			ItemLeaveGenerationDetails detail = (ItemLeaveGenerationDetails) adapter
					.getGroup(pos);
			String str_date = detail.getLeaveDetailValue();
			DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
			Date date = (Date) formatter.parse(str_date);
			long endDate = date.getTime();
			return endDate;
		} catch (ParseException e) {
		}
		return pos;
	}

	/**
	 * Get remaining leave count from the server
	 */
	public void getRemainingLeave() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		showLoadingDialog();
		
		String username = pref.getString(Constants.USER_NAME, null);
		String password = pref.getString(Constants.PASSWORD, null);
		
		long startDate = getDateTime(POSITION_START_DATE); //date.getTime();
		long endDate = getDateTime(POSITION_END_DATE);//date1.getTime();
//		Log.e("startDate", "" + startDate);
//		Log.e("EndDate", "" + endDate);
//		Log.e("Name & pass", "" + username + " " + password);
		((AppApplication) getApplication())
				.getRestStore()
				.performQuery(
						new Query()
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_USERNAME_FIELD_NAME,
										username)
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_PASSWORD_FIELD_NAME,
										password)
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_START_DATE_FIELD_NAME,
										startDate)
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_END_DATE_FIELD_NAME,
										endDate)
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_LEAVE_TYPE,
										getFieldValueLong(elementLeaveType.asObjectElement().get(LeaveQuota.LEAVE_TYPE_ID_FIELD_NAME)))
								.fieldIsEqualTo(
										LeaveRemaining.PARAMETER_LANGUAGE,
										Language.getLang()),
										LeaveRemaining.RESOURCE_NAME,
						new OAuthRestStoreCallback(this) {
							@Override
							public void success(final DataElement element,
									String resource) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {

										dismissLoadingDialog();
										enableSubmitButton(true);
//										Toast.makeText(AnnualLeaveGenerationActivity.this, "Success", Toast.LENGTH_SHORT).show();
										if (element != null && element.isObject()) {
											double leaveCount = element.asObjectElement().get(LeaveRemaining.RESPONSE_COUNT).asPrimitiveElement().valueAsDouble();
											LEAVE_COUNT = leaveCount;
											String unit = "";
											if (elementLeaveType != null && elementLeaveType.isObject()) {
												DataElement unitField = elementLeaveType.asObjectElement()
														.get(LeaveQuota.UNIT_FIELD_NAME);
												if (unitField != null && unitField.isPrimitive()) {
													String unitFieldString = unitField.asPrimitiveElement().valueAsString();
													if (unitFieldString == null || unitFieldString.equals("") || unitFieldString.equals("null")) {
														unit = getResources().getString(R.string.hour);
													} else {
														unit = unitFieldString .equals(LeaveQuota.UNIT_FIELD_HOUR) ? getResources()
																.getString(R.string.hour) : getResources()
																.getString(R.string.day);
													}
												}												
											}
											DecimalFormat df = new DecimalFormat("0.00");
											String formate = df.format(LEAVE_COUNT); 
											listDetail.get(POSITION_LEAVE_COUNT).setLeaveDetailValue(formate + unit);

											adapter.notifyDataSetChanged();
										}

									}
								});

							}

							@Override
							public void fail(final DatastoreException ex,
									String resource) {

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dismissLoadingDialog();
										enableSubmitButton(false);
										Handler handle = new Handler();
										handle.post(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(AnnualLeaveGenerationActivity.this, ex.getMessage(),
														Toast.LENGTH_SHORT).show();										
											}
											
										});							
									}
								});
							}

						});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_first:
			
			
			submitLeaveDetails();
			break;

		case R.id.left_first:
			onBackPressed();
			break;
		default:
			break;
		}

	}

	/**
	 * Submit the detail of the user generated leave to the server
	 * 
	 */
	private void submitLeaveDetails() {
		 if (!NetworkUtils.hasNetworkConnection(this)) {
	        	Toast.makeText(AnnualLeaveGenerationActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
	            onBackPressed();
	        	return;
	     }
		showLoadingDialog();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		JsonObjectElement jsonObject = new JsonObjectElement();

		String username = pref.getString(Constants.USER_NAME, null);
		String password = pref.getString(Constants.PASSWORD, null);

		long startDate = getDateTime(POSITION_START_DATE); //date.getTime();
		long endDate = getDateTime(POSITION_END_DATE); 
		float duration = (float) LEAVE_COUNT;

		jsonObject.set(Leave.PARAMETER_USRENAME_FIELD, username);
		jsonObject.set(Leave.PARAMETER_PASSWORD_FIELD, password);
		jsonObject.set(
				Leave.PARAMERTER_LEAVE_TYPE_ID_FIELD_NAME,
				getFieldValueLong(elementLeaveType.asObjectElement().get(
						LeaveQuota.LEAVE_TYPE_ID_FIELD_NAME)));
		jsonObject.set(Leave.PARAMETER_LEAVE_START_DATE, startDate);
		jsonObject.set(Leave.PARAMETER_LEAVE_END_DATE, endDate);
		jsonObject.set(Leave.PARAMETER_LENGTH_FIELD_NAME, duration);
		jsonObject.set(
				Leave.PARAMETER_UNIT_FIELD_NAME,
				getFieldValueString(
						elementLeaveType.asObjectElement().get(
								LeaveQuota.UNIT_FIELD_NAME)).charAt(FIRST_CHARACTER_UNIT));    // first character of the Unit
		jsonObject.set(
				Leave.PARAMETER_QUOTA_FIELD_NAME,
				getFieldValueDouble(elementLeaveType.asObjectElement().get(
						LeaveQuota.REMAINING_FIELD_NAME)));
		jsonObject.set(Leave.PARAMETER_LANGUAGE, ""+Language.getLang());

		((AppApplication) getApplication()).getRestStore().createElement(
				jsonObject, Leave.RESOURCE_NAME, new OAuthRestStoreCallback(this) {
					@Override
					public void success(final DataElement element,
							String resource) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissLoadingDialog();
								enableSubmitButton(true);
								Handler handle = new Handler();
								handle.post(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(
												activity,
												getResources()
														.getString(
																R.string.success_in_leave_submission),
												Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_OK);
										finish();
									}
									
								});							
							}
						});

					}

					@Override
					public void fail(final DatastoreException ex, String resource) {
						ex.printStackTrace();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissLoadingDialog();
								enableSubmitButton(false);
								Handler handle = new Handler();
								handle.post(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(
												activity,
												ex.getMessage(),
												Toast.LENGTH_SHORT).show();										
									}
									
								});
							}
						});
					}

				});

	}

	/**
	 * check whether the field value is null or not
	 * 
	 * @param fieldValue
	 *            DataElement variable
	 * @return proper string
	 */
	String getFieldValueString(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsString();

		}
		return "";
	}

	/**
	 * check whether the field value is null or not
	 * 
	 * @param fieldValue
	 *            DataElement variable
	 * @return proper double
	 */
	double getFieldValueDouble(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsDouble();

		}
		return 0;
	}

	/**
	 * check whether the field value is null or not
	 * 
	 * @param fieldValue
	 *            DataElement variable
	 * @return proper Long
	 */
	long getFieldValueLong(DataElement fieldValue) {
		if (fieldValue != null && fieldValue.isPrimitive()) {

			return fieldValue.asPrimitiveElement().valueAsNumber().longValue();

		}
		return 0;
	}
	
	@Override
	public void onBackPressed() {
		if (listViewLeaveDetail.isGroupExpanded(POSITION_START_DATE)) {
			listViewLeaveDetail.collapseGroup(POSITION_START_DATE);
			return;
		} 
		
		if (listViewLeaveDetail.isGroupExpanded(POSITION_END_DATE)) {
			listViewLeaveDetail.collapseGroup(POSITION_END_DATE);
			return;
		}
		
		super.onBackPressed();

	}
	
	/**
	 * Control the submit button status base on is it success to connect server. 
	 * @param enable
	 */
	private void enableSubmitButton(boolean enable) {	
		TextView submitLeaveDetail = (TextView) findViewById(R.id.right_first);
		if (submitLeaveDetail != null) {
			submitLeaveDetail.setEnabled(enable);
		}
	}
	
}
