package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.EnterpriseAdapter;
import com.esquel.epass.item.ItemEnterprise;

/**
 * 
 * @author joyaether
 * 
 */
public class EnterpriseActivity extends BaseActivity {

	private static final int MAX_SIZE = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enterprise_apps_list);

		/**
		 * set up top menu
		 */

		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);

		((ImageView) findViewById(R.id.right_first))
				.setImageResource(R.drawable.icon_menu_enterprise);

		ListView lv = (ListView) findViewById(R.id.listview);

		List<ItemEnterprise> list = new ArrayList<ItemEnterprise>();
		for (int i = 0; i < MAX_SIZE; i++) {
			ItemEnterprise item = new ItemEnterprise();
			item.setDescribe("Describe");
			item.setEnterpriseName("enterprise_name");
			item.setTitle("title");
			list.add(item);
		}
		EnterpriseAdapter adapter = new EnterpriseAdapter(this, list);
		lv.setAdapter(adapter);

		findViewById(R.id.right_first).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(EnterpriseActivity.this,
								AppUpdateListActivity.class);
						startActivity(intent);

					}
				});
	}

}
