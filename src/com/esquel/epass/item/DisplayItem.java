package com.esquel.epass.item;

/**
 * 
 */
public class DisplayItem {

    private String channelName;
    private String articleName;
    private String image;
    private long channelId;
    private long articleId;
    private String filterName;
    private int numberArticle = 0;
    private Object tag;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNumberArticle() {
        return numberArticle;
    }

    public void setNumberArticle(int numberArticle) {
        this.numberArticle = numberArticle;
    }

    public String getFilterImageName() {
        return filterName;
    }

    public void setFilterImageName(String mFilterName) {
        this.filterName = mFilterName;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;
        if (object != null && object instanceof DisplayItem) {
            sameSame = this.articleId == (((DisplayItem) object).articleId);
        }

        return sameSame;
    }
}
