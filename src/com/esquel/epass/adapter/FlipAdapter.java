package com.esquel.epass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.HomeActivity;
import com.esquel.epass.activity.UserInfoActivity;
import com.esquel.epass.delegate.SelectChannel;
import com.esquel.epass.item.DisplayItem;
import com.esquel.epass.lib.flipview.Log;
import com.esquel.epass.schema.UserInfo;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.FontUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * @author joyaether
 * 
 */
public class FlipAdapter extends BaseAdapter {

    private final LayoutInflater inflater;

    private final Context context;

    private static final int NUMBER_CHANNEL_PER_LAYOUT_ABOVE = 5;
    private static final int NUMBER_CHANNEL_PER_LAYOUT_BELOW = 6;
    private SelectChannel selectChannel = null;
    // private FlipViewController controller;
    private List<List<DisplayItem>> mDisplayList;
    private List<ImageView> menuView = new ArrayList<ImageView>();
    private boolean hasAnimation = false;
    private Animation cycleAnimation;
    static final long MAX_LONG = Long.MAX_VALUE;
    private DataElement userInfoElement;
    private ImageDownloader imageDownloader;
    private OnClickAddItemListener onClickAddItemListener;
    
    public interface OnClickAddItemListener {
    	void onClickItem();
    }
    
    /*
     * The last item must be the cell for add item.
     */
    private static final int ITEM_ADDING = 1;

