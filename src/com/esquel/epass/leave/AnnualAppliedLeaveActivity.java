package com.esquel.epass.leave;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.AppliedLeaveAdapter;
import com.esquel.epass.item.ItemAppliedLeave;
import com.esquel.epass.schema.IsStaff;
import com.esquel.epass.schema.LeavesApplicationStatus;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.Language;
import com.google.gson.JsonArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.schema.Query;

/**
 * Populating List of applied leave by user
 * 
 * 
 * 
 */
public class AnnualAppliedLeaveActivity extends BaseGestureActivity implements
		OnClickListener, OnItemClickListener {

	private String TAG_SELECTED = "all";
	private static final String TAG_ALL = "all";
	private static final String TAG_TO_BE_APPROVED = "toBeApproved";
	private static final String TAG_APPROVED = "approved";
	private static final String TAG_REJECTED = "rejected";

	public static final String REJECTION_REASON_VALUE = "No reason";
	public static final String KEY_LEAVE_REJECT_REASON = "reject_reason";

	/**
	 * Holds data element
	 */
	DataElement all, toBeApproved, rejected, approved;

	TextView allLeaves, approveLeaves, toBeApproveLeaves, rejectedLeaves;

	View selectedView;
	PullToRefreshListView appliedListView; // PullToRefreshListView for
											// populating applied
											// leave
	AppliedLeaveAdapter adapter;
	List<ItemAppliedLeave> appliedLeavelist;

	TextView bottomText;
	ImageView bottomImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_applied_leave);

		setUIData();

		// Setting data in List to populate All type of Leaves(Approved,To be
		// approved & rejected)
	}

	/**
	 * set data to the UI when Activity is initialized
	 * 
	 */
	private void setUIData() {

		selectedView = findViewById(R.id.allLeaves);

		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		appliedListView = (PullToRefreshListView) findViewById(R.id.listview_);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_activity_leave_list);

		ImageButton generateLeave = ((ImageButton) findViewById(R.id.right_first));
		generateLeave.setImageResource(R.drawable.icon_plus);
		generateLeave.setOnClickListener(this);
		findViewById(R.id.left_first).setOnClickListener(this);

		allLeaves = (TextView) findViewById(R.id.allLeaves);
		approveLeaves = (TextView) findViewById(R.id.approved);
		toBeApproveLeaves = (TextView) findViewById(R.id.toBeApproved);
		rejectedLeaves = (TextView) findViewById(R.id.rejected);
		bottomText = (TextView) findViewById(R.id.leave_bottom_text);
		bottomImage = (ImageView) findViewById(R.id.leave_bottom_icon);

		bottomText.setOnClickListener(this);
		bottomImage.setOnClickListener(this);

		appliedListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				try {
					showLoadingDialog();
					getLeaveStatus();
				} catch (ParseException e) {
				}
			}
		});

		isStaff();

	}

	/**
	 * Make {@link IsStaff#RESOURCE_NAME } call to the server. <br/>
	 * If comes true than go back to {@link AnnualAppliedLeaveActivity} because
	 * staff cannot apply a new leave. <br/>
	 * Otherwise select leave type & apply new leave.
	 */
	private void isStaff() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		showLoadingDialog();
		String password = pref.getString(Constants.PASSWORD, null);
		((AppApplication) getApplication()).getRestStore().performQuery(
				new Query().fieldIsEqualTo(IsStaff.PARAMETER_PASSWORD_FIELD,
						password).fieldIsEqualTo(IsStaff.PARAMETER_LANGUAGE,
						Language.getLang()), IsStaff.RESOURCE_NAME,
				new OAuthRestStoreCallback(this) {
					@Override
					public void success(final DataElement element,
							String resource) {
						try {
							getLeaveStatus();
						} catch (ParseException e) {
						}

					}

					@Override
					public void fail(final DatastoreException ex,
							String resource) {
						checkIsSupervisor(new StoreCallback() {

							@Override
							public void success(DataElement element,
									String resource) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										goToApproveLeave();										
									}
									
								});
							}

							@Override
							public void failure(DatastoreException ex,
									String resource) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dismissLoadingDialog();
										Toast.makeText(getApplicationContext(),
												R.string.is_staff_error,
												Toast.LENGTH_SHORT).show();
										onBackPressed();
									}
								});
							}});				
					}
				});
	}

	@Override
	public void onRestart() {
		super.onRestart();
		try {
			showLoadingDialog();
			getLeaveStatus();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to get status of different type of leaves of user from server
	 * 
	 * @throws ParseException
	 */
	private void getLeaveStatus() throws ParseException {
		Log.e("getLeaveStatus", " " + "Success");
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = pref.getString(Constants.USER_NAME, null);
		String password = pref.getString(Constants.PASSWORD, null);

		// String str_date = "01-01-2015";
		// DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		// Date date = (Date) formatter.parse(str_date);
		// str_date = "05-01-2015";
		// Date date1 = (Date) formatter.parse(str_date);
		// System.out.println("Today is " +);

		// long startDate = date.getTime();
		// long endDate = date1.getTime();
		Date currentDate = new Date();
		long startDate = 1420041600000L;
		long threeMonth = 7889238000L;
		long endDate = currentDate.getTime();
		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeZone(TimeZone
//				.getTimeZone(((AppApplication) getApplication())
//						.getDefaultTimeZone()));
//		calendar.setTime(currentDate);
//		calendar.set(Calendar.MONTH, Calendar.JANUARY);
//		calendar.set(Calendar.DAY_OF_MONTH, 1);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
		startDate = calendar.getTimeInMillis();

		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DATE, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		endDate = calendar.getTimeInMillis();
		startDate = startDate - threeMonth;
		Log.e("EndDate", "" + endDate);
		Log.e("Name & pass", "" + username + " " + password);
		((AppApplication) getApplication())
				.getRestStore()
				.performQuery(
						new Query()
								.fieldIsEqualTo(
										LeavesApplicationStatus.PARAMETER_USERNAME_FIELD_NAME,
										username)
								.fieldIsEqualTo(
										LeavesApplicationStatus.PARAMETER_PASSWORD_FIELD_NAME,
										password)
								.fieldIsEqualTo(
										LeavesApplicationStatus.PARAMETER_START_DATE_FIELD_NAME,
										startDate)
								.fieldIsEqualTo(
										LeavesApplicationStatus.PARAMETER_END_DATE_FIELD_NAME,
										endDate)
								.fieldIsEqualTo(
										LeavesApplicationStatus.PARAMETER_LANGUAGE,
										Language.getLang()),
						LeavesApplicationStatus.RESOURCE_NAME,
						new OAuthRestStoreCallback(this) {
							@Override
							public void success(final DataElement element,
									String resource) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										checkIsSupervisor(new OAuthRestStoreCallback(AnnualAppliedLeaveActivity.this) {

											@Override
											public void success(
													DataElement e,
													String resource) {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														dismissLoadingDialog();
														bottomText.setVisibility(View.VISIBLE);
														bottomImage.setVisibility(View.VISIBLE);
														appliedListView.onRefreshComplete();
														if (element != null
																&& element.isArray()) {
															all = element;
														} else {
															all = new JsonArrayElement(
																	new JsonArray());
														}
														setLeaveResponse(all);
													}
												});
											}

											@Override
											public void fail(
													DatastoreException ex,
													String resource) {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														dismissLoadingDialog();
														bottomText.setVisibility(View.GONE);
														bottomImage.setVisibility(View.GONE);
														appliedListView.onRefreshComplete();
														if (element != null
																&& element.isArray()) {
															all = element;
														} else {
															all = new JsonArrayElement(
																	new JsonArray());
														}
														setLeaveResponse(all);
													}
												});
											}
											
										});
										
									}

								});

							}

							@Override
							public void fail(final DatastoreException ex,
									String resource) {
		                    	Log.e("AnnualAppliedLeaveActivity", ex.getMessage());
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dismissLoadingDialog();
										Toast.makeText(
												AnnualAppliedLeaveActivity.this,
												ex.getMessage(),
												Toast.LENGTH_SHORT).show();
										appliedListView.onRefreshComplete();
										all = new JsonArrayElement(
												new JsonArray());
										setLeaveResponse(all);
									}
								});
							}

						});
	}

	/**
	 * setting applied leave response from the server to the list
	 * 
	 * @param element
	 */
	private void setLeaveResponse(DataElement element) {
		all = element;
		JsonArrayElement arrayRejected = new JsonArrayElement(new JsonArray());
		JsonArrayElement arrayApproved = new JsonArrayElement(new JsonArray());
		JsonArrayElement arrayToBeApproved = new JsonArrayElement(
				new JsonArray());

		JsonArrayElement arrayAllLeaves = new JsonArrayElement(new JsonArray());

		for (int i = 0; i < element.asArrayElement().size(); i++) {
			int status = element.asArrayElement().get(i).asObjectElement()
					.get(LeavesApplicationStatus.RESPONSE_STATUS)
					.asPrimitiveElement().valueAsInt();
			if (status == LeavesApplicationStatus.RESPONSE_REJECTED
					|| status == LeavesApplicationStatus.RESPONSE_OTHER_REJECTED_STATUS
					|| status == LeavesApplicationStatus.RESPONSE_OTHER_REJECTED) {

				arrayRejected.add(element.asArrayElement().get(i)
						.asObjectElement());

			} else if (status == LeavesApplicationStatus.RESPONSE_APPROVED) {

				arrayApproved.add(element.asArrayElement().get(i)
						.asObjectElement());
			} else if (status == LeavesApplicationStatus.RESPONSE_TO_BE_APPROVED) {

				arrayToBeApproved.add(element.asArrayElement().get(i)
						.asObjectElement());

			}
			
			if (status != LeavesApplicationStatus.RESPONSE_OTHER_TO_BE_APPROVED) {
				arrayAllLeaves.add(element.asArrayElement().get(i).asObjectElement());
			}
		}
		
		all = arrayAllLeaves;
		rejected = arrayRejected;
		approved = arrayApproved;
		toBeApproved = arrayToBeApproved;

		// Count of Leaves to be Append in the tab header
		allLeaves.setText(getString(R.string.leave_all) + "\n"
				+ all.asArrayElement().size());
		toBeApproveLeaves.setText(getString(R.string.leave_tobeapprove) + "\n"
				+ toBeApproved.asArrayElement().size());
		approveLeaves.setText(getString(R.string.leave_approved) + "\n"
				+ approved.asArrayElement().size());
		rejectedLeaves.setText(getString(R.string.leave_rejected) + "\n"
				+ rejected.asArrayElement().size());

		selectTab(selectedView); // Selected current tab

	}

	/**
	 * Populating list of all applied leaves in the list
	 * 
	 * @param element
	 */
	void setAllLeaveData(DataElement element) {

		adapter = new AppliedLeaveAdapter(this, all);
		adapter.notifyDataSetChanged();
		appliedListView.setAdapter(adapter);
		appliedListView.setOnItemClickListener(this);

	}

	/**
	 * Populating list of all applied leaves that are waiting for approval
	 */
	void setToBeApprovedLeaveData() {
		if (toBeApproved != null) {
			adapter = new AppliedLeaveAdapter(this, toBeApproved);
			appliedListView.setAdapter(adapter);
		}
	}

	/**
	 * Populating list of all applied leaves that are approved
	 */
	void setApprovedLeaveData() {
		if (approved != null) {
			adapter = new AppliedLeaveAdapter(this, approved);
			appliedListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Populating list of all applied leaves that are rejected
	 */
	void setRejectedLeaveData() {

		if (rejected != null) {
			adapter = new AppliedLeaveAdapter(this, rejected);
			appliedListView.setAdapter(adapter);
		}
	}

	/**
	 * 
	 * Make a selection of current tab & populate data according to it.
	 * 
	 * @param view
	 *            Current view which is selected/clicked in a Tab View
	 */
	public void selectTab(View view) {
		String tag = view.getTag().toString();
		// if (tag.equals(TAG_SELECTED)) {
		// return;
		// }
		selectedView.setBackgroundColor(getResources().getColor(
				R.color.transparent));
		TextView tv = ((TextView) selectedView);
		tv.setTextColor(getResources().getColor(R.color.red_default));
		TAG_SELECTED = tag;

		if (TAG_ALL.equals(TAG_SELECTED)) {

			setAllLeaveData(all);

		} else if (TAG_TO_BE_APPROVED.equals(TAG_SELECTED)) {

			setToBeApprovedLeaveData();

		} else if (TAG_APPROVED.equals(TAG_SELECTED)) {

			setApprovedLeaveData();

		} else if (TAG_REJECTED.equals(TAG_SELECTED)) {

			setRejectedLeaveData();

		}

		view.setBackgroundColor(getResources().getColor(R.color.red_default));
		if (view instanceof TextView) {
			TextView tv2 = ((TextView) view);
			tv2.setTextColor(getResources().getColor(R.color.white));
		}
		selectedView = view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_first:
			Intent intent = new Intent(AnnualAppliedLeaveActivity.this,
					AnnualLeaveTypeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);

			break;
		case R.id.left_first:
			onBackPressed();
			break;

		case R.id.leave_bottom_icon:

		case R.id.leave_bottom_text:
			
			goToApproveLeave();
			break;

		default:
			break;
		}

	}

	private void goToApproveLeave() {
		Intent intent1 = new Intent(AnnualAppliedLeaveActivity.this,
				AnnualLeaveLeaderListActivity.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent1);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		DataElement e = (DataElement) parent.getAdapter().getItem(position);
		// just put empty value for now
		String rejectReason = "";
		if (e != null && e.isObject()) {
			DataElement status = e.asObjectElement().get(
					LeavesApplicationStatus.RESPONSE_STATUS);
			if (status != null && status.isPrimitive()) {
				if (LeavesApplicationStatus.RESPONSE_REJECTED == status.asPrimitiveElement().valueAsInt()
						|| LeavesApplicationStatus.RESPONSE_OTHER_REJECTED_STATUS == status.asPrimitiveElement().valueAsInt()
						|| LeavesApplicationStatus.RESPONSE_OTHER_REJECTED == status.asPrimitiveElement().valueAsInt()) {
					rejectedLeave(rejectReason);
				}
			}
		}
	}

	/**
	 * Shows leave rejection reason
	 * 
	 * @param rejectReason
	 */
	private void rejectedLeave(String rejectReason) {

		Intent intent = new Intent(AnnualAppliedLeaveActivity.this,
				AnnualLeaveRejectedActivity.class);
		intent.putExtra(KEY_LEAVE_REJECT_REASON, rejectReason);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);

	}
	
	private void checkIsSupervisor(StoreCallback storeCallback) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);		
		String username = pref.getString(Constants.USER_NAME, null);
		
		((AppApplication) getApplication()).getRestStore().performQuery(new Query()
			.fieldIsEqualTo("username", username), "is_supervisor", storeCallback);
	}

}
