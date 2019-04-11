/** 
 * UserChannel.java
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
@DatabaseTable(tableName = "user_channels")
public class UserChannel extends Model<UserChannel, String> implements
		Identity<String> {

	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "id";
	public static final String USER_FIELD_NAME = "user";
	public static final String CHANNEL_FIELD_NAME = "channel";
	public static final String IDENTIFIER_FIELD_NAME = "identifier";
	public static final String TYPE_FIELD_NAME = "type";
	public static final String SEQUENCE_FIELD_NAME = "sequence";
	public static final String TYPE_APPLICATION = "application";
	public static final String TYPE_CHANNEL = "channel";
	public static final String IMAGE_URL_FIELD_NAME = "image";
	public static final String TYPE_SYSTEOM_APPLICATION = "system";
	public static final String NAME_FIELD_NAME = "name";
	public static final String BUNDLE_IDENTIFIER_FIELD_NAME = "bundle_identifier";
	
	public enum UserChannelType {
		APPLICATION(TYPE_APPLICATION),
		CHANNEL(TYPE_CHANNEL),
		SYSTEM_APPLICATION(TYPE_SYSTEOM_APPLICATION);
		
		private String type;
		
		UserChannelType(String typeString) {
			type = typeString;
		}
		
		public String getType() {
			return type;
		}
	}

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private String id;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;
	
	@DatabaseField(columnName = IDENTIFIER_FIELD_NAME, canBeNull = false)
	@SerializedName(IDENTIFIER_FIELD_NAME)
	@Expose
	private Long identifier;
	
	@DatabaseField(columnName = TYPE_FIELD_NAME, canBeNull = false)
	@SerializedName(TYPE_FIELD_NAME)
	@Expose
	private String type;
	
	@DatabaseField(columnName = SEQUENCE_FIELD_NAME, canBeNull = false)
	@SerializedName(SEQUENCE_FIELD_NAME)
	@Expose
	private int sequence;

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}

	public UserChannel() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
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

}
