package com.greighamilton.moneymanagement.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class AddIncomeActivity extends Activity implements
		OnItemSelectedListener {
	
	private static final int ONE_0FF = 0;
	private static final int WEEK = 1;
	private static final int MONTH = 2;
	private static final int YEAR = 3;
	private static final int SERIES = 4;

	private int day;
	private int month;
	private int year;

	private Spinner incomeSpinner;
	private Spinner repetitionSpinner;
	private List<String> incomeCategories;
	private List<Integer> incomeCategoryIDs;
	
	private CheckBox oneOff;
	private EditText repLength;
	private TextView repText;
	private Spinner repPeriod;

	private DatabaseHelper db;
	private Cursor c;
	private Bundle extras;
	private String currentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addincome);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
		db = DatabaseHelper.getInstance(this);
		
		day = Util.getTodaysDay();
		month = Util.getTodaysMonth();
		year = Util.getTodaysYear();
		
		// Spinner for income categories
		incomeSpinner = (Spinner) findViewById(R.id.income_category);
		incomeCategories = db.getIncomeCategoryList();
		incomeCategoryIDs = db.getIncomeCategoryIDList();

		ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(this,	android.R.layout.simple_spinner_item, incomeCategories);
		incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		incomeSpinner.setAdapter(incomeAdapter);
		if (incomeCategories.isEmpty())	incomeSpinner.setEnabled(false);
		// Spinner for repetition period
		repetitionSpinner = (Spinner) findViewById(R.id.income_repetition_period);
		ArrayAdapter<CharSequence> repetitionAdapter = ArrayAdapter
				.createFromResource(this, R.array.repetition_array,
						android.R.layout.simple_spinner_item);
		repetitionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repetitionSpinner.setAdapter(repetitionAdapter);
		
		Button button = (Button) findViewById(R.id.income_date);
		
		oneOff = (CheckBox) findViewById(R.id.income_oneoff_checkbox);
		repLength = (EditText) findViewById(R.id.income_repetition_length);
		repText = (TextView) findViewById(R.id.income_repetition_text);
		repPeriod = (Spinner) findViewById(R.id.income_repetition_period);
		
		oneOff.setChecked(true);
		repLength.setEnabled(false);
		repText.setEnabled(false);
		repPeriod.setEnabled(false);
		
		extras = getIntent().getExtras();
		
		if (extras != null) {
		    currentId = extras.getString("CURRENT_ID");
		    c = db.getIncomeId(currentId);
		    c.moveToFirst();
		    TextView incomeName = (TextView) findViewById(R.id.income_name);
		    String name = c.getString(DatabaseHelper.INCOME_NAME);
		    incomeName.setText(name);
		    TextView incomeAmount = (TextView) findViewById(R.id.income_amount);
		    String amount = c.getString(DatabaseHelper.INCOME_AMOUNT);
		    incomeAmount.setText(amount);
		    //TextView incomeCategory = (TextView) findViewById(R.id.income_category);
		    //incomeCategory.append(c.getString(DatabaseHelper.INCOME_AMOUNT));		    

			this.setTitle("Edit Income: "+name+" (£"+amount+")");
		    
		    button.setText(c.getString(DatabaseHelper.INCOME_DATE));
		    String date = c.getString(DatabaseHelper.INCOME_DATE);
		    
		    day = Util.getDayFromString(date);
		    month = Util.getMonthFromString(date);
		    year = Util.getYearFromString(date);
		    
		    int category = c.getInt(DatabaseHelper.INCOME_CATEGORY_ID);
		    int index = incomeCategoryIDs.indexOf((Integer) category);
		    incomeSpinner.setSelection((index >= 0 && index < incomeCategories.size()) ? index : 0);
		    
		    if (c.getString(DatabaseHelper.INCOME_REPETITION_PERIOD).equals("0")) {
		    	oneOff.setChecked(true);
		    	repLength.setEnabled(false);
				repText.setEnabled(false);
				repPeriod.setEnabled(false);
		    } else if (c.getString(DatabaseHelper.INCOME_REPETITION_PERIOD).equals("4")) {
		    	oneOff.setVisibility(View.GONE);
		    	repLength.setVisibility(View.GONE);
				repPeriod.setVisibility(View.GONE);
				repText.setEnabled(true);
				repText.setText("Part of a series");
		    }
		    else {
		    	TextView incomeRepLen = (TextView) findViewById(R.id.income_repetition_length);
		    	incomeRepLen.append(c.getString(DatabaseHelper.INCOME_REPETITION_LENGTH));
		    	repetitionSpinner.setSelection(Integer.parseInt(c.getString(DatabaseHelper.INCOME_REPETITION_PERIOD))-1);
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
		getMenuInflater().inflate(R.menu.activity_addincome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.income_menu_cancel:
			finish();
			break;

		case R.id.income_menu_save:

			EditText nameBox = (EditText) findViewById(R.id.income_name);
			EditText amountBox = (EditText) findViewById(R.id.income_amount);
			EditText repBox = (EditText) findViewById(R.id.income_repetition_length);

			if (nameBox.getText().toString().length() > 0
					&& amountBox.getText().toString().length() > 0
					&& (oneOff.isChecked() || repBox.getText().toString().length() > 0)
					&& incomeCategories.size() > 0) {

				// Get name data
				String name = ((EditText) findViewById(R.id.income_name))
						.getText().toString();

				// Get amount data
				float amount = (Float
						.parseFloat(((EditText) findViewById(R.id.income_amount))
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
							.parseInt(((EditText) findViewById(R.id.income_repetition_length))
									.getText().toString()));
				}

				// Get notes data
				String notes;
				if (((EditText) findViewById(R.id.income_notes)).getText()
						.length() == 0)
					notes = "";
				else
					notes = ((EditText) findViewById(R.id.income_notes))
							.getText().toString();

				// Get category id data
				int categoryId = incomeCategoryIDs.get(incomeSpinner.getSelectedItemPosition());

				// Get data for notification checkbox
				int notification_id;
//				if (((CheckBox) findViewById(R.id.income_notification))
//						.isChecked()) {
//					notification_id = 1;
//					// TODO Create a notification
//				} else {
					notification_id = 0;
//				}
					
				boolean reAdd = false;
				
				// Edit (not Add)
				if (extras != null) {
					
					boolean repetitionPeriodChanged = db.getIncomeRepetitionPeriod(currentId) != repetition_period;
					boolean repetitionLengthChanged = db.getIncomeRepetitionLength(currentId) != repetition_length;
					
					if (repetitionPeriodChanged || repetitionLengthChanged) {
						db.deleteIncomeSeries(currentId);
						reAdd = true; // we need to delete the series, and re-add this entry
					} else {
						db.updateIncome(currentId, name, amount, date, repetition_period,
								repetition_length, notes, categoryId, notification_id);
					}
				}
				
				// Add
				if (extras == null || reAdd) {
					
					// Add to db
					int seriesID = db.addIncome(name, amount, date, repetition_period,
							repetition_length, notes, categoryId, notification_id);
					
					// Series
					if (repetition_length != ONE_0FF) {
						for (int i=0; i<repetition_length; i++) {
							switch (repetition_period) {
								case WEEK :	date = Util.addWeeksToDate(date, 1); break;
								case MONTH : date = Util.addMonthsToDate(date, 1); break;
								case YEAR :	date = Util.addYearsToDate(date, 1); break;
								default : break;
							}
							db.addIncome(name, amount, date, SERIES, seriesID,
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
				if (!(((CheckBox) findViewById(R.id.income_oneoff_checkbox))
						.isChecked())
						&& repBox.getText().toString().length() == 0)
					Toast.makeText(this, "A Repetition Must Be Set.",
							Toast.LENGTH_SHORT).show();
				if (incomeCategories.size() == 0)
					Toast.makeText(this, "Please add a category.",
							Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addCategory(View v) {
		Intent i = new Intent(AddIncomeActivity.this, AddCategoryActivity.class);
		AddIncomeActivity.this.startActivity(i);
	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day) {
		// Add selected date text to button
		Button button = (Button) findViewById(R.id.income_date);
		button.setText(day + "/" + month + "/" + year);
	}

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
		repLength.setEnabled(!oneOff.isChecked());
		repText.setEnabled(!oneOff.isChecked());
		repPeriod.setEnabled(!oneOff.isChecked());
	}
}