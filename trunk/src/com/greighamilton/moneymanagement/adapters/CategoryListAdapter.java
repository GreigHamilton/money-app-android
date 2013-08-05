package com.greighamilton.moneymanagement.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

/**
 * Class for a CategoryListAdapter object.
 * 
 * @author Greig Hamilton
 *
 */
public class CategoryListAdapter extends CursorAdapter {
	
	private LayoutInflater inflater;
	private Cursor c;
	
	public CategoryListAdapter(Context context, Cursor cursor) {
		super(context, cursor, CursorAdapter.NO_SELECTION);
		inflater = LayoutInflater.from(context);
		c = cursor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		view.setTag(R.id.list_item_category, c.getInt(DatabaseHelper.CATEGORY_ID));
		
		TextView categoryName = (TextView) view.findViewById(R.id.list_category_name);
		TextView categoryDescription = (TextView) view.findViewById(R.id.list_category_description);
		
		categoryName.setText(c.getString(DatabaseHelper.CATEGORY_NAME));
		categoryName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD), Typeface.BOLD);
		if (c.getString(DatabaseHelper.CATEGORY_DESCRIPTION).equals(""))
			categoryDescription.setText("No description.");
		else
			categoryDescription.setText(c.getString(DatabaseHelper.CATEGORY_DESCRIPTION));
		categoryDescription.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
		
		TextView box = (TextView) view.findViewById(R.id.list_category_box);
		String colorString = c.getString(DatabaseHelper.CATEGORY_COLOUR);
		box.setBackgroundColor(Color.parseColor(colorString));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.list_item_category, null);
	}
}