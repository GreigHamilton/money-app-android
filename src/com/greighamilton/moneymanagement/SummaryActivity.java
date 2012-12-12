package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		getMenuInflater().inflate(R.menu.activity_summary, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
      case R.id.menu_addincome:
    	  	Intent i = new Intent(SummaryActivity.this, AddIncomeActivity.class);
    	  	SummaryActivity.this.startActivity(i);
        break;
        
      case R.id.menu_addexpense:
  	  	Intent j = new Intent(SummaryActivity.this, AddExpenseActivity.class);
  	  SummaryActivity.this.startActivity(j);
      break;
      
      case R.id.menu_viewtrends:
    	  Intent l = new Intent(SummaryActivity.this, ViewTrendsActivity.class);
    	  SummaryActivity.this.startActivity(l);
        break;
      }
      return super.onOptionsItemSelected(item);
    }
}
