package com.greighamilton.moneymanagement.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class for the password object.
 * 
 * @author Greig Hamilton
 *
 */
public class Password {
	
	/**
	 * Method for setting a new password.
	 * 
	 * @param c				the current context
	 * @param password		the password
	 */
	public static void setPassword(Context c, String password) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);		
		sp.edit().putString("PASSWORD", password).commit();
		
	}
	
	/**
	 * Method for checking if the password is correct.
	 * 
	 * @param c			the current context
	 * @param guess		the guess made by the user
	 * 
	 * @return			boolean whether the password was correct or not
	 */
	public static boolean isPasswordCorrect(Context c, String guess) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);	
		String actualPassword = sp.getString("PASSWORD", "");
		return (guess.equals(actualPassword));
		
	}
	
	/**
	 * Method for checking current status of password protection in app.
	 * 
	 * @param c			the current context
	 * 
	 * @return			true or false depending if app is password protected or not
	 */
	public static boolean isPasswordProtected(Context c) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);	
		return (sp.getBoolean("pref_use_password", false));
		
	}

}
