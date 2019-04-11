package com.esquel.epass.appstore;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.adapter.FilteredAppListAdapter;
import com.esquel.epass.appstore.AppListAdapter.OnClickDownloadButtonListener;
import com.esquel.epass.appstore.AppListAdapter.OnClickOpenButtonListener;
import com.esquel.epass.fragment.SearchLeaderBoardFragment;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.Region;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.NetworkUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.google.gson.JsonArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;

public class LeaderBoardActivity extends AppStoreActivity implements
		OnItemClickListener, OnClickListener, OnClickOpenButtonListener,
		OnClickDownloadButtonListener {

	private static final int DELAY_INTERVAL_REFRESH_LISTVIEW = 500;
	private static final int DELAY_INTERVAL_CLEAR_FOCUS = 200;
	PullToRefreshListView listLeaderBoard;
	public static final int REQUEST_CODE_CATEGORY = 102;
	public static final String EXTRA_CATEGORY_ID = "category.id";
	String appname;

	View selectedView;
	private String selectedTag = "all";
	private static final String TAG_BY_DATE = "by_lastmoddate_date";
	private static final String TAG_BY_MOST_POPULAR = "by_most_popular";
	private static final String TAG_BY_RECOMMENDED = "by_recommended";
	private static final String TAG_BY_UPDATE = "by_update";
	private AppListAdapter newAppListAdapter;
	private AppListAdapter mostPopularAdapter;
	private AppListAdapter recommendedAdapter;
	private FilteredAppListAdapter updateAdapter;
	private long categoryId;
	private boolean inSearching;
	public static final String EXTRA_OAUTH_TOKEN = "com.esquel.intent.extra.OAUTH_TOKEN";
	public static final String KEY_APP_CATEGORY = "app-category";
	public static final int REQUEST_CODE_CATEGORY_DETAIL = 10000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		appname = getApplicationContext().getPackageName();
		checkRegion();
	}
	
	private void checkRegion() {
		String regionJson = SharedPreferenceManager.getAppStoreRegion(this);
		if (regionJson != null) {
			setData();
		} else {
			setDefaultRegion();
		}
	}
	
	private void setDefaultRegion() {
		showLoadingDialog();
		getRestStore().performQuery(new Query().limitResultsTo(1L), "regions", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
					SharedPreferenceManager.setAppStoreRegion(LeaderBoardActivity.this, element.asArrayElement().get(0).toJson());
				}
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setData();
					}
					
				});
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setData();
					}
					
				});
			}
			
		});
	}

	private void setData() {

		selectedView = findViewById(R.id.bycreationdate);

		findViewById(R.id.left_first).setOnClickListener(this);

		TextView rightTopButton = (TextView) findViewById(R.id.right_button);
		rightTopButton.setText(R.string.category);
		rightTopButton.setOnClickListener(this);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_leader_board);

		listLeaderBoard = (PullToRefreshListView) findViewById(R.id.listview_leaderboard);

		listLeaderBoard.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				showLoadingDialog();
				setListView(true);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						listLeaderBoard.onRefreshComplete();						
					}
					
				}, DELAY_INTERVAL_REFRESH_LISTVIEW);
			}
			
		});

		listLeaderBoard.setOnItemClickListener(this);

		if (!NetworkUtils.hasNetworkConnection(this)) {
			Toast.makeText(LeaderBoardActivity.this, R.string.network_error,
					Toast.LENGTH_SHORT).show();
			onBackPressed();
			return;
		}

		selectTab(selectedView);
		ImageView cancelButton = (ImageView) findViewById(R.id.cancel_search);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelSearch();
			}
		});


		final EditText searchView = (EditText) findViewById(R.id.search);
		searchView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(final TextView v, int actionId,
					KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) && !isInSearching()) {
					setInSearching(true);
					handleSearchResult(v.getText() != null ? v.getText().toString() : "");
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							v.clearFocus();
						}
						
					}, DELAY_INTERVAL_CLEAR_FOCUS);
				}
				return false;
			}

		});

	}
    
	private AppListAdapter getCurrentAdapter() {
		AppListAdapter adapter = null;
		String tag = selectedTag;
		if (TAG_BY_DATE.equals(tag)) {
			adapter = newAppListAdapter;
		} else if (TAG_BY_MOST_POPULAR.equals(tag)) {
			adapter = mostPopularAdapter;
		} else if (TAG_BY_RECOMMENDED.equals(tag)) {
			adapter = recommendedAdapter;
		} else if (TAG_BY_UPDATE.equals(tag)) {
			adapter = updateAdapter;
		}

		return adapter;
	}

	private void setAdapterWithTab(AppListAdapter adapter) {
		String tag = selectedTag;
		if (TAG_BY_DATE.equals(tag)) {
			newAppListAdapter = adapter;
		} else if (TAG_BY_MOST_POPULAR.equals(tag)) {
			mostPopularAdapter = adapter;
		} else if (TAG_BY_RECOMMENDED.equals(tag)) {
			recommendedAdapter = adapter;
		} else if (TAG_BY_UPDATE.equals(tag) && adapter instanceof FilteredAppListAdapter) {
			updateAdapter = (FilteredAppListAdapter) adapter;
		}
	}

	protected void setListView(boolean refresh) {
		AppListAdapter currentAdapter = getCurrentAdapter();
		String tag = selectedTag;
		if (currentAdapter != null) {
			if (refresh) {
				if (TAG_BY_UPDATE.equals(tag)) {
					getAppUpdateList((FilteredAppListAdapter) currentAdapter);
				} else {
					currentAdapter.getCache().clear();
					currentAdapter.notifyDataSetChanged();
				}			
			}
			listLeaderBoard.setAdapter(currentAdapter);
			return;
		}
		if (TAG_BY_UPDATE.equals(tag)) {
			currentAdapter = new FilteredAppListAdapter(this);
			getAppUpdateList((FilteredAppListAdapter) currentAdapter);
		} else {
			currentAdapter = new AppListAdapter(this,
					((AppApplication) getApplication()).getRestStore(),
					"application_versions", getQuery());
		}
		
		currentAdapter.setOnClickOpenButtonListener(this);
		currentAdapter.setOnClickDownloadButtonListener(this);
		updateAdapter(currentAdapter);
	}

	private void updateAdapter(AppListAdapter currentAdapter) {
		setAdapterWithTab(currentAdapter);
		final AppListAdapter adapter = currentAdapter;
		dismissLoadingDialog();
		listLeaderBoard.onRefreshComplete();
		listLeaderBoard.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.left_first:
			onBackPressed();
			break;

		case R.id.right_button:
			showCategoryActivity();
			break;
		default:
			break;
		}
	}

	private void showCategoryActivity() {
		Intent intent = new Intent(LeaderBoardActivity.this,
				LeaderBoardCategoryActivity.class);
		intent.putExtra(EXTRA_CATEGORY_ID, getCategoryId());
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, REQUEST_CODE_CATEGORY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CATEGORY && resultCode == RESULT_OK && data != null) {
			setCategoryId(data.getLongExtra(EXTRA_CATEGORY_ID, 0L));
			resetAllAppList();
			showLoadingDialog();
			setListView(true);
		} else if (requestCode == REQUEST_CODE_CATEGORY_DETAIL && resultCode == RESULT_OK) {
			resetAllAppList();
			showLoadingDialog();
			setListView(true);
		}
		
	}
	
	private void resetAllAppList() {
		mostPopularAdapter = null;
		newAppListAdapter = null;
		recommendedAdapter = null;
		updateAdapter = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		DataElement item = (DataElement) parent.getAdapter().getItem(position);
		if (item == null || item.isNull()) {
			return;
		}

		Intent intent = new Intent(LeaderBoardActivity.this,
				LeaderBoardDetailActivity.class);
		intent.putExtra(LeaderBoardDetailActivity.KEY_APPLICATION_DETAIL, item.toString());
		int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);
		if (requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION) {
			intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, REQUEST_CODE_CATEGORY_DETAIL);

	}

	private Query getQuery() {
		Query query = new Query();
		query.fieldIsEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_platform", "Android")
				.expandField(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_app_categories/app_category")
				.fieldIsEqualTo(ApplicationVersion.RELEASED_FIELD_NAME, true);
		String tag = selectedTag;
		if (TAG_BY_DATE.equals(tag)) {
			query.fieldIsOrderedBy(ApplicationVersion.AVAILABLE_FIELD_NAME, Ordering.DESCENDING);
		} else if (TAG_BY_MOST_POPULAR.equals(tag)) {
			query.fieldIsOrderedBy(ApplicationVersion.DOWNLOADS_FIELD_NAME, Ordering.DESCENDING);
		} else if (TAG_BY_RECOMMENDED.equals(tag)) {
			query.fieldIsEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "featured", true);
		} 
		
		if (getCategoryId() > 0L) {
			query.fieldIsEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_app_categories/app_category", getCategoryId());
		}
		query.fieldIsNotEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + Application.IDENTIFIER_FIELD_NAME, getPackageName());
		//set the region to the query
		String regionJson = SharedPreferenceManager.getAppStoreRegion(this);
		JsonObjectElement object = new JsonObjectElement(regionJson);
		String regionName = object.get("name").asPrimitiveElement().valueAsString();
