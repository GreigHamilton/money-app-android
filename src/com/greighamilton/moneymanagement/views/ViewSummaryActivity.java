package com.greighamilton.moneymanagement.views;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.R.id;
import com.greighamilton.moneymanagement.R.layout;
import com.greighamilton.moneymanagement.R.menu;
import com.greighamilton.moneymanagement.utilities.AddExpenseActivity;
import com.greighamilton.moneymanagement.utilities.AddIncomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ViewSummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewsummary);
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
      
      case R.id.viewsummary_menu_addincome:
    	  	Intent i = new Intent(ViewSummaryActivity.this, AddIncomeActivity.class);
    	  	ViewSummaryActivity.this.startActivity(i);
        break;
        
      case R.id.viewsummary_menu_addexpense:
  	  	Intent j = new Intent(ViewSummaryActivity.this, AddExpenseActivity.class);
  	  ViewSummaryActivity.this.startActivity(j);
      break;
      
      case R.id.viewsummary_menu_viewtrends:
    	  Intent l = new Intent(ViewSummaryActivity.this, ViewTrendsActivity.class);
    	  ViewSummaryActivity.this.startActivity(l);
        break;
      }
      return super.onOptionsItemSelected(item);
    }
}
