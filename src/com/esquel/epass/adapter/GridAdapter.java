package com.esquel.epass.adapter;

import com.esquel.epass.R;
import com.esquel.epass.ui.WiggleImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author joyaether
 * 
 */
public class GridAdapter extends BaseAdapter {

	private boolean inEditMode = false;
	private Context mContext;

	public GridAdapter(Context context) {
		this.setContext(context);
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

	@Override
	public int getCount() {
		final int count = 10;
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(getContext(), R.layout.grid_image_item, null);
		}

		WiggleImageView imageView = (WiggleImageView) view
				.findViewById(R.id.image);
		imageView.setImageResource(R.drawable.esquel_pass_icon);
		if (isInEditMode()) {
			imageView.start();
		} else {
			imageView.stop();
		}

		return view;
	}

	/**
	 * @return the mContext
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * @param mContext
	 *            the mContext to set
	 */
	public void setContext(Context context) {
		this.mContext = context;
	}

}
