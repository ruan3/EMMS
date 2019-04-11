package com.esquel.epass.ui;

import com.esquel.epass.R;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 
 * @author joyaether
 * 
 */
public class ContextWrapperEdgeEffect extends ContextWrapper {

	private ResourcesEdgeEffect mResourcesEdgeEffect;
	private int mColor;
	private Drawable mEdgeDrawable;
	private Drawable mGlowDrawable;

	public ContextWrapperEdgeEffect(Context context) {
		this(context, 0);
	}

	public ContextWrapperEdgeEffect(Context context, int color) {
		super(context);
		mColor = color;
		Resources resources = context.getResources();
		mResourcesEdgeEffect = new ResourcesEdgeEffect(resources.getAssets(),
				resources.getDisplayMetrics(), resources.getConfiguration());
	}

	public void setEdgeEffectColor(int color) {
		mColor = color;
		if (mEdgeDrawable != null) {
			mEdgeDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		}
		if (mGlowDrawable != null) {
			mGlowDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		}
	}

	@Override
	public Resources getResources() {
		return mResourcesEdgeEffect;
	}

	/**
	 * 
	 * @author joyaether
	 * 
	 */
	private class ResourcesEdgeEffect extends Resources {
		private final int overscrollEdge = getPlatformDrawableId("overscroll_edge");
		private final int overscrollGlow = getPlatformDrawableId("overscroll_glow");

		public ResourcesEdgeEffect(AssetManager assets, DisplayMetrics metrics,
				Configuration config) {
			// super(metrics, localConfiguration);
			super(assets, metrics, config);
		}

		private int getPlatformDrawableId(String name) {
			try {
				return ((Integer) Class
						.forName("com.android.internal.R$drawable")
						.getField(name).get(null)).intValue();
			} catch (ClassNotFoundException e) {
				Log.e("[ContextWrapperEdgeEffect].getPlatformDrawableId()",
						"Cannot find internal resource class");
				return 0;
			} catch (NoSuchFieldException e1) {
				Log.e("[ContextWrapperEdgeEffect].getPlatformDrawableId()",
						"Internal resource id does not exist: " + name);
				return 0;
			} catch (IllegalArgumentException e2) {
				Log.e("[ContextWrapperEdgeEffect].getPlatformDrawableId()",
						"Cannot access internal resource id: " + name);
				return 0;
			} catch (IllegalAccessException e3) {
				Log.e("[ContextWrapperEdgeEffect].getPlatformDrawableId()",
						"Cannot access internal resource id: " + name);
			}
			return 0;
		}

		@Override
		public Drawable getDrawable(int resId)
				throws Resources.NotFoundException {
			Drawable ret = null;
			if (resId == this.overscrollEdge) {
				mEdgeDrawable = ContextWrapperEdgeEffect.this.getBaseContext()
						.getResources().getDrawable(R.drawable.overscroll_edge);
				ret = mEdgeDrawable;
			} else if (resId == this.overscrollGlow) {
				mGlowDrawable = ContextWrapperEdgeEffect.this.getBaseContext()
						.getResources().getDrawable(R.drawable.overscroll_glow);
				ret = mGlowDrawable;
			} else {
				return super.getDrawable(resId);
			}

			if (ret != null) {
				ret.setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);
			}

			return ret;
		}
	}
}
