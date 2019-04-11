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

@DatabaseTable(tableName = "requests")
public class Request extends Model<Request, Long> implements Identity<Long> {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "request_id";
	public static final String USER_FIELD_NAME = "user";
	public static final String TASK_FIELD_NAME = "task";
	public static final String REQUESTER_LOCATION_FIELD_NAME = "requester_location";
	public static final String REQUESTER_LOCATION_DEPARTMENT_FIELD_NAME = "requester_location_department";
	public static final String REQUESTER_NAME_FIELD_NAME = "requester_name";
	public static final String REQUESTER_SALARY_DATE_FIELD_NAME = "requester_salary_date";
	public static final String USAGE_FIELD_NAME = "usage";
	public static final String RECEIVE_METHOD_FIELD_NAME = "receive_method";
	public static final String RECEIVE_ADDRESS_FIELD_NAME = "receive_address";
	public static final String PHONE_FIELD_NAME = "phone";
	public static final String REMARK_FIELD_NAME = "remark";
	public static final String STATUS_FIELD_NAME = "status";
	public static final String REJECT_REASON_FIELD_NAME = "reject_reason";
	public static final int REQUEST_STATUS_IN_PROGRESS = 0;
	public static final int REQUEST_STATUS_COMPLETED = 1;
	public static final int REQUEST_STATUS_REJECTED = 2;
	public static final int SALARY_DATE_FIFTH = 0;
	public static final int SALARY_DATE_FIFTEENTH = 15;
	public static final int USAGE_LOAN = 0;
	public static final int USAGE_INSURANCE = 1;
	public static final int USAGE_HOUSING = 2;
	public static final int RECEIVE_METHOD_SEND = 0; 
	public static final int RECEIVE_METHOD_TAKE = 1;
	
	@DatabaseField(
			columnName = ID_FIELD_NAME,
			canBeNull = false,
			generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;
	
	@DatabaseField(
			columnName = USER_FIELD_NAME + "_id",
			foreign = true)
	@SerializedName(USER_FIELD_NAME)
	@Expose
	private User user;
	
	@DatabaseField(
			columnName = TASK_FIELD_NAME + "_id",
			foreign = true)
	@SerializedName(TASK_FIELD_NAME)
	@Expose
	private Task task;
	
	@DatabaseField(
			columnName = REQUESTER_LOCATION_FIELD_NAME,
			foreign = true)
	@SerializedName(REQUESTER_LOCATION_FIELD_NAME)
	@Expose
	private Location requestLocation;
	
	@DatabaseField(
			columnName = REQUESTER_LOCATION_DEPARTMENT_FIELD_NAME,
			foreign = true)
	@SerializedName(REQUESTER_LOCATION_DEPARTMENT_FIELD_NAME)
	@Expose
	private Location requestLocationDepartment;
	
	@DatabaseField(columnName = REQUESTER_NAME_FIELD_NAME)
	@SerializedName(REQUESTER_NAME_FIELD_NAME)
	@Expose
	private String requesterName;
	
	@DatabaseField(columnName = REQUESTER_SALARY_DATE_FIELD_NAME)
	@SerializedName(REQUESTER_SALARY_DATE_FIELD_NAME)
	@Expose
	private Long requesterSalaryDate;
	
	@DatabaseField(columnName = USAGE_FIELD_NAME)
	@SerializedName(USAGE_FIELD_NAME)
	@Expose
	private Long usage;
	
	@DatabaseField(columnName = RECEIVE_METHOD_FIELD_NAME)
	@SerializedName(RECEIVE_METHOD_FIELD_NAME)
	@Expose
	private Long receiveMethod;
	
	@DatabaseField(columnName = RECEIVE_ADDRESS_FIELD_NAME)
	@SerializedName(RECEIVE_ADDRESS_FIELD_NAME)
	@Expose
	private String receiveAddress;
	
	@DatabaseField(columnName = PHONE_FIELD_NAME)
	@SerializedName(PHONE_FIELD_NAME)
	@Expose
	private String phone;
	
	@DatabaseField(columnName = REMARK_FIELD_NAME)
	@SerializedName(REMARK_FIELD_NAME)
	@Expose
	private String remark;
	
	@DatabaseField(columnName = STATUS_FIELD_NAME)
	@SerializedName(STATUS_FIELD_NAME)
	@Expose
	private Long status;
	
	@DatabaseField(columnName = REJECT_REASON_FIELD_NAME)
	@SerializedName(REJECT_REASON_FIELD_NAME)
	@Expose
	private String rejectReason;
	
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
	
	public Request() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

}
