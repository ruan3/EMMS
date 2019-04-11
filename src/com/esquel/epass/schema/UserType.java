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

@DatabaseTable(tableName = "user_types")
public class UserType extends Model<UserType, Long> implements Identity<Long> {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "user_type_id";
	public static final String NAME_FIELD_NAME = "name";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME,
			canBeNull = false,
			generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;
	
	@DatabaseField(columnName = NAME_FIELD_NAME)
	@SerializedName(NAME_FIELD_NAME)
	@Expose
	private String name;
	
	@DatabaseField(
			columnName = CREATED_DATE_FIELD_NAME,
			canBeNull = false,
			dataType = DataType.DATE_STRING,
			format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;
	
	@DatabaseField(
			columnName = LAST_MODIFIED_DATE_FIELD_NAME,
			canBeNull = false,
			dataType = DataType.DATE_STRING,
			format = "yyyy-MM-dd HH:mm:ss")
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
	
	public UserType() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

}
