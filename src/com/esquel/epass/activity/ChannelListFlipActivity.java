package com.esquel.epass.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.restlet.data.Reference;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.ConfigurationManager;
import com.esquel.epass.DownloadTask;
import com.esquel.epass.R;
import com.esquel.epass.RetrieveNewsDatabaseManager;
import com.esquel.epass.adapter.ChannelAdapter;
import com.esquel.epass.adapter.ChannelAdapter.ChannelItem;
import com.esquel.epass.adapter.ChannelAdapter.OnChannelItemClickListener;
import com.esquel.epass.adapter.ChannelAdapter.OnClickAddItemListener;
import com.esquel.epass.appstore.AppStoreActivity;
import com.esquel.epass.appstore.LeaderBoardActivity;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.delegate.DownloadCallback;
import com.esquel.epass.delegate.SelectChannel;
import com.esquel.epass.dynamicchannel.DragEditChannelActivity;
import com.esquel.epass.leave.AnnualAppliedLeaveActivity;
import com.esquel.epass.lib.flipview.FlipView;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.oauth.OAuthToken;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Category;
import com.esquel.epass.schema.Channel;
import com.esquel.epass.schema.Region;
import com.esquel.epass.schema.ReleasedApplicationVersion;
import com.esquel.epass.schema.SystemApplication;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.schema.UserChannel.UserChannelType;
import com.esquel.epass.utils.BuildConfig;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.EsquelPassRegion;
import com.esquel.epass.utils.LogUtils;
import com.esquel.epass.utils.PackageUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.Utility;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.util.ImageDownloader;
import com.joyaether.datastore.util.ImageDownloader.Mode;
import com.joyaether.datastore.widget.DataAdapter;
import com.umeng.analytics.MobclickAgent;

/**
 * A class of {@link Activity} to showing the channel list information.
 * 
 * @author joyaether
 * 
 */
