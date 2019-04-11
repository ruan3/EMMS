package com.esquel.epass.delegate;

/**
 * 
 */
public interface LikeListener {
    /**
     * 
     * @param articleId
     * @param like = true|false
     */
    void like(long articleId, boolean like);

    boolean getLike(long articleId);

    boolean getCountLike(long articleId);
}
