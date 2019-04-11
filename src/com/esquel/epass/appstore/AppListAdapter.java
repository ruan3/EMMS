package com.esquel.epass.appstore;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.utils.EPassDataCache;
import com.esquel.epass.utils.LocaleUtils;
import com.esquel.epass.utils.LocaleUtils.SupportedLanguage;
import com.esquel.epass.utils.TimeUtils;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.widget.DataAdapter;

public class AppListAdapter extends DataAdapter {

	private Context mContext;
	private OnClickOpenButtonListener onClickOpenButtonListener;
	private OnClickDownloadButtonListener onClickDownloadButtonListener;
	
	public interface OnClickOpenButtonListener {
		void onClickOpenButton(String packageName);
	}
	
	public interface OnClickDownloadButtonListener {
		void onClickDownloadButton(DataElement item);
	}


	public AppListAdapter(Context context, Store store, String schema,
			Query query) {
		super(new EPassDataCache(store, schema, query));
		this.mContext = context;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	protected void onDataAvailable(final DataElement data, View view) {
		DataElement application = data.asObjectElement().get(ApplicationVersion.APPLICATION_FIELD_NAME);
		if (application == null || application.isNull()) {
			return;
		}
		DataElement e = application.asObjectElement().get(Application.ID_FIELD_NAME);
		
		e = application.asObjectElement().get("name");
		if (e != null && e.isPrimitive()) {
			TextView appName = (TextView) view.findViewById(R.id.app_name);
			appName.setText(e.asPrimitiveElement().valueAsString());
		}

		e = application.asObjectElement().get("application_app_categories");
		if (e != null && e.isArray()) {
			ArrayElement categories = e.asArrayElement();
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
			TextView appCategory = (TextView) view
					.findViewById(R.id.app_category);
			appCategory.setText(textOfCategories);
		}
		e = data.asObjectElement().get(ApplicationVersion.AVAILABLE_FIELD_NAME);
		if (e != null && e.isPrimitive()) {
				Calendar calendar = Calendar.getInstance();
				long releasedTime = calendar.getTimeInMillis() - e.asPrimitiveElement().valueAsLong();
				TextView description = (TextView) view.findViewById(R.id.app_decs);
				SupportedLanguage language = LocaleUtils.getLanguage(mContext);
				String result = "";
				if (language == SupportedLanguage.ENGLISH || language == SupportedLanguage.VIETNAMESE) {
					result = TimeUtils.getFormattedTime(mContext, releasedTime);
				} else {
					result = mContext.getText(R.string.released_at) + TimeUtils.getFormattedTime(mContext, releasedTime);
				}
				description.setText(result);		
		}

		e = application.asObjectElement().get("average_rating");
		if (e != null && e.isPrimitive()) {
			RatingBar appRating = (RatingBar) view
					.findViewById(R.id.app_ratingbar);
			appRating.setRating(e.asPrimitiveElement().valueAsFloat());
		}

		e = application.asObjectElement().get("icon_url");
		if (e != null && e.isPrimitive()) {
			ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
			((AppApplication) ((Activity) mContext).getApplication())
					.getImageDownloader().download(
							e.asPrimitiveElement().valueAsString(), imageView);

		}

		e = application.asObjectElement().get("identifier");
		if (e != null && e.isPrimitive()) {
			TextView button = (TextView) view.findViewById(R.id.app_state);
			final String packageName = e.asPrimitiveElement().valueAsString();
			boolean installed = Utility.isPackageInstalled(mContext, packageName);
			boolean needUpdate = Utility.isNeedUpdate(mContext, data.asObjectElement());
			int stateResId = 0;
			if (installed && needUpdate) {
				stateResId = R.string.updated;
			} else if (installed && !needUpdate) {
				stateResId = R.string.open;
			} else {
				stateResId = R.string.download;
			}
			button.setText(stateResId);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean installed = Utility.isPackageInstalled(mContext, packageName);
					boolean isNeedUpdate = Utility.isNeedUpdate(mContext, data.asObjectElement());
					if (installed && !isNeedUpdate) {
						if (getOnClickOpenButtonListener() != null) {
							getOnClickOpenButtonListener().onClickOpenButton(packageName);
						}
					} else {
						if (getOnClickDownloadButtonListener() != null) {
							getOnClickDownloadButtonListener().onClickDownloadButton(data);
						}
					}
					
				}

			});
		}
	}

	@Override
	public View getInflatedView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(mContext, R.layout.item_leaderboard_cell, null);
		}

		// reset the data in the view
		TextView count = (TextView) view.findViewById(R.id.list_count);
		count.setText("");
		ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
		imageView.setImageBitmap(null);
		TextView appName = (TextView) view.findViewById(R.id.app_name);
		appName.setText("...");
		TextView description = (TextView) view.findViewById(R.id.app_decs);
		description.setText("");
		TextView appCategory = (TextView) view.findViewById(R.id.app_category);
		appCategory.setText("");
		RatingBar appRating = (RatingBar) view.findViewById(R.id.app_ratingbar);
		appRating.setRating(0f);
		View button = view.findViewById(R.id.app_state);
		button.setOnClickListener(null);
		return view;
	}
	
	public OnClickOpenButtonListener getOnClickOpenButtonListener() {
		return onClickOpenButtonListener;
	}

	public void setOnClickOpenButtonListener(OnClickOpenButtonListener onClickOpenButtonListener) {
		this.onClickOpenButtonListener = onClickOpenButtonListener;
	}

	public OnClickDownloadButtonListener getOnClickDownloadButtonListener() {
		return onClickDownloadButtonListener;
	}

	public void setOnClickDownloadButtonListener(
			OnClickDownloadButtonListener onClickDownloadButtonListener) {
		this.onClickDownloadButtonListener = onClickDownloadButtonListener;
	}

}
