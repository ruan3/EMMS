package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.item.ItemChat;
import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class ChatAdapter extends BaseAdapter {

	Activity activity;
	List<ItemChat> list;

	public ChatAdapter(Activity activity, List<ItemChat> list) {
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
		ItemChat itemChat = list.get(position);
		View convertView = view;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_chat, parent, false);
			holder = new ViewHolder();
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.from = (TextView) convertView.findViewById(R.id.from);
			holder.order = (TextView) convertView.findViewById(R.id.order);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.content.setText(itemChat.getContent());
		holder.from.setText(itemChat.getFrom());
		holder.order.setText((position + 1) + "");
		return convertView;
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ViewHolder {
		private TextView content;
		private TextView from;
		private TextView order;
	}

}
