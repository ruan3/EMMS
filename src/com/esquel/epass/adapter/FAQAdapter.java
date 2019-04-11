package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.item.ItemFAQ;
import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class FAQAdapter extends BaseAdapter {

	Activity activity;
	List<ItemFAQ> list;

	public FAQAdapter(Activity activity, List<ItemFAQ> list) {
		this.activity = activity;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		ItemFAQ itemFaq = list.get(position);
		View convertView = view;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_faq, parent, false);
			holder = new ViewHolder();
			holder.question = (TextView) convertView
					.findViewById(R.id.question);
			holder.answer = (TextView) convertView.findViewById(R.id.answer);
			holder.order = (TextView) convertView.findViewById(R.id.order);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.question.setText(itemFaq.getQuestion());
		holder.answer.setText(itemFaq.getAnswer());
		holder.order.setText((position + 1) + "");
		return convertView;
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ViewHolder {
		private TextView question;
		private TextView answer;
		private TextView order;

	}

}
