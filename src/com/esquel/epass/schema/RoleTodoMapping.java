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
import com.joyaether.datastore.security.Role;

@DatabaseTable(tableName = "role_todo_mappings")
public class RoleTodoMapping extends Model<RoleTodoMapping, Long> implements Identity<Long> {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "role_todo_mapping_id";
	public static final String ROLE_FIELD_NAME = "role";
	public static final String LOCATION_DEPARTMENT_FIELD_NAME = "location_department";
	public static final String USER_TYPE_FIELD_NAME = "user_type";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME,
			canBeNull = false,
			generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;
	
	@DatabaseField(
			columnName = ROLE_FIELD_NAME + "_id",
			uniqueCombo = true,
			foreign = true)
	@SerializedName(ROLE_FIELD_NAME)
	@Expose
	private Role role;
	
	@DatabaseField(
			columnName = LOCATION_DEPARTMENT_FIELD_NAME + "_id",
			uniqueCombo = true,
			foreign = true)
	@SerializedName(LOCATION_DEPARTMENT_FIELD_NAME)
	@Expose
	private LocationDepartment locationDepartment;
	
	@DatabaseField(
			columnName = USER_TYPE_FIELD_NAME,
			uniqueCombo = true,
			foreign = true)
	@SerializedName(USER_TYPE_FIELD_NAME)
	@Expose
	private UserType userType;
	
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
	
	public RoleTodoMapping() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

}
