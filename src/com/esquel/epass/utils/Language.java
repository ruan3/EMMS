package com.esquel.epass.utils;

import java.util.Locale;

import android.util.Log;
/**
 * 
 * Language of a system
 *
 */
public class Language {

	static String vietnamese = "vn";
	/**
	 * 
	 * @return language of your system between English ,Chinese & Vietnamese 
	 */
	public static Locale getLang() {
		String defaultLang= Locale.getDefault().getLanguage();
		
		Log.e("Lang",""+defaultLang);
		if((defaultLang.toString()).equals(Locale.ENGLISH))
		{
			return  Locale.ENGLISH;
		}else if((defaultLang.toString()).equals(vietnamese)){
			return new Locale(vietnamese);
		}else{
			return  Locale.CHINESE;
		}
		
	}
}
