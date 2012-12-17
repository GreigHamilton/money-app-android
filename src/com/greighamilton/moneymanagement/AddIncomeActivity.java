package com.greighamilton.moneymanagement;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class AddIncomeActivity extends FragmentActivity {
	
	int day;
	int month;
	int year;
	
    DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addincome);
		
		db = DatabaseHelper.getInstance(this);
		
		// Find Spinner in View
		Spinner incomeSpinner = (Spinner) findViewById(R.id.income_category);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> incomeAdapter = ArrayAdapter.createFromResource(this,
		        R.array.income_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		incomeSpinner.setAdapter(incomeAdapter);
		
		Spinner repetitionSpinner = (Spinner) findViewById(R.id.income_repetition_period);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> repetitionAdapter = ArrayAdapter.createFromResource(this,
		        R.array.repetition_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		repetitionSpinner.setAdapter(repetitionAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_addincome, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
      case R.id.menu_cancel:
    	  	finish();
      break;
      
      case R.id.menu_save:
    	  String name = ((EditText) findViewById(R.id.income_name)).getText().toString();
    	  int amount = (Integer.parseInt(((EditText) findViewById(R.id.income_amount)).getText().toString()));
    	  // Set date after fragment chooser
    	  String date = day+"/"+month+"/"+year;
    	  // TODO REPETITION PERIOD
    	  // Get repetition length data
    	  //int repetition_length = (Integer.parseInt(((EditText) findViewById(R.id.income_repetition_length)).getText().toString()));
    	  // Get notes data
    	  String notes = ((EditText) findViewById(R.id.income_notes)).getText().toString();
    	  // TODO CATEGORY ID
    	  // Get data for one-off checkbox
    	  int notification_id;
          if (((CheckBox) findViewById(R.id.income_notification)).isChecked()) {
        	  notification_id = 1;
              // TODO Create a notification
          }
          else {
        	  notification_id = 0;
          }
    	  db.addIncome(name, amount, date, notes, notification_id);
    	  finish();
      break;
      }
      return super.onOptionsItemSelected(item);
    }
	
	public void clickCategory(View v){
		Toast.makeText(this, "Click category button", Toast.LENGTH_LONG).show();
	}
	
	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void populateSetDate(int year, int month, int day) {
		// Add selected date text to button
		Button button = (Button) findViewById(R.id.income_date);
		button.setText(day+"/"+month+"/"+year);
	}
	
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
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
			month = mm;
			day = dd;
			populateSetDate(yy, mm+1, dd);
		}
	}	 
}


