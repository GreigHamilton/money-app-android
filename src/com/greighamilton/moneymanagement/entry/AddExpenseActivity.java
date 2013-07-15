package com.greighamilton.moneymanagement.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.util.Util;

/**
 * Class for add expense activity.
 * 
 * @author Greig Hamilton
 *
 */
public class AddExpenseActivity extends Activity implements	OnItemSelectedListener {
	
	private static final int ONE_0FF = 0;
	private static final int WEEK = 1;
	private static final int MONTH = 2;
	private static final int YEAR = 3;
	private static final int WEEKBI = 4;
    private static final int WEEK4 = 5;
	
	private static final int SERIES = 4;

	int day;
	int month;
	int year;

	Spinner expenseSpinner;
	Spinner repetitionSpinner;
	List<String> expenseCategories;
	List<Integer> expenseCategoryIDs;
	
	CheckBox oneOff;
	EditText repLength;
	TextView repText;
	Spinner repPeriod;

	private DatabaseHelper db;
	private Cursor c;
	private Bundle extras;
	private String currentId;

    private String currencySymbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addexpense);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        currencySymbol = sp.getString("CURRENCYSYMBOL", "");
	}
	
	@Override
	protected void onResume() {
	    super.onResume();

		db = DatabaseHelper.getInstance(this);
		
		day = Util.getTodaysDay();
		month = Util.getTodaysMonth();
		year = Util.getTodaysYear();
		
		// Spinner for expense categories
		expenseSpinner = (Spinner) findViewById(R.id.expense_category);
		expenseCategories = db.getExpenseCategoryList();
		expenseCategoryIDs = db.getExpenseCategoryIDList();
		
		ArrayAdapter<String> expenseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseCategories);
		expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expenseSpinner.setAdapter(expenseAdapter);
		if (expenseCategories.isEmpty()) expenseSpinner.setEnabled(false);
		
		// Spinner for repetition period
		repetitionSpinner = (Spinner) findViewById(R.id.expense_repetition_period);
		ArrayAdapter<CharSequence> repetitionAdapter = ArrayAdapter.createFromResource(this, R.array.repetition_array, android.R.layout.simple_spinner_item);
		repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repetitionSpinner.setAdapter(repetitionAdapter);
		repetitionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int selection, long arg3) {
				TextView repPeriodText = (TextView) findViewById(R.id.expense_repetition_period_text);
				switch (selection) {
					case 0: repPeriodText.setText("weeks."); break;
					case 1: repPeriodText.setText("months."); break;
					case 2: repPeriodText.setText("years."); break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// nothing
			}
        });
		
		Button button = (Button) findViewById(R.id.expense_date);		
		
		oneOff = (CheckBox) findViewById(R.id.expense_oneoff_checkbox);
		repLength = (EditText) findViewById(R.id.expense_repetition_length);
		repText = (TextView) findViewById(R.id.expense_repetition_text);
		repPeriod = (Spinner) findViewById(R.id.expense_repetition_period);
		
		oneOff.setChecked(true);
		this.clickCheckbox(null);
		
		extras = getIntent().getExtras();
		
		if (extras != null) {
		    currentId = extras.getString("CURRENT_ID");
		    c = db.getExpenseId(currentId);
		    c.moveToFirst();
		    TextView expenseName = (TextView) findViewById(R.id.expense_name);
		    String name = c.getString(DatabaseHelper.EXPENSE_NAME);
		    expenseName.append(name);
		    TextView expenseAmount = (TextView) findViewById(R.id.expense_amount);
		    String amount = c.getString(DatabaseHelper.EXPENSE_AMOUNT);
		    expenseAmount.append(amount);
		    //TextView expenseCategory = (TextView) findViewById(R.id.expense_category);
		    //expenseCategory.append(c.getString(DatabaseHelper.INCOME_AMOUNT));		    

			this.setTitle("Edit Expense: "+name+" (�"+amount+")");
		    
		    button.setText(c.getString(DatabaseHelper.INCOME_DATE));
		    String date = c.getString(DatabaseHelper.INCOME_DATE);
		    
		    day = Util.getDayFromString(date);
		    month = Util.getMonthFromString(date);
		    year = Util.getYearFromString(date);
		    
		    int category = c.getInt(DatabaseHelper.EXPENSE_CATEGORY_ID);
		    int index = expenseCategoryIDs.indexOf((Integer) category);
		    expenseSpinner.setSelection((index >= 0 && index < expenseCategories.size()) ? index : 0);
		    
		    
		    if (c.getString(DatabaseHelper.EXPENSE_REPETITION_PERIOD).equals("0")) {
		    	oneOff.setChecked(true);
		    	repLength.setEnabled(false);
				repText.setEnabled(false);
				repPeriod.setEnabled(false);
		    } else if (c.getString(DatabaseHelper.EXPENSE_REPETITION_PERIOD).equals("4")) {
		    	oneOff.setVisibility(View.GONE);
		    	repLength.setVisibility(View.GONE);
				repPeriod.setVisibility(View.GONE);
				repText.setEnabled(true);
				repText.setText("Part of a series");
		    } else {
		    	TextView expenseRepLen = (TextView) findViewById(R.id.expense_repetition_length);
		    	expenseRepLen.setText(c.getString(DatabaseHelper.EXPENSE_REPETITION_LENGTH));
		    	repetitionSpinner.setSelection(Integer.parseInt(c.getString(DatabaseHelper.EXPENSE_REPETITION_PERIOD))-1);  // add one so one-off is period 0
		    	oneOff.setChecked(false);
		    	repLength.setEnabled(true);
				repText.setEnabled(true);
				repPeriod.setEnabled(true);
		    }
		}
		else {
			Calendar cal = Calendar.getInstance();
			cal.getTime();

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
			String date = df.format(cal.getTime());
			button.setText(date);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_addexpense, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.expense_menu_cancel:
			finish();
			break;

		case R.id.expense_menu_save:

			EditText nameBox = (EditText) findViewById(R.id.expense_name);
			EditText amountBox = (EditText) findViewById(R.id.expense_amount);
			EditText repBox = (EditText) findViewById(R.id.expense_repetition_length);

			if (nameBox.getText().toString().length() > 0
					&& amountBox.getText().toString().length() > 0
					&& ((oneOff.isChecked()) || repBox.getText().toString().length() > 0)
					&& expenseCategories.size() > 0) {

				// Get name data
				String name = ((EditText) findViewById(R.id.expense_name))
						.getText().toString();

				// Get amount data
				float amount = (Float
						.parseFloat(((EditText) findViewById(R.id.expense_amount))
								.getText().toString()));

				// Get date		
				String date = Util.makeDateString(day, month, year);

				// Get repetition data
				int repetition_period;
				int repetition_length;
				if (oneOff.isChecked()) {
					repetition_period = 0;
					repetition_length = 0;
				} else {
					repetition_period = repetitionSpinner.getSelectedItemPosition() + 1; // add one so one-off is period 0
					repetition_length = (Integer
							.parseInt(((EditText) findViewById(R.id.expense_repetition_length))
									.getText().toString()));
				}

				// Get notes data
				String notes;
				if (((EditText) findViewById(R.id.expense_notes)).getText()
						.length() == 0)
					notes = "";
				else
					notes = ((EditText) findViewById(R.id.expense_notes))
							.getText().toString();

				// Get category id data
				int categoryId = expenseCategoryIDs.get(expenseSpinner.getSelectedItemPosition());

				// Get data for notification checkbox
				int notification_id = 0;
					
				boolean reAdd = false;
				
				// Edit (not Add)
				if (extras != null) {
					
					boolean repetitionPeriodChanged = db.getExpenseRepetitionPeriod(currentId) != repetition_period;
					boolean repetitionLengthChanged = db.getExpenseRepetitionLength(currentId) != repetition_length;
					
					if (repetitionPeriodChanged || repetitionLengthChanged) {
						db.deleteExpenseSeries(currentId);
						reAdd = true; // we need to delete the series, and re-add this entry
					} else {
						db.updateExpense(currentId, name, amount, date, repetition_period,
								repetition_length, notes, categoryId, notification_id);
					}
				}
				
				// Add
				if (extras == null || reAdd) {
					
					// Add to db
					int seriesID = db.addExpense(name, amount, date, repetition_period,
							repetition_length, notes, categoryId, notification_id);

					// Series
					if (repetition_length != ONE_0FF) {
						for (int i=0; i<repetition_length; i++) {
							switch (repetition_period) {
								case WEEK :		date = Util.addWeeksToDate(date, 1); break;
								case MONTH : 	date = Util.addMonthsToDate(date, 1); break;
								case YEAR :		date = Util.addYearsToDate(date, 1); break;
								case WEEKBI :	date = Util.addWeeksToDate(date, 2); break;
								case WEEK4 :	date = Util.addWeeksToDate(date, 4); break;
								default : break;
							}
							db.addExpense(name, amount, date, SERIES, seriesID,
									notes, categoryId, notification_id);
						}
					}
				}				
				finish();
			}

			else {

				if (nameBox.getText().toString().length() == 0)
					nameBox.setError("Name is required.");
				if (amountBox.getText().toString().length() == 0)
					amountBox.setError("Amount is required.");
				if (!oneOff.isChecked()	&& repBox.getText().toString().length() == 0)
					Toast.makeText(this, "A Repetition Must Be Set.",
							Toast.LENGTH_SHORT).show();
				if (expenseCategories.size() == 0)
					Toast.makeText(this, "Please add a category.",
							Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addCategory(View v) {
		Intent i = new Intent(AddExpenseActivity.this, AddCategoryActivity.class);
		i.setFlags(1);
		AddExpenseActivity.this.startActivity(i);
	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day) {
		// Add selected date text to button
		Button button = (Button) findViewById(R.id.expense_date);
		button.setText(day + "/" + month + "/" + year);
	}

	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			year = yy;
			month = mm + 1;
			day = dd;
			populateSetDate(yy, mm + 1, dd);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void clickCheckbox(View v) {
		ViewGroup repetitionView1 = (ViewGroup) findViewById(R.id.expense_repetition_1);
		ViewGroup repetitionView2 = (ViewGroup) findViewById(R.id.expense_repetition_2);
		
		for (int i=0; i<repetitionView1.getChildCount(); i++) {
			repetitionView1.getChildAt(i).setEnabled(!oneOff.isChecked());
		}
		
		for (int i=0; i<repetitionView2.getChildCount(); i++) {
			repetitionView2.getChildAt(i).setEnabled(!oneOff.isChecked());
		}
	}
}