package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.item.ItemBookMark;
import com.esquel.epass.ui.WiggleImageView;

/**
 * 
 * @author joyaether
 * 
 */
public class EditBookMarkAdapter extends BaseAdapter {

	private final Context mContext;
	Activity activity;
	List<ItemBookMark> list;
	private boolean inEditMode = false;

	public EditBookMarkAdapter(Context c, List<ItemBookMark> list) {
		mContext = c;
		activity = (Activity) c;
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
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ItemBookMark itemBookMark = list.get(position);
		View grid;

		if (convertView == null) {
			grid = new View(mContext);
			LayoutInflater inflater = activity.getLayoutInflater();
			grid = inflater.inflate(R.layout.item_gridview_edit_bookmark,
					parent, false);
		} else {
			grid = (View) convertView;
		}
		final ImageView delete = (ImageView) grid.findViewById(R.id.delete);
		WiggleImageView img = (WiggleImageView) grid.findViewById(R.id.img);
		TextView title = (TextView) grid.findViewById(R.id.title);
		img.setImageResource(itemBookMark.getImg());
		String text = itemBookMark.getText();
		title.setText(text);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				delete.setVisibility(View.GONE);
				list.remove(position);
				notifyDataSetChanged();
			}
		});

		if (isInEditMode()) {
			img.start();
			delete.setVisibility(View.VISIBLE);
		} else {
			img.stop();
			delete.setVisibility(View.GONE);
		}

		return grid;

	}

	/**
	 * @return the inEditMode
	 */
	public boolean isInEditMode() {
		return inEditMode;
	}

	/**
	 * @param inEditMode
	 *            the inEditMode to set
	 */
	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
	}

	public void toggleEditMode() {
		setInEditMode(isInEditMode() ? false : true);
		notifyDataSetChanged();
	}

}
