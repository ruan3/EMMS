package com.esquel.epass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author joyaether
 * 
 */
public class CustomImageViewCatalogCover extends ImageView {

    public CustomImageViewCatalogCover(Context context) {

        super(context);
    }

    public CustomImageViewCatalogCover(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightDefault = 230;
        final int wdithtDefault = 600;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (width * heightDefault) / wdithtDefault;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension((int) width, (int) height);
    }

}
