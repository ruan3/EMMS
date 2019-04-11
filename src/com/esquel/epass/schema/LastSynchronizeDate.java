package com.esquel.epass.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "last_sync_data_date")
public class LastSynchronizeDate extends Model<LastSynchronizeDate, String>
		implements Identity<String> {

	public static final String ID_FIELD_NAME = "username";
	public static final String LAST_SYNCHRONIZEDATE_DATE_FILED = "last_sync_date";

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, id = true, unique = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private String username;

	@DatabaseField(columnName = LAST_SYNCHRONIZEDATE_DATE_FILED, canBeNull = false)
	@SerializedName(LAST_SYNCHRONIZEDATE_DATE_FILED)
	@Expose
	private long lastSyncDate;

	@Override
	public String getIdentity() {
		return username;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}

	/**
	 * @return the lastSyncDate
	 */
	public long getLastSyncDate() {
		return lastSyncDate;
	}

	/**
	 * @param lastSyncDate
	 *            the lastSyncDate to set
	 */
	public void setLastSyncDate(long lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

}
