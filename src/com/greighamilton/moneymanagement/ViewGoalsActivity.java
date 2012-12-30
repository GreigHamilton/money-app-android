package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ViewGoalsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewgoals);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_viewgoals, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
	      case R.id.viewgoals_menu_addgoal:
	    	  Intent k = new Intent(ViewGoalsActivity.this, AddGoalActivity.class);
	    	  ViewGoalsActivity.this.startActivity(k);
	      break;
      }
      return super.onOptionsItemSelected(item);
    }
}
