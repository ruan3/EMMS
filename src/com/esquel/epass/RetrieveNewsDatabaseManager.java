package com.esquel.epass;

import java.io.EOFException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.CacheDirective;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.esquel.epass.delegate.DownloadCallback;
import com.esquel.epass.utils.EsquelPassRegion;
import com.esquel.epass.utils.LogUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.ZipManager;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Datastore;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.rest.RestStore;
import com.joyaether.datastore.rest.RestStoreClient;

/**
 * A Utility class to handle download article database.
 * 
 * @author joyaether
 * 
 */
public final class RetrieveNewsDatabaseManager {

	private static final String REPEAT_COUNT = "repeatCount";
	private static final String API_URL = com.esquel.epass.utils.BuildConfig.getContentServerEndPoint();
	private static final String SCHEMA = "content/json/";
	private static final String API_FORMAT = ".json";
	private static final String LAST_MODIFIED_DATE_FIELD_NAME = "lastmoddate";
	private static final String DB_URL_FIELD_NAME = "dburl";
	public static final String DOWNLOAD_DB_PATH = "download";
	// private static final String DESTINATION_NEWS_DATABASE_PATH = "";

	private static RetrieveNewsDatabaseManager manager;
	private Context mContext;
	private DownloadCallback mCallback;

	private boolean isTaskRunning = false;
	public static final String INTENT_ACTION_DOWNLOAD_TASK_RUNNING = "com.esquel.epass.ACTION.download.task.running";
	public static final String INTENT_FILTER_DOWNLOAD_TASK_RUNNING = "com.esquel.epass.FILTER.download.task.running";

	public static synchronized RetrieveNewsDatabaseManager getInstance(
			Context context) {
		if (manager == null) {
			manager = new RetrieveNewsDatabaseManager(context);
		}
		return manager;
	}

	private RetrieveNewsDatabaseManager(Context context) {
		setContext(context);
	}

	/**
	 * Adds a directive for caching mechanism.
	 * 
	 * @param response
	 *            The response to add the caching mechanism directive
	 * @param cacheDirective
	 *            The caching mechanism directive to add to the response
	 */
	protected void addCacheDirective(Request request,
			CacheDirective cacheDirective) {
		List<CacheDirective> cacheDirectives = request.getCacheDirectives();
		if (cacheDirectives == null) {
			cacheDirectives = new ArrayList<CacheDirective>();
		}
		request.setCacheDirectives(cacheDirectives);
	}

	/**
	 * 
	 */
	public void startDownload() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
		String apiEndPoint = pref.getString(
				ConfigurationManager.CONTENT_RESOURCE_END_POINT_FIELDNAME, null);
		if (apiEndPoint == null) {
			apiEndPoint = API_URL;
		}
		
