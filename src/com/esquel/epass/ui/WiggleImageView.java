package com.esquel.epass.ui;

import com.esquel.epass.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * A {@link ImageView} with wiggle animation.
 * 
 * @author joyaether
 * 
 */
public class WiggleImageView extends ImageView {

	private Animation wiggleAnimation;

	public WiggleImageView(Context context) {
		this(context, null);
	}

	public WiggleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WiggleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Start to wiggle.
	 */
	public void start() {
		if (wiggleAnimation == null) {
			wiggleAnimation = AnimationUtils.loadAnimation(getContext(),
					R.anim.wiggle);
			wiggleAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// Do nothing
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					animation.reset();
					animation.start();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// Do nothing
				}

			});
			wiggleAnimation.setFillAfter(true);
		}
		setAnimation(wiggleAnimation);
		wiggleAnimation.start();

	}

	/**
	 * Stop the animation.
	 */
	public void stop() {
		setAnimation(null);
	}

}
