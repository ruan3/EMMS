package com.esquel.epass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author joyaether
 * 
 */
public class CustomImageView extends ImageView {

	public CustomImageView(Context context) {

		super(context);
	}

	public CustomImageView(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int heightDefault = 330;
		final int wdithtDefault = 600;
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (width * heightDefault) / wdithtDefault;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension((int) width, (int) height);
	}

}
