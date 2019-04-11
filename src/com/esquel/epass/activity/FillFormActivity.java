package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.oauth.TokenUtils;
import com.esquel.epass.schema.Location;
import com.esquel.epass.schema.LocationDepartment;
import com.esquel.epass.schema.Request;
import com.esquel.epass.schema.Task;
import com.esquel.epass.utils.NetworkUtils;
import com.google.gson.JsonArray;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.rest.security.IdToken;
import com.joyaether.datastore.schema.Query;
import com.umeng.analytics.MobclickAgent;

public class FillFormActivity extends BaseGestureActivity {

    private static final int PADDING_BOTTOM = 40;
	private static final int PADDING_RIGHT = 15;
	private static final int PADDING_TOP = 40;
	private static final int PADDING_LEFT = 15;
	ListView listView;
    ListView optionListView;
    RelativeLayout.LayoutParams layoutParams;
    LinearLayout viewHeader;
    View select;
    public static final String EXTRA_USER_NAME = "user-profile";
    public static final String EXTRA_TASK_NAME = "task";
    private DataElement locations;
    private DataElement locationDepartments;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_form);
        setLayout();
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
//        initUi();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
    private void setLayout() {
    	setUserInfo();
    	getLocationAndDepartment();
    	setSalaryDate();
    	setTaskType();
//    	setUsage();
    	setReceiveMethod();
    	findViewById(R.id.right_button).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showLoadingDialog();
						createRequest();
					}

				});

		findViewById(R.id.left_first).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}

		});
	}

	private void getLocationAndDepartment() {
		showLoadingDialog();
		((AppApplication) getApplication())
				.getRestStore()
				.performQuery(
						new Query()
								.expandField(Location.LOCATION_DEPARTMENT_FIELD_NAME),
						"locations", new OAuthRestStoreCallback(this) {

							@Override
							public void success(DataElement element,
									String resource) {
								locations = element;
								getLocationDepartment();

							}

							@Override
							public void fail(DatastoreException ex,
									String resource) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										dismissLoadingDialog();
										Toast.makeText(FillFormActivity.this,
												R.string.error_get_location,
												Toast.LENGTH_SHORT).show();
										onBackPressed();
									}

								});
							}

						});
	}

	private void getLocationDepartment() {
		((AppApplication) getApplication())
				.getRestStore()
				.performQuery(
						new Query()
								.expandField(Location.LOCATION_DEPARTMENT_FIELD_NAME),
						"location_departments",
						new OAuthRestStoreCallback(this) {

							@Override
							public void success(DataElement element,
									String resource) {
								locationDepartments = element;
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										dismissLoadingDialog();
										setLocation();
									}

								});
							}

							@Override
							public void fail(DatastoreException ex,
									String resource) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										dismissLoadingDialog();
										Toast.makeText(FillFormActivity.this,
												R.string.error_get_location,
												Toast.LENGTH_SHORT).show();
										onBackPressed();
									}

								});
							}

						});
	}

	private void setLocation() {
		Spinner locationView = (Spinner) findViewById(R.id.location);
		final Spinner locationDepartmentView = (Spinner) findViewById(R.id.location_department);
		LocationDataAdapter locationAdapter = new LocationDataAdapter(this,
				locations.asArrayElement());
		locationView.setAdapter(locationAdapter);
		if (locations != null && locations.isArray()) {
			DataElement defaultLocation = locations.asArrayElement().get(0);
			if (defaultLocation != null && defaultLocation.isObject()) {
				DataElement idField = defaultLocation.asObjectElement().get(
						Location.ID_FIELD_NAME);
				int locationId = 0;
				if (idField != null && idField.isPrimitive()) {
					locationId = idField.asPrimitiveElement().valueAsInt();
				}

				if (locationDepartments != null
						&& locationDepartments.isArray()) {
					LocationDepartmentDataAdapter locationDepartmentAdapter = new LocationDepartmentDataAdapter(
							this, getLocationDepartment(locationId));
					locationDepartmentView
							.setAdapter(locationDepartmentAdapter);
					locationView
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(
										AdapterView<?> parent, View view,
										int position, long id) {
									DataElement defaultLocation = locations
											.asArrayElement().get(position);
									if (defaultLocation != null
											&& defaultLocation.isObject()) {
										DataElement idField = defaultLocation
												.asObjectElement().get(
														Location.ID_FIELD_NAME);
										int locationId = 0;
										if (idField != null
												&& idField.isPrimitive()) {
											locationId = idField
													.asPrimitiveElement()
													.valueAsInt();
										}
										LocationDepartmentDataAdapter locationDepartmentAdapter = new LocationDepartmentDataAdapter(
												FillFormActivity.this,
												getLocationDepartment(locationId));
										locationDepartmentView
												.setAdapter(locationDepartmentAdapter);
									}

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> parent) {

								}

							});
					locationDepartmentView
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(
										AdapterView<?> parent, View view,
										int position, long id) {
									DataElement item = (DataElement) parent
											.getAdapter().getItem(position);
									if (item != null && item.isObject()) {
										DataElement address = item
												.asObjectElement()
												.get(LocationDepartment.RECEIVING_ADDRESS_FIELD_NAME);
										EditText textView = (EditText) findViewById(R.id.receive_address);
										if (textView != null) {
											if (address != null
													&& address.isPrimitive()) {
												textView.setText(address
														.asPrimitiveElement()
														.valueAsString());
											} else {
												textView.setText(R.string.error_without_receive_address);
											}
										}

									}

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> parent) {

								}

							});
				}

			}

		}
	}

	private ArrayElement getLocationDepartment(int id) {
		JsonArrayElement array = new JsonArrayElement(new JsonArray());
		if (id == 0) {
			return array;
		}
		for (DataElement item : locationDepartments.asArrayElement()) {
			DataElement e = item.asObjectElement().get(
					LocationDepartment.LOCATION_FIELD_NAME);
			if (e != null) {
				if (e.isObject()) {
					DataElement locationIdField = e.asObjectElement().get(
							Location.ID_FIELD_NAME);
					if (locationIdField.asPrimitiveElement().valueAsInt() == id) {
						array.add(item);
					}
				} else if (e.isPrimitive()) {
					if (e.asPrimitiveElement().valueAsInt() == id) {
						array.add(item);
					}
				}
			}
		}
		return array;
	}

	private void createRequest() {
		if (!NetworkUtils.hasNetworkConnection(this)) {
			Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		EditText userIdView = (EditText) findViewById(R.id.userid);
		EditText userNameView = (EditText) findViewById(R.id.name);
		Spinner locationView = (Spinner) findViewById(R.id.location);
		Spinner locationDepartmentView = (Spinner) findViewById(R.id.location_department);
		Spinner salaryDateView = (Spinner) findViewById(R.id.requester_salary_date);
		Spinner taskView = (Spinner) findViewById(R.id.task_type);
		Spinner usageView = (Spinner) findViewById(R.id.usage);
		Spinner receiveMethodView = (Spinner) findViewById(R.id.receive_method);
		EditText receiveAddress = (EditText) findViewById(R.id.receive_address);
		EditText phoneView = (EditText) findViewById(R.id.phone);
		EditText remarkView = (EditText) findViewById(R.id.request_remark);

		if (receiveAddress.getText().toString() == null
				|| receiveAddress.getText().toString().length() == 0) {
			Toast.makeText(this, R.string.error_fill_address,
					Toast.LENGTH_SHORT).show();
			dismissLoadingDialog();
			return;
		}

		if (phoneView.getText().toString() == null
				|| phoneView.getText().toString().length() == 0) {
			Toast.makeText(this, R.string.error_fill_phone, Toast.LENGTH_SHORT)
					.show();
			dismissLoadingDialog();
			return;
		}

		JsonObjectElement requestObject = new JsonObjectElement();
		EPassRestStoreClient client = (EPassRestStoreClient) ((AppApplication) getApplication())
				.getRestStore().getClient();
		requestObject.set(Request.USER_FIELD_NAME,
				TokenUtils.getUserId(client.getToken()));
		requestObject.set(Request.REQUESTER_NAME_FIELD_NAME, userNameView
				.getText().toString());
		DataElement e = (DataElement) locationView.getSelectedItem();
		if (e != null && e.isObject()) {
			DataElement id = e.asObjectElement().get(Location.ID_FIELD_NAME);
			if (id != null && id.isPrimitive()) {
				requestObject.set(Request.REQUESTER_LOCATION_FIELD_NAME, id
						.asPrimitiveElement().valueAsLong());
			}
		}
		e = (DataElement) locationDepartmentView.getSelectedItem();
		if (e != null && e.isObject()) {
			DataElement id = e.asObjectElement().get(
					LocationDepartment.ID_FIELD_NAME);
			if (id != null && id.isPrimitive()) {
				requestObject.set(
						Request.REQUESTER_LOCATION_DEPARTMENT_FIELD_NAME, id
								.asPrimitiveElement().valueAsLong());
			}
		}
		requestObject.set(Request.REQUESTER_SALARY_DATE_FIELD_NAME,
				salaryDateView.getSelectedItemPosition());
		DataElement element = (DataElement) taskView.getSelectedItem();
		e = element.asObjectElement().get(Task.ID_FIELD_NAME);
		if (e != null && e.isPrimitive()) {
			requestObject.set(Request.TASK_FIELD_NAME, e.asPrimitiveElement()
					.valueAsLong());
		}

		String taskName = "";
		e = element.asObjectElement().get(Task.NAME_FIELD_NAME);
		if (e != null && e.isPrimitive()) {
			taskName = e.asPrimitiveElement().valueAsString();
		}

		e = (DataElement) usageView.getSelectedItem();
		if (e != null && e.isObject()) {
			DataElement description = e.asObjectElement().get("task_usage_id");
			if (description != null && description.isPrimitive()) {
				requestObject.set(Request.USAGE_FIELD_NAME, description
						.asPrimitiveElement().valueAsLong());
			}
		}

		requestObject.set(Request.RECEIVE_METHOD_FIELD_NAME,
				receiveMethodView.getSelectedItemPosition());
		requestObject.set(Request.RECEIVE_ADDRESS_FIELD_NAME, receiveAddress
				.getText().toString());
		requestObject.set(Request.PHONE_FIELD_NAME, phoneView.getText()
				.toString());
		requestObject.set(Request.REMARK_FIELD_NAME, remarkView.getText()
				.toString());
		requestObject.set(Request.STATUS_FIELD_NAME, 0);
		final String logTaskName = taskName;
		((AppApplication) getApplication()).getRestStore().createElement(
				requestObject, "requests", new StoreCallback() {

					@Override
					public void success(DataElement element, String resource) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								MobclickAgent.onEvent(FillFormActivity.this,
										"requests", logTaskName);
								dismissLoadingDialog();
								Toast.makeText(FillFormActivity.this,
										R.string.success_submit_request,
										Toast.LENGTH_SHORT).show();
								onBackPressed();
							}

						});
					}

					@Override
					public void failure(final DatastoreException ex,
							String resource) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissLoadingDialog();
								Toast.makeText(FillFormActivity.this,
										R.string.failed_submit_request,
										Toast.LENGTH_SHORT).show();

							}

						});
					}

				});
	}

	private void setUserInfo() {
		EditText userIdView = (EditText) findViewById(R.id.userid);
		EditText userNameView = (EditText) findViewById(R.id.name);
		// Spinner locationView = (Spinner) findViewById(R.id.location);
		// final Spinner locationDepartmentView = (Spinner)
		// findViewById(R.id.location_department);

		String username = getIntent().getStringExtra(EXTRA_USER_NAME);
		if (username != null) {
			String[] nameField = username.split("-");
			if (nameField != null && nameField.length == 2) {
				userIdView.setText(nameField[1]);
				userNameView.setText(nameField[0]);
			} else {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(this);
				String user = pref.getString(HomeActivity.KEY_USER_ID, null);
				if (user != null) {
					userIdView.setText(user);
				}
				userNameView.setText(username);
			}
		} else {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);
			String user = pref.getString(HomeActivity.KEY_USER_ID, null);
			if (user != null) {
				userIdView.setText(user);
			}
		}
	}

	private class LocationDataAdapter extends BaseAdapter {

		private Context mContext;

		private ArrayElement items;

		public LocationDataAdapter(Context context, ArrayElement array) {
			mContext = context;
			items = array;
		}

		@Override
		public int getCount() {
			if (items == null || !items.isArray()) {
				return 0;
			}

			return items.size();
		}

		@Override
		public DataElement getItem(int position) {
			if (items == null || !items.isArray()) {
				return null;
			}
			return items.get(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(FillFormActivity.this,
						android.R.layout.simple_spinner_item, null);
			}

			DataElement data = getItem(position);
			if (data != null && data.isObject()) {
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				DataElement e = data.asObjectElement().get(
						Location.NAME_FIELD_NAME);
				if (e != null && e.isPrimitive()) {
					textView.setText(e.asPrimitiveElement().valueAsString());
				}
			}

			return view;

		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(FillFormActivity.this,
						android.R.layout.simple_spinner_dropdown_item, null);
			}
			view.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
			DataElement data = getItem(position);
			if (data != null && data.isObject()) {
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				DataElement e = data.asObjectElement().get(
						Location.NAME_FIELD_NAME);
				if (e != null && e.isPrimitive()) {
					textView.setText(e.asPrimitiveElement().valueAsString());
				}
			}
			return view;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	private class LocationDepartmentDataAdapter extends BaseAdapter {

		private Context mContext;

		private ArrayElement items;

		public LocationDepartmentDataAdapter(Context context, ArrayElement array) {
			mContext = context;
			items = array;
		}

		@Override
		public int getCount() {
			if (items == null || !items.isArray()) {
				return 0;
			}

			return items.size();
		}

		@Override
		public DataElement getItem(int position) {
			if (items == null || !items.isArray()) {
				return null;
			}
			return items.get(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(FillFormActivity.this,
						android.R.layout.simple_spinner_item, null);
			}

			DataElement data = getItem(position);
			if (data != null && data.isObject()) {
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				DataElement e = data.asObjectElement().get(
						LocationDepartment.NAME_FIELD_NAME);
				if (e != null && e.isPrimitive()) {
					textView.setText(e.asPrimitiveElement().valueAsString());
				}
			}

			return view;

		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(FillFormActivity.this,
						android.R.layout.simple_spinner_dropdown_item, null);
			}
			view.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
			DataElement data = getItem(position);
			if (data != null && data.isObject()) {
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				DataElement e = data.asObjectElement().get(
						LocationDepartment.NAME_FIELD_NAME);
				if (e != null && e.isPrimitive()) {
					textView.setText(e.asPrimitiveElement().valueAsString());
				}
			}
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
    	
    }
    
    private void setSalaryDate() {
    	Spinner salaryDateView = (Spinner) findViewById(R.id.requester_salary_date);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
    	        R.array.requester_salary_date, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	salaryDateView.setAdapter(adapter);
    }
    
    private void setTaskType() {
    	Spinner taskView = (Spinner) findViewById(R.id.task_type);
    	String tasks = getIntent().getStringExtra(EXTRA_TASK_NAME);
    	if (tasks != null) {
    		JsonArrayElement array = new JsonArrayElement(tasks);
    		if (array != null) {
    			List<DataElement> taskList = new ArrayList<DataElement>();
    			
    			for (int i = 0; i < array.size(); i++) {
        			DataElement  e = array.get(i);
        			if (e != null && e.isObject()) {
        				taskList.add(e);
        			}
        			
        		}
        		ArrayAdapter<DataElement> adapter = new ArrayAdapter<DataElement>(this, android.R.layout.simple_spinner_item, taskList){
        			
        			@Override
        			public View getView(int position, View convertView, ViewGroup parent) {
        				View view = super.getView(position, convertView, parent);
        				DataElement item = getItem(position);
        				TextView textView  = (TextView) view;
        				DataElement e = item.asObjectElement().get(Task.NAME_FIELD_NAME);
        				if (e != null && e.isPrimitive()) {
        					textView.setText(e.asPrimitiveElement().valueAsString());
        				}
        			
        				return view;
        			}
        			
        			@Override
        			public View getDropDownView(int position, View convertView, ViewGroup parent) {
        				View view = View.inflate(getContext(), android.R.layout.simple_spinner_dropdown_item, null);
        				view.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        				DataElement item = getItem(position);
        				TextView textView  = (TextView) view;
        				DataElement e = item.asObjectElement().get(Task.NAME_FIELD_NAME);
        				if (e != null && e.isPrimitive()) {
        					textView.setText(e.asPrimitiveElement().valueAsString());
        				}
        				return view;
        			}
        		};
//        		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        		taskView.setAdapter(adapter);
        		taskView.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						DataElement data = (DataElement) parent.getAdapter()
								.getItem(position);
						if (data != null && data.isObject()) {
							List<DataElement> list = new ArrayList<DataElement>();
							DataElement usages = data.asObjectElement().get(
									Task.USAGE_FIELD_NAME);
							if (usages != null && usages.isArray()) {
								for (DataElement e : usages.asArrayElement()) {
									list.add(e);
								}
							}
							Spinner usageView = (Spinner) findViewById(R.id.usage);
							ArrayAdapter<DataElement> adapter = new ArrayAdapter<DataElement>(
									FillFormActivity.this,
									android.R.layout.simple_spinner_item, list) {

								@Override
								public View getView(int position,
										View convertView, ViewGroup parent) {
									View view = super.getView(position,
											convertView, parent);
									DataElement item = getItem(position);
									TextView textView = (TextView) view;
									DataElement e = item.asObjectElement().get(
											"description");
									if (e != null && e.isPrimitive()) {
										textView.setText(e.asPrimitiveElement()
												.valueAsString());
									}

									return view;
								}

								@Override
								public View getDropDownView(int position,
										View convertView, ViewGroup parent) {
									View view = View
											.inflate(
													getContext(),
													android.R.layout.simple_spinner_dropdown_item,
													null);
									view.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
									DataElement item = getItem(position);
									TextView textView = (TextView) view;
									DataElement e = item.asObjectElement().get(
											"description");
									if (e != null && e.isPrimitive()) {
										textView.setText(e.asPrimitiveElement()
												.valueAsString());
									}
									return view;
								}
							};
							usageView.setAdapter(adapter);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

			}

		}
	}

	private void setReceiveMethod() {
		Spinner receiveMethodView = (Spinner) findViewById(R.id.receive_method);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.receive_method,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		receiveMethodView.setAdapter(adapter);
	}

	public class Item {
		private String label;
		private List<String> values = new ArrayList<String>();
		private boolean required;
		private boolean enable;

		public Item(String label, List<String> values, boolean required,
				boolean enable) {
			this.label = label;
			this.values = values;
			this.required = required;
			this.enable = enable;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

	}

	public class Values extends BaseAdapter {

		List<Item> list;
		Activity activity;
		int positionFocus = -1;

		public Values(Activity activity, List<Item> list) {
			this.activity = activity;
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
			Item item = list.get(position);
			View convertView = view;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.item_fill_form, parent, false);
				holder = new ViewHolder();
				holder.label = (TextView) convertView.findViewById(R.id.label);
				holder.tv = (TextView) convertView.findViewById(R.id.tv);
				holder.edt = (EditText) convertView.findViewById(R.id.edt);
				holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.label.setText(item.getLabel());
			if (item.getValues().size() == 0) {
				holder.arrow.setVisibility(View.GONE);
				holder.edt.setVisibility(View.VISIBLE);
				holder.tv.setVisibility(View.GONE);
			} else {

				holder.arrow.setVisibility(View.VISIBLE);
				holder.edt.setVisibility(View.GONE);
				holder.tv.setText(item.getValues().get(0));
				holder.tv.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		/**
		 * 
		 * @author joyaether
		 * 
		 */
		private class ViewHolder {
			private TextView label;
			private TextView tv;
			private EditText edt;
			private ImageView arrow;
		}

	}

	@Override
	public void onBackPressed() {
		// if (select.getVisibility() == View.VISIBLE) {
		// select.setVisibility(View.GONE);
		// listView.removeFooterView(viewHeader);
		// listView.scrollTo(0, 1);
		// } else {
		super.onBackPressed();
		// }

	}

	public class OptionAdapter extends BaseAdapter {

		List<String> list;
		Activity activity;
		int positionFocus = -1;

		public OptionAdapter(Activity activity, List<String> list) {
			this.activity = activity;
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
			String item = list.get(position);
			View convertView = view;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.item_option, parent, false);
				holder = new ViewHolder();
				holder.label = (TextView) convertView.findViewById(R.id.txt);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.label.setText(item);
			return convertView;
		}

		/**
		 * 
		 * @author joyaether
		 * 
		 */
		private class ViewHolder {
			private TextView label;

		}

	}

}
