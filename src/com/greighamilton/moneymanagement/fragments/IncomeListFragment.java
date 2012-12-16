package com.greighamilton.moneymanagement.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

/**
 * @see http://android.artemzin.ru/?p=7
 *
 */
public class IncomeListFragment extends ListFragment {

	private DatabaseHelper db;
	private Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabaseHelper.getInstance(getActivity());
		c = db.getIncome();
		ListAdapter listAdapter = new IncomeAdapter(getActivity(), c);
		this.setListAdapter(listAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.simple_list_fragment, container, false);
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		Toast.makeText(getActivity(), v.getTag(R.id.list_item_income).toString(), Toast.LENGTH_SHORT).show();
	}

	private class IncomeAdapter extends CursorAdapter {
		private LayoutInflater inflater;
		
		public IncomeAdapter(Context context, Cursor cursor) {
			super(context, cursor, CursorAdapter.NO_SELECTION);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			view.setTag(R.id.list_item_income, c.getInt(DatabaseHelper.INCOME_ID));
			
			TextView incomeName = (TextView) view.findViewById(R.id.income_name);
			TextView incomeAmount = (TextView) view.findViewById(R.id.income_amount);
			TextView incomeDate = (TextView) view.findViewById(R.id.income_date);
			
			incomeName.setText(c.getString(DatabaseHelper.INCOME_NAME));
			incomeAmount.setText(Integer.toString(c.getInt(DatabaseHelper.INCOME_AMOUNT)));
			incomeDate.setText(c.getString(DatabaseHelper.INCOME_DATE));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_item_income, null);
		}
	}
}