package com.esquel.epass.schema;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.BlobDatabaseField;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "article_contents")
public class ArticleContent extends Model<ArticleContent, Long> implements
		Identity<Long> {

	public static final String ID_FIELD_NAME = "id";
	public static final String ARTICLE_FIELD_NAME = "article";
	public static final String SEQUENCE_FIELD_NAME = "sequence";
	public static final String TYPE_FIELD_NAME = "type";
	public static final String URL_FIELD_NAME = "url";
	public static final String TEXT_CONTENT_FIELD_NAME = "text_content";
	public static final String VIDEO_THUMBNAIL_FIELD_NAME = "video_thumbnail";

	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_VIDEO = "video";

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;

	@DatabaseField(columnName = ARTICLE_FIELD_NAME + "_id", foreign = true, canBeNull = false)
	@SerializedName(ARTICLE_FIELD_NAME)
	@Expose
	private Article article;

	@DatabaseField(columnName = SEQUENCE_FIELD_NAME, canBeNull = false)
	@SerializedName(SEQUENCE_FIELD_NAME)
	@Expose
	private int sequence;

	@DatabaseField(columnName = TYPE_FIELD_NAME, canBeNull = false)
	@SerializedName(TYPE_FIELD_NAME)
	@Expose
	private String type;

	@DatabaseField(columnName = URL_FIELD_NAME, canBeNull = true)
	@SerializedName(URL_FIELD_NAME)
	@BlobDatabaseField
	@Expose
	private String url;

	@DatabaseField(columnName = TEXT_CONTENT_FIELD_NAME, canBeNull = true)
	@SerializedName(TEXT_CONTENT_FIELD_NAME)
	@Expose
	private String textContent;

	@DatabaseField(columnName = VIDEO_THUMBNAIL_FIELD_NAME, canBeNull = true)
	@BlobDatabaseField
	@SerializedName(VIDEO_THUMBNAIL_FIELD_NAME)
	@Expose
	private String videoThumbnail;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;

	@Override
	public int hashCode() {
		return id.intValue();
	}

	@Override
	public Long getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the videoThumbnail
	 */
	public String getVideoThumbnail() {
		return videoThumbnail;
	}

	/**
	 * @param videoThumbnail
	 *            the videoThumbnail to set
	 */
	public void setVideoThumbnail(String videoThumbnail) {
		this.videoThumbnail = videoThumbnail;
	}

	/**
	 * @return the textContent
	 */
	public String getTextContent() {
		return textContent;
	}

	/**
	 * @param textContent
	 *            the textContent to set
	 */
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the article
	 */
	public Article getArticle() {
		return article;
	}

	/**
	 * @param article
	 *            the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
	}

}
