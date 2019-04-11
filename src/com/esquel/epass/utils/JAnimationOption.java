package com.esquel.epass.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * 
 * @author joyaether
 * 
 */
public final class JAnimationOption {

	private static final int COLLAPSE_DURATION = 500;
	private static final int EXPAND_DURATION = 500;

	private JAnimationOption() {

	}

	public static void setMarginTop(View view, int top) {
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view
				.getLayoutParams();
		params.setMargins(0, (int) -top, 0, 0);
		view.setLayoutParams(params);
	}

	public static void jExpandAlert(final Context context, final View view,
			final int height) {
		view.getLayoutParams().height = 0;
		view.requestLayout();
		view.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				int a = interpolatedTime == 1 ? height
						: (int) ((interpolatedTime) * height);

				view.getLayoutParams().height = a;
				view.requestLayout();

			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}

			@Override
			protected void finalize() {
				// do nothing
			}
		};
		a.setDuration(EXPAND_DURATION);
		view.startAnimation(a);
	}

	public static void jExpandAlert(final Context context, final View view,
			final int fromHeight, final int toHeight) {
		view.getLayoutParams().height = fromHeight;
		view.requestLayout();
		view.setVisibility(View.VISIBLE);
		final int target = toHeight - fromHeight;
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				int a = interpolatedTime == 1 ? target
						: (int) ((interpolatedTime) * target);

				view.getLayoutParams().height = a + fromHeight;
				view.requestLayout();

			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}

			@Override
			protected void finalize() {
				// do nothing
			}
		};
		a.setDuration(EXPAND_DURATION);
		view.startAnimation(a);
	}

	public static void jCollapseAlert(final Context context, final View view,
			final int fromHeight, final int toHeight) {
		view.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				int target = toHeight - fromHeight;

				int a = interpolatedTime == 1 ? target
						: (int) ((interpolatedTime) * target);
				view.getLayoutParams().height = fromHeight + a;
				view.requestLayout();

			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}

			@Override
			protected void finalize() {
				// Do nothing
			}
		};
		a.setDuration(COLLAPSE_DURATION);
		view.startAnimation(a);
	}

	public static void jCollapseAlert(final Context context, final View view,
			final int height) {

		view.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				int a = interpolatedTime == 1 ? 0
						: (int) ((1 - interpolatedTime) * height);
				view.getLayoutParams().height = a;
				view.requestLayout();
				if (interpolatedTime == 1) {
					view.setVisibility(View.GONE);
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}

			@Override
			protected void finalize() {
				// Do nothing
			}
		};
		a.setDuration(COLLAPSE_DURATION);
		view.startAnimation(a);
	}
}
