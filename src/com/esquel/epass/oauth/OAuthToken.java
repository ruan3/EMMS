/**
 * OAuthToken.java
 *
 * Copyright (c) 2008-2014 Joy Aether Limited. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * 
 * This unpublished material is proprietary to Joy Aether Limited.
 * All rights reserved. The methods and
 * techniques described herein are considered trade secrets
 * and/or confidential. Reproduction or distribution, in whole
 * or in part, is forbidden except by express written permission
 * of Joy Aether Limited.
 */
package com.esquel.epass.oauth;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.joyaether.datastore.Build;
import com.joyaether.datastore.rest.oauth.OAuthConstants;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;

public class OAuthToken {
	
	private static final String TAG = OAuthToken.class.getSimpleName();
	
	/** Whether this token has been used to make requests to server. */
	private static final String CLAIM_ID_TOKEN = "id_token";
	
	/** The claims in an OAuth token. */
	private JWTClaimsSet claims;
	
	private static final long UNIT_MULTIPLIER = 1000L;
	
	/** A private constructor. */
	private OAuthToken() {}
	
	private static final String FIRST_ACCESS_CLAIM = "fac";

	
	/**
	 * Specifies a new ID token, a JWT that contains identity information about the user. 
	 * 
	 * @param idToken the JWT that contains identity information about the user
	 */
	public void setIdToken(String idToken) {
		if (claims != null && idToken != null) {
			claims.setClaim(CLAIM_ID_TOKEN, idToken);
		}
	}
	
	/**
	 * Serializes the token into its compact format.
	 * 
	 * @return The serialized token
	 */
	public String serialize() {
		PlainJWT jwt = new PlainJWT(claims);
		// Serializes the plain JWT to a compact form
		return jwt.serialize();
	}
	
	/**
	 * Returns the token as a JSON string
	 * 
	 * @return a string representation of the token
	 */
	public String toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(OAuthConstants.ACCESS_TOKEN, getClaimAsString(OAuthConstants.ACCESS_TOKEN));
		json.addProperty(OAuthConstants.REFRESH_TOKEN, getClaimAsString(OAuthConstants.REFRESH_TOKEN));
		json.addProperty(OAuthConstants.ID_TOKEN, getClaimAsString(OAuthConstants.ID_TOKEN));
		json.addProperty(OAuthConstants.EXPIRES_IN, getClaimAsString(OAuthConstants.EXPIRES_IN));
		json.addProperty(OAuthConstants.SCOPE, getClaimAsString(OAuthConstants.SCOPE));
		
		String tokenType = getClaimAsString(OAuthConstants.TOKEN_TYPE);
		json.addProperty(OAuthConstants.TOKEN_TYPE, tokenType);
		if (OAuthConstants.TOKEN_TYPE_MAC.equalsIgnoreCase(tokenType)) {
			json.addProperty(OAuthConstants.MAC_TOKEN_KEY, getClaimAsString(OAuthConstants.MAC_TOKEN_KEY));
			json.addProperty(OAuthConstants.MAC_TOKEN_ALGORITHM, getClaimAsString(OAuthConstants.MAC_TOKEN_ALGORITHM));
			json.addProperty(OAuthConstants.JWT_KEY_ID, getClaimAsString(OAuthConstants.JWT_KEY_ID));
		}
		
		return new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(json);
	}
	
	/**
	 * Gets the specified claim (registered or custom) as a {@link String}.
	 * 
	 * @param name The name of the claim. Must not be {@code null}.
	 * @return The value of the claim as a {@link String}, {@code null} if not specified.
	 */
	private String getClaimAsString(String name) {
		String result = null;
		if (claims != null) {
			Object obj = claims.getClaim(name);
			result = obj == null ? null : obj.toString();
		}
		return result;
	}
	
	/**
	 * Deserializes the token from the plain or encrypted string.
	 * 
	 * @param s
	 *          The string to deserialize. Must not be {@code null}
	 * @return The corresponding token or {@code null} if the given there is error
	 *         parsing the given string
	 */
	public static OAuthToken deserialize(String s) {
		try {
			JWT jwt = getJWT(s);
			if (jwt != null) {				
				OAuthToken token = new OAuthToken();
				token.claims = new JWTClaimsSet(jwt.getJWTClaimsSet());
				return token;
			} else {
				return deserialize(new StringRepresentation(s));
			}
		} catch (Exception ex) {
			// Ignore
		}
		return null;
	}
	
	/**
	 * Returns a JWT (JSON Web Token) from the given string 
	 * 
	 * @param s a plain or encrypted JSON Web Token string
	 * @return a parsed JWT (signed or plain) or {@code null} if the given string cannot be parsed into a JWT
	 */
	private static JWT getJWT(String s) {
		JWT result = null;
		
		if (s != null) {
			try {
				result = PlainJWT.parse(s);
			} catch (ParseException e) {				
				// We go on to try signed JWT
			}
			
			if (result == null) {
				try {
					result = SignedJWT.parse(s);
				} catch (ParseException e) {
					// We give up because the string is not a valid JWT
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Deserialize the token from a {@link Representation}.
	 * 
	 * @param representation The {@link Representation} to deserialize. Must not be {@code null}
	 * @return The corresponding token or {@code null} if the given representation is not a valid token
	 */
	public static OAuthToken deserialize(Representation representation) {
		OAuthToken result = null;
		try {
			JsonRepresentation json = new JsonRepresentation(representation);
			JSONObject object = json.getJsonObject();
			JSONArray array = object.names();
			
			OAuthToken token = new OAuthToken();
			token.claims = new JWTClaimsSet();
			for (int i = 0; i < array.length(); i++) {
				token.claims.setCustomClaim(array.getString(i), object.get(array.getString(i)));
			}
			token.claims.setCustomClaim(FIRST_ACCESS_CLAIM, true);
			token.claims.setIssueTime(new Date());
			token.claims.setNotBeforeTime(new Date());
			token.claims.setExpirationTime(new Date(new Date().getTime() + object.getLong(OAuthConstants.EXPIRES_IN) * UNIT_MULTIPLIER));
			result = token;
		} catch (Exception ex) {
			// Failed to parse token response
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to deserialize a Representation to a token.", ex);
			}
		}
		return result;
	}

}
