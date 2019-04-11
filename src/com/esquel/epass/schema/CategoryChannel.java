package com.esquel.epass.schema;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "category_channels")
public class CategoryChannel extends Model<CategoryChannel, Long> implements
		Identity<Long> {

	public static final String ID_FIELD_NAME = "id";
	public static final String CATEGORY_FIELD_NAME = "category";
	public static final String CHANNEL_FIELD_NAME = "channel";

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;

	@DatabaseField(columnName = CATEGORY_FIELD_NAME + "_id", foreign = true, canBeNull = false)
	@SerializedName(CATEGORY_FIELD_NAME)
	@Expose
	private Category category;

	@DatabaseField(columnName = CHANNEL_FIELD_NAME + "_id", foreign = true, canBeNull = false)
	@SerializedName(CHANNEL_FIELD_NAME)
	@Expose
	private Channel channel;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;

	@Override
	public Long getIdentity() {
		return null;
	}

	@Override
	public String getIdentityAttribute() {
		return null;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
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
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
