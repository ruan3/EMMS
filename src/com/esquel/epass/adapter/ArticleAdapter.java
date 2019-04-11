package com.esquel.epass.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.ArticleListActivity;
import com.esquel.epass.activity.ArticleViewActivity;
import com.esquel.epass.activity.UserInfoActivity;
import com.esquel.epass.delegate.BackListener;
import com.esquel.epass.schema.Article;
import com.esquel.epass.utils.Constants;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.sqlite.OrmPrimitiveElement;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * @author joyaether
 * 
 */
@SuppressLint("InflateParams")
public class ArticleAdapter extends BaseAdapter {

    // private static final int MILLISECOND = 1000;
    //
    // private static final int HOUR_IN_DAY = 24;
    //
    // private static final int SECOND_IN_HOUR = 3600;

    private LayoutInflater inflater;

    private Context context;
    long channelId;
    List<DataElement> list;
    List<Integer> likeArray = new ArrayList<Integer>();
    List<Boolean> youArray = new ArrayList<Boolean>();
    String channelName;
    int offSet;
    Handler handler = new Handler();
    private final HashMap<String, String> details;
    static final int ONE_ITEM = 1;
    static final int TWO_ITEMS = 2;
    static final int THREE_ITEMS = 3;
    static final int FOUR_ITEMS = 4;
    static final int FIVE_ITEMS = 5;

    int screenWidthDp;
    HashMap<Integer, TextView> likeWait = new HashMap<Integer, TextView>();
    int allowMaxLength = -1;
    private ImageDownloader imageDownloader;
    int typeShow;
    int LINES;
    boolean isScrollMode = false;
    private boolean subscribedChannel;

    public ArticleAdapter(Context context, long idChannel, String channelName, List<DataElement> list, HashMap<String, String> map, int typeShow) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.channelId = idChannel;
        this.channelName = channelName;
        this.details = map;
        offSet = ((AppApplication) ((Activity) context).getApplication()).getOffSet();
        if (typeShow == 6) {
            this.typeShow = 5;
            isScrollMode = true;
            
        } else {
            this.typeShow = typeShow;
            isScrollMode = false;
        }

