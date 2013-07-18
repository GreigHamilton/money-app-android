package com.greighamilton.moneymanagement.entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;

/**
 * Class for add category activity.
 * 
 * @author Greig Hamilton
 *
 */
public class AddCategoryActivity extends Activity {
	
	String selectedColour;
	String colorCode;
	
	private DatabaseHelper db;
	private Cursor c;
	private Bundle extras;
	private String currentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcategory);

		db = DatabaseHelper.getInstance(this);
		extras = getIntent().getExtras();
		
		if (getIntent().getFlags() == 0) {
			RadioButton incomeCheck = (RadioButton) findViewById(R.id.category_type_income);
	    	incomeCheck.setChecked(true);
		}
		else {
			RadioButton expenseCheck = (RadioButton) findViewById(R.id.category_type_expense);
	    	expenseCheck.setChecked(true);
		}

		if (extras != null) {
			
		    currentId = extras.getString("CURRENT_ID");
		    c = db.getCategoryId(currentId);
		    c.moveToFirst();
		    TextView categoryName = (TextView) findViewById(R.id.category_name);
		    String name = c.getString(DatabaseHelper.CATEGORY_NAME);
		    categoryName.append(name);
		    this.setTitle("Edit Category: "+name);
		    
		    if(c.getString(DatabaseHelper.CATEGORY_TYPE).equals("0")) {
		    	RadioButton incomeCheck = (RadioButton) findViewById(R.id.category_type_income);
		    	incomeCheck.setChecked(true);
		    }
		    else if (c.getString(DatabaseHelper.CATEGORY_TYPE).equals("1")) {
		    	RadioButton expenseCheck = (RadioButton) findViewById(R.id.category_type_expense);
		    	expenseCheck.setChecked(true);
		    }
		    
		    colorCode =  c.getString(DatabaseHelper.CATEGORY_COLOUR);
		    Button colourButton = (Button) findViewById(R.id.category_colour);
		    colourButton.setTextColor(Color.parseColor(colorCode));
		    TextView colourBox = (TextView) findViewById(R.id.category_colour_box);
		    colourBox.setBackgroundColor(Color.parseColor(colorCode));
		    
		    TextView description = (TextView) findViewById(R.id.category_description);
		    description.append(c.getString(DatabaseHelper.CATEGORY_DESCRIPTION));
		    
		}
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

		case R.id.category_menu_cancel:
			finish();
			break;

		case R.id.category_menu_save:

			Button button = (Button) findViewById(R.id.category_colour);
			EditText nameBox = ((EditText) findViewById(R.id.category_name));

			if (button.getText().equals("Colour")
					|| nameBox.getText().toString().length() == 0) {
				// check status of color code
				if (button.getText().equals("Colour"))
					button.setError("A colour is required.");

				if (nameBox.getText().toString().length() == 0)
					nameBox.setError("A name is required.");
			}

			else {
				// Get name data
				String name = ((EditText) findViewById(R.id.category_name))
						.getText().toString();

				// Get type data
				int type;
				if (((RadioButton) findViewById(R.id.category_type_income))
						.isChecked())
					type = 0;
				else
					type = 1;

				// Get description data
				String description;
				if (((EditText) findViewById(R.id.category_description))
						.getText().length() == 0)
					description = "";
				else
					description = ((EditText) findViewById(R.id.category_description))
							.getText().toString();

				if (extras != null) {
					db.updateCategory(currentId, name, type, colorCode,
							description);
				} else {
					db.addCategory(name, type, colorCode, description);
				}
				
				finish();
			}
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showColourPickerDialog(View v) {
		DialogFragment newFragment = new ColourPickerFragment();
		newFragment.show(getFragmentManager(), "colourPicker");
	}

	/**
	 * Classfor colour picker fragment.
	 * 
	 * @author Greig Hamilton
	 *
	 */
	@SuppressLint("ValidFragment")
	public class ColourPickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.pick_color);
			final String[] colours;
			
			if (((RadioButton) findViewById(R.id.category_type_income)).isChecked()) {
				colours = getResources().getStringArray(R.array.colors_array_income);
			}
			else {
				colours = getResources().getStringArray(R.array.colors_array_expenses);
			}
			builder.setItems(colours,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// The 'which' argument contains the index position
							// of the selected item
							selectedColour = colours[item];
							
							Button button = (Button) findViewById(R.id.category_colour);
							String boxColor;
							
					        if (selectedColour.equalsIgnoreCase("Red")) {
					        	button.setTextColor(getResources().getColor(R.color.Red));
					        	colorCode = getResources().getString(R.color.Red);
					        }
					        else if (selectedColour.equalsIgnoreCase("Green")) {
					        	button.setTextColor(getResources().getColor(R.color.Green));
					        	colorCode = getResources().getString(R.color.Green);
					        }
					        else if (selectedColour.equalsIgnoreCase("Blue")) {
					        	button.setTextColor(getResources().getColor(R.color.Blue));
					        	colorCode = getResources().getString(R.color.Blue);
					        }
					        else if (selectedColour.equalsIgnoreCase("Pink")) {
					        	button.setTextColor(getResources().getColor(R.color.Pink));
					        	colorCode = getResources().getString(R.color.Pink);
					        }
					        else if (selectedColour.equalsIgnoreCase("Orange")) {
					        	button.setTextColor(getResources().getColor(R.color.Orange));
					        	colorCode = getResources().getString(R.color.Orange);
					        }
					        else if (selectedColour.equalsIgnoreCase("Purple")) {
					        	button.setTextColor(getResources().getColor(R.color.Purple));
					        	colorCode = getResources().getString(R.color.Purple);
					        }
					        else
					        	colorCode = getResources().getString(R.color.grey1);
					        
					        boxColor = colorCode;
					        TextView box = (TextView) findViewById(R.id.category_colour_box);
							box.setBackgroundColor(Color.parseColor(colorCode));
					        
					        button.setText(selectedColour);
						}
					});
			return builder.create();
		}
	}
}
