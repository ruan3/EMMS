package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.esquel.epass.adapter.RewardsAdapter;
import com.esquel.epass.item.ItemReward;
import com.esquel.epass.R;

/**
 * 
 * @author hung
 * 
 */
public class RewardsActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rewards);
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_first).setVisibility(View.INVISIBLE);
		((ImageView) findViewById(R.id.right_first))
				.setImageResource(R.drawable.icon_menu_enterprise);
		ListView lv = (ListView) findViewById(R.id.listview);
		List<ItemReward> list = new ArrayList<ItemReward>();
		final int size = 10;
		for (int i = 0; i < size; i++) {
			ItemReward item = new ItemReward();
			item.setDescribe("Describe");
			item.setTitle("title");
			list.add(item);
		}
		RewardsAdapter adapter = new RewardsAdapter(this, list);
		lv.setAdapter(adapter);
	}

}
