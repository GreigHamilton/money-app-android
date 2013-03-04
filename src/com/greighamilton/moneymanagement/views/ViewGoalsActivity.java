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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

import com.greighamilton.moneymanagement.DashboardActivity;
import com.greighamilton.moneymanagement.R;
import com.greighamilton.moneymanagement.data.DatabaseHelper;
import com.greighamilton.moneymanagement.entry.AddGoalActivity;
import com.greighamilton.moneymanagement.util.Util;

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

		setUpSpinner();
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}

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

	private void refreshView() {

		if (listOfGoals.isEmpty())
			setContentView(R.layout.activity_viewgoals_welcome);
		else {
			Cursor c = db.getGoal(listOfGoalIDs.get(selectedItem));
			c.moveToFirst();
			if (!c.isAfterLast()) {
				selectedNeeded = c.getInt(DatabaseHelper.GOAL_NEEDED);
				selectedSaved = c.getInt(DatabaseHelper.GOAL_SAVED);
				String imagePath = c.getString(DatabaseHelper.GOAL_IMAGE);

				nameText.setText(c.getString(DatabaseHelper.GOAL_NAME));
				neededText.setText("£ " + Util.floatFormat(selectedNeeded));
				savedText.setText("£ " + Util.floatFormat(selectedSaved));
				toSaveText.setText("£ " + Util.floatFormat((selectedNeeded - selectedSaved)));
				
				progress.setMax(selectedNeeded);
				progress.setProgress(selectedSaved);

				Log.i("", "Path: " + imagePath);

				if (imagePath != null) {

					try {
						Uri uri = Uri.parse(imagePath);
						// Bitmap image =
						// MediaStore.Images.Media.getBitmap(this.getContentResolver(),
						// uri);
						// ByteArrayOutputStream out = new
						// ByteArrayOutputStream();
						// image.compress(Bitmap.CompressFormat.JPEG, 50, out);
						// Bitmap miniImage = BitmapFactory.decodeStream(new
						// ByteArrayInputStream(out.toByteArray()));
						// //Bitmap image = BitmapFactory.decodeFile(imagePath);
						Bitmap miniImage = getThumbnail(uri);
						goalImage.setImageBitmap(miniImage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			c.close();
		}
	}

	public Bitmap getThumbnail(Uri uri) throws FileNotFoundException,
			IOException {
		InputStream input = this.getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double THUMBNAIL_SIZE = 2;
		double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE)
				: 1.0;
		// double ratio = 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}
	
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
        	
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
            
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
	
	public void clickAddToSavings(View v) {
		showAddRemoveDialog(ADD);
	}
	
	public void clickRemoveFromSavings(View v) {
		showAddRemoveDialog(REMOVE);
	}
	
	public void clickImage(View v) {
		Toast.makeText(this, "This feature is not supported yet.", Toast.LENGTH_SHORT).show();
//		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//		photoPickerIntent.setType("image/*");
//		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}
	
	protected void showAddRemoveDialog(final boolean add) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle((add) ? "Add to Savings" : "Remove from Savings"); 
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
            		Toast.makeText(ViewGoalsActivity.this, "New amount saved : £"+Util.floatFormat(amountSaved), Toast.LENGTH_SHORT).show();
            		if (amountSaved == amountNeeded) {
            			Toast.makeText(ViewGoalsActivity.this, "Congratulations! You have reached your goal!", Toast.LENGTH_SHORT).show();        		
            		}
            	} else {
            		float amountSaved = db.getGoalSaved(listOfGoalIDs.get(selectedItem));
            		float amountToRemove = Integer.parseInt(input.getText().toString());
            		
            		amountToRemove = Math.min(amountToRemove, amountSaved); // cap at maximum
            		amountSaved -= amountToRemove;
            		
            		db.updateGoalSaved(listOfGoalIDs.get(selectedItem), amountSaved);
            		Toast.makeText(ViewGoalsActivity.this, "New amount saved : £"+Util.floatFormat(amountSaved), Toast.LENGTH_SHORT).show();
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
