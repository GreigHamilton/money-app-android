package com.greighamilton.moneymanagement.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

/**
 * Class used to update the database after a system update. Migrates old table records to the new format.
 * 
 * @author Greig Hamilton
 *
 */
public class Update {
	
	public static void doUpdate(Context c) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);		
		int version = sp.getInt("VERSION", 0);
		
		// only update the color parser if version is version 0 (do it once only)
		if (version < 1) fixColorParser(c, sp);
		
		// only update the locale if version is version 1 (do it once only)
		if (version < 2) setLocale(c, sp);
	}
	
	/**
	 * Method used to fix the color parser issue.
	 * 
	 * @param context		the current context
	 * @param sp			the app shared preferences
	 */
	private static void fixColorParser(Context context, SharedPreferences sp) {
		
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		Cursor c = db.getCategories();
		while (c.moveToNext()) {
			String colour = c.getString(DatabaseHelper.CATEGORY_COLOUR);
			if (colour == null) {
				int newColor = (c.getInt(DatabaseHelper.CATEGORY_TYPE) == 0) ? R.color.Green : R.color.Red;
				db.updateCategory(""+c.getInt(DatabaseHelper.CATEGORY_ID),
						c.getString(DatabaseHelper.CATEGORY_NAME),
						c.getInt(DatabaseHelper.CATEGORY_TYPE),
						context.getResources().getString(newColor),
						c.getString(DatabaseHelper.CATEGORY_DESCRIPTION));
			}
		}
		sp.edit().putInt("VERSION", 1).commit();
	}
	
	private static void setLocale(Context context, SharedPreferences sp) {
		
		String locale = context.getResources().getConfiguration().locale.getCountry();
		
		if (locale.equalsIgnoreCase("US") || locale.equalsIgnoreCase("CANADA") || locale.equalsIgnoreCase("AU")) {
			sp.edit().putString("CURRENCYSYMBOL", "$").commit();
		}
		
		else if (locale.equalsIgnoreCase("FRANCE") 
				|| locale.equalsIgnoreCase("ITALY") 
				|| locale.equalsIgnoreCase("GERMANY")
				|| locale.equalsIgnoreCase("BELGIUM")
				|| locale.equalsIgnoreCase("IRELAND")
				|| locale.equalsIgnoreCase("SPAIN")) {
			
			sp.edit().putString("CURRENCYSYMBOL", "€").commit();
		}
		else {
			sp.edit().putString("CURRENCYSYMBOL", "£").commit();
		}
		
		sp.edit().putInt("VERSION", 2).commit();
	}

}
