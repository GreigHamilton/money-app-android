package com.greighamilton.moneymanagement.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Password {
	
	public static void setPassword(Context c, String password) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);		
		sp.edit().putString("PASSWORD", password).commit();
		
	}
	
	public static boolean isPasswordCorrect(Context c, String guess) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);	
		String actualPassword = sp.getString("PASSWORD", "");
		return (guess.equals(actualPassword));
		
	}
	
	public static boolean isPasswordProtected(Context c) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);	
		return (sp.getBoolean("pref_use_password", false));
		
	}

}
