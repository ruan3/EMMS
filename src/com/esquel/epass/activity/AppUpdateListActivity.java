package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.esquel.epass.adapter.EnterpriseAdapter;
import com.esquel.epass.item.ItemEnterprise;
import com.esquel.epass.R;

/**
 * @author zshop
 * 
 */
public class AppUpdateListActivity extends BaseActivity {

	private static final int MAX_SIZE = 10;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_update_list);
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
	}

}
