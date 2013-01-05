package com.greighamilton.moneymanagement.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.dialogs.ViewIncExpOptionsDialog;

/**
 * @see http://android.artemzin.ru/?p=7
 *
 */
public class ExpenseListFragment extends ListFragment implements com.greighamilton.moneymanagement.dialogs.ViewIncExpOptionsDialog.OptionsDialogListener {

	private DatabaseHelper db;
	private Cursor c;
	
	Spinner monthSpinner;
	Spinner yearSpinner;
	
	private int monthReq;
	private int yearReq;
	private boolean allMonths = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabaseHelper.getInstance(getActivity());
		// start-up only show current month only
		Time now = new Time();
		now.setToNow();
				
		monthReq = 1;
		yearReq = now.year;
				
		c = db.getSpecifiedExpenses(monthReq, yearReq, allMonths);
		ListAdapter listAdapter = new ExpenseAdapter(getActivity(), c);
		this.setListAdapter(listAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.simple_list_fragment_incexp,
				container, false);
		setSpinnerContent(view);
		return view;
	}
	
	private void setSpinnerContent(View view) {
		
		// get current month and year
				Time now = new Time();
				now.setToNow();

				int month = now.month;
				int year = now.year;
				String yearText = "" + year;

				// Spinner for months
				monthSpinner = (Spinner) view.findViewById(R.id.incexp_month);
				List<String> months = new ArrayList<String>();
				months.add("Jan"); months.add("Feb"); months.add("Mar"); months.add("Apr");
				months.add("May"); months.add("Jun"); months.add("Jul"); months.add("Aug");
				months.add("Sep"); months.add("Oct"); months.add("Nov"); months.add("Dec");

				ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
				monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				monthSpinner.setAdapter(monthAdapter);
				monthSpinner.setSelection(month);
				monthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				    @Override
				    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				        
				    	monthReq = position+1;
				    	
				    	c = db.getSpecifiedExpenses(monthReq, yearReq, allMonths);
						ListAdapter listAdapter = new ExpenseAdapter(getActivity(), c);
						setListAdapter(listAdapter);
				    }

				    @Override
				    public void onNothingSelected(AdapterView<?> parentView) {
				    }

				});

				// Spinner for months
				yearSpinner = (Spinner) view.findViewById(R.id.incexp_year);
				final List<String> years = new ArrayList<String>();
				years.add("2012"); years.add("2013"); years.add("2014"); years.add("2015");
				ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
				yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				yearSpinner.setAdapter(yearAdapter);
				yearSpinner.setSelection(years.indexOf(yearText));
				yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				    @Override
				    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				        
				    	yearReq = Integer.parseInt(years.get(position));

				    	c = db.getSpecifiedExpenses(monthReq, yearReq, allMonths);
						ListAdapter listAdapter = new ExpenseAdapter(getActivity(), c);
						setListAdapter(listAdapter);
				    }

				    @Override
				    public void onNothingSelected(AdapterView<?> parentView) {
				    }

				});
				
				final CheckBox selectAll = (CheckBox) view.findViewById(R.id.incexp_all);
				selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
						if (selectAll.isChecked())
							allMonths=true;
						else
							allMonths=false;
							   
						c = db.getSpecifiedExpenses(monthReq, yearReq, allMonths);
						ListAdapter listAdapter = new ExpenseAdapter(getActivity(), c);
						setListAdapter(listAdapter);
					   }
					});
				
			}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		showOptionsDialog(v.getTag(R.id.list_item_expense).toString());
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
			TextView expenseNotes = (TextView) view.findViewById(R.id.expense_notes);
			//TextView expenseNotId = (TextView) view.findViewById(R.id.expense_notification);
			
			expenseName.setText(c.getString(DatabaseHelper.EXPENSE_NAME));
			expenseAmount.setText(Integer.toString(c.getInt(DatabaseHelper.EXPENSE_AMOUNT)));
			expenseDate.setText(c.getString(DatabaseHelper.EXPENSE_DATE));
			expenseNotes.setText(c.getString(DatabaseHelper.EXPENSE_NOTES));
			//expenseNotId.setText(Integer.toString(c.getInt(DatabaseHelper.EXPENSE_NOTIFICATION_ID)));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_item_expense, null);
		}
	}

	private void showOptionsDialog(String id) {
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        ViewIncExpOptionsDialog optionsDialog = new ViewIncExpOptionsDialog(id, "EXPENSE");
        optionsDialog.show(fm, "fragment_viewincexp_options_dialog");
    }

	@Override
	public void onFinishOptionsDialog(String inputText) {
		// TODO Auto-generated method stub
		
	}
}