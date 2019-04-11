package com.esquel.epass.activity;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.restlet.data.Reference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.esquel.epass.DownloadTask;
import com.esquel.epass.R;
import com.esquel.epass.delegate.DownloadCallback;
import cx.hell.android.pdfview.OpenFileActivity;

public class ViewPdfActivity extends BaseGestureActivity {
	public static final String EXTRA_PDF_URL = "pdf-url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        download();
    }

    private void initUi(File file) {
    	if (!file.exists()) {
    		Toast.makeText(ViewPdfActivity.this, R.string.fail_to_get_task, Toast.LENGTH_SHORT).show();
    		return;
    	}
		Intent intent = new Intent();
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setClass(this, OpenFileActivity.class);
		this.startActivity(intent);       
		finish();

    }
    
    private void download() {
    	String urlString = getIntent().getStringExtra(EXTRA_PDF_URL);
    	if (urlString == null) {
    		return;
    	}
    	Reference source = new Reference(urlString);
		String lastSegment = source.getLastSegment();
		final File file = new File(getExternalFilesDir(null),
				lastSegment);
//		if (file.exists()) {
//			initUi(file);
//			return;
//		}
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Reference destination = new Reference(file.getAbsolutePath());
    	final DownloadTask task = new DownloadTask(this, source, destination);
    	task.setCallback(new DownloadCallback() {

			@Override
			public void success(boolean hasUpdate) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initUi(file);
						dismissLoadingDialog();
					}
					
				});
			}

			@Override
			public void fail(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
						Toast.makeText(ViewPdfActivity.this, R.string.fail_to_get_task, Toast.LENGTH_SHORT).show();
						onBackPressed();
					}
					
				});
			}
    		
    	});
    	showLoadingDialog();
    	executor.submit(new Runnable() {

			@Override
			public void run() {
				task.download();
			}
			
		});
    	
    }
}
