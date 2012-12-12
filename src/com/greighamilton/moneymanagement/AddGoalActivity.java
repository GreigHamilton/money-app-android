package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class AddGoalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoal);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_addgoal, menu);
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
	
	public void clickTimescale(View v){
		Toast.makeText(this, "Click timescale", Toast.LENGTH_LONG).show();
	}
	
	public void clickImage(View v){
		Toast.makeText(this, "Click image", Toast.LENGTH_LONG).show();
	}
	
	public void clickSave(View v){
		Toast.makeText(this, "Click save", Toast.LENGTH_LONG).show();
	}
}
