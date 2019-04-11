package com.esquel.epass.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esquel.epass.R;
import com.esquel.epass.activity.ArticleViewActivity;
import com.esquel.epass.activity.ZoomActivity;
import com.esquel.epass.datastore.ScrollListener;
import com.esquel.epass.delegate.BackListener;

/**
 * 
 * @author joyaether
 * 
 */
public class EdgeEffectScrollView extends android.widget.ScrollView implements
        OnTouchListener {

    private static final int MAX_CLICK_DURATION = 1000;
    private long pressStartTime;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    float lastX;
    float lastY;
    boolean isTouch = false;
    private static final int SWIPE_THRESHOLD = 150;
    private static final int SWIPE_THRESHOLD_Y = 80;

    public EdgeEffectScrollView(Context context) {
        super(context, null);
        setOnTouchListener(this);
    }

    public EdgeEffectScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.scrollViewStyle);
        setOnTouchListener(this);
    }

    public EdgeEffectScrollView(Context context, AttributeSet attrs,
            int defStyle) {
        super(new ContextWrapperEdgeEffect(context), attrs, defStyle);
        init(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (getContext() instanceof ScrollListener && isTouch) {
            ((ScrollListener) getContext()).scroll(t, oldt, isTouch);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        int color = context.getResources().getColor(
                R.color.default_edgeeffect_color);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.EdgeEffectView, defStyle, 0);
            color = a.getColor(R.styleable.EdgeEffectView_edgeeffect_color,
                    color);
            a.recycle();
        }
        setEdgeEffectColor(color);
    }

    public void setEdgeEffectColor(int edgeEffectColor) {
        ((ContextWrapperEdgeEffect) getContext())
                .setEdgeEffectColor(edgeEffectColor);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
            pressStartTime = System.currentTimeMillis();
            isTouch = true;
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            isTouch = false;
            ((ScrollListener) getContext()).scroll(-1, -1, isTouch);
            float x = event.getRawX();
            float y = event.getRawY();
            if (x - lastX > SWIPE_THRESHOLD
                    && Math.abs(lastY - y) < SWIPE_THRESHOLD_Y
                    && getContext() instanceof BackListener) {
                ((BackListener) getContext()).onBack();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                long pressDuration = System.currentTimeMillis()
                        - pressStartTime;
                if (pressDuration < MAX_CLICK_DURATION
                        && distance(x, y, lastX, lastY) < MAX_CLICK_DISTANCE) {
                    LinearLayout ll = (LinearLayout) getChildAt(0);
                    int childCount = ll.getChildCount();
                    for (int i = 0; i < childCount; i++) {

                        if (ll.getChildAt(i) instanceof ImageView
                                || ll.getChildAt(i) instanceof CustomImageView) {
                            int[] locations = new int[2];
                            ll.getChildAt(i).getLocationOnScreen(locations);
                            int screenX = locations[0];
                            int screenY = locations[1];
                            int width = ll.getChildAt(i).getWidth();
                            int height = ll.getChildAt(i).getHeight();
                            if (x > screenX && x <= screenX + width
                                    && y > screenY && y < screenY + height
                                    && ll.getChildAt(i).getTag() != null) {
                                String src = ll.getChildAt(i).getTag()
                                        .toString();
                                Intent intent = new Intent(getContext(),
                                        ZoomActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(ZoomActivity.CURRENT_URL, src);
                                intent.putStringArrayListExtra(
                                        ZoomActivity.URL_SET,
                                        ((ArticleViewActivity) getContext())
                                                .getUrls());
                                getContext().startActivity(intent);
                                break;
                            }
                        }

                    }
                }

            }

        }
        return super.onTouchEvent(event);
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);

    }
}
