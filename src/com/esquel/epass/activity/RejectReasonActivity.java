package com.esquel.epass.activity;

import com.esquel.epass.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RejectReasonActivity extends BaseGestureActivity {
	
	public static final String EXTRA_REJECT_REASON = "reject-reason";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reject_reason);
		
		String reason = getIntent().getStringExtra(EXTRA_REJECT_REASON);
		if (reason != null) {
			TextView textView = (TextView) findViewById(R.id.reject_reason);
			textView.setText(reason);
		}
		
		findViewById(R.id.left_first).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
        	
        });
	}

}
