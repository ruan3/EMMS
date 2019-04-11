package com.esquel.epass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.esquel.epass.R;

/**
 * @author zshop
 * 
 */
public class NewAppArticle extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_app_article_list);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		ImageButton ib = ((ImageButton) findViewById(R.id.right_first));
		TextView title = (TextView) findViewById(R.id.title_menu);
		title.setText("App News");
		ib.setImageResource(R.drawable.icon_menu_enterprise);
		WebView webView = (WebView) findViewById(R.id.info);
		webView.loadUrl("https://www.google.com.vn/");
		findViewById(R.id.right_first).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(NewAppArticle.this,
								EnterpriseActivity.class);
						startActivity(intent);

					}
				});
	}
}
