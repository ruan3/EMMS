/** 
 * Channel.java
 *
 * Copyright (c) 2008-2014 Joy Aether Limited. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * 
 * This unpublished material is proprietary to Joy Aether Limited.
 * All rights reserved. The methods and
 * techniques described herein are considered trade secrets
 * and/or confidential. Reproduction or distribution, in whole
 * or in part, is forbidden except by express written permission
 * of Joy Aether Limited.
 */
package com.esquel.epass.schema;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "channels")
public class Channel extends Model<Channel, Long> implements Identity<Long> {

	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "id";
	public static final String NAME_FIELD_NAME = "name";
	public static final String CATEGORY_CHANNEL_FIELD_NAME = "category_channels";
	public static final String DEFAULT_IMAGE_FIELD_NAME = "default_image";
	public static final String CHANNEL_ARTICLE_FIELD_NAME = "channel_articles";
	public static final String SEQUENCE_FIELD_NAME = "sequence";

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;

	@DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
	@SerializedName(NAME_FIELD_NAME)
	@Expose
	private String name;

	@DatabaseField(columnName = DEFAULT_IMAGE_FIELD_NAME, canBeNull = false)
	@SerializedName(DEFAULT_IMAGE_FIELD_NAME)
	@Expose
	private String defaultImage;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;

	@ForeignCollectionField(eager = false)
	@SerializedName(CATEGORY_CHANNEL_FIELD_NAME)
	@Expose
	private ForeignCollection<CategoryChannel> categoryChannels;

	@ForeignCollectionField(eager = false)
	@SerializedName(CHANNEL_ARTICLE_FIELD_NAME)
	@Expose
	private ForeignCollection<ChannelArticle> channelArticles;
	
	@DatabaseField(columnName = SEQUENCE_FIELD_NAME, canBeNull = false)
	@SerializedName(SEQUENCE_FIELD_NAME)
	@Expose
	private int sequence;

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

	public Channel() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the categoryChannels
	 */
	public ForeignCollection<CategoryChannel> getCategoryChannels() {
		return categoryChannels;
	}

	/**
	 * @param categoryChannels
	 *            the categoryChannels to set
	 */
	public void setCategoryChannels(
			ForeignCollection<CategoryChannel> categoryChannels) {
		this.categoryChannels = categoryChannels;
	}

	/**
	 * @return the defaultImage
	 */
	public String getDefaultImage() {
		return defaultImage;
	}

	/**
	 * @param defaultImage
	 *            the defaultImage to set
	 */
	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}

	/**
	 * @return the channelArticles
	 */
	public ForeignCollection<ChannelArticle> getChannelArticles() {
		return channelArticles;
	}

	/**
	 * @param channelArticles
	 *            the channelArticles to set
	 */
	public void setChannelArticles(
			ForeignCollection<ChannelArticle> channelArticles) {
		this.channelArticles = channelArticles;
	}
}
