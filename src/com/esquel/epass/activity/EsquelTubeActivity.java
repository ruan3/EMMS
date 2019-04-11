package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.EsquelTubeAdapter;
import com.esquel.epass.item.ItemEsquelTube;

/**
 * 
 * @author joyaether
 * 
 */
public class EsquelTubeActivity extends BaseActivity {

	private static final int MAX_SIZE = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_esqueltube);
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);

		ImageButton ib = ((ImageButton) findViewById(R.id.right_first));

		ib.setImageResource(R.drawable.icon_search_white);

		ListView tab1ListView = (ListView) findViewById(R.id.lv1);
		ListView tab2ListView = (ListView) findViewById(R.id.lv2);
		ListView tab3ListView = (ListView) findViewById(R.id.lv3);

		List<ItemEsquelTube> list = new ArrayList<ItemEsquelTube>();
		for (int i = 0; i < MAX_SIZE; i++) {
			ItemEsquelTube item = new ItemEsquelTube();
			item.setTitle("???????????? - ?????????");
			item.setDecs("???????????? : 417,967");
			list.add(item);
		}
		EsquelTubeAdapter adapter = new EsquelTubeAdapter(this, list);
		tab1ListView.setAdapter(adapter);
		tab2ListView.setAdapter(adapter);
		tab3ListView.setAdapter(adapter);
	}
}
