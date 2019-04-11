package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.ChatAdapter;
import com.esquel.epass.adapter.FAQAdapter;
import com.esquel.epass.item.ItemChat;
import com.esquel.epass.item.ItemFAQ;

/**
 * @author zshop
 * 
 */
public class FAQActivity extends BaseActivity {

	String selectedtag = "faq";
	View selectedView;
	ListView faqListview;
	ListView chatListView;
	private static final int MAX_SIZE = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq_chat);
		selectedView = findViewById(R.id.tab1);
		findViewById(R.id.left_second).setVisibility(View.INVISIBLE);
		findViewById(R.id.right_second).setVisibility(View.INVISIBLE);
		// findViewById(R.id.right_first).setVisibility(View.INVISIBLE);
		ImageButton ib = ((ImageButton) findViewById(R.id.right_first));

		ib.setImageResource(R.drawable.icon_comment_white);

		faqListview = (ListView) findViewById(R.id.listview_faq);

		List<ItemFAQ> list = new ArrayList<ItemFAQ>();
		for (int i = 0; i < MAX_SIZE; i++) {
			ItemFAQ item = new ItemFAQ();
			item.setQuestion("Your night out on town is doing what?");
			item.setAnswer("Party all night not worry about tomorrow");
			list.add(item);
		}
		FAQAdapter adapter = new FAQAdapter(this, list);
		faqListview.setAdapter(adapter);

		chatListView = (ListView) findViewById(R.id.listview_chat);

		List<ItemChat> list2 = new ArrayList<ItemChat>();
		for (int i = 0; i < MAX_SIZE; i++) {
			ItemChat item = new ItemChat();
			item.setContent("Your night out on town is doing what?");
			item.setFrom("Party");
			list2.add(item);
		}
		ChatAdapter adapter2 = new ChatAdapter(this, list2);
		chatListView.setAdapter(adapter2);

		faqListview.setOnItemClickListener(onItemClickListener);
		chatListView.setOnItemClickListener(onItemClickListener);

	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(FAQActivity.this,
					PrivateConversationActivity.class);
			startActivity(intent);

		}
	};

	public void selectTab(View view) {
		String tag = view.getTag().toString();
		if (tag.equals(selectedtag)) {
			return;
		}
		selectedView.setBackgroundColor(Color.WHITE);
		TextView tv = ((TextView) selectedView);
		tv.setTextColor(Color.parseColor("#a62740"));
		selectedtag = tag;
		if ("faq".equals(selectedtag)) {
			faqListview.setVisibility(View.VISIBLE);
			chatListView.setVisibility(View.GONE);
		} else {
			faqListview.setVisibility(View.GONE);
			chatListView.setVisibility(View.VISIBLE);
		}

		view.setBackgroundColor(Color.parseColor("#a62740"));
		if (view instanceof TextView) {
			TextView tv2 = ((TextView) view);
			tv2.setTextColor(Color.WHITE);
		}
		selectedView = view;
	}
}
