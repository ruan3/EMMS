package com.esquel.epass.activity;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.SubscribedChannelAdapter;
import com.esquel.epass.dynamicchannel.DragEditChannelActivity;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.rest.JsonArrayElement;

public class SubscribedChannelActivity extends BaseGestureActivity implements
		OnItemClickListener, OnClickListener {

	PullToRefreshListView listSubscribeChannel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscribed_channel);

		setData();
	}

	private void setData() {
		listSubscribeChannel = (PullToRefreshListView) findViewById(R.id.listview_subscribed_channel);

		findViewById(R.id.left_first).setOnClickListener(this);

		TextView rightTopButton = (TextView) findViewById(R.id.right_button);
		rightTopButton.setText(R.string.category);
		rightTopButton.setOnClickListener(this);

		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_leader_board);

		String response = loadJSONFromAsset("tempSubscribeChannel.json");

		DataElement jsonArrayResponse = new JsonArrayElement(response);

		SubscribedChannelAdapter subscribedChannelAdapter = new SubscribedChannelAdapter(
				this, jsonArrayResponse);
		listSubscribeChannel.setAdapter(subscribedChannelAdapter);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.left_first:
			onBackPressed();
			break;

		case R.id.right_button:

			callEditListActivity();
			break;
		default:
			break;
		}
	}

	private void callEditListActivity() {
		Intent intent = new Intent(SubscribedChannelActivity.this,
				DragEditChannelActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

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
}
