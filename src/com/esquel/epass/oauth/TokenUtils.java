package com.esquel.epass.oauth;

import android.util.Log;

import com.joyaether.datastore.rest.oauth.Token;
import com.joyaether.datastore.rest.security.IdToken;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

public class TokenUtils {
	
	private static final String USER_ID = "user_id";
	private static final String LOCATION_DEPARTMENT = "location_department";
	private static final String EMPLOYEE_NUMBER = "employee_number";

	private TokenUtils() {
		
	}
	
	public static String getUserId(String idToken) {
		try {			
			JWT jwt = JWTParser.parse(idToken);
			return (String) jwt.getJWTClaimsSet().getCustomClaims().get(USER_ID);

		} catch (Exception e) {
			Log.d(TokenUtils.class.getSimpleName(), e.getMessage(), e);
		}

		return "";
	}
	
	public static String getUserId(Token token) {
		try {
			if (token instanceof IdToken) {				
				JWT jwt = JWTParser.parse(((IdToken) token).getIdToken());
				return (String) jwt.getJWTClaimsSet().getCustomClaims().get(USER_ID);
			}
		} catch (Exception e) {
			Log.d(TokenUtils.class.getSimpleName(), e.getMessage(), e);
		}

		return "";
	}
	
	public static String getEmployeeNumber(Token token) {
		try {
			if (token instanceof IdToken) {				
				JWT jwt = JWTParser.parse(((IdToken) token).getIdToken());
				return (String) jwt.getJWTClaimsSet().getCustomClaims().get(EMPLOYEE_NUMBER);
			}
		} catch (Exception e) {
			Log.d(TokenUtils.class.getSimpleName(), e.getMessage(), e);
		}

		return "";
	}
	
	public static String getLocationDepartment(Token token) {
		try {
			if (token instanceof IdToken) {				
				JWT jwt = JWTParser.parse(((IdToken) token).getIdToken());
				return (String) jwt.getJWTClaimsSet().getCustomClaims().get(LOCATION_DEPARTMENT);
			}
		} catch (Exception e) {
			Log.d(TokenUtils.class.getSimpleName(), e.getMessage(), e);
		}

		return "";
	}

}
