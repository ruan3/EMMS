package com.esquel.epass.leave;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.BaseGestureActivity;


/**
 * 
 * Contains reason for leave rejection.
 * 
 */
public class AnnualLeaveRejectedActivity extends BaseGestureActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_annual_leave_rejection);
		setUIData();
	}

	/**
	 * set data to the UI when Activity is initialized.
	 * 
	 */
	private void setUIData() {
		// Setting Visibility of Header Icons
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		// findViewById(R.id.left_first).setVisibility(View.INVISIBLE);
		 findViewById(R.id.right_first).setVisibility(View.INVISIBLE);
		 
		 findViewById(R.id.left_first).setOnClickListener(this);
		 
		Bundle b = getIntent().getExtras();
		String rejectReason = b.getString(AnnualAppliedLeaveActivity.KEY_LEAVE_REJECT_REASON, "");
		//Header of the activity 
		TextView titleMenu = (TextView) findViewById(R.id.title_menu);
		titleMenu.setText(R.string.title_activity_leave_rejected);
		
		TextView editRejection  = (TextView) findViewById(R.id.rejection);
		editRejection.setText(rejectReason);
		
		 
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.left_first :
			onBackPressed();
			break;	

		default:
			break;
		}
	}
	
	
}
