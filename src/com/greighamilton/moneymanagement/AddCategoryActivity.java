package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
public class AddCategoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcategory);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_addcategory, menu);
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
	
	public void clickSave(View v){
		Toast.makeText(this, "Click save", Toast.LENGTH_LONG).show();
	}
	
	public void showColourPickerDialog(View v) {
	    DialogFragment newFragment = new ColourPickerFragment();
	    newFragment.show(getFragmentManager(), "colourPicker");
	}
}
