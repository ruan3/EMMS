package com.esquel.epass.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.lib.flipview.FlipView;
import com.esquel.epass.schema.SlipData;

/**
 * 
 * @author joyaether
 * 
 */
public class SlipAdapter extends BaseAdapter {

    private final int firstTab = 0;
    private final int secondTab = 1;
    private final int thirdTab = 2;
    private final int fourthTab = 3;
    private final LayoutInflater inflater;
    private List<SlipData> list;
    private final Context mContext;
    private Runnable onGoPaySlipListener;
    private int widthScreen;
    private static final int DURATION = 1000;
    private int evenColorItem = Color.WHITE;
    private int oddColorItem = Color.parseColor("#f6f6f6");
    private int selectedColorText = Color.WHITE;
    private int normalColorText = Color.parseColor("#666666");
    private int selectedBackgroundColor = Color.parseColor("#a62740");
    private int normalBackgroundColor = Color.TRANSPARENT;
    private int[] selectedDrawable = new int[] { R.drawable.widget1_hover, R.drawable.widget2_hover, R.drawable.widget3_hover, R.drawable.widget4_hover };
    private int[] normalDrawable = new int[] { R.drawable.widget1, R.drawable.widget2, R.drawable.widget3, R.drawable.widget4 };
    private OnItemClickListener itemClick;
    int selectedDefault = 0;
    int countAnimation = 0;
    HashMap<Integer, ArrayList<String[]>> store = new HashMap<Integer, ArrayList<String[]>>();

    // private Handler handler = new Handler();
    FlipView flipView;