		RestStore store = Datastore.getInstance().getRestStore(
				new RestStoreClient(getContext(), apiEndPoint + SCHEMA, null));
		LogUtils.e("请求数据库路径--->"+apiEndPoint + SCHEMA);
		LogUtils.e("别名--->"+EsquelPassRegion.getDefault(getContext()).toString()
						+ API_FORMAT);
		setTaskRunning(true);
		store.performAction(Method.GET, null,
				EsquelPassRegion.getDefault(getContext()).toString()
						+ API_FORMAT, null, null, new StoreCallback() {

					@Override
					public void success(DataElement element, String resource) {
						LogUtils.e("请求数据库路径element--->"+element.toJson() + "resource---->" +resource);
						if (element == null || element.isNull()) {
							getCallback()
							.fail(new Exception(
									"return element is null"));
							return;
						}
						byte[] jsonFileBuffer = element.valueAsByteArray();
						if (jsonFileBuffer == null) {
							getCallback()
							.fail(new Exception(
									"return element is null"));
							return;
						}
						String json = new String(jsonFileBuffer);
						LogUtils.e("json--->"+json);
						JsonObjectElement object = new JsonObjectElement(json);
						if (object != null && object.isObject()) {
							
							DataElement e = object.asObjectElement().get(
									LAST_MODIFIED_DATE_FIELD_NAME);
							long latestDate = 0L;
							if (e != null && e.isPrimitive()) {
								latestDate = e.asPrimitiveElement()
										.valueAsLong();
							}

							long date = latestDate;

							e = object.asObjectElement()
									.get(DB_URL_FIELD_NAME);
							String dbUrl = null;
							if (e != null && e.isPrimitive()) {
								dbUrl = e.asPrimitiveElement().valueAsString();
							}
							

							final String url = dbUrl;
							long lastUpdateDate = SharedPreferenceManager
									.getLastSyncDataDate(getContext());
							if (lastUpdateDate < date) {
								download(url, date, EsquelPassRegion.getDefault(getContext()));
							} else {
						        String dbName = EsquelPassRegion.getDefault(getContext()).toString() + ".db";
								File downloadDB = new File(
										getContext().getExternalFilesDir(null).getParent() + "/"
						                + RetrieveNewsDatabaseManager.DOWNLOAD_DB_PATH,
						                dbName);
								File currentDb = new File(
										getContext().getExternalFilesDir(null),
										dbName);
								if (!currentDb.exists()) {
									download(url, date, EsquelPassRegion.getDefault(getContext()));
								} else {
									setTaskRunning(false);
									getCallback().success(downloadDB.exists());
								}
								
							}

						} else {
							if (getCallback() != null) {
								getCallback()
										.fail(new Exception(
												"return element is null"));
								return;
							}
						}
					}

					@Override
					public void failure(DatastoreException ex, String resource) {
						LogUtils.e("请求数据库路径失败ex--->"+ex.toString() + "resource---->" +resource);
						setTaskRunning(false);
						if (getCallback() != null) {
							getCallback().fail(ex);
						}
					}

				});
	}

	/**
	 * Retrieve the news database.
	 * 
	 * @param url
	 */
	private void download(
			String url, 
			final long date,
			final EsquelPassRegion region) {
		LogUtils.e("开始下载数据库-->"+url);
		Reference source = new Reference(url);
		String lastSegment = source.getLastSegment();
		final File file = new File(
				getContext().getExternalFilesDir(null),
				lastSegment);
		// File file = new File(getContext().getFilesDir(), lastSegment);
		DownloadTask task = new DownloadTask(
				getContext(), 
				new Reference(url),
				new Reference(file.getAbsolutePath()));
		
		task.setCallback(new DownloadCallback() {

			@Override
			public void success(boolean hasUpdate) {
				// unzip file
				// String latestDBFolder = file.getParent();
				// File latestDBPath =
				LogUtils.e("下载数据库成功-->"+hasUpdate);
				File dbFile = new File(file.getParentFile(), region.toString()
						+ ".db");
				String path = file.getParent();
				boolean isAlreadyExist = false;
				if (dbFile.exists()) {
					path = file.getParentFile().getParent() + "/" + DOWNLOAD_DB_PATH + "/";
					isAlreadyExist = true;
				}
				boolean success = ZipManager.unzip(
						getContext(), 
						file.getAbsolutePath(), 
						path,
						EsquelPassRegion.getDefault(getContext()));
				setTaskRunning(false);
				if (!success) {
					if (getCallback() != null) {
						getCallback().fail(new DatastoreException("The content database was corrupted..."));
					}
				} else {					
					SharedPreferenceManager.setLastSyncDataDate(getContext(), date);
					if (getCallback() != null) {
						getCallback().success(isAlreadyExist);						
					}
				}
			}

			@Override
			public void fail(Exception e) {
				LogUtils.e("下载数据库成功e-->"+e.toString());
				setTaskRunning(false);
				if (getCallback() != null) {
					getCallback().fail(e);
				}
			}

		});
		task.download();
	}

	/**
	 * @return the mContext
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * @param mContext
	 *            the mContext to set
	 */
	public void setContext(Context context) {
		this.mContext = context;
	}

	/**
	 * @return the callback
	 */
	public DownloadCallback getCallback() {
		return mCallback;
	}

	/**
	 * @param callback
	 *            the callback to set
	 */
	public void setCallback(DownloadCallback callback) {
		this.mCallback = callback;
	}
	
	public boolean isTaskRunning() {
		return isTaskRunning;
	}

	public void setTaskRunning(boolean isTaskRunning) {
		this.isTaskRunning = isTaskRunning;
		if (!isTaskRunning) {
			Intent intent = new Intent();
			intent.setAction(INTENT_ACTION_DOWNLOAD_TASK_RUNNING);
			getContext().sendBroadcast(intent);
		}
	}
}
