/**
 * Copyright (c)
 */
package com.esquel.epass.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.datastore.ScrollListener;
import com.esquel.epass.delegate.BackListener;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.ArticleContent;
import com.esquel.epass.schema.Like;
import com.esquel.epass.ui.EdgeEffectScrollView;
import com.esquel.epass.ui.FitWidthImageView;
import com.esquel.epass.utils.JAnimationOption;
import com.esquel.epass.utils.NetworkUtils;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.rest.JsonObjectElement;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.sqlite.OrmPrimitiveElement;
import com.umeng.analytics.MobclickAgent;

/**
 * @author zshop
 */
public class ArticleViewActivity extends BaseGestureActivity implements
        OnClickListener, ScrollListener, BackListener {
    DataElement articleContent;
    LinearLayout container;
    ImageButton back;
    View menu;
    int heightTop;
    RelativeLayout.LayoutParams param;
    long channelId;
    String articleId;
    EdgeEffectScrollView scrollView;
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_TOP = 10;
    private static final int PADDING_RIGHT = 20;
    private static final int PADDING_BOTTOM = 10;
    boolean liked = false;
    private static final int DEFAULT_IMAGE_HEIGHT = 320;
    private static final int DEFAULT_IMAGE_WIDTH = 480;
    public static final String PREFIX_RESOURCE = "1-";
    /*
     * isScrollUp=true if finger move up
     */
    boolean isScrollUp = false;
    ImageButton likeButton;
    int likeDrawable = R.drawable.like_on;
    int disLikeDrawable = R.drawable.like_off;
    float lastPositionY = -1;
    float lastPositionX = -1;
    int position;
    LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    private ArrayList<String> urls = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        setContentView(R.layout.activity_article_view);
        likeButton = (ImageButton) findViewById(R.id.btn_like);
        menu = findViewById(R.id.top_menu);
        scrollView = (EdgeEffectScrollView) findViewById(R.id.scroll);
        menu.post(new Runnable() {
            @Override
            public void run() {
                heightTop = menu.getHeight();
            }
        });

        param = (RelativeLayout.LayoutParams) menu.getLayoutParams();
        back = (ImageButton) findViewById(R.id.btn_back);
        back.setOnClickListener(this);
        container = (LinearLayout) findViewById(R.id.cover);
        Intent intent = getIntent();
        channelId = intent.getExtras().getLong("id_channel");
        articleId = intent.getExtras().getString("id_article");
        position = intent.getExtras().getInt("position");
        liked = intent.getExtras().getBoolean(
                ArticleListActivity.LIKED_FIELD_NAME);
        if (intent.getExtras().containsKey(ArticleListActivity.LIKED_FIELD_NAME)) {
            if (liked) {
                likeButton.setImageResource(likeDrawable);
            } else {
                likeButton.setImageResource(disLikeDrawable);
            }
        } else {
        	getLikeFormServer(articleId);
        }
        likeButton.setOnClickListener(this);
        getArticle(channelId, articleId);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);

    }
    
    private void getLikeFormServer(String articleId) {
    	if (!NetworkUtils.hasNetworkConnection(this) || articleId == null) {
    		return;
    	}
    	showLoadingDialog();
    	List<String> list = new ArrayList<String>();
    	list.add("1-" + articleId);
    	getRestStore().performQuery(new Query().fieldIsIn(Like.ID_FIELD_NAME, list), "likes", new StoreCallback() {

			@Override
			public void success(DataElement element, String resource) {
				if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
					DataElement e = element.asArrayElement().get(0);
					final DataElement likeField = e.asObjectElement().get(Like.LIKED_FIELD_NAME);
					if (likeField != null && likeField.isPrimitive()) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								dismissLoadingDialog();
								liked = likeField.asPrimitiveElement().valueAsBoolean();
								likeButton.setImageResource(liked ? likeDrawable : disLikeDrawable);
							}
							
						});
					}
				}
			}

			@Override
			public void failure(DatastoreException ex, String resource) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissLoadingDialog();
					}
					
				});
			}
    		
    	});
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        container.removeAllViews();
        if (articleContent.asArrayElement().size() == 0) {
            getArticle(articleId);
        } else {
            buildUI();
        }

    }

    public void getArticle(long mChannelId, String mArticleId) {

        Query query = new Query();
        query.fieldIsEqualTo(ArticleContent.ARTICLE_FIELD_NAME + "/"
                + Article.ID_FIELD_NAME, mArticleId);
        query.fieldIsOrderedBy(ArticleContent.SEQUENCE_FIELD_NAME,
                Ordering.ASCENDING);
        query.expandField(ArticleContent.ARTICLE_FIELD_NAME);
        getSqliteStore().performQuery(query,
                EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE_CONTENT,
                new StoreCallback() {
                    @Override
                    public void success(final DataElement element,
                            String resource) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                articleContent = element;
                                if (articleContent.asArrayElement().size() == 0) {
                                    getArticle(articleId);

                                } else {
                                    buildUI();
                                }

                            }
                        });
                    }

                    @Override
                    public void failure(DatastoreException ex, String resource) {
                        // Do nothing
                    }

                });

    }

    public void getArticle(String localArticleId) {
        getSqliteStore().readElement(localArticleId,
                EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE, new StoreCallback() {

                    @Override
                    public void success(final DataElement element,
                            String resource) {
                        if (element != null && element.isObject()) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    setLayoutWithArticleInfo(element);
                                }

                            });
                        }
                    }

                    @Override
                    public void failure(DatastoreException ex, String resource) {
                        // Do nothing
                    }

                });
    }

    /**
     * get a date String with Chinese format.
     * 
     * @return
     */
    private String getDateString(Date date) {
        String format = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
        return formatter.format(date);
    }

    private void setLayoutWithArticleInfo(DataElement element) {
        TextView articleDateView = (TextView) findViewById(R.id.article_date);
        DataElement articleDate = element.asObjectElement().get(
                Article.ARTICLE_DATE_FIELD_NAME);
        if (articleDate != null
                && articleDate.isPrimitive()
                && articleDate.asPrimitiveElement() instanceof OrmPrimitiveElement) {
            OrmPrimitiveElement date = (OrmPrimitiveElement) articleDate
                    .asPrimitiveElement();
            if (articleDateView != null && date != null && date.valueAsDate() != null) {
                articleDateView.setText(getDateString(date.valueAsDate()));
            }
        }
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT,
                PADDING_BOTTOM);
        LayoutInflater inflater = LayoutInflater.from(ArticleViewActivity.this);
        View postView = inflater.inflate(R.layout.view_post_date_article, null);
        container.addView(postView, linearParams);

        TextView author = (TextView) findViewById(R.id.btn_back);
        DataElement authorElement = element.asObjectElement().get(
                Article.AUTHOR_FIELD_NAME);
        if (authorElement != null && authorElement.isPrimitive()) {
            author.setText(authorElement.asPrimitiveElement().valueAsString());
        }

        String title = Utility
                .getStringByKey(element, Article.TITLE_FIELD_NAME);
        if (title != null && title.length() > 0) {
            container.addView(createTitle(title), linearParams);
            MobclickAgent.onEvent(ArticleViewActivity.this, "articles",
                    title);
        }

        final String imgLink = Utility.getStringByKey(element,
                Article.IMAGE_FIELD_NAME);
        ImageView iv = createImage(imgLink);
        container.addView(iv, layoutParams);

    }

    private void buildUI() {

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT,
                PADDING_BOTTOM);
        /**
         * add post date
         */

		LayoutInflater inflater = LayoutInflater.from(ArticleViewActivity.this);
		View postView = inflater.inflate(R.layout.view_post_date_article, null);
		container.addView(postView, linearParams);

		DataElement e = articleContent.asArrayElement().get(0)
				.asObjectElement().get(ArticleContent.ARTICLE_FIELD_NAME);

		TextView articleDateView = (TextView) findViewById(R.id.article_date);
		DataElement articleDate = e.asObjectElement().get(
				Article.ARTICLE_DATE_FIELD_NAME);
		if (articleDate != null
				&& articleDate.isPrimitive()
				&& articleDate.asPrimitiveElement() instanceof OrmPrimitiveElement) {
			OrmPrimitiveElement date = (OrmPrimitiveElement) articleDate
					.asPrimitiveElement();
			articleDateView.setText(getDateString(date.valueAsDate()));
		}

		TextView author = (TextView) findViewById(R.id.btn_back);
		DataElement authorElement = e.asObjectElement().get(
				Article.AUTHOR_FIELD_NAME);
		if (authorElement != null && authorElement.isPrimitive()) {
			author.setText(authorElement.asPrimitiveElement().valueAsString());
		}

		String title = Utility.getStringByKey(e, Article.TITLE_FIELD_NAME);
		if (title != null && title.length() > 0) {
			container.addView(createTitle(title), linearParams);
		}

		final String imgLink = Utility.getStringByKey(e,
				Article.IMAGE_FIELD_NAME);
		ImageView iv = createImage(imgLink);
		container.addView(iv, layoutParams);

		constructContent(linearParams);

    }

    private void constructContent(LinearLayout.LayoutParams linearParams) {
        /*
         * construct the ui base on the content type
         */
        for (int i = 0; i < articleContent.asArrayElement().size(); i++) {
            DataElement element = articleContent.asArrayElement().get(i);
            DataElement type = element.asObjectElement().get(
                    ArticleContent.TYPE_FIELD_NAME);
            if (type != null && type.isPrimitive()) {
                if (type.asPrimitiveElement().valueAsString()
                        .endsWith(ArticleContent.TYPE_IMAGE)) {
                    String url = "";
                    DataElement urlString = element.asObjectElement().get(
                            ArticleContent.URL_FIELD_NAME);
                    if (urlString != null && urlString.isPrimitive()) {
                        url = urlString.asPrimitiveElement().valueAsString();
                    }
                    ImageView image = null;
                    image = createImage(url);
                    container.addView(image, layoutParams);
                } else {
                    String text = "";
                    DataElement contentString = element.asObjectElement().get(
                            ArticleContent.TEXT_CONTENT_FIELD_NAME);
                    if (contentString != null && contentString.isPrimitive()) {
                        text = contentString.asPrimitiveElement()
                                .valueAsString();
                    }
                    TextView content = null;
                    if (text.contains("\\n")) {
                        text = text.replaceAll("\\\\n", "");
                    }

                    if (text.contains("\\r")) {
                        text = text.replace("\\\\r", "");
                    }
                    content = createContent(text);
                    container.addView(content, linearParams);
                }

            }
        }
    }

    public TextView createTitle(String str) {
        TextView title = new TextView(ArticleViewActivity.this);
        title.setText(str);
        title.setTextAppearance(ArticleViewActivity.this, R.style.article_name);
        Linkify.addLinks(title, Linkify.ALL);
        MobclickAgent.onEvent(this, "articles", str);
        return title;
    }

    public TextView createContent(String str) {

        TextView title = new TextView(ArticleViewActivity.this);
        title.setText(str);
        title.setTextAppearance(ArticleViewActivity.this,
                R.style.article_summary);
        title.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT,
                PADDING_BOTTOM);
        Linkify.addLinks(title, Linkify.ALL);
        return title;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ImageView createImage(final String str) {
        FitWidthImageView img = new FitWidthImageView(ArticleViewActivity.this);
        img.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        img.setTag(str);
        startDownloader(str, img);

        if (!urls.contains(str)) {
            urls.add(str);
        }
        return img;
    }

    @Override
    public void onBackPressed() {

        overridePendingTransition(-1, R.anim.trans_right_out);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("articleId", articleId);
        returnIntent.putExtra("position", position);
        returnIntent.putExtra(ArticleListActivity.LIKED_FIELD_NAME, liked);
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(back)) {
            onBackPressed();
        } else if (v.equals(likeButton)) {
            liked = !liked;
            // if (liked) {
            // likeButton.setImageResource(likeDrawable);
            // } else {
            // likeButton.setImageResource(disLikeDrawable);
            // }
            setLike("");
            likeButton.setEnabled(false);
        }

    }

    /**
     * 
     * @param isLike
     *            if(isLike==true)->post method, else -> delete method.
     */
    void setLike(final String add) {

        /*
         * please, write logic for this function
         */
        JsonObjectElement e = new JsonObjectElement();
        e.set("resource_id", add + PREFIX_RESOURCE + articleId);
        DataElement element = e.asObjectElement();

        if (liked) {
            getRestStore().createElement(element, "likes", new StoreCallback() {

                @Override
                public void success(DataElement element, String resource) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            likeButton.setEnabled(true);
                            likeButton.setImageResource(likeDrawable);
                        }

                    });
                }

                @Override
                public void failure(final DatastoreException ex,
                        final String resource) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (add.length() == 0) {
                                setLike("[");
                                return;
                            }
                            liked = !liked;
                            likeButton.setImageResource(disLikeDrawable);
                            likeButton.setEnabled(true);
                        }

                    });

                }
            });
        } else {

            getRestStore().deleteElement(add + PREFIX_RESOURCE + articleId,
                    "likes", new StoreCallback() {

                        @Override
                        public void success(DataElement element, String resource) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    likeButton.setEnabled(true);
                                    likeButton
                                            .setImageResource(disLikeDrawable);
                                }

                            });

                        }

                        @Override
                        public void failure(final DatastoreException ex,
                                String resource) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (add.length() == 0) {
                                        setLike("[");
                                        return;
                                    }
                                    liked = !liked;
                                    likeButton.setImageResource(likeDrawable);
                                    likeButton.setEnabled(true);
                                }

                            });

                        }
                    });
        }

    }

    @Override
    public void scroll(final int y, final int oldY, final boolean isTouch) {

        if (!isTouch) {

            int currentHeight = menu.getHeight();
            if (isScrollUp) {
                JAnimationOption.jCollapseAlert(ArticleViewActivity.this, menu,
                        currentHeight, 0);
            } else {
                JAnimationOption.jExpandAlert(ArticleViewActivity.this, menu,
                        currentHeight, heightTop);
            }
        } else {
            if (y > oldY) {
                isScrollUp = true;
            } else {
                isScrollUp = false;
            }

            int h = menu.getHeight();
            int delta = y - oldY;
            int newHeight = h - delta;
            if (newHeight < 0) {
                newHeight = 0;
            } else if (newHeight > heightTop) {
                newHeight = heightTop;
            }
            menu.getLayoutParams().height = newHeight;
            menu.requestLayout();
        }

    }

    @Override
    public void onBack() {

    }

}
