package com.greighamilton.moneymanagement;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ViewCategoriesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewcategories);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_viewcategories, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      
	      case R.id.menu_addcategory:
	    	  Intent k = new Intent(ViewCategoriesActivity.this, AddCategoryActivity.class);
	    	  ViewCategoriesActivity.this.startActivity(k);
	      break;
      }
      return super.onOptionsItemSelected(item);
    }
}
