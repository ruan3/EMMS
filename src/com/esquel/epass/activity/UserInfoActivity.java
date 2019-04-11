package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.MenuAdapter;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.fragment.SearchResultFragment;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Category;
import com.esquel.epass.schema.Region;
import com.esquel.epass.schema.UserInfo;
import com.esquel.epass.ui.LoadingDialog;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.callback.AuthorizationCallback;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.rest.security.IdToken;
import com.joyaether.datastore.schema.Query;

/**
 * @author joyaether
 * 
 */
public class UserInfoActivity extends BaseGestureActivity {
    private static final String CHECK_KEY = "65270289";
    ListView listView;
    private LoadingDialog loadingDialog;
    public static final String EXTRA_IN_SEARCH = "search";
    private View logoutButtonView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        setContentView(R.layout.activity_user_info);
        listView = (ListView) findViewById(R.id.listview);
        final EditText searchView = (EditText) findViewById(R.id.search);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    startSearch(s.toString());
                } else {
                    handleSearchResult(null, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }

        });
        boolean inSearch = getIntent().getBooleanExtra(EXTRA_IN_SEARCH, false);
        if (inSearch) {
        	searchView.post(new Runnable() {

				@Override
				public void run() {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
        		
        	});        	
        }
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this,
                        ProfileDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(Constants.INTENT_EXTRA_REQUEST_SUBSCRIBED_CHANNEL_CHANGES, true);
                startActivityForResult(intent, Constants.REQUEST_CODE_LANGUAGE_CHANGE);
            }

        });
        ImageView cancelButton = (ImageView) findViewById(R.id.cancel_search);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelSearch();
            }

        });

        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        logoutButtonView = View.inflate(this, R.layout.logout_out_button, null);
        logoutButtonView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
            }

        });
        listView.addFooterView(logoutButtonView);
        getUserInfoData();
        getCategoryChannel(((AppApplication) getApplication()).getSqliteStore(),
                new Query().expandField(Category.CATEGORY_CHANNEL_FIELD_NAME),
                EPassSqliteStoreOpenHelper.SCHEMA_CATEGORY);
        
        setVersion();
    }
    private void setVersion() {
        TextView textView = (TextView) findViewById(R.id.version);
        textView.setVisibility(View.VISIBLE);
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            textView.setText("v" + versionName + "." + versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void logout() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        
        ((AppApplication) getApplication()).getRestStore().logout(new AuthorizationCallback() {

            @Override
            public void success(IdToken token) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {                    
                        loadingDialog.dismiss();
                        handleLogout();
                    }

                });
            }

            @Override
            public void failure(DatastoreException ex) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        handleLogout();
                    }

                });
            }

        });
    }

    private void getCategoryChannel(Store store, Query query, String source) {
        store.performQuery(query, source, new OAuthRestStoreCallback(this) {
            @Override
            public void success(final DataElement element, String resource) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        MenuAdapter menuAdapter = new MenuAdapter(UserInfoActivity.this, element);
                        listView.setAdapter(menuAdapter);
                    }

                });

            }

            @Override
            public void fail(DatastoreException ex, String resource) {

            }

        });
    }

    private void startSearch(String keyword) {
    	 String regionString = SharedPreferenceManager.getAppStoreRegion(this);
         JsonObjectElement region = regionString == null ? null : new JsonObjectElement(regionString);
         String regionName = null;
         DataElement regionNameElement = region == null ? null : region.get(Region.NAME_FIELD_NAME);
         if (regionNameElement != null && regionNameElement.isPrimitive()) {
         	regionName = regionNameElement.asPrimitiveElement().valueAsString();
         }
        ((AppApplication) getApplication()).getSqliteStore()
        .performRawQuery(getArticleDetailRawSql(keyword, regionName),
                EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE, new StoreCallback() {

                    @Override
                    public void success(DataElement element, String resource) {
                        if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
                            final List<String> articleIds = new ArrayList<String>();
                            for (DataElement e : element.asArrayElement()) {
                                DataElement id = e.asObjectElement().get(Article.ID_FIELD_NAME);
                                if (id != null && id.isPrimitive()) {
                                    articleIds.add(id.asPrimitiveElement().valueAsString());
                                }
                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    handleSearchResult(articleIds, false);

                                }

                            });
                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    handleSearchResult(null, false);
                                }

                            });
                        }
                    }

					@Override
					public void failure(DatastoreException ex, String resource) {
						runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                handleSearchResult(null, false);
                            }

                        });						
					}

                });
    }

    private void handleSearchResult(List<String> channelIds, boolean cleanResult) {
        FragmentManager manager = getSupportFragmentManager();
        findViewById(R.id.profile_wrapper).setVisibility(View.GONE);
        Fragment fragment = manager.findFragmentByTag(SearchResultFragment.class.getSimpleName());
        if (fragment == null) {
            Bundle bundle = new Bundle();
            if (channelIds != null) {
                String[] ids = new String[channelIds.size()];
                for (int i = 0; i < channelIds.size(); i++) {
                    ids[i] = channelIds.get(0);
                }
                bundle.putStringArray(SearchResultFragment.KEY_ID, ids);
            }
            bundle.putBoolean(SearchResultFragment.KEY_IS_EMPTY, cleanResult);

            fragment = new SearchResultFragment();
            fragment.setArguments(bundle);

            manager.beginTransaction().replace(R.id.content_wrapper, fragment, SearchResultFragment.class.getSimpleName()).addToBackStack(SearchResultFragment.class.getSimpleName()).commit();
        } else {
            ((SearchResultFragment) fragment).search(channelIds);
            if (cleanResult) {
                ((SearchResultFragment) fragment).resetSearchResult();
            }
        }

    }

    public void getUserInfoData() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
        String password = pref.getString(HomeActivity.KEY_PASSWORD, null);

        ((AppApplication) getApplication()).getRestStore().performQuery(new Query().fieldIsEqualTo("username", userId).fieldIsEqualTo("password", password).fieldIsEqualTo("key", CHECK_KEY),
                "user_info", new OAuthRestStoreCallback(this) {
                    @Override
                    public void success(final DataElement element, String resource) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                if (element != null && element.isObject()) {
                                    DataElement flocalnameDataElement = element.asObjectElement().get(UserInfo.FLOCAL_NAME);
                                    String flocalname = "";
                                    if (flocalnameDataElement != null && flocalnameDataElement.isPrimitive()) {
                                        flocalname = flocalnameDataElement.asPrimitiveElement().valueAsString();

                                    }
                                    DataElement fdepartmentnameElement = element.asObjectElement().get(UserInfo.FDEPARTMENT_NAME);
                                    String fdepartmentname = "";
                                    if (fdepartmentnameElement != null && fdepartmentnameElement.isPrimitive()) {
                                        fdepartmentname = fdepartmentnameElement.asPrimitiveElement().valueAsString();

                                    }
                                    TextView flocalnameTv = (TextView) findViewById(R.id.flocalname);
                                    TextView fdepartmentnameTv = (TextView) findViewById(R.id.fdepartmentname);
                                    flocalnameTv.setText(flocalname);
                                    fdepartmentnameTv.setText(fdepartmentname);
                                }

                            }

                        });

                    }

                    @Override
                    public void fail(DatastoreException ex, String resource) {
                    	Log.e("UserInfoActivity", ex.getMessage());
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                loadingDialog.dismiss();

                            }

                        });
                    }

                });
    }

    public void cancelSearch() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(R.id.content_wrapper) != null) {
            manager.popBackStack();
            EditText searchView = (EditText) findViewById(R.id.search);
            searchView.setText("");
            findViewById(R.id.profile_wrapper).setVisibility(View.VISIBLE);
        }

    }

    private void handleLogout() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(UserInfoActivity.this);
        pref.edit().remove(EPassRestStoreClient.KEY_TOKEN)
        .remove(SharedPreferenceManager.KEY_USER_NAME).commit();
        Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
    
    private String getLikeQueryStatement(String keyword) {
    	return  "SELECT DISTINCT articles.id, articles.title "
    			+ "FROM articles LEFT JOIN article_contents ON article_contents.article_id = "
    			+ "articles.id WHERE articles.title LIKE '%"
    			+ keyword + "%' OR article_contents.text_content LIKE "
    			+ "'%" + keyword + "%' group by articles.id";
    }
    
    private String getArticleDetailRawSql(String keyword, String regionName) {
    	String defaultRegionWhere = "regions.name = '" + Region.DEFAULT_REGION_NAME + "'";
    	String regionWhere = regionName == null || Region.DEFAULT_REGION_NAME.equals(regionName) ?
    			defaultRegionWhere : defaultRegionWhere + " OR regions.name = '" + regionName + "'";
    	
    	return "SELECT distinct article_regions.article_id as " + Article.ID_FIELD_NAME + ", articles.title FROM " +
    			"article_regions left join articles on (article_regions.article_id = articles.id) " +
    			"left join article_contents on (article_contents.article_id = articles.id) left " +
    			"join regions on (regions.region_id = article_regions.region_id) where articles.title " +
    			"LIKE '%" + keyword + "%' AND " + regionWhere + " group by articles.id";
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.REQUEST_CODE_LANGUAGE_CHANGE && resultCode == Activity.RESULT_OK) {
    		TextView title = (TextView) findViewById(R.id.title);
    		title.setText(R.string.content);
    		TextView logoutTextView = (TextView) logoutButtonView.findViewById(R.id.logout);
    		logoutTextView.setText(R.string.logout);
    		if (listView.getAdapter() instanceof HeaderViewListAdapter) {
    			((BaseAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
    		}
    	}
    	
    	if (data != null && data.getExtras() != null
    			&& data.getExtras().getBoolean(Constants.INTENT_EXTRA_RESULT_SUBSCRIBED_CHANNEL_CHANGES)) {
    		setResult(Activity.RESULT_OK, data);
    	}
    }

}
