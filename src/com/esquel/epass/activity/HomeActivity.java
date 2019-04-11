package com.esquel.epass.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.ConfigurationManager;
import com.esquel.epass.R;
import com.esquel.epass.ui.LoadingDialog;
import com.esquel.epass.utils.BuildConfig;
import com.esquel.epass.utils.LogUtils;
import com.joyaether.datastore.callback.AuthorizationCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.security.IdToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author joyaether
 * 
 */
public class HomeActivity extends Activity implements OnClickListener,
        TextWatcher {

    private static final int LOGO_ALPHA_DURATION = 1000;
    private static final int LOGO_TRANSLATE_DURATION = 1000;
    private static final String LOGIN_BUTTON_COLOR = "#97919c";
    private static final float MAXIMUM_ALPHA = 1.0f;
    private static final float MINIMUN_ALPHA = 0.2f;
    private static final int ALPHA_ANIMATION_DURATION = 500;
    private static final int TRANSLATE_ANIMATION_DURATION = 500;
    private static final float PADDING_TOP = 20f;
    private LoadingDialog loadingDialog;
    public static final String KEY_USER_ID = "code";
    public static final String KEY_PASSWORD = "pass";
    public static final String KEY_REMEMBER_PASSWORD = "remember-pass";

    int heightScreen;
    int widthScreen;
    ImageView iv;
    RelativeLayout inputUserLayout;
    RelativeLayout inputPassWordLayout;
    LinearLayout loginLayout;
    EditText inputPassWord, inputUserName;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inputUserLayout = (RelativeLayout) findViewById(R.id.input_user_layout);
        inputPassWordLayout = (RelativeLayout) findViewById(R.id.input_password_layout);
        loginLayout = (LinearLayout) findViewById(R.id.login_panel);
        login = (TextView) findViewById(R.id.login);
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputPassWord.addTextChangedListener(this);
        inputUserName.addTextChangedListener(this);
        login.setOnClickListener(this);
        if (inputPassWord.getText().toString().trim().length()
                * inputUserName.getText().toString().trim().length() == 0) {
            login.setEnabled(false);
            login.setTextColor(Color.parseColor(LOGIN_BUTTON_COLOR));
        } else {
            login.setEnabled(true);
            login.setTextColor(Color.WHITE);
        }
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String user = pref.getString(KEY_USER_ID, null);
        if (user != null) {
            inputUserName.setText(user);
        }
        final CheckBox checkBox = (CheckBox) findViewById(R.id.remember_password);
        boolean rememberPassword = pref
                .getBoolean(KEY_REMEMBER_PASSWORD, false);
        if (rememberPassword) {
            String password = pref.getString(KEY_PASSWORD, null);
            if (password != null) {
                inputPassWord.setText(password);
            }
        }
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(HomeActivity.this);
                pref.edit().putBoolean(KEY_REMEMBER_PASSWORD, isChecked)
                        .commit();
            }

        });
        checkBox.setChecked(rememberPassword);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        heightScreen = dm.heightPixels;
        widthScreen = dm.widthPixels;

        iv = (ImageView) findViewById(R.id.log);
        iv.post(new Runnable() {

            @Override
            public void run() {
                int h = iv.getHeight();
                int w = iv.getWidth();
                AnimationSet animationSet = createAnimationSet(w, h);
                animationSet.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation arg0) {
                        // Do nothing
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                        // Do nothing
                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                    	final float epassTitleAlpha = 0.1f;
                        final float userLayoutAlpha = 0.25f;
                        final float passLayoutAlpha = 0.75f;
                        final float loginLayoutAlpha = 0.9f;
                        findViewById(R.id.esquel_pass_title)
                        	.setAnimation(createAnimationSetX(epassTitleAlpha));
                        inputUserLayout
                                .startAnimation(createAnimationSetX(userLayoutAlpha));
                        inputPassWordLayout
                                .startAnimation(createAnimationSetX(passLayoutAlpha));
                        loginLayout
                                .startAnimation(createAnimationSetX(loginLayoutAlpha));
                        findViewById(R.id.login_description)
                        	.setAnimation(createAnimationSetX(loginLayoutAlpha));

                    }
                });
                iv.startAnimation(animationSet);
            }
        });
        findViewById(R.id.remember_password_text).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkBox.setChecked(!checkBox.isChecked());
			}
        	
        });

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
        ConfigurationManager.getInstance().startToGetNewConfig(this);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }

    public AnimationSet createAnimationSetX(float position) {

        int x = (int) ((float) widthScreen * position);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);

        TranslateAnimation trans = new TranslateAnimation(x, 0, 0, 0);

        trans.setDuration(TRANSLATE_ANIMATION_DURATION);

        AlphaAnimation alpha = new AlphaAnimation(1 - position, 1.0f);
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(trans);
        return animationSet;
    }

    public AnimationSet createAnimationSet(int w, int h) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);
        TranslateAnimation trans = new TranslateAnimation(
                (widthScreen - w) / 2f, (widthScreen - w) / 2f,
                (heightScreen - h) / 2f, PADDING_TOP);
        trans.setDuration(LOGO_TRANSLATE_DURATION);

        AlphaAnimation alpha = new AlphaAnimation(MINIMUN_ALPHA, MAXIMUM_ALPHA);
        alpha.setDuration(LOGO_ALPHA_DURATION);
        // add new animations to the set
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(trans);
        return animationSet;
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        if (inputPassWord.getText().toString().trim().length()
                * inputUserName.getText().toString().trim().length() == 0) {
            login.setEnabled(false);
            login.setTextColor(Color.parseColor(LOGIN_BUTTON_COLOR));
        } else {
            login.setEnabled(true);
            login.setTextColor(Color.WHITE);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
            int arg3) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // Do nothing
    }

    @Override
    public void onClick(View v) {
        if (v == login) {
        	LogUtils.e("登录开始");
            final String userid = inputUserName.getText().toString();
            final String password = inputPassWord.getText().toString();
            if (!hasNetworkConnection()) {
                showDialog(getString(R.string.warning_title),
                        getString(R.string.network_error));
                return;
            }

            if (userid == null || userid.length() == 0) {
                showDialog(getString(R.string.warning_title),
                        getString(R.string.warning_message_no_user));
                return;
            }
            if (password == null || password.length() == 0) {
                showDialog(getString(R.string.warning_title),
                        getString(R.string.warning_message_no_password));
                return;
            }
            loadingDialog.show();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String apiEndPoint = pref.getString(
            		ConfigurationManager.RESOURCE_END_POINT_FIELD_NAME, 
            		BuildConfig.getServerAPIEndPoint());
            String authEndPoint = pref.getString(
                    ConfigurationManager.AUTHORIZATION_END_POINT, 
                    BuildConfig.getServerAPIEndPoint() + "auth/");
            LogUtils.e("请求连接--->"+authEndPoint+"----->"+apiEndPoint);
            ((AppApplication) getApplication()).getRestStore().login(
                    inputUserName.getText().toString(),
                    inputPassWord.getText().toString(),
                    new AuthorizationCallback() {

                        @Override
                        public void success(final IdToken token) {
                        	LogUtils.e("登录成功--->"+token);
                        	if (token == null) {
                        		runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                        Toast.makeText(HomeActivity.this,
                                                getString(R.string.login_error),
                                                Toast.LENGTH_LONG).show();
                                       
                                    }

                                });
                        		return;
                        	}
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                    SharedPreferences pref = PreferenceManager
                                            .getDefaultSharedPreferences(HomeActivity.this);
                                    pref.edit().putString(KEY_USER_ID, userid)
                                            .putString(KEY_PASSWORD, password)
                                            .commit();
                                    Intent intent = new Intent(
                                            HomeActivity.this,
                                            ChannelListFlipActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            });

                        }

                        @Override
                        public void failure(final DatastoreException ex) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                	LogUtils.e("登录失败--->"+ex.toString());
                                    loadingDialog.dismiss();
                                    Toast.makeText(HomeActivity.this,
                                            getString(R.string.login_error),
                                            Toast.LENGTH_LONG).show();
                                   
                                }

                            });

                        }
                    });
        }

    }

    private boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();     
        if (info == null) {
        	return false;
        }
        NetworkInfo.State network = info.getState();
        return network == NetworkInfo.State.CONNECTED;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.warning_message_confirm, null);
        builder.show();
    }
}