//		query.conditionsAreOred(true);
		List<String> regions = new ArrayList<String>();
		regions.add(Region.DEFAULT_REGION_NAME);
		if (!Region.DEFAULT_REGION_NAME.equals(regionName)) {
			regions.add(regionName);
		}
		query.fieldIsIn(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_regions/region/name", regions);
		return query;
	}

	public void selectTab(View view) {

		selectedView.setBackgroundColor(getResources().getColor(
				R.color.transparent));
		TextView tv = ((TextView) selectedView);
		tv.setTextColor(getResources().getColor(R.color.red_default));

		view.setBackgroundColor(getResources().getColor(R.color.red_default));
		if (view instanceof TextView) {
			TextView tv2 = ((TextView) view);
			tv2.setTextColor(getResources().getColor(R.color.white));
		}
		selectedView = view;
		String tag = view.getTag().toString();
		selectedTag = tag;

		setListView(false);
	}

	private void handleSearchResult(String searchingKey) {
		FragmentManager manager = getSupportFragmentManager();
		findViewById(R.id.profile_wrapper).setVisibility(View.GONE);
		findViewById(R.id.right_button).setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.title_menu);
		titleView.setText(R.string.search);
		Fragment fragment = manager
				.findFragmentByTag(SearchLeaderBoardFragment.class
						.getSimpleName());
		if (fragment == null) {
			Bundle bundle = new Bundle();
			bundle.putString(SearchLeaderBoardFragment.KEY_VALUE_LIKE, searchingKey);
			fragment = new SearchLeaderBoardFragment();
			fragment.setArguments(bundle);

			manager.beginTransaction()
					.add(R.id.content_wrapper, fragment,
							SearchLeaderBoardFragment.class.getSimpleName())
					.addToBackStack(
							SearchLeaderBoardFragment.class.getSimpleName())
					.commit();
		} else {
			((SearchLeaderBoardFragment) fragment).setQuery(this, searchingKey);
		}
