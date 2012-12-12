package com.greighamilton.moneymanagement;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DashboardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_dashboard, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
      case R.id.menu_addincome:
    	  	Intent i = new Intent(DashboardActivity.this, AddIncomeActivity.class);
    	  	DashboardActivity.this.startActivity(i);
        break;
        
      case R.id.menu_addexpense:
  	  	Intent j = new Intent(DashboardActivity.this, AddExpenseActivity.class);
  	  	DashboardActivity.this.startActivity(j);
      break;
      
      case R.id.menu_viewgoals:
    	  	Intent k = new Intent(DashboardActivity.this, ViewGoalsActivity.class);
    	  	DashboardActivity.this.startActivity(k);
        break;
        
      case R.id.menu_viewcategories:
    	  	Intent l = new Intent(DashboardActivity.this, ViewCategoriesActivity.class);
    	  	DashboardActivity.this.startActivity(l);
        break;
        
      case R.id.menu_summary:
  	  	Intent m = new Intent(DashboardActivity.this, SummaryActivity.class);
  	  	DashboardActivity.this.startActivity(m);
      break;
      
      case R.id.menu_viewtrends:
    	  	Intent n = new Intent(DashboardActivity.this, ViewTrendsActivity.class);
    	  	DashboardActivity.this.startActivity(n);
        break;
      }
      return super.onOptionsItemSelected(item);
    }

}
