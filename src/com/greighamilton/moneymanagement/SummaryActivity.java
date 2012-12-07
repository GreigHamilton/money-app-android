package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dashboard, menu);
		return true;
	}
	
	public void clickIncome1(View v){
		Toast.makeText(this, "Click income 1", Toast.LENGTH_LONG).show();
	}
	
	public void clickIncome2(View v){
		Toast.makeText(this, "Click income 2", Toast.LENGTH_LONG).show();
	}
	
	public void clickExpense1(View v){
		Toast.makeText(this, "Click expense 1", Toast.LENGTH_LONG).show();
	}
	
	public void clickExpense2(View v){
		Toast.makeText(this, "Click expense 2", Toast.LENGTH_LONG).show();
	}
}
