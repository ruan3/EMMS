package com.esquel.epass.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.dynamicchannel.DragEditChannelActivity;
import com.esquel.epass.schema.Region;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.LocaleUtils;
import com.esquel.epass.utils.LocaleUtils.SupportedLanguage;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.Utility;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.widget.DataAdapter;
import com.joyaether.datastore.widget.DataCache;

/**
 * 
 * @author joyaether
 * 
 */
public class ProfileDetailActivity extends BaseGestureActivity {

	private int selected = -1;
    private int selectedValue = 1;
    private TextView numberItem;
    private TextView regionName;
    private RegionDataAdapter regionDataAdapter;
    private static final int DEFAULT_USER_CHANNEL_COUNT = 5;
    private Intent resultIntent = new Intent();
    private int resultCode = Activity.RESULT_CANCELED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

	private void initView() {
		setContentView(R.layout.activity_profile_detail);
        numberItem = (TextView) findViewById(R.id.numberItem);
        numberItem.setText(Utility.getTypeShow(this) == 6 ?  getString(R.string.scroll_view) : Utility.getTypeShow(this) + "");
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        
        initRegion();
        initLanguageSelect();
        initSubscribedChannel();
	}
	
	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initView();
	}
    
    private void initSubscribedChannel() {
    	int subscribedChannelNumber = SharedPreferenceManager.getUserChannelCount(this, DEFAULT_USER_CHANNEL_COUNT);
    	View subscribedChannelView = findViewById(R.id.select_user_channel);
    	TextView numberView = (TextView) subscribedChannelView.findViewById(R.id.user_channel_number_item);
    	numberView.setText(subscribedChannelNumber + "");
    	subscribedChannelView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProfileDetailActivity.this, DragEditChannelActivity.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL);
			}
    		
    	});
    }
    
    private void initLanguageSelect() {
    	View languageSelectView = findViewById(R.id.language_select);
    	TextView languageView = (TextView) languageSelectView.findViewById(R.id.language_name);
    	languageSelectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectedNumber = 0;
				SupportedLanguage language = LocaleUtils.getLanguage(ProfileDetailActivity.this);
		    	if (language == SupportedLanguage.ENGLISH) {
		    		selectedNumber = 1;
		    	} else if (language == SupportedLanguage.VIETNAMESE) {
		    		selectedNumber = 2;
		    	} else {
		    		selectedNumber = 0;
		    	}
				openLanguageDialog(selectedNumber);
			}
    		
    	});
    	String currentLanguage = "";
    	SupportedLanguage language = LocaleUtils.getLanguage(this);
    	if (language == SupportedLanguage.ENGLISH) {
    		currentLanguage = getString(R.string.english);
    	} else if (language == SupportedLanguage.VIETNAMESE) {
    		currentLanguage = getString(R.string.vietnamese);
    	} else {
    		currentLanguage = getString(R.string.chinese);
    	}
    	languageView.setText(currentLanguage);
    }
    
    private void openLanguageDialog(int selectedNumber) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	CharSequence[] array = {getString(R.string.chinese), getString(R.string.english), getString(R.string.vietnamese)};
    	builder.setTitle(getResources().getString(R.string.langugage));
    	builder.setSingleChoiceItems(array, selectedNumber, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SupportedLanguage language = null;
				switch (which) {
				case 0:
					language = SupportedLanguage.CHINESE_SIMPLFIED;
					break;
				case 1:
					language = SupportedLanguage.ENGLISH;
					break;
				case 2:
					language = SupportedLanguage.VIETNAMESE;
					break;
					default:
						language = SupportedLanguage.CHINESE_SIMPLFIED;
						break;
				}
				LocaleUtils.setLanguage(ProfileDetailActivity.this, language);
				dialog.dismiss();
				initView();
				resultCode = Activity.RESULT_OK;
			}
		});
    	builder.show();
    }

    public void selectNumber(View v) {
        int position = Utility.getTypeShow(this) - 1;
        Dialog dialog = onCreateDialogSingleChoice(position);
        dialog.show();

        selectedValue = position + 1;
    }
  
    
    public Dialog onCreateDialogSingleChoice(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = { "1", "2", "3", "4", "5", getString(R.string.scroll_view) };
        builder.setTitle(getResources().getString(R.string.select))
        .setSingleChoiceItems(array, position, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                selected = which + 1;

            }
        })

        // Set the action buttons
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (selectedValue != selected) {
                            Utility.setTypeShow(ProfileDetailActivity.this, selected < 1 ? 1 : selected);
                            selectedValue = selected;
                            int result = selectedValue < 1 ? 1 : selectedValue;
                            
                            numberItem.setText("" + (result == 6 ? getString(R.string.scroll_view) : result));

                        }

                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
    
    private void initRegion() {
    	findViewById(R.id.select).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRegionDialog();
			}
        	
        });
    	//ensure the data is cahced
    	regionDataAdapter = new RegionDataAdapter(getSqliteStore(), EPassSqliteStoreOpenHelper.SCHEMA_REGION, new Query());
    	regionName = (TextView) findViewById(R.id.region_name);
		String regionJsonString = SharedPreferenceManager.getAppStoreRegion(ProfileDetailActivity.this);
    	if (regionJsonString != null) {
    		//already select the region before
        	JsonObjectElement regionObject = new JsonObjectElement(regionJsonString);
        	DataElement e = regionObject.get("name");
        	if (e != null && e.isPrimitive()) {
        		regionName.setText(e.asPrimitiveElement().valueAsString());
        	}
    	} else {
    		showLoadingDialog();
    		Futures.addCallback(cacheRegionData(), new FutureCallback<Boolean>() {
    			@Override
        		public void onSuccess(Boolean result) {
    				//set default region & use the first item as default
    				DataElement firstItem = (DataElement) regionDataAdapter.getItem(0);
    				SharedPreferenceManager.setAppStoreRegion(ProfileDetailActivity.this, firstItem.toJson());
    				final DataElement e = firstItem.asObjectElement().get("name");
    	        	if (e != null && e.isPrimitive()) {
    	        		runOnUiThread(new Runnable() {

    						@Override
    						public void run() {
    							dismissLoadingDialog();
    							regionName.setText(e.asPrimitiveElement().valueAsString());							
    						}
    						
    					});
    	        	}
    				
        		}

    			@Override
    			public void onFailure(Throwable arg0) {
    				runOnUiThread(new Runnable() {

    					@Override
    					public void run() {
    						dismissLoadingDialog();
    						regionName.setText("");							
    					}
    					
    				});
    			}
    			
    		});
    		
    	}
		
    	
    	
    }
    
    /**
     * Get the region data in the {@link DataCache} before show in the view to 
     * prevent that can not show in the adapter with some reason.
     * @return the future to notify the process is finished.
     */
    private SettableFuture<Boolean> cacheRegionData() {
		final SettableFuture<Boolean> future = SettableFuture.create();

    	ListenableFuture<DataElement> data = regionDataAdapter.getCache().getData(0l);
    	Futures.addCallback(data, new FutureCallback<DataElement>() {
    		
    		@Override
    		public void onSuccess(DataElement result) {
    			future.set(true);
    		}

			@Override
			public void onFailure(Throwable arg0) {
				future.setException(arg0);
			}
    		
    	});
    	return future;
    }
    
    private void showRegionDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.region_dialog_title);
    	builder.setAdapter(regionDataAdapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DataElement item = (DataElement) regionDataAdapter.getItem(which);
				SharedPreferenceManager.setAppStoreRegion(ProfileDetailActivity.this, item.toJson());
				if (item != null && item.isObject()) {
					DataElement name = item.asObjectElement().get(Region.NAME_FIELD_NAME);
					if (name != null && name.isPrimitive()) {
						regionName.setText(name.asPrimitiveElement().valueAsString());
					}
				}
			}
		});
    	builder.show();
    }
    
    
    private class RegionDataAdapter extends DataAdapter {

		public RegionDataAdapter(Store store, String schema, Query query) {
			super(store, schema, query);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		protected void onDataAvailable(final DataElement data, final View view) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (data != null && data.isObject() && view instanceof TextView) {
						DataElement e = data.asObjectElement().get("name");
						if (e != null && e.isPrimitive()) {
							((TextView) view).setText(e.asPrimitiveElement().valueAsString());
						}
					}					
				}
				
			});
			
		}

		@Override
		public View getInflatedView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(ProfileDetailActivity.this, android.R.layout.simple_list_item_1, null);
			}
			return view;
		}
    	
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL &&
    			resultCode == Activity.RESULT_OK) {
    		int subscribedChannelNumber = SharedPreferenceManager.getUserChannelCount(this, DEFAULT_USER_CHANNEL_COUNT);
        	View subscribedChannelView = findViewById(R.id.select_user_channel);
        	TextView numberView = (TextView) subscribedChannelView.findViewById(R.id.user_channel_number_item);
        	numberView.setText(subscribedChannelNumber + "");
        	resultIntent.putExtra(Constants.INTENT_EXTRA_RESULT_SUBSCRIBED_CHANNEL_CHANGES, true);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	setResult(resultCode, resultIntent);
    	super.onBackPressed();
    }

}
