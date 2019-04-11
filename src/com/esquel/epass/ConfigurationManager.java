package com.esquel.epass;

import java.io.File;

import org.restlet.data.Method;
import org.restlet.data.Reference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.delegate.DownloadCallback;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.joyaether.datastore.ArrayElement;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Datastore;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.rest.RestStore;
import com.joyaether.datastore.rest.RestStoreClient;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author hung
 * 
 */
public final class ConfigurationManager {

    private static final String FILE_NAME = "epass.apk";
	private static final String FIELD_VALUE_VERSION = "version";
    private static final String PARAM_NAME_FIELD_NAME = "paramName";
    private static final String CHECKING_PARAMETERS_FIELD_NAME = "checkingParameters";
    private static final String PARAM_VALUE_FIELD_NAME = "paramValue";
    private static final String PLATFORM_FIELD_NAME = "android";
    private ConfigurationTask task;
    private static ConfigurationManager manager;
    private static final String CONFIGURATION_FIELD_NAME = "configurations";
    public static final String RESOURCE_END_POINT_FIELD_NAME = "resourceEndpoint";
    public static final String CONTENT_RESOURCE_END_POINT_FIELDNAME = "contentResourceEndpoint";
    public static final String AUTHORIZATION_END_POINT = "authorizationEndpoint";
    public static final String REVOCATION_END_POINT = "revocationEndpoint";
    private static final String MESSAGE_FIELD_NAME = "messages";
    private static final String CONFIG_API_ENDPOINT = com.esquel.epass.utils.BuildConfig.getConfigurationEndPoint();
    private static final String CONFIG_FILE_NAME = "config.json";
    private AlertDialog dialog ;
    private DownloadTask downloadTask; 

    /**
     * 
     * @param mContext
     *            The context must be the context from Application
     */
    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (manager == null) {
            manager = new ConfigurationManager();
        }

