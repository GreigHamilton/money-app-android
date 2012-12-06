package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
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
		Spinner spinner = (Spinner) findViewById(R.id.category);		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.income_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dashboard, menu);
		return true;
	}
	
	public void clickCategory(View v){
		Toast.makeText(this, "Click boaby x", Toast.LENGTH_LONG).show();
	}
	
	public void clickDate(View v){
		Toast.makeText(this, "Click vag x", Toast.LENGTH_LONG).show();
	}
	
	public void clickSave(View v){
		Toast.makeText(this, "Click vag x", Toast.LENGTH_LONG).show();
	}

}
