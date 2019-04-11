package com.esquel.epass.activity;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.esquel.epass.adapter.UserProfileAdapter;
import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class UserProfileActivity extends BaseActivity implements
		OnClickListener {

	ImageView more;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		more = (ImageView) findViewById(R.id.iv_more);
		more.setOnClickListener(this);
		String[] products = { "Dell Inspiron", "HTC One X", "HTC Wildfire S",
				"HTC Sense", "HTC Sensation XE", "iPhone 4S",
				"Samsung Galaxy Note 800", "Samsung Galaxy S3", "MacBook Air",
				"Mac Mini", "MacBook Pro", "love 1", "love 2" };
		List<String> list = Arrays.asList(products);
		UserProfileAdapter adapter = new UserProfileAdapter(
				UserProfileActivity.this, list);
		ListView lv = (ListView) findViewById(R.id.listview);
		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(more)) {
			Intent intent = new Intent(UserProfileActivity.this,
					RewardsActivity.class);
			startActivity(intent);
		}
	}
}
