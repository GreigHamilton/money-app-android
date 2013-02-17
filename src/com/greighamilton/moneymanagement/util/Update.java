package com.greighamilton.moneymanagement.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class Update {
	
	public static void doUpdate(Context c) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);		
		int version = sp.getInt("VERSION", 0);
		
		Log.i("","ENTER doUpdate");
		
		if (version < 1) fixColorParser(c, sp);
	}
	
	private static void fixColorParser(Context context, SharedPreferences sp) {
		Log.i("","ENTER fixColorParser");
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

}
