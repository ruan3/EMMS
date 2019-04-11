package com.esquel.epass.dynamicchannel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.esquel.epass.R;
import com.esquel.epass.activity.BaseGestureActivity;
import com.esquel.epass.adapter.DesiredChannelAdapter;
import com.esquel.epass.schema.CategoryChannel;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;

public class AddChannelAcvitivty extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	PullToRefreshListView desiredChannelList;
	public static final String EXTRA_SUBCRIBED_CHANNEL_ID = "channel-id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desired_channel);
		setData();
	}

	private void setData() {

		findViewById(R.id.left_first).setOnClickListener(this);

		findViewById(R.id.right_button).setVisibility(View.INVISIBLE);

		desiredChannelList = (PullToRefreshListView) findViewById(R.id.listview_desired_channel);
		
		
		String response = loadJSONFromAsset("tempSubscribeChannel.json");

		DataElement jsonArrayResponse = new JsonArrayElement(response);

		DesiredChannelAdapter desiredChannelAdapter = new DesiredChannelAdapter(this,
				jsonArrayResponse);

		desiredChannelList.setAdapter(desiredChannelAdapter);
	}
	
	public String loadJSONFromAsset(String jsonFileName) {
		String json = null;
		try {

			InputStream is = getApplicationContext().getAssets().open(
					jsonFileName);

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			return null;
		}
		return json;

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
	
	private Query getQuery() {
		int[] subcribedChannelId = getIntent().getIntArrayExtra(EXTRA_SUBCRIBED_CHANNEL_ID);
		Query query = new Query().expandField(CategoryChannel.CATEGORY_FIELD_NAME)
				.expandField(CategoryChannel.CHANNEL_FIELD_NAME)
				.fieldIsOrderedBy(CategoryChannel.CATEGORY_FIELD_NAME, Ordering.ASCENDING);
		if (subcribedChannelId != null) {
			query.fieldIsIn(CategoryChannel.CHANNEL_FIELD_NAME, Arrays.asList(subcribedChannelId));
		}
		return query;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

}