//		setInSearching(false);
	
	}

	public void cancelSearch() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.findFragmentById(R.id.content_wrapper) != null) {
			manager.popBackStack();
			EditText searchView = (EditText) findViewById(R.id.search);
			searchView.setText("");
			findViewById(R.id.profile_wrapper).setVisibility(View.VISIBLE);
			findViewById(R.id.right_button).setVisibility(View.VISIBLE);
			TextView titleMenu = (TextView) findViewById(R.id.title_menu);
			titleMenu.setText(R.string.title_leader_board);
			setInSearching(false);
		}
		

	}
	
	@Override
	public void onBackPressed() {
		if (isInSearching()) {
			cancelSearch();
			return;
		} 
		super.onBackPressed();
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isInSearching() {
		return inSearching;
	}

	public void setInSearching(boolean inSearching) {
		this.inSearching = inSearching;
	}

	@Override
	public void onClickOpenButton(String packageName) {
		openApp(packageName);
	}

	@Override
	public void onClickDownloadButton(DataElement item) {
		download(item.asObjectElement());
	}
	
	@Override
	protected void onApplicationInstalled() {
		AppListAdapter adapter = getCurrentAdapter();
		adapter.notifyDataSetChanged();
	}
	
	private void getAppUpdateList(final FilteredAppListAdapter adapter) {
		showLoadingDialog();
		getRestStore().performQuery(getQuery(), "application_versions", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isArray()) {
					ArrayElement array = new JsonArrayElement(new JsonArray());
					for (DataElement data : element.asArrayElement()) {
						
						if (data != null && data.isObject() && isNeedUpdate(data.asObjectElement())) {
							array.add(data);
						}
					}
					adapter.setListItem(array);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						adapter.notifyDataSetChanged();
					}
					
				});
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
					}
					
				});
			}
			
		});
	}

}
