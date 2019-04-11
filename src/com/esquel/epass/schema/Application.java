package com.esquel.epass.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.joyaether.datastore.schema.Identity;
import com.joyaether.datastore.schema.Model;

@DatabaseTable(tableName = "applications")
public class Application extends Model<Application, Long> implements Identity<Long> {
	
	public static final String ID_FIELD_NAME = "application_id";
	public static final String RELEASED_APPLICATION_VERSIONS_FIELD_NAME = "released_application_versions";
	public static final String IDENTIFIER_FIELD_NAME = "identifier";
	public static final String NAME_FIELD_NAME = "name";
	public static final String SCHEME_FIELD_NAME = "scheme";
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String OWNER_ID_FIELD_NAME = "owner_id";
	public static final String TERM_APPLICATION_PLATFORM_FIELD_NAME = "$term_application_platform";
	public static final String TERM_APPLICATION_TYPE_FIELD_NAME = "$term_application_type";
	public static final String OAUTH_CLIENT_ID_FIELD_NAME = "oauth_client_id";
	public static final String OAUTH_CLIENT_SECRET_FIELD_NAME = "oauth_client_secret";
	public static final String FEATURED_FIELD_NAME = "featured";
	public static final String STORE_URL_FIELD_NAME = "store_url";
	public static final String ICON_URL_FIELD_NAME = "icon_url";
	public static final String RELEASED_FIELD_NAME = "released";
	public static final String RATING_ONE_FIELD_NAME = "rating_1";
	public static final String RATING_TWO_FIELD_NAME = "rating_2";
	public static final String RATING_THREE_FIELD_NAME = "rating_3";
	public static final String RATING_FOUR_FIELD_NAME = "rating_4";
	public static final String RATING_FIVE_FIELD_NAME = "rating_5";
	public static final String TOTAL_DOWNLOAD_FIELD_NAME = "total_downloads";
	public static final String APPLICATION_PLATFORM_FIELD_NAME = "application_platform";
	
	@DatabaseField(id = true, 
			columnName = ID_FIELD_NAME, canBeNull = false)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private long id;
	
	@DatabaseField(columnName = IDENTIFIER_FIELD_NAME, canBeNull = true)
	@SerializedName(IDENTIFIER_FIELD_NAME)
	@Expose
	private String identifier;
	
	@DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
	@SerializedName(NAME_FIELD_NAME)
	@Expose
	private String name;
	
	@DatabaseField(columnName = SCHEME_FIELD_NAME, canBeNull = true)
	@SerializedName(SCHEME_FIELD_NAME)
	@Expose
	private String scheme;
	
	@DatabaseField(columnName = DESCRIPTION_FIELD_NAME, canBeNull = true)
	@SerializedName(DESCRIPTION_FIELD_NAME)
	@Expose
	private String description;
	
	@DatabaseField(columnName = OWNER_ID_FIELD_NAME, canBeNull = true)
	@SerializedName(OWNER_ID_FIELD_NAME)
	@Expose
	private String ownerId;
	
	@DatabaseField(columnName = TERM_APPLICATION_PLATFORM_FIELD_NAME, canBeNull = true)
	@SerializedName(TERM_APPLICATION_PLATFORM_FIELD_NAME)
	@Expose
	private String termApplicationPlatform;
	
	@DatabaseField(columnName = TERM_APPLICATION_TYPE_FIELD_NAME, canBeNull = true)
	@SerializedName(TERM_APPLICATION_TYPE_FIELD_NAME)
	@Expose
	private String termApplicationType;
	
	@DatabaseField(columnName = OAUTH_CLIENT_ID_FIELD_NAME, canBeNull = true)
	@SerializedName(OAUTH_CLIENT_ID_FIELD_NAME)
	@Expose
	private String oauthClientId;
	
	@DatabaseField(columnName = OAUTH_CLIENT_SECRET_FIELD_NAME, canBeNull = true)
	@SerializedName(OAUTH_CLIENT_SECRET_FIELD_NAME)
	@Expose
	private String oauthClientSecret;
	
	@DatabaseField(columnName = FEATURED_FIELD_NAME, canBeNull = true)
	@SerializedName(FEATURED_FIELD_NAME)
	@Expose
	private int featured;
	
	@DatabaseField(columnName = STORE_URL_FIELD_NAME, canBeNull = true)
	@SerializedName(STORE_URL_FIELD_NAME)
	@Expose
	private String storeUrl;
	
	@DatabaseField(columnName = ICON_URL_FIELD_NAME, canBeNull = true)
	@SerializedName(ICON_URL_FIELD_NAME)
	@Expose
	private String iconUrl;
	
	@DatabaseField(columnName = RELEASED_FIELD_NAME, canBeNull = true)
	@SerializedName(RELEASED_FIELD_NAME)
	@Expose
	private int released;
	
	@DatabaseField(columnName = RATING_ONE_FIELD_NAME, canBeNull = true)
	@SerializedName(RATING_ONE_FIELD_NAME)
	@Expose
	private int ratingOne;
	
	@DatabaseField(columnName = RATING_TWO_FIELD_NAME, canBeNull = true)
	@SerializedName(RATING_TWO_FIELD_NAME)
	@Expose
	private int ratingTwo;
	
	@DatabaseField(columnName = RATING_THREE_FIELD_NAME, canBeNull = true)
	@SerializedName(RATING_THREE_FIELD_NAME)
	@Expose
	private int ratingThree;
	
	@DatabaseField(columnName = RATING_FOUR_FIELD_NAME, canBeNull = true)
	@SerializedName(RATING_FOUR_FIELD_NAME)
	@Expose
	private int ratingFour;
	
	@DatabaseField(columnName = RATING_FIVE_FIELD_NAME, canBeNull = true)
	@SerializedName(RATING_FIVE_FIELD_NAME)
	@Expose
	private int ratingFive;
	
	@DatabaseField(columnName = TOTAL_DOWNLOAD_FIELD_NAME, canBeNull = false)
	@SerializedName(TOTAL_DOWNLOAD_FIELD_NAME)
	@Expose
	private int totalDownload;

	@Override
	public Long getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}
	
	
	
}
