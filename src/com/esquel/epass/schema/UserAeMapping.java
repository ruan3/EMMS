/** 
 * UserAeMapping.java
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

@DatabaseTable(tableName = "user_ae_mappings")
public class UserAeMapping extends Model<UserAeMapping, Long> implements Identity<Long> {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "user_ae_mapping_id";
	public static final String USER_FIELD_NAME = "user";
	public static final String ID_TOKEN_FIELD_NAME = "id_token";
	public static final String AE_SOURCE_FIELD_NAME = "ae_source";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME,
			canBeNull = false,
			generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long userAeMappingid;
	
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
	
	@DatabaseField(
			columnName = USER_FIELD_NAME + "_id",
			foreign = true,
			canBeNull = false)
	@SerializedName(USER_FIELD_NAME)
	@Expose
	private User user;
	
	@DatabaseField(
			columnName = ID_TOKEN_FIELD_NAME,
			canBeNull = false,
			uniqueCombo = true)
	@SerializedName(ID_TOKEN_FIELD_NAME)
	@Expose
	private String idToken;
	
	@DatabaseField(
			columnName = AE_SOURCE_FIELD_NAME,
			canBeNull = false,
			uniqueCombo = true)
	@SerializedName(AE_SOURCE_FIELD_NAME)
	@Expose
	private String aeSource;
	
	@Override
	public int hashCode() {
		return userAeMappingid.intValue();
	}
	
	@Override
	public Long getIdentity() {
		return userAeMappingid;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}
	
	public UserAeMapping() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

}
