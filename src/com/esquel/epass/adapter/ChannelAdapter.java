package com.esquel.epass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.esquel.epass.lib.flipview.Log;
import com.esquel.epass.schema.Application;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.SystemApplication;
import com.esquel.epass.schema.UserChannel;
import com.esquel.epass.schema.UserInfo;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.FontUtils;
import com.esquel.epass.utils.PackageUtils;
import com.esquel.epass.utils.SharedPreferenceManager;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.callback.OAuthRestStoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * @author joyaether
 * 
 */
public class ChannelAdapter extends BaseAdapter {

    private final LayoutInflater inflater;

    private final Context context;

    private static final int NUMBER_CHANNEL_PER_LAYOUT_ABOVE = 5;
    private static final int NUMBER_CHANNEL_PER_LAYOUT_BELOW = 6;
    // private FlipViewController controller;
    private List<List<ObjectElement>> mDisplayList;
    private List<ImageView> menuView = new ArrayList<ImageView>();
    private boolean hasAnimation = false;
    private Animation cycleAnimation;
    static final long MAX_LONG = Long.MAX_VALUE;
    private DataElement userInfoElement;
    private ImageDownloader imageDownloader;
    private OnClickAddItemListener onClickAddItemListener;
    private OnChannelItemClickListener onChannelItemClickListener;
    
    public interface OnClickAddItemListener {
    	void onClickItem();
    }
    
    public interface OnChannelItemClickListener {
    	void onChannelItemClick(ObjectElement item);
    }
    
