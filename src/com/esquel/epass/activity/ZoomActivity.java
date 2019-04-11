package com.esquel.epass.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.ui.PinchImageView;
import com.esquel.epass.utils.FontUtils;

/**
 * 
 * @author hung
 * 
 */
public class ZoomActivity extends BaseGestureActivity implements
        OnClickListener {

    public static final String URL_SET = "url";
    public static final String CURRENT_URL = "current";
    PinchImageView pinchImageView;
    ProgressBar progressBar;
    ImageView close;
    ArrayList<String> urls = new ArrayList<String>();
    int position;
    TextView tv;
    ImageButton next;
    ImageButton back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        next = (ImageButton) findViewById(R.id.btn_next);
        back = (ImageButton) findViewById(R.id.btn_back);
        tv = (TextView) findViewById(R.id.tv);
        FontUtils.setFontTextView(this, tv, FontUtils.ROBOTO_LIGHT);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        urls = getIntent().getStringArrayListExtra(URL_SET);
        String currentUrl = getIntent().getStringExtra(CURRENT_URL);
        position = urls.indexOf(currentUrl);
        if (urls == null || currentUrl == null) {
            finish();
        }
        pinchImageView = (PinchImageView) findViewById(R.id.imagePinch);
        pinchImageView.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
       
        display(currentUrl);
        displayPosition(position);
        validatePosition();
    }

    private void validatePosition() {
        if (position == 0) {
            back.setEnabled(false);
        } else {
            back.setEnabled(true);
        }
        if (position == urls.size() - 1) {
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }
    }

    private void displayPosition(int p) {
        tv.setText((p + 1) + "/" + urls.size());
    }

    private void display(String url) {
    	progressBar.setVisibility(View.GONE);
    	pinchImageView.setVisibility(View.VISIBLE);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        getImageDownloader().download(url, R.drawable.icon_loading_image,
        		screenWidth, screenHeight, pinchImageView);
    }

    @Override
    public void onClick(View v) {
        if (v == next) {
            position = position + 1;
        } else if (v == back) {
            position = position - 1;
        }
        display(urls.get(position));
        displayPosition(position);
        validatePosition();
    }
}