    public FlipAdapter(Context context, List<DisplayItem> displayList) {
        this.context = context;
        addLastItem(displayList);
        this.mDisplayList = getList(displayList);
        inflater = LayoutInflater.from(context);
        if (context instanceof SelectChannel) {
            selectChannel = (SelectChannel) context;
        }

        BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent);
        cycleAnimation = AnimationUtils.loadAnimation(context, R.anim.cycle);
    }
    
    private void addLastItem(List<DisplayItem> displayList) {
    	DisplayItem item = new DisplayItem();
    	item.setChannelName(context.getString(R.string.add));
    	displayList.add(item);
    }

    @Override
    public int getCount() {

        return mDisplayList.size();

    }

    public List<DisplayItem> getList(int position) {
        return mDisplayList.get(position);
    }

    public List<List<DisplayItem>> getList(List<DisplayItem> list) {
        /**
         * remove Slip Item
         */

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getChannelId() == MAX_LONG) {
                list.remove(i);
                break;
            }
        }
        DisplayItem item = new DisplayItem();
        item.setChannelId(MAX_LONG);
        item.setChannelName(context.getString(R.string.channel_epay_slip));
        if (list.size() > 1) {
            list.add(1, item);
        }
        item = new DisplayItem();
        item.setChannelId(MAX_LONG-1);
        item.setChannelName(context.getString(R.string.channel_to_do_list));
        if (list.size() > 2) {
            list.add(2, item);
        }
        
        item = new DisplayItem();
        item.setChannelId(MAX_LONG-2);
        item.setChannelName(context.getString(R.string.channel_apply_leave));
        if (list.size() > 3) {
            list.add(3, item);
        }
        
        item = new DisplayItem();
        item.setChannelId(MAX_LONG - 3);
        item.setChannelName(context.getString(R.string.channel_app_store));
        if (list.size() > 4) {
            list.add(4, item);
        }

        List<List<DisplayItem>> out = new ArrayList<List<DisplayItem>>();
        List<DisplayItem> temp = new ArrayList<DisplayItem>();
        int count = 0;
        boolean addedFirstPage = false;
        for (int i = 0; i < list.size(); i++) {
            temp.add(list.get(i));
            count++;
            if (!addedFirstPage && count == NUMBER_CHANNEL_PER_LAYOUT_ABOVE) {
                addedFirstPage = true;
                out.add(temp);
                count = 0;
                temp = new ArrayList<DisplayItem>();
            } else if (count == NUMBER_CHANNEL_PER_LAYOUT_BELOW) {
                out.add(temp);
                count = 0;
                temp = new ArrayList<DisplayItem>();
            }
        }
        if (!temp.isEmpty()) {
            out.add(temp);
        }

        return out;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null || view.findViewById(R.id.view_above) == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_channel_list, null);
            View menuButton = view.findViewById(R.id.btn_menu);
            menuButton.startAnimation(cycleAnimation);
            ViewGroup aboveView = (ViewGroup) view.findViewById(R.id.view_above);
            ViewGroup belowView = (ViewGroup) view.findViewById(R.id.view_below);
            holder.channelName = Utility.getTextViewsByTag(aboveView, "channel_name");
            holder.channel = Utility.getViewsByTag(aboveView, "channel");
            holder.thumb = Utility.getImageViewsByTag(aboveView, "thumb");
            holder.numberArticle = Utility.getTextViewsByTag(aboveView, "number_of_article");
            holder.channelName2 = Utility.getTextViewsByTag(belowView, "channel_name");
            holder.channel2 = Utility.getViewsByTag(belowView, "channel");
            holder.thumb2 = Utility.getImageViewsByTag(belowView, "thumb");
            holder.numberArticle2 = Utility.getTextViewsByTag(belowView, "number_of_article");
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        


        setView(position, view, holder);
        return view;
    }

    private void setFilterImage(View view, String filterName, boolean isFirstBlock) {
        int resourceId = 0;
        if (filterName == null) {
            resourceId = isFirstBlock ? R.drawable.flip_000000 : R.drawable.f_000000;
        } else {
            resourceId = context.getResources().getIdentifier((isFirstBlock ? "flip_" : "f_") + filterName, "drawable", "com.esquel.epass");
        }

        if (resourceId == 0) {
            resourceId = isFirstBlock ? R.drawable.flip_000000 : R.drawable.f_000000;
        }
        view.setBackgroundResource(resourceId);

    }

    private void setView(final int position, View convertView, ViewHolder holder) {
        convertView.findViewById(R.id.top_menu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });

        convertView.findViewById(R.id.btn_menu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });
        if (position == 0) {
            convertView.findViewById(R.id.avatar).setVisibility(View.GONE);
            convertView.findViewById(R.id.username).setVisibility(View.GONE);
            convertView.findViewById(R.id.search).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.top_title).setVisibility(View.VISIBLE);

        } else {
            convertView.findViewById(R.id.avatar).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.username).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.search).setVisibility(View.GONE);
            convertView.findViewById(R.id.top_title).setVisibility(View.GONE);
            setUserName(convertView);
        }
        
        convertView.findViewById(R.id.search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra(UserInfoActivity.EXTRA_IN_SEARCH, true);
				context.startActivity(intent);
			}
        	
        });

        if (position == 0 || (position == 1 && hasAnimation)) {
            List<DisplayItem> displayItem = mDisplayList.get(position);
            convertView.findViewById(R.id.view_above).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.view_below).setVisibility(View.GONE);
            for (int i = 0; i < holder.channel.size(); i++) {
                holder.channel.get(i).setVisibility(View.GONE);
            }
            for (int i = 0; i < displayItem.size() && i < holder.channelName.size(); i++) {
                final long id = displayItem.get(i).getChannelId();

                if (i == 0) {
                    try {
                        String title = displayItem.get(i).getArticleName();
                        if (holder.articleTitle != null) {
                            holder.articleTitle.setText(title);
                        }
                    } catch (Exception e) {
                    }
                }

                holder.channelName.get(i).setText(displayItem.get(i).getChannelName());
                holder.channel.get(i).setVisibility(View.VISIBLE);

                final String title = displayItem.get(i).getChannelName();
                holder.channel.get(i).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        selectChannel.selectChannel(id, title);
                    }
                });
                holder.numberArticle.get(i).setText("" + displayItem.get(i).getNumberArticle());

                try {
                    String image = displayItem.get(i).getImage();
                    if (image.length() > 0) {
                        loadImage(position, image, holder.thumb.get(i));
                    } else {
                        holder.thumb.get(i).setImageResource(R.drawable.default_image);
                    }

                } catch (Exception e) {
                }
                if (i == 0) {
                    View view = convertView.findViewById(R.id.filter_image);
                    if (view != null) {
                        setFilterImage(view, displayItem.get(i).getFilterImageName(), true);
                    }
                } else {
                    setFilterImage(holder.channelName.get(i), displayItem.get(i).getFilterImageName(), false);
                }				
            }
        } else {
            final List<DisplayItem> displayItem = mDisplayList.get(position);
            convertView.findViewById(R.id.view_above).setVisibility(View.GONE);
            convertView.findViewById(R.id.view_below).setVisibility(View.VISIBLE);
            for (int i = 0; i < holder.channel2.size(); i++) {
                holder.channel2.get(i).setVisibility(View.GONE);
            }

            for (int i = 0; i < displayItem.size() && i < holder.channelName2.size(); i++) {
            	if (position == getCount() - 1 && i == displayItem.size() - 1) {
            		holder.channel2.get(i).findViewById(R.id.last_item).setVisibility(
							View.VISIBLE);
            		holder.channel2.get(i).findViewById(R.id.channel_wrapper).setVisibility(
							View.GONE);
				} else {
					holder.channel2.get(i).findViewById(R.id.last_item).setVisibility(View.GONE);
					holder.channel2.get(i).findViewById(R.id.channel_wrapper).setVisibility(
							View.VISIBLE);
				}
                final long id = displayItem.get(i).getChannelId();

                holder.channelName2.get(i).setText(displayItem.get(i).getChannelName());
                holder.channel2.get(i).setVisibility(View.VISIBLE);

                final int pos = i;
                final String title = displayItem.get(i).getChannelName();

                holder.channel2.get(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                    	if (position == getCount() - 1 && pos == displayItem.size() - 1) {
                    		if (getOnClickAddItemListener() != null) {
                    			getOnClickAddItemListener().onClickItem();
                    		}
                    	} else {
                    		if (displayItem.get(pos).getNumberArticle() > 0) {
                                selectChannel.selectChannel(id, title);
                            } else {
                                Toast.makeText(context, "No article", Toast.LENGTH_SHORT).show();
                            }
                    	}
                        
                    }
                });
                holder.numberArticle2.get(i).setText("" + displayItem.get(i).getNumberArticle());
                try {
                    String image = displayItem.get(i).getImage();
                    if (image.length() > 0) {
                        loadImage(position, image, holder.thumb2.get(i));

                    } else {
                        holder.thumb2.get(i).setImageResource(R.drawable.default_image);
                    }

                } catch (Exception e) {
                    holder.thumb2.get(i).setImageResource(R.drawable.default_image);
                }
                setFilterImage(holder.channelName2.get(i), displayItem.get(i).getFilterImageName(), false);
                
                
            }
        }

        FontUtils.setFontTextView(context, (TextView) convertView.findViewById(R.id.top_title), FontUtils.ROBOTO_LIGHT);
    }

    public synchronized void update(List<DisplayItem> displayList, int currentPosition) {
        List<List<DisplayItem>> newList = getList(displayList);
        if (newList.size() == 0) {
            return;
        }
        List<DisplayItem> currentDisplayList = mDisplayList.get(currentPosition);
        newList.add(1, currentDisplayList);
        mDisplayList = newList;

    }

    public void setFlag(boolean flag) {
        this.hasAnimation = flag;
    }

    public boolean getFlag() {
        return this.hasAnimation;
    }

    public synchronized void removeItem(int position) {
        hasAnimation = false;
        if (position > -1 && mDisplayList.size() > position) {
            mDisplayList.remove(position);
            notifyDataSetChanged();
        }

    }

    /**
     * 
     * @author joyaether
     * 
     */
    private void loadImage(int position, String url, ImageView imageView) {
        if (getImageDownloader() != null) {
            getImageDownloader().download(url, R.drawable.icon_loading_image, Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, imageView);
        }
    }

    /**
     * @return the menuView
     */
    public List<ImageView> getMenuView() {
        return menuView;
    }

    /**
     * @param menuView
     *            the menuView to set
     */
    public void setMenuView(List<ImageView> menuView) {
        this.menuView = menuView;
    }

    public void refreshUserData(final Runnable runnable) {
        if (userInfoElement == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
            String password = pref.getString(HomeActivity.KEY_PASSWORD, null);
            ((AppApplication) ((Activity) context).getApplication()).getRestStore().performQuery(
                    new Query().fieldIsEqualTo("username", userId).fieldIsEqualTo("password", password).fieldIsEqualTo("key", "65270289"), "user_info",
                    new OAuthRestStoreCallback(context) {

                        @Override
                        public void success(DataElement element, String resource) {
                            if (element != null && element.isObject()) {
                                userInfoElement = element;
                                ((Activity) context).runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (runnable != null) {
                                            runnable.run();
                                        }

                                    }

                                });
                            }

                        }

                        @Override
                        public void fail(DatastoreException ex, String resource) {

                        }

                    });
        }
    }

    private void setUserName(final View view) {
        final TextView textView = (TextView) view.findViewById(R.id.username);
        if (userInfoElement == null) {
            refreshUserData(new Runnable() {

                @Override
                public void run() {
                    Log.log("setUserName");
                    setUserInfo(userInfoElement, textView);
                }

            });
        } else {
            setUserInfo(userInfoElement, textView);
        }

    }

    private void setUserInfo(DataElement element, TextView view) {
        if (element == null) {
            return;
        }
        String username = "";
        DataElement e = element.asObjectElement().get(UserInfo.FLOCAL_NAME);
        if (e != null && e.isPrimitive()) {
            
            username = e.asPrimitiveElement().valueAsString();
            SharedPreferenceManager.setUserName(context, username);
        }
        view.setText(username);
    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    public void setImageDownloader(ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

    public OnClickAddItemListener getOnClickAddItemListener() {
		return onClickAddItemListener;
	}

	public void setOnClickAddItemListener(OnClickAddItemListener onClickAddItemListener) {
		this.onClickAddItemListener = onClickAddItemListener;
	}

	/**
     * 
     * @author joyaether
     * 
     */
    private class ViewHolder {
        private ArrayList<TextView> channelName;
        private ArrayList<ImageView> thumb;
        private ArrayList<View> channel;
        private ArrayList<TextView> numberArticle;
        private TextView articleTitle;
        private ArrayList<TextView> channelName2;
        private ArrayList<ImageView> thumb2;
        private ArrayList<View> channel2;
        private ArrayList<TextView> numberArticle2;

    }

}
