package com.esquel.epass.adapter;

import java.util.List;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class UserProfileAdapter extends BaseAdapter {

	Activity activity;
	List<String> list;

	public UserProfileAdapter(Activity activity, List<String> list) {
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
		String str = list.get(position);
		View convertView = view;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.item,
					parent, false);
			holder = new ViewHolder();
			holder.titleView = (TextView) convertView.findViewById(R.id.tv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.titleView.setText(str);

		return convertView;
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ViewHolder {
		private TextView titleView;

	}

}
