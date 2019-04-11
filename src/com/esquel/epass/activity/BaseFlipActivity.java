package com.esquel.epass.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.FlipAdapter;
import com.esquel.epass.delegate.BackListener;
import com.esquel.epass.lib.flipview.FlipView;
import com.esquel.epass.lib.flipview.OverFlipMode;
import com.esquel.epass.lib.flipview.RubberBandOverFlipper;
import com.joyaether.datastore.widget.DataAdapter;

/**
 * 
 * @author joyaether
 * 
 */
public abstract class BaseFlipActivity extends DataStoreActivity implements BackListener, FlipView.OnFlipListener, FlipView.OnOverFlipListener {

    // protected FlipViewController flipViewController;
    protected FlipAdapter flipAdapter;
    private static final int MIN_LEVEL = (int) (RubberBandOverFlipper.MAX_OVER_FLIP_DISTANCE - 20);
    private static final int MAX_LEVEL = (int) (RubberBandOverFlipper.MAX_OVER_FLIP_DISTANCE - 15);
    boolean minFlag = false;
    boolean maxFlag = false;
    FlipView flipView;
    private static final String FONT_FILE = "fonts/font.otf";

    // FlipView flipViewController;
    int currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(-1, R.anim.trans_left_out);
        setContentView(R.layout.activity_channel_list);
        flipView = (FlipView) findViewById(R.id.flip_view);

        flipView.peakNext(false);
        flipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        flipView.setEmptyView(findViewById(R.id.empty_view));
        setView();
    }

    private void setView() {
        if (isRefreshable()) {
            TextView textView = (TextView) findViewById(R.id.textView);
            Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_FILE);
            textView.setTypeface(typeface);
        }

        flipView.setOnFlipListener(this);
        flipView.setOnOverFlipListener(this);
    }

    /**
     * 
     * @return
     */
    protected abstract DataAdapter getAdapter();

    public void animationStart() {
        TextView textView = (TextView) findViewById(R.id.textView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (minFlag && maxFlag) {
            if (textView != null) {
                String flipDownText = getResources().getString(R.string.flip_down);
                textView.setText(flipDownText);
            }
            if (imageView != null) {
                Animation rotation1 = AnimationUtils.loadAnimation(this, R.anim.rotate_right_left);

                imageView.startAnimation(rotation1);
            }

        } else {
            if (imageView != null) {
                Animation rotation2 = AnimationUtils.loadAnimation(this, R.anim.rotate_left_right);
                imageView.startAnimation(rotation2);
            }

        }
    }

    // @Override
    // public void releaseFinger(final float angle) {
    // animationStart(angle);
    // }

    /**
     * Synchronize data.
     */
    public abstract void syncData();

    // @Override
    // public void refreshViewGone(float angle) {
    // final RelativeLayout refreshView = (RelativeLayout)
    // findViewById(R.id.refresh_layout);
    // ImageView imageView = (ImageView) findViewById(R.id.imageView);
    // if (state == FlipState.FLIP_DOWN
    // && refreshView.getVisibility() == View.VISIBLE) {
    //
    // if (isRefreshable()) {
    // // Toast.makeText(this, "Updating...",
    // // Toast.LENGTH_SHORT).show();
    // syncData();
    //
    // }
    // Animation rotation1 = AnimationUtils.loadAnimation(this,
    // R.anim.rotate_right_left);
    // imageView.startAnimation(rotation1);
    // }
    // int delayOption = DEFAULT_DELAY_OPTION;
    // if (angle < ACCUMULATED_ANGLE_1) {
    // delayOption = DELAY_OPTION_1;
    // } else if (angle < ACCUMULATED_ANGLE_2) {
    // delayOption = DELAY_OPTION_2;
    // } else if (angle < ACCUMULATED_ANGLE_3) {
    // delayOption = DELAY_OPTION_3;
    // } else if (angle < ACCMULATED_ANGLE_4) {
    // delayOption = DELAY_OPTION_4;
    // } else if (angle < ACCMULATED_ANGLE_5) {
    // delayOption = DELAY_OPTION_5;
    // }
    // new Handler().postDelayed(new Runnable() {
    // @Override
    // public void run() {
    // refreshView.setVisibility(View.INVISIBLE);
    // }
    // }, delayOption);
    // }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(-1, R.anim.trans_right_out);
        if (ChannelListFlipActivity.class.getCanonicalName().equals(getClass().getCanonicalName())) {
            overridePendingTransition(-1, -1);
            return;
        }
        super.onBackPressed();
        overridePendingTransition(-1, R.anim.trans_right_out);
    }

    // @Override
    // public void backScreen() {
    // if (!ChannelListFlipActivity.class.isAssignableFrom(getClass())) {
    // onBackPressed();
    // }
    // }

    protected boolean isRefreshable() {
        return true;
    }

    // @Override
    // public void onResume() {
    // super.onResume();
    // FlipViewController flipView = getFlipView();
    // if (flipView != null) {
    // flipView.onResume();
    // }
    // }
    //
    // @Override
    // public void onPause() {
    // super.onPause();
    // FlipViewController flipView = getFlipView();
    // if (flipView != null) {
    // flipView.onPause();
    // }
    // }
    //
    // public void updateFlipView() {
    // FlipViewController flipView = getFlipView();
    // if (flipView != null) {
    // Adapter adapter = flipView.getAdapter();
    // if (adapter instanceof DataAdapter) {
    // ((DataAdapter) adapter).getCache().clear();
    // ((DataAdapter) adapter).notifyDataSetChanged();
    // }
    // }
    // }
    //
    // /**
    // * @param onViewFlipListener
    // * the onViewFlipListener to set
    // */
    // public void setOnViewFlipListener(
    // FlipViewController.ViewFlipListener onViewFlipListener) {
    // FlipViewController flipView = getFlipView();
    // if (flipView != null) {
    // ((FlipViewController) flipView)
    // .setOnViewFlipListener(onViewFlipListener);
    // }
    // }
    //
    // FlipViewController getFlipView() {
    // // get the root view
    // View view = findViewById(android.R.id.content).getRootView();
    // View flipView = view.findViewWithTag(FLIP_VIEW_TAG);
    // if (flipView instanceof FlipViewController) {
    // return (FlipViewController) flipView;
    // }
    // return null;
    // }

    @Override
    public void onBack() {
        onBackPressed();

    }

    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {
        TextView textView = (TextView) findViewById(R.id.page_number);
        textView.setText((position + 1) + "/" + flipAdapter.getCount());
        currentPosition = position;
    }

    @Override
    public void onOverFlip(FlipView v, OverFlipMode mode, boolean overFlippingPrevious, float overFlipDistance, float flipDistancePerPage) {

        if (overFlipDistance > MAX_LEVEL && !maxFlag) {
            minFlag = true;
            maxFlag = true;
            animationStart();
        }
        if (overFlipDistance < MIN_LEVEL && minFlag) {
            minFlag = false;
            maxFlag = false;

            animationStart();
        }
    }

    @Override
    public void release(int indexPage) {
        if (minFlag && maxFlag && indexPage == 0) {
            syncData();
        }
        minFlag = false;
        maxFlag = false;

    }
}
