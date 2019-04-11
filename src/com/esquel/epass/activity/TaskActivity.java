package com.esquel.epass.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.oauth.TokenUtils;
import com.esquel.epass.schema.Request;
import com.esquel.epass.schema.Task;
import com.esquel.epass.schema.UserInfo;
import com.esquel.epass.ui.LoadingDialog;
import com.esquel.epass.utils.NetworkUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.security.IdToken;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.widget.DataAdapter;

public class TaskActivity extends BaseGestureActivity {

    private static final int TAB_STATUS_REJECTED = 2;
	private static final int TAB_STATUS_COMPLETED = 1;
	private static final int TAB_STATUS_IN_PROGRESS = 0;
	private static final int TAB_STATUS_ALL = -1;
	PullToRefreshListView listview;
    TextView selectedTab;
    private static final String CHECK_KEY = "65270289";
    private ObjectElement userProfile;
    private ArrayElement taskList;
    LoadingDialog loadingDialog;
    List<DataElement> elements = new ArrayList<DataElement>();
    private HashMap<Integer, Long> requestCountMap;
    private int currentStatus = TAB_STATUS_ALL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkUtils.hasNetworkConnection(this)) {
        	Toast.makeText(TaskActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            onBackPressed();
        	return;
        }
        loadingDialog = new LoadingDialog(this);
        requestCountMap = new HashMap<Integer, Long>();
        listview = (PullToRefreshListView) findViewById(R.id.listview);
        loadingDialog.setCancelable(false);
        setContentView(R.layout.activity_list_task);
        getUserProfile();       
    }

    private void getUserProfile() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
        String password = pref.getString(HomeActivity.KEY_PASSWORD, null);
        showLoadingDialog();
        if (SharedPreferenceManager.getUserName(this) == null) {
            ((AppApplication) getApplication()).getRestStore().performQuery(new Query().fieldIsEqualTo("username", userId)
            		.fieldIsEqualTo("password", password).fieldIsEqualTo("key", CHECK_KEY),
                    "user_info", new StoreCallback() {

                        @Override
                        public void success(DataElement element, String resource) {
                            if (element != null && element.isObject()) {
                                userProfile = element.asObjectElement();
                                DataElement e = element.asObjectElement().get(UserInfo.FLOCAL_NAME);
                                if (e != null && e.isPrimitive()) {                                  
                                    SharedPreferenceManager.setUserName(TaskActivity.this, e.asPrimitiveElement().valueAsString());
                                }
                                getTasks();
                            } else {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                        initUi();
                                        initUi2();
                                    }

                                });
                            }
                        }

                        @Override
                        public void failure(DatastoreException ex, String resource) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dismissLoadingDialog();
                                    Toast.makeText(TaskActivity.this, R.string.error_get_task, Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                    initUi();
                                    initUi2();
                                }

                            });
                        }

                    });
        } else {
        	getTasks();
        }

    }

    private void getTasks() {
        ((AppApplication) getApplication()).getRestStore().performQuery(new Query()
//        .selectField(Task.ID_FIELD_NAME)
//        .selectField(Task.NAME_FIELD_NAME)
//        .selectField(Task.USAGE_FIELD_NAME)
        .expandField(Task.USAGE_FIELD_NAME)
        , "tasks", new StoreCallback() {

            @Override
            public void success(DataElement element, String resource) {
                if (element != null && element.isArray()) {
                    taskList = element.asArrayElement();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            initUi();
                            initUi2();
                        }

                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            initUi();
                            initUi2();
                        }

                    });
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        Toast.makeText(TaskActivity.this, R.string.error_get_task, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                });
            }

        });
    }

    private void initUi() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int widthItem = getResources().getDimensionPixelSize(R.dimen.width_item_grid);
        int padding = (width - 2 * widthItem) / 3;
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        if (taskList == null || taskList.size() == 0) {
        	final TaskDataAdapter dataAdapter = new TaskDataAdapter(((AppApplication) getApplication()).getRestStore(),
            		"tasks", new Query());
        	new Thread(new Runnable() {

				@Override
				public void run() {
					ListenableFuture<Long> future = dataAdapter.getCache().getCount();
					Futures.addCallback(future, new FutureCallback<Long>() {
						public void onSuccess(final Long result) {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {										
						        	gridView.setAdapter(dataAdapter);
								}
													
							});
						}

						@Override
						public void onFailure(Throwable t) {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {										
									dismissLoadingDialog();
								}
													
							});
						}
					});
				}
        		
        	}).start();
        } else {
        	gridView.setAdapter(new TaskAdapter());       	
        }
        
        gridView.setHorizontalSpacing(padding);
        gridView.setVerticalSpacing(padding / 3);
        gridView.setPadding(padding, 0, padding, 0);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	DataElement item = (DataElement) parent.getAdapter().getItem(position);
            	DataElement e = item.asObjectElement().get(Task.SAMPLE_IMAGE_URL_FIELD_NAME);
                Intent intent = new Intent(TaskActivity.this, ViewPdfActivity.class);
            	if (e != null && e.isPrimitive()) {
            		intent.putExtra(ViewPdfActivity.EXTRA_PDF_URL, e.asPrimitiveElement().valueAsString());
            	}
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

            }
        });

        findViewById(R.id.right_first).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(TaskActivity.this, FillFormActivity.class);
                intent.putExtra(FillFormActivity.EXTRA_USER_NAME, SharedPreferenceManager.getUserName(TaskActivity.this));
                if (taskList != null) {
                    intent.putExtra(FillFormActivity.EXTRA_TASK_NAME, taskList.toJson());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        findViewById(R.id.left_first).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
        	
        });
        
        
    }
    
    private class TaskAdapter extends BaseAdapter {

		public TaskAdapter() {
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getCount() {
			return taskList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			
			DataElement e = taskList.get(position);
			return e;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(TaskActivity.this).inflate(R.layout.item_gridview_list_task, parent, false);				
			}
			DataElement data = (DataElement) getItem(position);
			if (data != null) {
				TextView textView = (TextView) view.findViewById(R.id.text);
				DataElement e = data.asObjectElement().get(Task.NAME_FIELD_NAME);			
				textView.setText(e.asPrimitiveElement().valueAsString());
			}
			return view;
		}
    	
    }
    
    
    
    private class TaskDataAdapter extends DataAdapter {

		public TaskDataAdapter(Store store, String schema, Query query) {
			super(store, schema, query);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		protected void onDataAvailable(DataElement data, View view) {
			TextView textView = (TextView) view.findViewById(R.id.text);
			DataElement e = data.asObjectElement().get(Task.NAME_FIELD_NAME);			
			textView.setText(e.asPrimitiveElement().valueAsString());
			
		}

		@Override
		public View getInflatedView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(TaskActivity.this).inflate(R.layout.item_gridview_list_task, parent, false);				
			}
			return view;
		}

		protected void onError(Throwable t) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {										
					dismissLoadingDialog();
				}
									
			});
		}
    	
    }

    public class ToDo extends BaseAdapter {

        List<String> list;
        Activity activity;

        public ToDo(Activity activity, List<String> list) {
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
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder = null;
            String item = list.get(position);
            View convertView = view;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_gridview_list_task, parent, false);
                holder = new ViewHolder();

                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(item);

            return convertView;
        }

        /**
         * 
         * @author joyaether
         * 
         */
        private class ViewHolder {
            private TextView text;
            private TextView from;
            private TextView order;
        }

    }

    private void initUi2() {
    	if (listview == null) {
            listview = (PullToRefreshListView) findViewById(R.id.listview);
    	}
        listview.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				getRequest();
			}
        	
        });
        selectedTab = (TextView) findViewById(R.id.tab11);
    }

    public void selectTab2(View view) {
        String tag = view.getTag().toString();
        if (tag.equals("1")) {
            findViewById(R.id.divider).setVisibility(View.VISIBLE);
            findViewById(R.id.gridview).setVisibility(View.VISIBLE);
            findViewById(R.id.cover_tab2).setVisibility(View.GONE);
            findViewById(R.id.tab1).setBackgroundColor(Color.parseColor("#a62740"));
            findViewById(R.id.tab2).setBackgroundColor(Color.TRANSPARENT);
            ((TextView) findViewById(R.id.tab1)).setTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.tab2)).setTextColor(Color.parseColor("#a62740"));

        } else {
        	getRequest();
            findViewById(R.id.divider).setVisibility(View.GONE);
            findViewById(R.id.gridview).setVisibility(View.GONE);          
            findViewById(R.id.tab2).setBackgroundColor(Color.parseColor("#a62740"));
            findViewById(R.id.tab1).setBackgroundColor(Color.TRANSPARENT);
            ((TextView) findViewById(R.id.tab2)).setTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.tab1)).setTextColor(Color.parseColor("#a62740"));
        }
    }

    public void selectTab(View view) {
        selectedTab.setBackgroundColor(Color.TRANSPARENT);
        selectedTab.setTextColor(Color.parseColor("#a62740"));
        view.setBackgroundColor(Color.parseColor("#a62740"));
        ((TextView) view).setTextColor(Color.WHITE);
        selectedTab = (TextView) view;
        String tag = view.getTag().toString();
        if (tag.equals("1")) {
            updateListView(TAB_STATUS_ALL);           
        } else if (tag.equals("2")) {
            updateListView(TAB_STATUS_IN_PROGRESS);
        } else if (tag.equals("3")) {
            updateListView(TAB_STATUS_COMPLETED);
        } else if (tag.equals("4")) {
            updateListView(TAB_STATUS_REJECTED);
        }
    }

    public void updateListView(int status) {
    	currentStatus = status;
        if (status == TAB_STATUS_ALL) {
        	SampleAdapter adapter = new SampleAdapter(this, elements);
            listview.setAdapter(adapter);          
            return;
        }
        List<DataElement> list = new ArrayList<DataElement>();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).asObjectElement().get(Request.STATUS_FIELD_NAME).isPrimitive()
                    && elements.get(i).asObjectElement().get(Request.STATUS_FIELD_NAME).asPrimitiveElement().valueAsInt() == status) {
                list.add(elements.get(i));
            }
        }
        SampleAdapter adapter = new SampleAdapter(this, list);
        if (listview != null) {
            listview.setAdapter(adapter);        	
        }
   }

    class Item {
        private String title;
        private String date;
        private String des;
        private int status;

        public Item(String title, String date, String des, int status) {
            this.setTitle(title);
            this.setDate(date);
            this.setDes(des);
            this.setStatus(status);
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class SampleAdapter extends BaseAdapter {

        List<DataElement> list;
        Activity activity;
        int positionFocus = TAB_STATUS_ALL;

        public SampleAdapter(Activity activity, List<DataElement> list) {
            this.activity = activity;
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder = null;
            DataElement item = list.get(position);
            View convertView = view;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_to_do, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.text1);
                holder.date = (TextView) convertView.findViewById(R.id.text2);
                holder.des = (TextView) convertView.findViewById(R.id.text3);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = "";
            DataElement task = item.asObjectElement().get(Request.TASK_FIELD_NAME);
            if (task != null && task.isObject()) {
            	DataElement name = task.asObjectElement().get(Task.NAME_FIELD_NAME);
            	if (name != null && name.isPrimitive()) {
            		title = name.asPrimitiveElement().valueAsString();
            	}
            }
            int status = item.asObjectElement().get(Request.STATUS_FIELD_NAME).isPrimitive() ? item.asObjectElement().get(Request.STATUS_FIELD_NAME).asPrimitiveElement().valueAsInt() : 0;
            Date date = new Date(item.asObjectElement().get(Request.LAST_MODIFIED_DATE_FIELD_NAME).asPrimitiveElement().valueAsLong());

            String dateStr = getDateString(date);

            // for the tab of "寰呭鐞�" is mean in progress so use 0
            // for the tab of "宸插畬鎴�" is mean completed so use 1
            // for the tab of "琚��鍥�" is mean rejected so use 2
            if (status == 2) {
                holder.icon.setVisibility(View.VISIBLE);
                holder.des.setTextColor(Color.parseColor("#fd3d3d"));
                holder.des.setText("琚��鍥�");
                convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DataElement item = (DataElement) getItem(position);
						Intent intent = new Intent(TaskActivity.this, RejectReasonActivity.class);

						if (item != null && item.isObject()) {
							DataElement e = item.asObjectElement().get(Request.REJECT_REASON_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								intent.putExtra(RejectReasonActivity.EXTRA_REJECT_REASON, e.asPrimitiveElement().valueAsString());
							}
						}
						startActivity(intent);
					}
                	
                });
            } else if (status == 1) {
                holder.icon.setVisibility(View.INVISIBLE);
                holder.des.setTextColor(Color.parseColor("#669900"));
                holder.des.setText("宸插畬鎴�");
                convertView.setOnClickListener(null);
            } else {
                holder.icon.setVisibility(View.INVISIBLE);
                holder.des.setTextColor(Color.parseColor("#137ef8"));
                holder.des.setText("寰呭鐞�");
                convertView.setOnClickListener(null);
            }
            holder.title.setText(title);

            holder.date.setText(dateStr);
            return convertView;
        }

        /**
         * 
         * @author joyaether
         * 
         */
        private class ViewHolder {
            private TextView title, date, des;

            private ImageView icon;
        }

    }

    private String getDateString(Date date) {
        String format = "yyyy" + getString(R.string.year) + "MM" + getString(R.string.month) + "dd" + getString(R.string.day)
        		+ " HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
        return formatter.format(date);
    }

    private void getRequest() {
    	if (!NetworkUtils.hasNetworkConnection(this)) {
			listview.onRefreshComplete();
        	Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
        	setListView();
        	return;
        }
        loadingDialog.show();
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);   	
    	String idToken = pref.getString(EPassRestStoreClient.KEY_ID_TOKEN, null);
    	String userId = TokenUtils.getUserId(idToken);
        Query query = new Query().expandField(Request.TASK_FIELD_NAME);
        if (userId != null) {
        	query.fieldIsEqualTo(Request.USER_FIELD_NAME, userId);
        }
        List<Integer> status = new ArrayList<Integer>();
        status.add(Request.REQUEST_STATUS_IN_PROGRESS);
        status.add(Request.REQUEST_STATUS_COMPLETED);
        status.add(Request.REQUEST_STATUS_REJECTED);
        query.fieldIsIn(Request.STATUS_FIELD_NAME, status);
        query.expandField(Request.TASK_FIELD_NAME);
        query.fieldIsOrderedBy(Request.CREATED_DATE_FIELD_NAME, Ordering.DESCENDING);
        requestCountMap.clear();
        ((AppApplication) getApplication()).getRestStore().performQuery(query, "requests", new OAuthRestStoreCallback(this) {

            @Override
            public void success(DataElement element, String resource) {
            	elements.clear();
            	if (element.isArray()) {
                    for (int i = 0; i < element.asArrayElement().size(); i++) {
                    	DataElement item = element.asArrayElement().get(i);
                    	if (item != null && item.isObject()) {
                    		DataElement e = item.asObjectElement().get(Request.STATUS_FIELD_NAME);
                    		if (e != null && e.isPrimitive()) {
                    			long count = 0;
            					if (requestCountMap.containsKey(e.asPrimitiveElement().valueAsInt())) {
            						count = requestCountMap.get(e.asPrimitiveElement().valueAsInt());
            					}
            					long status = e.asPrimitiveElement().valueAsLong();
            					if (status == 1l){
            						requestCountMap.put(Request.REQUEST_STATUS_COMPLETED, count + 1l);
            	
            					} else if (status == 2l) {
                					requestCountMap.put(Request.REQUEST_STATUS_REJECTED, count + 1l);
            					} else if (status == 0){
            						requestCountMap.put(Request.REQUEST_STATUS_IN_PROGRESS, count + 1l);
            					}
                    		}
                    	}
                        elements.add(element.asArrayElement().get(i));
                    }
                    requestCountMap.put(TAB_STATUS_ALL, (long) element.asArrayElement().size());
            	}
                runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (listview != null) {
							listview.onRefreshComplete();
						}
						if (loadingDialog != null) {
							loadingDialog.dismiss();
						}
			            setListView();
					}
                	
                });
            }

            @Override
            public void fail(DatastoreException ex, String resource) {
            	runOnUiThread(new Runnable() {

					@Override
					public void run() {
			            
						loadingDialog.dismiss();	
						listview.onRefreshComplete();
						setListView();
			            Toast.makeText(TaskActivity.this, R.string.error_getting_request, Toast.LENGTH_SHORT).show();
					}
                	
                });
            }

        });

    }

	private void setListView() {
		findViewById(R.id.cover_tab2).setVisibility(View.VISIBLE);
		updateListView(currentStatus);   
		TextView tab1 = (TextView) findViewById(R.id.tab11);
		tab1.setText(getString(R.string.tab1) + (requestCountMap.containsKey(TAB_STATUS_ALL) ?
				requestCountMap.get(TAB_STATUS_ALL) : 0));
		TextView tab2 = (TextView) findViewById(R.id.tab22);
		tab2.setText(getString(R.string.tab2) + (requestCountMap.containsKey(Request.REQUEST_STATUS_IN_PROGRESS) ?
				requestCountMap.get(Request.REQUEST_STATUS_IN_PROGRESS) : 0));

		TextView tab3 = (TextView) findViewById(R.id.tab33);
		tab3.setText(getString(R.string.tab3) + (requestCountMap.containsKey(TAB_STATUS_COMPLETED) ?
				requestCountMap.get(TAB_STATUS_COMPLETED) : 0));

		TextView tab4 = (TextView) findViewById(R.id.tab44);
		tab4.setText(getString(R.string.tab4) + (requestCountMap.containsKey(TAB_STATUS_REJECTED) ?
				requestCountMap.get(TAB_STATUS_REJECTED) : 0));
	}
}
