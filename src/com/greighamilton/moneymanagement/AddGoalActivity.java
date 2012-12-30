package com.greighamilton.moneymanagement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.greighamilton.moneymanagement.data.DatabaseHelper;

public class AddGoalActivity extends Activity {

	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoal);

		db = DatabaseHelper.getInstance(this);
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

		case R.id.goal_menu_cancel:
			finish();
			break;

		case R.id.goal_menu_save:

			// Get name data
			String name = ((EditText) findViewById(R.id.add_goal_name))
					.getText().toString();

			// Get amount to save data
			int amountToSave = (Integer
					.parseInt(((EditText) findViewById(R.id.add_goal_amount_to_save))
							.getText().toString()));

			// Get amount saved data
			int amountSaved = (Integer
					.parseInt(((EditText) findViewById(R.id.add_goal_amount_saved))
							.getText().toString()));

			String photoAddress;

			//db.addGoal(name, amountToSave, amountSaved, photoAddress);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clickImage(View v) {
		Toast.makeText(this, "Click image", Toast.LENGTH_LONG).show();
	}

	public void clickSave(View v) {
		Toast.makeText(this, "Click save", Toast.LENGTH_LONG).show();
	}
}
