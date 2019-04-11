package com.esquel.epass.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.esquel.epass.ConfigurationManager;
import com.esquel.epass.R;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author 
 * 
 */
public class BaseGestureActivity extends DataStoreActivity {

    private GestureDetector gestureDetector;
    public static final String ACTION_TOKEN_EXPIRED = "token-expired"; 
    private TokenExpiredReceiver receiver;
    private IntentFilter filter;
    private boolean registerReceiver;
    public static final String EXTRA_REQUEST_CODE = "request-code";

    @Override
    public void onRestart() {
        super.onRestart();
        if (isApplicationBroughtToBackground()) {
            ConfigurationManager.getInstance().startToGetNewConfig(this);
        }
        
		if (receiver != null && filter != null) {
			registerReceiver(receiver, filter);
			setRegisterReceiver(true);
		}

        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
    }

    private boolean isApplicationBroughtToBackground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return topActivity.getClassName().equals(getClass().getName());
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        gestureDetector = new GestureDetector(this, new GestureListener());

    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);

    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(-1, R.anim.trans_right_out);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);

    }
    
   

    private final class GestureListener extends SimpleOnGestureListener {
        private static final int SWIPE_X_DISTANCE_THRESHOLD = 120;
        private static final int SWIPE_Y_DISTANCE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceY) < SWIPE_Y_DISTANCE_THRESHOLD && Math.abs(distanceX) > SWIPE_X_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    onBackPressed();
                    return true;
                }
            }
            return false;
        }
    }
    
    private class TokenExpiredReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_TOKEN_EXPIRED)) {
				handleTokenExpired();
			}
		}
    	
    }
    
    
    private void handleTokenExpired() {
		dismissLoadingDialog();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		pref.edit().remove(EPassRestStoreClient.KEY_TOKEN).commit();
		SharedPreferenceManager.setUserName(this, null);
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(AppApplication.ACTION_LOGOUT);
		sendBroadcast(broadcastIntent);

		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		Toast.makeText(this, R.string.error_token_expired, Toast.LENGTH_SHORT).show();
		finish();
    }

	public boolean isRegisterReceiver() {
		return registerReceiver;
	}

	public void setRegisterReceiver(boolean registerReceiver) {
		this.registerReceiver = registerReceiver;
	}

}
