package com.esquel.epass.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author hung
 * 
 */
public final class FontUtils {

    public static final String ROBOTO_LIGHT = "fonts/Roboto_Light.ttf";
    public static final String ROBOTO_REGULAR = "fonts/Roboto_Regular.ttf";
    public static final String ROBOTO_BOLD = "fonts/Roboto_Bold.ttf";

    public static void setFontTextView(Context context, TextView textview,
            String path) {
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(),
                path);
        textview.setTypeface(myTypeface);
    }

    public static void setFontEditText(Context context, EditText edittext,
            String path) {
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(),
                path);
        edittext.setTypeface(myTypeface);
    }
    private FontUtils(){
        
    }
    /**
     * Recursively sets a {@link Typeface} to all {@link TextView}s in a
     * {@link ViewGroup}.
     */
    public static  void setAppFont(Context context, ViewGroup mContainer,
            String fontPath) {
        Typeface mFont = Typeface
                .createFromAsset(context.getAssets(), fontPath);

        if (mContainer == null || mFont == null) {
            return;
        }

        final int mCount = mContainer.getChildCount();

        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView) {

                ((TextView) mChild).setTypeface(mFont);
            } else if (mChild instanceof EditText) {
                ((EditText) mChild).setTypeface(mFont);
            } else if (mChild instanceof ViewGroup) {

                setAppFont(context, (ViewGroup) mChild, fontPath);
            }
        }
    }
}
