package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddIncomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addincome);
		
		// Find Spinner in View
		Spinner incomeSpinner = (Spinner) findViewById(R.id.category);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> incomeAdapter = ArrayAdapter.createFromResource(this,
		        R.array.income_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		incomeSpinner.setAdapter(incomeAdapter);
		
		Spinner repetitionSpinner = (Spinner) findViewById(R.id.repetition_spinner);
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
      }
      return super.onOptionsItemSelected(item);
    }
	
	public void clickCategory(View v){
		Toast.makeText(this, "Click boaby x", Toast.LENGTH_LONG).show();
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}

}