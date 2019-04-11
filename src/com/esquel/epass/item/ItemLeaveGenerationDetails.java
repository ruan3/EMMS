package com.esquel.epass.item;

/**
 * Key value pair of user detail, when applying for leave
 * 
 * 
 */
public class ItemLeaveGenerationDetails {

	/**
	 * Contains the Key's name of particular detail of user's leave
	 * 
	 */
	private String leaveDetailKeyName; // Contains the Key's name of particular
										// detail of user's leave

	/**
	 * Contains the Key of particular detail of user's leave
	 * 
	 */
	private String leaveDetailKey; // Contains the Key of particular detail of
									// user's leave

	/**
	 * Contains the corresponding value to key
	 */
	private String leaveDetailValue; // Contains the corresponding value to key

	/**
	 * 
	 * 
	 * @return particular key's name of User detail
	 */
	public String getLeaveDetailKeyName() {
		return leaveDetailKeyName;
	}

	/**
	 * 
	 * 
	 * set particular key's name of User detail
	 */
	public void setLeaveDetailKeyName(String leaveDetailKeyName) {
		this.leaveDetailKeyName = leaveDetailKeyName;
	}

	/**
	 * 
	 * 
	 * @return particular key of User detail
	 */
	public String getLeaveDetailKey() {
		return leaveDetailKey;
	}

	/**
	 * 
	 * 
	 * set particular key of User detail
	 */
	public void setLeaveDetailKey(String leaveDetailKey) {
		this.leaveDetailKey = leaveDetailKey;
	}

	/**
	 * 
	 * 
	 * @return particular value of User detail
	 */
	public String getLeaveDetailValue() {
		return leaveDetailValue;
	}

	/**
	 * 
	 * 
	 * set particular value of User detail
	 */
	public void setLeaveDetailValue(String leaveDetailValue) {
		this.leaveDetailValue = leaveDetailValue;
	}

}
