package com.esquel.epass.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

@DatabaseTable(tableName = "regions")
public class Region extends Model<Region, Long> implements Identity<Long> {
	public static final String ID_FIELD_NAME = "region_id";
	public static final String NAME_FIELD_NAME = "name";
	public static final String ARTICLE_REGION_FIELD_NAME = "article_regions";
	public static final String DEFAULT_REGION_NAME = "ALL";

	
	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;
	
	@DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = true)
	@SerializedName(NAME_FIELD_NAME)
	@Expose
	private String name;
	
	@ForeignCollectionField(eager = false)
	@SerializedName(ARTICLE_REGION_FIELD_NAME)
	@Expose
	private ForeignCollection<ArticleRegion> articleRegions;
	
	@Override
	public Long getIdentity() {
		return id;
	}
	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}	

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
