package com.esquel.epass.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.esquel.epass.R;

/**
 * 
 * @author zshop
 * 
 */
public class CustomRelativeLayout extends RelativeLayout {

    static int h = -1;

    public CustomRelativeLayout(Context context) {
        super(context);

    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Call super() so that resolveUri() is called.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (h == -1) {
            View content = ((Activity)getContext()).findViewById(Window.ID_ANDROID_CONTENT);
            h = content.getHeight() - getContext().getResources().getDimensionPixelSize(R.dimen.four_dp)
                    - getContext().getResources().getDimensionPixelSize(R.dimen.height_include_view_bottom_article);
            h = h / 5;
        }
        setMeasuredDimension(widthMeasureSpec,h);
    }
}
