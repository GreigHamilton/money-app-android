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
public class ExpenseListFragment extends ListFragment {

	private DatabaseHelper db;
	private Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabaseHelper.getInstance(getActivity());
		c = db.getExpenses();
		ListAdapter listAdapter = new ExpenseAdapter(getActivity(), c);
		this.setListAdapter(listAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.simple_list_fragment, container, false);
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		Toast.makeText(getActivity(), v.getTag(R.id.list_item_expense).toString(), Toast.LENGTH_SHORT).show();
	}

	private class ExpenseAdapter extends CursorAdapter {
		private LayoutInflater inflater;
		
		public ExpenseAdapter(Context context, Cursor cursor) {
			super(context, cursor, CursorAdapter.NO_SELECTION);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			view.setTag(R.id.list_item_expense, c.getInt(DatabaseHelper.EXPENSE_ID));
			
			TextView expenseName = (TextView) view.findViewById(R.id.expense_name);
			TextView expenseAmount = (TextView) view.findViewById(R.id.expense_amount);
			TextView expenseDate = (TextView) view.findViewById(R.id.expense_date);
			//TextView expenseRepLength = (TextView) view.findViewById(R.id.expense_repetition_length);
			TextView expenseNotes = (TextView) view.findViewById(R.id.expense_notes);
			TextView expenseNotId = (TextView) view.findViewById(R.id.expense_notification);
			
			expenseName.setText(c.getString(DatabaseHelper.EXPENSE_NAME));
			expenseAmount.setText(Integer.toString(c.getInt(DatabaseHelper.EXPENSE_AMOUNT)));
			expenseDate.setText(c.getString(DatabaseHelper.EXPENSE_DATE));
			// expenseRepLength.setText(Integer.toString(c.getInt(DatabaseHelper.EXPENSE_REPETITION_LENGTH)));
			expenseNotes.setText(c.getString(DatabaseHelper.EXPENSE_NOTES));
			expenseNotId.setText(Integer.toString(c.getInt(DatabaseHelper.EXPENSE_NOTIFICATION_ID)));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_item_expense, null);
		}
	}
}