    public ChannelAdapter(Context context, List<ObjectElement> displayList) {
        this.context = context;
        addLastItem(displayList);
        this.mDisplayList = getList(displayList);
        inflater = LayoutInflater.from(context);

        BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent);
        cycleAnimation = AnimationUtils.loadAnimation(context, R.anim.cycle);
    }
    
    private void addLastItem(List<ObjectElement> displayList) {
    	ObjectElement item = new JsonObjectElement();
    	item.set(ChannelItem.CHANNEL_NAME_FIELD_NAME, context.getString(R.string.add));
    	displayList.add(item);
    }

    @Override
    public int getCount() {

        return mDisplayList.size();

    }

    public List<ObjectElement> getList(int position) {
        return mDisplayList.get(position);
    }

    public List<List<ObjectElement>> getList(List<ObjectElement> list) {
        List<List<ObjectElement>> out = new ArrayList<List<ObjectElement>>();
        List<ObjectElement> temp = new ArrayList<ObjectElement>();
        int count = 0;
        boolean addedFirstPage = false;
        for (int i = 0; i < list.size(); i++) {
            temp.add(list.get(i));
            count++;
            if (!addedFirstPage && count == NUMBER_CHANNEL_PER_LAYOUT_ABOVE) {
                addedFirstPage = true;
                out.add(temp);
                count = 0;
                temp = new ArrayList<ObjectElement>();
            } else if (count == NUMBER_CHANNEL_PER_LAYOUT_BELOW) {
                out.add(temp);
                count = 0;
                temp = new ArrayList<ObjectElement>();
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
        Holder holder = null;
//        if (view == null || view.findViewById(R.id.view_above) == null) {
            holder = new Holder();
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
//        } 
//    else {
//            holder = (Holder) view.getTag();
//        }
        


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

    private void setView(final int position, View convertView, Holder holder) {
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
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL);
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
				((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL);
			}
        	
        });
        
        List<View> channelViews = null;
        List<TextView> channelNameViews = null;
        List<TextView> articleNumberViews = null;
        List<ImageView> imageViews = null;
        if (position == 0 || (position == 1 && hasAnimation)) {
        	channelViews = holder.channel;
        	channelNameViews = holder.channelName;
        	articleNumberViews = holder.numberArticle;
        	imageViews = holder.thumb;
        	convertView.findViewById(R.id.view_above).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.view_below).setVisibility(View.GONE);
        } else {
        	channelViews = holder.channel2;
        	channelNameViews = holder.channelName2;
        	articleNumberViews = holder.numberArticle2;
        	imageViews = holder.thumb2;
        	convertView.findViewById(R.id.view_above).setVisibility(View.GONE);
            convertView.findViewById(R.id.view_below).setVisibility(View.VISIBLE);
        }
        final List<ObjectElement> displayItem = mDisplayList.get(position);
        for (int i = 0; i < channelNameViews.size(); i++) {
        	channelViews.get(i).setVisibility(View.GONE);
        }
        for (int i = 0; i < displayItem.size() && i < channelNameViews.size(); i++) {
        	if (position == getCount() - 1 && i == displayItem.size() - 1) {
        		channelViews.get(i).findViewById(R.id.last_item).setVisibility(
						View.VISIBLE);
        		channelViews.get(i).findViewById(R.id.channel_wrapper).setVisibility(
						View.GONE);
        	} else {
        		if (i > 0) {
        			channelViews.get(i).findViewById(R.id.last_item).setVisibility(View.GONE);
        			channelViews.get(i).findViewById(R.id.channel_wrapper).setVisibility(
							View.VISIBLE);
        		}        		
        	}
        	final DataElement item = displayItem.get(i);
            DataElement e = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
            
            int id = 0;
            if (e != null && e.isPrimitive()) {
            	id = e.asPrimitiveElement().valueAsInt();
            }
            String cachedApplicationDetail = SharedPreferenceManager.getSubscribedApplicationDetail(context, id);
            JsonObjectElement cachedApplicationObject = null;
            if (cachedApplicationDetail != null) {
            	cachedApplicationObject = new JsonObjectElement(cachedApplicationDetail); 
            }
            String type = null;
            e = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
            if (e != null && e.isPrimitive()) {
            	type = e.asPrimitiveElement().valueAsString();
            }
            
            if (type != null) {
            	if (type.equals(UserChannel.UserChannelType.CHANNEL.getType())) {
                	articleNumberViews.get(i).setVisibility(View.VISIBLE);
                } else {
                	articleNumberViews.get(i).setVisibility(View.GONE);
                }   
            } else {
            	articleNumberViews.get(i).setVisibility(View.VISIBLE);
            }
                   
            String title = "";
            
            if (cachedApplicationObject != null) {
            	e = cachedApplicationObject.get(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + Application.NAME_FIELD_NAME);
            } else {
                e = item.asObjectElement().get(ChannelItem.CHANNEL_NAME_FIELD_NAME);

            }
            
            if (e != null && e.isPrimitive()) {
            	title = e.asPrimitiveElement().valueAsString();
            }
            
            String packageName = null;
            if (type != null && type.equals(UserChannel.TYPE_SYSTEOM_APPLICATION) && id != 0) {
            	title = getSystemAppName(id);
            } else if (type != null && type.equals(UserChannel.TYPE_APPLICATION) && id != 0) {
            	DataElement bundleIdentifier = item.asObjectElement().get(ChannelItem.BUNDLE_IDENTIFIER);
            	if (bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
            		packageName = bundleIdentifier.asPrimitiveElement().valueAsString();
                	title = PackageUtils.getApplicationName(context, packageName);
            	}
            }
            
            channelNameViews.get(i).setText(title);
            channelViews.get(i).setVisibility(View.VISIBLE);
            final int pos = i;
            int articleNumber = 0;
            e = item.asObjectElement().get(ChannelItem.ARTICLE_NUMBER_FIELD_NAME);
            if (e != null && e.isPrimitive()) {
            	articleNumber = e.asPrimitiveElement().valueAsInt();
            }
            articleNumberViews.get(i).setText("" + articleNumber);
            final int number = articleNumber;
            channelViews.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                	if (position == getCount() - 1 && pos == displayItem.size() - 1) {
                		if (getOnClickAddItemListener() != null) {
                			getOnClickAddItemListener().onClickItem();
                		}
                	} else {
                		DataElement type = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
                		if (type != null && type.isPrimitive()) {
                			if (UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
                				if (number == 0) {
                					Toast.makeText(context, context.getString(R.string.no_article), Toast.LENGTH_SHORT).show();
                					return;
                				}
                			}
                			if (getOnChannelItemClickListener() != null) {
                				getOnChannelItemClickListener().onChannelItemClick(item.asObjectElement());
                    		}
                		}
                	}                  	
                }
            });

            try {
                String image = "";
                if (cachedApplicationObject != null) {
                	e = cachedApplicationObject.get(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + Application.ICON_URL_FIELD_NAME);
                } else {
                    e = item.asObjectElement().get(ChannelItem.IMAGE_URL_FIELD_NAME);
                }
                
                if (e != null && e.isPrimitive()) {
                	image = e.asPrimitiveElement().valueAsString();
                }
                imageViews.get(i).setImageResource(R.drawable.default_image);
                if (type != null && type.equals(UserChannel.TYPE_APPLICATION)) {
                	if (packageName != null) {
                		Drawable drawable = PackageUtils.getApplicationIcon(context, packageName);
                		imageViews.get(i).setImageDrawable(drawable);
                	}
                } else {
                	if (image.length() > 0) {
                        loadImage(position, image, imageViews.get(i));
                    } else {
                    	e = item.asObjectElement().get(ChannelItem.IMAGE_RESOURCE_FIELD_NAME);
                    	if (e != null && e.isPrimitive()) {
                    		imageViews.get(i).setImageResource(e.asPrimitiveElement().valueAsInt());
                    	}
                    }
                }           
            } catch (Exception ex) {
            }
            
            e = item.asObjectElement().get(ChannelItem.FILTER_IMAGE_FIELD_NAME);
            String filterImage = null;
            if (e != null && e.isPrimitive()) {
            	filterImage = e.asPrimitiveElement().valueAsString();
            }
            
            if (i == 0) {
                View view = convertView.findViewById(R.id.filter_image);
                if (view != null) {
                    setFilterImage(view, filterImage, true);
                }
            } else {
                setFilterImage(channelNameViews.get(i), filterImage, false);
            }
        }
        