        initParameters();
    }

    public void initParameters() {
        Configuration configuration = context.getResources().getConfiguration();
        screenWidthDp = configuration.screenWidthDp;

        if (screenWidthDp <= 320) {
            if (typeShow == TWO_ITEMS) {
                LINES = 6;
            } else if (typeShow == FIVE_ITEMS) {
                LINES = 2;
            } else {
                LINES = 4;
            }
        } else if (screenWidthDp > 720) {
            if (typeShow == TWO_ITEMS) {
                LINES = 7;
            } else if (typeShow == FIVE_ITEMS) {
                LINES = 4;
            } else {
                LINES = 5;
            }
        } else {
            if (typeShow == TWO_ITEMS) {
                LINES = 6;
            } else if (typeShow == FIVE_ITEMS) {
                LINES = 3;
            } else {
                LINES = 4;
            }
        }

    }

    public synchronized void updateLike(List<Integer> likeArrayParam, List<Boolean> youArrayParam) {
        this.likeArray = likeArrayParam;
        this.youArray = youArrayParam;
    }

    public synchronized void updateTypeShow(int typeShow) {

        if (typeShow == 6) {
            this.typeShow = 5;
            isScrollMode = true;
        } else {
            this.typeShow = typeShow;
            isScrollMode = false;
        }

        initParameters();
    }

    @Override
    public int getCount() {
    	int countOfPage = typeShow;
    	if (countOfPage == 5 && isScrollMode) {
    		countOfPage = 2;
    	}
        if (list.size() % countOfPage == 0) {
            return list.size() / countOfPage;
        } else {
            return list.size() / countOfPage + 1;
        }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (typeShow == 1) {
            return getDefaultView(position, convertView, parent);
        }
        View grid = convertView;
        if (grid == null || (convertView.getTag() != null && !convertView.getTag().toString().equals("" + typeShow))) {
            if (typeShow == TWO_ITEMS) {
                grid = inflater.inflate(R.layout.component_gridview_two_item, null);

            } else if (typeShow == THREE_ITEMS) {
                grid = inflater.inflate(R.layout.component_gridview_three_item, null);

            } else if (typeShow == FOUR_ITEMS) {
                grid = inflater.inflate(R.layout.component_gridview_four_item, null);

            } else if (typeShow == FIVE_ITEMS) {
                grid = inflater.inflate(isScrollMode ? R.layout.component_gridview_five_item :
                	R.layout.component_five_item, null);
            }
        } else {
            grid = (View) convertView;
        }
//        if (typeShow == FIVE_ITEMS && isScrollMode) {
//            if (position % 2 == 0) {
//                grid = inflater.inflate(R.layout.component_gridview_five_item, null);
//            } else {
//                grid = inflater.inflate(R.layout.component_gridview_five_item_2, null);
//            }
//
//        }
        if (typeShow != 6) {
        	TextView titleMenu = (TextView) grid.findViewById(R.id.title_menu);
            if (titleMenu != null) {
                titleMenu.setText(channelName);
            }	
        }
        
        int count = typeShow;
//        if (count == 5 && isScrollMode) {
//        	count = 2;
//        }

        for (int i = 0; i < count; i++) {

            // view + divider=2
            final int number = 2;
            int index = position * count + i;
            View childView = null;
            if (count == FOUR_ITEMS) {
                if (i == 0) {
                    childView = ((ViewGroup) grid).findViewById(R.id.firstChild);
                } else if (i == 1) {
                    childView = ((ViewGroup) grid).findViewById(R.id.secondChild);
                } else if (i == 2) {
                    childView = ((ViewGroup) grid).findViewById(R.id.thirdChild);
                } else if (i == 3) {
                    childView = ((ViewGroup) grid).findViewById(R.id.fourthChild);
                }
            } else {
                childView = ((ViewGroup) grid).getChildAt(i * number + 1);
            }

            if (index >= list.size()) {
                if (count != FOUR_ITEMS) {
                    ((ViewGroup) grid).getChildAt(i * number).setVisibility(View.GONE);
                }
                childView.setVisibility(View.GONE);
            } else {
                if (count != FOUR_ITEMS) {
                    System.out.println("i====" + i);
                    ((ViewGroup) grid).getChildAt(i * number).setVisibility(View.VISIBLE);
                }
                if (childView != null) {
                    childView.setVisibility(View.VISIBLE);
                }
                final DataElement item = list.get(index);
                TextView title = (TextView) childView.findViewById(R.id.title);
                title.setText("");                
                final TextView summaryTextView = (TextView) childView.findViewById(R.id.summary);
                
//                if (summaryTextView != null) {
//                	summaryTextView.setText("");
//                    summaryTextView.setMinLines(LINES);
//                }               
//               
                ImageView icon = (ImageView) childView.findViewById(R.id.icon);
                TextView articleDate = (TextView) childView.findViewById(R.id.date);
            
                // title
                TextView likeCountView = (TextView) childView.findViewById(R.id.like_count);
                if (likeArray.size() > 0) {
                	if (title != null) {
                        setTitle(title, Utility.getStringByKey(item, Article.TITLE_FIELD_NAME));                		
                	}
                    if (likeCountView != null) {
                    	setLikeCount(likeCountView, likeArray.get(index));
                    }
                } else {
                    String name = Utility.getStringByKey(item, Article.TITLE_FIELD_NAME);
                    if (title != null) {
                    	setTitle(title, name);
                    }
                    if (likeCountView != null) {
                    	setLikeCount(likeCountView, 0);
                    }
                    if (title != null) {
                    	title.setTag(name);
                    }
                    // likeWait.put(index, title);

                }
                // icon
                String url = Utility.getStringByKey(item, Article.IMAGE_FIELD_NAME);
                if (url == null || url.equals("")) {
                    icon.setVisibility(View.GONE);
                } else {
                    loadImage(index, url, icon);
                }
                // page
                // page.setText("文章" + (index + 1) + "/" + list.size());
                // summary
                DataElement id = item.asObjectElement().get(Article.ID_FIELD_NAME);
                String summary = details.get(id.asPrimitiveElement().valueAsString());
                if (summaryTextView != null) {
					if (count == TWO_ITEMS && !isScrollMode) {
						summaryTextView.setTextSize(16);
					} else {
						summaryTextView.setTextSize(14);
					}
					summaryTextView.setText("");
                }
                if (summary != null) {
                    summary = summary.trim();
                    while (summary.contains("\r\n\r\n")) {
                        summary = summary.replaceAll("\r\n\r\n", "\r\n");
                    }

                    if (summary.contains("\\n")) {
                        summary = summary.replaceAll("\\\\n", "");
                    }

                    if (summary.contains("\\r")) {
                        summary = summary.replace("\\\\r", "");
                    }
                    if (summaryTextView != null) {
                    	summaryTextView.setText(summary);
                    }
                }
                if (summaryTextView != null && typeShow != FIVE_ITEMS && !isScrollMode) {
                	ViewTreeObserver observer = summaryTextView.getViewTreeObserver();
                    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int maxLines = (int) summaryTextView.getHeight()
                                / summaryTextView.getLineHeight();
                        summaryTextView.setMaxLines(maxLines);
                        summaryTextView.getViewTreeObserver().removeGlobalOnLayoutListener(
                                this);
                    }
                    });
                }
                
                
                DataElement articleDateElement = item.asObjectElement().get(Article.ARTICLE_DATE_FIELD_NAME);
                if (articleDateElement != null && articleDateElement.isPrimitive()) {
                    OrmPrimitiveElement primitiveElement = (OrmPrimitiveElement) articleDateElement.asPrimitiveElement();
                    Date date = primitiveElement.valueAsDate();
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    long time = 0l;
                    if (date != null && primitiveElement.isNumber()) {
                    	time = System.currentTimeMillis() + offSet - date.getTime();
                    } else if (primitiveElement.isNumber()) {
                    	time = System.currentTimeMillis() + offSet - primitiveElement.valueAsLong();
                    } else {
                    	try {
							time = System.currentTimeMillis() + offSet - dateFormat.parse(primitiveElement.valueAsString()).getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    articleDate.setText("" + toDuration(time));
                }

                final int idx = index;
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ArticleViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("id_article", Utility.getStringByKey(item, Article.ID_FIELD_NAME));
                        intent.putExtra("id_channel", channelId);
                        boolean you = youArray.size() > 0 ? youArray.get(idx) : false;
                        intent.putExtra("liked", you);
                        intent.putExtra("position", idx);
                        String name = Utility.getStringByKey(item, Article.TITLE_FIELD_NAME) + Utility.getStringByKey(item, Article.TITLE_FIELD_NAME);
                        if (name != null) {
                            intent.putExtra(ArticleViewActivity.KEY_TITLE, name);
                        }
                        ((Activity) context).startActivityForResult(intent, ArticleListActivity.REQUEST_CODE_ARTICLE_VIEW);

                    }
                });
                
            }
        }

        grid.findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((BackListener) context).onBack();
            }
        });
        grid.findViewById(R.id.btn_menu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL);
            }

        });
        grid.setTag("" + typeShow);
        
        CheckBox checkBox = (CheckBox) grid.findViewById(R.id.subscribe_channel);
        checkBox.setVisibility(isSubscribedChannel() && channelId != 1 ? View.VISIBLE : View.GONE);
        checkBox.setChecked(isSubscribedChannel());
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				setSubscribedChannel(isChecked);
			}
        	
        });
        
        return grid;
    }

    private void loadImage(int position, final String url, final ImageView imageView) {
        if (getImageDownloader() != null) {
            getImageDownloader().download(url, R.drawable.icon_loading_image, Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, imageView);
        }
    }

    public void setTitle(TextView tv, String titleString) {
        tv.setText(titleString);
    }

    public void refesh() {
        Iterator<Integer> iterator = likeWait.keySet().iterator();

        while (iterator.hasNext()) {
            int key = iterator.next();
            TextView value = (TextView) likeWait.get(key);
            setLikeCount(value, likeArray.get(key));
        }

    }

    public static final List<Long> TIMES = Arrays.asList(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30), TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> TIME_STRING_SINGULAR = Arrays.asList("year", "month", "day", "hour", "minute", "second");
    public static final List<String> TIME_STRING_PLURAL = Arrays.asList("year", "months", "days", "hours", "minutes", "seconds");

    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < ArticleAdapter.TIMES.size(); i++) {
            Long current = ArticleAdapter.TIMES.get(i);
            long temp = duration / current;
            if (temp > 0) {
                if (temp < 2) {
                    res.append(temp).append(" ").append(ArticleAdapter.TIME_STRING_SINGULAR.get(i)).append(temp > 1 ? "" : "").append(" ago");
                } else {
                    res.append(temp).append(" ").append(ArticleAdapter.TIME_STRING_PLURAL.get(i)).append(temp > 1 ? "" : "").append(" ago");
                }

                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0 second ago";
        } else {
            return res.toString();
        }
    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    public void setImageDownloader(ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

    public String getDepartmentString(String str) {
        final int maxLength = 20;

        if (typeShow == FIVE_ITEMS && screenWidthDp <= 320) {
            if (str.length() > maxLength) {
                return str.substring(0, maxLength);
            }
        }
        return str;

    }

    private void setLikeCount(TextView textView, int likeCount) {
        textView.setText("" + likeCount);
    }

    public View getDefaultView(final int position, View convertView, ViewGroup parent) {
        // ViewHolder holder = null;
        final DataElement item = list.get(position);
        View grid = convertView;
        if (grid == null || (convertView.getTag() != null && !convertView.getTag().toString().equals("" + typeShow))) {

            grid = inflater.inflate(R.layout.adapter_article_list, null);

        } else {
            grid = (View) convertView;
        }
        TextView articleName = (TextView) grid.findViewById(R.id.tv_article_name);
        articleName.setText("");
        final TextView summary = (TextView) grid.findViewById(R.id.tv_article_summary);
        summary.setText("");
        ImageView img = (ImageView) grid.findViewById(R.id.img);
        ImageButton back = (ImageButton) grid.findViewById(R.id.btn_back);
        TextView title = (TextView) grid.findViewById(R.id.title_menu);
        title.setText("");
        TextView articleDate = (TextView) grid.findViewById(R.id.date);
        articleDate.setText("");
        grid.findViewById(R.id.btn_menu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_SETTING_ADD_USER_CHANNEL);
            }

        });
        TextView likeCountView = (TextView) grid.findViewById(R.id.like_count);

        if (likeArray.size() > 0) {
            setTitle(articleName, Utility.getStringByKey(item, Article.TITLE_FIELD_NAME));
            setLikeCount(likeCountView, likeArray.get(position));
        } else {
            String name = Utility.getStringByKey(item, Article.TITLE_FIELD_NAME);
            setTitle(articleName, name);
            setLikeCount(likeCountView, likeArray.get(position));
            articleName.setTag(name);
            likeWait.put(position, likeCountView);

        }
        String url = Utility.getStringByKey(item, Article.IMAGE_FIELD_NAME);
        if (url == null || url.equals("")) {
            img.setVisibility(View.GONE);
        } else {
            loadImage(position, url, img);
        }
        title.setText(channelName);
        DataElement e = item.asObjectElement().get(Article.ARTICLE_DATE_FIELD_NAME);
        if (e != null && e.isPrimitive()) {
            OrmPrimitiveElement primitiveElement = (OrmPrimitiveElement) e.asPrimitiveElement();          
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date;
			try {
				date = format.parse(primitiveElement.valueAsString());
			} catch (Exception e1) {
				date = new Date();
			}     
            long t = System.currentTimeMillis() + offSet - date.getTime();
            articleDate.setText("" + toDuration(t));
        }

        grid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArticleViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("id_article", Utility.getStringByKey(item, Article.ID_FIELD_NAME));
                intent.putExtra("id_channel", channelId);
                boolean you = youArray.size() > 0 ? youArray.get(position) : false;
                intent.putExtra("liked", you);
                intent.putExtra("position", position);
                String name = Utility.getStringByKey(item, Article.TITLE_FIELD_NAME) + Utility.getStringByKey(item, Article.TITLE_FIELD_NAME);
                if (name != null) {
                    intent.putExtra(ArticleViewActivity.KEY_TITLE, name);
                }
                ((Activity) context).startActivityForResult(intent, ArticleListActivity.REQUEST_CODE_ARTICLE_VIEW);

            }
        });
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((BackListener) context).onBack();
            }
        });
        TextView currentIndex = (TextView) grid.findViewById(R.id.current_article);
        currentIndex.setText((position + 1) + "");
        TextView count = (TextView) grid.findViewById(R.id.total_articles);
        count.setText(getCount() + "");

        DataElement id = item.asObjectElement().get(Article.ID_FIELD_NAME);
        String summaryString = details.get(id.asPrimitiveElement().valueAsString());
        // reset the text

        if (summary != null && summaryString != null) {
            summary.setText("");
            summaryString = summaryString.trim();
            while (summaryString.contains("\r\n\r\n")) {
                summaryString = summaryString.replaceAll("\r\n\r\n", "\r\n");
            }

            if (summaryString.contains("\\n")) {
                summaryString = summaryString.replaceAll("\\\\n", "");
            }

            if (summaryString.contains("\\r")) {
                summaryString = summaryString.replace("\\\\r", "");
            }
            summary.setText(summaryString);
        } else {
        	summary.setText("");
        }
        grid.setTag("" + typeShow);
        
        ViewTreeObserver observer = summary.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int maxLines = (int) summary.getHeight()
                    / summary.getLineHeight();
            summary.setMaxLines(maxLines - 1);
            summary.getViewTreeObserver().removeGlobalOnLayoutListener(
                    this);
        }
        });
        
        final CheckBox checkBox = (CheckBox) grid.findViewById(R.id.subscribe_channel);
        checkBox.setVisibility(channelId != 1 ? View.VISIBLE : View.GONE);
        checkBox.setChecked(isSubscribedChannel());
        final View rootView = grid;
        checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checkBox.isChecked()) {
					showUnSubscribedChannelDialog(rootView);
				}
			}
        	
        });
       
        return grid;
    }

	public boolean isSubscribedChannel() {
		return subscribedChannel;
	}

	public void setSubscribedChannel(boolean subscribedChannel) {
		this.subscribedChannel = subscribedChannel;
	}
	
	private void showUnSubscribedChannelDialog(final View view) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    	dialog.setTitle(R.string.remove_channel);
    	dialog.setMessage(R.string.remove_channel_message);
    	dialog.setCancelable(false);
    	dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.subscribe_channel);
				setSubscribedChannel(true);
				checkBox.setChecked(true);				
			}
		});
    	dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.subscribe_channel);
				checkBox.setChecked(false);
				setSubscribedChannel(false);

			}
		});
    	dialog.show();
    }
}
