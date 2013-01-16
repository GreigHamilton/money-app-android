package com.greighamilton.moneymanagement.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.dialogs.ViewIncExpOptionsDialog;

/**
 * @see http://android.artemzin.ru/?p=7
 * 
 */
public class IncomeListFragment extends ListFragment
		implements
		com.greighamilton.moneymanagement.dialogs.ViewIncExpOptionsDialog.OptionsDialogListener {

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
		
		monthReq = now.month;
		yearReq = now.year;
		
		String month = ""+monthReq;
    	if (month.length()<2)
    		month = "0"+month;
    	String year = ""+yearReq;
		
		c = db.getSpecifiedIncome(month, year, allMonths);
		ListAdapter listAdapter = new IncomeAdapter(getActivity(), c);
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
		    	
		    	String month = ""+monthReq;
		    	if (month.length()<2)
		    		month = "0"+month;
		    	String year = ""+yearReq;
				
				c = db.getSpecifiedIncome(month, year, allMonths);
				ListAdapter listAdapter = new IncomeAdapter(getActivity(), c);
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

		    	String month = ""+monthReq;
		    	if (month.length()<2)
		    		month = "0"+month;
		    	String year = ""+yearReq;
				
				c = db.getSpecifiedIncome(month, year, allMonths);
				ListAdapter listAdapter = new IncomeAdapter(getActivity(), c);
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
					   
				String month = ""+monthReq;
		    	if (month.length()<2)
		    		month = "0"+month;
		    	String year = ""+yearReq;
				
				c = db.getSpecifiedIncome(month, year, allMonths);
				ListAdapter listAdapter = new IncomeAdapter(getActivity(), c);
				setListAdapter(listAdapter);
			   }
			});
		
	}
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		showOptionsDialog(v.getTag(R.id.list_item_income).toString());
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
			TextView incomeNotes = (TextView) view.findViewById(R.id.income_notes);

			incomeName.setText(c.getString(DatabaseHelper.INCOME_NAME));
			incomeAmount.setText(Integer.toString(c.getInt(DatabaseHelper.INCOME_AMOUNT)));
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

	private void showOptionsDialog(String id) {
		android.support.v4.app.FragmentManager fm = getFragmentManager();
		ViewIncExpOptionsDialog optionsDialog = new ViewIncExpOptionsDialog(id, "INCOME");
		optionsDialog.show(fm, "fragment_viewincexp_options_dialog");
	}

	@Override
	public void onFinishOptionsDialog(String inputText) {
	}
}