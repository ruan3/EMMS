package com.esquel.epass.activity;

import com.esquel.epass.R;
import com.esquel.epass.utils.EsquelPassRegion;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * A activity to show the region selection.
 * 
 * @author joyaether
 * 
 */
public class RegionActivity extends ListActivity implements OnItemClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_region);
		String[] sampleRegions = getResources().getStringArray(
				R.array.region_selection);
		getListView().setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_single_choice,
						android.R.id.text1, sampleRegions));
		getListView().setOnItemClickListener(this);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// String[] regionCode =
		// getResources().getStringArray(R.array.region_code);
		// TODO temporary comment out this code because the server have not
		// implement other region now
		// EsquelPassRegion.setDefault(this, regionCode[position]);
		Toast.makeText(this, EsquelPassRegion.getDefault(this).toString(),
				Toast.LENGTH_SHORT).show();
	}

}
