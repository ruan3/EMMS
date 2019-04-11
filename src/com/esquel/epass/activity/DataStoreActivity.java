package com.esquel.epass.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.esquel.epass.R;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.LogUtils;
import com.joyaether.datastore.rest.RestStore;
import com.joyaether.datastore.sqlite.SqliteStore;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * @author joyaether
 * 
 */
public abstract class DataStoreActivity extends BaseActivity {

	public static final String KEY_PARENT_ID = "parent-id";
	public static final String KEY_TITLE = "title";
    
    protected SqliteStore getSqliteStore() {
		return ((AppApplication) getApplication()).getSqliteStore();
	}

	protected RestStore getRestStore() {
		return ((AppApplication) getApplication()).getRestStore();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		getImageDownloader().cancelDownloads();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getImageDownloader().cancelDownloads();
	}

	protected String getParentStringId() {
		Intent intent = getIntent();
		if (intent == null) {
			return null;
		}
		return intent.getStringExtra(KEY_PARENT_ID);
	}

	protected long getParentLongId() {
		Intent intent = getIntent();
		if (intent == null) {
			return 0;
		}
		return intent.getLongExtra(KEY_PARENT_ID, 0);
	}

	protected String getHeaderTitle() {
		Intent intent = getIntent();
		if (intent == null) {
			return "";
		}

		String title = intent.getStringExtra(KEY_TITLE);
		if (title == null) {
			return "";
		}
		return title;
	}
	
	public ImageDownloader getImageDownloader() {
		return ((AppApplication) getApplication()).getImageDownloader();
	}
	
	protected void startDownloader(String url, ImageView imageView) {
		LogUtils.e("开始下载图片路径--->"+url);
		if (url == null || url.length() == 0) {
			imageView.setVisibility(View.GONE);
			return;
		}
		
    	ImageDownloader downloader = getImageDownloader();
    	if (downloader != null) {
    		downloader.download(url, R.drawable.icon_loading_image,
    				Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, imageView);
    	}
    }

}
