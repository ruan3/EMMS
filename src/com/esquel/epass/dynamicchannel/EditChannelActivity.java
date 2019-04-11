package com.esquel.epass.dynamicchannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.EditChannelAdapter;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Category;
import com.esquel.epass.schema.CategoryChannel;
import com.esquel.epass.schema.Channel;
import com.esquel.epass.schema.ChannelArticle;
import com.esquel.epass.schema.UserChannel;
import com.google.gson.JsonArray;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.serialization.ModelSerializationPolicy;
import com.joyaether.datastore.serialization.ModelSerializationStrategy;
import com.joyaether.datastore.sqlite.OrmIterableElement;
import com.joyaether.datastore.sqlite.OrmObjectElement;

public class EditChannelActivity extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	ListView reOrderChannelList;
	ImageView addIcon;
	TextView reorderMenu;
	EditChannelAdapter editChannelAdapter;
	private HashMap<Long, ObjectElement> displayItems = new HashMap<Long, ObjectElement>();
	
	private static final String CATEGORY_NAME_FIELD_NAME = "category_name";
	private static final String CHANNEL_ID_FIELD_NAME = "channel_id";
	private static final String CHANNEL_NAME_FIELD_NAME = "channel_name";
	private static final String IMAGE_URL_FIELD_NAME = "image_url";
	private static final String EXTRA_RESULT_ITEM = "result-item";
	private HashMap<Long, ObjectElement> selectedItems = new HashMap<Long, ObjectElement>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reorder_subscribed_channel);
		
		setData();
		getCategory();
	}

	private void setData() {

		findViewById(R.id.left_first).setOnClickListener(this);
		findViewById(R.id.right_button).setOnClickListener(this);
		findViewById(R.id.right_button).setVisibility(View.GONE);
		findViewById(R.id.reorder_menu_ll).setVisibility(View.GONE);

		/*
		 * TextView rightTopButton = (TextView) findViewById(R.id.right_button);
		 * rightTopButton.setText(R.string.category);
		 * rightTopButton.setOnClickListener(this);
		 */

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		// String data = (String)
		// getIntent().getExtras().getString(SCREEN_HEADER, "");
		titleMenu.setText(R.string.add_channel_title);
		// titleMenu.setText(R.string.title_leader_board);

		reOrderChannelList = (ListView) findViewById(R.id.listview_reorder_scribed_channel);
		addIcon = (ImageView) findViewById(R.id.addicon);
		reorderMenu = (TextView) findViewById(R.id.reorder_menu);

		addIcon.setOnClickListener(this);
		reorderMenu.setOnClickListener(this);
		reOrderChannelList.setOnItemClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.addicon:
			callEditDialog("");
			break;

		case R.id.reorder_menu:
			callEditDialog("");
			break;

		case R.id.left_first:
			onBackPressed();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (selectedItems.size() > 0) {
			JsonArrayElement array = new JsonArrayElement(new JsonArray());
			for (Long channelId : selectedItems.keySet()) {
				ObjectElement object = selectedItems.get(channelId);
				if (object != null) {
					array.add(object);
				}
			}
			Intent intent = new Intent();
			intent.putExtra(EXTRA_RESULT_ITEM, array.toJson());
			setResult(Activity.RESULT_OK, intent);
		}
		super.onBackPressed();
	}

	private void callEditDialog(String data) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(data);
		// alertDialogBuilder.setTitle("Title");

		alertDialogBuilder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						Intent intent = new Intent(EditChannelActivity.this,
								AddChannelAcvitivty.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (parent.getAdapter() instanceof ChannelAdapter) {
			ChannelAdapter adapter = (ChannelAdapter) parent.getAdapter();
			if (!adapter.isHeader(position)) {
				final DataElement item = (DataElement) adapter.getItem(position);				
				
				DataElement value = item.asObjectElement().get(DisplayChannelItem.VALUE_FIELD_NAME);
				String titleString = "";
				if (value != null && value.isObject()) {
					DataElement title = value.asObjectElement().get(CHANNEL_NAME_FIELD_NAME);
					if (title != null && title.isPrimitive()) {
						titleString = title.asPrimitiveElement().valueAsString();
					}
				}			
				showSelectedChannelDialog(titleString, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataElement displayChannelItem = item.asObjectElement().get(DisplayChannelItem.VALUE_FIELD_NAME);
						long itemId = 0L;
						JsonObjectElement object = new JsonObjectElement();
						object.set(UserChannel.ID_FIELD_NAME, UUID.randomUUID().toString());
						if (displayChannelItem != null && displayChannelItem.isObject()) {
							DataElement e = displayChannelItem.asObjectElement().get(CHANNEL_ID_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								itemId = e.asPrimitiveElement().valueAsLong();
								object.set(UserChannel.IDENTIFIER_FIELD_NAME, e.asPrimitiveElement().valueAsLong());
							}
							
							e = displayChannelItem.asObjectElement().get(CHANNEL_NAME_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								object.set(UserChannel.NAME_FIELD_NAME, e.asPrimitiveElement().valueAsString());
							}
							e = displayChannelItem.asObjectElement().get(IMAGE_URL_FIELD_NAME);
							if (e != null && e.isPrimitive()) {
								object.set(UserChannel.IMAGE_URL_FIELD_NAME , e.asPrimitiveElement().valueAsString());
							}
							object.set(UserChannel.TYPE_FIELD_NAME, UserChannel.TYPE_CHANNEL);
						}
						if (itemId > 0L) {
							selectedItems.put(itemId, object);
							displayItems.remove(itemId);
							setListView();
						}
						
					}
				});
				
//				Intent intent = new Intent();
//				intent.putExtra(EXTRA_RESULT_ITEM, object.toJson());
//				setResult(Activity.RESULT_OK, intent);
//				finish();
			}
		}
	}
	
	private void showSelectedChannelDialog(String message, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.add_channel_title);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.confirm, onClickListener);
		builder.show();
	}
	
	private void getCategory() {
		Query query = new Query().expandField(CategoryChannel.CATEGORY_FIELD_NAME);
		long[] subscribedChannel = getIntent().getLongArrayExtra(DragEditChannelActivity.EXTRA_SUBCRIBED_CHANNEL_ID);
		List<Long> list = new ArrayList<Long>();
		for (int i = 0; i < subscribedChannel.length; i++) {
			list.add(subscribedChannel[i]);
		}
		
		if (subscribedChannel != null) {
			query.fieldIsNotIn(CategoryChannel.CHANNEL_FIELD_NAME + "/" + Channel.ID_FIELD_NAME, list);	
		}
		
		getSqliteStore().performQuery(query, EPassSqliteStoreOpenHelper.SCHEMA_CATEGORY_CHANNEL, new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
					for (DataElement e : element.asArrayElement()) {
						if (e != null && e.isObject() && e instanceof OrmObjectElement) {
							OrmObjectElement ormObject = (OrmObjectElement) e.asObjectElement();
							HashMap<String, String> expand = new HashMap<String, String>();
							expand.put(CategoryChannel.CATEGORY_FIELD_NAME, null);
							ModelSerializationStrategy strategy = ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization().withExpansionTree(expand);
							ormObject.setSerializationStrategy(strategy);
							String categoryName = null;
							DataElement category = e.asObjectElement().get(CategoryChannel.CATEGORY_FIELD_NAME);
							if (category != null && category.isObject()) {
								DataElement name = category.asObjectElement().get(Category.NAME_FIELD_NAME);
								if (name != null && name.isPrimitive()) {
									categoryName = name.asPrimitiveElement().valueAsString();
								}
							}
							long channelId = 0;
							DataElement channel = e.asObjectElement().get(CategoryChannel.CHANNEL_FIELD_NAME);
							if (channel != null && channel.isObject()) {
								DataElement id = channel.asObjectElement().get(Channel.ID_FIELD_NAME);
								if (id != null && id.isPrimitive()) {
									channelId = id.asPrimitiveElement().valueAsLong();
								}
							}
							if (channelId > 0L && categoryName != null) {
								if (displayItems.get(channelId) == null) {
									JsonObjectElement object = new JsonObjectElement();
									displayItems.put(channelId, object);
								}
								displayItems.get(channelId).set(CATEGORY_NAME_FIELD_NAME, categoryName);
								displayItems.get(channelId).set(CHANNEL_ID_FIELD_NAME, channelId);
							}
						}
					}
					getChannel();
				}
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				Log.d("", "");
			}
			
		});
	}
	
	private void getChannel() {
		List<Long> channels = new ArrayList<Long>();
		channels.addAll(displayItems.keySet());
		getSqliteStore().performQuery(new Query().fieldIsIn(ChannelArticle.CHANNEL_FIELD_NAME + "/" + Channel.ID_FIELD_NAME, channels)
				.resultIsGroupBy(ChannelArticle.CHANNEL_FIELD_NAME + "_id")
				.expandField(ChannelArticle.CHANNEL_FIELD_NAME),
				EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL_ARTICLE, new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isArray() && element instanceof OrmIterableElement) {
					OrmIterableElement ormIterable = (OrmIterableElement) element.asArrayElement();
					HashMap<String, String> expand = new HashMap<String, String>();
					expand.put(ChannelArticle.ARTICLE_FIELD_NAME, null);
					expand.put(ChannelArticle.CHANNEL_FIELD_NAME, null);
					ModelSerializationStrategy strategy = ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization().withExpansionTree(expand);
					for (DataElement channelArticle : element.asArrayElement()) {
						if (channelArticle != null && channelArticle.isObject() && channelArticle instanceof OrmObjectElement) {
							OrmObjectElement ormObject = (OrmObjectElement) channelArticle.asObjectElement();
							ormObject.setSerializationStrategy(strategy);
							DataElement channel = ormObject.get(ChannelArticle.CHANNEL_FIELD_NAME);
							long channelId = 0L;
							if (channel != null && channel.isObject()) {
								DataElement channelIdElement = channel.asObjectElement().get(Channel.ID_FIELD_NAME);
								
								if (channelIdElement != null && channelIdElement.isPrimitive()) {
									channelId = channelIdElement.asPrimitiveElement().valueAsLong();
								}
								DataElement channelName = channel.asObjectElement().get(Channel.NAME_FIELD_NAME);
								if (channelName != null && channelName.isPrimitive() && channelId > 0L) {
									displayItems.get(channelId).set(CHANNEL_NAME_FIELD_NAME, channelName.asPrimitiveElement().valueAsString());
								}
							}
							DataElement article = channelArticle.asObjectElement().get(ChannelArticle.ARTICLE_FIELD_NAME);
							if (article != null && article.isObject()) {
								DataElement imageUrl = article.asObjectElement().get(Article.IMAGE_FIELD_NAME);
								if (imageUrl != null && imageUrl.isPrimitive() && channelId > 0L) {
									displayItems.get(channelId).set(IMAGE_URL_FIELD_NAME, imageUrl.asPrimitiveElement().valueAsString());
								}
							}
						}
						
					}
				}
				setListView();
				
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				Log.d("", "");
			}
			
		});
	}
	
	private List<ObjectElement> createListObject() {
		LinkedHashMap<String, List<ObjectElement>> map = new LinkedHashMap<String, List<ObjectElement>>();
		List<ObjectElement> objects = new ArrayList<ObjectElement>();
		for (Long channelId : displayItems.keySet()) {
			ObjectElement cachedItems = displayItems.get(channelId);
			DataElement title = cachedItems.get(CATEGORY_NAME_FIELD_NAME);
			if (title != null && title.isPrimitive()) {
				List<ObjectElement> list = null;
				String categoryName = title.asPrimitiveElement().valueAsString();
				list = map.get(categoryName);
				if (list == null) {
					list = new ArrayList<ObjectElement>();
				}
				list.add(cachedItems);
				map.put(categoryName, list);
			}
		}
		
		for (String categoryName : map.keySet()) {
			JsonObjectElement header = new JsonObjectElement();
			header.set(DisplayChannelItem.TITLE_FIELD_NAME, categoryName);
			objects.add(header);
			List<ObjectElement> items = map.get(categoryName);
			if (items != null) {
				for (ObjectElement item : items) {
					JsonObjectElement o = new JsonObjectElement();
					o.set(DisplayChannelItem.VALUE_FIELD_NAME, item);
					objects.add(o);
				}
			}
			
		}
		return objects;
	}
	
	private void setListView() {
		final List<ObjectElement> displayItems = createListObject();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ChannelAdapter adapter = new ChannelAdapter(EditChannelActivity.this, displayItems);
				reOrderChannelList.setAdapter(adapter);				
			}
			
		});
	}
	
	private class ChannelAdapter extends BaseAdapter {
		
		private List<ObjectElement> items;

		public ChannelAdapter(Context context, List<ObjectElement> objects) {
			items = objects;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(EditChannelActivity.this, R.layout.unsubscribed_channel, null);
			}
			
			ObjectElement item = (ObjectElement) getItem(position);
			if (item != null && item.isObject()) {
				DataElement categoryName = item.get(DisplayChannelItem.TITLE_FIELD_NAME);
				TextView header = (TextView) view.findViewById(R.id.header);
				View channelView = view.findViewById(R.id.channel);
				if (categoryName != null && categoryName.isPrimitive()) {
					//header
					header.setVisibility(View.VISIBLE);
					channelView.setVisibility(View.GONE);
					header.setText(categoryName.asPrimitiveElement().valueAsString());
				} else {
					//channel
					header.setVisibility(View.GONE);
					channelView.setVisibility(View.VISIBLE);
					ImageView imageView = (ImageView) channelView.findViewById(R.id.image);
					TextView channelTitle = (TextView) channelView.findViewById(R.id.channel_name);
					DataElement value = item.get(DisplayChannelItem.VALUE_FIELD_NAME);
					if (value != null && value.isObject()) {
						DataElement e = value.asObjectElement().get(IMAGE_URL_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							getImageDownloader().download(e.asPrimitiveElement().valueAsString(), imageView);
						}
						e = value.asObjectElement().get(CHANNEL_NAME_FIELD_NAME);
						if (e != null && e.isPrimitive()) {
							channelTitle.setText(e.asPrimitiveElement().valueAsString());
						}
					}
				}
			}
			
			return view;
		}
		
		public boolean isHeader(int position) {
			ObjectElement item = (ObjectElement) getItem(position);
			if (item != null && item.isObject()) {
				DataElement categoryName = item.get(DisplayChannelItem.TITLE_FIELD_NAME);
				if (categoryName != null && categoryName.isPrimitive()) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	private class DisplayChannelItem {
		public static final String TITLE_FIELD_NAME = "title";
		public static final String VALUE_FIELD_NAME = "value";
	}

}
