package com.greighamilton.moneymanagement.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;

/**
 * Class for an IncomeListAdapter object.
 * 
 * @author Greig Hamilton
 *
 */
public class IncomeListAdapter extends CursorAdapter {
	
	private LayoutInflater inflater;
	private DatabaseHelper db;
	
	private String currencySymbol;

	public IncomeListAdapter(Context context, Cursor cursor) {
		super(context, cursor, CursorAdapter.NO_SELECTION);
		inflater = LayoutInflater.from(context);
		db = DatabaseHelper.getInstance(context);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		currencySymbol = sp.getString("CURRENCYSYMBOL", "");
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		view.setTag(R.id.list_item_income, c.getInt(DatabaseHelper.INCOME_ID));
		
		TextView incomeName = (TextView) view.findViewById(R.id.income_name);
		TextView incomeAmount = (TextView) view.findViewById(R.id.income_amount);
		TextView incomeDate = (TextView) view.findViewById(R.id.income_date);
		TextView incomeNotes = (TextView) view.findViewById(R.id.income_notes);

		incomeName.setText(c.getString(DatabaseHelper.INCOME_NAME));
		incomeAmount.setText("" + currencySymbol +Util.floatFormat(Float.toString(c.getFloat(DatabaseHelper.INCOME_AMOUNT))));
		incomeDate.setText(c.getString(DatabaseHelper.INCOME_DATE));
		incomeNotes.setText(c.getString(DatabaseHelper.INCOME_NOTES));
		
		String catID = c.getString(DatabaseHelper.INCOME_CATEGORY_ID);
		
		String colour = db.getCategoryColour(Integer.parseInt(catID));
		TextView incomeCatId = (TextView) view.findViewById(R.id.income_category);
		incomeCatId.setBackgroundColor(Color.parseColor(colour));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.list_item_income, null);
	}
}