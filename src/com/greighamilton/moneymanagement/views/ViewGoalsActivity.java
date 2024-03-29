package com.greighamilton.moneymanagement.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddGoalActivity;
import com.greighamilton.moneymanagement.util.Util;

/**
 * Class for Goals activity.
 * 
 * @author Greig Hamilton
 *
 */
public class ViewGoalsActivity extends Activity {

	private static final boolean ADD = true;
	private static final boolean REMOVE = false;	
	
	private DatabaseHelper db;

	private List<String> listOfGoals;
	private List<Integer> listOfGoalIDs;

	private int selectedItem;
	private int selectedNeeded;
	private int selectedSaved;

	private Spinner goalSpinner;
	private TextView nameText;
	private TextView neededText;
	private TextView savedText;
	private TextView toSaveText;
	private ImageView goalImage;
	private ProgressBar progress;
	private TextView percentProgress;
	
	private String currencySymbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewgoals);

		db = DatabaseHelper.getInstance(this);

		goalSpinner = (Spinner) findViewById(R.id.goal_spinner);
		nameText = (TextView) findViewById(R.id.goal_name);
		neededText = (TextView) findViewById(R.id.goal_needed);
		savedText = (TextView) findViewById(R.id.goal_saved);
		toSaveText = (TextView) findViewById(R.id.goal_to_save);
		goalImage = (ImageView) findViewById(R.id.goal_image);
		progress = (ProgressBar) findViewById(R.id.progress);
		percentProgress = (TextView) findViewById(R.id.goal_progress_percent);

		setUpSpinner();
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		currencySymbol = sp.getString("CURRENCYSYMBOL", "");
	}

	/**
	 * Method to set up the goals spinner widget.
	 * 
	 */
	private void setUpSpinner() {

		listOfGoals = db.getListOfGoals();
		listOfGoalIDs = db.getListOfGoalIDs();
		
		selectedItem = 0;

		if (listOfGoals.isEmpty()) setContentView(R.layout.activity_viewgoals_welcome);
		else {

			ArrayAdapter<String> goalAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, listOfGoals);
			goalAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			goalSpinner.setAdapter(goalAdapter);
			goalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					selectedItem = position;
					refreshView();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});

		}
	}

	/**
	 * Method used to refresh the view of the goals interface on events.
	 * 
	 */
	private void refreshView() {

		if (listOfGoals.isEmpty())
			setContentView(R.layout.activity_viewgoals_welcome);
		else {
			Cursor c = db.getGoal(listOfGoalIDs.get(selectedItem));
			c.moveToFirst();
			if (!c.isAfterLast()) {
				
				// set up all the widgets from the database data
				selectedNeeded = c.getInt(DatabaseHelper.GOAL_NEEDED);
				selectedSaved = c.getInt(DatabaseHelper.GOAL_SAVED);
				String imagePath = c.getString(DatabaseHelper.GOAL_IMAGE);

				nameText.setText(c.getString(DatabaseHelper.GOAL_NAME));
				neededText.setText("" + currencySymbol + " "  + Util.floatFormat(selectedNeeded));
				savedText.setText("" + currencySymbol + " " + Util.floatFormat(selectedSaved));
				toSaveText.setText("" + currencySymbol + " " + Util.floatFormat((selectedNeeded - selectedSaved)));
				
				progress.setMax(selectedNeeded);
				progress.setProgress(selectedSaved);
				
				percentProgress.setText((int)(((float)selectedSaved/(float)selectedNeeded)*100)+"%");
				
				ImageView imageView = (ImageView)
				findViewById(R.id.goal_image);

				try {
					if (!imagePath.equals("")) {
			
						 String[] filePathColumn = { MediaStore.Images.Media.DATA
						 };
						 
						 Uri selectedImage = Uri.parse(imagePath);
						
						 Cursor cursor = getContentResolver().query(selectedImage,
						 filePathColumn, null, null, null);
						 cursor.moveToFirst();
						
						 int columnIndex =
						 cursor.getColumnIndex(filePathColumn[0]);
						 String picturePath = cursor.getString(columnIndex);
						 cursor.close();
						 
						 imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
					}
					else {
						
						imageView.setImageBitmap(BitmapFactory.decodeFile("@drawable/ic_add_picture.png"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					imageView.setActivated(false);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					imageView.setActivated(false);
				}
			}
			c.close();
		}
	}
	
	/**
	 * On click event method for goal.
	 * 
	 * @param v		the current view
	 */
	public void clickGoal(View v) {
		Intent i = new Intent(this, AddGoalActivity.class);
		startActivity(i);		
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

		case android.R.id.home:
        	
            finish();
            break;
            
		case R.id.viewgoals_menu_addgoal:
			Intent k = new Intent(ViewGoalsActivity.this, AddGoalActivity.class);
			ViewGoalsActivity.this.startActivity(k);
			setUpSpinner();
			refreshView();
			break;
		case R.id.viewgoals_menu_removegoal:
			if (!listOfGoals.isEmpty()) {
				showDeleteDialog();
			} else {
				Toast.makeText(this, "No Goal Selected", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Method for adding to goal amount.
	 * 
	 * @param v		the current view
	 */
	public void clickAddToSavings(View v) {
		showAddRemoveDialog(ADD);
	}
	
	/**
	 * Method for removing from goal amount.
	 * 
	 * @param v		the current view
	 */
	public void clickRemoveFromSavings(View v) {
		showAddRemoveDialog(REMOVE);
	}
	
	/**
	 * Method for showing the dialog box.
	 * 
	 * @param add		boolean value: either add or remove
	 */
	protected void showAddRemoveDialog(final boolean add) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle((add) ? "Add to Amount Saved" : "Remove from Amount Saved"); 
        alert.setMessage((add) ? "How much more have you saved?" : "How much should be removed?"); 

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton((add) ? "Add" : "Remove", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            try {
            	if (add) {
            		float amountSaved = db.getGoalSaved(listOfGoalIDs.get(selectedItem));
            		float amountNeeded = db.getGoalNeeded(listOfGoalIDs.get(selectedItem));            		
            		float amountToAdd = Integer.parseInt(input.getText().toString());
            		
            		amountToAdd = Math.min(amountToAdd, amountNeeded-amountSaved); // cap at maximum
            		amountSaved += amountToAdd;
            		
            		db.updateGoalSaved(listOfGoalIDs.get(selectedItem), amountSaved);
            		Toast.makeText(ViewGoalsActivity.this, "New amount saved : " + currencySymbol +Util.floatFormat(amountSaved), Toast.LENGTH_SHORT).show();
            		if (amountSaved == amountNeeded) {
            			Toast.makeText(ViewGoalsActivity.this, "Congratulations! You have reached your goal!", Toast.LENGTH_SHORT).show();        		
            		}
            	} else {
            		float amountSaved = db.getGoalSaved(listOfGoalIDs.get(selectedItem));
            		float amountToRemove = Integer.parseInt(input.getText().toString());
            		
            		amountToRemove = Math.min(amountToRemove, amountSaved); // cap at maximum
            		amountSaved -= amountToRemove;
            		
            		db.updateGoalSaved(listOfGoalIDs.get(selectedItem), amountSaved);
            		Toast.makeText(ViewGoalsActivity.this, "New amount saved : " + currencySymbol +Util.floatFormat(amountSaved), Toast.LENGTH_SHORT).show();
            	}
            } catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
            }
            refreshView();
          // Do something with value!
        }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Cancelled
          }
        });
        alert.show();
	}
	
	/**
	 * Method to show the delete dialog box.
	 * 
	 */
	protected void showDeleteDialog() {

    	new AlertDialog.Builder(ViewGoalsActivity.this)
        .setTitle("Delete")
        .setMessage("Are you sure you want to delete this?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {            	
            	db.deleteGoal(listOfGoalIDs.get(selectedItem));
	            setUpSpinner();
	            refreshView();
                Toast.makeText(ViewGoalsActivity.this, "Goal removed", Toast.LENGTH_SHORT).show();
            }
         })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
         .show();
	}
}
