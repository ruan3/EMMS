package com.esquel.epass.item;


/**
 * 
 * Data Object for details about leave applied
 *
 */
public class ItemAppliedLeave {

	/**
	 * Type of leave
	 */
	private String type;
	/**
	 * Status of leave
	 */
	private String status;
	/**
	 * Date of leave applied on
	 */
	private String date;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
