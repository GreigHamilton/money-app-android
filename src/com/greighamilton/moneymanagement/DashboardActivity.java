package com.greighamilton.moneymanagement;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
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
		getMenuInflater().inflate(R.menu.activity_dashboard, menu);
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
      
      case R.id.menu_addgoal:
    	  	Intent k = new Intent(DashboardActivity.this, AddGoalActivity.class);
    	  	DashboardActivity.this.startActivity(k);
        break;
        
      case R.id.menu_addcategory:
    	  	Intent l = new Intent(DashboardActivity.this, AddCategoryActivity.class);
    	  	DashboardActivity.this.startActivity(l);
        break;
      }
      return super.onOptionsItemSelected(item);
    }

}
