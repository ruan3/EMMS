package com.esquel.epass.leave;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.utils.Constants;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;

public class AnnualLeaveDetails extends BaseGestureActivity implements
		OnClickListener, OnItemClickListener {
	public static final String EXTRA_DATA = "data";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_leave_leader_details);

		setUIData();

	}

	private void setUIData() {
		findViewById(R.id.left_first).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title_menu);
		title.setText(R.string.approve_leave_title);
		TextView rightButton = (TextView) findViewById(R.id.right_button);
		rightButton.setText(R.string.leave_approve);
		rightButton.setOnClickListener(this);
		String dataString = getIntent().getStringExtra(EXTRA_DATA);
		if (dataString != null) {
			JsonObjectElement data = new JsonObjectElement(dataString);
			if (data != null && data.isObject()) {
				DataElement unitField = data.get("leave_unit");
				String unit = "";
				if (unitField != null && unitField.isPrimitive()) {
					unit = unitField.asPrimitiveElement().valueAsString();
				}
				DataElement e = data.get("employee_number");
				if (e != null && e.isPrimitive()) {
					setTextValue(R.id.info_value1, e.asPrimitiveElement().valueAsString());
				}
				
				e = data.get("employee_name");
				if (e != null && e.isPrimitive()) {
					setTextValue(R.id.info_value2, e.asPrimitiveElement().valueAsString());
				}
				
				e = data.get("leave_name");
				if (e != null && e.isPrimitive()) {
					setTextValue(R.id.info_value4, e.asPrimitiveElement().valueAsString());
				}
				
				e = data.get("leave_start_date");
				if (e != null && e.isPrimitive()) {
					
					Date date = new Date(e.asPrimitiveElement().valueAsLong());
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
					setTextValue(R.id.info_value6, dateFormat.format(date));
				}
				
				e = data.get("leave_end_date");
				if (e != null && e.isPrimitive()) {
					
					Date date = new Date(e.asPrimitiveElement().valueAsLong());
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
					setTextValue(R.id.info_value7, dateFormat.format(date));
				}
				
				e = data.get("leave_length");
				if (e != null && e.isPrimitive()) {
					setTextValue(R.id.info_value8, e.asPrimitiveElement().valueAsString() + unit);
				}
			}
		}
		

	}
	
	private void setTextValue(int textViewId, String value) {
		TextView textView = (TextView) findViewById(textViewId);
		textView.setText(value);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.right_button:
			selectApproveOrRejectDialog();
			break;
		case R.id.left_first:
			onBackPressed();
			break;

		}
	}
	
	private void selectApproveOrRejectDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setItems(R.array.select_leave_status, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean isApprove = which == 0;
				dialogAlert(isApprove, isApprove ? getString(R.string.approve_description) : getString(R.string.reject_description));
			} 
		});
		alertDialogBuilder.show();
	}
	
	private void performLeaveAction(boolean approve) {
		showLoadingDialog();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = pref.getString(Constants.USER_NAME, null);
		String password = pref.getString(Constants.PASSWORD, null);
		JsonObjectElement object = new JsonObjectElement();
		object.set("username", username);
		object.set("password", password);
		object.set("lang", "zh");
		String dataString = getIntent().getStringExtra(EXTRA_DATA);
		if (dataString == null) {
			return;
		}
		JsonObjectElement data = new JsonObjectElement(dataString);
		String leaveId = null;
		DataElement e = data.get("leave_id");
		if (e != null && e.isPrimitive()) {
			leaveId = e.asPrimitiveElement().valueAsString();
		}
		String uri = "leaves/" + leaveId + "/" + (approve ? "approve" : "reject");
		((AppApplication) getApplication()).getRestStore()
			.createElement(object, uri, new OAuthRestStoreCallback(this) {

				@Override
				public void success(DataElement element, String resource) {
					Log.d("", "SUCCESS");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dismissLoadingDialog();
							setResult(Activity.RESULT_OK);
							finish();
						}
						
					});
				}

				@Override
				public void fail(final DatastoreException ex, String resource) {
					Log.d("", ex.getMessage());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dismissLoadingDialog();
							Toast.makeText(AnnualLeaveDetails.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
					});
				}
				
			});
	}

	private void dialogAlert(final boolean isApprove, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(message);

		alertDialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						performLeaveAction(isApprove);
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
