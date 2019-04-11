package com.esquel.epass.appstore;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.LeaderBoardCategoryAdapter;
import com.esquel.epass.adapter.LeaderBoardExpandableAdapter;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.widget.DataAdapter;

public class LeaderBoardCategoryActivity extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	ListView listCategory;
	LeaderBoardCategoryAdapter leaderAdapter;

	LeaderBoardExpandableAdapter leaderExpandableAdapter;

	// ArrayList<ItemCategory> listCAtegoryDetail;

	List<DataElement> childDataElementList;
	List<String> parentDataElementList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard_category);
		setData();
	}

	private void setData() {

		listCategory = (ListView) findViewById(R.id.listview_leaderboard_category);

		findViewById(R.id.left_first).setOnClickListener(this);
		findViewById(R.id.right_button).setVisibility(View.GONE);

		TextView rightTopButton = (TextView) findViewById(R.id.title_menu);
		rightTopButton.setText(R.string.select_category);

		long selectedCategoryId = getIntent().getLongExtra(LeaderBoardActivity.EXTRA_CATEGORY_ID, 0L);
		initCategoryHeader(selectedCategoryId);
		listCategory.setAdapter(new ApplicationCategoryAdapter(getRestStore(), "app_categories", new Query(), selectedCategoryId));
	    listCategory.setOnItemClickListener(this);

	}
	
	/**
	 * Put the header view as the default selection and this field is not getting from server.
	 */
	private void initCategoryHeader(long selectedCategoryId) {
		View view = View.inflate(this, R.layout.category_list_item, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(LeaderBoardActivity.EXTRA_CATEGORY_ID, 0);
				setResult(Activity.RESULT_OK, intent);

				finish();				
			}
			
		});
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(R.string.all_category);
		ImageView selected = (ImageView) view.findViewById(R.id.select);
		selected.setVisibility(selectedCategoryId == 0L ? View.VISIBLE : View.GONE);
		listCategory.addHeaderView(view);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.left_first:
			onBackPressed();
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		DataElement item = (DataElement) parent.getAdapter().getItem(position);
		// DataElement e =
		// item.asObjectElement().get(Task.SAMPLE_IMAGE_URL_FIELD_NAME);
		long categoryId = 0L;
		if (item != null && item.isObject()) {
			DataElement e = item.asObjectElement().get("app_category_id");
			if (e != null && e.isPrimitive()) {
				categoryId = e.asPrimitiveElement().valueAsLong();
			}
		}
		Intent intent = new Intent();
		intent.putExtra(LeaderBoardActivity.EXTRA_CATEGORY_ID, categoryId);
		setResult(Activity.RESULT_OK, intent);

		finish();

	}
	
	private class ApplicationCategoryAdapter extends DataAdapter {
		
		private long selected;

		public ApplicationCategoryAdapter(Store store, String schema,
				Query query, long selectedId) {
			super(store, schema, query);
			selected = selectedId;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		protected void onDataAvailable(DataElement data, View view) {
			TextView name = (TextView) view.findViewById(android.R.id.text1);
			ImageView selectedView = (ImageView) view.findViewById(R.id.select);
			if (data != null && data.isObject()) {
				DataElement e = data.asObjectElement().get("name");
				if (e != null && e.isPrimitive()) {
					String categoryName = e.asPrimitiveElement().valueAsString();
					name.setText(categoryName);
				}
				
				e = data.asObjectElement().get("app_category_id");
				if (e != null && e.isPrimitive()) {
					selectedView.setVisibility(selected == e.asPrimitiveElement().valueAsLong() ? View.VISIBLE : View.GONE);
				}
			}
		}

		@Override
		public View getInflatedView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(LeaderBoardCategoryActivity.this, R.layout.category_list_item, null);
			} 
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText("...");
			ImageView imageView = (ImageView) view.findViewById(R.id.select);
			imageView.setVisibility(View.GONE);
			return view;
		}
		
	}
}