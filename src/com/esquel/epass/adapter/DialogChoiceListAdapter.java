package com.esquel.epass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.R;

/**
 * A adapter to show the dialog choice.
 * @author TorresLam
 *
 */
public class DialogChoiceListAdapter extends BaseAdapter {

	private final Context context;
	private final List<String> listItem;

	public DialogChoiceListAdapter(Context context, List<String> list) {
		this.context = context;
		this.listItem = list;
	}

	public DialogChoiceListAdapter(Context context, String... list) {
		this.context = context;
		listItem = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			listItem.add(list[i]);
		}
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * A class to hold the view reference.
	 */
	public class ViewHolder {
		TextView text;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(
					R.layout.item_dialog_choices, null);
			holder = new ViewHolder();
			holder.text = (TextView) view
					.findViewById(R.id.txtItemSortbyOrder);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.text.setText(listItem.get(position));
		return view;
	}
}