        return manager;
    }

    public void startToGetNewConfig(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String messages = pref.getString(MESSAGE_FIELD_NAME, null);
        if (messages != null) {
            JsonObjectElement object = new JsonObjectElement(messages);
            if (object != null && object.isObject()) {
                handleMessage(context, object);
            }
        }
        // spend other thread to get the new config file from server
        task = new ConfigurationTask(context);
        task.execute((Void) null);
    }

    /*
     * handle a json about the message and the message is storing the data what
     * the dialog to show
     * 
     * @param array
     */
    private void handleMessage(final Context context, final DataElement element) {
        float version = getVersion(element);
        try {
            int versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
            DataElement repeatCountElement = element.asObjectElement().get("repeatCount");
            if (version > versionCode) {
            	long repeatCount = repeatCountElement == null ? 0 : repeatCountElement.asPrimitiveElement()
            			.valueAsInt();
            	long cachedRepeatCount = SharedPreferenceManager.getRepeatCount(context);
            	if (cachedRepeatCount == -100) {
            		cachedRepeatCount = repeatCount;
            	}
            	if (cachedRepeatCount > 0 ) {
            		SharedPreferenceManager.setRepeatCount(context, cachedRepeatCount - 1l);
                    showDialog(context, element);
            	} else if (cachedRepeatCount < 0 && repeatCount < 0) {
                    showDialog(context, element);
            	}
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }

	private void showDialog(final Context context, final DataElement element) {
		Handler mainHandler = new Handler(context.getMainLooper());
		mainHandler.post(new Runnable() {

		    @SuppressWarnings("deprecation")
			@Override
		    public void run() {
		    	if (dialog == null || dialog.getContext() != context) {
		    		AlertDialog.Builder builder = new AlertDialog.Builder(
		    	            context);
		    		dialog = builder.create();
		    	}                       
		        DataElement e = element.asObjectElement().get("header");
		        if (e != null && e.isPrimitive()) {
		            dialog.setTitle(e.asPrimitiveElement()
		                    .valueAsString());
		        }

		        e = element.asObjectElement().get("content");
		        if (e != null && e.isPrimitive()) {
		            dialog.setMessage(e.asPrimitiveElement()
		                    .valueAsString());
		        }

		        e = element.asObjectElement().get("confirmButtonText");
		        if (e != null && e.isPrimitive()) {
		        	final DataElement clickEventUrl = element
                            .asObjectElement().get("URL");
		        	final Reference url = new Reference(clickEventUrl.asPrimitiveElement().valueAsString());
		        	String pathDir = FILE_NAME;
		        	if (context != null && context.getExternalFilesDir(null) != null) {		        		
		        		pathDir = context.getExternalFilesDir(null).toString() + "/" + FILE_NAME;
		        	}
                	final File file = new File(pathDir);
                	final Reference destination = new Reference(file.getAbsolutePath());
                	if (downloadTask == null) {
                    	downloadTask = new DownloadTask(context, url, destination);
                	}
		            dialog.setButton(e.asPrimitiveElement()
		                    .valueAsString(), new OnClickListener() {

		                @Override
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                	
		                    final DataElement clickEventUrl = element
		                            .asObjectElement().get("URL");
		                    if (clickEventUrl != null
		                            && clickEventUrl.isPrimitive()) {
		                    	MobclickAgent.onEvent(context, "app_upgrade");
		                    	ProgressBar progressView = new ProgressBar(context);	                    	
		                    	final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		                    	alertDialog.setCancelable(false);
		                    	alertDialog.setView(progressView);
		                    	alertDialog.setTitle(R.string.downloading);
		                    	if (downloadTask == null || !downloadTask.isRunning()) {
			                    	downloadTask = new DownloadTask(context, url, destination);
		                    	}
		                    	final Dialog d = alertDialog.create();
		                    	if (!downloadTask.isRunning()) {
		                    		d.show();
		                    	}
		                    	
		                    	downloadTask.setCallback(new DownloadCallback() {

									@Override
									public void success(boolean hasUpdate) {
										((Activity) context).runOnUiThread(new Runnable() {

											@Override
											public void run() {
												d.dismiss();
												Intent intent = new Intent(Intent.ACTION_VIEW);
											    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
											    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											    context.startActivity(intent);
											}
											
										});
										
									}

									@Override
									public void fail(Exception e) {
										((Activity) context).runOnUiThread(new Runnable() {

											@Override
											public void run() {
												d.dismiss();
												
											}
											
										});
									}
		                    		
		                    	});
		                    	if (!downloadTask.isRunning()) {
			                    	downloadTask.download();
		                    	}
		                    }
		                }

		            });
		        }
		        if (!dialog.isShowing()) {
		        	if (downloadTask != null && !downloadTask.isRunning()) {
			        	dialog.show();
		        	}
		        }            
		    }

		});
	}

    private float getVersion(DataElement element) {
        DataElement e = element.asObjectElement().get(
                CHECKING_PARAMETERS_FIELD_NAME);
        if (e != null && e.isArray()) {
            for (DataElement dataElement : e.asArrayElement()) {
                DataElement paramName = dataElement.asObjectElement().get(
                        PARAM_NAME_FIELD_NAME);
                if (paramName != null
                        && paramName.isPrimitive()
                        && FIELD_VALUE_VERSION.equals(paramName
                                .asPrimitiveElement().valueAsString())) {
                    DataElement paramValue = dataElement.asObjectElement().get(
                            PARAM_VALUE_FIELD_NAME);
                    if (paramValue != null && paramValue.isPrimitive()) {
                        return Float.parseFloat(paramValue.asPrimitiveElement()
                                .valueAsString());
                    }
                }
            }
        }

        return 0f;
    }

    /**
     * 
     * @author zshop
     * 
     */
    private class ConfigurationTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public ConfigurationTask(Context context) {
            setContext(context);
        }

        @Override
        protected Void doInBackground(Void... params) {

            RestStore store = Datastore.getInstance()
                    .getRestStore(
                            new RestStoreClient(getContext(),
                                    CONFIG_API_ENDPOINT, null));

            store.performAction(Method.GET, null, "1", CONFIG_FILE_NAME, null,
                    new StoreCallback() {

                        @Override
                        public void success(DataElement element, String resource) {
                            if (element != null && element.isObject()) {
                                DataElement config = element.asObjectElement()
                                        .get(CONFIGURATION_FIELD_NAME)
                                        .asObjectElement();
                                if (config != null && config.isObject()) {
                                    // store the server api end point
                                    SharedPreferences pref = PreferenceManager
                                            .getDefaultSharedPreferences(getContext());
                                    Editor edit = pref.edit();
                                    DataElement e = config.asObjectElement()
                                            .get(RESOURCE_END_POINT_FIELD_NAME);
                                    if (e != null && e.isPrimitive()) {
                                        edit.putString(
                                                RESOURCE_END_POINT_FIELD_NAME,
                                                e.asPrimitiveElement()
                                                        .valueAsString());
                                    }

                                    e = config
                                            .asObjectElement()
                                            .get(CONTENT_RESOURCE_END_POINT_FIELDNAME);
                                    if (e != null && e.isPrimitive()) {
                                        edit.putString(
                                                CONTENT_RESOURCE_END_POINT_FIELDNAME,
                                                e.asPrimitiveElement()
                                                        .valueAsString());
                                    }

                                    e = config.asObjectElement().get(
                                            AUTHORIZATION_END_POINT);
                                    if (e != null && e.isPrimitive()) {
                                        edit.putString(AUTHORIZATION_END_POINT,
                                                e.asPrimitiveElement()
                                                        .valueAsString());
                                    }

                                    e = config.asObjectElement().get(
                                            REVOCATION_END_POINT);
                                    if (e != null && e.isPrimitive()) {
                                        edit.putString(REVOCATION_END_POINT, e
                                                .asPrimitiveElement()
                                                .valueAsString());
                                    }
                                    edit.commit();

                                    // reset the reststore to use the new
                                    // setting

                                }
                                DataElement messages = element
                                        .asObjectElement()
                                        .get(MESSAGE_FIELD_NAME)
                                        .asArrayElement();
                                if (messages != null && messages.isArray()) {
                                    handleVersionUpdate(getContext(), messages);
                                }
                                if (getContext() instanceof AppApplication) {
                                	((AppApplication) getContext())
                                    .resetRestStore();
                                } else if (getContext() instanceof Activity) {
                                	((AppApplication) ((Activity) getContext()).getApplication()).resetRestStore();
                                }
                               
                            }
                        }

                        @Override
                        public void failure(DatastoreException ex,
                                String resource) {
                            Log.e("", "");
                        }

                    });
            return null;
        }

        public Context getContext() {
            return mContext;
        }

        public void setContext(Context context) {
            this.mContext = context;
        }
    }

    /*
     * handle the version is it updated
     * 
     * @param element must passing in the ObjectElement of "message"
     */
    private void handleVersionUpdate(Context context, DataElement element) {
        // get the json with Android version
        if (element == null || !element.isArray()) {
            return;
        }

        ArrayElement array = element.asArrayElement();
        DataElement result = null;
        for (DataElement dataElement : array) {
            DataElement e = dataElement.asObjectElement().get(
                    CHECKING_PARAMETERS_FIELD_NAME);
            if (e != null && e.isArray()) {
                for (DataElement checkParam : e.asArrayElement()) {
                    DataElement paramValue = checkParam.asObjectElement().get(
                            PARAM_VALUE_FIELD_NAME);
                    if (paramValue != null
                            && paramValue.isPrimitive()
                            && paramValue.asPrimitiveElement().valueAsString()
                                    .equals(PLATFORM_FIELD_NAME)) {
                        result = dataElement;
                        break;
                    }
                }
            }
            if (result != null) {
                break;
            }
        }

        if (result == null) {
            return;
        }

        String newMessage = result.toJson();
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref.edit().putString(MESSAGE_FIELD_NAME, newMessage).commit();
    }
    

}