public class ChannelListFlipActivity extends BaseFlipActivity implements SelectChannel, OnClickListener, OnChannelItemClickListener, OnClickAddItemListener {
	private static final int FLIPVIEW_UI_UPDATE_INTERVAL = 1000;
	private static final int ID_EPAYSLIP = 1;
	private static final int ID_TASK = 2;
	private static final int ID_LEAVE = 3;
	private static final int PROGRESS_FINISH = 100;
	private static final int INDEX_SYSTEM_APP_EPAY_SLIP = 1;
	private static final int INDEX_SYSTEM_APP_TASK = 2;
	private static final int INDEX_SYSTEM_APP_LEAVE = 3;
	private static final int INDEX_SYSTEM_APP_APP_STORE = 4;
	private Animation refreshAnimation;
    private ImageView imgRefresh;
    private TextView titleRefresh;
    private int selected;
	private static final int NOTIFICATION_ID = 1001;
	public static final int REQUEST_CODE_INSTALL_APP = 1003;  
	private List<ObjectElement> subScribedChannels = new ArrayList<ObjectElement>();
	private DatabaseDownloadTaskReceiver receiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.endPoint == BuildConfig.ServerEndPoint.DEVELOPMENT) {
        	//must call a protected resource as the first call in development server
        	//so that can be use the cached token to get the other resource
//        	callAProtectedServerResource();
        }
        View bottomView = findViewById(R.id.bottom_menu);
        bottomView.bringToFront();
        imgRefresh = (ImageView) findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(this);
        titleRefresh = (TextView) findViewById(R.id.title_refresh);
        titleRefresh.setOnClickListener(this);
        titleRefresh.setVisibility(View.INVISIBLE);
        AnimationUtils.loadAnimation(this, R.anim.cycle);
        refreshAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        initDownloadTaskReceiver();
        if (isContentDBExist()) {
            RetrieveNewsDatabaseManager manager = RetrieveNewsDatabaseManager.getInstance(this);
            if (manager.isTaskRunning()) {
            	startLoadingAnimation();
            } else {
            	showLoadingDialog();
            	getChannelItem(false);
            }    	
        } else {
            getNewDataFromServer(false);
        }

        int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
        final int api18 = 18;
        if (currentAPIVersion == api18) {
            flipView.set18API(true);
        }

        setMenu();
        ConfigurationManager.getInstance().startToGetNewConfig(this);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        
        initDownloadTaskReceiver();
    }
    
    private void initDownloadTaskReceiver() {
    	if (receiver == null) {
    		receiver = new DatabaseDownloadTaskReceiver();
    	}
    	IntentFilter filter = new IntentFilter(RetrieveNewsDatabaseManager.INTENT_FILTER_DOWNLOAD_TASK_RUNNING);
    	registerReceiver(receiver, filter);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (receiver != null) {
    		unregisterReceiver(receiver);
    	}
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
    
    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {
        TextView textView = (TextView) findViewById(R.id.page_number);
        textView.setText((position + 1) + "/" + flipView.getAdapter().getCount());

    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (isApplicationBroughtToBackground()) {
            ConfigurationManager.getInstance().startToGetNewConfig(this);
        }
        getImage(true);
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return topActivity.getClassName().equals(getClass().getName());
        }
        return false;
    }

    private void setMenu() {
        ImageButton menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = Utility.getTypeShow(ChannelListFlipActivity.this) - 1;
                Dialog dialog = onCreateDialogSingleChoice(position);
                dialog.show();

            }
        });

    }

    private void startLoadingAnimation() {
        imgRefresh.setImageResource(R.drawable.icon_loading);
        imgRefresh.startAnimation(refreshAnimation);
        titleRefresh.setVisibility(View.INVISIBLE);
    }

    private void finishLoadingAnimation() {
        imgRefresh.setImageResource(R.drawable.icon_refresh);
        refreshAnimation.cancel();
    }

    private boolean isContentDBExist() {
        String region = EsquelPassRegion.getDefault(this).toString();
        File file = new File(getExternalFilesDir(null), region + ".db");
        return file.exists();
    }

    private boolean isDownloadDBExist() {
        String dbName = EsquelPassRegion.getDefault(this).toString() + ".db";
        File downloadDB = new File(this.getExternalFilesDir(null).getParent() + "/" + RetrieveNewsDatabaseManager.DOWNLOAD_DB_PATH, dbName);
        return downloadDB.exists();
    }

    @Override
    public void selectChannel(long channelId, String title) {
        if (channelId == Long.MAX_VALUE) {
            Intent intent = new Intent(ChannelListFlipActivity.this, SlipActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (channelId == Long.MAX_VALUE - 1) {
            Intent intent = new Intent(ChannelListFlipActivity.this, TaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (channelId == Long.MAX_VALUE - 2) {
        	Intent intent = new Intent(ChannelListFlipActivity.this, AnnualAppliedLeaveActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (channelId == Long.MAX_VALUE - 3) {
            Intent intent = new Intent(ChannelListFlipActivity.this, LeaderBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
        	Intent intent = new Intent(ChannelListFlipActivity.this, ArticleListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("id", channelId);
            intent.putExtra(KEY_TITLE, title);
            intent.putExtra(Constants.INTENT_EXTRA_REQUEST_CODE, Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
            startActivityForResult(intent, Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
        }

    }

    @Override
    protected DataAdapter getAdapter() {
        return null;
    }

    @Override
    public void syncData() {
        getNewDataFromServer(false);
    }

    /**
	 * 
	 */
    private void getNewDataFromServer(final boolean refresh) {
    	LogUtils.e("开始从服务器获取数据--->"+refresh);
        startLoadingAnimation();
        RetrieveNewsDatabaseManager manager = RetrieveNewsDatabaseManager.getInstance(this);
        LogUtils.e("开始从服务器获取数据路径--->"+manager.DOWNLOAD_DB_PATH);
        
        if (!refresh) {
            showLoadingDialog();
        }
        manager.setCallback(new DownloadCallback() {

            @Override
            public void success(final boolean hasUpdate) {
            	setDefaultRegion(new StoreCallback() {

					@Override
					public void success(DataElement element, String resource) {
						LogUtils.e("elemnet--->"+element+"resource--->"+resource);
						if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
							SharedPreferenceManager.setAppStoreRegion(ChannelListFlipActivity.this, element.asArrayElement().get(0).toJson());
						}
						if (!refresh) {           		
		                	getChannelItem(false);
		            	}

		                runOnUiThread(new Runnable() {

		                    @Override
		                    public void run() {
		                        if (refresh) {
		                        	dismissLoadingDialog();
		                            if (hasUpdate) {
		                                titleRefresh.setVisibility(View.VISIBLE);
		                            }                          
		                        }
		                        finishLoadingAnimation();
		                    }
		                });
					}

					@Override
					public void failure(DatastoreException ex, String resource) {
						LogUtils.e("ex--->"+ex.toString()+"resource--->"+resource);
					}
            		
            	});

            	
            }

            @Override
            public void fail(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	LogUtils.e("加载文章失败--->");
                    	dismissLoadingDialog();
                        finishLoadingAnimation();
                        if (!isContentDBExist()) {
                            showErrorDownloadDatabaseDialog();
                        }
                    }
                });
            }
        });
        manager.startDownload();
    }

    private void showErrorDownloadDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.center_alignment_textview, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(R.string.error_channel);
        builder.setView(view);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNewDataFromServer(false);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_refresh) {
            if (isDownloadDBExist()) {
                titleRefresh.setVisibility(View.INVISIBLE);
                handleUpdateDatabase(v);
            } else {
                syncData();
            }
//        	getChannelItem();
        } else if (v.getId() == R.id.title_refresh) {
            handleUpdateDatabase(v);
        }
    }

    private void handleUpdateDatabase(View v) {

        final int timeDelay = 100;
        try {
            ((AppApplication) getApplication()).replaceDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flipView.post(new Runnable() {
                    @Override
                    public void run() {
//                        getChannels();
//                    	getChannelItem();
                    	getChannelItem(true);
                    }

                });
            }
        }, timeDelay);
        v.setVisibility(View.INVISIBLE);
    }

    public Dialog onCreateDialogSingleChoice(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = { "1", "2", "3", "4", "5", getString(R.string.scroll_view) };
        builder.setTitle(getResources().getString(R.string.select)).setSingleChoiceItems(array, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = which + 1;
            }
        }).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Utility.setTypeShow(ChannelListFlipActivity.this, selected);

            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        return builder.create();
    }
    /**
     * 获取图片的方法
     * @param restart
     */
    private void getImage(final boolean restart) {
    	ExecutorService excutor = Executors.newSingleThreadExecutor();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (ObjectElement item : subScribedChannels) {
                	DataElement e = item.get(ChannelItem.IMAGE_URL_FIELD_NAME);
                	if (e != null && e.isPrimitive()) {
                		ImageDownloader downloader = ((AppApplication) getApplication()).getImageDownloader();
                		LogUtils.e("下载地址---->"+e.asPrimitiveElement().valueAsString());
                        downloader.download(e.asPrimitiveElement().valueAsString(), R.drawable.icon_loading_image, Constants.DEFAULT_IMAGE_WIDTH,
                        		Constants.DEFAULT_IMAGE_HEIGHT, null, Bitmap.Config.ARGB_8888, Mode.NO_ASYNC_TASK);
                	}
                }
                if (restart) {
                	runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (flipView.getAdapter() != null) {
					        	flipView.postDelayed(new Runnable() {

									@Override
									public void run() {
										((BaseAdapter) flipView.getAdapter()).notifyDataSetChanged();
									}
					        		
					        	}, FLIPVIEW_UI_UPDATE_INTERVAL);
					        }							
						}
                		
                	});
                }
            }

        };
        excutor.submit(runnable);
    }

	@Override
	public void onClickItem() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.dialog_option_app_n_channel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ChannelListFlipActivity.this, 
						which == 0 ? DragEditChannelActivity.class : LeaderBoardActivity.class);
				int requestCode = which == 0 ? Constants.REQUEST_CODE_CHANNEL_ADD_CHANNEL
						: Constants.REQUEST_CODE_CHANNEL_ADD_APPLICATION;
				intent.putExtra(AppStoreActivity.EXTRA_REQUEST_CODE, requestCode);
				startActivityForResult(intent, requestCode);
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == Constants.REQUEST_CODE_CHANNEL_ADD_CHANNEL || requestCode == Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST)
				&& resultCode == Activity.RESULT_OK) {
			showLoadingDialog();
			getOfflineUserChannel(true);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {					
					dismissLoadingDialog();
				}
        		
        	});
		} else if (requestCode == Constants.REQUEST_CODE_CHANNEL_ADD_APPLICATION && resultCode == Activity.RESULT_OK){
			showLoadingDialog();
			getChannelItem(true);
		} else if (data != null && data.getExtras() != null
				&& data.getExtras().getBoolean(Constants.INTENT_EXTRA_RESULT_SUBSCRIBED_CHANNEL_CHANGES)) {
			getOfflineUserChannel(true);		
		}
	}
    
    private void getOfflineUserChannel(boolean animate) {
    	JsonArrayElement array = new JsonArrayElement(new JsonArray());
    	String userChannel = SharedPreferenceManager.getUserChannel(this);
    	if (userChannel != null) {
    		array = new JsonArrayElement(userChannel);
    	}
    	for (DataElement object : array) {
    		if (object != null && object.isObject()) {
    			DataElement type = object.asObjectElement().get(UserChannel.TYPE_FIELD_NAME);
    			if (type != null && type.isPrimitive() && UserChannel.TYPE_SYSTEOM_APPLICATION
    					.equals(type.asPrimitiveElement().valueAsString())) {
    				initDynamicChannelList(true, array, animate);
    				return;
    			}
    		}
    	}
    	
    	initDynamicChannelList(false, array, animate);
    }
	
	private void getChannelItem(final boolean animate) {
		getRestStore().performQuery(new Query().fieldIsOrderedBy(UserChannel.SEQUENCE_FIELD_NAME, Ordering.ASCENDING),
				"user_channels", new StoreCallback() {

			@Override
			public void success(final DataElement element, String resource) {
				if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
					SharedPreferenceManager.setUserChannel(ChannelListFlipActivity.this, element.toJson());
					ListenableFuture<Boolean> result = hasInitalDynamicChannel();
					Futures.addCallback(result, new FutureCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean rst) {
							initDynamicChannelList(rst, element.asArrayElement(), animate);
						}

						@Override
						public void onFailure(Throwable arg0) {
							initDynamicChannelList(false, element.asArrayElement(), animate);
						}
						
					});
				} else {
					createDefaultChannel(animate);
				}
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				getOfflineUserChannel(animate);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {					
						dismissLoadingDialog();
					}
            		
            	});
			}
			
		});
	}
	
	private void getDownloadedApplications(List<ObjectElement> channelsList, HashMap<String, Integer> appIndexMap, List<String> applicationNames) {
		for (String packageName : applicationNames) {			
			CharSequence appName = null;
			try {
				appName = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,
						ApplicationInfo.FLAG_INSTALLED));
			} catch (Exception e) {
				android.util.Log.e(getClass().getSimpleName(), e.getMessage());
			}
			if (appName != null) {
				channelsList.get(appIndexMap.get(packageName)).set(ChannelItem.CHANNEL_NAME_FIELD_NAME, appName.toString());
			} else {
				channelsList.set(appIndexMap.get(packageName), null);
			}
		}
	}
	
	private ListenableFuture<Boolean> isApplicationDataReady(final List<ObjectElement> channels
			, final HashMap<String, Integer> appIndexMap, List<String> applicationIds) {
		final SettableFuture<Boolean> future = SettableFuture.create();
		getDownloadedApplications(channels, appIndexMap, applicationIds);
		future.set(true);
		return future;
	}
	
	private ListenableFuture<Boolean> isChannelDataReady(final List<ObjectElement> channels
			, final HashMap<Long, Integer> channelIndexMap, List<Long> channelIds) {
		final SettableFuture<Boolean> future = SettableFuture.create();
		ListenableFuture<DataElement> channelResult = getChannels(channelIds);
		Futures.addCallback(channelResult, new FutureCallback<DataElement>() {
			
			@Override
			public void onSuccess(DataElement element) {
				if (element != null && element.isArray()) {
					for (DataElement channel : element.asArrayElement()) {
						if (channel != null && channel.isObject()) {
							DataElement id = channel.asObjectElement().get(Channel.ID_FIELD_NAME);
							if (id != null && id.isPrimitive() && channelIndexMap.containsKey(id.asPrimitiveElement().valueAsLong())) {
								DataElement name = channel.asObjectElement().get(Channel.NAME_FIELD_NAME);
								if (name != null && name.isPrimitive()) {
									channels.get(channelIndexMap.get(id.asPrimitiveElement().valueAsLong()))
										.set(ChannelItem.CHANNEL_NAME_FIELD_NAME, name.asPrimitiveElement().valueAsString());
								}
							}
						}
					}
				}
								
				future.set(true);
			}

			@Override
			public void onFailure(Throwable arg0) {
				future.setException(arg0);
			}
			
		});
		return future;
	}
	
	private void initDynamicChannelList(final boolean alreadyAddedSystemApp, ArrayElement channelItems, boolean animate) {
		final List<String> applicationIds = new ArrayList<String>();
		final HashMap<String, Integer> appIndexMap = new HashMap<String, Integer>();
		final List<Long> channelIds = new ArrayList<Long>();
		final HashMap<Long, Integer> channelIndexMap = new HashMap<Long, Integer>();

		final List<ObjectElement> channels = new ArrayList<ObjectElement>();	
		int index = 0;
		for (DataElement dataElement : channelItems) {
			if (dataElement != null && dataElement.isObject()) {
				ObjectElement object = dataElement.asObjectElement();
				DataElement type = object.get(ChannelItem.TYPE_FIELD_NAME);
				DataElement identifier = object.get(ChannelItem.IDENTIFIER_FIELD_NAME);
				DataElement bundleIdentifier = object.get(ChannelItem.BUNDLE_IDENTIFIER);
				DataElement sequence = object.get(ChannelItem.SEQUENCE_FIELD_NAME);
				if (type != null && type.isPrimitive()) {
					if (UserChannel.TYPE_APPLICATION.equals(type.asPrimitiveElement().valueAsString())) {					
						if (sequence != null && sequence.isPrimitive()
								&& bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
							applicationIds.add(bundleIdentifier.asPrimitiveElement().valueAsString());
							appIndexMap.put(bundleIdentifier.asPrimitiveElement().valueAsString(), index);
						}
					} else if (UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
						if (identifier != null && identifier.isPrimitive()) {
							channelIds.add(identifier.asPrimitiveElement().valueAsLong());
						}
						if (sequence != null && sequence.isPrimitive()) {
							channelIndexMap.put(identifier.asPrimitiveElement().valueAsLong(), index);
						}
					} else {
						if (identifier != null && identifier.isPrimitive()) {
							object.set(ChannelItem.CHANNEL_NAME_FIELD_NAME, getSystemAppName(identifier.asPrimitiveElement().valueAsInt()));
							object.set(ChannelItem.IMAGE_RESOURCE_FIELD_NAME, getSystemAppDrawable(identifier.asPrimitiveElement().valueAsInt()));
						}
						
					}
				}
				int targetSequence = sequence != null && sequence.isPrimitive() ? sequence.asPrimitiveElement().valueAsInt() : 0;
				object.set(ChannelItem.SEQUENCE_FIELD_NAME, targetSequence);
				channels.add(object);
			}
			index++;
		}
		subScribedChannels.clear();
		subScribedChannels.addAll(channels);
		ListenableFuture<Boolean> appFuture = isApplicationDataReady(channels, appIndexMap, applicationIds);
		ListenableFuture<Boolean> channelFuture = isChannelDataReady(channels, channelIndexMap, channelIds);
		//wait the above 2 task to finish
		while (!appFuture.isDone() && !channelFuture.isDone()) {
			//waiting
		}
		
		String ids = "";
		for (long id : channelIndexMap.keySet()) {
			ids = ids + id + ",";
		}
		
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		
		setChannelDetails(alreadyAddedSystemApp, channelIndexMap, channels, ids, animate, getRegion());
	}
	
	private void createDefaultChannel(boolean animate) {
		JsonObjectElement object = new JsonObjectElement();
		object.set(ChannelItem.IDENTIFIER_FIELD_NAME, 1);
		object.set(ChannelItem.TYPE_FIELD_NAME, UserChannelType.CHANNEL.getType());
		object.set(ChannelItem.SEQUENCE_FIELD_NAME, 1);
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		map.put(1L, 0);
		subScribedChannels.add(object);
		setChannelDetails(false, map, subScribedChannels, "1", animate, getRegion());
	}
	
	private String getRegion() {
		String regionString = SharedPreferenceManager.getAppStoreRegion(this);
		String region = Region.DEFAULT_REGION_NAME;
		if (regionString != null) {
			JsonObjectElement regionElement = new JsonObjectElement(regionString);
			if (regionElement != null) {
				DataElement regionName = regionElement.get(Region.NAME_FIELD_NAME);
				if (regionName != null && regionName.isPrimitive()) {
					region = regionName.asPrimitiveElement().valueAsString();
				}
			}
		}
		return region;
	}
	
	private String getSystemAppName(int id) {
		if (id == SystemApplication.EPAY_SLIP.getId()) {
			return getString(R.string.channel_epay_slip);
		} else if (id == SystemApplication.TASK.getId()) {
			return getString(R.string.channel_to_do_list);
		} else if (id == SystemApplication.LEAVE.getId()) {
			return getString(R.string.channel_apply_leave);
		} else {
			return getString(R.string.channel_app_store);
		}
	}
	
	private int getSystemAppDrawable(int id) {
		if (id == SystemApplication.EPAY_SLIP.getId()) {
			return R.drawable.epay_slip;
		} else if (id == SystemApplication.TASK.getId()) {
			return R.drawable.task;
		} else if (id == SystemApplication.LEAVE.getId()) {
			return R.drawable.apply_leave;
		} else {
			return R.drawable.app_store;
		}
	}
	
	private ListenableFuture<DataElement> getChannels(List<Long> channelIds) {
		Query query = new Query();
		if (channelIds.size() > 0 && channelIds.size() < 2) {
			query.fieldIsEqualTo(Channel.ID_FIELD_NAME, channelIds.get(0));

		} else if (channelIds.size() > 1) {
			query.fieldIsIn(Channel.ID_FIELD_NAME, channelIds);

		}
		return getSqliteStore().performQuery(query,
				EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL, null);
	}
	
	private ObjectElement createSystemAppElement(int id, String name, int sequence, int imageResource) {
		JsonObjectElement object = new JsonObjectElement();
		object.set(ChannelItem.IDENTIFIER_FIELD_NAME, id);
		object.set(ChannelItem.CHANNEL_NAME_FIELD_NAME, name);
		object.set(ChannelItem.TYPE_FIELD_NAME, UserChannelType.SYSTEM_APPLICATION.getType());
		object.set(ChannelItem.IMAGE_RESOURCE_FIELD_NAME, imageResource);
		object.set(ChannelItem.SEQUENCE_FIELD_NAME, sequence);
		return object;
	}
	
	private ListenableFuture<Boolean> hasInitalDynamicChannel() {
		final SettableFuture<Boolean> result = SettableFuture.create();
		ListenableFuture<DataElement> future = getRestStore().count(new Query().fieldIsEqualTo(UserChannel.TYPE_FIELD_NAME,
				UserChannel.TYPE_SYSTEOM_APPLICATION), "user_channels", null);
		Futures.addCallback(future, new FutureCallback<DataElement>() {
			
			@Override
			public void onSuccess(DataElement dataElement) {
				if (dataElement.isPrimitive() && dataElement.asPrimitiveElement().isNumber()
						&& dataElement.asPrimitiveElement().valueAsInt() > 0) {
					result.set(true);
					return;
				}
				result.set(false);
			}

			@Override
			public void onFailure(Throwable arg0) {
				result.setException(arg0);
			}
			
		});
		return result;
	}
	
	private ListenableFuture<DataElement> getChannelDetail(String channelIds, String region) {
		String defaultRegionWhere = "regions.name = '" + Region.DEFAULT_REGION_NAME + "'";
    	String regionWhere = region == null || Region.DEFAULT_REGION_NAME.equals(region) ?
    			defaultRegionWhere : defaultRegionWhere + " OR regions.name = '" + region + "'";
    	
    	
		String rawQuery = "select t1.channel_id channel_id, sequence, name, number, image, filter_name, t1.id "
                + "   from ( select distinct c.id channel_id, c.sequence, c.name, count(a.id) number, ca.id id   "
                + "  from channels c inner join channel_articles ca on (c.id = ca.channel_id) inner   "
                + "  join articles a on (a.id = ca.article_id) inner join article_regions on (a.id = article_regions.article_id) inner join regions on " +
                "(article_regions.region_id = regions.region_id) where " + regionWhere + " group by c.id ) t1 inner join (  "
                + "   select ca2.channel_id channel_id, max(a2.article_date) article_date,  " + "   a2.image image from articles a2 inner join channel_articles ca2 on "
                + "(a2.id =     ca2.article_id) where image != '' group by ca2.channel_id ) t2 on t1.channel_id = t2.channel_id   "
                + "  inner join (select category_channels.channel_id channel_id, max(    category_channels.lastmoddate)," + " categories.filter_name  filter_name from categories "
                + " inner join category_channels on (category_channels.category_id = categories.id)   "
                + " where channel_id in (" + channelIds + ") "
                + " group by category_channels.channel_id) t3 on t1.channel_id = t3.channel_id "
                + "order by sequence asc ";
		return getSqliteStore().performRawQuery(rawQuery, EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL_ARTICLE, null);
	}

	@Override
	public void onChannelItemClick(ObjectElement item) {
		if (item != null) {
			DataElement type = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
			if (UserChannel.TYPE_SYSTEOM_APPLICATION.equals(type.asPrimitiveElement().valueAsString())) {
				DataElement id = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
				Intent intent = null;
				if (id.asPrimitiveElement().valueAsInt() == ID_EPAYSLIP) {
					intent = new Intent(this, SlipActivity.class);
				} else if (id.asPrimitiveElement().valueAsInt() == ID_TASK) {
					intent = new Intent(this, TaskActivity.class);
				} else if (id.asPrimitiveElement().valueAsInt() == ID_LEAVE) {
					intent = new Intent(this, AnnualAppliedLeaveActivity.class);
				} else {
					intent = new Intent(this, LeaderBoardActivity.class);
				}
				startActivityForResult(intent, Constants.REQUEST_CODE_CHANNEL_ADD_APPLICATION);
			} else if (UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
				DataElement name = item.asObjectElement().get(ChannelItem.CHANNEL_NAME_FIELD_NAME);
				DataElement id = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
				Intent intent = new Intent(this, ArticleListActivity.class);
				if (id != null && id.isPrimitive()) {
					intent.putExtra("id", id.asPrimitiveElement().valueAsLong());
				}
				if (name != null && name.isPrimitive()) {
					intent.putExtra(KEY_TITLE, name.asPrimitiveElement().valueAsString());	
				}
	            intent.putExtra(Constants.INTENT_EXTRA_REQUEST_CODE, Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
				startActivityForResult(intent, Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
			} else {
				DataElement bundleIdentifier = item.asObjectElement().get(ChannelItem.BUNDLE_IDENTIFIER);
				DataElement id = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
				if (bundleIdentifier != null && bundleIdentifier.isPrimitive() && id != null && id.isPrimitive()) {
					openApp(id.asPrimitiveElement().valueAsInt(), bundleIdentifier.asPrimitiveElement().valueAsString());
				}
			}
		}
	}
	
	protected void openApp(int appId, String packageName) {
//		packageName = "com.esquel.b2b";
    	Intent intent = PackageUtils.getPackageIntent(this, packageName);
		if (intent != null) {			
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			String token = pref.getString(EPassRestStoreClient.KEY_TOKEN, null);
			String idToken = pref.getString(EPassRestStoreClient.KEY_ID_TOKEN, null);
			
			if (token != null && idToken != null) {
				OAuthToken t = OAuthToken.deserialize(token);
				t.setIdToken(idToken);
				intent.putExtra(LeaderBoardActivity.EXTRA_OAUTH_TOKEN, t.toJson());
			}
			startActivity(intent);
		} else {
			showInstallationDialog(appId, packageName);		
		}
		
    }

	private void handleDownloadApp(int appId) {
		ListenableFuture<DataElement> future = getApplicationDownloadUrl(appId);
		Futures.addCallback(future, new FutureCallback<DataElement>() {
			
			@Override
			public void onSuccess(DataElement result) {
				if (result != null && result.isObject()) {
					DataElement releasedApplicationVersions = result.asObjectElement().get(Application.RELEASED_APPLICATION_VERSIONS_FIELD_NAME);
					if (releasedApplicationVersions != null && releasedApplicationVersions.isArray()
							&& releasedApplicationVersions.asArrayElement().size() > 0) {
						DataElement releasedApplicationVersion = releasedApplicationVersions.asArrayElement().get(0);
						if (releasedApplicationVersion != null && releasedApplicationVersion.isObject()) {
							final DataElement installUrl = releasedApplicationVersion.asObjectElement()
									.get(ReleasedApplicationVersion.INSTALLER_URL_FIELD_NAME);
							if (installUrl != null && installUrl.isPrimitive()) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										downloadApplication(installUrl.asPrimitiveElement().valueAsString());											
									}
									
								});
								return;
							}
						}
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(ChannelListFlipActivity.this, R.string.can_not_launch_the_app, Toast.LENGTH_SHORT).show();
					}
					
				});
			}

			@Override
			public void onFailure(Throwable t) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(ChannelListFlipActivity.this, R.string.can_not_launch_the_app, Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		});
	}
	
	private ListenableFuture<DataElement> getApplicationDownloadUrl(int id) {
		return getRestStore().performQuery(new Query().fieldIsEqualTo(Application.ID_FIELD_NAME, id)
				.expandField(Application.RELEASED_APPLICATION_VERSIONS_FIELD_NAME)
				.selectField(Application.RELEASED_APPLICATION_VERSIONS_FIELD_NAME + "/"
				+ ReleasedApplicationVersion.INSTALLER_URL_FIELD_NAME) , "applications", null);
	}
	
	private void downloadApplication(String url) {
		Reference source = new Reference(url);
		final File file = new File(getExternalFilesDir(null), source.getLastSegment());
		final String fileName = file.getAbsolutePath();
		Reference destination = new Reference(fileName);
		DownloadTask downloadTask = new DownloadTask(this, source, destination);
		
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);	
	    
		NotificationCompat.Builder builder = getNotificationBuilder();
		builder.setProgress(PROGRESS_FINISH, PROGRESS_FINISH, true);
		builder.setContentText(getString(R.string.downloading))
			.setProgress(0, 0, true)
			.setContentText(getString(R.string.app_downloading));
		
		
		Toast.makeText(this, R.string.downloading, Toast.LENGTH_SHORT).show();
		manager.notify(NOTIFICATION_ID, builder.build());
		downloadTask.setCallback(new DownloadCallback() {

			@Override
			public void success(boolean hasUpdate) {
				
			    runOnUiThread(new Runnable() {

					@Override
					public void run() {
						NotificationCompat.Builder builder = getNotificationBuilder();
						builder.setProgress(0, 0, false);
						builder.setContentText(getString(R.string.download_complete));
						builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(ChannelListFlipActivity.this);
						Intent intent = getInstallAppIntent(fileName);
					    stackBuilder.addParentStack(ChannelListFlipActivity.this);
					    stackBuilder.addNextIntent(intent);
						PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
						builder.setContentIntent(pendingIntent);
						
						manager.notify(NOTIFICATION_ID, builder.build());
						if (ChannelListFlipActivity.this != null) {
							Toast.makeText(ChannelListFlipActivity.this, R.string.download_complete, Toast.LENGTH_SHORT).show();
						}
						startActivityForResult(intent, REQUEST_CODE_INSTALL_APP);
					}
			    	
			    });
			}

			@Override
			public void fail(Exception e) {
				 runOnUiThread(new Runnable() {

						@Override
						public void run() {
							NotificationCompat.Builder builder = getNotificationBuilder();
							builder.setProgress(0, 0, false);
							builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
							builder.setContentText(getString(R.string.app_download_failed));
							manager.notify(NOTIFICATION_ID, builder.build());
							if (ChannelListFlipActivity.this != null) {
								Toast.makeText(ChannelListFlipActivity.this, R.string.app_download_failed, Toast.LENGTH_SHORT).show();
							}
						}
				 });
				
			}
			
		});
		downloadTask.download();
	}
	
	private Intent getInstallAppIntent(String fileName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
	    return intent;
	}
	
	protected Builder getNotificationBuilder() {
		NotificationCompat.Builder builder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(android.R.drawable.stat_sys_download)
		        .setContentTitle(getString(R.string.app_download_title))
		        .setContentText(getString(R.string.app_downloading));
		return builder;
		
	}

	private void setChannelDetails(final boolean alreadyAddedSystemApp,
			final HashMap<Long, Integer> channelIndexMap,
			final List<ObjectElement> channels, String ids, final boolean animate,
			String region) {
		ListenableFuture<DataElement> future = getChannelDetail(ids, region);
		Futures.addCallback(future, new FutureCallback<DataElement>() {
			@Override
			public void onSuccess(DataElement result) {
				if (result != null && result.isArray() && result.asArrayElement().size() > 0) {
					
//					SharedPreferenceManager.setUserChannel(ChannelListFlipActivity.this, result.toJson());
					for (DataElement channel : result.asArrayElement()) {
						if (channel != null && channel.isObject()) {
							DataElement e = channel.asObjectElement().get("channel_id");
							long id = 0L;
							if (e != null && e.isPrimitive()) {
								id = e.asPrimitiveElement().valueAsLong();
							}

							if (id > 0L && channelIndexMap.containsKey(e.asPrimitiveElement().valueAsLong()) && channels.get(channelIndexMap.get(id)) != null) {
								e = channel.asObjectElement().get(Article.IMAGE_FIELD_NAME);
								if (e != null && e.isPrimitive()) {
									channels.get(channelIndexMap.get(id)).set(ChannelItem.IMAGE_URL_FIELD_NAME,
											e.asPrimitiveElement().valueAsString());												
								}
								e = channel.asObjectElement().get("number");
								if (e != null && e.isPrimitive()) {
									channels.get(channelIndexMap.get(id)).set(ChannelItem.ARTICLE_NUMBER_FIELD_NAME,
											e.asPrimitiveElement().valueAsInt());												
								}
								try {
									e = channel.asObjectElement().get(Category.FILTER_NAME_FIELD_NAME);
									if (e != null && e.isPrimitive()) {
										channels.get(channelIndexMap.get(id)).set(ChannelItem.FILTER_IMAGE_FIELD_NAME,
												e.asPrimitiveElement().valueAsString());												
									}
								} catch (Exception ex) {
									
								}
								e = channel.asObjectElement().get("name");
								if (e != null && e.isPrimitive()) {
									channels.get(channelIndexMap.get(id)).set(ChannelItem.CHANNEL_NAME_FIELD_NAME,
											e.asPrimitiveElement().valueAsString());												
								}
								
							}
							
						}

					}
				}
				if (!alreadyAddedSystemApp) {				
					channels.add(INDEX_SYSTEM_APP_EPAY_SLIP, createSystemAppElement(SystemApplication.EPAY_SLIP.getId(),
							getString(R.string.channel_epay_slip), SystemApplication.EPAY_SLIP.getId() + 1,
							R.drawable.epay_slip));
					channels.add(INDEX_SYSTEM_APP_TASK, createSystemAppElement(SystemApplication.TASK.getId(),
							getString(R.string.channel_to_do_list), SystemApplication.TASK.getId() + 1,
							R.drawable.task));
					channels.add(INDEX_SYSTEM_APP_LEAVE, createSystemAppElement(SystemApplication.LEAVE.getId(),
							getString(R.string.channel_apply_leave), SystemApplication.LEAVE.getId() + 1, 
							R.drawable.apply_leave));
					channels.add(INDEX_SYSTEM_APP_APP_STORE, createSystemAppElement(SystemApplication.APP_STORE.getId(),
							getString(R.string.channel_app_store), SystemApplication.APP_STORE.getId() + 1,
							R.drawable.app_store));
				}
				final List<ObjectElement> results = new ArrayList<ObjectElement>();
				for (int i = 0; i < channels.size(); i++) {
					if (channels.get(i) != null) {
						results.add(channels.get(i));
					}
				}
				Log.d("setChannelDetails", "" + results.size());
				SharedPreferenceManager.setUserChannelCount(ChannelListFlipActivity.this, results.size());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						dismissLoadingDialog();
						ChannelAdapter channelAdapter = new ChannelAdapter(ChannelListFlipActivity.this, results);
						channelAdapter.setOnChannelItemClickListener(ChannelListFlipActivity.this);
						channelAdapter.setOnClickAddItemListener(ChannelListFlipActivity.this);
						channelAdapter.setImageDownloader(getImageDownloader());
						flipView.setAdapter(channelAdapter);
						channelAdapter.notifyDataSetChanged();
						if (animate) {
							startFlipAnimation();
						}
					}
					
				});
			}

			@Override
			public void onFailure(Throwable arg0) {
			}
		});
	}
	
	private void startFlipAnimation() {
		BaseAdapter adapter = (BaseAdapter) flipView.getAdapter();
		if (adapter.getCount() > 1) {
			flipView.flipTo(1);
	        flipView.smoothFlipTo(0);
		}		
		 TextView textView = (TextView) findViewById(R.id.page_number);
         if (textView.getText() == null || textView.getText().length() == 0) {
             textView.setText("1/" + adapter.getCount());
         }
	}
	
	/**
	 * The receiver to receiver a intent when the content database is download complete.
	 *
	 */
	private class DatabaseDownloadTaskReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(RetrieveNewsDatabaseManager.INTENT_ACTION_DOWNLOAD_TASK_RUNNING)) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						finishLoadingAnimation();
					}
					
				});
			}
		}
		
	}
	
	private void showInstallationDialog(final int appId, final String packageName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.application_download_alert);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				handleDownloadApp(appId);
			}
		});
		builder.show();
	}
	
	private void setDefaultRegion(StoreCallback storeCallback) {
		String region = SharedPreferenceManager.getAppStoreRegion(this);
		if (region == null) {
			getSqliteStore().performQuery(new Query().fieldIsEqualTo(Region.NAME_FIELD_NAME, "ALL"), EPassSqliteStoreOpenHelper.SCHEMA_REGION,
					storeCallback);
		}
	}

} 
