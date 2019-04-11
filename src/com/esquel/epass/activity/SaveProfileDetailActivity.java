package com.esquel.epass.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.rest.JsonArrayElement;

/**
 * 
 * @author joyaether
 * 
 */
public class SaveProfileDetailActivity extends Activity {

	RelativeLayout title;
	ListView contentListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wages_self_inquiry);
		title = (RelativeLayout) findViewById(R.id.rlayout_Wages_title);
		contentListView = (ListView) findViewById(R.id.lv_wages_content);

		// include title bar
		View child = getLayoutInflater().inflate(R.layout.titlebar, null);
		ImageView img2 = (ImageView) child.findViewById(R.id.ibtn_title_left);
		img2.setVisibility(View.INVISIBLE);
		TextView tv2 = (TextView) child.findViewById(R.id.tv_tile_right);
		tv2.setVisibility(View.INVISIBLE);
		TextView tv3 = (TextView) child.findViewById(R.id.tv_title_center);
		tv3.setText("工资自助查询");
		ImageView img3 = (ImageView) child.findViewById(R.id.ibtn_title_center);
		img3.setBackgroundResource(R.drawable.icon_len);
		title.addView(child);

		String json = getIntent().getStringExtra(
				SlipActivity.EXTRA_PAY_SLIP_DATE);
		if (json == null) {
			return;
		}

		JsonArrayElement array = new JsonArrayElement(json);
		ArrayList<String> dt = new ArrayList<String>();

		for (DataElement e : array) {
			ObjectElement object = e.asObjectElement();
			dt.add(object.get(SlipActivity.YEAR_FIELD_NAME)
					.asPrimitiveElement().valueAsInt()
					+ getString(R.string.year)
					+ object.get(SlipActivity.PERIOD_FIELD_NAME)
							.asPrimitiveElement().valueAsInt()
					+ getString(R.string.month)
					+ "("
					+ object.get(SlipActivity.NET_PAY_FIELD_NAME)
							.asPrimitiveElement().valueAsString()
					+ getString(R.string.dollar) + ")");
		}

		// add view content
		// dt.add("2014年1月 (2508.37元)");
		// dt.add("2014年2月 (2388.37元)");
		// dt.add("2014年3月 (2388.37元)");
		// dt.add("2014年4月 (2388.37元)");
		// dt.add("2014年5月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");
		// dt.add("2014年6月 (2388.37元)");

		contentListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.item2, dt));
		contentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(SlipActivity.EXTRA_RESULT_POSITION, position);
				setResult(Activity.RESULT_OK, intent);
				finish();
				// Intent
			}

		});
	}

}
