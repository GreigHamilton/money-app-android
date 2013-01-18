package com.greighamilton.moneymanagement.utilities;

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

public class AddIncomeActivity extends Activity implements
		OnItemSelectedListener {

	int day;
	int month;
	int year;

	Spinner incomeSpinner;
	Spinner repetitionSpinner;
	List<String> incomeCategories;

	private DatabaseHelper db;
	private Cursor c;
	private Bundle extras;
	private String currentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addincome);

		db = DatabaseHelper.getInstance(this);
		
		// Spinner for income categories
		incomeSpinner = (Spinner) findViewById(R.id.income_category);
		incomeCategories = db.getIncomeCategoryList();

		ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, incomeCategories);
		incomeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		incomeSpinner.setAdapter(incomeAdapter);
		if (incomeCategories.isEmpty())
			incomeSpinner.setEnabled(false);
		// Spinner for repetition period
		repetitionSpinner = (Spinner) findViewById(R.id.income_repetition_period);
		ArrayAdapter<CharSequence> repetitionAdapter = ArrayAdapter
				.createFromResource(this, R.array.repetition_array,
						android.R.layout.simple_spinner_item);
		repetitionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repetitionSpinner.setAdapter(repetitionAdapter);
		
		Button button = (Button) findViewById(R.id.income_date);
		
		extras = getIntent().getExtras();
		
		if (extras != null) {
		    currentId = extras.getString("CURRENT_ID");
		    c = db.getIncomeId(currentId);
		    c.moveToFirst();
		    TextView incomeName = (TextView) findViewById(R.id.income_name);
		    incomeName.append(c.getString(DatabaseHelper.INCOME_NAME));
		    TextView incomeAmount = (TextView) findViewById(R.id.income_amount);
		    incomeAmount.append(c.getString(DatabaseHelper.INCOME_AMOUNT));
		    //TextView incomeCategory = (TextView) findViewById(R.id.income_category);
		    //incomeCategory.append(c.getString(DatabaseHelper.INCOME_AMOUNT));
		    
		    button.setText(c.getString(DatabaseHelper.INCOME_DATE));
		    String date = c.getString(DatabaseHelper.INCOME_DATE);
		    
		    day = Integer.parseInt(date.substring(0, 2));
		    month = Integer.parseInt(date.substring(3, 5));
		    year = Integer.parseInt(date.substring(6));
		    
		    incomeSpinner.setSelection(Integer.parseInt(c.getString(DatabaseHelper.INCOME_CATEGORY_ID)));
		    
		    if (c.getString(DatabaseHelper.INCOME_REPETITION_PERIOD).equals("0")) {
		    	CheckBox oneoff = (CheckBox) findViewById(R.id.income_oneoff_checkbox);
		    	oneoff.setChecked(!oneoff.isChecked());
		    	
		    	EditText repLength = (EditText) findViewById(R.id.income_repetition_length);
				TextView repText = (TextView) findViewById(R.id.income_repetition_text);
				Spinner repPeriod = (Spinner) findViewById(R.id.income_repetition_period);
				
		    	repLength.setEnabled(false);
				repText.setEnabled(false);
				repPeriod.setEnabled(false);
		    }
		    else {
		    	TextView incomeRepLen = (TextView) findViewById(R.id.income_repetition_length);
		    	incomeRepLen.append(c.getString(DatabaseHelper.INCOME_REPETITION_LENGTH));
		    	repetitionSpinner.setSelection(Integer.parseInt(c.getString(DatabaseHelper.INCOME_REPETITION_PERIOD)));
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
					&& ((((CheckBox) findViewById(R.id.income_oneoff_checkbox))
							.isChecked()) || repBox.getText().toString()
							.length() > 0) && incomeCategories.size() > 0) {

				// Get name data
				String name = ((EditText) findViewById(R.id.income_name))
						.getText().toString();

				// Get amount data
				int amount = (Integer
						.parseInt(((EditText) findViewById(R.id.income_amount))
								.getText().toString()));

				// Get date after fragment chooser
				String dayNo = ""+day;
				if (dayNo.length()<2)
					dayNo="0"+day;
				String monthNo = ""+month;
				if (monthNo.length()<2)
					monthNo="0"+month;
				String date = dayNo + "/" + monthNo + "/" + year;
				// if the date hasn't been set, sets it to current date
				if (date.equals("00/00/0")) {
					Calendar c = Calendar.getInstance();
					c.getTime();

					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",
							Locale.UK);
					date = df.format(c.getTime());
				}

				// Get repetition data
				int repetition_period;
				int repetition_length;
				if (((CheckBox) findViewById(R.id.income_oneoff_checkbox))
						.isChecked()) {
					repetition_period = 0;
					repetition_length = 0;
				} else {
					repetition_period = incomeSpinner.getSelectedItemPosition() + 1; // add one so one-off is period 0
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
				int categoryId = incomeSpinner.getSelectedItemPosition();

				// Get data for notification checkbox
				int notification_id;
				if (((CheckBox) findViewById(R.id.income_notification))
						.isChecked()) {
					notification_id = 1;
					// TODO Create a notification
				} else {
					notification_id = 0;
				}
				
				if (extras != null) {
					db.updateIncome(currentId, name, amount, date, repetition_period,
							repetition_length, notes, categoryId, notification_id);
				}
				else {
					db.addIncome(name, amount, date, repetition_period,
							repetition_length, notes, categoryId, notification_id);
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

		EditText repLength = (EditText) findViewById(R.id.income_repetition_length);
		TextView repText = (TextView) findViewById(R.id.income_repetition_text);
		Spinner repPeriod = (Spinner) findViewById(R.id.income_repetition_period);

		if (((CheckBox) findViewById(R.id.income_oneoff_checkbox)).isChecked()) {
			repLength.setEnabled(false);
			repText.setEnabled(false);
			repPeriod.setEnabled(false);
		} else {
			repLength.setEnabled(true);
			repText.setEnabled(true);
			repPeriod.setEnabled(true);
		}
	}
}