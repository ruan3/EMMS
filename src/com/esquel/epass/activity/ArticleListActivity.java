package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.ArticleAdapter;
import com.esquel.epass.adapter.NoneDataAdapter;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.delegate.BackListener;
import com.esquel.epass.delegate.LikeListener;
import com.esquel.epass.lib.flipview.FlipView;
import com.esquel.epass.lib.flipview.OverFlipMode;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.ArticleContent;
import com.esquel.epass.schema.ArticleRegion;
import com.esquel.epass.schema.ChannelArticle;
import com.esquel.epass.schema.Region;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.LogUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.Utility;
import com.google.gson.JsonArray;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.util.ImageDownloader;
import com.joyaether.datastore.util.ImageDownloader.Mode;

/**
 * 
 */
public class ArticleListActivity extends BaseGestureActivity implements LikeListener, BackListener {
    private static final int ARTICLE_LIST_STYLE_SCROLL_VIEW = 6;
	long channelId;
    ArticleAdapter adapter;
    List<DataElement> articles = new ArrayList<DataElement>();
    DataElement lastArticle = null;
    Animation flipAnimation, refreshAnimation;
    boolean isLoading = false;
    public static final String LIKED_FIELD_NAME = "liked";
    public static final String LIKE_COUNT_NAME = "like_count";
    public static final String RESOURCE_ID_FIELD_NAME = "resource_id";
    public static final String PREFIX_RESOURCE = "1-";
    public static final int REQUEST_CODE_ARTICLE_VIEW = 100;
    List<String> ids;
    List<Integer> totalArticleLikes = new ArrayList<Integer>();
    List<Boolean> myArticleLikes = new ArrayList<Boolean>();
    static final String KEY_TITLE = "title";
    final HashMap<String, String> map = new HashMap<String, String>();
    FlipView flipView;
    int typeShow = 2;
    ListView listView;
    int firstVisibleItem = 0;
    String title;
    TextView titleMenu;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        Intent intent = getIntent();
        channelId = intent.getExtras().getLong("id");
        getArticleFromDataBase(channelId);
        setContentView(R.layout.activity_article_list);
        titleMenu = (TextView) findViewById(R.id.title_menu);
        typeShow = Utility.getTypeShow(this);
        flipView = (FlipView) findViewById(R.id.flip_view);
        listView = (ListView) findViewById(R.id.listview);
        listView.setDividerHeight(0);
        listView.setBackgroundColor(Color.WHITE);
        flipView.peakNext(false);
        flipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
        final int api = 18;
        if (currentAPIVersion == api) {
            flipView.set18API(true);
        }
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        findViewById(R.id.btn_menu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleListActivity.this, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(intent, Constants.REQUEST_CODE_LANGUAGE_CHANGE);
            }

        });
        if (typeShow != ARTICLE_LIST_STYLE_SCROLL_VIEW) {
            flipView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            findViewById(R.id.top_menu2).setVisibility(View.GONE);
            
        } else {
            flipView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            findViewById(R.id.top_menu2).setVisibility(View.VISIBLE);
           
            CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_channel);
            int requestCode = getIntent().getIntExtra(
					Constants.INTENT_EXTRA_REQUEST_CODE, 0);
            boolean subscribed = requestCode == Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST;
            checkBox.setVisibility(subscribed & channelId != 1? View.VISIBLE : View.GONE);
            checkBox.setChecked(subscribed && channelId != 1);
        }

    }
    
    @Override
    public void onRestart() {
        super.onRestart();
        getImage(articles);
        if (adapter != null) {
        	boolean needUpdateUI = typeShow != Utility.getTypeShow(this);
            typeShow = Utility.getTypeShow(this);
            if (needUpdateUI) {
                adapter = new ArticleAdapter(ArticleListActivity.this, channelId, title, articles, map, typeShow);
                int requestCode = getIntent().getIntExtra(
    					Constants.INTENT_EXTRA_REQUEST_CODE, 0);
                adapter.setSubscribedChannel(requestCode == Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
            }
            adapter.updateLike(totalArticleLikes, myArticleLikes);
            adapter.setImageDownloader(((AppApplication) getApplication()).getImageDownloader());
            if (typeShow != ARTICLE_LIST_STYLE_SCROLL_VIEW) {
                flipView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                findViewById(R.id.top_menu2).setVisibility(View.GONE);
                if (needUpdateUI) {
                    flipView.setAdapter(adapter);
                }
            } else {
                flipView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                findViewById(R.id.top_menu2).setVisibility(View.VISIBLE);
                if (needUpdateUI) {
                	listView.setAdapter(adapter);
                }
                CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_channel);
                int requestCode = getIntent().getIntExtra(
    					Constants.INTENT_EXTRA_REQUEST_CODE, 0);
                boolean subscribed = requestCode == Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST;
                checkBox.setVisibility(subscribed ? View.VISIBLE : View.GONE);
                checkBox.setChecked(subscribed && channelId != 1);
            }
            if (needUpdateUI) {
                adapter.notifyDataSetChanged();
            }


            // if (typeShow != Utility.getTypeShow(this)) {
            //
            // adapter.updateTypeShow(typeShow);
            // adapter.notifyDataSetChanged();
            // } else {
            // adapter.notifyDataSetChanged();
            // }
        }
    }

    private void initLike() {
        if (totalArticleLikes.size() == 0) {
            for (int i = 0; i < ids.size(); i++) {
                totalArticleLikes.add(0);
                myArticleLikes.add(false);
            }
        }
    }

    private void getArticleDetail() {
        ids = new ArrayList<String>();
        for (DataElement e : articles) {
            ids.add(e.asObjectElement().get(Article.ID_FIELD_NAME).valueAsString());
        }
        initLike();
        ((AppApplication) getApplication()).getSqliteStore().performQuery(
                new Query().fieldIsIn(ArticleContent.ARTICLE_FIELD_NAME, ids).fieldIsEqualTo(ArticleContent.TYPE_FIELD_NAME, ArticleContent.TYPE_TEXT),
                EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE_CONTENT, new StoreCallback() {

                    @Override
                    public void success(DataElement element, String resource) {

                        if (element != null && element.isArray()) {
                            getImage(articles);
                            for (DataElement e : element.asArrayElement()) {
                                String articleId = e.asObjectElement().get(ArticleContent.ARTICLE_FIELD_NAME).asObjectElement().get(Article.ID_FIELD_NAME).valueAsString();

                                if (!map.containsKey(articleId)) {
                                    map.put(articleId, e.asObjectElement().get(ArticleContent.TEXT_CONTENT_FIELD_NAME).valueAsString());
                                }
                            }
                        }
                        getLikeCountInDatabase();

                    }

                    @Override
                    public void failure(DatastoreException ex, String resource) {
                        // Do nothing
                    }
                });
    }

    private void getImage(final List<DataElement> elements) {
        ExecutorService excutor = Executors.newSingleThreadExecutor();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (DataElement e : elements) {
                    DataElement imageElement = e.asObjectElement().get(Article.IMAGE_FIELD_NAME);
                    if (imageElement != null && imageElement.isPrimitive()) {
                        ImageDownloader downloader = ((AppApplication) getApplication()).getImageDownloader();
                        LogUtils.e("获取到图片路径----->"+imageElement.valueAsString());
                        downloader.download(imageElement.valueAsString(), R.drawable.icon_loading_image,
                        		Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, null, Bitmap.Config.ARGB_8888, Mode.NO_ASYNC_TASK);

                    }

                }
            }

        };
        excutor.submit(runnable);

    }

    public void getLikeCount(final int index) {
        if (ids.size() > index) {
            String id = ids.get(index);
            ((AppApplication) getApplication()).getRestStore().readElement(PREFIX_RESOURCE + id, "likes", new OAuthRestStoreCallback(this) {

                @Override
                public void success(DataElement element, String resource) {
                    if (element != null && element.isObject()) {
                        int countOfLike = element.asObjectElement().get(LIKE_COUNT_NAME).valueAsInt();
                        boolean liked = element.asObjectElement().get(LIKED_FIELD_NAME).valueAsBoolean();
                        int currentCount = totalArticleLikes.get(index);
                        if (currentCount != countOfLike) {
                            totalArticleLikes.set(index, countOfLike);
                            myArticleLikes.set(index, liked);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    adapter.updateLike(totalArticleLikes, myArticleLikes);
                                    adapter.refesh();
                                    adapter.notifyDataSetChanged();
                                }

                            });
                        }
                    }
                }

                @Override
                public void fail(DatastoreException ex, String resource) {
                    // do nothing

                }

            });
        }
    }

    @Override
    public void onDestroy() {
        getImageDownloader().cancelDownloads();        
        super.onDestroy();
    }

    public void getLikeArticles() {
        StringBuilder sb = new StringBuilder("");
        sb.append(PREFIX_RESOURCE + ids.get(0));
        for (int i = 1; i < ids.size(); i++) {
            sb.append("," + PREFIX_RESOURCE + ids.get(i));
        }
        ((AppApplication) getApplication()).getRestStore().performQuery(new Query().fieldIsIn("resource_id", Arrays.asList(sb.toString())), "likes",
        		new OAuthRestStoreCallback(this) {
            @Override
            public void success(final DataElement element, String resource) {
                saveLikeCountInDatabase(element);
                for (int i = 0; i < element.asArrayElement().size(); i++) {
                    int countOfLike = element.asArrayElement().get(i).asObjectElement().get(LIKE_COUNT_NAME).valueAsInt();
                    boolean you = element.asArrayElement().get(i).asObjectElement().get(LIKED_FIELD_NAME).valueAsBoolean();
                    totalArticleLikes.set(i, countOfLike);
                    myArticleLikes.set(i, you);

                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter.updateLike(totalArticleLikes, myArticleLikes);
                        adapter.refesh();
                        adapter.notifyDataSetChanged();
                    }

                });

            }

			@Override
			public void fail(DatastoreException ex, String resource) {
				
			}

        });

    }

    private void display() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (articles.isEmpty()) {
                    NoneDataAdapter noneAdapter = new NoneDataAdapter(ArticleListActivity.this);
                    flipView.setAdapter(noneAdapter);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getIntent();
                            title = intent.getStringExtra(KEY_TITLE);
                            if (titleMenu != null) {
                                titleMenu.setText(title);
                            }
                            adapter = new ArticleAdapter(ArticleListActivity.this, channelId, title, articles, map, typeShow);
                            int requestCode = getIntent().getIntExtra(
                					Constants.INTENT_EXTRA_REQUEST_CODE, 0);
                            adapter.setSubscribedChannel(requestCode == Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST);
                            adapter.updateLike(totalArticleLikes, myArticleLikes);
                            adapter.setImageDownloader(((AppApplication) getApplication()).getImageDownloader());
                            if (typeShow != ARTICLE_LIST_STYLE_SCROLL_VIEW) {
                                flipView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                                findViewById(R.id.top_menu2).setVisibility(View.GONE);
                                flipView.setAdapter(adapter);
                            } else {
                                flipView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                findViewById(R.id.top_menu2).setVisibility(View.VISIBLE);
                                listView.setAdapter(adapter);
                            }

                        }

                    });
                    /**
                     * get like from server
                     */
                    getLikeArticles();

                }
            }

        });

    }

    private void getLikeCountInDatabase() {

        final List<String> array = new ArrayList<String>();
        for (int i = 0; i < ids.size(); i++) {
            array.add(PREFIX_RESOURCE + ids.get(i));
        }

        ((AppApplication) getApplication()).getSqliteStore().performQuery(new Query().fieldIsIn("resource_id", array), EPassSqliteStoreOpenHelper.SCHEMA_LIKE, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for (int i = 0; i < element.asArrayElement().size(); i++) {
                    DataElement e = element.asArrayElement().get(i);
                    int count = e.asObjectElement().get(LIKE_COUNT_NAME).valueAsInt();
                    String id = e.asObjectElement().get(RESOURCE_ID_FIELD_NAME).valueAsString();
                    boolean you = e.asObjectElement().get(LIKED_FIELD_NAME).valueAsBoolean();
                    int index = ids.indexOf(id.replace(PREFIX_RESOURCE, ""));
                    if (index > 0) {
                        totalArticleLikes.set(index, count);
                        myArticleLikes.set(index, you);
                    }

                }
                display();
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                display();
            }

        });
    }

    private void saveLikeCountInDatabase(final DataElement element) {
        ((AppApplication) getApplication()).getSqliteStore().createElement(element, EPassSqliteStoreOpenHelper.SCHEMA_LIKE, new StoreCallback() {

            @Override
            public void success(DataElement element, String resource) {

            }

            @Override
            public void failure(DatastoreException ex, String resource) {

            }

        });
    }

    public void refreshViewGone(float angle) {

    }

    public void updateTypeShow(int type) {
        this.typeShow = type;
    }

    public void getArticleFromDataBase(final long mChannelId) {
        String regionString = SharedPreferenceManager.getAppStoreRegion(this);
        JsonObjectElement region = regionString == null ? null : new JsonObjectElement(regionString);
        String regionName = null;
        DataElement regionNameElement = region == null ? null : region.get(Region.NAME_FIELD_NAME);
        if (regionNameElement != null && regionNameElement.isPrimitive()) {
        	regionName = regionNameElement.asPrimitiveElement().valueAsString();
        }
        ((AppApplication) getApplication()).getSqliteStore().performRawQuery(getArticleDetailRawSql(mChannelId, regionName),
        		EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE_REGION, new StoreCallback() {
            @Override
            public void success(final DataElement element, String resource) {
                articles.clear();
                int count = 0;
                for (DataElement object : element.asArrayElement()) {
                    if (count == 0) {
                        lastArticle = object;
                    }
                    if (object != null && object.isObject()) {
                        articles.add(object);
                    }
                    count++;
                }
                getArticleDetail();
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                // do nothing
            	Log.d("ArticleListActivity", ex.getMessage());
            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ArticleListActivity.REQUEST_CODE_ARTICLE_VIEW) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", 0);
                boolean liked = data.getBooleanExtra(ArticleListActivity.LIKED_FIELD_NAME, false);

                if (myArticleLikes.get(position) != liked) {
                    int count = totalArticleLikes.get(position);

                    myArticleLikes.set(position, liked);
                    if (liked) {
                        totalArticleLikes.set(position, count + 1);
                    } else {
                        totalArticleLikes.set(position, count - 1);
                    }
                    adapter.updateLike(totalArticleLikes, myArticleLikes);
                    adapter.notifyDataSetChanged();
                }
            }
        } 
        if (data != null && data.getExtras() != null && 
        		data.getExtras().getBoolean(Constants.INTENT_EXTRA_RESULT_SUBSCRIBED_CHANNEL_CHANGES)) {
    		setResult(Activity.RESULT_OK, data);
    	}
    }

    @Override
    public void like(long articleId, boolean like) {

    }

    @Override
    public boolean getLike(long articleId) {
        return false;
    }

    @Override
    public boolean getCountLike(long articleId) {
        return false;
    }

    @Override
    public void onBackPressed() {
    	int requestCode = getIntent().getIntExtra(
				Constants.INTENT_EXTRA_REQUEST_CODE, 0);
    	if (requestCode != Constants.REQUEST_CODE_SUBSCRIBED_CHANNEL_IN_ARTICLE_LIST) {
    		pressBackButton();
    		return;
    	}
        CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_channel);
        if (!checkBox.isChecked() && channelId != 1) {
        	showLoadingDialog();
        	getRestStore().performQuery(new Query().fieldIsOrderedBy(UserChannel.SEQUENCE_FIELD_NAME, Ordering.ASCENDING),
        			"user_channels", new StoreCallback() {

				@Override
				public void success(DataElement element, String resource) {
					if (element != null && element.isArray()) {
						final JsonArrayElement array = new JsonArrayElement(new JsonArray());
						boolean removedObject = false;
						for (int i = 0; i < element.asArrayElement().size(); i++) {
							DataElement data = element.asArrayElement().get(i);
							if (data != null && data.isObject()) {
								ObjectElement object = data.asObjectElement();
								DataElement id = object.get(UserChannel.IDENTIFIER_FIELD_NAME);
								DataElement type = object.get(UserChannel.TYPE_FIELD_NAME);
								if (id != null && id.isPrimitive() && type != null && id.asPrimitiveElement().valueAsLong() == channelId 
										&& type.isPrimitive() && type.asPrimitiveElement().valueAsString().equals(UserChannel.TYPE_CHANNEL)) {
									removedObject = true;
								} else {
									int moveSequence = 1;								
									DataElement sequence = object.get(UserChannel.SEQUENCE_FIELD_NAME);
									if (sequence != null && sequence.isPrimitive() && removedObject) {
										object.set(UserChannel.SEQUENCE_FIELD_NAME, sequence.asPrimitiveElement().valueAsInt() - moveSequence);
									} 
									array.add(object);
								}
							}
						}
						getRestStore().createElement(array, resource, new StoreCallback() {

							@Override
							public void success(DataElement element,
									String resource) {
								SharedPreferenceManager.setUserChannelCount(ArticleListActivity.this, array.size());
								SharedPreferenceManager.setUserChannel(ArticleListActivity.this, element.toJson());
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										setResult(Activity.RESULT_OK);
								        pressBackButton();
									}
									
								});								
							}

							@Override
							public void failure(DatastoreException ex,
									String resource) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
								        pressBackButton();
									}
									
								});								
							}
							
						});
						return;
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
					        pressBackButton();
						}
						
					});
				}

				@Override
				public void failure(DatastoreException ex, String resource) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
					        pressBackButton();
						}
						
					});
				}
        		
        	});
        	return;
        } else {
        	setResult(Activity.RESULT_CANCELED);
        }
        pressBackButton();
    }
    
    private void pressBackButton() {
        super.onBackPressed();
    	overridePendingTransition(-1, R.anim.trans_right_out);
        if (ChannelListFlipActivity.class.getCanonicalName().equals(getClass().getCanonicalName())) {
            overridePendingTransition(-1, -1);
            return;
        }
    }

    @Override
    public void onBack() {
        onBackPressed();

    }
    
    private String getArticleDetailRawSql(long channelId, String regionName) {
    	String defaultRegionWhere = "regions.name = '" + Region.DEFAULT_REGION_NAME + "'";
    	String regionWhere = regionName == null || Region.DEFAULT_REGION_NAME.equals(regionName) ?
    			defaultRegionWhere : defaultRegionWhere + " OR regions.name = '" + regionName + "'";
    	
    	return "SELECT distinct article_regions.article_id as " + Article.ID_FIELD_NAME +
    			", articles.article_date," +
    			" articles.sequence, articles.title, articles.title, articles.thumbnail," +
    			" articles.image, articles.status FROM article_regions left join articles" +
    			" on (article_regions.article_id = articles.id) left join channel_articles" +
    			" on(channel_articles.article_id = articles.id) left join regions on" +
    			" (regions.region_id = article_regions.region_id) where" +
    			" channel_articles.channel_id = " + channelId + " AND " + regionWhere + 
    			" ORDER BY articles.article_date DESC";
    }

}
