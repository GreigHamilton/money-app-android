package com.greighamilton.moneymanagement.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;

public class ExpenseListAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private Cursor c;
	private DatabaseHelper db;

	public ExpenseListAdapter(Context context, Cursor cursor) {
		super(context, cursor, CursorAdapter.NO_SELECTION);
		inflater = LayoutInflater.from(context);
		c = cursor;
		db = DatabaseHelper.getInstance(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		view.setTag(R.id.list_item_expense,
				c.getInt(DatabaseHelper.EXPENSE_ID));

		TextView expenseName = (TextView) view.findViewById(R.id.expense_name);
		TextView expenseAmount = (TextView) view.findViewById(R.id.expense_amount);
		TextView expenseDate = (TextView) view.findViewById(R.id.expense_date);
		TextView expenseNotes = (TextView) view.findViewById(R.id.expense_notes);

		expenseName.setText(c.getString(DatabaseHelper.EXPENSE_NAME));
		expenseAmount.setText("£"+Util.floatFormat(Float.toString(c.getFloat(DatabaseHelper.EXPENSE_AMOUNT))));
		expenseDate.setText(c.getString(DatabaseHelper.EXPENSE_DATE));
		expenseNotes.setText(c.getString(DatabaseHelper.EXPENSE_NOTES));

		String catID = c.getString(DatabaseHelper.EXPENSE_CATEGORY_ID);
		String colour = db.getCategoryColour(Integer.parseInt(catID));
		TextView incomeCatId = (TextView) view
				.findViewById(R.id.expense_category);
		incomeCatId.setBackgroundColor(Color.parseColor(colour));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.list_item_expense, null);
	}
}