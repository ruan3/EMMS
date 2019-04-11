package com.esquel.epass.appstore;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.LocaleUtils;
import com.esquel.epass.utils.PackageUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.TimeUtils;
import com.esquel.epass.utils.Utility;
import com.esquel.epass.utils.LocaleUtils.SupportedLanguage;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;

public class LeaderBoardDetailActivity extends AppStoreActivity implements
		OnClickListener {
	private static final int DEFAULT_IMAGE_HEIGHT = 100;
	private static final int DEFAULT_IMAGE_WIDTH = 100;
	public static final String KEY_APPLICATION_DETAIL = "app-detail";
	final Context context = this;
	RatingBar ratingBar;
	TextView rateApp;
	TextView rigthMenu;
	TextView appName;
	TextView appCategory;
	TextView appDecs;
	TextView description;
	JsonObjectElement element;
	float ratingStar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard_detail);
		setData();		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setRightButtonState(element);
		rateApp.setEnabled(isPackageInstalled());
	}

	private void setData() {

		ratingBar = (RatingBar) findViewById(R.id.app_ratingbar);
		rateApp = (TextView) findViewById(R.id.rate_app);

		rigthMenu = (TextView) findViewById(R.id.right_button);

		findViewById(R.id.left_first).setOnClickListener(this);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.leader_board_detail);

		rateApp.setText(R.string.rate);

		appName = (TextView) findViewById(R.id.app_name);
		appCategory = (TextView) findViewById(R.id.app_category);
		appDecs = (TextView) findViewById(R.id.app_decs);
		description = (TextView) findViewById(R.id.desc);

		Bundle b = getIntent().getExtras();
		String data = b.getString(KEY_APPLICATION_DETAIL, "");

		element = new JsonObjectElement(data);
		
		DataElement application = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME);
		ratingStar = application.asObjectElement().get("average_rating")
				.valueAsFloat();

		appName.setText(application.asObjectElement().get("name").valueAsString());

		DataElement releaseDate = element.asObjectElement().get(ApplicationVersion.AVAILABLE_FIELD_NAME);
		if (releaseDate != null && releaseDate.isPrimitive()) {
			Calendar calendar = Calendar.getInstance();
			long time = calendar.getTimeInMillis() - releaseDate.asPrimitiveElement().valueAsLong();
			SupportedLanguage language = LocaleUtils.getLanguage(this);
			String result = "";
			if (language == SupportedLanguage.ENGLISH || language == SupportedLanguage.VIETNAMESE) {
				result = TimeUtils.getFormattedTime(this, time);
			} else {
				result = getText(R.string.released_at) + TimeUtils.getFormattedTime(this, time);
			}
			appDecs.setText(result);
		}
		
		DataElement category = application.asObjectElement().get("application_app_categories");
		if (category != null && category.isArray()) {
			ArrayElement categories = category.asArrayElement();
			String textOfCategories = "";
			for (DataElement applicationAppCategory : categories) {
				DataElement appCategory = applicationAppCategory.isObject() ? applicationAppCategory
						.asObjectElement().get("app_category") : null;
				if (appCategory != null && appCategory.isObject()) {
					DataElement appNameElement = appCategory.asObjectElement()
							.get("name");
					if (appNameElement != null && appNameElement.isPrimitive()) {
						textOfCategories = textOfCategories
								+ appNameElement.asPrimitiveElement()
										.valueAsString() + ",";
					}
				}
			}
			if (textOfCategories.endsWith(",")) {
				textOfCategories = textOfCategories.substring(0,
						textOfCategories.length() - 1);
			}
			appCategory.setText(textOfCategories);
		}
		
		description.setText(application.asObjectElement().get("description")
				.valueAsString());

		ratingBar.setRating(ratingStar);

		rigthMenu.setOnClickListener(this);
		rateApp.setOnClickListener(this);

		ImageView imageView = (ImageView) findViewById(R.id.icon);
		if (getApplication() instanceof AppApplication) {
			((AppApplication) getApplication()).getImageDownloader().download(application.asObjectElement()
					.get("icon_url").asPrimitiveElement().valueAsString(), DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT, imageView);
		}
		
		rateApp.setEnabled(isPackageInstalled());
		initSubscribeButton();
		
		
	}
	
	private void initSubscribeButton() {
		final CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
		String packageName = null;
		DataElement application = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME);
		if (application != null && application.isObject()) {
			DataElement packageNameElement = application.asObjectElement().get(Application.IDENTIFIER_FIELD_NAME);
			if (packageNameElement != null && packageNameElement.isPrimitive()) {
				packageName = packageNameElement.asPrimitiveElement().valueAsString();
			}
		}
		boolean subscribed = isSubscribed();
		boolean installed = PackageUtils.isPackageExist(this, packageName);
		boolean showCheckBox = subscribed || installed;
		checkBox.setVisibility(showCheckBox || installed ? View.VISIBLE : View.INVISIBLE);
		checkBox.setChecked(subscribed);
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()) {
					addUserChannel();
				} else {
					removeUserChannel();
				}
			}
			
		});
		
	}
	
	private ListenableFuture<Integer> getSubscribedChannelCount() {
		final SettableFuture<Integer> result = SettableFuture.create();
		getRestStore().count(new Query(), "user_channels", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				int count = 0;
				if (element != null && element.isPrimitive()) {
					count = element.asPrimitiveElement().valueAsInt();
				} else {
					String userChannel = SharedPreferenceManager.getUserChannel(LeaderBoardDetailActivity.this);
					if (userChannel != null) {
						JsonArrayElement array = new JsonArrayElement(userChannel);
						count = array.size();
					}
				}
				result.set(count);
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				result.set(0);
			}
			
		});
		return result;
	}
	
	private void addUserChannel() {
		showLoadingDialog();
		ListenableFuture<Integer> future = getSubscribedChannelCount();
		Futures.addCallback(future, new FutureCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				JsonObjectElement object = new JsonObjectElement();
				DataElement application = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME);
				DataElement e = null;
				long id = 0l;
				if (application != null && application.isObject()) {
					e = application.asObjectElement().get(Application.ID_FIELD_NAME);
					if (e != null && e.isPrimitive()) {
						id = e.asPrimitiveElement().valueAsLong();
						object.set(UserChannel.IDENTIFIER_FIELD_NAME, id);
					}
					e = application.asObjectElement().get(Application.IDENTIFIER_FIELD_NAME);
					if (e != null && e.isPrimitive()) {
						object.set(UserChannel.BUNDLE_IDENTIFIER_FIELD_NAME, e.asPrimitiveElement().valueAsString());
					}
					e = application.asObjectElement().get(Application.NAME_FIELD_NAME);
					if (e != null && e.isPrimitive()) {
						object.set(UserChannel.NAME_FIELD_NAME, e.asPrimitiveElement().valueAsString());
					}
				}
				object.set(UserChannel.TYPE_FIELD_NAME, UserChannel.TYPE_APPLICATION);
				object.set(UserChannel.SEQUENCE_FIELD_NAME, result + 1);
				int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);
		    	if (requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION) {
		    		getSubscibredItems().put(id, object);
		    		runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dismissLoadingDialog();
						}
						
					});
		    		return;
		    	}
				getRestStore().createElement(object, "user_channels", new StoreCallback() {

					@Override
					public void success(DataElement element, String resource) {
						if (element != null && element.isObject()) {
							String userChannel = SharedPreferenceManager.getUserChannel(LeaderBoardDetailActivity.this);
							
							JsonArrayElement array = new JsonArrayElement(userChannel);
							array.add(element);
							SharedPreferenceManager.setUserChannel(LeaderBoardDetailActivity.this, array.toJson());
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									dismissLoadingDialog();
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
								CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
								checkBox.setChecked(!checkBox.isChecked());
							}
							
						});
					}
					
				});
			}

			@Override
			public void onFailure(Throwable arg0) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
						checkBox.setChecked(!checkBox.isChecked());
					}
					
				});
			}
		});
	}
	
	private void removeUserChannel() {
		showLoadingDialog();
		DataElement userChannel = getCurrentUserChannel();
		if (userChannel == null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dismissLoadingDialog();
					CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
					checkBox.setChecked(!checkBox.isChecked());
				}
				
			});
			return;
		}
		String id = null;
		if (userChannel.isObject()) {
			DataElement e = userChannel.asObjectElement().get(UserChannel.ID_FIELD_NAME);
			if (e != null && e.isPrimitive()) {
				id = e.asPrimitiveElement().valueAsString();
			}
		}
		if (id == null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dismissLoadingDialog();
					CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
					checkBox.setChecked(!checkBox.isChecked());
				}
				
			});
			return;
		}
		
		int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);
    	if (requestCode == Constants.REQUEST_CODE_EDIT_CHANNEL_ADD_APPLICATION) {
    		getRemoveItems().add(id);
    		runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dismissLoadingDialog();
				}
				
			});
    		return;
    	}
		
		getRestStore().deleteElement(id, "user_channels", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
					}
					
				});
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						CheckBox checkBox = (CheckBox) findViewById(R.id.subscribe_button);
						checkBox.setChecked(!checkBox.isChecked());
					}
					
				});
			}
			
		});
	}
	
	private void setRightButtonState(ObjectElement object) {
		TextView rightButton = (TextView) findViewById(R.id.right_button);
		String text = getString(R.string.download);
		if (isNeedUpdate(object)) {
			text = getString(R.string.updated);
		} else if (isPackageInstalled()) {
			text = getString(R.string.open);
		}
		rightButton.setText(text);
	}
	
	private boolean isSubscribed() {
		return getCurrentUserChannel() != null;
	}
	
	private DataElement getCurrentUserChannel() {
		String userChannel = SharedPreferenceManager.getUserChannel(this);
		if (userChannel == null) {
			return null;
		}
		JsonArrayElement array = new JsonArrayElement(userChannel);
		for (DataElement object : array) {
			if (object != null && object.isObject()) {
				DataElement type = object.asObjectElement().get(UserChannel.TYPE_FIELD_NAME);
				if (type != null && type.isPrimitive() && type.asPrimitiveElement().valueAsString().equals(UserChannel.TYPE_APPLICATION)) {
					DataElement identity = object.asObjectElement().get(UserChannel.BUNDLE_IDENTIFIER_FIELD_NAME);
					DataElement appId = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME).asObjectElement()
							.get(Application.IDENTIFIER_FIELD_NAME);
					if (identity != null && identity.isPrimitive() && appId != null && appId.isPrimitive()
							&& identity.asPrimitiveElement().valueAsString().equals(appId.asPrimitiveElement().valueAsString())) {
						return object;
					}
				}
			}
		}
		return null;
	}
	
	private boolean isPackageInstalled() {
		DataElement e = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME).asObjectElement().get("identifier");
		return e != null && e.isPrimitive() && Utility.isPackageInstalled(this, e.asPrimitiveElement().valueAsString());
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.left_first:
			onBackPressed();
			break;

		case R.id.right_button:
			if (isPackageInstalled() && !isNeedUpdate(element)) {
				String packageName = null;
				DataElement e = element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME).asObjectElement().get("identifier");
				if (e != null && e.isPrimitive()) {
					packageName = e.asPrimitiveElement().valueAsString();
				}
				if (packageName == null) {
					return;
				}
				openApp(packageName);
			} else {
				download(element);
			}
			break;

		case R.id.rate_app:
			openRateDialog();
			break;

		default:
			break;
		}

	}

	private void openRateDialog() {
		LayoutInflater li = LayoutInflater.from(context);
		View dialogView = li.inflate(R.layout.rating_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(dialogView);

		alertDialogBuilder.setTitle(R.string.dialog_description);

		final RatingBar dialogRatingBar = (RatingBar) dialogView
				.findViewById(R.id.dialog_rating_bar);

		final TextView dialogAppDesc = (TextView) dialogView
				.findViewById(R.id.dialog_app_desc);


//		dialogRatingBar.setRating(ratingBar.getRating());

		dialogAppDesc.setText("");

		// set dialog message
		alertDialogBuilder
				.setPositiveButton(R.string.rate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (dialogRatingBar.getRating() == 0f) {
									Toast.makeText(LeaderBoardDetailActivity.this, R.string.error_rating_app, Toast.LENGTH_SHORT).show();
									return;
								}
								callService(dialogRatingBar,
										dialogRatingBar.getRating());
							}

						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	protected void callService(final RatingBar dialogRatingBar,
			final float rating) {
		JsonObjectElement requestObject = new JsonObjectElement();

		requestObject.set("application",
				element.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME)
				.asObjectElement().get("application_id").valueAsInt());

		int ratingInt = (int) rating;

		requestObject.set("rate", ratingInt);

		((AppApplication) getApplication()).getRestStore()

		.createElement(requestObject, "application_ratings",
				new StoreCallback() {
					@Override
					public void success(final DataElement dataElement,
							String resource) {
						Log.d("", "");
						setResult(Activity.RESULT_OK);
					}

					@Override
					public void failure(final DatastoreException ex,
							String resource) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dismissLoadingDialog();
								Toast.makeText(getApplicationContext(),
										R.string.is_staff_error,
										Toast.LENGTH_SHORT).show();
							}
						});
					}

				});
	}

	


}
