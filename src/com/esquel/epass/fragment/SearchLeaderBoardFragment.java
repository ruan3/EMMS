package com.esquel.epass.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.ArticleViewActivity;
import com.esquel.epass.appstore.LeaderBoardActivity;
import com.esquel.epass.appstore.AppListAdapter;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Region;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.widget.DataAdapter;

/**
 * 
 * 
 */
public class SearchLeaderBoardFragment extends ListFragment {

	private static final int SAMPLE_INTERVAL = 500;
	// private static final int COUNT = 10;
	public static final String KEY_ID = "id";
	public static final String KEY_IS_EMPTY = "empty";
	private AppListAdapter adapter;
	public static final String KEY_VALUE_LIKE = "like";
	
	public interface OnSearchListener {
		void onResultItemClickListener(DataElement element);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.search_view, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Query query = getQuery(getActivity());
		if (getArguments() != null && getArguments().getString(KEY_VALUE_LIKE) != null) {
			query.fieldIsLike(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "name", "%" + getArguments().getString(KEY_VALUE_LIKE) + "%");
		}
		adapter = new AppListAdapter(getActivity(), ((AppApplication) getActivity().getApplication()).getRestStore(), "application_versions", query);
		getListView().setAdapter(adapter);
	}


	public void resetSearchResult() {
		getView().findViewById(R.id.no_result).setVisibility(View.GONE);
		getListView().setVisibility(View.GONE);
	}

	public void search(final List<String> ids) {
		if (ids == null || ids.size() == 0) {
			getView().findViewById(R.id.no_result).setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			return;
		}
		getView().post(new Runnable() {

			@Override
			public void run() {
				getView().findViewById(R.id.no_result).setVisibility(View.GONE);
				getListView().setVisibility(View.GONE);
				getListView().setAdapter(
						new ResultAdapter(((AppApplication) getActivity()
								.getApplication()).getSqliteStore(),
								EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE,
								new Query()
										.fieldIsIn(Article.ID_FIELD_NAME, ids)
										.resultIsGroupBy(Article.ID_FIELD_NAME)
										.resultIsDistinct(true)
										.fieldIsOrderedBy(
												Article.TITLE_FIELD_NAME,
												Ordering.ASCENDING)
										.resultIsDistinct(true)));
				getListView().postDelayed(new Runnable() {

					@Override
					public void run() {
						getListView().setVisibility(View.VISIBLE);
					}

				}, SAMPLE_INTERVAL);
			}

		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (getActivity() instanceof LeaderBoardActivity) {
			((LeaderBoardActivity) getActivity()).cancelSearch();
		}
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ResultAdapter extends DataAdapter {

		public ResultAdapter(Store store, String schema, Query query) {
			super(store, schema, query);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		protected void onDataAvailable(final DataElement data, final View view) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					handleLayout(data, view);
				}

			});
		}

		private void handleLayout(final DataElement data, final View view) {
			ImageView imageView = (ImageView) view.findViewById(R.id.icon);
			TextView textView = (TextView) view.findViewById(R.id.title);

			DataElement e = data.asObjectElement()
					.get(Article.TITLE_FIELD_NAME);
			String name = "";
			if (e != null && e.isPrimitive()) {
				name = e.asPrimitiveElement().valueAsString();
				textView.setText(name);
			}

			e = data.asObjectElement().get(Article.IMAGE_FIELD_NAME);
			if (e != null && e.isPrimitive()) {
				String image = e.asPrimitiveElement().valueAsString();
				loadImage(image, imageView);

			}

			e = data.asObjectElement().get(Article.ID_FIELD_NAME);
			if (e != null && e.isPrimitive()) {
				final String id = e.asPrimitiveElement().valueAsString();
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								ArticleViewActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("id_article", id);
						getActivity().startActivity(intent);
					}

				});

			} else {
				view.setOnClickListener(null);
			}
		}

		@Override
		public View getInflatedView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(getActivity(),
						R.layout.item_leaderboard_cell, null);
			}
			TextView count = (TextView) convertView
					.findViewById(R.id.list_count);

			ImageView appIcon = (ImageView) convertView
					.findViewById(R.id.app_icon);

			TextView appName = (TextView) convertView
					.findViewById(R.id.app_name);
			TextView appDecs = (TextView) convertView
					.findViewById(R.id.app_decs);
			TextView appCategory = (TextView) convertView
					.findViewById(R.id.app_category);

			RatingBar appRating = (RatingBar) convertView
					.findViewById(R.id.app_ratingbar);

			return view;
		}

		protected void onError(Throwable t) {
			// do nothing
		}

	}

	private void loadImage(final String url, final ImageView imageView) {
		if (((AppApplication) (getActivity()).getApplication())
				.getImageDownloader() != null) {
			((AppApplication) (getActivity()).getApplication())
					.getImageDownloader().download(url,
							R.drawable.icon_loading_image,
							Constants.DEFAULT_IMAGE_WIDTH,
							Constants.DEFAULT_IMAGE_HEIGHT, imageView);
		}
	}
	
	public void setQuery(Context context, String searchingKeyword) {
		Query query = getQuery(context);
		query.fieldIsLike(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "name", "%" + searchingKeyword + "%");
		
		/*
		 * The server has not expose the applicaton_keywords of the resource
		 */
//		query.fieldIsLike("application_keywords/keyword/name", searchingKeyword);
		adapter.getCache().setQuery(query);
		adapter.notifyDataSetChanged();
	}
	
	private Query getQuery(Context context) {
		Query query = new Query();
		query.fieldIsEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_platform", "Android")
		.expandField(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_versions")
		.expandField(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "application_app_categories/app_category");
		
		query.fieldIsOrderedBy("lastmoddate", Ordering.DESCENDING);
		query.fieldIsEqualTo("released", true);
		query.fieldIsNotEqualTo(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + Application.IDENTIFIER_FIELD_NAME, getActivity().getPackageName());
		//set the region to the query
		String regionJson = SharedPreferenceManager.getAppStoreRegion(getActivity());
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
}
