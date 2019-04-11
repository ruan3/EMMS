package com.esquel.epass.leave;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.utils.Constants;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.gson.JsonArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.schema.Query;

public class AnnualLeaveLeaderListActivity extends BaseGestureActivity
		implements OnClickListener, OnItemClickListener {

	EditText search;

	PullToRefreshListView annualList;	
	public static final int REQUEST_CODE = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_leave_leader);

		setUIData();

	}

	private void setUIData() {

		findViewById(R.id.btn_back).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.pending_leave_title);
		search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				ListAdapter adapter = ((HeaderViewListAdapter) annualList.getRefreshableView().getAdapter()).getWrappedAdapter();
				((AnnualleaveApproveListAdapter) adapter).setInSearch(true);
				((AnnualleaveApproveListAdapter) adapter).setSearchKey(s == null ? null : s.toString());
				((BaseAdapter) adapter).notifyDataSetChanged();
			}
			
		});
		annualList = (PullToRefreshListView) findViewById(R.id.annual_leave_leave_list);
		annualList.setOnItemClickListener(this);
		getLeaves();
		annualList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				getLeaves();
			}
			
		});

	}

	private void getLeaves() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = pref.getString(Constants.USER_NAME, null);
		String password = pref.getString(Constants.PASSWORD, null);
		
		Date currentDate = new Date();
		long startDate = 0L;
		long threeMonth = 7889238000L;
		long endDate = currentDate.getTime();
		
		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeZone(TimeZone
//				.getTimeZone(((AppApplication) getApplication())
//						.getDefaultTimeZone()));
//		calendar.setTime(currentDate);
//		calendar.set(Calendar.MONTH, Calendar.JANUARY);
//		calendar.set(Calendar.DATE, 1);
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
		showLoadingDialog();
		((AppApplication) getApplication()).getRestStore().performQuery(new Query().fieldIsEqualTo("username", username)
				.fieldIsEqualTo("password", password).fieldIsEqualTo("start_date", startDate)
				.fieldIsEqualTo("end_date", endDate).fieldIsEqualTo("status", 0)
				.fieldIsEqualTo("lang", "zh"), "leaves_approve_list", new OAuthRestStoreCallback(this) {

					@Override
					public void success(final DataElement element, String resource) {
						if (element != null && element.isArray()) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									annualList.onRefreshComplete();
									AnnualleaveApproveListAdapter annualLeaveAdapter	= new AnnualleaveApproveListAdapter(
											AnnualLeaveLeaderListActivity.this, element.asArrayElement());	
									annualList.getRefreshableView().setAdapter(annualLeaveAdapter);
								}
								
							});
						} else {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {									
									annualList.onRefreshComplete();
									dismissLoadingDialog();
								}
													
							});
						}
					}

					@Override
					public void fail(final DatastoreException ex, String resource) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								annualList.onRefreshComplete();
								Toast.makeText(AnnualLeaveLeaderListActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
								dismissLoadingDialog();
							}
												
						});
					}	
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent = new Intent(AnnualLeaveLeaderListActivity.this,
				AnnualLeaveDetails.class);
		DataElement e = (DataElement) parent.getAdapter().getItem(position);
		if (e != null && e.isObject()) {
			intent.putExtra(AnnualLeaveDetails.EXTRA_DATA, e.toJson());
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(intent, REQUEST_CODE);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_back:
			onBackPressed();
			break;

		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			getLeaves();
		}
	}
	
	public class AnnualleaveApproveListAdapter extends BaseAdapter {
		
		private Context mContext;
		private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
		private boolean inSearch;
		private Predicate<DataElement> predicate;
		private String searchKey;
		private ArrayElement dataSet;
		private ArrayElement originalDataSet;

		public AnnualleaveApproveListAdapter(Context context, ArrayElement array) {
			this.mContext = context;
			this.originalDataSet = array;
			this.dataSet = array;
			predicate = new Predicate<DataElement>() {
				 @Override
			        public boolean apply(DataElement input) {
					 	DataElement e = input.asObjectElement().get("employee_name");
					 	if (e != null && e.isPrimitive()) {
					 		String name = e.asPrimitiveElement().valueAsString();
					 		return name.contains(getSearchKey());
					 	}
			            return true;
			        }
			};
		}
		


		@Override
		public long getItemId(int position) {
			return position;
		}

		public boolean isInSearch() {
			return inSearch;
		}


		public void setInSearch(boolean inSearch) {
			this.inSearch = inSearch;
		}



		public String getSearchKey() {
			return searchKey;
		}



		public void setSearchKey(String key) {
			this.searchKey = key;
			if (key == null || key.isEmpty()) {
				this.dataSet = originalDataSet;
				return;
			}
			Iterator<DataElement> iterators = Iterators.filter(originalDataSet.iterator(), predicate);
			ArrayElement arrayElement = new JsonArrayElement(new JsonArray());
			while(iterators.hasNext()) {
				arrayElement.add(iterators.next());
			}
			this.dataSet = arrayElement;
//			this.dataSet = Iterators.filter(dataSet.iterator(), predicate);
//			Iterator<DataElement> iterator = Iterators.filter(dataSet.iterator(), predicate);
		}



		@Override
		public int getCount() {
			return dataSet.size();
		}



		@Override
		public Object getItem(int position) {
			return dataSet.get(position);
		}



		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			dismissLoadingDialog();
			View view = convertView;
			if (view == null) {
				view = View.inflate(mContext, R.layout.item_anual_leave_leader, null);
			}
			
			TextView firstTextView = (TextView) view.findViewById(R.id.text1);
			firstTextView.setText("");
			TextView secondTextView = (TextView) view.findViewById(R.id.text2);
			secondTextView.setText("");	
			
			DataElement data = (DataElement) getItem(position);
			DataElement e = data.asObjectElement().get("employee_name");
			String text1 = "";
			if (e != null && e.isPrimitive()) {
				text1 = e.asPrimitiveElement().valueAsString();
			}
			
			e = data.asObjectElement().get("leave_name");
			if (e != null && e.isPrimitive()) {
				text1 = text1 + " - " + e.asPrimitiveElement().valueAsString();
			}
			String text2 = "";
			e = data.asObjectElement().get("leave_start_date");
			if (e != null && e.isPrimitive()) {
				Date date = new Date(e.asPrimitiveElement().valueAsLong());
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				dateFormat.setTimeZone(TimeZone.getTimeZone(((AppApplication) ((Activity) mContext).getApplication()).getDefaultTimeZone()));
				text2 = dateFormat.format(date);
			}
			firstTextView.setText(text1);
			secondTextView.setText(text2);
			return view;
		}
	}
}
