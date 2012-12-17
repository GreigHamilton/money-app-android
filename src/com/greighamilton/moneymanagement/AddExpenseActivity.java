package com.greighamilton.moneymanagement;

import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.fragments.DatePickerFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExpenseActivity extends Activity {
	
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addexpense);
		
		db = DatabaseHelper.getInstance(this);
		
		// Find Spinner in View
		Spinner spinner = (Spinner) findViewById(R.id.expense_category);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.expense_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		Spinner repetitionSpinner = (Spinner) findViewById(R.id.expense_repetition_spinner);
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
	    	  String name = ((EditText) findViewById(R.id.expense_name)).getText().toString();
	    	  db.addExpense(name);
	    	  finish();
	      break;
	      }
	      return super.onOptionsItemSelected(item);
	    }
	
	public void clickCategory(View v){
		Toast.makeText(this, "Click category button", Toast.LENGTH_LONG).show();
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void clickSave(View v){
		Toast.makeText(this, "Click vag x", Toast.LENGTH_LONG).show();
	}

}