    public SlipAdapter(Context context, List<SlipData> list, FlipView flipView) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        this.flipView = flipView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final SlipData slipData = list.get(position);
        ViewHolder holder = null;
        View convertView = view;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_slip, null);

            holder = new ViewHolder();
            // @formatter:off
			// holder.listView = (ListView)
			// convertView.findViewById(R.id.listview);
			holder.container = (LinearLayout) convertView
					.findViewById(R.id.container);
			holder.leftTitle = (ImageButton) convertView
					.findViewById(R.id.ibtn_title_left);
			holder.centerTitle = (TextView) convertView
					.findViewById(R.id.tv_title_center);
			holder.rightTitle = (TextView) convertView
					.findViewById(R.id.tv_tile_right);
			holder.empCodeView = (TextView) convertView
					.findViewById(R.id.tv_belowtitlebar_left);
			holder.dateStringView = (TextView) convertView
					.findViewById(R.id.tv_belowtitlebar_right);
			holder.tabs[firstTab] = (LinearLayout) convertView
					.findViewById(R.id.tab1);
			holder.tabs[secondTab] = (LinearLayout) convertView
					.findViewById(R.id.tab2);
			holder.tabs[thirdTab] = (LinearLayout) convertView
					.findViewById(R.id.tab3);
			holder.tabs[fourthTab] = (LinearLayout) convertView
					.findViewById(R.id.tab4);
			holder.tv11 = (TextView) convertView.findViewById(R.id.tv11);
			holder.tv12 = (TextView) convertView.findViewById(R.id.tv12);
			holder.tv21 = (TextView) convertView.findViewById(R.id.tv21);
			holder.tv22 = (TextView) convertView.findViewById(R.id.tv22);
			holder.tv31 = (TextView) convertView.findViewById(R.id.tv31);
			holder.tv32 = (TextView) convertView.findViewById(R.id.tv32);
			holder.tv41 = (TextView) convertView.findViewById(R.id.tv41);
			holder.tv42 = (TextView) convertView.findViewById(R.id.tv42);
			// @formatter:on
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String[]> itemList = new ArrayList<String[]>();
        if (store.containsKey(position)) {
            itemList = store.get(position);
        } else {
            itemList.add(new String[] { slipData.getNetpay(mContext), "" });
            itemList.add(new String[] { slipData.getTotalWorkingHours(mContext), slipData.getTotalIncomeInfo(mContext) });
            itemList.add(new String[] { slipData.getGrossPay(mContext), slipData.getTotalDeduction(mContext) });

            itemList.add(new String[] { slipData.getFPayTypeName(mContext) + "\n" + slipData.getProductionLineCode(mContext),
                    slipData.getMonthlyRate(mContext) + mContext.getString(R.string.dollar) + "\n" + slipData.getHourlyRate(mContext) + mContext.getString(R.string.dollar) });
            store.put(position, itemList);
        }
        // holder.listView.setAdapter(new SlipInsideAdapter(context, itemList));
        holder.tv11.setText(itemList.get(0)[0]);
        holder.tv12.setText(itemList.get(0)[1]);
        holder.tv21.setText(itemList.get(1)[0]);
        holder.tv22.setText(itemList.get(1)[1]);
        holder.tv31.setText(itemList.get(2)[0]);
        holder.tv32.setText(itemList.get(2)[1]);
        holder.tv41.setText(itemList.get(3)[0]);
        holder.tv42.setText(itemList.get(3)[1]);
        // String[] row = itemList.get(position);
        int size = 11;
        holder.tv41.setTextSize(size);
        holder.tv42.setTextSize(size);
        holder.tv41.setTextColor(Color.GRAY);
        holder.tv42.setTextColor(Color.GRAY);

        holder.centerTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (getOnGoPaySlipListener() != null) {
                    getOnGoPaySlipListener().run();
                }

            }
        });
        holder.rightTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (getItemClickListener() != null) {
                    getItemClickListener().onItemClick(null, arg0, position, getItemId(position));
                }
            }
        });
        holder.leftTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Activity) mContext).onBackPressed();
            }

        });

        for (int i = 0; i < holder.tabs.length; i++) {
            holder.tabs[i].setOnClickListener(onClickListenerTab);
            holder.tabs[i].setTag(position + "|" + i);
            if (i == selectedDefault) {
                holder.tabs[i].setBackgroundColor(selectedBackgroundColor);
                ((ImageView) holder.tabs[i].getChildAt(0)).setBackgroundResource(selectedDrawable[selectedDefault]);
                ((TextView) holder.tabs[i].getChildAt(1)).setTextColor(selectedColorText);
            } else {
                holder.tabs[i].setBackgroundColor(normalBackgroundColor);
                ((ImageView) holder.tabs[i].getChildAt(0)).setBackgroundResource(normalDrawable[i]);
                ((TextView) holder.tabs[i].getChildAt(1)).setTextColor(normalColorText);
            }
        }

        holder.container.removeAllViews();
        List<String[]> userInfoList = getList(mContext, position, selectedDefault);
        int count = 0;
        String[] array;
        for (int i = 0; i < userInfoList.size(); i++) {
            array = getList(mContext, position, selectedDefault).get(i);
            if (array.length == 2 && array[selectedDefault] != null && array[1] != null) {
                LinearLayout childItem = (LinearLayout) inflater.inflate(R.layout.item_flip_profile, null);
                TextView tv1 = (TextView) childItem.findViewById(R.id.tv1);
                tv1.setText(array[selectedDefault] + ": ");
                TextView tv2 = (TextView) childItem.findViewById(R.id.tv2);
                tv2.setText(array[1]);
                if (count % 2 == 0) {
                    childItem.setBackgroundColor(evenColorItem);
                } else {
                    childItem.setBackgroundColor(oddColorItem);
                }
                holder.container.addView(childItem);
                count++;
            }
        }

        String empCode = slipData.getUserInfo().getFempCode();
        holder.empCodeView.setText(empCode);
        String dateString = slipData.getYearAndMonth(mContext);
        holder.dateStringView.setText(dateString);
        return convertView;
    }

    synchronized void increaseAnimation() {
        countAnimation++;
    }

    synchronized void reduceAnimation(int position) {
        countAnimation--;

    }

    public List<String[]> getList(Context context, int indexPage, int indexTab) {
        if (indexTab == firstTab) {
            return list.get(indexPage).getWorkingHoursInfo().getData(context);
        } else if (indexTab == secondTab) {
            return list.get(indexPage).getIncomeInfo().getData(context);
        } else if (indexTab == fourthTab) {
            return list.get(indexPage).getDeductionInfo().getData(context);
        } else if (indexTab == thirdTab) {
            return list.get(indexPage).getBenefitInfo().getData(context);
        }
        return null;
    }

    public AnimationSet createAnimationSetX(final int indexPosition, int index, final boolean refresh) {
        index = index + 1;
        final float initAlpha = 0.2f;
        float position = ((float) index) * initAlpha;
        if (position > 1) {
            position = 1.0f;
        }
        int x = (int) ((float) widthScreen * position);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);
        TranslateAnimation trans = new TranslateAnimation(x, 0, 0, 0);
        trans.setDuration(DURATION);
        AlphaAnimation alpha = new AlphaAnimation(1 - position, 1.0f);
        alpha.setDuration(DURATION);
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(trans);
        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                increaseAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reduceAnimation(indexPosition);

            }
        });
        return animationSet;
    }

    public OnItemClickListener getItemClickListener() {
        return itemClick;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClick = listener;
    }

    /**
     * @return the onPickSlipListener
     */
    public Runnable getOnGoPaySlipListener() {
        return onGoPaySlipListener;
    }

    /**
     * @param onPickSlipListener
     *            the onPickSlipListener to set
     */
    public void setOnGoPaySlipListener(Runnable listener) {
        this.onGoPaySlipListener = listener;
    }

    OnClickListener onClickListenerTab = new OnClickListener() {
        @Override
        public void onClick(View view) {

            countAnimation = 0;
            ViewGroup pView = (ViewGroup) view.getParent();
            String tag = view.getTag().toString();
            String[] line = tag.split("\\|");
            int position = Integer.parseInt(line[0]);
            int indexTab = Integer.parseInt(line[1]);
            for (int i = 0; i < pView.getChildCount(); i++) {
                LinearLayout v = (LinearLayout) pView.getChildAt(i);
                v.setBackgroundColor(normalBackgroundColor);
                ((ImageView) v.getChildAt(0)).setBackgroundResource(normalDrawable[i]);
                ((TextView) v.getChildAt(1)).setTextColor(normalColorText);
            }

            view.setBackgroundColor(selectedBackgroundColor);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                View v = viewGroup.getChildAt(0);
                if (v instanceof ImageView) {
                    ImageView imageView = (ImageView) v;
                    imageView.setBackgroundResource(selectedDrawable[indexTab]);
                }
                v = viewGroup.getChildAt(1);
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    textView.setTextColor(selectedColorText);
                }
            }
            LinearLayout container = (LinearLayout) ((View) pView.getParent()).findViewById(R.id.container);
            container.removeAllViews();

            if (list != null) {
                List<String[]> paySlipData = getList(mContext, position, indexTab);
                int count = 0;
                for (int i = 0; i < paySlipData.size(); i++) {

                    String[] array = getList(mContext, position, indexTab).get(i);
                    if (array.length == 2 && array[0] != null && array[1] != null) {
                        LinearLayout childItem = (LinearLayout) inflater.inflate(R.layout.item_flip_profile, null);
                        TextView tv1 = (TextView) childItem.findViewById(R.id.tv1);
                        tv1.setText(array[0] + ": ");
                        TextView tv2 = (TextView) childItem.findViewById(R.id.tv2);
                        tv2.setText(array[1]);
                        if (count % 2 == 0) {
                            childItem.setBackgroundColor(evenColorItem);
                        } else {
                            childItem.setBackgroundColor(oddColorItem);
                        }
                        container.addView(childItem);
                        childItem.startAnimation(createAnimationSetX(position, count, i == paySlipData.size() - 1));
                        count++;
                    }

                }
            }
            flipView.invalidate();
        }
    };

    /**
     * 
     * @author joyaether
     * 
     */
    private class ViewHolder {
        public TextView empCodeView;
        public TextView dateStringView;
        public TextView centerTitle;
        public TextView rightTitle;
        public ImageButton leftTitle;
        public LinearLayout container;
        // public ListView listView;
        public TextView tv11;
        public TextView tv12;
        public TextView tv21;
        public TextView tv22;
        public TextView tv31;
        public TextView tv32;
        public TextView tv41;
        public TextView tv42;
        final int numberOfTabs = 4;
        LinearLayout[] tabs = new LinearLayout[numberOfTabs];
    }

}
