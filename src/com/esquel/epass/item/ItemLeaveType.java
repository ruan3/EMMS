package com.esquel.epass.item;


/**
 * 
 * Data Object for details about leave type
 *
 */
public class ItemLeaveType {
	private String id ;								//id of particular leave type
	private int iconResourceId;						//Icon of particular Type of Leave
	private String type;						//Type of Leave
	private String remainingTime;				//Remaining Time of a particular Leave
	
	
	public int getIconResourceId() {
		return iconResourceId;
	}
	public void setIconResourceId(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