//        if (position == 0 || (position == 1 && hasAnimation)) {
//            final List<ObjectElement> displayItem = mDisplayList.get(position);
//            convertView.findViewById(R.id.view_above).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.view_below).setVisibility(View.GONE);
//            for (int i = 0; i < holder.channel.size(); i++) {
//                holder.channel.get(i).setVisibility(View.GONE);
//            }
//            for (int i = 0; i < displayItem.size() && i < holder.channelName.size(); i++) {
//            	if (position == getCount() - 1 && i == displayItem.size() - 1) {
//            		holder.channel.get(i).findViewById(R.id.last_item).setVisibility(
//							View.VISIBLE);
//            		holder.channel.get(i).findViewById(R.id.channel_wrapper).setVisibility(
//							View.GONE);
//            	} else {
//            		if (i > 0) {
//            			holder.channel.get(i).findViewById(R.id.last_item).setVisibility(View.GONE);
//    					holder.channel.get(i).findViewById(R.id.channel_wrapper).setVisibility(
//    							View.VISIBLE);
//            		}        		
//            	}
//            	final DataElement item = displayItem.get(i);
//            	
//                DataElement e = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
//                
//                int id = 0;
//                if (e != null && e.isPrimitive()) {
//                	id = e.asPrimitiveElement().valueAsInt();
//                }
//                String cachedApplicationDetail = SharedPreferenceManager.getSubscribedApplicationDetail(context, id);
//                JsonObjectElement cachedApplicationObject = null;
//                if (cachedApplicationDetail != null) {
//                	cachedApplicationObject = new JsonObjectElement(cachedApplicationDetail); 
//                }
//                String type = null;
//                e = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	type = e.asPrimitiveElement().valueAsString();
//                }
//                String title = "";
//                
//                if (cachedApplicationObject != null) {
//                	e = cachedApplicationObject.get(Application.NAME_FIELD_NAME);
//                } else {
//                    e = item.asObjectElement().get(ChannelItem.CHANNEL_NAME_FIELD_NAME);
//
//                }
//                
//                if (e != null && e.isPrimitive()) {
//                	title = e.asPrimitiveElement().valueAsString();
//                }
//                
//                if (type != null && type.equals(UserChannel.TYPE_SYSTEOM_APPLICATION) && id != 0) {
//                	title = getSystemAppName(id);
//                } else if (type != null && type.equals(UserChannel.TYPE_APPLICATION) && id != 0) {
//                	DataElement bundleIdentifier = item.asObjectElement().get(ChannelItem.BUNDLE_IDENTIFIER);
//                	if (bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
//                    	title = PackageUtils.getApplicationName(context, bundleIdentifier.asPrimitiveElement().valueAsString());
//                	}
//                }
//                
//                holder.channelName.get(i).setText(title);
//                holder.channel.get(i).setVisibility(View.VISIBLE);
//                final int pos = i;
//                int articleNumber = 0;
//                e = item.asObjectElement().get(ChannelItem.ARTICLE_NUMBER_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	articleNumber = e.asPrimitiveElement().valueAsInt();
//                }
//                holder.numberArticle.get(i).setText("" + articleNumber);
//                final int number = articleNumber;
//                holder.channel.get(i).setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                    	if (position == getCount() - 1 && pos == displayItem.size() - 1) {
//                    		if (getOnClickAddItemListener() != null) {
//                    			getOnClickAddItemListener().onClickItem();
//                    		}
//                    	} else {
//                    		DataElement type = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                    		if (type != null && type.isPrimitive()) {
//                    			if (UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
//                    				if (number == 0) {
//                    					Toast.makeText(context, context.getString(R.string.no_article), Toast.LENGTH_SHORT).show();
//                    					return;
//                    				}
//                    			}
//                    			if (getOnChannelItemClickListener() != null) {
//                    				getOnChannelItemClickListener().onChannelItemClick(item.asObjectElement());
//                        		}
//                    		}
//                    	}                  	
//                    }
//                });
//               
//                
//                e = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	if (e.asPrimitiveElement().valueAsString().equals(UserChannel.UserChannelType.CHANNEL)) {
//                    	holder.numberArticle.get(i).setVisibility(View.VISIBLE);
//                	} else {
//                    	holder.numberArticle.get(i).setVisibility(View.GONE);
//                	}
//                } else {
//                	holder.numberArticle.get(i).setVisibility(View.VISIBLE);
//                }
//
//                try {
//                    String image = "";
//                    if (cachedApplicationObject != null) {
//                    	e = cachedApplicationObject.get(Application.ICON_URL_FIELD_NAME);
//                    } else {
//                        e = item.asObjectElement().get(ChannelItem.IMAGE_URL_FIELD_NAME);
//                    }
//                    
//                    if (e != null && e.isPrimitive()) {
//                    	image = e.asPrimitiveElement().valueAsString();
//                    }
//                    holder.thumb.get(i).setImageResource(R.drawable.default_image);
//                    if (image.length() > 0) {
//                        loadImage(position, image, holder.thumb.get(i));
//                    } else {
//                    	e = item.asObjectElement().get(ChannelItem.IMAGE_RESOURCE_FIELD_NAME);
//                    	if (e != null && e.isPrimitive()) {
//                    		holder.thumb.get(i).setImageResource(e.asPrimitiveElement().valueAsInt());
//                    	}
//                    }
//
//                } catch (Exception ex) {
//                }
//                
//                e = item.asObjectElement().get(ChannelItem.FILTER_IMAGE_FIELD_NAME);
//                String filterImage = null;
//                if (e != null && e.isPrimitive()) {
//                	filterImage = e.asPrimitiveElement().valueAsString();
//                }
//                
//                if (i == 0) {
//                    View view = convertView.findViewById(R.id.filter_image);
//                    if (view != null) {
//                        setFilterImage(view, filterImage, true);
//                    }
//                } else {
//                    setFilterImage(holder.channelName.get(i), filterImage, false);
//                }				
//            }
//        } else {
//            final List<ObjectElement> displayItem = mDisplayList.get(position);
//            convertView.findViewById(R.id.view_above).setVisibility(View.GONE);
//            convertView.findViewById(R.id.view_below).setVisibility(View.VISIBLE);
//            for (int i = 0; i < holder.channel2.size(); i++) {
//                holder.channel2.get(i).setVisibility(View.GONE);
//            }
//
//            for (int i = 0; i < displayItem.size() && i < holder.channelName2.size(); i++) {
//            	if (position == getCount() - 1 && i == displayItem.size() - 1) {
//            		holder.channel2.get(i).findViewById(R.id.last_item).setVisibility(
//							View.VISIBLE);
//            		holder.channel2.get(i).findViewById(R.id.channel_wrapper).setVisibility(
//							View.GONE);
//				} else {
//					holder.channel2.get(i).findViewById(R.id.last_item).setVisibility(View.GONE);
//					holder.channel2.get(i).findViewById(R.id.channel_wrapper).setVisibility(
//							View.VISIBLE);
//				}
//            	final DataElement item = displayItem.get(i);
//            	DataElement e = item.asObjectElement().get(ChannelItem.IDENTIFIER_FIELD_NAME);
//            	int id = 0;
//                if (e != null && e.isPrimitive()) {
//                	id = e.asPrimitiveElement().valueAsInt();
//                }
//            	String cachedApplicationDetail = SharedPreferenceManager.getSubscribedApplicationDetail(context, id);
//                JsonObjectElement cachedApplicationObject = null;
//                if (cachedApplicationDetail != null) {
//                	cachedApplicationObject = new JsonObjectElement(cachedApplicationDetail); 
//                }
//                String type = null;
//                e = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	type = e.asPrimitiveElement().valueAsString();
//                }
//                String title = "";
//                
//                if (cachedApplicationObject != null) {
//                	e = cachedApplicationObject.get(Application.NAME_FIELD_NAME);
//                } else {
//                    e = item.asObjectElement().get(ChannelItem.CHANNEL_NAME_FIELD_NAME);
//
//                }
//                
//                if (e != null && e.isPrimitive()) {
//                	title = e.asPrimitiveElement().valueAsString();
//                }
//                
//                String packageName = null;
//                if (type != null && type.equals(UserChannel.TYPE_SYSTEOM_APPLICATION) && id != 0) {
//                	title = getSystemAppName(id);
//                } else if (type != null && type.equals(UserChannel.TYPE_APPLICATION) && id != 0) {
//                	DataElement bundleIdentifier = item.asObjectElement().get(ChannelItem.BUNDLE_IDENTIFIER);
//                	if (bundleIdentifier != null && bundleIdentifier.isPrimitive()) {
//                		packageName = bundleIdentifier.asPrimitiveElement().valueAsString();
//                    	title = PackageUtils.getApplicationName(context, packageName);
//                	}
//                }
//                
//                
//                
//                holder.channelName2.get(i).setText(title);
//                holder.channel2.get(i).setVisibility(View.VISIBLE);
//
//                final int pos = i;
//
//                int articleNumber = 0;
//                e = item.asObjectElement().get(ChannelItem.ARTICLE_NUMBER_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	articleNumber = e.asPrimitiveElement().valueAsInt();
//                }
//                
//                final int number = articleNumber;
//                holder.channel2.get(i).setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                    	if (position == getCount() - 1 && pos == displayItem.size() - 1) {
//                    		if (getOnClickAddItemListener() != null) {
//                    			getOnClickAddItemListener().onClickItem();
//                    		}
//                    	} else {
//                    		DataElement type = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                    		if (type != null && type.isPrimitive()) {
//                    			if (UserChannel.TYPE_CHANNEL.equals(type.asPrimitiveElement().valueAsString())) {
//                    				if (number == 0) {
//                    					Toast.makeText(context, context.getString(R.string.no_article), Toast.LENGTH_SHORT).show();
//                    					return;
//                    				}
//                    			}
//                    			if (getOnChannelItemClickListener() != null) {
//                    				getOnChannelItemClickListener().onChannelItemClick(item.asObjectElement());
//                        		}
//                    		}
//                    	}
//                        
//                    }
//                });
//                holder.numberArticle2.get(i).setText("" + number);
//                e = item.asObjectElement().get(ChannelItem.TYPE_FIELD_NAME);
//                if (e != null && e.isPrimitive()) {
//                	if (e.asPrimitiveElement().valueAsString().equals(UserChannel.UserChannelType.CHANNEL.getType())) {
//                    	holder.numberArticle2.get(i).setVisibility(View.VISIBLE);
//                	} else {
//                    	holder.numberArticle2.get(i).setVisibility(View.GONE);
//                	}
//                } else {
//                	holder.numberArticle2.get(i).setVisibility(View.VISIBLE);
//                }
//                try {
//                	String image = "";
//                    e = item.asObjectElement().get(ChannelItem.IMAGE_URL_FIELD_NAME);
//                    if (e != null && e.isPrimitive()) {
//                    	image = e.asPrimitiveElement().valueAsString();
//                    }
//                    holder.thumb2.get(i).setImageResource(R.drawable.default_image);
//                    if (type != null && type.equals(UserChannel.TYPE_APPLICATION)) {
//                    	if (packageName != null) {
//                    		Drawable drawable = PackageUtils.getApplicationIcon(context, packageName);
//                    		holder.thumb2.get(i).setImageDrawable(drawable);
//                    	}
//                    } else {
//                    	if (image.length() > 0) {
//                            loadImage(position, image, holder.thumb2.get(i));
//
//                        } else {
//                        	e = item.asObjectElement().get(ChannelItem.IMAGE_RESOURCE_FIELD_NAME);
//                        	if (e != null && e.isPrimitive()) {
//                        		holder.thumb2.get(i).setImageResource(e.asPrimitiveElement().valueAsInt());
//                        	}                    	
//                        }
//                    }
//                                     
//                } catch (Exception ex) {
////                    holder.thumb2.get(i).setImageResource(R.drawable.default_image);
//                }
//                e = item.asObjectElement().get(ChannelItem.FILTER_IMAGE_FIELD_NAME);
//                String filterImage = null;
//                if (e != null && e.isPrimitive()) {
//                	filterImage = e.asPrimitiveElement().valueAsString();
//                }
//                setFilterImage(holder.channelName2.get(i), filterImage, false);
//                
//                
//            }
//        }

        FontUtils.setFontTextView(context, (TextView) convertView.findViewById(R.id.top_title), FontUtils.ROBOTO_LIGHT);
    }

    public synchronized void update(List<ObjectElement> displayList, int currentPosition) {
        List<List<ObjectElement>> newList = getList(displayList);
        if (newList.size() == 0) {
            return;
        }
        List<ObjectElement> currentDisplayList = mDisplayList.get(currentPosition);
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

	public OnChannelItemClickListener getOnChannelItemClickListener() {
		return onChannelItemClickListener;
	}

	public void setOnChannelItemClickListener(OnChannelItemClickListener onChannelItemClickListener) {
		this.onChannelItemClickListener = onChannelItemClickListener;
	}

	/**
     * 
     * @author joyaether
     * 
     */
    private class Holder {
        private ArrayList<TextView> channelName;
        private ArrayList<ImageView> thumb;
        private ArrayList<View> channel;
        private ArrayList<TextView> numberArticle;
        private ArrayList<TextView> channelName2;
        private ArrayList<ImageView> thumb2;
        private ArrayList<View> channel2;
        private ArrayList<TextView> numberArticle2;

    }
    
    private String getSystemAppName(int id) {
		if (id == SystemApplication.EPAY_SLIP.getId()) {
			return context.getString(R.string.channel_epay_slip);
		} else if (id == SystemApplication.TASK.getId()) {
			return context.getString(R.string.channel_to_do_list);
		} else if (id == SystemApplication.LEAVE.getId()) {
			return context.getString(R.string.channel_apply_leave);
		} else {
			return context.getString(R.string.channel_app_store);
		}
	}
    
    public class ChannelItem {
    	public static final String CHANNEL_NAME_FIELD_NAME = "name";
    	public static final String TYPE_FIELD_NAME = "type";
    	public static final String IMAGE_URL_FIELD_NAME = "image_url";
    	public static final String IMAGE_RESOURCE_FIELD_NAME = "image_resource";
    	public static final String ARTICLE_NUMBER_FIELD_NAME = "article_number";
    	public static final String IDENTIFIER_FIELD_NAME = "identifier";
    	public static final String FILTER_IMAGE_FIELD_NAME = "filter_image";
    	public static final String SEQUENCE_FIELD_NAME = "sequence";
    	public static final String BUNDLE_IDENTIFIER = "bundle_identifier";
    	public static final String IMAGE_ICON_FILED_NAME = "image_icon";
    }

}
