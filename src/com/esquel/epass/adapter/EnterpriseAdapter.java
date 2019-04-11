package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.item.ItemEnterprise;
import com.esquel.epass.ui.RoundedImageView;
import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class EnterpriseAdapter extends BaseAdapter {

	Activity activity;
	List<ItemEnterprise> list;

	public EnterpriseAdapter(Activity activity, List<ItemEnterprise> list) {
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
		ItemEnterprise itemEnterpriseApp = list.get(position);
		View convertView = view;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.adapter_enterprise_apps_list, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.decs = (TextView) convertView.findViewById(R.id.decs);
			holder.enterpriseName = (TextView) convertView
					.findViewById(R.id.enterprise_name);
			holder.icon = (RoundedImageView) convertView
					.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(itemEnterpriseApp.getTitle());
		holder.decs.setText(itemEnterpriseApp.getDescribe());
		holder.enterpriseName.setText(itemEnterpriseApp.getEnterpriseName());
		holder.icon.setImageResource(R.drawable.flip_animation);
		return convertView;
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ViewHolder {
		private TextView title;
		private TextView decs;
		private TextView enterpriseName;
		private RoundedImageView icon;

	}

}
