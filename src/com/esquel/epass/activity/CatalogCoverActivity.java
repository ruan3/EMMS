package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.CatalogCoverAdapter;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.item.DisplayItem;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Category;
import com.esquel.epass.schema.Channel;
import com.esquel.epass.schema.ChannelArticle;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;

/**
 * 
 * @author hung
 * 
 */
public class CatalogCoverActivity extends BaseGestureActivity {
    private static final int SDK_VERSION_11 = 11;
	ListView listView;
    public static final String KEY_CATEGORY_ID = "category-id";
    List<DisplayItem> itemList = new ArrayList<DisplayItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        setContentView(R.layout.activity_catalog_cover);
        listView = (ListView) findViewById(R.id.listview);
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        List<Long> ids = new ArrayList<Long>();
        if (getIntent().getExtras() != null) {

            long[] line = getIntent().getExtras().getLongArray("list");

            for (int i = 0; i < line.length; i++) {
                ids.add((line[i]));
            }

        }
        String title = getHeaderTitle();
        if (title != null) {
        	TextView titleView = (TextView) findViewById(R.id.title);
        	titleView.setText(title);
        }
        
        long id = getIntent().getLongExtra(KEY_CATEGORY_ID, 0);

        getChannels(id);
       
    }
    
    private synchronized void getImage(final DisplayItem item, long channelId, long channelArticleId, final int index, final int count) {

    	getSqliteStore().performQuery(new Query().expandField(ChannelArticle.ARTICLE_FIELD_NAME)
    				.fieldIsEqualTo(ChannelArticle.CHANNEL_FIELD_NAME, channelId)
    				.fieldIsOrderedBy(ChannelArticle.LAST_MODIFIED_DATE_FIELD_NAME, Ordering.DESCENDING)
    				.fieldIsNotEqualTo(ChannelArticle.ARTICLE_FIELD_NAME + "/"
                        		+ Article.IMAGE_FIELD_NAME, "")
    				.limitResultsTo(1L), 
    				EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL_ARTICLE, new StoreCallback() {

						@Override
						public void success(DataElement element, String resource) {
							if (element != null && element.isArray()) {
								DataElement article = element.asArrayElement()
										.get(0).asObjectElement().get(
										ChannelArticle.ARTICLE_FIELD_NAME);
								item.setImage(article.asObjectElement()
										.get(Article.IMAGE_FIELD_NAME).asPrimitiveElement()
										.valueAsString());
								if (itemList.size() == count) {
									
									final ArrayList<DisplayItem> imageDataList = new ArrayList<DisplayItem>();
									imageDataList.clear();
									imageDataList.addAll(itemList);
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											listView.post(new Runnable() {

												@Override
												public void run() {
													CatalogCoverAdapter menuAdapter = new CatalogCoverAdapter(
			                                                CatalogCoverActivity.this, imageDataList);	
													menuAdapter.setImageDownloader(getImageDownloader());
													listView.setAdapter(menuAdapter);
												}
												
											});
											
										}
				    					
				    				});
								}
			    				
			    			}
						}

						@Override
						public void failure(DatastoreException ex,
								String resource) {
							
						}
    		
    	});
    }
    
    /**
     * get the channel with using raw query and notify ui.
     * 
     * @param map
     *            a {@link HashMap} contain a set of channel id with the key and
     *            set of {@link ItemDisplay} for show in ui.
     */
    private synchronized void getChannels(long categoryId) {

        getSqliteStore()
                .performRawQuery("select t1.channel_id channel_id, sequence, name, number, image, filter_name, t1.id "
                		+ "   from ( select distinct c.id channel_id, c.sequence, c.name, count(a.id) number, ca.id id   "
                		+ "  from channels c inner join channel_articles ca on (c.id = ca.channel_id) inner   "
                		+ "  join articles a on (a.id = ca.article_id) group by c.id ) t1 inner join (  "
                		+ "   select ca2.channel_id channel_id, max(a2.article_date) article_date,  "
                		+ "   a2.image image from articles a2 inner join channel_articles ca2 on "
                		+ "(a2.id =     ca2.article_id) where image != '' group by ca2.channel_id ) t2 on t1.channel_id = t2.channel_id   "
                		+ "  inner join (select category_channels.channel_id channel_id, max(    category_channels.lastmoddate),"
                		+ " categories.filter_name  filter_name from categories    "
                		+ " inner join category_channels on (category_channels.category_id = categories.id)   where categories.id = " 
                		+ categoryId + " group by category_channels.channel_id) t3 on t1.channel_id = t3.channel_id    order by sequence asc ", 
                		EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL_ARTICLE,
                        new StoreCallback() {
                            @Override
                            public void success(final DataElement element,
                                    String resource) {
                                if (element == null || !element.isArray()) {
                                    return;
                                }

                                final List<DisplayItem> items = new ArrayList<DisplayItem>();
                                for (int i = 0; i < element.asArrayElement()
                                        .size(); i++) {
                                    DisplayItem item = new DisplayItem();
                                    DataElement dataElement = element
                                            .asArrayElement().get(i);

                                    DataElement e = dataElement
                                            .asObjectElement()
                                            .get(ChannelArticle.CHANNEL_FIELD_NAME
                                                    + "_id");
                                    if (e != null && e.isPrimitive()) {
                                        item.setChannelId(e
                                                .asPrimitiveElement()
                                                .valueAsLong());
                                        DataElement channelArticleId = 
                                        		dataElement.asObjectElement().get(ChannelArticle.ID_FIELD_NAME);

                                        if (Build.VERSION.SDK_INT < SDK_VERSION_11) {
                                        	getImage(item, item.getChannelId(),
                                        			channelArticleId.asPrimitiveElement().valueAsLong(),
                                        			i, element.asArrayElement()
                                                    .size());
                                        }
                                    }

                                    e = dataElement.asObjectElement().get(
                                    		Channel.NAME_FIELD_NAME);
                                    if (e != null && e.isPrimitive()) {
                                    	item.setChannelName(e.asPrimitiveElement().valueAsString());
                                    }

                                    if (Build.VERSION.SDK_INT >= SDK_VERSION_11) {
                                   	 e = dataElement.asObjectElement().get(
                                                Article.IMAGE_FIELD_NAME);
                                        
                                        if (e != null && e.isPrimitive()) {
                                        	item.setImage(e.asPrimitiveElement().valueAsString());
                                        }      
                                   }
                                    
                                    if (e != null && e.isPrimitive()) {
                                    	item.setImage(e.asPrimitiveElement().valueAsString());
                                    }                                
                                    
                                    e = dataElement.asObjectElement().get("number");
                                    if (e != null && e.isPrimitive()) {
                                        item.setNumberArticle(e
                                                .asPrimitiveElement()
                                                .valueAsInt());
                                    }
                                    
                                    try {
                                    	e = dataElement.asObjectElement().get(Category.FILTER_NAME_FIELD_NAME);
                                        if (e != null && e.isPrimitive()) {
                                        	item.setFilterImageName(e.asPrimitiveElement()
                                        			.valueAsString());
                                        }
                                    } catch (Exception ex) {
                                    	
                                    }
                                    

                                    items.add(item);

                                }
                                itemList.clear();
                                itemList = items;

                                /**
                                 * notify ui
                                 */
                                if (Build.VERSION.SDK_INT >= SDK_VERSION_11) {
                                	runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                        	CatalogCoverAdapter menuAdapter = new CatalogCoverAdapter(
                                                    CatalogCoverActivity.this, items);
                                        	menuAdapter.setImageDownloader(getImageDownloader());
                                            listView.setAdapter(menuAdapter);
                                            
                                        }

                                    });
                                }
                                

                            }

                            @Override
                            public void failure(DatastoreException ex,
                                    String resource) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                       

                                    }

                                });
                            }

                        });
    }

}
