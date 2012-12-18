package com.greighamilton.moneymanagement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.greighamilton.moneymanagement.AddIncomeActivity.SelectDateFragment;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class AddExpenseActivity extends Activity {

	int day;
	int month;
	int year;

	Spinner expenseSpinner;
	Spinner repetitionSpinner;

	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addexpense);

		db = DatabaseHelper.getInstance(this);

		// Spinner for expense categories
		expenseSpinner = (Spinner) findViewById(R.id.expense_category);
		ArrayAdapter<CharSequence> expenseAdapter = ArrayAdapter
				.createFromResource(this, R.array.expense_array,
						android.R.layout.simple_spinner_item);
		expenseAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expenseSpinner.setAdapter(expenseAdapter);

		// Spinner for repetition period
		repetitionSpinner = (Spinner) findViewById(R.id.expense_repetition_period);
		ArrayAdapter<CharSequence> repetitionAdapter = ArrayAdapter
				.createFromResource(this, R.array.repetition_array,
						android.R.layout.simple_spinner_item);
		repetitionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repetitionSpinner.setAdapter(repetitionAdapter);
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

		case R.id.menu_cancel:
			finish();
			break;

		case R.id.menu_save:
			// Get name data
			String name = ((EditText) findViewById(R.id.expense_name)).getText().toString();

			// Get amount data
			int amount = (Integer.parseInt(((EditText) findViewById(R.id.expense_amount))
							.getText().toString()));

			// Get date after fragment chooser
			String date = day + "/" + month + "/" + year;
			// if the date hasn't been set, sets it to current date
			if (date.equals("0/0/0")) {
				Calendar c = Calendar.getInstance();
				c.getTime();

				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
				date = df.format(c.getTime());
			}

			// Get repetition data
			int repetition_period;
			int repetition_length;
			if (((CheckBox) findViewById(R.id.expense_oneoff_checkbox)).isChecked()) {
				repetition_period = 0;
				repetition_length = 0;
				// TODO Create a notification
			} else {
				repetition_period = expenseSpinner.getSelectedItemPosition() + 1; // add one so one-off is period 0
				repetition_length = (Integer.parseInt(((EditText) findViewById(R.id.expense_repetition_length))
								.getText().toString()));
			}

			// Get notes data
			String notes;
			if (((EditText) findViewById(R.id.expense_notes)).getText().length() == 0)
				notes = "";
			else
				notes = ((EditText) findViewById(R.id.expense_notes)).getText().toString();

			// Get category id data
			int categoryId = expenseSpinner.getSelectedItemPosition();

			// Get data for notification checkbox
			int notification_id;
			if (((CheckBox) findViewById(R.id.expense_notification)).isChecked()) {
				notification_id = 1;
				// TODO Create a notification
			} else {
				notification_id = 0;
			}

			db.addExpense(name, amount, date, repetition_period, repetition_length, notes, categoryId, notification_id);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clickCategory(View v) {
		Toast.makeText(this, "Click category button", Toast.LENGTH_LONG).show();
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
}