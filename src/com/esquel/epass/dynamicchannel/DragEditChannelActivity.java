package com.esquel.epass.dynamicchannel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.ChannelAdapter.ChannelItem;
import com.esquel.epass.appstore.AppStoreActivity;
import com.esquel.epass.appstore.LeaderBoardActivity;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.SystemApplication;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.PackageUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
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
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DragListener;

//import com.mobeta.android.dslv.DragSortListView;

public class DragEditChannelActivity extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	private EditDragListAdapter adapter;

	boolean isListViewDragable = false;

	private List<ObjectElement> subScribedChannels;
	private HashMap<Integer, ObjectElement> nonAndroidApplications = new HashMap<Integer, ObjectElement>();
	private List<ObjectElement> tempSubScribedChannels = new ArrayList<ObjectElement>();
	private Activity activity;

	private ImageView addIcon;
	private TextView reorderMenu;
	private DragSortListView dragList;
	
	public static final String EXTRA_SUBCRIBED_CHANNEL_ID = "subcribed-channel";

	public static final int EDITABLE_POSITION = 0;
	
	private static final String CHANNEL_ID_FIELD_NAME = "channel_id";
	private static final String CHANNEL_NAME_FIELD_NAME = "name";
	public static final String EXTRA_RESULT_ITEM = "result-item";
	private boolean listHasChange = false;
	

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				adapter.setAdapterData(dragDataInList(from, to));
				if (dragList.getAdapter() == null) {
					dragList.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				setListHasChange(true);
			}
		}

	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapter.setAdapterData(removeDataInList(which));
			if (dragList.getAdapter() == null) {
				dragList.setAdapter(adapter);
			}
			adapter.notifyDataSetChanged();
			setListHasChange(true);
		}

	};

	/**
	 * Remove Particular data from the list.
	 * 
	 * @param which
	 *            is position to be deleted.
	 * @return
	 */
	private List<ObjectElement> removeDataInList(int which) {

		List<ObjectElement> list = adapter.getAdapterData();

		List<ObjectElement> newlist = new ArrayList<ObjectElement>();

		if (list != null && list.size() != 0) {

			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (i != which) {
					newlist.add(
							list.get(i).asObjectElement());
				}
			}
			list = newlist;
		}
		return list;
	}

	/**
	 * Drag a cell from particular position to other in the list.
	 * 
	 * @param
	 * @return
	 */
	private List<ObjectElement> dragDataInList(int from, int to) {

		List<ObjectElement> list = adapter.getAdapterData();

		List<ObjectElement> newlist = new ArrayList<ObjectElement>();

		if (list != null && list.size() != 0) {

			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (i == to) {

					if (from < to) {
						newlist.add(
								list.get(i).asObjectElement());
						newlist.add(
								list.get(from)
										.asObjectElement());
					} else {

						newlist.add(
								list.get(from)
										.asObjectElement());
						newlist.add(
								list.get(i).asObjectElement());
					}

				} else {
					if (i != from) {
						newlist.add(
								list.get(i).asObjectElement());
					}
				}
			}
			list = newlist;
		}
		return list;
	}

	@Override
	public void onBackPressed() {
		if (isListViewDragable) {
			if (isListHasChange()) {
				showConfirmDialog();
			} else {
				exitEditMode();
			}
		} else {
			List<ObjectElement> objects = new ArrayList<ObjectElement>();
			objects.addAll(adapter.getAdapterData());
			
			for (Integer sequence : nonAndroidApplications.keySet()) {
				ObjectElement objectElement = nonAndroidApplications.get(sequence);
				objects.add(sequence, objectElement);
			}
			
			final JsonArrayElement array = new JsonArrayElement(new JsonArray());
			for (int i = 0; i < objects.size(); i++) {
				ObjectElement object = objects.get(i);		
				object.set(UserChannel.SEQUENCE_FIELD_NAME, i + 1);
				array.add(object);		
			}
			array.get(0).asObjectElement().set("identifier", 1);
			array.get(0).asObjectElement().set("type", "channel");
			array.get(0).asObjectElement().set("id", (String) null);
			
			SharedPreferenceManager.setUserChannel(this, array.toJson());
			super.onBackPressed();
		}

	}

	private void exitEditMode() {
		dragList.setDragEnabled(false);
		isListViewDragable = false;
		adapter.setAdapterData(subScribedChannels);
		if (dragList.getAdapter() == null) {
			dragList.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_subscribed_channel);

		findViewById(R.id.left_second).setOnClickListener(this);
		findViewById(R.id.right_second).setOnClickListener(this);
		findViewById(R.id.left_first).setOnClickListener(this);
		findViewById(R.id.left_first).setVisibility(View.VISIBLE);
		findViewById(R.id.left_second).setVisibility(View.GONE);
		findViewById(R.id.reorder_menu_ll).setVisibility(View.GONE);
		
		// Title of the Activity.
		((TextView) findViewById(R.id.title_menu))
				.setText(R.string.supcribed_channel_title);

		addIcon = (ImageView) findViewById(R.id.addicon);
		reorderMenu = (TextView) findViewById(R.id.reorder_menu);

		activity = this;

		addIcon.setOnClickListener(this);
		reorderMenu.setOnClickListener(this);

		dragList = (DragSortListView) findViewById(android.R.id.list);

		dragList.setDropListener(onDrop);
		dragList.setDragEnabled(false);
		dragList.setRemoveListener(onRemove);
		dragList.setDragListener(new DragListener() {

			@Override
			public void drag(int from, int to) {
				if (from == EDITABLE_POSITION || to == EDITABLE_POSITION) {
					dragList.cancelDrag();
				}

			}
		});

		subScribedChannels = new ArrayList<ObjectElement>();

		adapter = new EditDragListAdapter(subScribedChannels);

		dragList.setAdapter(adapter);
		((TextView) findViewById(R.id.right_second)).setText(R.string.edit);
		getUserChannel();

	}

	/**
	 * 
	 * Holder of cell in a {@link EditDragListAdapter edit drag list adapter}.
	 * 
	 */
	private class ViewHolder {
		TextView channelName;
		ImageView imgRemoveCell, dragHandle, icon;
	}

	/**
	 * Drag List Adapter.
	 * 
	 */
	private class EditDragListAdapter extends BaseAdapter {

		private List<ObjectElement> list;

		public EditDragListAdapter(List<ObjectElement> subscribedChannels) {
			super();

			list = subscribedChannels;

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			if (v == null) {
				LayoutInflater inflater = ((Activity) activity)
						.getLayoutInflater();
				v = inflater
						.inflate(R.layout.item_drag_subscribe_channel, null);
				holder = new ViewHolder();

				ImageView tv = (ImageView) v.findViewById(R.id.click_remove);
				
				holder.icon = (ImageView) v.findViewById(R.id.subscibe_channel_icon);
				holder.imgRemoveCell = tv;
				holder.dragHandle = (ImageView) v
						.findViewById(R.id.drag_handle);

				holder.channelName = (TextView) v
						.findViewById(R.id.channel_name);
				v.setTag(holder);

			} else {
				holder = (ViewHolder) v.getTag();
			}
			ObjectElement item = (ObjectElement) getItem(position);
			if (item == null) {
				return v;
			}
			int id = 0;
			DataElement idElement = item.get(ChannelItem.IDENTIFIER_FIELD_NAME);
			if (id > 0) {
				id = idElement.asPrimitiveElement().valueAsInt();
			}
			
            
            DataElement bundleIdentifier = item.get(UserChannel.BUNDLE_IDENTIFIER_FIELD_NAME);
            String packageName = null;
            if (bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
            	packageName = bundleIdentifier.asPrimitiveElement().valueAsString();
            }
            
            String typeValue = null;
			DataElement type = item.get(UserChannel.TYPE_FIELD_NAME);
			if (type != null && type.isPrimitive()) {
				typeValue = type.asPrimitiveElement().valueAsString();
			}
			
			String cachedApplicationDetail = SharedPreferenceManager.getSubscribedApplicationDetail(DragEditChannelActivity.this, id);
            JsonObjectElement cachedApplicationObject = null;
            if (cachedApplicationDetail != null && UserChannel.UserChannelType.APPLICATION.getType().equals(typeValue)) {
            	cachedApplicationObject = new JsonObjectElement(cachedApplicationDetail); 
            }
            
            DataElement nameElement = null;
            String name = "";
            if (cachedApplicationObject != null) {
            	nameElement = cachedApplicationObject.get(Application.NAME_FIELD_NAME);
            } else {
            	nameElement = item.get(UserChannel.NAME_FIELD_NAME);
            }
            if (nameElement != null && nameElement.isPrimitive()) {
            	name = nameElement.asPrimitiveElement().valueAsString();
            }
            if (name == null && typeValue != null && UserChannel.UserChannelType.APPLICATION.getType().equals(typeValue)) {
            	name = PackageUtils.getApplicationName(activity, packageName);
            } 
            
			holder.channelName.setText(name);
			holder.icon.setImageBitmap(null);
			if (isListViewDragable) {
				if (position > EDITABLE_POSITION) {
					setCellEditable(holder);
				} else {
					setCellUneditable(holder);
				}
				v.findViewById(R.id.click_remove_replica).setVisibility(
						View.VISIBLE);
			} else {
				v.findViewById(R.id.click_remove_replica).setVisibility(
						View.GONE);
				setCellUneditable(holder);
			}
			
			
			
			if (UserChannel.UserChannelType.SYSTEM_APPLICATION.getType().equals(typeValue)
					|| position == EDITABLE_POSITION) {
				holder.imgRemoveCell.setVisibility(View.GONE);
			} else {
				holder.imgRemoveCell.setVisibility(isListViewDragable ? View.VISIBLE : View.GONE);
			}
			
			View applicationIcon = v.findViewById(R.id.subscribed_icon);
			applicationIcon.setVisibility(UserChannel.UserChannelType.CHANNEL.getType().equals(typeValue) ? View.GONE : View.VISIBLE);
			
			DataElement image = null;
			
            if (cachedApplicationObject != null) {
            	image = cachedApplicationObject.get(Application.ICON_URL_FIELD_NAME);
            } else {
            	image = item.get(UserChannel.IMAGE_URL_FIELD_NAME);
            }
            
            if (UserChannel.UserChannelType.APPLICATION.getType().equals(typeValue)) {
            	if (packageName != null) {
                	Drawable drawable = PackageUtils.getApplicationIcon(activity, packageName);
                	holder.icon.setImageDrawable(drawable);
            	}
            } else {
            	if (image != null && image.isPrimitive()) {
    				if (image.asPrimitiveElement().isNumber()) {
    					holder.icon.setImageResource(image.asPrimitiveElement().valueAsInt());
    				} else {
    					getImageDownloader().download(image.asPrimitiveElement().valueAsString(), holder.icon);
    				}
    			}
            }
			

			return v;
		}

		/**
		 * 
		 * @return Data list of the adapter .
		 */
		List<ObjectElement> getAdapterData() {
			return list;
		}

		/**
		 * set Particular cell to un-editable/un-drag able form.
		 * 
		 * @param holder
		 */
		private void setCellUneditable(ViewHolder holder) {

			holder.imgRemoveCell.setVisibility(View.GONE);

			holder.dragHandle.setVisibility(View.INVISIBLE);

			findViewById(R.id.left_first).setVisibility(View.VISIBLE);

			findViewById(R.id.left_second).setVisibility(View.GONE);

			findViewById(R.id.reorder_menu_ll).setVisibility(View.GONE);

			((TextView) findViewById(R.id.right_second)).setText(R.string.edit);
			
		}

		/**
		 * set Particular cell to editable/drag able form.
		 * 
		 * @param holder
		 */
		private void setCellEditable(ViewHolder holder) {

			holder.imgRemoveCell.setVisibility(View.VISIBLE);

			holder.dragHandle.setVisibility(View.VISIBLE);

			findViewById(R.id.left_first).setVisibility(View.GONE);

			findViewById(R.id.left_second).setVisibility(View.VISIBLE);

			findViewById(R.id.reorder_menu_ll).setVisibility(View.VISIBLE);

			holder.imgRemoveCell.setVisibility(View.VISIBLE);
			
			((TextView) findViewById(R.id.right_second)).setText(R.string.finish);
			
			((TextView) findViewById(R.id.left_second)).setText(R.string.cancel);
			
		}

		/**
		 * Set data of particular cell.
		 * 
		 * @param dataElement
		 */
		void setAdapterData(List<ObjectElement> dataElement) {

			list = dataElement;

		}

		@Override
		public int getCount() {

			return list.size();

		}

		@Override
		public Object getItem(int position) {

			return list.get(position);

		}

		@Override
		public long getItemId(int position) {

			return position;

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.addicon:
		case R.id.reorder_menu:
			callEditDialog();
			break;
		case R.id.left_second:
			resetEditableList();
			break;
		case R.id.left_first:
			onBackPressed();
			break;
		case R.id.right_second:
			if (isListViewDragable) {
				if (isListHasChange()) {				
					updateUserChannels();
				} else {
					exitEditMode();
				}
			} else {
				editListView();
			}
			break;
		default:
			break;
		}
	}
	
	private void showConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.has_not_save_changes);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setNeutralButton(R.string.discard, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitEditMode();
			}
		});
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ListenableFuture<Boolean> future = updateUserChannels();
				Futures.addCallback(future, new FutureCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								exitEditMode();
							}
							
						});
					}

					@Override
					public void onFailure(Throwable arg0) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								exitEditMode();
							}
							
						});
					}
					
				});
			}
		});
		builder.show();
	}

	/**
	 * Reset list to it's initial form. When no drag functions are done.
	 * 
	 */
	private void resetEditableList() {
		dragList.setDragEnabled(false);
		isListViewDragable = false;
		adapter.setAdapterData(subScribedChannels);
		if (dragList.getAdapter() == null) {
			dragList.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();		
	}

	/**
	 * Call update service when user wants to update list.
	 * 
	 */
	private ListenableFuture<Boolean> updateUserChannels() {
		final SettableFuture<Boolean> result = SettableFuture.create();
		List<ObjectElement> objects = new ArrayList<ObjectElement>();
		objects.addAll(adapter.getAdapterData());
		
		for (Integer sequence : nonAndroidApplications.keySet()) {
			ObjectElement objectElement = nonAndroidApplications.get(sequence);
			objects.add(sequence, objectElement);
		}
		
		final JsonArrayElement array = new JsonArrayElement(new JsonArray());
		for (int i = 0; i < objects.size(); i++) {
			ObjectElement object = objects.get(i);
			if (object != null) {
				object.set(UserChannel.SEQUENCE_FIELD_NAME, i + 1);
			}
			array.add(object);		
		}
		
		


		array.get(0).asObjectElement().set("identifier", 1);
		array.get(0).asObjectElement().set("type", "channel");
		array.get(0).asObjectElement().set("id", (String) null);
		
		showLoadingDialog();
		Log.d("json", array.toJson());
		getRestStore().createElement(array, "user_channels", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				SharedPreferenceManager.setUserChannelCount(DragEditChannelActivity.this, array.size());
				SharedPreferenceManager.setUserChannel(DragEditChannelActivity.this, element.toJson());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						isListViewDragable = false;
						subScribedChannels = adapter.getAdapterData();
						if (dragList.getAdapter() == null) {
							dragList.setAdapter(adapter);
						}
						adapter.notifyDataSetChanged();
						dragList.setDragEnabled(false);
						setResult(Activity.RESULT_OK);
						setListHasChange(false);
						result.set(true);
					}
					
				});
			}

			@Override
			public void failure(final DatastoreException ex, String resource) {
				Log.e("fail", ex.getMessage());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(DragEditChannelActivity.this, R.string.failed_to_add_channel_or_app, Toast.LENGTH_SHORT).show();
						exitEditMode();
						dismissLoadingDialog();
						setListHasChange(false);
						result.setException(ex);
					}
					
				});
			}
			
		});
		return result;
	}

	private void editListView() {
		dragList.setDragEnabled(true);
		isListViewDragable = true;
		List<ObjectElement> objects = new ArrayList<ObjectElement>();
		objects.addAll(adapter.getAdapterData());
		tempSubScribedChannels.clear();
		tempSubScribedChannels.addAll(objects);
		adapter.setAdapterData(tempSubScribedChannels);

		if (dragList.getAdapter() == null) {
			dragList.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}

	private void callEditDialog() {

		final String[] list = getResources().getStringArray(
				R.array.dialog_option_app_n_channel);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, list);
		alertDialogBuilder.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = null;
						int requestCode = 0;
						if (which == 0) {
							intent = new Intent(DragEditChannelActivity.this, EditChannelActivity.class);
							requestCode = Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_CHANNEL;
							long[] subscribedChannel = getSubscribedChannelId();
							intent.putExtra(EXTRA_SUBCRIBED_CHANNEL_ID, subscribedChannel);													
						} else {
							intent = new Intent(DragEditChannelActivity.this, LeaderBoardActivity.class);
							requestCode = Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION;
						}
						intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
						startActivityForResult(intent, requestCode);
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
	
	private long[] getSubscribedChannelId() {
		List<Long> list = new ArrayList<Long>();
		for (ObjectElement object : subScribedChannels) {
			if (object != null) {
				DataElement id = object.get(UserChannel.IDENTIFIER_FIELD_NAME);
				DataElement type = object.get(UserChannel.TYPE_FIELD_NAME);
				if (id != null && id.isPrimitive() && type != null && type.isPrimitive() && UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
					list.add(id.asPrimitiveElement().valueAsLong());
				}
			}		
		}
		long[] array = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	private void getUserChannel() {
		subScribedChannels.clear();
		showLoadingDialog();
		getRestStore().performQuery(new Query().fieldIsOrderedBy(UserChannel.SEQUENCE_FIELD_NAME, Ordering.ASCENDING),
				"user_channels", new StoreCallback() {

			@Override
			public void success(final DataElement element, String resource) {
				if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
					ListenableFuture<Boolean> result = hasInitalDynamicChannel();
					Futures.addCallback(result, new FutureCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean rst) {
							initDynamicChannelList(rst, element.asArrayElement());
						}

						@Override
						public void onFailure(Throwable arg0) {
							
						}
						
					});
				} else {
					getOfflineUserChannel();
				}
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				Log.e("", "");
				getOfflineUserChannel();
			}
			
		});
	}
	
	private void getOfflineUserChannel() {
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
    				initDynamicChannelList(true, array);
    				return;
    			}
    		}
    	}
    	
    	initDynamicChannelList(false, array);
    }
	
	private void initDynamicChannelList(final boolean alreadyAddedSystemApp, ArrayElement userChannels) {
		final List<String> applicationIds = new ArrayList<String>();
		final HashMap<String, Integer> appIndexMap = new HashMap<String, Integer>();
		final List<Long> channelIds = new ArrayList<Long>();
		final HashMap<Long, Integer> channelIndexMap = new HashMap<Long, Integer>();
		
		int preIndex = 0;
		if (!alreadyAddedSystemApp) {
			preIndex = 4;
		}
		
		int index = 0;
		nonAndroidApplications.clear();
		for (DataElement dataElement : userChannels) {
			if (dataElement != null && dataElement.isObject()) {
				ObjectElement object = dataElement.asObjectElement();
				DataElement type = object.get(UserChannel.TYPE_FIELD_NAME);
				DataElement identifier = object.get(UserChannel.IDENTIFIER_FIELD_NAME);
				DataElement bundleIdentifier = object.get(ChannelItem.BUNDLE_IDENTIFIER);
				DataElement sequence = object.get(UserChannel.SEQUENCE_FIELD_NAME);
				if (type != null && type.isPrimitive()) {
					if (UserChannel.TYPE_APPLICATION.equals(type.asPrimitiveElement().valueAsString())) {
						if (sequence != null && sequence.isPrimitive()
								&& bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
							appIndexMap.put(bundleIdentifier.asPrimitiveElement().valueAsString(), index);
							applicationIds.add(bundleIdentifier.asPrimitiveElement().valueAsString());
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
							object.set(UserChannel.IMAGE_URL_FIELD_NAME, getSystemAppDrawable(identifier.asPrimitiveElement().valueAsInt()));
						}
					}
				}
				int targetSequence = sequence != null && sequence.isPrimitive() ? sequence.asPrimitiveElement().valueAsInt() : 0;
				targetSequence = targetSequence + preIndex;
				object.set(UserChannel.SEQUENCE_FIELD_NAME, targetSequence);
				subScribedChannels.add(object);
			}
			index++;
		}
		
		getDownloadedApplications(subScribedChannels, appIndexMap, applicationIds);
		setChannelDetails(alreadyAddedSystemApp, channelIds,
				channelIndexMap, subScribedChannels);

	}
	
	private void initListView(List<ObjectElement> list) {
		adapter.setAdapterData(list);
		if (dragList.getAdapter() == null) {
			dragList.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}
	
	private ObjectElement createSystemAppElement(int id, String name, int sequence) {
		JsonObjectElement object = new JsonObjectElement();
		object.set(UserChannel.IDENTIFIER_FIELD_NAME, id);
		object.set(UserChannel.NAME_FIELD_NAME, name);
		object.set(UserChannel.SEQUENCE_FIELD_NAME, sequence);
		object.set(UserChannel.ID_FIELD_NAME, UUID.randomUUID().toString());
		object.set(UserChannel.TYPE_FIELD_NAME, UserChannel.TYPE_SYSTEOM_APPLICATION);
		Date date = new Date();
		object.set(UserChannel.CREATED_DATE_FIELD_NAME, date.getTime());
		object.set(UserChannel.LAST_MODIFIED_DATE_FIELD_NAME, date.getTime());
		object.set(UserChannel.IMAGE_URL_FIELD_NAME, getSystemAppDrawable(id));

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
	
	private ListenableFuture<DataElement> getChannels(List<Long> channelIds) {
		String ids = "";
		for (Long id : channelIds) {
			ids = ids + id + ",";
		}
		
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		return getChannelDetail(ids);
	}
	
	private ListenableFuture<DataElement> getChannelDetail(String channelIds) {
		String rawQuery = "select t1.channel_id channel_id, sequence, name, number, image, filter_name, t1.id "
                + "   from ( select distinct c.id channel_id, c.sequence, c.name, count(a.id) number, ca.id id   "
                + "  from channels c inner join channel_articles ca on (c.id = ca.channel_id) inner   " + "  join articles a on (a.id = ca.article_id) group by c.id ) t1 inner join (  "
                + "   select ca2.channel_id channel_id, max(a2.article_date) article_date,  " + "   a2.image image from articles a2 inner join channel_articles ca2 on "
                + "(a2.id =     ca2.article_id) where image != '' group by ca2.channel_id ) t2 on t1.channel_id = t2.channel_id   "
                + "  inner join (select category_channels.channel_id channel_id, max(    category_channels.lastmoddate)," + " categories.filter_name  filter_name from categories "
                + " inner join category_channels on (category_channels.category_id = categories.id)   "
                + (!channelIds.isEmpty() ? " where channel_id in (" + channelIds + ") " : "")
                + " group by category_channels.channel_id) t3 on t1.channel_id = t3.channel_id "
                + "order by sequence asc ";
		return getSqliteStore().performRawQuery(rawQuery, EPassSqliteStoreOpenHelper.SCHEMA_CHANNEL_ARTICLE, null);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	protected void showEditDragableChoicesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.dialog_option_app_n_channel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_CHANNEL || requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION)
				&& resultCode == Activity.RESULT_OK) {
			String result = data.getStringExtra(EXTRA_RESULT_ITEM);
			String[] removeApplicationItems = data.getStringArrayExtra(AppStoreActivity.EXTRA_REMOVE_ITEM);
			List<String> items = new ArrayList<String>();
			if (removeApplicationItems != null) {
				for (int i = 0; i < removeApplicationItems.length; i++) {
					items.add(removeApplicationItems[i]);
				}

			}
			boolean hasChange = false;
			if (result != null) {
				JsonArrayElement object = null;
				try {
					object = new JsonArrayElement(result);
				} catch (Exception e) {
					
				}
				if (object != null) {
					for (DataElement dataElement : object) {
						if (dataElement != null && dataElement.isObject()) {
							tempSubScribedChannels.add(dataElement.asObjectElement());
						}
					}
					hasChange = true;
					
				}
				
			}
			
			if (items.size() > 0) {
				for (int i = 0; i < tempSubScribedChannels.size(); i++) {
					DataElement type = tempSubScribedChannels.get(i).get(UserChannel.TYPE_FIELD_NAME);
					if (type != null && type.isPrimitive() && type.asPrimitiveElement().valueAsString().equals(UserChannel.TYPE_APPLICATION)) {
						DataElement id = tempSubScribedChannels.get(i).get(UserChannel.ID_FIELD_NAME);
						if (id != null && id.isPrimitive() && items.contains(id.asPrimitiveElement().valueAsString())) {
							hasChange = true;
							tempSubScribedChannels.remove(i);
						}
					}
				}
			} 
			
			if (hasChange) {
				setListHasChange(true);
				adapter.setAdapterData(tempSubScribedChannels);
				if (dragList.getAdapter() == null) {
					dragList.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
			}
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

	private void setChannelDetails(final boolean alreadyAddedSystemApp,
			final List<Long> channelIds,
			final HashMap<Long, Integer> channelIndexMap,
			final List<ObjectElement> channels) {
		ListenableFuture<DataElement> channelResult = getChannels(channelIds);
		Futures.addCallback(channelResult, new FutureCallback<DataElement>() {
			
			@Override
			public void onSuccess(DataElement element) {
				if (element != null && element.isArray()) {
					for (DataElement channel : element.asArrayElement()) {
						if (channel != null && channel.isObject()) {
							DataElement id = channel.asObjectElement().get(CHANNEL_ID_FIELD_NAME);
							if (id != null && id.isPrimitive() && channelIndexMap.containsKey(id.asPrimitiveElement().valueAsLong())) {
								if (id.asPrimitiveElement().valueAsLong() == 1) {
								} else if (alreadyAddedSystemApp) {
								}
								DataElement name = channel.asObjectElement().get(CHANNEL_NAME_FIELD_NAME);
								if (name != null && name.isPrimitive()) {
									channels.get(channelIndexMap.get(id.asPrimitiveElement().valueAsLong()))
										.set(UserChannel.NAME_FIELD_NAME, name.asPrimitiveElement().valueAsString());
								}
								DataElement image = channel.asObjectElement().get(UserChannel.IMAGE_URL_FIELD_NAME);
								if (image != null && image.isPrimitive()) {
									channels.get(channelIndexMap.get(id.asPrimitiveElement().valueAsLong()))
										.set(UserChannel.IMAGE_URL_FIELD_NAME, image.asPrimitiveElement().valueAsString());
								}
							}
						}
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						if (!alreadyAddedSystemApp) {
							channels.add(1, createSystemAppElement(SystemApplication.EPAY_SLIP.getId(),
									getString(R.string.channel_epay_slip), SystemApplication.EPAY_SLIP.getId() + 1));
							channels.add(2, createSystemAppElement(SystemApplication.TASK.getId(),
									getString(R.string.channel_to_do_list), SystemApplication.TASK.getId() + 1));
							channels.add(3, createSystemAppElement(SystemApplication.LEAVE.getId(),
									getString(R.string.channel_apply_leave), SystemApplication.LEAVE.getId() + 1));
							channels.add(4, createSystemAppElement(SystemApplication.APP_STORE.getId(),
									getString(R.string.channel_app_store), SystemApplication.APP_STORE.getId() + 1));
						}
						final List<ObjectElement> results = new ArrayList<ObjectElement>();
						for (int i = 0; i < channels.size(); i++) {
							if (channels.get(i) != null) {
								results.add(channels.get(i));
							}
						}
						initListView(results);								
					}
					
				});
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("", "");
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

	public boolean isListHasChange() {
		return listHasChange;
	}

	public void setListHasChange(boolean hasChange) {
		this.listHasChange = hasChange;
	}
}