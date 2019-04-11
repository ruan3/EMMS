package com.esquel.epass.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

@DatabaseTable(tableName = "article_regions")
public class ArticleRegion extends Model<ArticleRegion, Long> implements Identity<Long> {
	public static final String ID_FIELD_NAME = "id";
	public static final String REGION_FIELD_NAME = "region";
	public static final String ARTICLE_FIELD_NAME = "article";
	
	@DatabaseField(columnName = ID_FIELD_NAME, canBeNull = false, generatedId = true)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private Long id;
	
	@DatabaseField(columnName = ARTICLE_FIELD_NAME + "_id", foreign = true, canBeNull = false)
	@SerializedName(ARTICLE_FIELD_NAME)
	@Expose
	private Article article;
	
	@DatabaseField(columnName = REGION_FIELD_NAME + "_id", foreign = true, canBeNull = false)
	@SerializedName(REGION_FIELD_NAME)
	@Expose
	private Region region;
	
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
