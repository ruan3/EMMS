package com.esquel.epass.schema;

public class LeavesApplicationStatus {
	
	
	public static final String RESOURCE_NAME = "leaves_application_status";
	public static final String PARAMETER_USERNAME_FIELD_NAME = "username";
	public static final String PARAMETER_PASSWORD_FIELD_NAME = "password";
	public static final String PARAMETER_START_DATE_FIELD_NAME = "start_date";
	public static final String PARAMETER_END_DATE_FIELD_NAME = "end_date";
	public static final String PARAMETER_LANGUAGE = "lang";
	
	
	public static final String RESPONSE_STATUS = "status";
	public static final String RESPONSE_LEAVE_ID = "leave_id";
	public static final String RESPONSE_EMP_NAME = "employee_name";
	public static final String RESPONSE_START_DATE = "leave_start_date";
	public static final String RESPONSE_END_DATE = "leave_end_date";
	public static final String RESPONSE_PERIOD = "period";
	public static final String RESPONSE_LEAVE_TYPE = "leave_type";
	public static final String RESPONSE_LEAVE_REJECT_REASON = "rejected";
	
	public static final int RESPONSE_OTHER_REJECTED = 3;
	public static final int RESPONSE_REJECTED = 6;
	public static final int RESPONSE_OTHER_REJECTED_STATUS = 9;
	public static final int RESPONSE_APPROVED = 5;
	public static final int RESPONSE_TO_BE_APPROVED = 0;
	public static final int RESPONSE_OTHER_TO_BE_APPROVED = 2;

}
