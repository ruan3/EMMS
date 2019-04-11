package com.esquel.epass.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

@DatabaseTable(tableName = "like")
public class Like extends Model<Like, String> implements Identity<String> {
	
	public final static String ID_FIELD_NAME				= "resource_id";
	public final static String LIKE_COUNT_FIELD_NAME 		= "like_count";
	public final static String LIKED_FIELD_NAME		    = "liked";
	
	@DatabaseField(id = true, 
			columnName = ID_FIELD_NAME, canBeNull = false)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private String id;
	
	@DatabaseField(columnName = LIKE_COUNT_FIELD_NAME)
	@SerializedName(LIKE_COUNT_FIELD_NAME)
	@Expose
	private int likeCount;
	
	@DatabaseField(columnName = LIKED_FIELD_NAME)
	@SerializedName(LIKED_FIELD_NAME)
	@Expose
	private boolean liked;

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
