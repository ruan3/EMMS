package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.adapter.SlipAdapter;
import com.esquel.epass.lib.flipview.FlipView;
import com.esquel.epass.lib.flipview.FlipView.OnFlipListener;
import com.esquel.epass.lib.flipview.FlipView.OnOverFlipListener;
import com.esquel.epass.lib.flipview.OverFlipMode;
import com.esquel.epass.schema.SlipData;
import com.esquel.epass.schema.UserInfo;
import com.google.gson.JsonArray;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonArrayElement;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author joyaether
 */
public class SlipActivity extends BaseGestureActivity implements OnFlipListener, OnOverFlipListener {

    private static final String CHECK_KEY = "65270289";
    // FlipViewController flipViewController;
    private static final int REQUEST_CODE_PICK_PAY_SLIP = 100;
    public static final String NET_PAY_FIELD_NAME = "netpay";
    public static final String YEAR_FIELD_NAME = "fyear";
    public static final String PERIOD_FIELD_NAME = "fperiod";
    public static final String EXTRA_PAY_SLIP_DATE = "pay-slip";
    public static final String EXTRA_RESULT_POSITION = "pos";
    private FlipView mFlipView;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int pos = data.getIntExtra(EXTRA_RESULT_POSITION, 0);
            mFlipView.flipTo(pos);
        }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
        String password = pref.getString(HomeActivity.KEY_PASSWORD, null);
        // flipViewController = new FlipViewController(SlipActivity.this);
        setContentView(R.layout.activity_slip);
        mFlipView = (FlipView) findViewById(R.id.flip_view);
        int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
        final int api = 18;
        if (currentAPIVersion == api) {
            mFlipView.set18API(true);
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ((AppApplication) getApplication()).getRestStore().performQuery(new Query().fieldIsEqualTo("username", userId).fieldIsEqualTo("password", password).fieldIsEqualTo("key", CHECK_KEY),
                "pay_slip", new OAuthRestStoreCallback(this) {
                    @Override
                    public void success(DataElement element, String resource) {
                    	if (element != null && element.isArray() && element.asArrayElement().size() > 0) {                    		                  	
                    		handlePaySlipData(progressBar, element.asArrayElement());
                    		return;
                    	} 
                    	runOnUiThread(new Runnable() {
            				
            				@Override
            				public void run() {
            					progressBar.setVisibility(View.GONE);
            					String message = "";
            					if (hasNetworkConnection()) {
            						message = getString(R.string.message_no_payslip);
            					} else {
            						message = getString(R.string.network_error);
            					}
            					Toast.makeText(SlipActivity.this, message, Toast.LENGTH_SHORT).show();
            					finish();
            				}
            				
            			});
            			return;
                    }

                    @Override
                    public void fail(DatastoreException ex, String resource) {
                    	Log.d(getClass().getSimpleName(), ex.getMessage(), ex);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                String message = "";
                                if (hasNetworkConnection()) {
                                    message = getString(R.string.message_no_payslip);
                                } else {
                                    message = getString(R.string.network_error);
                                }
                                Toast.makeText(SlipActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        });
                    }

                });
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.onEvent(this, "epay_slip");
    }

    private HashMap<String, String[]> getFilterMapWithUserDepartment(String key) {
        int userArrayId = getResources().getIdentifier(key + "_" + "user_field", "array", "com.esquel.epass");
        int workingHourId = getResources().getIdentifier(key + "_" + "working_hours_field", "array", "com.esquel.epass");
        int incomeId = getResources().getIdentifier(key + "_" + "income_field", "array", "com.esquel.epass");
        int deductionId = getResources().getIdentifier(key + "_" + "deduction_field", "array", "com.esquel.epass");
        int benefitId = getResources().getIdentifier(key + "_" + "benefit", "array", "com.esquel.epass");
        
        String[] userArray = null;
        if (userArrayId == 0) {
        	userArrayId = getResources().getIdentifier("user_field", "array", "com.esquel.epass");
        }
        userArray = getResources().getStringArray(userArrayId);
        String[] workingHourArray = null;
        if (workingHourId == 0) {
        	workingHourId = getResources().getIdentifier("working_hours_field", "array", "com.esquel.epass");
        }
        workingHourArray = getResources().getStringArray(workingHourId);
        String[] incomeArray = null;
        if (incomeId == 0) {
        	incomeId = getResources().getIdentifier("income_field", "array", "com.esquel.epass");
        }
        incomeArray = getResources().getStringArray(incomeId);
        
        String[] deductionArray = null;
        if (deductionId == 0) {
        	deductionId = getResources().getIdentifier("deduction_field", "array", "com.esquel.epass");
        }
        deductionArray = getResources().getStringArray(deductionId);
        String[] benefitArray = null;
        if (benefitId == 0) {
        	benefitId = getResources().getIdentifier("benefit", "array", "com.esquel.epass");
        }
        benefitArray = getResources().getStringArray(benefitId);
        
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put(SlipData.KEY_USER, userArray);
        map.put(SlipData.KEY_BENEFIT, benefitArray);
        map.put(SlipData.KEY_DEDUCTION, deductionArray);
        map.put(SlipData.KEY_INCOME, incomeArray);
        map.put(SlipData.KEY_WORKING_HOUR, workingHourArray);
        return map;
    }

    private void handlePaySlipData(final ProgressBar progressBar, final ArrayElement arrayElement) {
        List<SlipData> list = new ArrayList<SlipData>();
        String code = "";
        if (arrayElement.size() > 0) {
        	DataElement e = arrayElement.get(0);
        	if (e != null && e.isObject()) {
        		DataElement userInfo = e.asObjectElement().get("user_info");
        		if (userInfo != null && userInfo.isObject()) {
        			DataElement fempCode = userInfo.asObjectElement().get(UserInfo.FEMP_CODE);
        			if (fempCode != null && fempCode.isPrimitive()) {
        				code = fempCode.asPrimitiveElement().valueAsString();
        			}
        		}
        	}
        } 
        final int numberOfChar = 3;
        if (code != null && code.length() >= numberOfChar) {
            code = code.substring(0, numberOfChar);
        }
        HashMap<String, String[]> map = getFilterMapWithUserDepartment(code.toLowerCase());
        for (int i = 0; i < arrayElement.size(); i++) {
            list.add(new SlipData(arrayElement.get(i), map));
        }
        
        if (list.size() == 0) {
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					String message = "";
					if (hasNetworkConnection()) {
						message = getString(R.string.message_no_payslip);
					} else {
						message = getString(R.string.network_error);
					}
					Toast.makeText(SlipActivity.this, message, Toast.LENGTH_SHORT).show();
					finish();
				}
				
			});
        	return;
        }
        final SlipAdapter adapter = new SlipAdapter(SlipActivity.this, list, mFlipView);
        adapter.setItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	if (arrayElement != null && arrayElement.size() > arg2) {
            		DataElement e = arrayElement.get(arg2);
            		if (e != null) {
            			Intent intent = new Intent(SlipActivity.this, ContentActivity.class);
                        intent.putExtra(ContentActivity.KEY_CONTENT_OBJECT_JSON, e.toJson());
                        startActivity(intent);
            		}
            	}
                
            }

        });
        adapter.setOnGoPaySlipListener(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SlipActivity.this, SaveProfileDetailActivity.class);
                String json = generatePaySlipDateAndSalaryJson(arrayElement);
                intent.putExtra(EXTRA_PAY_SLIP_DATE, json);
                startActivityForResult(intent, REQUEST_CODE_PICK_PAY_SLIP);
            }

        });
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                mFlipView.setAdapter(adapter);
                try {
                    mFlipView.peakNext(false);

                } catch (Error e) {

                }
                mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
               // mFlipView.setEmptyView(findViewById(R.id.empty_view));
            }

        });

    }

    private boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private String generatePaySlipDateAndSalaryJson(ArrayElement array) {
        JsonArrayElement jsonArrayElement = new JsonArrayElement(new JsonArray());
        for (DataElement e : array) {
            JsonObjectElement object = new JsonObjectElement();
            if (e != null && e.isObject()) {
                // set specify fews field to a new JsonObjectElement
                object.set(YEAR_FIELD_NAME, e.asObjectElement().get(YEAR_FIELD_NAME));
                object.set(PERIOD_FIELD_NAME, e.asObjectElement().get(PERIOD_FIELD_NAME));
                object.set(NET_PAY_FIELD_NAME, e.asObjectElement().get(NET_PAY_FIELD_NAME));
            }
            jsonArrayElement.add(object);
        }
        return jsonArrayElement.toJson();
    }

    @Override
    public void onOverFlip(FlipView v, OverFlipMode mode, boolean overFlippingPrevious, float overFlipDistance, float flipDistancePerPage) {

    }

    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {

    }

    @Override
    public void release(int indexPage) {

    }

}
