package com.esquel.epass.schema;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "categories")
public class Category extends Model<Category, Long> implements Identity<Long> {

	public static final String ID_FIELD_NAME = "id";
	public static final String NAME_FIELD_NAME = "name";
	public static final String CATEGORY_CHANNEL_FIELD_NAME = "category_channels";
	public static final String FILTER_NAME_FIELD_NAME = "filter_name";

	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;

	@DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
	@SerializedName(NAME_FIELD_NAME)
	@Expose
	private String name;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;
	
	@DatabaseField(columnName = FILTER_NAME_FIELD_NAME, canBeNull = false) 
	@SerializedName(FILTER_NAME_FIELD_NAME)
	@Expose
	private String filterName;

	@ForeignCollectionField(eager = false)
	@SerializedName(CATEGORY_CHANNEL_FIELD_NAME)
	@Expose
	private ForeignCollection<CategoryChannel> categoryChannels;

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

	/**
	 * @return the categoryChannels
	 */
	public ForeignCollection<CategoryChannel> getCategoryChannels() {
		return categoryChannels;
	}

	/**
	 * @param categoryChannels
	 *            the categoryChannels to set
	 */
	public void setCategoryChannels(
			ForeignCollection<CategoryChannel> categoryChannels) {
		this.categoryChannels = categoryChannels;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
