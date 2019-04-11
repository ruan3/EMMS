package com.esquel.epass.utils;

import com.joyaether.datastore.ObjectElement;

public class ParserUtils {
	
	private ParserUtils() {
		
	}
	
	public static String getString(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return "";
		}
		return o.get(fieldName).asPrimitiveElement().valueAsString();
	}

	public static int getInt(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return 0;
		}
		try {
			return o.get(fieldName).asPrimitiveElement().valueAsInt();
		} catch( Exception e) {
			return 0;
		}
	}

	public static long getLong(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return 0;
		}
		try {
			return o.get(fieldName).asPrimitiveElement().valueAsLong();
		} catch( Exception e) {
			return 0l;
		}
	}

	public static boolean getBoolean(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return false;
		}
		try {
			return o.get(fieldName).asPrimitiveElement().valueAsBoolean();
		} catch( Exception e) {
			return false;
		}
	}
	
	public static double getDouble(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return 0d;
		}
		try {
			return o.get(fieldName).asPrimitiveElement().valueAsDouble();
		} catch( Exception e) {
			return 0d;
		}
	}
	
	public static float getFloat(ObjectElement o, String fieldName) {
		if (o == null || o.get(fieldName) == null
				|| !o.get(fieldName).isPrimitive()) {
			return 0f;
		}
		try {
			return o.get(fieldName).asPrimitiveElement().valueAsFloat();
		} catch( Exception e) {
			return 0f;
		}
	}
}
